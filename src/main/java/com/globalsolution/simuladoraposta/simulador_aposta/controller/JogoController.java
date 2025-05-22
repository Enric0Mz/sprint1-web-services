package com.globalsolution.simuladoraposta.simulador_aposta.controller;

import com.globalsolution.simuladoraposta.simulador_aposta.model.Aposta;
import com.globalsolution.simuladoraposta.simulador_aposta.model.Usuario;
import com.globalsolution.simuladoraposta.simulador_aposta.service.JogoService;
import com.globalsolution.simuladoraposta.simulador_aposta.service.UsuarioService;
import com.globalsolution.simuladoraposta.simulador_aposta.repository.ApostaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jogo")
public class JogoController {

    @Autowired
    private JogoService jogoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ApostaRepository apostaRepository;

    @PostMapping("/tigrinho/{usuarioId}/apostar")
    public ResponseEntity<?> apostarTigrinho(
            @PathVariable Long usuarioId,
            @RequestBody Map<String, BigDecimal> apostaRequest) {
        try {
            BigDecimal valorApostado = apostaRequest.get("valorApostado");
            Aposta resultadoAposta = jogoService.jogarTigrinho(usuarioId, valorApostado);

            return ResponseEntity.ok(Map.of(
                    "status", "sucesso",
                    "mensagem", "Aposta realizada com sucesso!",
                    "resultado", resultadoAposta.getResultado(),
                    "valorApostado", resultadoAposta.getValorApostado(),
                    "valorGanhoPerda", resultadoAposta.getValorGanhoPerda(),
                    "saldoAtual", resultadoAposta.getSaldoAposAposta(),
                    "dataAposta", resultadoAposta.getDataAposta()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "erro", "mensagem", e.getMessage()));
        }
    }

    @GetMapping("/{usuarioId}/historico") // Endpoint para histórico (RF016)
    public ResponseEntity<?> getHistoricoApostas(@PathVariable Long usuarioId) {
        try {
            List<Aposta> historico = jogoService.getHistoricoApostas(usuarioId);
            return ResponseEntity.ok(historico);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "erro", "mensagem", e.getMessage()));
        }
    }

    @GetMapping("/{usuarioId}/estatisticas")
    public ResponseEntity<?> getEstatisticas(@PathVariable Long usuarioId) {

        try {
            BigDecimal totalApostado = jogoService.calcularTotalApostado(usuarioId);
            BigDecimal totalGanhoLiquido = jogoService.calcularTotalGanhoLiquido(usuarioId);
            Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);

            List<Aposta> todasApostas = apostaRepository.findByUsuario(usuario);

            long totalRodadas = todasApostas.size();
            long vitorias = todasApostas.stream().filter(a -> a.getValorGanhoPerda().compareTo(BigDecimal.ZERO) > 0).count();
            long derrotas = todasApostas.stream().filter(a -> a.getValorGanhoPerda().compareTo(BigDecimal.ZERO) < 0).count();
            long empates = totalRodadas - vitorias - derrotas; // Casos de 1:1

            double percentualVitorias = (totalRodadas > 0) ? (double) vitorias / totalRodadas * 100 : 0.0;
            double percentualDerrotas = (totalRodadas > 0) ? (double) derrotas / totalRodadas * 100 : 0.0;


            return ResponseEntity.ok(Map.of(
                    "totalRodadas", totalRodadas,
                    "totalApostado", totalApostado,
                    "totalGanhoLiquido", totalGanhoLiquido,
                    "saldoAtual", usuario.getSaldo(),
                    "percentualVitorias", String.format("%.2f%%", percentualVitorias),
                    "percentualDerrotas", String.format("%.2f%%", percentualDerrotas)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", "erro", "mensagem", e.getMessage()));
        }
    }
}