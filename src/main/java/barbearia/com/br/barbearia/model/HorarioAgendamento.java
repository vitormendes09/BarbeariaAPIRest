package barbearia.com.br.barbearia.model;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "horarios_agendamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAgendamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private User cliente;
    
    @ManyToOne
    @JoinColumn(name = "barbeiro_id", nullable = false)
    private User barbeiro;
    
    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;
    
    @Column(name = "data_hora_fim", nullable = false)
    private LocalDateTime dataHoraFim;
    
    @ManyToMany
    @JoinTable(
        name = "horario_servicos",
        joinColumns = @JoinColumn(name = "horario_id"),
        inverseJoinColumns = @JoinColumn(name = "servico_id")
    )
    private List<Servico> servicos;
    
    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;
    
    @Column(name = "tempo_total_minutos", nullable = false)
    private Integer tempoTotalMinutos;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status = StatusAgendamento.AGENDADO;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum StatusAgendamento {
        AGENDADO,
        CONFIRMADO,
        CANCELADO,
        FINALIZADO
    }
}