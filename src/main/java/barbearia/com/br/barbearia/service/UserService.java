package barbearia.com.br.barbearia.service;

import barbearia.com.br.barbearia.model.User;
import barbearia.com.br.barbearia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User criarUsuario(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> buscarBarbeirosAtivos() {
        return userRepository.findByRole(User.UserRole.BARBEIRO).stream()
                .filter(User::getAtivo)
                .toList();
    }

    public void desativarUsuario(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        user.setAtivo(false);
        userRepository.save(user);
    }
}