package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.AgendaAtividade;
import com.fatec.Clube_de_Campo.Entities.Atividade;
import com.fatec.Clube_de_Campo.Repositories.AgendaAtividadeRepository;
import com.fatec.Clube_de_Campo.Repositories.AtividadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AgendaAtividadeService {
    @Autowired
    private AgendaAtividadeRepository agendaAtividadeRepository;
    @Autowired
    private AtividadeRepository atividadeRepository;

    public AgendaAtividade insere(AgendaAtividade agendaAtividade) {
        Atividade atividade = atividadeRepository.findById(agendaAtividade.getAtividade().getId())
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
        long atividadadesAgendadas = agendaAtividadeRepository.countByAtividadeAndData(atividade, agendaAtividade.getData());
        if (atividadadesAgendadas >= atividade.getMaxGruposPorDia()) {
            throw new IllegalStateException("Número máximo de grupos atingido");
        }
        return agendaAtividadeRepository.save(agendaAtividade);
    }

    public List<AgendaAtividade> buscaAtividadesDisponiveisPorIdEData(Long atividadeId, LocalDate data) {
        Atividade atividade = atividadeRepository.findById(atividadeId)
                .orElseThrow(() -> new RuntimeException("Atividade não encontrada"));
        return agendaAtividadeRepository.findByAtividadeAndData(atividade, data).stream()
                .filter(agendaAtividade -> agendaAtividade.getParticipantes() < atividade.getMaxParticipantes())
                .toList();
    }
}
