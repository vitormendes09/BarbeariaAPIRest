package barbearia.com.br.barbearia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @GetMapping("/security-context")
    public ResponseEntity<?> getSecurityContext() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> context = new HashMap<>();
        
        if (auth == null || !auth.isAuthenticated()) {
            context.put("status", "NOT_AUTHENTICATED");
            context.put("message", "No authentication found in SecurityContext");
        } else {
            context.put("status", "AUTHENTICATED");
            context.put("name", auth.getName());
            context.put("authorities", auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            context.put("authenticated", auth.isAuthenticated());
            context.put("principalType", auth.getPrincipal().getClass().getSimpleName());
            
            // Detalhes do principal
            if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                var userDetails = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
                context.put("username", userDetails.getUsername());
                context.put("enabled", userDetails.isEnabled());
            }
        }
        
        return ResponseEntity.ok(context);
    }
}