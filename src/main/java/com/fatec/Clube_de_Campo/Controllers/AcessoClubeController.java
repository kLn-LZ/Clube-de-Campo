package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.StatusAcesso;
import com.fatec.Clube_de_Campo.Services.AcessoClubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/associados/{associadoId}/acesso")
public class AcessoClubeController {
    @Autowired
    private AcessoClubeService acessoClubeService;

    @GetMapping
    public ResponseEntity<StatusAcesso> buscaStatusDeAcesso(@PathVariable Long associadoId) {
        return ResponseEntity.ok(acessoClubeService.buscaStatusdeAcesso(associadoId));
    }
}
