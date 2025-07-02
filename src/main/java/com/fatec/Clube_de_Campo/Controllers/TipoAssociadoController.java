package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.DTOs.request.TipoAssociadoRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.TipoAssociadoResponseDTO;
import com.fatec.Clube_de_Campo.Services.TipoAssociadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tipo-associados")
public class TipoAssociadoController {

    @Autowired
    private TipoAssociadoService tipoAssociadoService;

    @PostMapping
    public ResponseEntity<TipoAssociadoResponseDTO> criarTipoAssociado(@RequestBody TipoAssociadoRequestDTO request, UriComponentsBuilder uriBuilder) {
        TipoAssociadoResponseDTO criado = tipoAssociadoService.insere(request);
        URI location = uriBuilder.path("/api/tipo-associados/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<TipoAssociadoResponseDTO>> listarTipoAssociados() {
        return ResponseEntity.ok(tipoAssociadoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoAssociadoResponseDTO> buscarTipoAssociadoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoAssociadoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoAssociadoResponseDTO> atualizarTipoAssociado(@PathVariable Long id, @RequestBody TipoAssociadoRequestDTO request) {
        return ResponseEntity.ok(tipoAssociadoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTipoAssociado(@PathVariable Long id) {
        tipoAssociadoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}