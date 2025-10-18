package barbearia.com.br.barbearia.controller;

import barbearia.com.br.barbearia.model.User;
import barbearia.com.br.barbearia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/barbeiros")
@RequiredArgsConstructor
public class BarbeiroController {

    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'BARBEIRO', 'ADMIN')")
    public ResponseEntity<List<User>> listarBarbeirosAtivos() {
        List<User> barbeiros = userRepository.findByRole(User.UserRole.BARBEIRO)
                .stream()
                .filter(User::getAtivo)
                .toList();
        return ResponseEntity.ok(barbeiros);
    }
}