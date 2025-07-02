package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.*;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.ReservaAreaRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.ReservaAtividadeRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.ReservaResponseDTO;
import com.fatec.Clube_de_Campo.Repositories.*;
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
public class ReservaService {
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private AgendaAtividadeRepository agendaAtividadeRepository;
    @Autowired
    private AcessoClubeService acessoClubeService;
    @Autowired
    private AtividadeRepository atividadeRepository;
    @Autowired
    private AssociadoRepository associadoRepository;

    @Transactional
    public ReservaResponseDTO insereReservaArea(ReservaAreaRequestDTO request) {
        validarAtributosEmComum(request.associadoId(), request.data(), request.hora());
        if (request.areaId() == null || request.areaId() <= 0) {
            throw new IllegalArgumentException("ID da área é obrigatório");
        }
        Associado associado = associadoRepository.findById(request.associadoId())
                .orElseThrow(() -> new RuntimeException("Associado não encontrado com ID: " + request.associadoId()));
        if (!acessoClubeService.podeAcessarTodasAreas(request.associadoId())) {
            throw new IllegalStateException("Associado não tem permissão para reservar áreas");
        }
        Area area = areaRepository.findById(request.areaId())
                .orElseThrow(() -> new RuntimeException("Área não encontrada com ID: " + request.areaId()));
        if (!area.isDisponivel()) {
            throw new IllegalStateException("Área não está disponível");
        }
        LocalDate data = LocalDate.parse(request.data(), DateTimeFormatter.ISO_LOCAL_DATE);
        if (reservaRepository.findByAreaAndDataAndHora(area, data, request.hora()).isPresent()) {
            throw new IllegalStateException("Área já reservada para esta data e hora");
        }
        if (reservaRepository.findByAssociadoAndDataAndHora(associado, data, request.hora()).isPresent()) {
            throw new IllegalStateException("Associado já possui uma reserva para esta data e hora");
        }
        Reserva reserva = new Reserva();
        reserva.setData(data);
        reserva.setHora(request.hora());
        reserva.setAssociado(associado);
        reserva.setArea(area);
        reserva.setAgendaAtividade(null);
        Reserva salva = reservaRepository.save(reserva);
        return new ReservaResponseDTO(salva.getId(), request.data(), salva.getHora(), request.associadoId(), request.areaId(), null);
    }

    @Transactional
    public ReservaResponseDTO insereReservaAtividade(ReservaAtividadeRequestDTO request) {
        validarAtributosEmComum(request.associadoId(), request.data(), request.hora());
        if (request.agendaAtividadeId() == null || request.agendaAtividadeId() <= 0) {
            throw new IllegalArgumentException("ID da agenda de atividade é obrigatório");
        }
        if (request.participantes() <= 0) {
            throw new IllegalArgumentException("Número de participantes deve ser maior que zero");
        }
        Associado associado = associadoRepository.findById(request.associadoId())
                .orElseThrow(() -> new RuntimeException("Associado não encontrado com ID: " + request.associadoId()));
        if (!acessoClubeService.podeAcessarAtividades(request.associadoId())) {
            throw new IllegalStateException("Associado não tem permissão para reservar atividades");
        }
        AgendaAtividade agendaAtividade = agendaAtividadeRepository.findById(request.agendaAtividadeId())
                .orElseThrow(() -> new RuntimeException("Agenda de atividade não encontrada com ID: " + request.agendaAtividadeId()));
        Atividade atividade = agendaAtividade.getAtividade();
        int participantesTotais = agendaAtividade.getParticipantes() + request.participantes();
        if (participantesTotais > atividade.getMaxParticipantes()) {
            throw new IllegalStateException("Limite de participantes excedido para a atividade");
        }
        LocalDate data = LocalDate.parse(request.data(), DateTimeFormatter.ISO_LOCAL_DATE);
        if (!agendaAtividade.getData().equals(data) || !agendaAtividade.getTime().equals(request.hora())) {
            throw new IllegalArgumentException("Data e hora devem corresponder à agenda de atividade");
        }
        if (reservaRepository.findByAssociadoAndDataAndHora(associado, data, request.hora()).isPresent()) {
            throw new IllegalStateException("Associado já possui uma reserva para esta data e hora");
        }
        agendaAtividade.setParticipantes(participantesTotais);
        agendaAtividadeRepository.save(agendaAtividade);
        Reserva reserva = new Reserva();
        reserva.setData(data);
        reserva.setHora(request.hora());
        reserva.setAssociado(associado);
        reserva.setAgendaAtividade(agendaAtividade);
        reserva.setArea(null);
        Reserva salva = reservaRepository.save(reserva);
        return new ReservaResponseDTO(salva.getId(), request.data(), salva.getHora(), request.associadoId(), null, request.agendaAtividadeId());
    }

    public List<ReservaResponseDTO> listarTodas() {
        return reservaRepository.findAll().stream()
                .map(reserva -> new ReservaResponseDTO(
                        reserva.getId(),
                        reserva.getData().toString(),
                        reserva.getHora(),
                        reserva.getAssociado().getId(),
                        reserva.getArea() != null ? reserva.getArea().getId() : null,
                        reserva.getAgendaAtividade() != null ? reserva.getAgendaAtividade().getId() : null
                ))
                .collect(Collectors.toList());
    }

    public ReservaResponseDTO buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada com ID: " + id));
        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getData().toString(),
                reserva.getHora(),
                reserva.getAssociado().getId(),
                reserva.getArea() != null ? reserva.getArea().getId() : null,
                reserva.getAgendaAtividade() != null ? reserva.getAgendaAtividade().getId() : null
        );
    }

    @Transactional
    public ReservaResponseDTO atualizarReservaArea(Long id, ReservaAreaRequestDTO request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        validarAtributosEmComum(request.associadoId(), request.data(), request.hora());
        if (request.areaId() == null || request.areaId() <= 0) {
            throw new IllegalArgumentException("ID da área é obrigatório");
        }
        Reserva existente = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada com ID: " + id));
        if (existente.getAgendaAtividade() != null) {
            throw new IllegalArgumentException("Reserva é do tipo atividade, use o endpoint correspondente");
        }
        Associado associado = associadoRepository.findById(request.associadoId())
                .orElseThrow(() -> new RuntimeException("Associado não encontrado com ID: " + request.associadoId()));
        if (!acessoClubeService.podeAcessarTodasAreas(request.associadoId())) {
            throw new IllegalStateException("Associado não tem permissão para reservar áreas");
        }
        Area area = areaRepository.findById(request.areaId())
                .orElseThrow(() -> new RuntimeException("Área não encontrada com ID: " + request.areaId()));
        if (!area.isDisponivel()) {
            throw new IllegalStateException("Área não está disponível");
        }
        LocalDate data = LocalDate.parse(request.data(), DateTimeFormatter.ISO_LOCAL_DATE);
        if (!existente.getData().equals(data) || !existente.getHora().equals(request.hora()) ||
                !existente.getArea().getId().equals(request.areaId())) {
            if (reservaRepository.findByAreaAndDataAndHora(area, data, request.hora()).isPresent()) {
                throw new IllegalStateException("Área já reservada para esta data e hora");
            }
        }
        if (!existente.getAssociado().getId().equals(request.associadoId()) ||
                !existente.getData().equals(data) || !existente.getHora().equals(request.hora())) {
            if (reservaRepository.findByAssociadoAndDataAndHora(associado, data, request.hora()).isPresent()) {
                throw new IllegalStateException("Associado já possui uma reserva para esta data e hora");
            }
        }
        existente.setData(data);
        existente.setHora(request.hora());
        existente.setAssociado(associado);
        existente.setArea(area);
        existente.setAgendaAtividade(null);
        Reserva atualizada = reservaRepository.save(existente);
        return new ReservaResponseDTO(
                atualizada.getId(), request.data(), atualizada.getHora(),
                request.associadoId(), request.areaId(), null
        );
    }

    @Transactional
    public ReservaResponseDTO atualizarReservaAtividade(Long id, ReservaAtividadeRequestDTO request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        validarAtributosEmComum(request.associadoId(), request.data(), request.hora());
        if (request.agendaAtividadeId() == null || request.agendaAtividadeId() <= 0) {
            throw new IllegalArgumentException("ID da agenda de atividade é obrigatório");
        }
        if (request.participantes() <= 0) {
            throw new IllegalArgumentException("Número de participantes deve ser maior que zero");
        }
        Reserva existente = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada com ID: " + id));
        if (existente.getArea() != null) {
            throw new IllegalArgumentException("Reserva é do tipo área, use o endpoint correspondente");
        }
        Associado associado = associadoRepository.findById(request.associadoId())
                .orElseThrow(() -> new RuntimeException("Associado não encontrado com ID: " + request.associadoId()));
        if (!acessoClubeService.podeAcessarAtividades(request.associadoId())) {
            throw new IllegalStateException("Associado não tem permissão para reservar atividades");
        }
        AgendaAtividade agendaAtividade = agendaAtividadeRepository.findById(request.agendaAtividadeId())
                .orElseThrow(() -> new RuntimeException("Agenda de atividade não encontrada com ID: " + request.agendaAtividadeId()));
        Atividade atividade = agendaAtividade.getAtividade();
        LocalDate data = LocalDate.parse(request.data(), DateTimeFormatter.ISO_LOCAL_DATE);
        if (!agendaAtividade.getData().equals(data) || !agendaAtividade.getTime().equals(request.hora())) {
            throw new IllegalArgumentException("Data e hora devem corresponder à agenda de atividade");
        }
        int participantesAtuais = reservaRepository.findById(id)
                .map(Reserva::getAgendaAtividade)
                .map(AgendaAtividade::getParticipantes)
                .orElse(0);
        int participantesTotais = agendaAtividade.getParticipantes() - participantesAtuais + request.participantes();
        if (participantesTotais > atividade.getMaxParticipantes()) {
            throw new IllegalStateException("Limite de participantes excedido para a atividade");
        }
        if (!existente.getAssociado().getId().equals(request.associadoId()) ||
                !existente.getData().equals(data) || !existente.getHora().equals(request.hora())) {
            if (reservaRepository.findByAssociadoAndDataAndHora(associado, data, request.hora()).isPresent()) {
                throw new IllegalStateException("Associado já possui uma reserva para esta data e hora");
            }
        }
        agendaAtividade.setParticipantes(participantesTotais);
        agendaAtividadeRepository.save(agendaAtividade);
        existente.setData(data);
        existente.setHora(request.hora());
        existente.setAssociado(associado);
        existente.setAgendaAtividade(agendaAtividade);
        existente.setArea(null);
        Reserva atualizada = reservaRepository.save(existente);
        return new ReservaResponseDTO(
                atualizada.getId(), request.data(), atualizada.getHora(),
                request.associadoId(), null, request.agendaAtividadeId()
        );
    }

    @Transactional
    public void deletar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada com ID: " + id));
        if (reserva.getAgendaAtividade() != null) {
            AgendaAtividade agendaAtividade = reserva.getAgendaAtividade();
            int participantesAtuais = agendaAtividade.getParticipantes();
            agendaAtividade.setParticipantes(participantesAtuais - 1);
            agendaAtividadeRepository.save(agendaAtividade);
        }
        reservaRepository.deleteById(id);
    }

    private void validarAtributosEmComum(Long associadoId, String data, String hora) {
        if (associadoId == null || associadoId <= 0) {
            throw new IllegalArgumentException("ID do associado é obrigatório");
        }
        LocalDate parsedData;
        try {
            parsedData = LocalDate.parse(data, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data inválido. Use YYYY-MM-DD");
        }
        if (!parsedData.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data da reserva deve ser no futuro");
        }
        try {
            LocalTime.parse(hora, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de hora inválido. Use HH:mm");
        }
    }
}
