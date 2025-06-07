package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.AgendaAtividade;
import com.fatec.Clube_de_Campo.Services.AgendaAtividadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/agenda-atividades")
public class AgendaAtividadeController {
    @Autowired
    private AgendaAtividadeService agendaAtividadeService;

    @PostMapping
    public ResponseEntity<AgendaAtividade> registra(@RequestBody AgendaAtividade agendaAtividade) {
        AgendaAtividade agendaAtividadeCriada = agendaAtividadeService.insere(agendaAtividade);
        return ResponseEntity.ok(agendaAtividadeCriada);
    }

    @GetMapping("/disponivel")
    public ResponseEntity<List<AgendaAtividade>> buscaAtividadesDisponiveisPorIdEData(
            @RequestParam Long atividadeId,
            @RequestParam String data) {
        LocalDate localDate = LocalDate.parse(data);
        return ResponseEntity.ok(agendaAtividadeService.buscaAtividadesDisponiveisPorIdEData(atividadeId, localDate));
    }
}
