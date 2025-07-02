package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.DTOs.request.ReservaAreaRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.ReservaAtividadeRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.ReservaResponseDTO;
import com.fatec.Clube_de_Campo.Services.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/reservas")
public class ReservaController {
    @Autowired
    private ReservaService reservaService;

    @PostMapping("/area")
    public ResponseEntity<ReservaResponseDTO> registrarReservaArea(@RequestBody ReservaAreaRequestDTO request, UriComponentsBuilder uriBuilder) {
        validarRequest(request.data(), request.hora());
        ReservaResponseDTO response = reservaService.insereReservaArea(request);
        URI location = uriBuilder.path("/api/reservas/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/atividade")
    public ResponseEntity<ReservaResponseDTO> registrarReservaAtividade(@RequestBody ReservaAtividadeRequestDTO request, UriComponentsBuilder uriBuilder) {
        validarRequest(request.data(), request.hora());
        ReservaResponseDTO response = reservaService.insereReservaAtividade(request);
        URI location = uriBuilder.path("/api/reservas/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarReservas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> buscarReservaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @PutMapping("/area/{id}")
    public ResponseEntity<ReservaResponseDTO> atualizarReservaArea(@PathVariable Long id, @RequestBody ReservaAreaRequestDTO request) {
        validarRequest(request.data(), request.hora());
        return ResponseEntity.ok(reservaService.atualizarReservaArea(id, request));
    }

    @PutMapping("/atividade/{id}")
    public ResponseEntity<ReservaResponseDTO> atualizarReservaAtividade(@PathVariable Long id, @RequestBody ReservaAtividadeRequestDTO request) {
        validarRequest(request.data(), request.hora());
        return ResponseEntity.ok(reservaService.atualizarReservaAtividade(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarReserva(@PathVariable Long id) {
        reservaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private void validarRequest(String data, String hora) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Data da reserva é obrigatória");
        }
        if (hora == null || hora.trim().isEmpty()) {
            throw new IllegalArgumentException("Hora da reserva é obrigatória");
        }
        try {
            LocalDate.parse(data, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data inválido. Use YYYY-MM-DD");
        }
        try {
            LocalTime.parse(hora, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de hora inválido. Use HH:mm");
        }
    }
}
