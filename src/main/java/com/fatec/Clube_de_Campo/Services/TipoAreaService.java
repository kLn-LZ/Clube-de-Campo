package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.DTOs.request.TipoAreaRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.TipoAreaResponseDTO;
import com.fatec.Clube_de_Campo.Entities.TipoArea;
import com.fatec.Clube_de_Campo.Repositories.TipoAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoAreaService {
    @Autowired
    private TipoAreaRepository tipoAreaRepository;

    @Transactional
    public TipoAreaResponseDTO insere(TipoAreaRequestDTO request) {
        validarRequest(request);
        if (tipoAreaRepository.findByNome(request.nome()).isPresent()) {
            throw new IllegalArgumentException("Tipo de área com nome '" + request.nome() + "' já existe");
        }
        TipoArea tipoArea = new TipoArea();
        tipoArea.setNome(request.nome());
        TipoArea salvo = tipoAreaRepository.save(tipoArea);
        return new TipoAreaResponseDTO(salvo.getId(), salvo.getNome());
    }

    public List<TipoAreaResponseDTO> listarTodos() {
        return tipoAreaRepository.findAll().stream()
                .map(tipoArea -> new TipoAreaResponseDTO(tipoArea.getId(), tipoArea.getNome()))
                .collect(Collectors.toList());
    }

    public TipoAreaResponseDTO buscaPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        TipoArea tipoArea = tipoAreaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de área não encontrado com ID: " + id));
        return new TipoAreaResponseDTO(tipoArea.getId(), tipoArea.getNome());
    }

    @Transactional
    public TipoAreaResponseDTO atualiza(Long id, TipoAreaRequestDTO request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        validarRequest(request);
        TipoArea existente = tipoAreaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de área não encontrado com ID: " + id));
        if (!existente.getNome().equalsIgnoreCase(request.nome()) &&
                tipoAreaRepository.findByNome(request.nome()).isPresent()) {
            throw new IllegalArgumentException("Tipo de área com nome '" + request.nome() + "' já existe");
        }
        existente.setNome(request.nome());
        TipoArea atualizado = tipoAreaRepository.save(existente);
        return new TipoAreaResponseDTO(atualizado.getId(), atualizado.getNome());
    }

    @Transactional
    public void deleta(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        if (!tipoAreaRepository.existsById(id)) {
            throw new RuntimeException("Tipo de área não encontrado com ID: " + id);
        }
        tipoAreaRepository.deleteById(id);
    }

    private void validarRequest(TipoAreaRequestDTO request) {
        if (request.nome() == null || request.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do tipo de área é obrigatório e não pode ser vazio");
        }
    }
}
