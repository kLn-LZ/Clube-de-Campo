package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.Area;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.AreaRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.AreaResponseDTO;
import com.fatec.Clube_de_Campo.Services.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaController {
    @Autowired
    private AreaService areaService;

    @PostMapping
    public ResponseEntity<AreaResponseDTO> criarArea(@RequestBody AreaRequestDTO request, UriComponentsBuilder uriBuilder) {
        AreaResponseDTO criado = areaService.insere(request);
        URI location = uriBuilder.path("/api/areas/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<AreaResponseDTO>> listarAreas() {
        return ResponseEntity.ok(areaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaResponseDTO> buscarAreaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(areaService.buscarPorId(id));
    }

    @GetMapping("/por-tipo/{tipoAreaId}")
    public ResponseEntity<List<AreaResponseDTO>> listarAreasPorTipoId(@PathVariable Long tipoAreaId) {
        return ResponseEntity.ok(areaService.buscarPorTipoId(tipoAreaId));
    }

    @GetMapping("/disponiveis/{tipoAreaId}")
    public ResponseEntity<List<AreaResponseDTO>> listarAreasDisponiveis(@PathVariable Long tipoAreaId) {
        return ResponseEntity.ok(areaService.buscarAreasDisponiveis(tipoAreaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaResponseDTO> atualizarArea(@PathVariable Long id, @RequestBody AreaRequestDTO request) {
        return ResponseEntity.ok(areaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarArea(@PathVariable Long id) {
        areaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
