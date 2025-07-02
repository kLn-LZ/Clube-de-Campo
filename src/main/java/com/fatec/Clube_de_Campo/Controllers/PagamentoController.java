package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.DTOs.request.PagamentoRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.CobrancaResponseDTO;
import com.fatec.Clube_de_Campo.Services.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping
    public ResponseEntity<CobrancaResponseDTO> registrarPagamento(@RequestBody PagamentoRequestDTO request, UriComponentsBuilder uriBuilder) {
        CobrancaResponseDTO cobrancaResponse = pagamentoService.processarPagamento(request);
        URI location = uriBuilder.path("/api/cobrancas/{id}").buildAndExpand(cobrancaResponse.id()).toUri();
        return ResponseEntity.created(location).body(cobrancaResponse);
    }

    @GetMapping
    public ResponseEntity<List<CobrancaResponseDTO>> listarCobrancas() {
        return ResponseEntity.ok(pagamentoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CobrancaResponseDTO> buscarCobrancaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.buscarPorId(id));
    }
}
