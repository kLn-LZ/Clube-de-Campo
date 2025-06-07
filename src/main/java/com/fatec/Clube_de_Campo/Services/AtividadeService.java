package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.Atividade;
import com.fatec.Clube_de_Campo.Repositories.AtividadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AtividadeService {
    @Autowired
    private AtividadeRepository atividadeRepository;

    public Atividade insere(Atividade atividade) {
        if (atividadeRepository.findByNome(atividade.getNome()).isPresent()) {
            throw new IllegalArgumentException("Atividade já existe");
        }
        return atividadeRepository.save(atividade);
    }

    public List<Atividade> listaAtividades() {
        return atividadeRepository.findAll();
    }

    public Atividade buscaAtividadePorId(Long id) {
        return atividadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
    }
}
