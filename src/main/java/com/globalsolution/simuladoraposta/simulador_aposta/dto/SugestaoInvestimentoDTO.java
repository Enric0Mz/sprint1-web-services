package com.globalsolution.simuladoraposta.simulador_aposta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SugestaoInvestimentoDTO {
    public BigDecimal valorOriginal;
    public String tipoSugestao;
    public BigDecimal rendimento1Mes;
    public BigDecimal rendimento6Meses;
    public BigDecimal rendimento1Ano;
    public BigDecimal rendimento5Anos;
    public String mensagemEducacional;
}