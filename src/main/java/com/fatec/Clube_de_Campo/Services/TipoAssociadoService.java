package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.DTOs.request.TipoAssociadoRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.TipoAssociadoResponseDTO;
import com.fatec.Clube_de_Campo.Entities.TipoAssociado;
import com.fatec.Clube_de_Campo.Repositories.AssociadoRepository;
import com.fatec.Clube_de_Campo.Repositories.TipoAssociadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoAssociadoService {

    @Autowired
    private TipoAssociadoRepository tipoAssociadoRepository;
    @Autowired
    private AssociadoRepository associadoRepository;

    @Transactional
    public TipoAssociadoResponseDTO insere(TipoAssociadoRequestDTO request) {
        validarRequest(request);
        if (tipoAssociadoRepository.findByNome(request.nome()).isPresent()) {
            throw new IllegalArgumentException("Tipo de associado com nome '" + request.nome() + "' já existe");
        }
        TipoAssociado tipoAssociado = new TipoAssociado();
        tipoAssociado.setNome(request.nome());
        tipoAssociado.setValor(request.valor());
        TipoAssociado salvo = tipoAssociadoRepository.save(tipoAssociado);
        return new TipoAssociadoResponseDTO(salvo.getId(), salvo.getNome(), salvo.getValor());
    }

    public List<TipoAssociadoResponseDTO> listarTodos() {
        return tipoAssociadoRepository.findAll().stream()
                .map(tipo -> new TipoAssociadoResponseDTO(tipo.getId(), tipo.getNome(), tipo.getValor()))
                .collect(Collectors.toList());
    }

    public TipoAssociadoResponseDTO buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        TipoAssociado tipoAssociado = tipoAssociadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de associado não encontrado com ID: " + id));
        return new TipoAssociadoResponseDTO(tipoAssociado.getId(), tipoAssociado.getNome(), tipoAssociado.getValor());
    }

    @Transactional
    public TipoAssociadoResponseDTO atualizar(Long id, TipoAssociadoRequestDTO request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        validarRequest(request);
        TipoAssociado existente = tipoAssociadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de associado não encontrado com ID: " + id));
        if (!existente.getNome().equalsIgnoreCase(request.nome()) &&
                tipoAssociadoRepository.findByNome(request.nome()).isPresent()) {
            throw new IllegalArgumentException("Tipo de associado com nome '" + request.nome() + "' já existe");
        }
        existente.setNome(request.nome());
        existente.setValor(request.valor());
        TipoAssociado atualizado = tipoAssociadoRepository.save(existente);
        return new TipoAssociadoResponseDTO(atualizado.getId(), atualizado.getNome(), atualizado.getValor());
    }

    @Transactional
    public void deletar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        if (!tipoAssociadoRepository.existsById(id)) {
            throw new RuntimeException("Tipo de associado não encontrado com ID: " + id);
        }
        if (associadoRepository.existsByTipoAssociadoId(id)) {
            throw new IllegalStateException("Tipo de associado está vinculado a associados e não pode ser deletado");
        }
        tipoAssociadoRepository.deleteById(id);
    }

    private void validarRequest(TipoAssociadoRequestDTO request) {
        if (request.nome() == null || request.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do tipo de associado é obrigatório");
        }
        if (request.valor() == null || request.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do tipo de associado deve ser maior que zero");
        }
    }
}