package barbearia.com.br.barbearia.controller;

import barbearia.com.br.barbearia.model.Servico;
import barbearia.com.br.barbearia.model.User;
import barbearia.com.br.barbearia.repository.ServicoRepository;
import barbearia.com.br.barbearia.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barbeiro")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('BARBEIRO', 'ADMIN')")
public class ServicoController {

    private final ServicoRepository servicoRepository;
    private final UserRepository userRepository;

    @PostMapping("/servicos")
    public ResponseEntity<?> criarServico(@Valid @RequestBody Servico servico) {
        User barbeiro = getCurrentBarbeiro();
        servico.setBarbeiro(barbeiro);
        servicoRepository.save(servico);
        return ResponseEntity.ok("Serviço criado com sucesso");
    }

    @GetMapping("/servicos")
    public ResponseEntity<List<Servico>> listarMeusServicos() {
        User barbeiro = getCurrentBarbeiro();
        List<Servico> servicos = servicoRepository.findByBarbeiroAndAtivoTrue(barbeiro);
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/servicos/todos")
    public ResponseEntity<List<Servico>> listarTodosServicos() {
        List<Servico> servicos = servicoRepository.findByAtivoTrue();
        return ResponseEntity.ok(servicos);
    }

    @PatchMapping("/servicos/{id}/toggle")
    public ResponseEntity<?> toggleServicoStatus(@PathVariable Long id) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
        
        // Verificar se o barbeiro é o dono do serviço ou é ADMIN
        User currentUser = getCurrentBarbeiro();
        if (!currentUser.getId().equals(servico.getBarbeiro().getId()) && 
            !currentUser.getRole().equals(User.UserRole.ADMIN)) {
            return ResponseEntity.status(403).body("Sem permissão para editar este serviço");
        }
        
        servico.setAtivo(!servico.getAtivo());
        servicoRepository.save(servico);
        return ResponseEntity.ok("Status do serviço atualizado");
    }

    private User getCurrentBarbeiro() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}