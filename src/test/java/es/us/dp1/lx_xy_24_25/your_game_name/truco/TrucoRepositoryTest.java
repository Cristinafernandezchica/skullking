package es.us.dp1.lx_xy_24_25.your_game_name.truco;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TrucoRepositoryTest {

    @Mock
    private TrucoRepository trucoRepository;

    private Truco truco1;
    private Truco truco2;
    private Baza baza;
    private Jugador jugador;
    private Mano mano;
    private Carta carta1;
    private Carta carta2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        baza = new Baza();
        baza.setId(1);

        jugador = new Jugador();
        jugador.setId(1);

        mano = new Mano();
        mano.setId(1);
        mano.setJugador(jugador);

        carta1 = new Carta();
        carta1.setId(1);

        carta2 = new Carta();
        carta2.setId(2);

        truco1 = new Truco();
        truco1.setId(1);
        truco1.setBaza(baza);
        truco1.setJugador(jugador);
        truco1.setMano(mano);
        truco1.setCarta(carta1);
        truco1.setTurno(1);

        truco2 = new Truco();
        truco2.setId(2);
        truco2.setBaza(baza);
        truco2.setJugador(jugador);
        truco2.setMano(mano);
        truco2.setCarta(carta2);
        truco2.setTurno(2);
    }

    @Test
    void shouldFindTrucosByBazaId() {
        List<Truco> trucos = List.of(truco1, truco2);
        when(trucoRepository.findTrucosByBazaId(1)).thenReturn(trucos);

        List<Truco> result = trucoRepository.findTrucosByBazaId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(truco1));
        assertTrue(result.contains(truco2));
        verify(trucoRepository, times(1)).findTrucosByBazaId(1);
    }

    @Test
    void shouldFindCartaIdByBazaId() {
        List<Carta> cartas = List.of(carta1, carta2);
        when(trucoRepository.findCartaIdByBazaId(1)).thenReturn(cartas);

        List<Carta> result = trucoRepository.findCartaIdByBazaId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(carta1));
        assertTrue(result.contains(carta2));
        verify(trucoRepository, times(1)).findCartaIdByBazaId(1);
    }

    @Test
    void shouldFindByJugadorId() {
        List<Truco> trucos = List.of(truco1, truco2);
        when(trucoRepository.findByJugadorId(1)).thenReturn(trucos);

        List<Truco> result = trucoRepository.findByJugadorId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(truco1));
        assertTrue(result.contains(truco2));
        verify(trucoRepository, times(1)).findByJugadorId(1);
    }

    @Test
    void shouldFindByManoId() {
        List<Truco> trucos = List.of(truco1, truco2);
        when(trucoRepository.findByManoId(1)).thenReturn(trucos);

        List<Truco> result = trucoRepository.findByManoId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(truco1));
        assertTrue(result.contains(truco2));
        verify(trucoRepository, times(1)).findByManoId(1);
    }

    @Test
    public void shouldDeleteByJugadorId() {
        Integer jugadorId = jugador.getId();

        trucoRepository.deleteByJugadorId(jugadorId);
        assertEquals(trucoRepository.findById(1), Optional.empty());
        assertEquals(trucoRepository.findById(2), Optional.empty());
        verify(trucoRepository, times(1)).deleteByJugadorId(jugadorId);
    }
}
