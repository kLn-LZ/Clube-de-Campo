package com.fatec.Clube_de_Campo.Entities.DTOs.response;

import java.time.LocalDate;
import java.util.List;

public record AssociadoResponseDTO(Long id,
                                   String nome,
                                   String rg,
                                   String cpf,
                                   Long enderecoId,
                                   List<ContatoResponseDTO> contatos,
                                   Long tipoAssociadoId,
                                   LocalDate dataCadastrado) {}
