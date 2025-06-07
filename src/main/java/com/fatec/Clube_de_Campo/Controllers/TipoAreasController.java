package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.TipoArea;
import com.fatec.Clube_de_Campo.Services.TipoAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-areas")
public class TipoAreasController {
    @Autowired
    private TipoAreaService tipoAreaService;

    @PostMapping
    public ResponseEntity<TipoArea> registraTipoArea(@RequestBody TipoArea tipoArea) {
        TipoArea TipoArea = tipoAreaService.insere(tipoArea);
        return ResponseEntity.ok(tipoArea);
    }

    @GetMapping
    public ResponseEntity<List<TipoArea>> buscaTodosTiposArea() {
        return ResponseEntity.ok(tipoAreaService.listaTipoArea());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoArea> buscaTipoAreaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoAreaService.buscaTipoArea(id));
    }
}
