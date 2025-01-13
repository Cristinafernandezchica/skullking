package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import static org.junit.Assert.assertNull;
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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaEstado;

@ExtendWith(MockitoExtension.class)
public class BazaRepositoryTest {

    @Mock
    private BazaRepository bazaRepository;

    private Baza baza1;
    private Baza baza2;
    private Ronda ronda;
    private Jugador jugador;
    private Partida partida;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(12);
        jugador.setUsuario(null);
        jugador.setPartida(partida);

        partida = new Partida();
        partida.setEstado(PartidaEstado.JUGANDO);
        partida.setFin(null);
        partida.setId(5);
        partida.setInicio(LocalDateTime.now());
        partida.setNombre("Partida Test");
        partida.setOwnerPartida(1);

        ronda = new Ronda();
        ronda.setId(1);
        ronda.setEstado(RondaEstado.JUGANDO);
        ronda.setNumBazas(3);
        ronda.setNumRonda(4);
        ronda.setPartida(partida);

        baza1 = new Baza();
        baza1.setId(1);
        baza1.setPaloBaza(PaloBaza.morada);
        baza1.setNumBaza(3);
        baza1.setGanador(jugador);
        baza1.setTurnos(List.of());
        baza1.setRonda(ronda);

        baza2 = new Baza();
        baza2.setId(2);
        baza2.setPaloBaza(PaloBaza.verde);
        baza2.setNumBaza(4);
        baza2.setGanador(jugador);
        baza2.setTurnos(List.of());
        baza2.setRonda(ronda);
    }

    @Test
    public void shouldFindAll() {
        when(bazaRepository.findAll()).thenReturn(List.of(baza1, baza2));

        List<Baza> result = bazaRepository.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bazaRepository, times(1)).findAll();
    }

    @Test
    public void shouldFindBazasByRondaId() {
        Integer rondaId = 1;
        when(bazaRepository.findBazasByRondaId(rondaId)).thenReturn(List.of(baza1, baza2));

        List<Baza> result = bazaRepository.findBazasByRondaId(rondaId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(baza1, result.get(0));
        assertEquals(baza2, result.get(1));
        assertTrue(result.stream().allMatch(b -> b.getRonda().getId().equals(rondaId)));
        verify(bazaRepository, times(1)).findBazasByRondaId(rondaId);
    }

    @Test
    public void shouldFindByRondaIdAndNumBaza() {
        Integer rondaId = 1;
        Integer numBaza = 3;

        when(bazaRepository.findByRondaIdAndNumBaza(rondaId, numBaza)).thenReturn(Optional.of(baza1));

        Optional<Baza> result = bazaRepository.findByRondaIdAndNumBaza(rondaId, numBaza);

        assertTrue(result.isPresent());
        assertEquals(rondaId, result.get().getRonda().getId());
        assertEquals(numBaza, result.get().getNumBaza());
        verify(bazaRepository, times(1)).findByRondaIdAndNumBaza(rondaId, numBaza);
    }

    @Test
    public void shouldFindBazaAnterior() {
        Integer bazaId = 2;
        Integer rondaId = 1;

        when(bazaRepository.findBazaAnterior(bazaId, rondaId)).thenReturn(Optional.of(baza1));

        Optional<Baza> result = bazaRepository.findBazaAnterior(bazaId, rondaId);

        assertTrue(result.isPresent());
        assertEquals(baza1.getId(), result.get().getId());
        verify(bazaRepository, times(1)).findBazaAnterior(bazaId, rondaId);
    }

    @Test
    public void shouldFindByIdRondaAndIdJugador() {
        Integer rondaId = 1;
        Integer jugadorId = 1;

        when(bazaRepository.findByIdRondaAndIdJugador(rondaId, jugadorId)).thenReturn(List.of(baza1));

        List<Baza> result = bazaRepository.findByIdRondaAndIdJugador(rondaId, jugadorId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(jugadorId, result.get(0).getGanador().getId());
        verify(bazaRepository, times(1)).findByIdRondaAndIdJugador(rondaId, jugadorId);
    }

    @Test
    public void shouldDeleteByJugadorId() {
        Integer jugadorId = 1;

        bazaRepository.deleteByJugadorId(jugadorId);
        assertEquals(bazaRepository.findById(1), Optional.empty());
        assertEquals(bazaRepository.findById(2), Optional.empty());
        verify(bazaRepository, times(1)).deleteByJugadorId(jugadorId);
    }
}
