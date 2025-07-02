package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.Associado;
import com.fatec.Clube_de_Campo.Entities.Cobranca;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.PagamentoRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.CobrancaResponseDTO;
import com.fatec.Clube_de_Campo.Repositories.AssociadoRepository;
import com.fatec.Clube_de_Campo.Repositories.CobrancaRepository;
import com.fatec.Clube_de_Campo.Repositories.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagamentoService {
    @Autowired
    private CobrancaRepository cobrancaRepository;
    @Autowired
    private AssociadoRepository associadoRepository;

    @Transactional
    public CobrancaResponseDTO processarPagamento(PagamentoRequestDTO request) {
        validarRequest(request);
        Associado associado = associadoRepository.findById(request.associadoId())
                .orElseThrow(() -> new RuntimeException("Associado não encontrado com ID: " + request.associadoId()));
        LocalDate dataPagamento;
        try {
            dataPagamento = LocalDate.parse(request.dataPagamento(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data inválido. Use YYYY-MM-DD");
        }
        Cobranca cobranca = cobrancaRepository.findFirstByAssociadoIdAndPagoFalseOrderByDataVencimentoAsc(request.associadoId())
                .orElseThrow(() -> new RuntimeException("Nenhuma cobrança pendente encontrada para o associado"));
        cobranca.setDataPagamento(dataPagamento);
        cobranca.setPago(true);

        BigDecimal multa = null;
        if (dataPagamento.isAfter(cobranca.getDataVencimento())) {
            multa = cobranca.getValor().multiply(new BigDecimal("0.1"));
            cobranca.setValor(cobranca.getValor().add(multa));
        }
        cobrancaRepository.save(cobranca);
        YearMonth proximoMes = YearMonth.of(cobranca.getAno(), cobranca.getMes()).plusMonths(1);
        if (!cobrancaRepository.existsByAssociadoIdAndMesAndAno(request.associadoId(), proximoMes.getMonthValue(), proximoMes.getYear())) {
            BigDecimal valorBase = associado.getTipoAssociado().getValor().add(multa);
            LocalDate proximaDtVencimento = proximoMes.atDay(10);
            Cobranca novaCobranca = new Cobranca(
                    proximoMes.getMonthValue(), proximoMes.getYear(), valorBase, proximaDtVencimento, associado
            );
            cobrancaRepository.save(novaCobranca);
            return new CobrancaResponseDTO(
                    novaCobranca.getId(), novaCobranca.getMes(), novaCobranca.getAno(),
                    novaCobranca.getValor(), novaCobranca.getDataVencimento().toString(),
                    novaCobranca.getDataPagamento() != null ? novaCobranca.getDataPagamento().toString() : null,
                    novaCobranca.isPago(), request.associadoId()
            );
        }
        return new CobrancaResponseDTO(
                cobranca.getId(), cobranca.getMes(), cobranca.getAno(),
                cobranca.getValor(), cobranca.getDataVencimento().toString(),
                cobranca.getDataPagamento().toString(), cobranca.isPago(), request.associadoId()
        );
    }

    public List<CobrancaResponseDTO> listarTodas() {
        return cobrancaRepository.findAll().stream()
                .map(cobranca -> new CobrancaResponseDTO(
                        cobranca.getId(), cobranca.getMes(), cobranca.getAno(),
                        cobranca.getValor(), cobranca.getDataVencimento().toString(),
                        cobranca.getDataPagamento() != null ? cobranca.getDataPagamento().toString() : null,
                        cobranca.isPago(), cobranca.getAssociado().getId()
                ))
                .collect(Collectors.toList());
    }

    public CobrancaResponseDTO buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        Cobranca cobranca = cobrancaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cobrança não encontrada com ID: " + id));
        return new CobrancaResponseDTO(
                cobranca.getId(), cobranca.getMes(), cobranca.getAno(),
                cobranca.getValor(), cobranca.getDataVencimento().toString(),
                cobranca.getDataPagamento() != null ? cobranca.getDataPagamento().toString() : null,
                cobranca.isPago(), cobranca.getAssociado().getId()
        );
    }

    private void validarRequest(PagamentoRequestDTO request) {
        if (request.associadoId() == null || request.associadoId() <= 0) {
            throw new IllegalArgumentException("ID do associado inválido");
        }
        if (request.dataPagamento() == null || request.dataPagamento().trim().isEmpty()) {
            throw new IllegalArgumentException("Data de pagamento é obrigatória");
        }
    }
}
