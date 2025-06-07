package com.fatec.Clube_de_Campo.Controllers;

import com.fatec.Clube_de_Campo.Entities.Reserva;
import com.fatec.Clube_de_Campo.Services.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    @Autowired
    private ReservaService reservaService;

    @PostMapping("/area")
    public ResponseEntity<Reserva> registraReservaParaArea(
            @RequestBody Reserva reserva,
            @RequestParam Long areaId) {
        Reserva created = reservaService.insereReservaArea(reserva, areaId);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/atividade")
    public ResponseEntity<Reserva> registraReservaParaAtividade(
            @RequestBody Reserva reserva,
            @RequestParam Long agendaAtividadeId,
            @RequestParam int participantes) {
        Reserva reservaCriada = reservaService.insereReservaAtividade(reserva, agendaAtividadeId, participantes);
        return ResponseEntity.ok(reservaCriada);
    }
}
