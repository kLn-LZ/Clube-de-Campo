package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.Associado;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.DependenteRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.DependenteResponseDTO;
import com.fatec.Clube_de_Campo.Entities.Dependente;
import com.fatec.Clube_de_Campo.Repositories.AssociadoRepository;
import com.fatec.Clube_de_Campo.Repositories.DependenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DependenteService {
    @Autowired
    private DependenteRepository dependenteRepository;
    @Autowired
    private AssociadoRepository associadoRepository;

    @Transactional
    public DependenteResponseDTO registrarDependente(DependenteRequestDTO request) {
        validarRequest(request);
        if (dependenteRepository.findByRg(request.rg()).isPresent()) {
            throw new IllegalArgumentException("Dependente com RG '" + request.rg() + "' já existe");
        }
        Associado associado = associadoRepository.findById(request.associadoId())
                .orElseThrow(() -> new RuntimeException("Associado não encontrado com ID: " + request.associadoId()));
        Dependente dependente = new Dependente();
        dependente.setNome(request.nome());
        dependente.setRg(request.rg());
        dependente.setAssociado(associado);
        Dependente salvo = dependenteRepository.save(dependente);
        return new DependenteResponseDTO(salvo.getId(), salvo.getNome(), salvo.getRg(), request.associadoId());
    }

    public List<DependenteResponseDTO> listarTodos() {
        return dependenteRepository.findAll().stream()
                .map(dep -> new DependenteResponseDTO(dep.getId(), dep.getNome(), dep.getRg(), dep.getAssociado().getId()))
                .collect(Collectors.toList());
    }

    public DependenteResponseDTO buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        Dependente dependente = dependenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dependente não encontrado com ID: " + id));
        return new DependenteResponseDTO(
                dependente.getId(),
                dependente.getNome(),
                dependente.getRg(),
                dependente.getAssociado().getId()
        );
    }

    @Transactional
    public DependenteResponseDTO atualizar(Long id, DependenteRequestDTO request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        validarRequest(request);
        Dependente existente = dependenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dependente não encontrado com ID: " + id));
        if (!existente.getRg().equals(request.rg()) &&
                dependenteRepository.findByRg(request.rg()).isPresent()) {
            throw new IllegalArgumentException("Dependente com RG '" + request.rg() + "' já existe");
        }
        Associado associado = associadoRepository.findById(request.associadoId())
                .orElseThrow(() -> new RuntimeException("Associado não encontrado com ID: " + request.associadoId()));
        existente.setNome(request.nome());
        existente.setRg(request.rg());
        existente.setAssociado(associado);
        Dependente atualizado = dependenteRepository.save(existente);
        return new DependenteResponseDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getRg(),
                request.associadoId()
        );
    }

    @Transactional
    public void deletar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        if (!dependenteRepository.existsById(id)) {
            throw new RuntimeException("Dependente não encontrado com ID: " + id);
        }
        dependenteRepository.deleteById(id);
    }

    private void validarRequest(DependenteRequestDTO request) {
        if (request.nome() == null || request.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do dependente é obrigatório e não pode ser vazio");
        }
        if (request.rg() == null || request.rg().trim().isEmpty()) {
            throw new IllegalArgumentException("RG do dependente é obrigatório e não pode ser vazio");
        }
        if (request.associadoId() == null || request.associadoId() <= 0) {
            throw new IllegalArgumentException("ID do associado é inválido");
        }
    }
}
