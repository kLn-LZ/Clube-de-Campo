package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.DTOs.request.DependenteRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.DependenteResponseDTO;
import com.fatec.Clube_de_Campo.Entities.Dependente;
import com.fatec.Clube_de_Campo.Services.DependenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/dependentes")
public class DependenteController {
    @Autowired
    private DependenteService dependenteService;

    @PostMapping
    public ResponseEntity<DependenteResponseDTO> criarDependente(@RequestBody DependenteRequestDTO request, UriComponentsBuilder uriBuilder) {
        DependenteResponseDTO criado = dependenteService.registrarDependente(request);
        URI location = uriBuilder.path("/api/dependentes/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<DependenteResponseDTO>> listarDependentes() {
        return ResponseEntity.ok(dependenteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DependenteResponseDTO> buscarDependentePorId(@PathVariable Long id) {
        return ResponseEntity.ok(dependenteService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DependenteResponseDTO> atualizarDependente(@PathVariable Long id, @RequestBody DependenteRequestDTO request) {
        return ResponseEntity.ok(dependenteService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDependente(@PathVariable Long id) {
        dependenteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
