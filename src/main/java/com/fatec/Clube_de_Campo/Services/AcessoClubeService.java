package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.DTOs.response.StatusAcessoResponseDTO;
import com.fatec.Clube_de_Campo.Entities.StatusAcesso;
import com.fatec.Clube_de_Campo.Repositories.AssociadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AcessoClubeService {
    @Autowired
    private CobrancaService cobrancaService;
    @Autowired
    private AssociadoRepository associadoRepository;

    public StatusAcessoResponseDTO buscarStatusDeAcesso(Long associadoId) {
        if (associadoId == null || associadoId <= 0) {
            throw new IllegalArgumentException("ID do associado inválido");
        }
        if (!associadoRepository.existsById(associadoId)) {
            throw new RuntimeException("Associado não encontrado com ID: " + associadoId);
        }
        int mesesNaoPagos = cobrancaService.calculaMesesNaoPagos(associadoId);
        StatusAcesso status;
        if (mesesNaoPagos >= 4) {
            status = StatusAcesso.BLOQUEADO;
        } else if (mesesNaoPagos >= 3) {
            status = StatusAcesso.APENAS_QUADRA;
        } else if (mesesNaoPagos >= 2) {
            status = StatusAcesso.RESTRITO;
        } else {
            status = StatusAcesso.COMPLETO;
        }
        return new StatusAcessoResponseDTO(status.name());
    }

    public boolean podeAcessarTodasAreas(Long associadoId) {
        if (associadoId == null || associadoId <= 0) {
            throw new IllegalArgumentException("ID do associado inválido");
        }
        StatusAcessoResponseDTO status = buscarStatusDeAcesso(associadoId);
        return status.statusAcesso().equals(StatusAcesso.COMPLETO.name());
    }

    public boolean podeAcessarAtividades(Long associadoId) {
        if (associadoId == null || associadoId <= 0) {
            throw new IllegalArgumentException("ID do associado inválido");
        }
        StatusAcessoResponseDTO status = buscarStatusDeAcesso(associadoId);
        return status.statusAcesso().equals(StatusAcesso.COMPLETO.name());
    }
}
