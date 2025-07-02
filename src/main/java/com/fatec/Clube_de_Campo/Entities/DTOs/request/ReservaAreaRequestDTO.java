package com.fatec.Clube_de_Campo.Entities.DTOs.request;

import java.time.LocalDate;

public record ReservaAreaRequestDTO(Long associadoId, String data, String hora, Long areaId) {}
