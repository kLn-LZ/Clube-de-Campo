package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.Atividade;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.AtividadeRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.AtividadeResponseDTO;
import com.fatec.Clube_de_Campo.Services.AtividadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/atividades")
public class AtividadeController {
    @Autowired
    private AtividadeService atividadeService;

    @PostMapping
    public ResponseEntity<AtividadeResponseDTO> criarAtividade(@RequestBody AtividadeRequestDTO request, UriComponentsBuilder uriBuilder) {
        AtividadeResponseDTO criada = atividadeService.insere(request);
        URI location = uriBuilder.path("/api/atividades/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    public ResponseEntity<List<AtividadeResponseDTO>> listarAtividades() {
        return ResponseEntity.ok(atividadeService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtividadeResponseDTO> buscarAtividadePorId(@PathVariable Long id) {
        return ResponseEntity.ok(atividadeService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtividadeResponseDTO> atualizarAtividade(@PathVariable Long id, @RequestBody AtividadeRequestDTO request) {
        return ResponseEntity.ok(atividadeService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAtividade(@PathVariable Long id) {
        atividadeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
