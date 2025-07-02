package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.DTOs.request.TipoAreaRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.TipoAreaResponseDTO;
import com.fatec.Clube_de_Campo.Entities.TipoArea;
import com.fatec.Clube_de_Campo.Services.TipoAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tipo-areas")
public class TipoAreasController {

    @Autowired
    private TipoAreaService tipoAreaService;

    @PostMapping
    public ResponseEntity<TipoAreaResponseDTO> criarTipoArea(@RequestBody TipoAreaRequestDTO request, UriComponentsBuilder uriBuilder) {
        TipoAreaResponseDTO criado = tipoAreaService.insere(request);
        URI location = uriBuilder.path("/api/tipo-areas/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<TipoAreaResponseDTO>> listarTiposArea() {
        return ResponseEntity.ok(tipoAreaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoAreaResponseDTO> buscarTipoAreaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoAreaService.buscaPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoAreaResponseDTO> atualizarTipoArea(@PathVariable Long id, @RequestBody TipoAreaRequestDTO request) {
        return ResponseEntity.ok(tipoAreaService.atualiza(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTipoArea(@PathVariable Long id) {
        tipoAreaService.deleta(id);
        return ResponseEntity.noContent().build();
    }
}
