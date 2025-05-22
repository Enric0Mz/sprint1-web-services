package com.globalsolution.simuladoraposta.simulador_aposta.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "INVESTIMENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Investimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "valor_original", nullable = false)
    private BigDecimal valorOriginal;

    @Column(name = "tipo_sugestao", nullable = false)
    private String tipoSugestao;

    @Column(name = "rendimento_1mes", nullable = false)
    private BigDecimal rendimento1Mes;

    @Column(name = "rendimento_6meses", nullable = false)
    private BigDecimal rendimento6Meses;

    @Column(name = "rendimento_1ano", nullable = false)
    private BigDecimal rendimento1Ano;

    @Column(name = "rendimento_5anos", nullable = false)
    private BigDecimal rendimento5Anos;

    @Column(name = "data_simulacao", nullable = false)
    private LocalDateTime dataSimulacao;
}