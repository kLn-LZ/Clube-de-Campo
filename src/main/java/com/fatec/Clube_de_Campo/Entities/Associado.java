package com.fatec.Clube_de_Campo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Associado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String rg;
    private String cpf;
    @OneToOne
    private Endereco endereco;
    @OneToMany
    private Contato contato;
    private LocalDate dataCadastrado;
    private TipoAssociado tipoAssociado;
}
