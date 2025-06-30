package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.Associado;
import com.fatec.Clube_de_Campo.Entities.Cobranca;
import com.fatec.Clube_de_Campo.Repositories.CobrancaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class CobrancaService {
    @Autowired
    private CobrancaRepository cobrancaRepository;

    public Cobranca createCobranca(Associado associado, BigDecimal valor, LocalDate vencimento) {
        Cobranca cobranca = new Cobranca(LocalDate.now().getMonthValue(),
                LocalDate.now().getYear(), valor, vencimento, associado);
        return cobrancaRepository.save(cobranca);
    }

    public Cobranca createProximaCobranca(Cobranca ultimaCobranca, Associado associado, BigDecimal multiplicadorMulta) {
        YearMonth proximoMes = YearMonth.of(ultimaCobranca.getAno(), ultimaCobranca.getMes()).plusMonths(1);
        LocalDate proximaDtVencimento = proximoMes.atDay(10); //
        BigDecimal valorBase = ultimaCobranca.getValor();
        BigDecimal novoValor = valorBase.multiply(multiplicadorMulta);

        return new Cobranca(
                proximoMes.getMonthValue(),
                proximoMes.getYear(),
                novoValor,
                proximaDtVencimento,
                associado
        );
    }

    public int calculaMesesNaoPagos(Long associadoId) {
        Cobranca ultimaCobranca = cobrancaRepository.findTopByAssociadoIdOrderByDataVencimentoDesc(associadoId)
                .orElseThrow(() -> new RuntimeException("Nenhuma cobrança encontrada"));

        if (ultimaCobranca.isPago()) {
            return 0;
        }

        YearMonth mesUltimaCobranca = YearMonth.of(ultimaCobranca.getAno(), ultimaCobranca.getMes());
        YearMonth mesAtual = YearMonth.now();
        return (int) mesUltimaCobranca.until(mesAtual, java.time.temporal.ChronoUnit.MONTHS);
    }
}
