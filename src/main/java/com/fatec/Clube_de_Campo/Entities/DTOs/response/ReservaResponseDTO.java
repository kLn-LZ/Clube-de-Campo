package com.fatec.Clube_de_Campo.Entities.DTOs.response;

import java.time.LocalDate;

public record ReservaResponseDTO(Long id, LocalDate data, String hora, Long associadoId, Long areaId, Long agendaAtividadeId) {}
