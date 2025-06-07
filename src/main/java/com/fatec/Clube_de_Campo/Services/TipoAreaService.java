package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.TipoArea;
import com.fatec.Clube_de_Campo.Repositories.TipoAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoAreaService {
    @Autowired
    private TipoAreaRepository areaTypeRepository;

    public TipoArea insere(TipoArea tipoArea) {
        if (areaTypeRepository.findByNome(tipoArea.getNome()).isPresent()) {
            throw new IllegalArgumentException("Tipo de área já existe");
        }
        return areaTypeRepository.save(tipoArea);
    }

    public List<TipoArea> listaTipoArea() {
        return areaTypeRepository.findAll();
    }

    public TipoArea buscaTipoArea(Long id) {
        return areaTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de área não encontrado"));
    }
}
