package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.*;
import com.fatec.Clube_de_Campo.Repositories.AssociadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AssociadoService {
    @Autowired
    private AssociadoRepository associadoRepository;

    @Autowired
    private CobrancaService cobrancaService;

    public Associado insere(Associado associado) {
        if (associadoRepository.findByCpf(associado.getCpf()).isPresent()) {
            throw new IllegalArgumentException("Documento do Associado já está registrado");
        }

        Associado associadoCriado = associadoRepository.save(associado);
        BigDecimal valorInicial = associado.getTipoAssociado().getValor();
        LocalDate dataVencimento = LocalDate.now().plusDays(30);

        Cobranca cobranca = cobrancaService.createCobranca(associadoCriado, valorInicial, dataVencimento);

        return associadoCriado;
    }

    public Associado buscaAssociadoPorId(Long id) {
        return associadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Associado não encontrado"));
    }

    public List<Associado> listaAssociados() {
        return associadoRepository.findAll();
    }

    public void excluiPorId(Long id) {
        if (associadoRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Associado não encontrado");
        }

        associadoRepository.deleteById(id);
    }
}
