package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.Atividade;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.AtividadeRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.AtividadeResponseDTO;
import com.fatec.Clube_de_Campo.Repositories.AgendaAtividadeRepository;
import com.fatec.Clube_de_Campo.Repositories.AtividadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtividadeService {
    @Autowired
    private AtividadeRepository atividadeRepository;
    @Autowired
    private AgendaAtividadeRepository agendaAtividadeRepository;

    @Transactional
    public AtividadeResponseDTO insere(AtividadeRequestDTO request) {
        validarRequest(request);
        if (atividadeRepository.findByNome(request.nome()).isPresent()) {
            throw new IllegalArgumentException("Atividade com nome '" + request.nome() + "' já existe");
        }
        Atividade atividade = new Atividade();
        atividade.setNome(request.nome());
        atividade.setMaxGruposPorDia(request.maxGruposPorDia());
        atividade.setMaxParticipantes(request.maxParticipantes());
        atividade.setDuracaoMinutos(request.duracaoMinutos());
        Atividade salva = atividadeRepository.save(atividade);
        return new AtividadeResponseDTO(
                salva.getId(), salva.getNome(), salva.getMaxGruposPorDia(),
                salva.getMaxParticipantes(), salva.getDuracaoMinutos()
        );
    }

    public List<AtividadeResponseDTO> listarTodas() {
        return atividadeRepository.findAll().stream()
                .map(atividade -> new AtividadeResponseDTO(
                        atividade.getId(), atividade.getNome(), atividade.getMaxGruposPorDia(),
                        atividade.getMaxParticipantes(), atividade.getDuracaoMinutos()
                ))
                .collect(Collectors.toList());
    }

    public AtividadeResponseDTO buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        Atividade atividade = atividadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada com ID: " + id));
        return new AtividadeResponseDTO(
                atividade.getId(), atividade.getNome(), atividade.getMaxGruposPorDia(),
                atividade.getMaxParticipantes(), atividade.getDuracaoMinutos()
        );
    }

    @Transactional
    public AtividadeResponseDTO atualizar(Long id, AtividadeRequestDTO request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        validarRequest(request);
        Atividade existente = atividadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada com ID: " + id));
        if (!existente.getNome().equalsIgnoreCase(request.nome()) &&
                atividadeRepository.findByNome(request.nome()).isPresent()) {
            throw new IllegalArgumentException("Atividade com nome '" + request.nome() + "' já existe");
        }
        existente.setNome(request.nome());
        existente.setMaxGruposPorDia(request.maxGruposPorDia());
        existente.setMaxParticipantes(request.maxParticipantes());
        existente.setDuracaoMinutos(request.duracaoMinutos());
        Atividade atualizada = atividadeRepository.save(existente);
        return new AtividadeResponseDTO(
                atualizada.getId(), atualizada.getNome(), atualizada.getMaxGruposPorDia(),
                atualizada.getMaxParticipantes(), atualizada.getDuracaoMinutos()
        );
    }

    @Transactional
    public void deletar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        if (!atividadeRepository.existsById(id)) {
            throw new RuntimeException("Atividade não encontrada com ID: " + id);
        }
        if (agendaAtividadeRepository.existsByAtividadeId(id)) {
            throw new IllegalStateException("Atividade possui agendas associadas e não pode ser deletada");
        }
        atividadeRepository.deleteById(id);
    }

    private void validarRequest(AtividadeRequestDTO request) {
        if (request.nome() == null || request.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da atividade é obrigatório");
        }
        if (request.maxGruposPorDia() <= 0) {
            throw new IllegalArgumentException("Máximo de grupos por dia deve ser maior que zero");
        }
        if (request.maxParticipantes() <= 0) {
            throw new IllegalArgumentException("Máximo de participantes deve ser maior que zero");
        }
        if (request.duracaoMinutos() <= 0) {
            throw new IllegalArgumentException("Duração em minutos deve ser maior que zero");
        }
    }
}
