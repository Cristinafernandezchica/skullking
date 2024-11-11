package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoService;

@WebMvcTest(ManoRestController.class)
public class ManoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManoService manoService;

    @MockBean
    private TrucoService trucoService;

    private Mano mano;
    private Jugador jugador;
    private Carta carta;
    private Partida partida;
    private Ronda ronda;

    @BeforeEach
    void setUp() {
        jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(12);
        jugador.setTurno(1);
        jugador.setUsuario(null);
        jugador.setPartida(partida);

        carta = new Carta();
        carta.setId(1);
        carta.setImagenFrontal("./images/cartas/morada_1.png");
        carta.setImagenTrasera("./images/cartas/parte_trasera.png");
        carta.setNumero(1);
        carta.setTipoCarta(TipoCarta.morada);

        mano =new Mano();
        mano.setApuesta(1);
        mano.setCartas(List.of(carta));
        mano.setId(1);
        mano.setJugador(jugador);
        mano.setResultado(5);
        mano.setRonda(ronda);

        ronda = new Ronda();
        ronda.setId(1);
        ronda.setBazaActual(3);
        ronda.setEstado(RondaEstado.JUGANDO);
        ronda.setNumBazas(4);
        ronda.setNumRonda(4);
        ronda.setPartida(partida);

        partida = new Partida();
        partida.setEstado(PartidaEstado.JUGANDO);
        partida.setFin(LocalDateTime.now());
        partida.setId(5);
        partida.setInicio(LocalDateTime.now());
        partida.setNombre("Partida Test");
        partida.setOwnerPartida(1);
    }

    // Test para obtener todas las manos
    @Test
    void testFindAll() {
        List<Mano> manos = Arrays.asList(mano);
        given(manoService.findAll()).willReturn(manos);

        ResponseEntity<List<Mano>> response = new ManoRestController(manoService, trucoService).findAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // Test para crear una mano
    @Test
    void testCreate() {
        given(manoService.saveMano(any(Mano.class))).willReturn(mano);

        ResponseEntity<Mano> response = new ManoRestController(manoService, trucoService).create(mano);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mano.getId(), response.getBody().getId());
    }

    // Test para actualizar una mano
    @Test
    void testUpdateMano() {
        given(manoService.findManoById(1)).willReturn(mano);
        given(manoService.updateMano(any(Mano.class), eq(1))).willReturn(mano);

        ResponseEntity<Mano> response = new ManoRestController(manoService, trucoService).putMethodName(1, mano);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mano.getApuesta(), response.getBody().getApuesta());
    }

    // Test para eliminar una mano
    @Test
    void testDeleteMano() {
        given(manoService.findManoById(1)).willReturn(mano);
        doNothing().when(manoService).deleteMano(1);

        ResponseEntity<MessageResponse> response = new ManoRestController(manoService, trucoService).delete(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Mano deleted!", response.getBody().getMessage());

        verify(manoService).deleteMano(1);
    }

    // Test para obtener una mano por jugador
    @Test
    void testFindManoByJugadorId() {
        given(manoService.findLastManoByJugadorId(1)).willReturn(mano);

        ResponseEntity<Mano> response = new ManoRestController(manoService, trucoService).findManoByJugadorId(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mano.getJugador().getId(), response.getBody().getJugador().getId());
    }

    // Test para obtener los trucos de una mano
    @Test
    void testFindTrucosByManoId() {
        Truco truco = new Truco();
        truco.setId(1);
        List<Truco> trucos = Arrays.asList(truco);

        given(trucoService.findTrucosByManoId(1)).willReturn(trucos);

        ResponseEntity<List<Truco>> response = new ManoRestController(manoService, trucoService).findTrucosByManoId(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
