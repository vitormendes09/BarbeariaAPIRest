package barbearia.com.br.barbearia.model;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "agendas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agenda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "barbeiro_id", nullable = false)
    private User barbeiro;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DayOfWeek diaSemana;
    
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    
    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;
    
    @Column(name = "hora_almoco_inicio")
    private LocalTime horaAlmocoInicio;
    
    @Column(name = "hora_almoco_fim")
    private LocalTime horaAlmocoFim;
    
    @Column(nullable = false)
    private Boolean ativo = true;
}