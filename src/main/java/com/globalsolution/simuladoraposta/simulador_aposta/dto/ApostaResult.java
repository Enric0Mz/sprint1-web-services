package com.globalsolution.simuladoraposta.simulador_aposta.dto;

import com.globalsolution.simuladoraposta.simulador_aposta.model.Aposta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApostaResult {
    private Aposta aposta;
    private List<SugestaoInvestimentoDTO> sugestoesInvestimento;
}