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
        validaAtributosEmComum(request.associadoId(), request.data(), request.hora());
        if (request.areaId() == null) {
            throw new IllegalArgumentException("ID da área é obrigatório");
        }
        Associado associado = associadoRepository.findById(request.associadoId())
                .orElseThrow(() -> new RuntimeException("Associado não encontrado"));

        if (!acessoClubeService.podeAcessarTodasAreas(request.associadoId())) {
            throw new IllegalStateException("Associado bloqueado de áreas que exijam reserva");
        }
        Area area = areaRepository.findById(request.areaId())
                .orElseThrow(() -> new RuntimeException("Área não encontrada"));
        if (!area.isDisponivel()) {
            throw new IllegalStateException("Área indisponível");
        }
        if (reservaRepository.findByAreaAndDataAndHora(area, request.data(), request.hora()).isPresent()) {
            throw new IllegalStateException("Área já reservada para esta data e hora");
        }
        if (reservaRepository.findByAssociadoAndDataAndHora(associado, request.data(), request.hora()).isPresent()) {
            throw new IllegalStateException("Associado já possui uma reserva para esta data e hora");
        }

        Reserva reserva = new Reserva();
        reserva.setData(request.data());
        reserva.setHora(request.hora());
        reserva.setAssociado(associado);
        reserva.setArea(area);
        reserva.setAgendaAtividade(null);
        Reserva savedReserva = reservaRepository.save(reserva);

        return new ReservaResponseDTO(
                savedReserva.getId(),
                savedReserva.getData(),
                savedReserva.getHora(),
                request.associadoId(),
                request.areaId(),
                null
        );
    }

    @Transactional
    public ReservaResponseDTO insereReservaAtividade(ReservaAtividadeRequestDTO request) {
        validaAtributosEmComum(request.associadoId(), request.data(), request.hora());
        if (request.agendaAtividadeId() == null) {
            throw new IllegalArgumentException("ID da agenda de atividade é obrigatório");
        }
        if (request.participantes() <= 0) {
            throw new IllegalArgumentException("Número de participantes deve ser maior que zero");
        }
        Associado associado = associadoRepository.findById(request.associadoId())
                .orElseThrow(() -> new RuntimeException("Associado não encontrado"));
        if (!acessoClubeService.podeAcessarAtividades(request.associadoId())) {
            throw new IllegalStateException("Associado bloqueado de atividades");
        }

        AgendaAtividade agendaAtividade = agendaAtividadeRepository.findById(request.agendaAtividadeId())
                .orElseThrow(() -> new RuntimeException("Atividade agendada não encontrada"));

        Atividade atividade = agendaAtividade.getAtividade();
        int participantesTotais = agendaAtividade.getParticipantes() + request.participantes();
        if (participantesTotais > atividade.getMaxParticipantes()) {
            throw new IllegalStateException("Limite de participantes excedido");
        }
        if (reservaRepository.findByAssociadoAndDataAndHora(associado, request.data(), request.hora()).isPresent()) {
            throw new IllegalStateException("Associado já possui uma reserva para esta data e hora");
        }

        agendaAtividade.setParticipantes(participantesTotais);
        agendaAtividadeRepository.save(agendaAtividade);

        Reserva reserva = new Reserva();
        reserva.setData(request.data());
        reserva.setHora(request.hora());
        reserva.setAssociado(associado);
        reserva.setAgendaAtividade(agendaAtividade);
        reserva.setArea(null);
        Reserva savedReserva = reservaRepository.save(reserva);

        return new ReservaResponseDTO(
                savedReserva.getId(),
                savedReserva.getData(),
                savedReserva.getHora(),
                request.associadoId(),
                null,
                request.agendaAtividadeId()
        );
    }

    private void validaAtributosEmComum(Long associadoId, LocalDate data, String hora) {
        if (associadoId == null) {
            throw new IllegalArgumentException("ID do associado é obrigatório");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data da reserva é obrigatória");
        }
        if (hora == null || hora.isBlank()) {
            throw new IllegalArgumentException("Hora da reserva é obrigatória");
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime.parse(hora, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de hora inválido. Use HH:mm (ex: 14:30)");
        }
        if (!data.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data da reserva deve ser no futuro");
        }
    }
}
