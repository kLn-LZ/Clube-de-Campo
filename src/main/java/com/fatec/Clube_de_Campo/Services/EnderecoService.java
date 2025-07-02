package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.DTOs.request.EnderecoRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.EnderecoResponseDTO;
import com.fatec.Clube_de_Campo.Entities.Endereco;
import com.fatec.Clube_de_Campo.Repositories.AssociadoRepository;
import com.fatec.Clube_de_Campo.Repositories.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private AssociadoRepository associadoRepository;

    @Transactional
    public EnderecoResponseDTO insere(EnderecoRequestDTO request) {
        validarRequest(request);
        if (enderecoRepository.findByCep(request.cep()).isPresent()) {
            throw new IllegalArgumentException("CEP já registrado: " + request.cep());
        }
        Endereco endereco = new Endereco();
        endereco.setLogradouro(request.logradouro());
        endereco.setCep(request.cep());
        endereco.setBairro(request.bairro());
        endereco.setCidade(request.cidade());
        endereco.setEstado(request.estado());
        Endereco salvo = enderecoRepository.save(endereco);
        return new EnderecoResponseDTO(
                salvo.getId(), salvo.getLogradouro(), salvo.getCep(), salvo.getBairro(),
                salvo.getCidade(), salvo.getEstado()
        );
    }

    public List<EnderecoResponseDTO> listarTodos() {
        return enderecoRepository.findAll().stream()
                .map(endereco -> new EnderecoResponseDTO(
                        endereco.getId(), endereco.getLogradouro(), endereco.getCep(),
                        endereco.getBairro(), endereco.getCidade(), endereco.getEstado()
                ))
                .collect(Collectors.toList());
    }

    public EnderecoResponseDTO buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado com ID: " + id));
        return new EnderecoResponseDTO(
                endereco.getId(), endereco.getLogradouro(), endereco.getCep(),
                endereco.getBairro(), endereco.getCidade(), endereco.getEstado()
        );
    }

    @Transactional
    public EnderecoResponseDTO atualizar(Long id, EnderecoRequestDTO request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        validarRequest(request);
        Endereco existente = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado com ID: " + id));
        if (!existente.getCep().equals(request.cep()) &&
                enderecoRepository.findByCep(request.cep()).isPresent()) {
            throw new IllegalArgumentException("CEP já registrado: " + request.cep());
        }
        existente.setLogradouro(request.logradouro());
        existente.setCep(request.cep());
        existente.setBairro(request.bairro());
        existente.setCidade(request.cidade());
        existente.setEstado(request.estado());
        Endereco atualizado = enderecoRepository.save(existente);
        return new EnderecoResponseDTO(
                atualizado.getId(), atualizado.getLogradouro(), atualizado.getCep(),
                atualizado.getBairro(), atualizado.getCidade(), atualizado.getEstado()
        );
    }

    @Transactional
    public void deletar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        if (!enderecoRepository.existsById(id)) {
            throw new RuntimeException("Endereço não encontrado com ID: " + id);
        }
        if (associadoRepository.existsByEnderecoId(id)) {
            throw new IllegalStateException("Endereço está associado a um ou mais associados e não pode ser deletado");
        }
        enderecoRepository.deleteById(id);
    }

    private void validarRequest(EnderecoRequestDTO request) {
        if (request.logradouro() == null || request.logradouro().trim().isEmpty()) {
            throw new IllegalArgumentException("Logradouro é obrigatório");
        }
        if (request.cep() == null || !request.cep().matches("\\d{5}-\\d{3}")) {
            throw new IllegalArgumentException("CEP inválido. Use o formato 12345-678");
        }
        if (request.bairro() == null || request.bairro().trim().isEmpty()) {
            throw new IllegalArgumentException("Bairro é obrigatório");
        }
        if (request.cidade() == null || request.cidade().trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade é obrigatória");
        }
        if (request.estado() == null || request.estado().trim().isEmpty()) {
            throw new IllegalArgumentException("Estado é obrigatório");
        }
    }
}