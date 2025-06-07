package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.Area;
import com.fatec.Clube_de_Campo.Repositories.AreaRepository;
import com.fatec.Clube_de_Campo.Repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AreaService {
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private ReservaRepository reservaRepository;

    public Area insere(Area area) {
        return areaRepository.save(area);
    }

    public List<Area> buscaAreaPorTipoId(Long tipoAreaId) {
        return areaRepository.findByTipoAreaId(tipoAreaId);
    }

    public List<Area> buscaAreaDisponivel(Long tipoAreaId, LocalDate data, String hora) {
        List<Area> areas = areaRepository.findByTipoAreaId(tipoAreaId);
        return areas.stream()
                .filter(area -> area.isDisponivel() &&
                        reservaRepository.findByAreaAndDataAndHora(area, data, hora).isEmpty())
                .toList();
    }

}
