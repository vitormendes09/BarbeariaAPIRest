package barbearia.com.br.barbearia.repository;

import barbearia.com.br.barbearia.model.Agenda;
import barbearia.com.br.barbearia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {
    List<Agenda> findByBarbeiroAndAtivoTrue(User barbeiro);
    Optional<Agenda> findByBarbeiroAndDiaSemanaAndAtivoTrue(User barbeiro, DayOfWeek diaSemana);
}