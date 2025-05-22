package com.globalsolution.simuladoraposta.simulador_aposta.service;

import com.globalsolution.simuladoraposta.simulador_aposta.dto.SugestaoInvestimentoDTO;
import com.globalsolution.simuladoraposta.simulador_aposta.model.Investimento;
import com.globalsolution.simuladoraposta.simulador_aposta.model.Usuario;
import com.globalsolution.simuladoraposta.simulador_aposta.repository.InvestimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvestimentoService {

    @Autowired(required = false)
    private InvestimentoRepository investimentoRepository;

    @Autowired
    private UsuarioService usuarioService;

    private static final BigDecimal TAXA_CDB_MENSAL = new BigDecimal("0.010"); // 1.0%
    private static final BigDecimal TAXA_TESOURO_SELIC_MENSAL = new BigDecimal("0.011"); // 1.1%

    private static final MathContext MC = new MathContext(10); // 10 dígitos de precisão

    /**
     * Calcula o montante final de um investimento com juros compostos.
     * Formula: Montante = Principal * (1 + Taxa)^Periodos
     * @param principal O valor inicial a ser investido.
     * @param taxaMensal A taxa de juros mensal (ex: 0.01 para 1%).
     * @param periodosMeses O número de períodos (meses) do investimento.
     * @return O montante final, arredondado para duas casas decimais.
     */
    private BigDecimal calcularJurosCompostos(BigDecimal principal, BigDecimal taxaMensal, int periodosMeses) {
        BigDecimal fator = BigDecimal.ONE.add(taxaMensal);
        BigDecimal montante = principal;
        for (int i = 0; i < periodosMeses; i++) {
            montante = montante.multiply(fator, MC);
        }
        return montante.setScale(2, RoundingMode.HALF_UP);
    }

    public List<SugestaoInvestimentoDTO> gerarSugestoes(BigDecimal valorPerdido, Long usuarioId) {
        List<SugestaoInvestimentoDTO> sugestoes = new ArrayList<>();
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);

        // CDB
        SugestaoInvestimentoDTO cdb = new SugestaoInvestimentoDTO();
        cdb.valorOriginal = valorPerdido;
        cdb.tipoSugestao = "CDB (Certificado de Depósito Bancário)";
        cdb.rendimento1Mes = calcularJurosCompostos(valorPerdido, TAXA_CDB_MENSAL, 1);
        cdb.rendimento6Meses = calcularJurosCompostos(valorPerdido, TAXA_CDB_MENSAL, 6);
        cdb.rendimento1Ano = calcularJurosCompostos(valorPerdido, TAXA_CDB_MENSAL, 12);
        cdb.rendimento5Anos = calcularJurosCompostos(valorPerdido, TAXA_CDB_MENSAL, 60);
        cdb.mensagemEducacional = "CDBs podem oferecer rendimentos maiores que a poupança, com segurança similar e liquidez em alguns casos. Pense nisso para objetivos de médio prazo!";
        sugestoes.add(cdb);

        // Tesouro Selic
        SugestaoInvestimentoDTO tesouroSelic = new SugestaoInvestimentoDTO();
        tesouroSelic.valorOriginal = valorPerdido;
        tesouroSelic.tipoSugestao = "Tesouro Selic";
        tesouroSelic.rendimento1Mes = calcularJurosCompostos(valorPerdido, TAXA_TESOURO_SELIC_MENSAL, 1);
        tesouroSelic.rendimento6Meses = calcularJurosCompostos(valorPerdido, TAXA_TESOURO_SELIC_MENSAL, 6);
        tesouroSelic.rendimento1Ano = calcularJurosCompostos(valorPerdido, TAXA_TESOURO_SELIC_MENSAL, 12);
        tesouroSelic.rendimento5Anos = calcularJurosCompostos(valorPerdido, TAXA_TESOURO_SELIC_MENSAL, 60);
        tesouroSelic.mensagemEducacional = "O Tesouro Selic é o investimento mais seguro do Brasil, acompanha a taxa básica de juros e é ótimo para reserva de emergência e objetivos de curto/médio prazo.";
        sugestoes.add(tesouroSelic);

        if (investimentoRepository != null) {
            for (SugestaoInvestimentoDTO dto : sugestoes) {
                Investimento invest = new Investimento();
                invest.setUsuario(usuario);
                invest.setValorOriginal(dto.valorOriginal);
                invest.setTipoSugestao(dto.tipoSugestao);
                invest.setRendimento1Mes(dto.rendimento1Mes);
                invest.setRendimento6Meses(dto.rendimento6Meses);
                invest.setRendimento1Ano(dto.rendimento1Ano);
                invest.setRendimento5Anos(dto.rendimento5Anos);
                invest.setDataSimulacao(LocalDateTime.now());
                investimentoRepository.save(invest);
            }
        }

        return sugestoes;
    }
}