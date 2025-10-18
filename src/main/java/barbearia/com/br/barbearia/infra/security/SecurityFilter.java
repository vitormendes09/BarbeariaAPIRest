package barbearia.com.br.barbearia.infra.security;

import barbearia.com.br.barbearia.services.AuthorizationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final AuthorizationService authorizationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("=== SECURITY FILTER DEBUG ===");
        log.debug("Request URI: {}", request.getRequestURI());
        log.debug("Method: {}", request.getMethod());
        
        // Log todos os headers
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.debug("Header: {} = {}", headerName, headerValue);
        }
        
        var token = this.recoverToken(request);
        log.debug("Recovered token: {}", token);
        
        if(token != null && !token.trim().isEmpty()){
            log.debug("Token length: {}", token.length());
            log.debug("Token starts with: {}", token.substring(0, Math.min(10, token.length())));
            
            var email = tokenService.validateToken(token);
            log.debug("Validated email: {}", email);
            
            if (email != null && !email.isEmpty()) {
                try {
                    UserDetails userDetails = authorizationService.loadUserByUsername(email);
                    if (userDetails != null && userDetails.isEnabled()) {
                        var authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug(" Authenticated user: {} with authorities: {}", email, userDetails.getAuthorities());
                    } else {
                        log.debug(" User not enabled or not found: {}", email);
                    }
                } catch (Exception e) {
                    log.error("Error loading user: {}", e.getMessage());
                }
            } else {
                log.debug(" Token validation failed for token: {}", token);
            }
        } else {
            log.debug(" No token found in request");
        }
        
        log.debug("=== END SECURITY FILTER DEBUG ===");
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        log.debug("Authorization Header: {}", authHeader);
        
        if(authHeader == null || authHeader.trim().isEmpty()) {
            log.debug("No Authorization header found");
            return null;
        }
        
        // Verifica se o header começa com "Bearer " (case insensitive)
        if (authHeader.startsWith("Bearer ") || authHeader.startsWith("bearer ")) {
            String token = authHeader.substring(7).trim();
            log.debug("Extracted token: {}", token);
            return token;
        }
        
        log.debug("Authorization header doesn't start with 'Bearer '");
        return null;
    }
}