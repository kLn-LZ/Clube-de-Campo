package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.AgendaAtividade;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.AgendaAtividadeRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.AgendaAtividadeResponseDTO;
import com.fatec.Clube_de_Campo.Services.AgendaAtividadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/agenda-atividades")
public class AgendaAtividadeController {

    @Autowired
    private AgendaAtividadeService agendaAtividadeService;

    @PostMapping
    public ResponseEntity<AgendaAtividadeResponseDTO> criarAgendaAtividade(@RequestBody AgendaAtividadeRequestDTO request, UriComponentsBuilder uriBuilder) {
        AgendaAtividadeResponseDTO criada = agendaAtividadeService.insere(request);
        URI location = uriBuilder.path("/api/agenda-atividades/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    public ResponseEntity<List<AgendaAtividadeResponseDTO>> listarAgendasAtividade() {
        return ResponseEntity.ok(agendaAtividadeService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaAtividadeResponseDTO> buscarAgendaAtividadePorId(@PathVariable Long id) {
        return ResponseEntity.ok(agendaAtividadeService.buscarPorId(id));
    }

    @GetMapping("/disponivel")
    public ResponseEntity<List<AgendaAtividadeResponseDTO>> buscarAtividadesDisponiveisPorIdEData(
            @RequestParam Long atividadeId,
            @RequestParam String data) {
        try {
            LocalDate localDate = LocalDate.parse(data, DateTimeFormatter.ISO_LOCAL_DATE);
            return ResponseEntity.ok(agendaAtividadeService.buscarAtividadesDisponiveisPorIdEData(atividadeId, localDate));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data inválido. Use YYYY-MM-DD");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaAtividadeResponseDTO> atualizarAgendaAtividade(@PathVariable Long id, @RequestBody AgendaAtividadeRequestDTO request) {
        return ResponseEntity.ok(agendaAtividadeService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAgendaAtividade(@PathVariable Long id) {
        agendaAtividadeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
