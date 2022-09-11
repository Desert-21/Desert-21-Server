package com.github.maciejmalewicz.Desert21.config.security.jwt;

import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenVerifier extends OncePerRequestFilter {

    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;


    public JwtTokenVerifier(JwtConfig jwtConfig, SecretKey secretKey) {
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (Strings.isNullOrEmpty(header) || !header.startsWith(jwtConfig.getPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.replace(jwtConfig.getPrefix(), "");


        try {
            var authWithUsername = getAuthoritiesFromToken(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authWithUsername.getSecond(),
                    null,
                    authWithUsername.getFirst()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException exc) {
            handleInvalidToken(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleInvalidToken(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
    }


    /**
     * @param token Jwt token in a form of String
     * @return A pair of:
     * - list of granted authority
     * - username
     * @throws JwtException when something goes wrong with reading the token
     */
    public Pair<Set<SimpleGrantedAuthority>, String> getAuthoritiesFromToken(String token) throws JwtException {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);

        Claims body = claims.getBody();
        List<Map<String, String>> authorities = (List<Map<String, String>>) body.get("authorities");

        var auth = authorities.stream()
                .map(m -> new SimpleGrantedAuthority(m.get("authority")))
                .collect(Collectors.toSet());
        var username = body.getSubject();
        return Pair.of(auth, username);
    }
}
