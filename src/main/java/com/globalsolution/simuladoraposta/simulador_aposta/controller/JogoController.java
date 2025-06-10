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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/jogo")
public class JogoController {

    @Autowired
    private JogoService jogoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ApostaRepository apostaRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/tigrinho/{usuarioId}/apostar")
    public ResponseEntity<?> apostarTigrinho(
            @PathVariable Long usuarioId,
            @RequestBody Map<String, BigDecimal> apostaRequest) {
        try {
            BigDecimal valorApostado = apostaRequest.get("valorApostado");

            if (valorApostado == null) {
                return ResponseEntity.badRequest().body(Map.of("status", "erro", "mensagem", "Valor da aposta é obrigatório."));
            }

            ApostaResult resultadoCompleto = jogoService.jogarTigrinho(usuarioId, valorApostado);

            Aposta aposta = Optional.ofNullable(resultadoCompleto)
                    .map(ApostaResult::getAposta)
                    .orElseThrow(() -> new RuntimeException("Detalhes da aposta não foram gerados corretamente."));

            String resultado = Optional.ofNullable(aposta.getResultado()).orElse("DESCONHECIDO");
            BigDecimal valorApostadoAposta = Optional.ofNullable(aposta.getValorApostado()).orElse(BigDecimal.ZERO);
            BigDecimal valorGanhoPerda = Optional.ofNullable(aposta.getValorGanhoPerda()).orElse(BigDecimal.ZERO);
            BigDecimal saldoAtual = Optional.ofNullable(aposta.getSaldoAposAposta()).orElse(BigDecimal.ZERO);
            String dataAposta = Optional.ofNullable(aposta.getDataAposta())
                    .map(DATE_TIME_FORMATTER::format)
                    .orElse("Data Desconhecida");

            Object sugestoesInvestimento = resultadoCompleto.getSugestoesInvestimento() != null ? resultadoCompleto.getSugestoesInvestimento() : null;

            Map<String, Object> responseBody = Map.of(
                    "status", "sucesso",
                    "mensagem", "Aposta realizada com sucesso!",
                    "aposta", Map.of(
                            "resultado", resultado,
                            "valorApostado", valorApostadoAposta,
                            "valorGanhoPerda", valorGanhoPerda,
                            "saldoAtual", saldoAtual,
                            "dataAposta", dataAposta // Use the formatted string
                    ),
                    "sugestoesInvestimento", sugestoesInvestimento
            );
            return ResponseEntity.ok(responseBody);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "erro", "mensagem", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace(); // Keep this during debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "erro", "mensagem", "Ocorreu um erro inesperado no servidor: " + e.getMessage()));
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
            long empates = totalRodadas - vitorias - derrotas; // This should be for GANHO_PEQUENO, which is a net zero

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