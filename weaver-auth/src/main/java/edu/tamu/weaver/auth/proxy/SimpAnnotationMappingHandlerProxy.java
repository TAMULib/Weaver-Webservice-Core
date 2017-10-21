package edu.tamu.weaver.auth.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.messaging.simp.SimpMessageMappingInfo;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.util.PathMatcher;

@Service
public class SimpAnnotationMappingHandlerProxy implements MappingHandlerProxy<SimpMessageMappingInfo, HandlerMethod, Message<?>> {

    private final static String GET_LOOKUP_DESTINATION_METHOD_NAME = "getLookupDestination";

    @Autowired
    private SimpAnnotationMethodMessageHandler simpAnnotationMethodMessageHandler;

    private Method getLookupDestination;

    public SimpAnnotationMappingHandlerProxy() throws NoSuchMethodException, SecurityException {
        getLookupDestination = SimpAnnotationMethodMessageHandler.class.getDeclaredMethod(GET_LOOKUP_DESTINATION_METHOD_NAME, String.class);
        getLookupDestination.setAccessible(true);
    }

    @Override
    public Optional<HandlerMethod> getHandler(Message<?> object) {
        Optional<HandlerMethod> handler = Optional.empty();
        String path = null;
        try {
            path = getPath(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (path != null) {
            handler = findHandler(path);
        }
        return handler;
    }

    @Override
    public Map<SimpMessageMappingInfo, HandlerMethod> getAllHandlers() {
        return simpAnnotationMethodMessageHandler.getHandlerMethods();
    }

    public Optional<HandlerMethod> findHandler(String path) {
        Optional<HandlerMethod> handler = Optional.empty();
        PathMatcher pathMatcher = simpAnnotationMethodMessageHandler.getPathMatcher();
        for (Entry<SimpMessageMappingInfo, HandlerMethod> entry : simpAnnotationMethodMessageHandler.getHandlerMethods().entrySet()) {
            SimpMessageMappingInfo info = entry.getKey();
            boolean matched = false;
            for (String pattern : info.getDestinationConditions().getPatterns()) {
                matched = pathMatcher.match(pattern, path);
                if (matched) {
                    handler = Optional.of(entry.getValue());
                    break;
                }
            }
            if (matched) {
                break;
            }
        }
        return handler;
    }

    private String getPath(Message<?> message) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        StompHeaderAccessor accessor = getAccessor(message);
        return (String) getLookupDestination.invoke(simpAnnotationMethodMessageHandler, accessor.getDestination());
    }

    public String getPath(StompHeaderAccessor accessor) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return (String) getLookupDestination.invoke(simpAnnotationMethodMessageHandler, accessor.getDestination());
    }

    private StompHeaderAccessor getAccessor(Message<?> message) {
        return MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    }

}
