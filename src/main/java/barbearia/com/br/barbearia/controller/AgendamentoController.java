package barbearia.com.br.barbearia.controller;

import barbearia.com.br.barbearia.model.HorarioAgendamento;
import barbearia.com.br.barbearia.model.Servico;
import barbearia.com.br.barbearia.model.User;
import barbearia.com.br.barbearia.repository.HorarioAgendamentoRepository;
import barbearia.com.br.barbearia.repository.ServicoRepository;
import barbearia.com.br.barbearia.repository.UserRepository;
import barbearia.com.br.barbearia.service.AgendamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final UserRepository userRepository;
    private final ServicoRepository servicoRepository;
    private final HorarioAgendamentoRepository horarioAgendamentoRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<?> criarAgendamento(@RequestBody AgendamentoRequest request) {
        try {
            User cliente = getCurrentUser();
            User barbeiro = userRepository.findById(request.barbeiroId())
                    .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));
            
            // Verificar se o barbeiro está ativo
            if (!barbeiro.getAtivo()) {
                return ResponseEntity.badRequest().body("Barbeiro não está ativo");
            }
            
            List<Servico> servicos = servicoRepository.findAllById(request.servicosIds());
            
            // Verificar se todos os serviços existem e estão ativos
            if (servicos.size() != request.servicosIds().size()) {
                return ResponseEntity.badRequest().body("Um ou mais serviços não foram encontrados");
            }
            
            for (Servico servico : servicos) {
                if (!servico.getAtivo()) {
                    return ResponseEntity.badRequest().body("Serviço " + servico.getNome() + " não está ativo");
                }
            }
            
            HorarioAgendamento agendamento = agendamentoService.calcularAgendamento(
                    cliente, barbeiro, request.dataHoraInicio(), servicos);
            
            // Salvar agendamento no banco
            horarioAgendamentoRepository.save(agendamento);
            
            return ResponseEntity.ok("Agendamento criado com sucesso");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/meus")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<HorarioAgendamento>> meusAgendamentos() {
        User usuario = getCurrentUser();
        List<HorarioAgendamento> agendamentos = horarioAgendamentoRepository.findByCliente(usuario);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/barbeiro")
    @PreAuthorize("hasAnyRole('BARBEIRO', 'ADMIN')")
    public ResponseEntity<List<HorarioAgendamento>> agendamentosBarbeiro() {
        User barbeiro = getCurrentUser();
        List<HorarioAgendamento> agendamentos = horarioAgendamentoRepository.findByBarbeiro(barbeiro);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/disponiveis")
    @PreAuthorize("hasAnyRole('CLIENTE', 'BARBEIRO', 'ADMIN')")
    public ResponseEntity<?> listarHorariosDisponiveis(
            @RequestParam Long barbeiroId,
            @RequestParam String data) {
        
        try {
            User barbeiro = userRepository.findById(barbeiroId)
                    .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));
            
            LocalDate dataConsulta = LocalDate.parse(data, DateTimeFormatter.ISO_DATE);
            
            // Implementar lógica para listar horários disponíveis
            // Por enquanto, retornar uma mensagem informativa
            return ResponseEntity.ok(
                String.format("Horários disponíveis para barbeiro %s em %s - Lógica a ser implementada", 
                    barbeiro.getNome(), dataConsulta)
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<?> cancelarAgendamento(@PathVariable Long id) {
        try {
            HorarioAgendamento agendamento = horarioAgendamentoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
            
            // Verificar se o usuário tem permissão para cancelar
            User usuario = getCurrentUser();
            if (!usuario.getId().equals(agendamento.getCliente().getId()) && 
                !usuario.getRole().equals(User.UserRole.ADMIN)) {
                return ResponseEntity.status(403).body("Sem permissão para cancelar este agendamento");
            }
            
            agendamento.setStatus(HorarioAgendamento.StatusAgendamento.CANCELADO);
            horarioAgendamentoRepository.save(agendamento);
            
            return ResponseEntity.ok("Agendamento cancelado com sucesso");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public record AgendamentoRequest(
            Long barbeiroId,
            LocalDateTime dataHoraInicio,
            List<Long> servicosIds
    ) {}
}