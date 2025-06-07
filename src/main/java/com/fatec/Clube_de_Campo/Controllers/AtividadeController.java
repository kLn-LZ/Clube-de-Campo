package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.Atividade;
import com.fatec.Clube_de_Campo.Services.AtividadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atividades")
public class AtividadeController {
    @Autowired
    private AtividadeService atividadeService;

    @PostMapping
    public ResponseEntity<Atividade> createActivity(@RequestBody Atividade atividade) {
        Atividade atividadeCriada = atividadeService.insere(atividade);
        return ResponseEntity.ok(atividadeCriada);
    }

    @GetMapping
    public ResponseEntity<List<Atividade>> listaAtividades() {
        return ResponseEntity.ok(atividadeService.listaAtividades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atividade> buscaAtividadePorId(@PathVariable Long id) {
        return ResponseEntity.ok(atividadeService.buscaAtividadePorId(id));
    }
}
