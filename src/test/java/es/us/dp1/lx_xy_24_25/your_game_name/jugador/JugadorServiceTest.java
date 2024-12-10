package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.exceptions.MaximoJugadoresPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.exceptions.UsuarioMultiplesJugadoresEnPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JugadorServiceTest {

    @Mock
    private JugadorRepository jugadorRepository;

    @Mock
    private PartidaRepository partidaRepository;

    @InjectMocks
    private JugadorService jugadorService;

    private Jugador jugador;
    private Partida partida;
    private User usuario;
	private Partida partida2;
	private Jugador jugador2;
	private User usuario2;
    private User usuario3;
    private Partida partida3;
    private List<Jugador> jugadoresEnPartida;

    private static final Integer MAX_JUGADORES = 8;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = new User();
        usuario.setId(1);
        usuario.setUsername("testuser");

        usuario2 = new User();
        usuario2.setId(2);
        usuario2.setUsername("testuser2");

        usuario3 = new User();
        usuario3.setId(3);
        usuario3.setUsername("testuser3");

        partida = new Partida();
        partida.setId(1);
        partida.setNombre("Test Partida");
        partida.setOwnerPartida(1);
        partida.setEstado(PartidaEstado.TERMINADA);

        partida2 = new Partida();
        partida2.setId(1);
        partida2.setNombre("Test Partida2");
        partida2.setOwnerPartida(1);
        partida2.setEstado(PartidaEstado.ESPERANDO);

        partida3 = new Partida();
        partida3.setId(3);
        partida3.setNombre("Test Partida3");
        partida3.setOwnerPartida(3);
        partida3.setEstado(PartidaEstado.ESPERANDO);

        jugador = new Jugador();
        jugador.setId(1);
        jugador.setUsuario(usuario);
        jugador.setPartida(partida2);

        jugador2 = new Jugador();
        jugador2.setId(2);
        jugador2.setUsuario(usuario2);
        jugador2.setPartida(partida2);

        jugadoresEnPartida = new ArrayList<>();
        jugadoresEnPartida.add(jugador);
    }

    @Test
    void shouldFindById() {
        when(jugadorRepository.findById(jugador.getId())).thenReturn(java.util.Optional.ofNullable(jugador));

        Jugador resultado = jugadorService.findById(jugador.getId());
        assertNotNull(resultado);
        assertEquals(jugador.getId(), resultado.getId());
    }


    // Test para saveJugador: usuario ya en partida
    @Test
    void shouldSaveJugador_UsuarioEnPartida() {
        when(jugadorRepository.findJugadoresByPartidaId(partida.getId())).thenReturn(jugadoresEnPartida);

        assertThrows(UsuarioMultiplesJugadoresEnPartidaException.class, () -> jugadorService.saveJugador(jugador));
    }

    // Test para saveJugador: partida en juego o en espera
    @Test
    void shouldSaveJugador_PartidaEnJuegoOEspera() {
        when(partidaRepository.findAll()).thenReturn(List.of(partida2)); // Partida en juego
        when(jugadorRepository.findJugadoresByPartidaId(partida2.getId())).thenReturn(List.of(jugador));

        assertThrows(UsuarioMultiplesJugadoresEnPartidaException.class, () -> jugadorService.saveJugador(jugador));
    }

    // Test para jugadorEnPartida: usuario en la misma partida
    @Test
    void shouldJugadorEnPartida_UsuarioEnMismaPartida() {
        boolean resultado = jugadorService.jugadorEnPartida(jugadoresEnPartida, jugador);
        assertTrue(resultado);
    }

    // Test para jugadorEnPartida: usuario no en la partida
    @Test
    void shouldJugadorEnPartida_UsuarioNoEnPartida() {
        jugadoresEnPartida.clear();
        boolean resultado = jugadorService.jugadorEnPartida(jugadoresEnPartida, jugador);
        assertFalse(resultado);
    }

    // Test para partidaEnJuegoEspera: usuario en partida activa
    @Test
    void shouldPartidaEnJuegoEspera_UsuarioEnPartidaActiva() {
        when(partidaRepository.findAll()).thenReturn(List.of(partida2)); // Partida en juego
        when(jugadorRepository.findJugadoresByPartidaId(partida2.getId())).thenReturn(List.of(jugador));

        boolean resultado = jugadorService.partidaEnJuegoEspera(jugador);
        assertTrue(resultado);
    }

    // Test para partidaEnJuegoEspera: usuario sin partida activa
    @Test
    void shouldPartidaEnJuegoEspera_UsuarioSinPartidaActiva() {
        when(partidaRepository.findAll()).thenReturn(new ArrayList<>()); // No hay partidas

        boolean resultado = jugadorService.partidaEnJuegoEspera(jugador);
        assertFalse(resultado);
    }

    @Test
    void shouldFindAll() {
        List<Jugador> jugadores = List.of(jugador);
        when(jugadorRepository.findAll()).thenReturn(jugadores);

        Iterable<Jugador> resultado = jugadorService.findAll();

        assertNotNull(resultado);
        assertEquals(1, ((List<Jugador>) resultado).size());
        verify(jugadorRepository, times(1)).findAll();
    }

    @Test
    void shouldFindJugadoresByPartidaId() {
        when(jugadorRepository.findJugadoresByPartidaId(partida.getId())).thenReturn(jugadoresEnPartida);

        List<Jugador> resultado = jugadorService.findJugadoresByPartidaId(partida.getId());

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(jugador.getId(), resultado.get(0).getId());
        verify(jugadorRepository, times(1)).findJugadoresByPartidaId(partida.getId());
    }

    @Test
    void shouldDeleteJugador() {
        doNothing().when(jugadorRepository).deleteById(jugador.getId());

        jugadorService.deleteJugador(jugador.getId());

        verify(jugadorRepository, times(1)).deleteById(jugador.getId());
    }

    @Test
    void shouldUpdateJugador() {
        when(jugadorRepository.findById(jugador.getId())).thenReturn(java.util.Optional.of(jugador));
        when(jugadorRepository.save(any(Jugador.class))).thenReturn(jugador);

        Jugador jugadorActualizado = new Jugador();
        jugadorActualizado.setUsuario(usuario);
        jugadorActualizado.setPartida(partida);

        Jugador resultado = jugadorService.updateJugador(jugadorActualizado, jugador.getId());

        assertNotNull(resultado);
        assertEquals(jugador.getId(), resultado.getId());
        verify(jugadorRepository, times(1)).save(any(Jugador.class));
    }

    @Test
    void shouldFindJugadorByUsuarioId() {
        List<Jugador> jugadores = List.of(jugador);
        when(jugadorRepository.findJugadoresByUsuarioId(usuario.getId())).thenReturn(jugadores);

        Jugador resultado = jugadorService.findJugadorByUsuarioId(usuario.getId());

        assertNotNull(resultado);
        assertEquals(jugador.getId(), resultado.getId());
        verify(jugadorRepository, times(1)).findJugadoresByUsuarioId(usuario.getId());
    }



    // Test para findPartidaByUsuarioId
    @Test
    void shouldFindPartidaByUsuarioId() {
        when(jugadorRepository.findJugadoresByUsuarioId(usuario.getId())).thenReturn(jugadoresEnPartida);

        Partida resultado = jugadorService.findPartidaByUsuarioId(usuario.getId());
        assertNotNull(resultado);
        assertEquals(partida.getId(), resultado.getId());
    }

    
}
