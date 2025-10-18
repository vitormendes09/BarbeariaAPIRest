package barbearia.com.br.barbearia.repository;

import barbearia.com.br.barbearia.model.HorarioAgendamento;
import barbearia.com.br.barbearia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HorarioAgendamentoRepository extends JpaRepository<HorarioAgendamento, Long> {
    List<HorarioAgendamento> findByBarbeiroAndDataHoraInicioBetween(User barbeiro, LocalDateTime inicio, LocalDateTime fim);
    List<HorarioAgendamento> findByCliente(User cliente);
    List<HorarioAgendamento> findByBarbeiro(User barbeiro);
}