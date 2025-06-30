package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.DTOs.request.ReservaAreaRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.ReservaAtividadeRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.ReservaResponseDTO;
import com.fatec.Clube_de_Campo.Services.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    @Autowired
    private ReservaService reservaService;

    @PostMapping("/area")
    public ResponseEntity<ReservaResponseDTO> registraReservaParaArea(@RequestBody ReservaAreaRequestDTO request) {
        ReservaResponseDTO response = reservaService.insereReservaArea(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/atividade")
    public ResponseEntity<ReservaResponseDTO> registraReservaParaAtividade(@RequestBody ReservaAtividadeRequestDTO request) {
        ReservaResponseDTO response = reservaService.insereReservaAtividade(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
