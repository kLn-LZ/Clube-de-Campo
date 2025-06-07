package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.Associado;
import com.fatec.Clube_de_Campo.Entities.Dependente;
import com.fatec.Clube_de_Campo.Repositories.AssociadoRepository;
import com.fatec.Clube_de_Campo.Repositories.DependenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DependenteService {
    @Autowired
    private DependenteRepository dependenteRepository;
    @Autowired
    private AssociadoRepository associadoRepository;

    public Dependente registerDependent(Dependente dependente, Long associadoId) {
        Associado associado = associadoRepository.findById(associadoId)
                .orElseThrow(() -> new RuntimeException("Associado não encontrado"));
        dependente.setAssociado(associado);
        return dependenteRepository.save(dependente);
    }
}
