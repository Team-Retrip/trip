package com.retrip.trip.infra.adapter.in.presentation.filter;

import com.retrip.trip.application.in.request.context.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String pathLowercase = path.toLowerCase();

        // 제외할 URL 체크
        if (path.equals("/") ||
                pathLowercase.contains("swagger") ||
                pathLowercase.contains("api-docs") ||
                pathLowercase.contains("actuator") ||
                pathLowercase.contains("robots.txt") ||
                pathLowercase.contains("status-check")) {
            filterChain.doFilter(request, response);
            return; // 필터 종료
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        request.setAttribute("userContext", UserContext.mockOf());
        filterChain.doFilter(request, response);
    }
}
