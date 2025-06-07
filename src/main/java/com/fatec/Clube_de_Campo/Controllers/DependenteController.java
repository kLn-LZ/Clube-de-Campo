package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.Dependente;
import com.fatec.Clube_de_Campo.Services.DependenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dependentes")
public class DependenteController {
    @Autowired
    private DependenteService dependenteService;

    @PostMapping
    public ResponseEntity<Dependente> registra(
            @RequestBody Dependente dependente,
            @RequestParam Long associadoId) {
        Dependente dependenteCriado = dependenteService.registerDependent(dependente, associadoId);
        return ResponseEntity.ok(dependenteCriado);
    }
}
