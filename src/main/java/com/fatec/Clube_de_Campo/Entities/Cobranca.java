package com.fatec.Clube_de_Campo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cobranca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int mes;
    private int ano;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private boolean pago;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "associado_id", nullable = false)
    private Associado associado;

    public Cobranca (int mes, int ano, BigDecimal valor, LocalDate dataVencimento, Associado associado) {
        this.mes = mes;
        this.ano = ano;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.associado = associado;

        this.pago = false;
    }
}
