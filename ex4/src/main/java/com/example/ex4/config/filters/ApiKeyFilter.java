package com.example.ex4.config.filters;

import com.example.ex4.config.authentication.ApiKeyAuthentication;
import com.example.ex4.config.managers.CustomAuthenticationManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private final String key;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CustomAuthenticationManager authenticationManager = new CustomAuthenticationManager(key);

        var requestKey = request.getHeader("X-Api-Key");

        // No API key provided, continue the filter chain
        if (requestKey == null) {
            filterChain.doFilter(request, response);
            return;
        }

        var auth = new ApiKeyAuthentication(requestKey);
        try {
            var authentication = authenticationManager.authenticate(auth);
            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (AuthenticationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
