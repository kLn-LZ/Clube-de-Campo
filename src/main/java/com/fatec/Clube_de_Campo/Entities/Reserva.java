package com.fatec.Clube_de_Campo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate data;
    private String hora;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "associado_id", nullable = false)
    private Associado associado;
    private Area area;
    private AgendaAtividade agendaAtividade;
}
