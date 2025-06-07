package com.fatec.Clube_de_Campo.Services;

import com.fatec.Clube_de_Campo.Entities.StatusAcesso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AcessoClubeService {
    @Autowired
    private PagamentoService pagamentoService;

    public StatusAcesso buscaStatusdeAcesso(Long associadoId) {
        int mesesNaoPagos = pagamentoService.calculaMesesNaoPagos(associadoId);
        if (mesesNaoPagos >= 4) {
            return StatusAcesso.BLOQUEADO;
        } else if (mesesNaoPagos >= 3) {
            return StatusAcesso.APENAS_QUADRA;
        } else if (mesesNaoPagos >= 2) {
            return StatusAcesso.RESTRITO;
        }
        return StatusAcesso.COMPLETO;
    }

    public boolean podeAcessarTodasAreas(Long associadoId) {
        StatusAcesso status = buscaStatusdeAcesso(associadoId);
        return status == StatusAcesso.COMPLETO;
    }

    public boolean podeAcessarAtividades(Long associadoId) {
        StatusAcesso status = buscaStatusdeAcesso(associadoId);
        return status == StatusAcesso.COMPLETO;
    }
}
