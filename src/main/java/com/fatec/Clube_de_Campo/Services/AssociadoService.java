package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.*;
import com.fatec.Clube_de_Campo.Repositories.AssociadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssociadoService {
    @Autowired
    private AssociadoRepository associadoRepository;

    public Associado insere(Associado associado) {
        if (associadoRepository.findByCpf(associado.getCpf()).isPresent()) {
            throw new IllegalArgumentException("Documento do Associado já está registrado");
        }
        return associadoRepository.save(associado);
    }

    public Associado buscaAssociadoPorId(Long id) {
        return associadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Associado não encontrado"));
    }

    public List<Associado> listaAssociados() {
        return associadoRepository.findAll();
    }
}
