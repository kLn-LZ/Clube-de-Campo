package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.Associado;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.AssociadoRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.AssociadoResponseDTO;
import com.fatec.Clube_de_Campo.Services.AssociadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/associados")
public class AssociadoController {
    @Autowired
    private AssociadoService associadoService;

    @PostMapping
    public ResponseEntity<AssociadoResponseDTO> criarAssociado(@RequestBody AssociadoRequestDTO request, UriComponentsBuilder uriBuilder) {
        AssociadoResponseDTO criado = associadoService.insere(request);
        URI location = uriBuilder.path("/api/associados/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<AssociadoResponseDTO>> listarAssociados() {
        return ResponseEntity.ok(associadoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssociadoResponseDTO> buscarAssociadoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(associadoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssociadoResponseDTO> atualizarAssociado(@PathVariable Long id, @RequestBody AssociadoRequestDTO request) {
        return ResponseEntity.ok(associadoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirAssociado(@PathVariable Long id) {
        associadoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
