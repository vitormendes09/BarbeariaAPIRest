package barbearia.com.br.barbearia.controller;

import barbearia.com.br.barbearia.model.Servico;
import barbearia.com.br.barbearia.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
public class ServicoPublicController {
    
    private final ServicoRepository servicoRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'BARBEIRO', 'ADMIN')")
    public ResponseEntity<List<Servico>> listarServicosAtivos() {
        List<Servico> servicos = servicoRepository.findByAtivoTrue();
        return ResponseEntity.ok(servicos);
    }
}