package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.Pagamento;
import com.fatec.Clube_de_Campo.Services.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {
    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping
    public ResponseEntity<Pagamento> recordPayment(@RequestBody Pagamento pagamento) {
        Pagamento pagamentoFeito = pagamentoService.insere(pagamento);
        return ResponseEntity.ok(pagamentoFeito);
    }
}
