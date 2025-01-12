package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

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
        jugador.setUsuario(null);
        jugador.setPartida(partida);

        carta = new Carta();
        carta.setId(1);
        carta.setImagenFrontal("./images/cartas/morada_1.png");
        carta.setNumero(1);
        carta.setTipoCarta(TipoCarta.morada);

        mano = new Mano();
        mano.setApuesta(1);
        mano.setCartas(List.of(carta));
        mano.setId(1);
        mano.setJugador(jugador);
        mano.setResultado(5);
        mano.setRonda(ronda);

        ronda = new Ronda();
        ronda.setId(1);
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
    void shouldFindAll() {
        List<Mano> manos = Arrays.asList(mano);
        given(manoService.findAll()).willReturn(manos);

        ResponseEntity<List<Mano>> response = new ManoRestController(manoService, trucoService).findAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    // Test para crear una mano
    @Test
    void shouldCreate() {
        given(manoService.saveMano(any(Mano.class))).willReturn(mano);

        ResponseEntity<Mano> response = new ManoRestController(manoService, trucoService).create(mano);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mano.getId(), response.getBody().getId());
    }

    // Test para actualizar una mano
    @Test
    void shouldUpdateMano() {
        given(manoService.findManoById(1)).willReturn(mano);
        given(manoService.updateMano(any(Mano.class), eq(1))).willReturn(mano);

        ResponseEntity<Mano> response = new ManoRestController(manoService, trucoService).update(1, mano);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mano.getApuesta(), response.getBody().getApuesta());
    }

    // Test para eliminar una mano
    @Test
    void shouldDeleteMano() {
        given(manoService.findManoById(1)).willReturn(mano);
        doNothing().when(manoService).deleteMano(1);

        ResponseEntity<MessageResponse> response = new ManoRestController(manoService, trucoService).delete(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Mano eliminada!", response.getBody().getMessage());

        verify(manoService).deleteMano(1);
    }

    // Test para obtener una mano por jugador
    @Test
    void shouldFindLastManoByJugadorId() {
        given(manoService.findLastManoByJugadorId(1)).willReturn(mano);

        ResponseEntity<Mano> response = new ManoRestController(manoService, trucoService).findLastManoByJugadorId(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mano.getJugador().getId(), response.getBody().getJugador().getId());
    }

    // Test para obtener los trucos de una mano
    @Test
    void shouldFindTrucosByManoId() {
        Truco truco = new Truco();
        truco.setId(1);
        List<Truco> trucos = Arrays.asList(truco);

        given(trucoService.findTrucosByManoId(1)).willReturn(trucos);

        ResponseEntity<List<Truco>> response = new ManoRestController(manoService, trucoService).findTrucosByManoId(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldCartasDisabled_SuccessNonEmpty() {
        Carta carta1 = new Carta();
        carta1.setId(1);
        carta1.setNumero(5);
        carta1.setTipoCarta(TipoCarta.morada);

        Carta carta2 = new Carta();
        carta2.setId(2);
        carta2.setNumero(7);
        carta2.setTipoCarta(TipoCarta.morada);

        List<Carta> cartas = Arrays.asList(carta1, carta2);
        given(manoService.cartasDisabled(1, TipoCarta.morada)).willReturn(cartas);

        ResponseEntity<List<Carta>> response = new ManoRestController(manoService, trucoService).cartasDisabled(1,
                TipoCarta.morada);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(1, response.getBody().get(0).getId());
        assertEquals(2, response.getBody().get(1).getId());
    }

    @Test
    void shouldCartasDisabled_SuccessEmpty() {
        given(manoService.cartasDisabled(1, TipoCarta.morada)).willReturn(new ArrayList<>());

        ResponseEntity<List<Carta>> response = new ManoRestController(manoService, trucoService).cartasDisabled(1,
                TipoCarta.morada);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void shouldCartasDisabled_ManoNotFound() {
        given(manoService.cartasDisabled(99, TipoCarta.morada))
                .willThrow(new NoSuchElementException("Mano no encontrada"));

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> new ManoRestController(manoService, trucoService).cartasDisabled(99, TipoCarta.morada));

        assertEquals("Mano no encontrada", exception.getMessage());
    }

    @Test
    void shouldCartasDisabled_InvalidTipoCarta() {
        given(manoService.cartasDisabled(1, null)).willReturn(new ArrayList<>());

        ResponseEntity<List<Carta>> response = new ManoRestController(manoService, trucoService).cartasDisabled(1,
                null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void shouldCartasDisabled_ServiceError() {
        given(manoService.cartasDisabled(1, TipoCarta.morada)).willThrow(new RuntimeException("Error interno"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> new ManoRestController(manoService, trucoService).cartasDisabled(1, TipoCarta.morada));

        assertEquals("Error interno", exception.getMessage());
    }

    @Test
    void shouldCartasDisabled_NullResponse() {
        given(manoService.cartasDisabled(1, TipoCarta.morada)).willReturn(null);

        ResponseEntity<List<Carta>> response = new ManoRestController(manoService, trucoService).cartasDisabled(1,
                TipoCarta.morada);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size()); // Verificamos que devuelva una lista vacía
    }

    @Test
    void shouldFindManoByRondaId_Success() {
        List<Mano> manos = Arrays.asList(mano);
        given(manoService.findAllManosByRondaId(1)).willReturn(manos);

        ResponseEntity<List<Mano>> response = new ManoRestController(manoService, trucoService).findManoByRondaId(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(mano.getId(), response.getBody().get(0).getId());
        assertEquals(mano.getApuesta(), response.getBody().get(0).getApuesta());
    }

    @Test
    void shouldFindManoByRondaId_NoManosFound() {
        given(manoService.findAllManosByRondaId(1)).willReturn(new ArrayList<>());

        ResponseEntity<List<Mano>> response = new ManoRestController(manoService, trucoService).findManoByRondaId(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void shouldFindManoByRondaId_RondaNotFound() {
        given(manoService.findAllManosByRondaId(99)).willThrow(new NoSuchElementException("Ronda no encontrada"));

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> new ManoRestController(manoService, trucoService).findManoByRondaId(99));

        assertEquals("Ronda no encontrada", exception.getMessage());
    }

    @Test
    void shouldFindManoByRondaId_InternalServerError() {
        given(manoService.findAllManosByRondaId(1)).willThrow(new RuntimeException("Error interno"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> new ManoRestController(manoService, trucoService).findManoByRondaId(1));

        assertEquals("Error interno", exception.getMessage());
    }

}
