package com.fatec.Clube_de_Campo.Entities.DTOs.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CobrancaResponseDTO(Long id, int mes, int ano, BigDecimal valor, String dataVencimento, String dataPagamento, boolean pago, Long associadoId) {}
