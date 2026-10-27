package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.EventoPartidoRequest;
import com.grupo2.LigaSF.dto.response.EventoPartidoResponse;

import java.util.List;

public interface EventoPartidoService {
    EventoPartidoResponse registrarEvento(Integer partidoId, EventoPartidoRequest request);
    void eliminarEvento(Integer eventoId);
    List<EventoPartidoResponse> listarPorPartido(Integer partidoId);
}
