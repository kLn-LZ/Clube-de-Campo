package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.Associado;
import com.fatec.Clube_de_Campo.Services.AssociadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/associados")
public class AssociadoController {
    @Autowired
    private AssociadoService associadoService;

    @PostMapping
    public ResponseEntity<Associado> registraAssociado(@RequestBody Associado associado) {
        Associado associadoCriado = associadoService.insere(associado);
        return ResponseEntity.ok(associadoCriado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Associado> buscaAssociadoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(associadoService.buscaAssociadoPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<Associado>> listaAssociados() {
        return ResponseEntity.ok(associadoService.listaAssociados());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluiAssociadoPorId(@PathVariable  Long id) {
        associadoService.excluiPorId(id);
        return ResponseEntity.noContent().build();
    }
}
