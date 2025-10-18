package barbearia.com.br.barbearia.service;

import barbearia.com.br.barbearia.model.Agenda;
import barbearia.com.br.barbearia.model.HorarioAgendamento;
import barbearia.com.br.barbearia.model.Servico;
import barbearia.com.br.barbearia.model.User;
import barbearia.com.br.barbearia.repository.AgendaRepository;
import barbearia.com.br.barbearia.repository.HorarioAgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final HorarioAgendamentoRepository horarioAgendamentoRepository;
    private final AgendaRepository agendaRepository;

    public void validarHorarioDisponivel(LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, User barbeiro) {
        // Verificar se o barbeiro está ativo
        if (!barbeiro.getAtivo()) {
            throw new RuntimeException("Barbeiro não está ativo");
        }

        // Verificar se há conflitos com outros agendamentos
        List<HorarioAgendamento> conflitos = horarioAgendamentoRepository
                .findByBarbeiroAndDataHoraInicioBetween(barbeiro, dataHoraInicio, dataHoraFim);
        
        // Filtrar apenas agendamentos que não estão cancelados
        conflitos = conflitos.stream()
                .filter(ag -> ag.getStatus() != HorarioAgendamento.StatusAgendamento.CANCELADO)
                .toList();
        
        if (!conflitos.isEmpty()) {
            throw new RuntimeException("Horário já está agendado para este barbeiro");
        }

        // Verificar se o barbeiro trabalha neste horário
        DayOfWeek diaSemana = dataHoraInicio.getDayOfWeek();
        var agendaOpt = agendaRepository.findByBarbeiroAndDiaSemanaAndAtivoTrue(barbeiro, diaSemana);
        
        if (agendaOpt.isEmpty()) {
            throw new RuntimeException("Barbeiro não trabalha neste dia da semana");
        }

        Agenda agenda = agendaOpt.get();
        LocalTime horaInicio = dataHoraInicio.toLocalTime();
        LocalTime horaFim = dataHoraFim.toLocalTime();

        if (horaInicio.isBefore(agenda.getHoraInicio()) || horaFim.isAfter(agenda.getHoraFim())) {
            throw new RuntimeException("Horário fora do expediente do barbeiro");
        }

        // Verificar horário de almoço
        if (agenda.getHoraAlmocoInicio() != null && agenda.getHoraAlmocoFim() != null) {
            if (horaInicio.isBefore(agenda.getHoraAlmocoFim()) && horaFim.isAfter(agenda.getHoraAlmocoInicio())) {
                throw new RuntimeException("Horário conflita com horário de almoço");
            }
        }
    }

    public HorarioAgendamento calcularAgendamento(User cliente, User barbeiro, LocalDateTime dataHoraInicio, List<Servico> servicos) {
        // Calcular tempo total e valor total
        int tempoTotal = servicos.stream()
                .mapToInt(Servico::getTempoEstimadoMinutos)
                .sum();
        
        BigDecimal valorTotal = servicos.stream()
                .map(Servico::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime dataHoraFim = dataHoraInicio.plusMinutes(tempoTotal);

        // Validar horário
        validarHorarioDisponivel(dataHoraInicio, dataHoraFim, barbeiro);

        HorarioAgendamento agendamento = new HorarioAgendamento();
        agendamento.setCliente(cliente);
        agendamento.setBarbeiro(barbeiro);
        agendamento.setDataHoraInicio(dataHoraInicio);
        agendamento.setDataHoraFim(dataHoraFim);
        agendamento.setServicos(servicos);
        agendamento.setValorTotal(valorTotal);
        agendamento.setTempoTotalMinutos(tempoTotal);
        agendamento.setStatus(HorarioAgendamento.StatusAgendamento.AGENDADO);

        return agendamento;
    }
}