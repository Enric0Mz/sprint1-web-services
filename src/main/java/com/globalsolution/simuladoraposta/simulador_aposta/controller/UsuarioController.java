package com.globalsolution.simuladoraposta.simulador_aposta.controller;

import com.globalsolution.simuladoraposta.simulador_aposta.model.Usuario;
import com.globalsolution.simuladoraposta.simulador_aposta.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody Map<String, String> credenciais) {
        try {
            String username = credenciais.get("username");
            String password = credenciais.get("password");
            Usuario novoUsuario = usuarioService.cadastrarUsuario(username, password);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody Map<String, String> credenciais) {
        try {
            String username = credenciais.get("username");
            String password = credenciais.get("password");
            Usuario usuarioLogado = usuarioService.autenticarUsuario(username, password);

            return ResponseEntity.ok(Map.of(
                    "id", usuarioLogado.getId(),
                    "username", usuarioLogado.getUsername(),
                    "saldo", usuarioLogado.getSaldo()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/{username}/saldo")
    public ResponseEntity<?> getSaldoUsuario(@PathVariable String username) {
        try {
            Usuario usuario = usuarioService.buscarUsuarioPorUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
            return ResponseEntity.ok(Map.of(
                    "username", usuario.getUsername(),
                    "saldo", usuario.getSaldo()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}