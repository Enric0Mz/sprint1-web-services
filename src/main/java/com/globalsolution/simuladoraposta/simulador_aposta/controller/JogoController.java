package com.globalsolution.simuladoraposta.simulador_aposta.controller;

import com.globalsolution.simuladoraposta.simulador_aposta.dto.ApostaResult;
import com.globalsolution.simuladoraposta.simulador_aposta.model.Aposta;
import com.globalsolution.simuladoraposta.simulador_aposta.model.Usuario;
import com.globalsolution.simuladoraposta.simulador_aposta.repository.ApostaRepository;
import com.globalsolution.simuladoraposta.simulador_aposta.service.JogoService;
import com.globalsolution.simuladoraposta.simulador_aposta.service.UsuarioService;
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
            ApostaResult resultadoCompleto = jogoService.jogarTigrinho(usuarioId, valorApostado);

            Map<String, Object> responseBody = Map.of(
                    "status", "sucesso",
                    "mensagem", "Aposta realizada com sucesso!",
                    "aposta", Map.of(
                            "resultado", resultadoCompleto.getAposta().getResultado(),
                            "valorApostado", resultadoCompleto.getAposta().getValorApostado(),
                            "valorGanhoPerda", resultadoCompleto.getAposta().getValorGanhoPerda(),
                            "saldoAtual", resultadoCompleto.getAposta().getSaldoAposAposta(),
                            "dataAposta", resultadoCompleto.getAposta().getDataAposta()
                    ),
                    "sugestoesInvestimento", resultadoCompleto.getSugestoesInvestimento() != null ? resultadoCompleto.getSugestoesInvestimento() : null
            );
            return ResponseEntity.ok(responseBody);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "erro", "mensagem", e.getMessage()));
        }
    }

    @GetMapping("/{usuarioId}/historico")
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
            long empates = totalRodadas - vitorias - derrotas;

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