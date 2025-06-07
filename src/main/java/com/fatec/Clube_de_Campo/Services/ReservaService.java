package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.AgendaAtividade;
import com.fatec.Clube_de_Campo.Entities.Area;
import com.fatec.Clube_de_Campo.Entities.Atividade;
import com.fatec.Clube_de_Campo.Entities.Reserva;
import com.fatec.Clube_de_Campo.Repositories.AgendaAtividadeRepository;
import com.fatec.Clube_de_Campo.Repositories.AreaRepository;
import com.fatec.Clube_de_Campo.Repositories.AtividadeRepository;
import com.fatec.Clube_de_Campo.Repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Reserva insereReservaArea(Reserva reserva, Long areaId) {
        if (!acessoClubeService.podeAcessarTodasAreas(reserva.getAssociado().getId())) {
            throw new IllegalStateException("Associado bloqueado de áreas exijam reserva");
        }
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Área não encontrada"));
        if (!area.isDisponivel() ||
                reservaRepository.findByAreaAndDataAndHora(area, reserva.getData(), reserva.getHora()).isPresent()) {
            throw new IllegalStateException("Área indisponível");
        }
        reserva.setArea(area);
        return reservaRepository.save(reserva);
    }

    public Reserva insereReservaAtividade(Reserva reserva, Long agendaId, int participantes) {
        if (!acessoClubeService.podeAcessarAtividades(reserva.getAssociado().getId())) {
            throw new IllegalStateException("Associado bloqueado de atividades");
        }
        AgendaAtividade agendaAtividade = agendaAtividadeRepository.findById(agendaId)
                .orElseThrow(() -> new RuntimeException("Atividade agendada não encontrada"));
        Atividade atividade = agendaAtividade.getAtividade();
        int participantesTotais = agendaAtividade.getParticipantes() + participantes;
        if (participantesTotais > atividade.getMaxParticipantes()) {
            throw new IllegalStateException("Limite de participantes excedido");
        }
        agendaAtividade.setParticipantes(participantesTotais);
        agendaAtividadeRepository.save(agendaAtividade);
        reserva.setAgendaAtividade(agendaAtividade);
        return reservaRepository.save(reserva);
    }
}
