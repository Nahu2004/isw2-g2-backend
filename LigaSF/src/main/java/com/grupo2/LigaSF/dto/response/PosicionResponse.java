package com.grupo2.LigaSF.dto.response;

public record PosicionResponse(
        Integer equipoId,
        String equipoNombre,
        int pj,
        int pg,
        int pe,
        int pp,
        int gf,
        int gc,
        int dg,
        int pts) {
}
