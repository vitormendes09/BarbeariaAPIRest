package barbearia.com.br.barbearia.repository;

import barbearia.com.br.barbearia.model.Servico;
import barbearia.com.br.barbearia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    List<Servico> findByBarbeiroAndAtivoTrue(User barbeiro);
    List<Servico> findByAtivoTrue();
}