package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.CartaService;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManoServiceTest {

    @Mock
    private ManoRepository manoRepository;

    @Mock
    private CartaService cartaService;

    @Mock
    private JugadorService jugadorService;

    @InjectMocks
    private ManoService manoService;

    private Jugador jugador;
    private Ronda ronda;
    private List<Carta> cartas;
    private Partida partida;
    private Carta carta;

    @BeforeEach
    public void setUp() {
        jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(12);
        jugador.setTurno(1);
        jugador.setUsuario(null);
        jugador.setPartida(partida);

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

        carta = new Carta();
        carta.setId(1);
        carta.setImagenFrontal("./images/cartas/morada_1.png");
        carta.setImagenTrasera("./images/cartas/parte_trasera.png");
        carta.setNumero(1);
        carta.setTipoCarta(TipoCarta.morada);
        cartas = List.of(carta);

    }

    @Test
    public void testSaveMano() {
        Mano mano = new Mano();
        when(manoRepository.save(mano)).thenReturn(mano);

        Mano savedMano = manoService.saveMano(mano);
        assertNotNull(savedMano);
        verify(manoRepository, times(1)).save(mano);
    }

    @Test
    public void testFindAll() {
        List<Mano> manos = List.of(new Mano(), new Mano());
        when(manoRepository.findAll()).thenReturn(manos);

        Iterable<Mano> result = manoService.findAll();
        assertNotNull(result);
        assertEquals(2, ((List<Mano>) result).size());
        verify(manoRepository, times(1)).findAll();
    }

    @Test
    public void testFindManoById() {
        Mano mano = new Mano();
        mano.setId(1);
        when(manoRepository.findById(1)).thenReturn(Optional.of(mano));

        Mano foundMano = manoService.findManoById(1);
        assertNotNull(foundMano);
        assertEquals(1, foundMano.getId());
        verify(manoRepository, times(1)).findById(1);
    }

    @Test
    public void testFindManoByIdNotFound() {
        when(manoRepository.findById(1)).thenReturn(Optional.empty());

        Mano foundMano = manoService.findManoById(1);
        assertNull(foundMano);
        verify(manoRepository, times(1)).findById(1);
    }

    @Test
    public void testDeleteMano() {
        manoService.deleteMano(1);
        verify(manoRepository, times(1)).deleteById(1);
    }

    @Test
    public void testUpdateMano() {
        Mano existingMano = new Mano();
        existingMano.setId(1);
        existingMano.setApuesta(3);

        Mano updatedMano = new Mano();
        updatedMano.setApuesta(5);

        when(manoRepository.findById(1)).thenReturn(Optional.of(existingMano));
        when(manoRepository.save(existingMano)).thenReturn(existingMano);

        Mano result = manoService.updateMano(updatedMano, 1);
        assertNotNull(result);
        assertEquals(5, result.getApuesta());
        verify(manoRepository, times(1)).save(existingMano);
    }


/*
    @Test
    public void testIniciarManos() {
        List<Jugador> jugadores = List.of(jugador);
        when(cartaService.findAll()).thenReturn(cartas);
        when(jugadorService.findJugadoresByPartidaId(any())).thenReturn(jugadores);

        manoService.iniciarManos(1, ronda);

        verify(manoRepository, times(1)).save(any(Mano.class));
    }
*/
    @Test
    public void testApostar() {
        Mano mano = new Mano();
        mano.setId(1);
        mano.setApuesta(null);

        when(manoRepository.findById(1)).thenReturn(Optional.of(mano));

        manoService.apostar(5, 1);

        assertEquals(5, mano.getApuesta());
        verify(manoRepository, times(1)).save(mano);
    }
/*
    @Test
    public void testCalculoPuntaje() {
        Mano mano1 = new Mano();
        mano1.setJugador(jugador);
        mano1.setApuesta(2);
        mano1.setResultado(2);

        Mano mano2 = new Mano();
        Jugador jugador2 = new Jugador();
        jugador2.setId(2);
        jugador2.setTurno(1);
        mano2.setJugador(jugador2);
        mano2.setApuesta(1);
        mano2.setResultado(0);

        List<Mano> manos = List.of(mano1, mano2);

        when(manoRepository.findAllByRondaId(1)).thenReturn(manos);

        manoService.calculoPuntaje(1, 1);

        assertEquals(40, jugador.getPuntos()); // (20 * 2)
        assertEquals(-10, jugador2.getPuntos()); // (-10 * 1)
    }
*/
    @Test
    public void testFindLastManoByJugadorId() {
        List<Mano> manos = new ArrayList<>();
        Mano mano1 = new Mano();
        mano1.setId(1);
        manos.add(mano1);
        Mano mano2 = new Mano();
        mano2.setId(2);
        manos.add(mano2);

        when(manoRepository.findAllManoByJugadorId(1)).thenReturn(manos);

        Mano lastMano = manoService.findLastManoByJugadorId(1);
        assertNotNull(lastMano);
        assertEquals(2, lastMano.getId());
    }
}
