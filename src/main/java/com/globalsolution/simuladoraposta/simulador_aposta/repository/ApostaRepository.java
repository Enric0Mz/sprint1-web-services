package com.globalsolution.simuladoraposta.simulador_aposta.repository;

import com.globalsolution.simuladoraposta.simulador_aposta.model.Aposta;
import com.globalsolution.simuladoraposta.simulador_aposta.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApostaRepository extends JpaRepository<Aposta, Long> {
    List<Aposta> findTop20ByUsuarioOrderByDataApostaDesc(Usuario usuario);
    List<Aposta> findByUsuario(Usuario usuario);
}