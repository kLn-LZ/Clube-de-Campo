package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.Area;
import com.fatec.Clube_de_Campo.Services.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaController {
    @Autowired
    private AreaService areaService;

    @PostMapping
    public ResponseEntity<Area> registra(@RequestBody Area area) {
        Area areaCriado = areaService.insere(area);
        return ResponseEntity.ok(areaCriado);
    }

    @GetMapping("/by-type/{tipoAreaId}")
    public ResponseEntity<List<Area>> listaAreasPorTipoId(@PathVariable Long tipoAreaId) {
        return ResponseEntity.ok(areaService.buscaAreaPorTipoId(tipoAreaId));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<Area>> listaAreasDisponiveis(
            @RequestParam Long tipoAreaId,
            @RequestParam String data,
            @RequestParam String hora) {
        LocalDate localDate = LocalDate.parse(data);
        return ResponseEntity.ok(areaService.buscaAreaDisponivel(tipoAreaId, localDate, hora));
    }
}
