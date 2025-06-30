package com.fatec.Clube_de_Campo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dependente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String rg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "associado_id", nullable = false)
    private Associado associado;
}
