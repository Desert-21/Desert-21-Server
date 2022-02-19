package com.github.maciejmalewicz.Desert21.config.websockets;

import com.github.maciejmalewicz.Desert21.config.security.jwt.JwtConfig;
import com.github.maciejmalewicz.Desert21.config.security.jwt.JwtTokenVerifier;
import com.google.common.base.Strings;
import io.jsonwebtoken.JwtException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.github.maciejmalewicz.Desert21.config.Constants.*;

@Component
public class UserChanelInterceptor implements ChannelInterceptor {

    private final JwtTokenVerifier tokenVerifier;
    private final JwtConfig jwtConfig;

    public UserChanelInterceptor(JwtTokenVerifier tokenVerifier, JwtConfig jwtConfig) {
        this.tokenVerifier = tokenVerifier;
        this.jwtConfig = jwtConfig;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            var header = Objects.requireNonNull(headerAccessor
                            .getNativeHeader("Authorization"))
                    .stream()
                    .findFirst()
                    .orElseThrow(this::exceptionSupplier);

            if (Strings.isNullOrEmpty(header) || !header.startsWith(jwtConfig.getPrefix())){
                throwAuthException();
            }
            var token = header.replace(jwtConfig.getPrefix(), "");
            try {
                var authoritiesAndUsername = tokenVerifier.getAuthoritiesFromToken(token);
                var userAuthority = authoritiesAndUsername
                        .getFirst().stream()
                        .map(SimpleGrantedAuthority::getAuthority)
                        .filter(auth -> auth.startsWith(USER_TOPIC_PREFIX))
                        .findFirst()
                        .orElseThrow(this::exceptionSupplier);
                var userId = userAuthority.replace(USER_TOPIC_PREFIX, "");

                var prefixToReplace = getUserPrefixToReplace();
                var destinationId = Objects.requireNonNull(headerAccessor.getDestination())
                        .replace(prefixToReplace, "");

                if (!userId.equals(destinationId)) {
                    throwAuthException();
                }
            } catch(JwtException exc) {
                throwAuthException();
            }
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }

    private IllegalArgumentException exceptionSupplier() {
        return new IllegalArgumentException("No permission for this topic");
    }

    private void throwAuthException() throws IllegalArgumentException {
        throw new IllegalArgumentException("No permission for this topic");
    }

    private String getUserPrefixToReplace() {
        return WEB_SOCKET_TOPICS_PATH + WEB_SOCKET_USERS_PATH + "/";
    }
}
