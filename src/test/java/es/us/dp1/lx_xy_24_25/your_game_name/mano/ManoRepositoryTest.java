package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;


@ExtendWith(MockitoExtension.class)
public class ManoRepositoryTest {

    @Mock
    private ManoRepository manoRepository;

    private Mano mano1;
    private Mano mano2;
    private Jugador jugador;
    private Ronda ronda;
    private Carta carta;
    private Carta carta2;
    private Partida partida;

    @BeforeEach
    void setUp() {
        partida = new Partida();
        partida.setEstado(PartidaEstado.JUGANDO);
        partida.setFin(LocalDateTime.now());
        partida.setId(5);
        partida.setInicio(LocalDateTime.now());
        partida.setNombre("Partida Test");
        partida.setOwnerPartida(1);

        jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(12);
        jugador.setPartida(partida);

        ronda = new Ronda();
        ronda.setId(1);
        ronda.setEstado(RondaEstado.JUGANDO);
        ronda.setNumBazas(4);
        ronda.setNumRonda(1);
        ronda.setPartida(partida);

        carta = new Carta();
        carta.setId(1);
        carta.setImagenFrontal("./images/cartas/morada_1.png");
        carta.setNumero(1);
        carta.setTipoCarta(TipoCarta.morada);

        carta2 = new Carta();
        carta2.setId(2);
        carta2.setImagenFrontal("./images/cartas/morada_2.png");
        carta2.setNumero(2);
        carta2.setTipoCarta(TipoCarta.morada);

        mano1 = new Mano();
        mano1.setApuesta(1);
        mano1.setCartas(List.of(carta));
        mano1.setId(1);
        mano1.setJugador(jugador);
        mano1.setResultado(5);
        mano1.setRonda(ronda);

        mano2 = new Mano();
        mano2.setApuesta(0);
        mano2.setCartas(List.of(carta2));
        mano2.setId(2);
        mano2.setJugador(jugador);
        mano2.setResultado(10);
        mano2.setRonda(ronda);
    }

    @Test
    public void shouldFindAllManoByJugadorId() {
        Integer jugadorId = jugador.getId();
        when(manoRepository.findAllManoByJugadorId(jugadorId)).thenReturn(List.of(mano1, mano2));

        List<Mano> result = manoRepository.findAllManoByJugadorId(jugadorId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> m.getJugador().getId().equals(jugadorId)));
        verify(manoRepository, times(1)).findAllManoByJugadorId(jugadorId);
    }

    @Test
    public void shouldFindAllByRondaId() {
        Integer rondaId = ronda.getId();
        when(manoRepository.findAllByRondaId(rondaId)).thenReturn(List.of(mano1, mano2));

        List<Mano> result = manoRepository.findAllByRondaId(rondaId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> m.getRonda().getId().equals(rondaId)));
        verify(manoRepository, times(1)).findAllByRondaId(rondaId);
    }

    @Test
    public void shouldDeleteByJugadorId() {
        Integer jugadorId = jugador.getId();

        manoRepository.deleteByJugadorId(jugadorId);
        assertEquals(manoRepository.findById(1), Optional.empty());
        assertEquals(manoRepository.findById(2), Optional.empty());
        verify(manoRepository, times(1)).deleteByJugadorId(jugadorId);
    }
}
