package com.fatec.Clube_de_Campo.Entities.DTOs.request;

import java.util.List;

public record AssociadoRequestDTO(String nome,
                                  String rg,
                                  String cpf,
                                  Long enderecoId,
                                  List<ContatoRequestDTO> contatos,
                                  Long tipoAssociadoId) {}
