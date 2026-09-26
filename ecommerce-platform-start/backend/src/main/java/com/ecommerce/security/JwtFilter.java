package com.ecommerce.security;

import com.ecommerce.model.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Intercepteaza fiecare cerere: citeste "Authorization: Bearer <token>", valideaza tokenul
 * si pune userul curent (AuthUser) in SecurityContext. Fara token valid, cererea ramane
 * neautentificata, iar regulile din SecurityConfig raspund cu 401 pe rutele protejate.
 */
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            try {
                Claims claims = jwtService.parse(token);
                Long id = ((Number) claims.get("id")).longValue();
                Role role = Role.valueOf(claims.get("role", String.class));
                AuthUser principal = new AuthUser(id, claims.getSubject(), role);

                var authentication = new UsernamePasswordAuthenticationToken(
                        principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException ex) {
                // token invalid, expirat sau cu claims lipsa: nu autentificam -> 401 pe rutele protejate
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
