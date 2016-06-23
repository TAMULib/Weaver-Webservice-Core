/* 
 * CoreControllerAspect.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.aspect;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.WARNING;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.enums.ApiResponseType;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.model.HttpRequest;
import edu.tamu.framework.model.WebSocketRequest;
import edu.tamu.framework.service.HttpRequestService;
import edu.tamu.framework.service.RoleService;
import edu.tamu.framework.service.WebSocketRequestService;

/**
 * Core Controller Aspect
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Component
@Aspect
public abstract class CoreControllerAspect {

    // TODO: put in application.properties of each app
    private final static int NUMBER_OF_RETRY_ATTEMPTS = 3;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    private WebSocketRequestService webSocketRequestService;

    @Autowired
    private HttpRequestService httpRequestService;

    @Autowired
    private SecurityContext securityContext;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public abstract Object validate(Object object, Annotation annotation, String className);

    /**
     * JoinPoint in which populates credentials and authorizes request.
     * 
     * @param joinPoint
     *            ProceedingJoinPoint
     * @param auth
     *            Auth
     * @return ApiResponse
     * @throws Throwable
     */
    @Around("execution(* *.*.*.controller.*.*(..)) && !@annotation(edu.tamu.framework.aspect.annotation.SkipAop) && @annotation(auth)")
    public ApiResponse polpulateCredentialsAndAuthorize(ProceedingJoinPoint joinPoint, Auth auth) throws Throwable {

        PreProcessObject preProcessObject = preProcess(joinPoint);

        ApiResponse apiresponse = null;

        if (roleService.valueOf(preProcessObject.shib.getRole()).ordinal() < roleService.valueOf(auth.role()).ordinal()) {
            logger.info(preProcessObject.shib.getFirstName() + " " + preProcessObject.shib.getLastName() + "(" + preProcessObject.shib.getUin() + ") attempted restricted access.");
            apiresponse = new ApiResponse(preProcessObject.requestId, ERROR, "You are not authorized for this request.");
        } else {

            apiresponse = (ApiResponse) joinPoint.proceed(preProcessObject.arguments);

            if (apiresponse != null) {

                // retry endpoint if error response type
                int attempt = 0;
                while (attempt <= NUMBER_OF_RETRY_ATTEMPTS && apiresponse.getMeta().getType() == ApiResponseType.ERROR) {
                    attempt++;
                    logger.debug("Retry attempt " + attempt);
                    apiresponse = (ApiResponse) joinPoint.proceed(preProcessObject.arguments);
                }

                apiresponse.getMeta().setId(preProcessObject.requestId);
            } else {
                apiresponse = new ApiResponse(WARNING, "Endpoint returns void!");
            }
        }

        // if using combined ApiMapping annotation send message as similar to
        // SendToUser annotation
        if (preProcessObject.protocol == Protocol.WEBSOCKET) {
            simpMessagingTemplate.convertAndSend(preProcessObject.destination, apiresponse);
        }

        return apiresponse;
    }

    /**
     * JoinPoint in which populates credentials.
     * 
     * @param joinPoint
     *            ProceedingJoinPoint
     * @return ApiResponse
     * @throws Throwable
     */
    @Around("execution(* *.*.*.controller.*.*(..)) && !@annotation(edu.tamu.framework.aspect.annotation.SkipAop) && !@annotation(edu.tamu.framework.aspect.annotation.Auth)")
    public ApiResponse populateCredentials(ProceedingJoinPoint joinPoint) throws Throwable {

        PreProcessObject preProcessObject = preProcess(joinPoint);

        ApiResponse apiresponse = (ApiResponse) joinPoint.proceed(preProcessObject.arguments);

        if (apiresponse != null) {

            // retry endpoint if error response type
            int attempt = 0;
            while (attempt <= NUMBER_OF_RETRY_ATTEMPTS && apiresponse.getMeta().getType() == ApiResponseType.ERROR) {
                attempt++;
                logger.debug("Retry attempt " + attempt);
                apiresponse = (ApiResponse) joinPoint.proceed(preProcessObject.arguments);
            }

            apiresponse.getMeta().setId(preProcessObject.requestId);
        } else {
            apiresponse = new ApiResponse(WARNING, "Endpoint returns void!");
        }

        // if using combined ApiMapping annotation send message as similar to
        // SendToUser annotation
        if (preProcessObject.protocol == Protocol.WEBSOCKET) {
            simpMessagingTemplate.convertAndSend(preProcessObject.destination, apiresponse);
        }

        return apiresponse;
    }

    /**
     * Pre process method.
     * 
     * @param joinPoint
     *            ProceedingJoinPoint
     * @return PreProcessObject
     * @throws Throwable
     */
    private PreProcessObject preProcess(ProceedingJoinPoint joinPoint) throws Throwable {

        Credentials shib = null;

        Map<String, String> apiVariables = null;

        String requestId = null;

        String data = null;

        Map<String, String[]> parameters = new HashMap<String, String[]>();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        Object[] arguments = joinPoint.getArgs();

        String[] argNames = methodSignature.getParameterNames();

        Class<?>[] argTypes = methodSignature.getParameterTypes();

        Class<?> clazz = methodSignature.getDeclaringType();

        Method method = clazz.getDeclaredMethod(methodSignature.getName(), methodSignature.getParameterTypes());

        Protocol protocol;

        String destination = "";

        Message<?> message = null;

        HttpServletRequest servletRequest = null;

        if (RequestContextHolder.getRequestAttributes() != null) {

            protocol = Protocol.HTTP;

            // determine endpoint path either from ApiMapping or RequestMapping annotation
            String path = servletContext.getContextPath();

            if (clazz.getAnnotationsByType(RequestMapping.class).length > 0) {
                path += clazz.getAnnotationsByType(RequestMapping.class)[0].value()[0];
            } else {
                path += clazz.getAnnotationsByType(ApiMapping.class)[0].value()[0];
            }

            if (method.getAnnotation(RequestMapping.class) != null) {
                path += method.getAnnotation(RequestMapping.class).value()[0];
            } else {
                path += method.getAnnotation(ApiMapping.class).value()[0];
            }

            HttpRequest request = httpRequestService.getAndRemoveRequestByDestinationAndUser(path, securityContext.getAuthentication().getName());

            servletRequest = request.getRequest();

            parameters = servletRequest.getParameterMap();

            logger.debug("The request: " + servletRequest);

            if (path.contains("{")) {
                apiVariables = getApiVariable(path, servletContext.getContextPath() + servletRequest.getServletPath());
            }

            shib = request.getCredentials();

            if (servletRequest.getAttribute("data") != null) {
                data = (String) servletRequest.getAttribute("data");
            }

        } else {

            // determine endpoint path either from ApiMapping or MessageMapping annotation
            String path = "";

            if (clazz.getAnnotationsByType(MessageMapping.class).length > 0) {
                path += clazz.getAnnotationsByType(MessageMapping.class)[0].value()[0];
            } else {
                path += clazz.getAnnotationsByType(ApiMapping.class)[0].value()[0];
            }

            if (method.getAnnotation(MessageMapping.class) != null) {
                path += method.getAnnotation(MessageMapping.class).value()[0];
                protocol = Protocol.DEFAULT;
            } else {
                path += method.getAnnotation(ApiMapping.class).value()[0];
                protocol = Protocol.WEBSOCKET;
            }

            WebSocketRequest request = webSocketRequestService.getAndRemoveMessageByDestinationAndUser(path, securityContext.getAuthentication().getName());

            message = request.getMessage();

            logger.debug("The message: " + message);

            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

            destination = accessor.getDestination().replace("ws", "queue") + "-user" + accessor.getSessionId();

            requestId = accessor.getNativeHeader("id").get(0);

            shib = request.getCredentials();

            if (path.contains("{")) {
                apiVariables = getApiVariable(path, accessor.getDestination());
            }

            if (accessor.getNativeHeader("data") != null) {
                data = accessor.getNativeHeader("data").get(0).toString();
            }
        }

        int index = 0;
        for (Annotation[] annotations : method.getParameterAnnotations()) {

            Annotation ann = null;
            String annotationString = null;

            for (Annotation annotation : annotations) {
                ann = annotation;
                annotationString = annotation.toString();
                annotationString = annotationString.substring(annotationString.lastIndexOf('.') + 1, annotationString.indexOf("("));
            }

            if (annotationString != null) {
                switch (annotationString) {
                case "ApiVariable":
                    arguments[index] = apiVariables.get(argNames[index]) != null ? objectMapper.convertValue(apiVariables.get(argNames[index]), objectMapper.constructType(argTypes[index])) : null;
                    break;
                case "Shib":
                    arguments[index] = shib;
                    break;
                case "ApiData":
                    arguments[index] = data != null ? objectMapper.convertValue(objectMapper.readTree(data), objectMapper.constructType(argTypes[index])) : null;
                    break;
                case "ApiModel":
                    arguments[index] = data != null ? objectMapper.convertValue(objectMapper.readTree(data), objectMapper.constructType(argTypes[index])) : null;
                    break;
                case "ApiValidatedModel":
                    arguments[index] = data != null ? validate(objectMapper.convertValue(objectMapper.readTree(data), objectMapper.constructType(argTypes[index])), ann, argTypes[index].getCanonicalName()) : null;
                    break;
                case "Parameters":
                    arguments[index] = parameters;
                    break;
                case "InputStream":
                    arguments[index] = servletRequest.getInputStream();
                    break;
                }
            }
            index++;
        }

        return new PreProcessObject(shib, requestId, arguments, protocol, destination);
    }

    /**
     * Get API variable
     * 
     * @param mapping
     *            String
     * @param path
     *            String
     * @return Map<String, String>
     */
    protected Map<String, String> getApiVariable(String mapping, String path) {
        if (path.contains("/ws"))
            mapping = "/ws" + mapping;
        if (path.contains("/private/queue"))
            mapping = "/private/queue" + mapping;
        Map<String, String> valuesMap = new HashMap<String, String>();
        String[] keys = mapping.split("/");
        String[] values = path.split("/");
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].contains("{") && keys[i].contains("}")) {
                valuesMap.put(keys[i].substring(1, keys[i].length() - 1), values[i]);
            }
        }
        return valuesMap;
    }

    /**
     * Inner pre process object class.
     */
    protected class PreProcessObject {

        Credentials shib;
        String requestId;
        Object[] arguments;
        Protocol protocol;
        String destination;

        public PreProcessObject(Credentials shib, Object[] arguments) {
            this.shib = shib;
            this.arguments = arguments;
        }

        public PreProcessObject(Credentials shib, String requestId, Object[] arguments) {
            this.shib = shib;
            this.requestId = requestId;
            this.arguments = arguments;
        }

        public PreProcessObject(Credentials shib, String requestId, Object[] arguments, Protocol protocol) {
            this.shib = shib;
            this.requestId = requestId;
            this.arguments = arguments;
            this.protocol = protocol;
        }

        public PreProcessObject(Credentials shib, String requestId, Object[] arguments, Protocol protocol, String destination) {
            this.shib = shib;
            this.requestId = requestId;
            this.arguments = arguments;
            this.protocol = protocol;
            this.destination = destination;
        }

    }

    private enum Protocol {
        WEBSOCKET, HTTP, DEFAULT
    }

}
