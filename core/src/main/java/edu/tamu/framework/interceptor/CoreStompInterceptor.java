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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import edu.tamu.framework.mapping.WebSocketRequestMappingHandler;
import edu.tamu.framework.mapping.condition.WebSocketRequestCondition;
import edu.tamu.framework.model.AbstractCoreUser;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.model.WebSocketRequest;
import edu.tamu.framework.service.WebSocketRequestService;
import edu.tamu.framework.util.JwtUtility;

/**
 * Stomp interceptor. Checks command, decodes and verifies token, either returns error message to
 * frontend or continues to controller.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public abstract class CoreStompInterceptor<U extends AbstractCoreUser> extends ChannelInterceptorAdapter {

    @Autowired
    private JwtUtility jwtService;

    @Autowired
    private WebSocketRequestService<U> webSocketRequestService;

    @Autowired
    private SecurityContext securityContext;

    @Autowired
    @Lazy
    private SimpMessagingTemplate simpMessagingTemplate;

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
                }

            } else {
                credentials = getAnonymousCredentials();
            }

            List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

            grantedAuthorities.add(new SimpleGrantedAuthority(credentials.getRole()));

            if (credentials.getNetid() == null) {
                credentials.setNetid(credentials.getEmail());
            }

            if (credentials.getUin() == null) {
                credentials.setUin(credentials.getEmail());
            }

            // TODO: extend CoreUser with UserDetails and implement required methods
            // pass <U extends CoreUser> in as Object principal, second argument
            Authentication auth = new AnonymousAuthenticationToken(credentials.getNetid(), credentials.getUin(), grantedAuthorities);

            auth.setAuthenticated(true);

            securityContext.setAuthentication(auth);

            break;
        case CONNECTED:
            break;
        case DISCONNECT:
            logger.debug("Disconnecting websocket connection for " + securityContext.getAuthentication().getName() + ".");
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
                    logger.error("Security Context Name: " + securityContext.getAuthentication().getName());
                    logger.error("JWT error: " + errorMessage);
                    simpMessagingTemplate.convertAndSend(accessorDestination.replace("ws", "queue") + "-user" + accessor.getSessionId(), new ApiResponse(requestId, ERROR, errorMessage));
                    return null;
                }

                if (jwtService.isExpired(credentialMap)) {
                    logger.info("The token for " + credentialMap.get("firstName") + " " + credentialMap.get("lastName") + " (" + credentialMap.get("uin") + ") has expired. Attempting to get new token.");
                    simpMessagingTemplate.convertAndSend(accessorDestination.replace("ws", "queue") + "-user" + accessor.getSessionId(), new ApiResponse(requestId, REFRESH));
                    return null;
                }

                credentials = new Credentials(credentialMap);

                user = confirmCreateUser(credentials);

            } else {
                credentials = getAnonymousCredentials();
            }

            webSocketRequestService.addRequest(new WebSocketRequest<U>(message, user, destination, credentials));

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
