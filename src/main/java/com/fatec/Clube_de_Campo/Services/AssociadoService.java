package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.*;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.AssociadoRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.ContatoRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.AssociadoResponseDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.ContatoResponseDTO;
import com.fatec.Clube_de_Campo.Repositories.AssociadoRepository;
import com.fatec.Clube_de_Campo.Repositories.ContatoRepository;
import com.fatec.Clube_de_Campo.Repositories.EnderecoRepository;
import com.fatec.Clube_de_Campo.Repositories.TipoAssociadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssociadoService {

    @Autowired
    private AssociadoRepository associadoRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private ContatoRepository contatoRepository;
    @Autowired
    private CobrancaService cobrancaService;
    @Autowired
    private TipoAssociadoRepository tipoAssociadoRepository;

    public AssociadoResponseDTO insere(AssociadoRequestDTO request) {
        validarRequest(request);
        if (associadoRepository.findByCpf(request.cpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já registrado: " + request.cpf());
        }
        Endereco endereco = enderecoRepository.findById(request.enderecoId())
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado com ID: " + request.enderecoId()));
        TipoAssociado tipoAssociado = tipoAssociadoRepository.findById(request.tipoAssociadoId())
                .orElseThrow(() -> new RuntimeException("Tipo de associado não encontrado com ID: " + request.tipoAssociadoId()));
        List<Contato> contatos = new ArrayList<>();
        if (request.contatos() != null && !request.contatos().isEmpty()) {
            for (ContatoRequestDTO contatoDTO : request.contatos()) {
                validarContato(contatoDTO);
                Contato contato = new Contato();
                contato.setTelefone(contatoDTO.telefone());
                contato.setEmail(contatoDTO.email());
                contatos.add(contato);
            }
        }
        Associado associado = new Associado();
        associado.setNome(request.nome());
        associado.setRg(request.rg());
        associado.setCpf(request.cpf());
        associado.setEndereco(endereco);
        associado.setContatos(contatos);
        associado.setTipoAssociado(tipoAssociado);
        associado.setDataCadastrado(LocalDate.now());
        contatos.forEach(contato -> contato.setAssociado(associado));
        Associado salvo = associadoRepository.save(associado);
        BigDecimal valorInicial = salvo.getTipoAssociado().getValor();
        LocalDate dataVencimento = LocalDate.now().plusDays(30);
        cobrancaService.createCobranca(salvo, valorInicial, dataVencimento);
        return new AssociadoResponseDTO(
                salvo.getId(), salvo.getNome(), salvo.getRg(), salvo.getCpf(),
                salvo.getEndereco().getId(),
                salvo.getContatos().stream().map(contato -> new ContatoResponseDTO(contato.getId(), contato.getTelefone(), contato.getEmail())).collect(Collectors.toList()),
                salvo.getTipoAssociado().getId(),
                salvo.getDataCadastrado()
        );
    }

    public List<AssociadoResponseDTO> listarTodos() {
        return associadoRepository.findAll().stream()
                .map(associado -> new AssociadoResponseDTO(
                        associado.getId(), associado.getNome(), associado.getRg(), associado.getCpf(),
                        associado.getEndereco().getId(),
                        associado.getContatos().stream().map(contato -> new ContatoResponseDTO(contato.getId(), contato.getTelefone(), contato.getEmail())).collect(Collectors.toList()),
                        associado.getTipoAssociado().getId(),
                        associado.getDataCadastrado()
                ))
                .collect(Collectors.toList());
    }

    public AssociadoResponseDTO buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        Associado associado = associadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Associado não encontrado com ID: " + id));
        return new AssociadoResponseDTO(
                associado.getId(), associado.getNome(), associado.getRg(), associado.getCpf(),
                associado.getEndereco().getId(),
                associado.getContatos().stream().map(contato -> new ContatoResponseDTO(contato.getId(), contato.getTelefone(), contato.getEmail())).collect(Collectors.toList()),
                associado.getTipoAssociado().getId(),
                associado.getDataCadastrado()
        );
    }

    @Transactional
    public AssociadoResponseDTO atualizar(Long id, AssociadoRequestDTO request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        validarRequest(request);
        Associado existente = associadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Associado não encontrado com ID: " + id));
        if (!existente.getCpf().equals(request.cpf()) &&
                associadoRepository.findByCpf(request.cpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já registrado: " + request.cpf());
        }
        Endereco endereco = enderecoRepository.findById(request.enderecoId())
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado com ID: " + request.enderecoId()));
        TipoAssociado tipoAssociado = tipoAssociadoRepository.findById(request.tipoAssociadoId())
                .orElseThrow(() -> new RuntimeException("Tipo de associado não encontrado com ID: " + request.tipoAssociadoId()));
        existente.getContatos().clear();
        if (request.contatos() != null && !request.contatos().isEmpty()) {
            for (ContatoRequestDTO contatoDTO : request.contatos()) {
                validarContato(contatoDTO);
                Contato contato = new Contato();
                contato.setTelefone(contatoDTO.telefone());
                contato.setEmail(contatoDTO.email());
                contato.setAssociado(existente);
                existente.getContatos().add(contato);
            }
        }
        existente.setNome(request.nome());
        existente.setRg(request.rg());
        existente.setCpf(request.cpf());
        existente.setEndereco(endereco);
        existente.setTipoAssociado(tipoAssociado);
        Associado atualizado = associadoRepository.save(existente);
        return new AssociadoResponseDTO(
                atualizado.getId(), atualizado.getNome(), atualizado.getRg(), atualizado.getCpf(),
                atualizado.getEndereco().getId(),
                atualizado.getContatos().stream()
                        .map(contato -> new ContatoResponseDTO(contato.getId(), contato.getTelefone(), contato.getEmail()))
                        .collect(Collectors.toList()),
                atualizado.getTipoAssociado().getId(),
                atualizado.getDataCadastrado()
        );
    }

    @Transactional
    public void excluir(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        if (!associadoRepository.existsById(id)) {
            throw new RuntimeException("Associado não encontrado com ID: " + id);
        }
        associadoRepository.deleteById(id);
    }

    private void validarRequest(AssociadoRequestDTO request) {
        if (request.nome() == null || request.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do associado é obrigatório");
        }
        if (request.rg() == null || request.rg().trim().isEmpty()) {
            throw new IllegalArgumentException("RG do associado é obrigatório");
        }
        if (request.cpf() == null || request.cpf().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do associado é obrigatório");
        }
        if (request.enderecoId() == null || request.enderecoId() <= 0) {
            throw new IllegalArgumentException("ID do endereço é inválido");
        }
        if (request.tipoAssociadoId() == null || request.tipoAssociadoId() <= 0) {
            throw new IllegalArgumentException("ID do tipo de associado é inválido");
        }
    }

    private void validarContato(ContatoRequestDTO contato) {
        if (contato.telefone() == null || contato.telefone().trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone do contato é obrigatório");
        }
        if (contato.email() == null || contato.email().trim().isEmpty()) {
            throw new IllegalArgumentException("Email do contato é obrigatório");
        }
        if (!contato.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Formato de email inválido: " + contato.email());
        }
    }
}

