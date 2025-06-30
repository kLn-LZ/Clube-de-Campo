package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.Cobranca;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.PagamentoRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.CobrancaResponseDTO;
import com.fatec.Clube_de_Campo.Services.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cobrancas")
public class CobrancaController {
    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping
    public ResponseEntity<CobrancaResponseDTO> registraPagamento(@RequestBody PagamentoRequestDTO pagamentoRequest) {
        CobrancaResponseDTO cobrancaResponse = pagamentoService.processarPagamento(
                pagamentoRequest.associadoId(),
                pagamentoRequest.dataPagamento()
        );
        return ResponseEntity.ok(cobrancaResponse);
    }
}
