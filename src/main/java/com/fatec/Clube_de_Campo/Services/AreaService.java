package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.Area;
import com.fatec.Clube_de_Campo.Entities.DTOs.request.AreaRequestDTO;
import com.fatec.Clube_de_Campo.Entities.DTOs.response.AreaResponseDTO;
import com.fatec.Clube_de_Campo.Entities.TipoArea;
import com.fatec.Clube_de_Campo.Repositories.AreaRepository;
import com.fatec.Clube_de_Campo.Repositories.ReservaRepository;
import com.fatec.Clube_de_Campo.Repositories.TipoAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AreaService {
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private TipoAreaRepository tipoAreaRepository;
    @Autowired
    private ReservaRepository reservaRepository;

    @Transactional
    public AreaResponseDTO insere(AreaRequestDTO request) {
        validarRequest(request);
        TipoArea tipoArea = tipoAreaRepository.findById(request.tipoAreaId())
                .orElseThrow(() -> new RuntimeException("Tipo de área não encontrado com ID: " + request.tipoAreaId()));
        if (areaRepository.findByNomeAndTipoAreaId(request.nome(), request.tipoAreaId()).isPresent()) {
            throw new IllegalArgumentException("Área com nome '" + request.nome() + "' já existe para este tipo");
        }
        Area area = new Area();
        area.setNome(request.nome());
        area.setTipoArea(tipoArea);
        area.setDisponivel(request.disponivel());
        Area salva = areaRepository.save(area);
        return new AreaResponseDTO(salva.getId(), salva.getNome(), request.tipoAreaId(), salva.isDisponivel());
    }

    public List<AreaResponseDTO> listarTodas() {
        return areaRepository.findAll().stream()
                .map(area -> new AreaResponseDTO(area.getId(), area.getNome(), area.getTipoArea().getId(), area.isDisponivel()))
                .collect(Collectors.toList());
    }

    public AreaResponseDTO buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área não encontrada com ID: " + id));
        return new AreaResponseDTO(area.getId(), area.getNome(), area.getTipoArea().getId(), area.isDisponivel());
    }

    public List<AreaResponseDTO> buscarPorTipoId(Long tipoAreaId) {
        if (tipoAreaId == null || tipoAreaId <= 0) {
            throw new IllegalArgumentException("ID do tipo de área inválido");
        }
        return areaRepository.findByTipoAreaId(tipoAreaId).stream()
                .map(area -> new AreaResponseDTO(area.getId(), area.getNome(), tipoAreaId, area.isDisponivel()))
                .collect(Collectors.toList());
    }


    public List<AreaResponseDTO> buscarAreasDisponiveis(Long tipoAreaId) {
        if (tipoAreaId == null || tipoAreaId <= 0) {
            throw new IllegalArgumentException("ID do tipo de área inválido");
        }
        return areaRepository.findByTipoAreaId(tipoAreaId).stream()
                .filter(Area::isDisponivel)
                .map(area -> new AreaResponseDTO(area.getId(), area.getNome(), tipoAreaId, area.isDisponivel()))
                .collect(Collectors.toList());
    }

    @Transactional
    public AreaResponseDTO atualizar(Long id, AreaRequestDTO request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        validarRequest(request);
        Area existente = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área não encontrada com ID: " + id));
        TipoArea tipoArea = tipoAreaRepository.findById(request.tipoAreaId())
                .orElseThrow(() -> new RuntimeException("Tipo de área não encontrado com ID: " + request.tipoAreaId()));
        if (!existente.getNome().equals(request.nome()) &&
                areaRepository.findByNomeAndTipoAreaId(request.nome(), request.tipoAreaId()).isPresent()) {
            throw new IllegalArgumentException("Área com nome '" + request.nome() + "' já existe para este tipo");
        }
        existente.setNome(request.nome());
        existente.setTipoArea(tipoArea);
        existente.setDisponivel(request.disponivel());
        Area atualizada = areaRepository.save(existente);
        return new AreaResponseDTO(atualizada.getId(), atualizada.getNome(), request.tipoAreaId(), atualizada.isDisponivel());
    }

    @Transactional
    public void deletar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        if (!areaRepository.existsById(id)) {
            throw new RuntimeException("Área não encontrada com ID: " + id);
        }
        if (reservaRepository.findByAreaId(id).isPresent()) {
            throw new IllegalStateException("Área possui reservas associadas e não pode ser deletada");
        }
        areaRepository.deleteById(id);
    }

    private void validarRequest(AreaRequestDTO request) {
        if (request.nome() == null || request.nome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da área é obrigatório e não pode ser vazio");
        }
        if (request.tipoAreaId() == null || request.tipoAreaId() <= 0) {
            throw new IllegalArgumentException("ID do tipo de área é inválido");
        }
    }
}
