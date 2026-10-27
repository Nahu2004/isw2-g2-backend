package com.grupo2.LigaSF;

import com.grupo2.LigaSF.dto.PartidoResumenDTO;
import com.grupo2.LigaSF.service.PartidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Sql("/datos-test.sql")
class PartidoServiceTest {

    @Autowired
    PartidoService partidos;

    @Test
    void listarPorFechaCalculaElResultado() {
        assertEquals(1, partidos.listar(1, 1).size());
        PartidoResumenDTO p = partidos.listar(1, 1).get(0);
        assertEquals(2, p.golesLocal());
        assertEquals(1, p.golesVisitante());
    }

    @Test
    void listarSinFechaDevuelveTodos() {
        assertEquals(4, partidos.listar(1, null).size());
    }

    @Test
    void partidoSinGolesMuestraCeroACero() {
        PartidoResumenDTO programado = partidos.listar(1, 3).get(0);
        assertEquals(0, programado.golesLocal());
        assertEquals(0, programado.golesVisitante());
    }

    @Test
    void fechaInexistenteDevuelveListaVacia() {
        assertTrue(partidos.listar(1, 9).isEmpty());
    }
}
