package com.globalsolution.simuladoraposta.simulador_aposta.service;

import com.globalsolution.simuladoraposta.simulador_aposta.model.Aposta;
import com.globalsolution.simuladoraposta.simulador_aposta.model.Usuario;
import com.globalsolution.simuladoraposta.simulador_aposta.repository.ApostaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class JogoService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ApostaRepository apostaRepository;

    private final Random random = new Random();

    private static final double PROBABILIDADE_PERDA = 0.95; // 95%
    private static final double PROBABILIDADE_PEQUENO_GANHO = 0.04; // 4% (1:1)
    private static final double PROBABILIDADE_MEDIO_GANHO = 0.009; // 0.9% (2:1)
    private static final double PROBABILIDADE_GRANDE_GANHO = 0.001; // 0.1% (5:1)

    @Transactional
    public Aposta jogarTigrinho(Long usuarioId, BigDecimal valorApostado) {

        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);

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

        BigDecimal valorGanhoPerda = valorApostado.negate();
        String resultadoTexto = "PERDA";

        double sorteio = random.nextDouble();

        if (sorteio < PROBABILIDADE_GRANDE_GANHO) { // 0.1%
            valorGanhoPerda = valorApostado.multiply(new BigDecimal("5.0"));
            resultadoTexto = "GANHO_JACKPOT";
        } else if (sorteio < (PROBABILIDADE_GRANDE_GANHO + PROBABILIDADE_MEDIO_GANHO)) { // 0.1% + 0.9% = 1%
            valorGanhoPerda = valorApostado.multiply(new BigDecimal("2.0"));
            resultadoTexto = "GANHO_MEDIO";
        } else if (sorteio < (PROBABILIDADE_GRANDE_GANHO + PROBABILIDADE_MEDIO_GANHO + PROBABILIDADE_PEQUENO_GANHO)) { // 1% + 4% = 5%
            valorGanhoPerda = valorApostado.multiply(new BigDecimal("1.0"));
            resultadoTexto = "GANHO_PEQUENO";
        }

        if (valorGanhoPerda.compareTo(BigDecimal.ZERO) > 0) {
            usuarioService.atualizarSaldo(usuarioId, valorGanhoPerda);
        }

        usuario = usuarioService.buscarUsuarioPorId(usuarioId);

        Aposta aposta = new Aposta();
        aposta.setUsuario(usuario);
        aposta.setValorApostado(valorApostado);
        aposta.setResultado(resultadoTexto);

        aposta.setValorGanhoPerda(valorGanhoPerda.subtract(valorApostado.negate()));

        if (resultadoTexto.equals("GANHO_PEQUENO")) {
            aposta.setValorGanhoPerda(BigDecimal.ZERO); // Lucro 0 (recebeu de volta o que apostou)
        } else if (resultadoTexto.equals("PERDA")) {
            aposta.setValorGanhoPerda(valorApostado.negate()); // Perdeu o valor apostado
        } else if (resultadoTexto.equals("GANHO_MEDIO")) {
            aposta.setValorGanhoPerda(valorApostado); // Lucro de 1x o valor apostado
        } else if (resultadoTexto.equals("GANHO_JACKPOT")) {
            aposta.setValorGanhoPerda(valorApostado.multiply(new BigDecimal("4.0"))); // Lucro de 4x o valor apostado
        }

        aposta.setSaldoAposAposta(usuario.getSaldo());
        aposta.setDataAposta(LocalDateTime.now());
        aposta.setTipoAposta("TIGRINHO");

        return apostaRepository.save(aposta);
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