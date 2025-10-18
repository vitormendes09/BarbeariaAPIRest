package barbearia.com.br.barbearia.controller;

import barbearia.com.br.barbearia.dto.AuthRequest;
import barbearia.com.br.barbearia.dto.AuthResponse;
import barbearia.com.br.barbearia.infra.security.TokenService;
import barbearia.com.br.barbearia.model.User;
import barbearia.com.br.barbearia.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;



import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email já está em uso");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNome(request.getEmail().split("@")[0]);
        user.setRole(User.UserRole.CLIENTE);
        
        userRepository.save(user);
        
        return ResponseEntity.ok("Usuário registrado com sucesso");
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
           
            
            
            // Buscar o usuário real do banco de dados
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuário não encontrado");
            }
            
            User user = userOptional.get();
            var token = tokenService.generateToken(user);
            
            AuthResponse response = new AuthResponse(
                token, 
                user.getId(), 
                user.getEmail(), 
                user.getRole().name()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Credenciais inválidas");
        }
    }

    @PostMapping("/register-barbeiro")
    public ResponseEntity<?> registerBarbeiro(@Valid @RequestBody AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email já está em uso");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNome(request.getEmail().split("@")[0]);
        user.setRole(User.UserRole.BARBEIRO);
        
        userRepository.save(user);
        
        return ResponseEntity.ok("Barbeiro registrado com sucesso");
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody AuthRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
        return ResponseEntity.badRequest().body("Email já está em uso");
    }
    
    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setNome(request.getEmail().split("@")[0]);
    user.setRole(User.UserRole.ADMIN);
    
    userRepository.save(user);
    
    return ResponseEntity.ok("Administrador registrado com sucesso");
}
}