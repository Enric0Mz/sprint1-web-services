package com.globalsolution.simuladoraposta.simulador_aposta.service;

import com.globalsolution.simuladoraposta.simulador_aposta.model.Usuario;
import com.globalsolution.simuladoraposta.simulador_aposta.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario cadastrarUsuario(String username, String password) {

        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Nome de usuário já existe!");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername(username);
        novoUsuario.setPassword(passwordEncoder.encode(password));
        novoUsuario.setSaldo(new BigDecimal("10000.00"));
        novoUsuario.setDataCriacao(LocalDateTime.now());

        return usuarioRepository.save(novoUsuario);
    }

    public Usuario autenticarUsuario(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado!");
        }

        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Senha inválida!");
        }
        return usuario;
    }

    public Optional<Usuario> buscarUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
    }

    public Usuario atualizarSaldo(Long usuarioId, BigDecimal valor) {

        Usuario usuario = buscarUsuarioPorId(usuarioId);
        usuario.setSaldo(usuario.getSaldo().add(valor));
        return usuarioRepository.save(usuario);
    }

}