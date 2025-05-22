package com.globalsolution.simuladoraposta.simulador_aposta.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "APOSTA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "valor_apostado", nullable = false)
    private BigDecimal valorApostado;

    @Column(nullable = false)
    private String resultado;

    @Column(name = "valor_ganho_perda", nullable = false)
    private BigDecimal valorGanhoPerda;

    @Column(name = "saldo_apos_aposta", nullable = false)
    private BigDecimal saldoAposAposta;

    @Column(name = "data_aposta", nullable = false)
    private LocalDateTime dataAposta;

    @Column(name = "tipo_aposta", nullable = false)
    private String tipoAposta;
}