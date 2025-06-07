package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.Associado;
import com.fatec.Clube_de_Campo.Entities.Pagamento;
import com.fatec.Clube_de_Campo.Repositories.AssociadoRepository;
import com.fatec.Clube_de_Campo.Repositories.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PagamentoService {
    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private AssociadoRepository associadoRepository;

    public Pagamento insere(Pagamento pagamento) {
        Associado associado = associadoRepository.findById(pagamento.getAssociado().getId())
                .orElseThrow(() -> new RuntimeException("Associado não encontrado"));
        if (ehPagamentoAtrasado(pagamento)) {
            pagamento.setValor(pagamento.getValor().multiply(BigDecimal.valueOf(1.05))); // 5% penalty
        }
        pagamento.setAssociado(associado);
        return pagamentoRepository.save(pagamento);
    }

    private boolean ehPagamentoAtrasado(Pagamento pagamento) {
        return pagamento.getData().isBefore(LocalDate.now());
    }

    public int calculaMesesNaoPagos(Long associadoId) {
        return pagamentoRepository.findLatestPagamentoByAssociadoId(associadoId)
                .map(pagamento -> {
                    long meses = java.time.temporal.ChronoUnit.MONTHS.between(pagamento.getData(), LocalDate.now());
                    return (int) Math.max(0, meses);
                })
                .orElse(Integer.MAX_VALUE);
    }
}
