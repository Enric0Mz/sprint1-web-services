package com.globalsolution.simuladoraposta.simulador_aposta.repository;

import com.globalsolution.simuladoraposta.simulador_aposta.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
}