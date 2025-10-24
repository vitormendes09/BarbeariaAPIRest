package barbearia.com.br.barbearia.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String role;
    
    public AuthResponse(String token, Long userId, String email, String role) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.role = role;
    } 
}