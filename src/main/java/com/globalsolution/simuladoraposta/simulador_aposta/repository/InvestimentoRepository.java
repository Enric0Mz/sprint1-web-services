package com.globalsolution.simuladoraposta.simulador_aposta.repository;

import com.globalsolution.simuladoraposta.simulador_aposta.model.Investimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestimentoRepository extends JpaRepository<Investimento, Long> {
}