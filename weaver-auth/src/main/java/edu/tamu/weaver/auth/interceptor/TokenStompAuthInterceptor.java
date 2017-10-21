package edu.tamu.weaver.auth.interceptor;

import static edu.tamu.weaver.auth.AuthConstants.ANONYMOUS_AUTHORITIES;
import static edu.tamu.weaver.auth.AuthConstants.AUTHORIZATION_HEADER;
import static edu.tamu.weaver.auth.AuthConstants.DEFAULT_CHARSET;
import static edu.tamu.weaver.auth.AuthConstants.ERROR_RESPONSE;
import static edu.tamu.weaver.auth.AuthConstants.EXPIRED_RESPONSE;
import static edu.tamu.weaver.auth.AuthConstants.UNAUTHORIZED_RESPONSE;
import static edu.tamu.weaver.auth.model.AccessDecision.ALLOW_ANONYMOUS;
import static java.util.UUID.randomUUID;
import static org.springframework.messaging.simp.stomp.StompCommand.CONNECT;
import static org.springframework.messaging.simp.stomp.StompCommand.MESSAGE;
import static org.springframework.messaging.simp.stomp.StompCommand.SEND;
import static org.springframework.messaging.simp.stomp.StompCommand.SUBSCRIBE;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;
import edu.tamu.weaver.auth.service.AbstractWeaverUserDetailsService;
import edu.tamu.weaver.auth.service.StompAccessManagerService;
import edu.tamu.weaver.auth.service.TokenAuthenticationService;
import edu.tamu.weaver.token.exception.ExpiredTokenException;
import edu.tamu.weaver.user.model.AbstractWeaverUser;

@Component
public class TokenStompAuthInterceptor<U extends AbstractWeaverUser, R extends AbstractWeaverUserRepo<U>, S extends AbstractWeaverUserDetailsService<U, R>> extends ChannelInterceptorAdapter {

    private final static List<StompCommand> AUTHENTICATED_COMMANDS = Arrays.asList(new StompCommand[] { CONNECT, SEND, SUBSCRIBE });

    private final static MimeType DEFAULT_MIME_TYPE = new MimeType(MimeTypeUtils.APPLICATION_JSON, Charset.forName(DEFAULT_CHARSET));

    private final static String STOMP_COMMAND_HEADER_KEY = "stompCommand";

    private final static String SUBSCRIPTION_HEADER_KEY = "subscription";

    @Autowired
    private StompAccessManagerService stompAccessManagerService;

    @Autowired
    private TokenAuthenticationService<U, R, S> tokenAuthenticationService;

    @Autowired
    private MessageChannel clientOutboundChannel;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand command = accessor.getCommand();
        if (AUTHENTICATED_COMMANDS.contains(command)) {

            Optional<Principal> principal = Optional.empty();

            String token = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
            if (stompAccessManagerService.decideAccess(message) != ALLOW_ANONYMOUS) {
                if (token != null) {
                    try {
                        principal = Optional.of(tokenAuthenticationService.authenticate(token));
                    } catch (Exception exception) {

                        if (exception instanceof ExpiredTokenException) {
                            send(accessor, EXPIRED_RESPONSE);
                        } else {
                            send(accessor, ERROR_RESPONSE);
                        }
                        message = null;
                    }
                } else {
                    if (command == SEND) {
                        send(accessor, UNAUTHORIZED_RESPONSE);
                    }
                    message = null;
                }
            } else {
                if (command == CONNECT) {
                    principal = Optional.of(new AnonymousAuthenticationToken("access", randomUUID().toString(), ANONYMOUS_AUTHORITIES));
                }
            }

            if (principal.isPresent()) {
                accessor.setUser(principal.get());
            }

        }
        return message;
    }

    private void send(StompHeaderAccessor accessor, byte[] response) {

        try {
            String path = stompAccessManagerService.getPath(accessor);

            Optional<HandlerMethod> handler = stompAccessManagerService.findHandler(path);
            if (handler.isPresent()) {

                accessor.removeNativeHeader(AUTHORIZATION_HEADER);
                accessor.setContentType(DEFAULT_MIME_TYPE);
                accessor.setHeader(STOMP_COMMAND_HEADER_KEY, MESSAGE);

                SimpUser simpUser = simpUserRegistry.getUser(accessor.getUser().getName());
                SimpSession simpSession = simpUser.getSession(accessor.getSessionId());
                for (SimpSubscription simpSubscription : simpSession.getSubscriptions()) {

                    // TODO: prefixes needs to be a property used in web socket configuration
                    if (simpSubscription.getDestination().equals(accessor.getDestination().replaceAll("/ws", "/private/queue"))) {
                        accessor.setDestination(simpSubscription.getDestination());
                        accessor.setSubscriptionId(simpSubscription.getId());
                        accessor.setNativeHeader(SUBSCRIPTION_HEADER_KEY, simpSubscription.getId());
                        clientOutboundChannel.send(MessageBuilder.createMessage(response, accessor.getMessageHeaders()));
                        return;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
