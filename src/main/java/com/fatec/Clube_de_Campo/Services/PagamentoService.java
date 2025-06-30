package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.Associado;
import com.fatec.Clube_de_Campo.Entities.Cobranca;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.CobrancaResponseDTO;
import com.fatec.Clube_de_Campo.Repositories.AssociadoRepository;
import com.fatec.Clube_de_Campo.Repositories.CobrancaRepository;
import com.fatec.Clube_de_Campo.Repositories.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PagamentoService {
    @Autowired
    private CobrancaService cobrancaService;
    @Autowired
    private AssociadoRepository associadoRepository;
    @Autowired
    private CobrancaRepository cobrancaRepository;

    public CobrancaResponseDTO processarPagamento(Long associadoId, LocalDate dataPagamento) {
        Associado associado = associadoRepository.findById(associadoId)
                .orElseThrow(() -> new RuntimeException("Associado não encontrado"));

        Cobranca ultimaCobranca = cobrancaRepository.findTopByAssociadoIdOrderByDataVencimentoDesc(associadoId)
                .orElseThrow(() -> new RuntimeException("Nenhuma cobrança encontrada para o associado"));

        if (ultimaCobranca.isPago()) {
            throw new RuntimeException("A última cobrança já está paga");
        }
        if (dataPagamento == null) {
            throw new RuntimeException("Data de pagamento não fornecida");
        }

        boolean ehPagamentoAtrasado = ehPagamentoAtrasado(ultimaCobranca, dataPagamento);
        BigDecimal multiplicadorMulta = ehPagamentoAtrasado ? BigDecimal.valueOf(1.05) : BigDecimal.ONE;

        ultimaCobranca.setDataPagamento(dataPagamento);
        ultimaCobranca.setPago(true);
        cobrancaRepository.save(ultimaCobranca);

        Cobranca nextCobranca = cobrancaService.createProximaCobranca(ultimaCobranca, associado, multiplicadorMulta);
        cobrancaRepository.save(nextCobranca);

        return new CobrancaResponseDTO(
                ultimaCobranca.getId(),
                ultimaCobranca.getMes(),
                ultimaCobranca.getAno(),
                ultimaCobranca.getValor(),
                ultimaCobranca.getDataVencimento(),
                ultimaCobranca.getDataPagamento(),
                ultimaCobranca.isPago(),
                associadoId
        );
    }

    private boolean ehPagamentoAtrasado(Cobranca cobranca, LocalDate dataPagamento) {
        return dataPagamento.isAfter(cobranca.getDataVencimento());
    }
}
