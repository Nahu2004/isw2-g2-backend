package com.grupo2.LigaSF;

import com.grupo2.LigaSF.dto.response.PosicionResponse;
import com.grupo2.LigaSF.service.TablaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Sql("/datos-test.sql")
class TablaServiceTest {

    @Autowired
    TablaService tabla;

    @Test
    void ordenaPorPuntosDiferenciaGolesYNombre() {
        List<PosicionResponse> t = tabla.calcular(1);
        assertEquals(List.of("Alpha", "Gamma", "Beta", "Delta", "Epsilon"),
                t.stream().map(PosicionResponse::equipoNombre).toList());
    }

    @Test
    void calculaLasColumnasConPartidosFinalizados() {
        PosicionResponse alpha = tabla.calcular(1).get(0);
        assertEquals(1, alpha.pj());
        assertEquals(1, alpha.pg());
        assertEquals(2, alpha.gf());
        assertEquals(1, alpha.gc());
        assertEquals(1, alpha.dg());
        assertEquals(3, alpha.pts());
    }

    @Test
    void equipoSinPartidosFinalizadosApareceConCeros() {
        PosicionResponse delta = tabla.calcular(1).get(3);
        assertEquals("Delta", delta.equipoNombre());
        assertEquals(0, delta.pj());
        assertEquals(0, delta.pts());
    }
}
