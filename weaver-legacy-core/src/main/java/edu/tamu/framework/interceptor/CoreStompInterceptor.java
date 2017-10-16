/* 
 * CoreStompInterceptor.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.interceptor;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.REFRESH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import edu.tamu.framework.mapping.WebSocketRequestMappingHandler;
import edu.tamu.framework.mapping.condition.WebSocketRequestCondition;
import edu.tamu.framework.model.AbstractCoreUser;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.model.WebSocketRequest;
import edu.tamu.framework.service.SecurityContextService;
import edu.tamu.framework.service.StompService;
import edu.tamu.framework.service.WebSocketRequestService;
import edu.tamu.framework.util.JwtUtility;

/**
 * Stomp interceptor. Checks command, decodes and verifies token, either returns error message to frontend or continues to controller.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Deprecated
@Component
public abstract class CoreStompInterceptor<U extends AbstractCoreUser> extends ChannelInterceptorAdapter {

    @Autowired
    private JwtUtility jwtService;

    @Autowired
    private WebSocketRequestService<U> webSocketRequestService;

    @Autowired
    private SecurityContextService<U> securityContextService;

    @Autowired
    @Lazy
    private StompService stompService;

    @Autowired
    @Lazy
    private WebSocketRequestMappingHandler webSocketRequestMappingHandler;

    @Autowired
    @Lazy
    private SimpAnnotationMethodMessageHandler simpAnnotationMethodMessageHandler;

    private final PathMatcher pathMatcher;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CoreStompInterceptor() {
        pathMatcher = (PathMatcher) new AntPathMatcher();
    }

    public abstract Credentials getAnonymousCredentials();

    /**
     * Override method to perform preprocessing before sending message.
     * 
     * @param message
     *            Message<?>
     * @param channel
     *            MessageChannel
     * 
     * @return Message<?>
     * 
     */
    @Override
    @SuppressWarnings("unchecked")
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        String accessorDestination = accessor.getDestination();

        if (accessorDestination != null) {
            logger.debug("Accessor destination: " + accessorDestination);
        }

        logger.debug(command.name());

        String jwt = null;

        if (accessor.getNativeHeader("jwt") != null) {
            jwt = accessor.getNativeHeader("jwt").get(0);
        }

        Credentials credentials = null;

        U user = null;

        switch (command) {
        case ABORT:
            break;
        case ACK:

            List<String> channelHeader = ((Map<String, List<String>>) message.getHeaders().get("nativeHeaders")).get("channel");

            if (channelHeader != null) {

                String refreshChannel = channelHeader.get(0);

                String refreshSessionId = (String) message.getHeaders().get("simpSessionId");

                String requestId = accessor.getNativeHeader("id").get(0);

                stompService.ackReliableMessage(refreshChannel.substring("/private".length()) + "-user" + refreshSessionId, requestId);
            }

            break;
        case BEGIN:
            break;
        case COMMIT:
            break;
        case CONNECT:

            if (jwt != null && !"undefined".equals(jwt)) {

                Map<String, String> credentialMap = jwtService.validateJWT(jwt);

                if (logger.isDebugEnabled()) {
                    logger.debug("Credential Map");
                    for (String key : credentialMap.keySet()) {
                        logger.debug(key + " - " + credentialMap.get(key));
                    }
                }

                String errorMessage = credentialMap.get("ERROR");
                if (errorMessage != null) {
                    logger.error("JWT error: " + errorMessage);
                    return MessageBuilder.withPayload(errorMessage).setHeaders(accessor).build();
                }

                credentials = new Credentials(credentialMap);

                user = confirmCreateUser(credentials);

                if (user == null) {
                    errorMessage = "Could not confirm user!";
                    logger.error(errorMessage);
                    return MessageBuilder.withPayload(errorMessage).setHeaders(accessor).build();
                } else {

                }

            } else {
                credentials = getAnonymousCredentials();
            }

            if (user != null) {
                securityContextService.setAuthentication(user.getUin(), user, user.getAuthorities());
            } else {
                List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
                grantedAuthorities.add(new SimpleGrantedAuthority(credentials.getRole()));
                securityContextService.setAuthentication(credentials.getUin(), credentials, grantedAuthorities);
            }

            break;
        case CONNECTED:
            logger.debug("Connected websocket connection for " + securityContextService.getAuthenticatedName() + ".");
            break;
        case DISCONNECT:
            logger.debug("Disconnected websocket connection for " + securityContextService.getAuthenticatedName() + ".");
            break;
        case ERROR:
            break;
        case MESSAGE:
            break;
        case NACK:
            break;
        case RECEIPT:
            break;
        case SEND:

            String requestId = accessor.getNativeHeader("id").get(0);

            if (jwt != null && !"undefined".equals(jwt)) {

                Map<String, String> credentialMap = new HashMap<String, String>();

                credentialMap = jwtService.validateJWT(jwt);

                logger.info(credentialMap.get("firstName") + " " + credentialMap.get("lastName") + " (" + credentialMap.get("uin") + ") requesting " + accessorDestination);

                if (logger.isDebugEnabled()) {
                    logger.debug("Credential Map");
                    for (String key : credentialMap.keySet()) {
                        logger.debug(key + " - " + credentialMap.get(key));
                    }
                }

                String errorMessage = credentialMap.get("ERROR");

                if (errorMessage != null) {
                    logger.error("Security Context Name: " + securityContextService.getAuthenticatedName());
                    logger.error("JWT error: " + errorMessage);
                    stompService.sendReliableMessage(accessorDestination.replace("ws", "queue") + "-user" + accessor.getSessionId(), requestId, new ApiResponse(requestId, ERROR, errorMessage));
                    return null;
                }

                if (jwtService.isExpired(credentialMap)) {
                    logger.info("The token for " + credentialMap.get("firstName") + " " + credentialMap.get("lastName") + " (" + credentialMap.get("uin") + ") has expired. Attempting to get new token.");
                    // send refresh message reliably
                    stompService.sendReliableMessage(accessorDestination.replace("ws", "queue") + "-user" + accessor.getSessionId(), requestId, new ApiResponse(requestId, REFRESH));
                    return null;
                }

                credentials = new Credentials(credentialMap);

                try {
                    user = (U) securityContextService.getAuthenticatedPrincipal();
                } catch (Exception e) {
                    logger.info("Authenticated principal is Credentials not a User");
                }

                if (user != null && credentials.getUin().equals(user.getUin())) {
                    credentials.setRole(user.getRole().toString());
                } else {

                    user = confirmCreateUser(credentials);

                    if (user == null) {
                        errorMessage = "Could not confirm user!";
                        logger.error(errorMessage);
                        stompService.sendReliableMessage(accessorDestination.replace("ws", "queue") + "-user" + accessor.getSessionId(), requestId, new ApiResponse(requestId, ERROR, errorMessage));
                        return null;
                    } else {
                        securityContextService.setAuthentication(user.getUin(), user);
                    }

                }

            } else {
                credentials = getAnonymousCredentials();
            }

            String destination = null;

            List<String> matches = new ArrayList<String>();

            // get pattern matches from ApiMapping annotation
            webSocketRequestMappingHandler.getHandlerMethods().entrySet().stream().forEach(info -> {
                WebSocketRequestCondition mappingCondition = info.getKey().getDestinationConditions();
                mappingCondition.getPatterns().stream().forEach(pattern -> {

                    if (pattern.contains("{")) {
                        if (((AntPathMatcher) this.pathMatcher).match(("/ws" + pattern), accessorDestination)) {
                            matches.add(pattern);
                        } else if (((AntPathMatcher) this.pathMatcher).match(("/private/queue" + pattern), accessorDestination)) {
                            matches.add(pattern);
                        }
                    } else {
                        if (("/ws" + pattern).equals(accessorDestination)) {
                            matches.add(pattern);
                        } else if (("/private/queue" + pattern).equals(accessorDestination)) {
                            matches.add(pattern);
                        }
                    }

                });
            });

            // get pattern matches from MessageMapping annotation
            simpAnnotationMethodMessageHandler.getHandlerMethods().entrySet().stream().forEach(info -> {
                DestinationPatternsMessageCondition mappingCondition = info.getKey().getDestinationConditions();
                mappingCondition.getPatterns().stream().forEach(pattern -> {

                    if (pattern.contains("{")) {
                        if (((AntPathMatcher) this.pathMatcher).match(("/ws" + pattern), accessorDestination)) {
                            matches.add(pattern);
                        } else if (((AntPathMatcher) this.pathMatcher).match(("/private/queue" + pattern), accessorDestination)) {
                            matches.add(pattern);
                        }
                    } else {
                        if (("/ws" + pattern).equals(accessorDestination)) {
                            matches.add(pattern);
                        } else if (("/private/queue" + pattern).equals(accessorDestination)) {
                            matches.add(pattern);
                        }
                    }

                });
            });

            String enhancedAccessorDestination = accessorDestination;

            if (accessorDestination.startsWith("/ws")) {
                enhancedAccessorDestination = accessorDestination.substring("/ws".length());
            } else if (accessorDestination.startsWith("/private/queue")) {
                enhancedAccessorDestination = accessorDestination.substring("/private/queue".length());
            }

            String[] destinationPaths = enhancedAccessorDestination.split("/");
            int m = 0;
            for (String pattern : matches) {
                String[] patternPaths = pattern.split("/");
                if (patternPaths.length == destinationPaths.length) {
                    int n = 0;
                    for (int i = 0; i < patternPaths.length; i++) {
                        if (patternPaths[i].equals(destinationPaths[i])) {
                            n++;
                        }
                    }
                    if (n > m) {
                        m = n;
                        destination = pattern;
                    }
                }
            }

            if (user != null) {
                webSocketRequestService.addRequest(new WebSocketRequest<U>(message, securityContextService.getAuthenticatedName(), destination, credentials, user));
            } else {
                webSocketRequestService.addRequest(new WebSocketRequest<U>(message, securityContextService.getAuthenticatedName(), destination, credentials));
            }

            break;
        case STOMP:
            break;
        case SUBSCRIBE:
            break;
        case UNSUBSCRIBE:
            break;
        default:
            break;
        }

        return message;
    }

    public abstract U confirmCreateUser(Credentials credentials);

}
