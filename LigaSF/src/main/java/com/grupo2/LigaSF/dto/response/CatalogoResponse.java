package com.grupo2.LigaSF.dto.response;

import java.util.List;

public class CatalogoResponse {

    private List<TorneoResponse> torneos;
    private List<CanchaResponse> canchas;
    private List<ArbitroResponse> arbitros;

    public CatalogoResponse() {
    }

    public CatalogoResponse(List<TorneoResponse> torneos, List<CanchaResponse> canchas, List<ArbitroResponse> arbitros) {
        this.torneos = torneos;
        this.canchas = canchas;
        this.arbitros = arbitros;
    }

    public List<TorneoResponse> getTorneos() {
        return torneos;
    }

    public void setTorneos(List<TorneoResponse> torneos) {
        this.torneos = torneos;
    }

    public List<CanchaResponse> getCanchas() {
        return canchas;
    }

    public void setCanchas(List<CanchaResponse> canchas) {
        this.canchas = canchas;
    }

    public List<ArbitroResponse> getArbitros() {
        return arbitros;
    }

    public void setArbitros(List<ArbitroResponse> arbitros) {
        this.arbitros = arbitros;
    }
}
