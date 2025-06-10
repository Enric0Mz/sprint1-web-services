package com.globalsolution.simuladoraposta.simulador_aposta.service;

import com.globalsolution.simuladoraposta.simulador_aposta.dto.ApostaResult;
import com.globalsolution.simuladoraposta.simulador_aposta.dto.SugestaoInvestimentoDTO;
import com.globalsolution.simuladoraposta.simulador_aposta.model.Aposta;
import com.globalsolution.simuladoraposta.simulador_aposta.model.Usuario;
import com.globalsolution.simuladoraposta.simulador_aposta.repository.ApostaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode; // Import RoundingMode
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class JogoService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ApostaRepository apostaRepository;

    @Autowired
    private InvestimentoService investimentoService;

    private final Random random = new Random();

    private static final double PROBABILIDADE_PERDA = 0.95; // 95%
    private static final double PROBABILIDADE_PEQUENO_GANHO = 0.04; // 4% (1:1)
    private static final double PROBABILIDADE_MEDIO_GANHO = 0.009; // 0.9% (2:1)
    private static final double PROBABILIDADE_GRANDE_GANHO = 0.001; // 0.1% (5:1)

    private static final int DECIMAL_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Transactional
    public ApostaResult jogarTigrinho(Long usuarioId, BigDecimal valorApostado) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);

        // Input validation (already good)
        if (usuario.getSaldo().compareTo(valorApostado) < 0) {
            throw new RuntimeException("Saldo insuficiente para esta aposta!");
        }
        if (valorApostado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O valor da aposta deve ser maior que zero.");
        }
        if (valorApostado.compareTo(new BigDecimal("100.00")) < 0 ||
                valorApostado.compareTo(new BigDecimal("1000.00")) > 0) {
            throw new RuntimeException("Valor da aposta fora dos limites permitidos (R$100 - R$1000).");
        }

        usuarioService.atualizarSaldo(usuarioId, valorApostado.negate());

        BigDecimal valorRecebidoNoGanho = BigDecimal.ZERO; // Total amount received if winning
        BigDecimal valorGanhoOuPerdaLiquido = valorApostado.negate(); // Net gain/loss for Aposta record
        String resultadoTexto = "PERDA";

        double sorteio = random.nextDouble();

        if (sorteio < PROBABILIDADE_GRANDE_GANHO) { // JACKPOT (5:1)
            valorRecebidoNoGanho = valorApostado.multiply(new BigDecimal("5.0")).setScale(DECIMAL_SCALE, ROUNDING_MODE);
            resultadoTexto = "GANHO_JACKPOT";
        } else if (sorteio < (PROBABILIDADE_GRANDE_GANHO + PROBABILIDADE_MEDIO_GANHO)) { // MEDIUM (2:1)
            valorRecebidoNoGanho = valorApostado.multiply(new BigDecimal("2.0")).setScale(DECIMAL_SCALE, ROUNDING_MODE);
            resultadoTexto = "GANHO_MEDIO";
        } else if (sorteio < (PROBABILIDADE_GRANDE_GANHO + PROBABILIDADE_MEDIO_GANHO + PROBABILIDADE_PEQUENO_GANHO)) { // SMALL (1:1)
            valorRecebidoNoGanho = valorApostado.multiply(new BigDecimal("1.0")).setScale(DECIMAL_SCALE, ROUNDING_MODE);
            resultadoTexto = "GANHO_PEQUENO";
        }

        valorGanhoOuPerdaLiquido = valorRecebidoNoGanho.subtract(valorApostado).setScale(DECIMAL_SCALE, ROUNDING_MODE);


        if (valorRecebidoNoGanho.compareTo(BigDecimal.ZERO) > 0) {
            usuarioService.atualizarSaldo(usuarioId, valorRecebidoNoGanho);
        }

        usuario = usuarioService.buscarUsuarioPorId(usuarioId);


        Aposta aposta = new Aposta();
        aposta.setUsuario(usuario);
        aposta.setValorApostado(valorApostado.setScale(DECIMAL_SCALE, ROUNDING_MODE));
        aposta.setResultado(resultadoTexto);
        aposta.setValorGanhoPerda(valorGanhoOuPerdaLiquido);
        aposta.setSaldoAposAposta(usuario.getSaldo().setScale(DECIMAL_SCALE, ROUNDING_MODE));
        aposta.setDataAposta(LocalDateTime.now());
        aposta.setTipoAposta("TIGRINHO");

        apostaRepository.save(aposta);

        List<SugestaoInvestimentoDTO> sugestoesInvestimento = null;
        if ("PERDA".equals(resultadoTexto)) {
            sugestoesInvestimento = investimentoService.gerarSugestoes(valorApostado.setScale(DECIMAL_SCALE, ROUNDING_MODE), usuarioId);
        }

        return new ApostaResult(aposta, sugestoesInvestimento);
    }

    public List<Aposta> getHistoricoApostas(Long usuarioId) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);
        return apostaRepository.findTop20ByUsuarioOrderByDataApostaDesc(usuario);
    }

    public BigDecimal calcularTotalApostado(Long usuarioId) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);
        return apostaRepository.findByUsuario(usuario)
                .stream()
                .map(Aposta::getValorApostado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularTotalGanhoLiquido(Long usuarioId) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);
        return apostaRepository.findByUsuario(usuario)
                .stream()
                .map(Aposta::getValorGanhoPerda)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}