package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.AgendaAtividade;
import com.fatec.Clube_de_Campo.Entities.Atividade;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.AgendaAtividadeRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.AgendaAtividadeResponseDTO;
import com.fatec.Clube_de_Campo.Repositories.AgendaAtividadeRepository;
import com.fatec.Clube_de_Campo.Repositories.AtividadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendaAtividadeService {
    @Autowired
    private AgendaAtividadeRepository agendaAtividadeRepository;
    @Autowired
    private AtividadeRepository atividadeRepository;

    @Transactional
    public AgendaAtividadeResponseDTO insere(AgendaAtividadeRequestDTO request) {
        validarRequest(request);
        Atividade atividade = atividadeRepository.findById(request.atividadeId())
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada com ID: " + request.atividadeId()));
        LocalDate data = LocalDate.parse(request.data(), DateTimeFormatter.ISO_LOCAL_DATE);
        if (agendaAtividadeRepository.findByAtividadeAndDataAndTime(atividade, data, request.time()).isPresent()) {
            throw new IllegalArgumentException("Já existe uma agenda para esta atividade, data e hora");
        }
        long atividadesAgendadas = agendaAtividadeRepository.countByAtividadeAndData(atividade, data);
        if (atividadesAgendadas >= atividade.getMaxGruposPorDia()) {
            throw new IllegalStateException("Número máximo de grupos atingido para esta data");
        }
        if (request.participantes() < 0) {
            throw new IllegalArgumentException("Número de participantes não pode ser negativo");
        }
        AgendaAtividade agendaAtividade = new AgendaAtividade();
        agendaAtividade.setAtividade(atividade);
        agendaAtividade.setData(data);
        agendaAtividade.setTime(request.time());
        agendaAtividade.setParticipantes(request.participantes());
        AgendaAtividade salva = agendaAtividadeRepository.save(agendaAtividade);
        return new AgendaAtividadeResponseDTO(
                salva.getId(), request.atividadeId(), request.data(), salva.getTime(), salva.getParticipantes()
        );
    }

    public List<AgendaAtividadeResponseDTO> listarTodas() {
        return agendaAtividadeRepository.findAll().stream()
                .map(agenda -> new AgendaAtividadeResponseDTO(
                        agenda.getId(), agenda.getAtividade().getId(),
                        agenda.getData().toString(), agenda.getTime(), agenda.getParticipantes()
                ))
                .collect(Collectors.toList());
    }

    public AgendaAtividadeResponseDTO buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        AgendaAtividade agendaAtividade = agendaAtividadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agenda de atividade não encontrada com ID: " + id));
        return new AgendaAtividadeResponseDTO(
                agendaAtividade.getId(), agendaAtividade.getAtividade().getId(),
                agendaAtividade.getData().toString(), agendaAtividade.getTime(), agendaAtividade.getParticipantes()
        );
    }

    public List<AgendaAtividadeResponseDTO> buscarAtividadesDisponiveisPorIdEData(Long atividadeId, LocalDate data) {
        if (atividadeId == null || atividadeId <= 0) {
            throw new IllegalArgumentException("ID da atividade inválido");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data é obrigatória");
        }
        Atividade atividade = atividadeRepository.findById(atividadeId)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada com ID: " + atividadeId));
        return agendaAtividadeRepository.findByAtividadeAndData(atividade, data).stream()
                .filter(agenda -> agenda.getParticipantes() < atividade.getMaxParticipantes())
                .map(agenda -> new AgendaAtividadeResponseDTO(
                        agenda.getId(), agenda.getAtividade().getId(),
                        agenda.getData().toString(), agenda.getTime(), agenda.getParticipantes()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public AgendaAtividadeResponseDTO atualizar(Long id, AgendaAtividadeRequestDTO request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        validarRequest(request);
        AgendaAtividade existente = agendaAtividadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agenda de atividade não encontrada com ID: " + id));
        Atividade atividade = atividadeRepository.findById(request.atividadeId())
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada com ID: " + request.atividadeId()));
        LocalDate data = LocalDate.parse(request.data(), DateTimeFormatter.ISO_LOCAL_DATE);
        if (!existente.getAtividade().getId().equals(request.atividadeId()) ||
                !existente.getData().equals(data) ||
                !existente.getTime().equals(request.time())) {
            if (agendaAtividadeRepository.findByAtividadeAndDataAndTime(atividade, data, request.time()).isPresent()) {
                throw new IllegalArgumentException("Já existe uma agenda para esta atividade, data e hora");
            }
        }
        long atividadesAgendadas = agendaAtividadeRepository.countByAtividadeAndDataExcludingId(atividade, data, id);
        if (atividadesAgendadas >= atividade.getMaxGruposPorDia()) {
            throw new IllegalStateException("Número máximo de grupos atingido para esta data");
        }
        if (request.participantes() < 0) {
            throw new IllegalArgumentException("Número de participantes não pode ser negativo");
        }
        existente.setAtividade(atividade);
        existente.setData(data);
        existente.setTime(request.time());
        existente.setParticipantes(request.participantes());
        AgendaAtividade atualizada = agendaAtividadeRepository.save(existente);
        return new AgendaAtividadeResponseDTO(
                atualizada.getId(), request.atividadeId(), request.data(), atualizada.getTime(), atualizada.getParticipantes()
        );
    }

    @Transactional
    public void deletar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        if (!agendaAtividadeRepository.existsById(id)) {
            throw new RuntimeException("Agenda de atividade não encontrada com ID: " + id);
        }
        agendaAtividadeRepository.deleteById(id);
    }

    private void validarRequest(AgendaAtividadeRequestDTO request) {
        if (request.atividadeId() == null || request.atividadeId() <= 0) {
            throw new IllegalArgumentException("ID da atividade inválido");
        }
        if (request.data() == null || request.data().trim().isEmpty()) {
            throw new IllegalArgumentException("Data é obrigatória");
        }
        try {
            LocalDate.parse(request.data(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data inválido. Use YYYY-MM-DD");
        }
        if (request.time() == null || request.time().trim().isEmpty()) {
            throw new IllegalArgumentException("Hora é obrigatória");
        }
        try {
            LocalTime.parse(request.time(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de hora inválido. Use HH:mm");
        }
    }
}
