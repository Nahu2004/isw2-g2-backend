package com.grupo2.LigaSF.service.impl;

import com.grupo2.LigaSF.dto.response.PosicionResponse;
import com.grupo2.LigaSF.entity.enums.EstadoPartido;
import com.grupo2.LigaSF.repository.EquipoRepository;
import com.grupo2.LigaSF.service.PartidoService;
import com.grupo2.LigaSF.service.TablaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class TablaServiceImpl implements TablaService {

    private static final int PUNTOS_VICTORIA = 3;
    private static final int PUNTOS_EMPATE = 1;

    /** Puntos, diferencia de gol y goles a favor (de mayor a menor); a igualdad, orden alfabético. */
    private static final Comparator<Fila> ORDEN = Comparator
            .comparingInt(Fila::puntos)
            .thenComparingInt(Fila::diferencia)
            .thenComparingInt(Fila::golesFavor)
            .reversed()
            .thenComparing(Fila::nombre, String.CASE_INSENSITIVE_ORDER);

    private final EquipoRepository equipoRepository;
    private final PartidoService partidoService;

    public TablaServiceImpl(EquipoRepository equipoRepository, PartidoService partidoService) {
        this.equipoRepository = equipoRepository;
        this.partidoService = partidoService;
    }

    @Override
    public List<PosicionResponse> calcular(Integer torneoId) {
        Map<Integer, Fila> filas = new LinkedHashMap<>();
        equipoRepository.findByTorneoId(torneoId)
                .forEach(e -> filas.put(e.getId(), new Fila(e.getId(), e.getNombre())));

        // Solo cuentan los partidos FINALIZADOS (RF-11)
        partidoService.listar(torneoId, null).stream()
                .filter(p -> p.getEstado() == EstadoPartido.FINALIZADO)
                .forEach(p -> {
                    filas.get(p.getEquipoLocalId()).registrar(p.getGolesLocal(), p.getGolesVisitante());
                    filas.get(p.getEquipoVisitanteId()).registrar(p.getGolesVisitante(), p.getGolesLocal());
                });

        return filas.values().stream().sorted(ORDEN).map(Fila::aResponse).toList();
    }

    private static class Fila {
        private final Integer equipoId;
        private final String nombre;
        private int ganados;
        private int empatados;
        private int perdidos;
        private int golesFavor;
        private int golesContra;

        Fila(Integer equipoId, String nombre) {
            this.equipoId = equipoId;
            this.nombre = nombre;
        }

        void registrar(int aFavor, int enContra) {
            golesFavor += aFavor;
            golesContra += enContra;
            if (aFavor > enContra) {
                ganados++;
            } else if (aFavor == enContra) {
                empatados++;
            } else {
                perdidos++;
            }
        }

        String nombre() {
            return nombre;
        }

        int golesFavor() {
            return golesFavor;
        }

        int diferencia() {
            return golesFavor - golesContra;
        }

        int puntos() {
            return ganados * PUNTOS_VICTORIA + empatados * PUNTOS_EMPATE;
        }

        PosicionResponse aResponse() {
            return new PosicionResponse(equipoId, nombre, ganados + empatados + perdidos,
                    ganados, empatados, perdidos, golesFavor, golesContra, diferencia(), puntos());
        }
    }
}
