package com.retrip.trip.infra.adapter.in.presentation.filter;

import com.retrip.trip.application.in.request.context.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.public-key}")
    private String publicKeyString;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String pathLowercase = path.toLowerCase();

        if (path.equals("/") ||
                pathLowercase.contains("swagger") ||
                pathLowercase.contains("api-docs") ||
                pathLowercase.contains("actuator") ||
                pathLowercase.contains("robots.txt") ||
                pathLowercase.contains("status-check") ||
                pathLowercase.contains("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Claims claims = getClaims(token);

            String subject = claims.getSubject();
            UUID memberId = UUID.fromString(subject);

            UserContext userContext = new UserContext(
                    memberId,
                    claims.get("nickname", String.class),
                    claims.get("email", String.class),
                    claims.get("name", String.class),
                    null,
                    0
            );

            request.setAttribute("userContext", userContext);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Claims getClaims(String token) throws Exception {
        String sanitizedKey = publicKeyString
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] publicBytes = Base64.getDecoder().decode(sanitizedKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        Jws<Claims> jws = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token);

        return jws.getPayload();
    }
}