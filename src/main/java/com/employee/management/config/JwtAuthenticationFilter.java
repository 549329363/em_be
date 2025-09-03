package com.employee.management.config;

import com.employee.management.service.AuthService;
import com.employee.management.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ApplicationContext applicationContext;

    private AuthService authService;

    private AuthService getAuthService() {
        if (authService == null) {
            authService = applicationContext.getBean(AuthService.class);
        }
        return authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);
            
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (getAuthService().validateToken(token)) {
                    String username = jwtUtils.getUsernameFromToken(token);
                    String rolesStr = jwtUtils.getRolesFromToken(token);
                    
                    if (username != null) {
                        List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesStr.split(","))
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());
                        
                        UsernamePasswordAuthenticationToken authToken = 
                                new UsernamePasswordAuthenticationToken(username, null, authorities);
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT认证失败", e);
        }
        
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}