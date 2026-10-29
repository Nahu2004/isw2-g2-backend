package com.grupo2.LigaSF;

import com.grupo2.LigaSF.dto.response.SuspendidoResponse;
import com.grupo2.LigaSF.exception.ResourceNotFoundException;
import com.grupo2.LigaSF.service.SuspendidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@Sql({"/datos-test.sql", "/datos-suspendidos.sql"})
class SuspendidoServiceTest {

    @Autowired
    SuspendidoService service;

    @Test
    void listaLaRojaYLaDobleAmarillaDeLaFechaAnterior() {
        List<SuspendidoResponse> suspendidos = service.listarPorFecha(1, 2);

        assertEquals(2, suspendidos.size());
        assertEquals("Alpha", suspendidos.get(0).equipoNombre());
        assertEquals("ROJA", suspendidos.get(0).motivo());
        assertEquals("Beta", suspendidos.get(1).equipoNombre());
        assertEquals("DOBLE_AMARILLA", suspendidos.get(1).motivo());
        assertEquals(1, suspendidos.get(0).fechaSancion());
    }

    @Test
    void laPrimeraFechaNoTieneSuspendidos() {
        assertTrue(service.listarPorFecha(1, 1).isEmpty());
    }

    @Test
    void sinTarjetasEnLaFechaAnteriorNoHaySuspendidos() {
        assertTrue(service.listarPorFecha(1, 3).isEmpty());
    }

    @Test
    void torneoInexistenteLanzaExcepcion() {
        assertThrows(ResourceNotFoundException.class, () -> service.listarPorFecha(99, 2));
    }
}
