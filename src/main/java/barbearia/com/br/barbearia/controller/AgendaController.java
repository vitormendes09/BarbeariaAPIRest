package barbearia.com.br.barbearia.controller;

import barbearia.com.br.barbearia.model.Agenda;
import barbearia.com.br.barbearia.model.User;
import barbearia.com.br.barbearia.repository.AgendaRepository;
import barbearia.com.br.barbearia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barbeiro/agenda")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('BARBEIRO', 'ADMIN')")
public class AgendaController {

    private final AgendaRepository agendaRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> criarHorarioTrabalho(@RequestBody Agenda agenda) {
        User barbeiro = getCurrentBarbeiro();
        agenda.setBarbeiro(barbeiro);
        agendaRepository.save(agenda);
        return ResponseEntity.ok("Horário de trabalho criado com sucesso");
    }

    @GetMapping
    public ResponseEntity<List<Agenda>> listarMinhaAgenda() {
        User barbeiro = getCurrentBarbeiro();
        List<Agenda> agenda = agendaRepository.findByBarbeiroAndAtivoTrue(barbeiro);
        return ResponseEntity.ok(agenda);
    }

    private User getCurrentBarbeiro() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}