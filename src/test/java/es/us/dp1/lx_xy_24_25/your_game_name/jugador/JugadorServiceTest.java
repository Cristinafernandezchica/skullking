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



}
    @Test
	void shouldFindAllJugadoresByPartidaId() {
        List<Jugador> jugadoresDeLaPartida= List.of(jugador2,jugador);
        when(jugadorRepository.findJugadoresByPartidaId(any(Integer.class))).thenReturn(jugadoresDeLaPartida);
		List<Jugador> jugadores = jugadorService.findJugadoresByPartidaId(3);

		assertEquals(jugador2, jugadores.get(0));
		assertEquals(jugador, jugadores.get(1));
	}

    @Test
	void shouldSaveAJugadorWithcorrect() {
		// Asegúrate de que el nombre del mock es el correcto
		when(jugadorRepository.save(jugador2)).thenReturn(jugador2);
		Jugador jugador = jugadorService.saveJugador(jugador2);  // Asegúrate de pasar el jugador correctamente
		assertEquals(jugador2, jugador);
	}




	@Test
	void  shouldNotFindAJugadorThatUserDoesntExists() {
        assertThrows(ResourceNotFoundException.class, ()-> jugadorService.findJugadorByUsuarioId(90));
    }

	@Test
	void shouldFindAJugadorWithAExistingUser() {
        List<Jugador> jugadoresMock = List.of(jugador);
        when(jugadorRepository.findJugadoresByUsuarioId(any(Integer.class))).thenReturn(jugadoresMock);
		Jugador jugadorATestear = jugadorService.findJugadorByUsuarioId(1);
		assertEquals(jugador, jugadorATestear);
    }

    @Test
    void shouldSaveJugadorSuccessfully() {
        List<Jugador> jugadoresPartida = List.of(jugador);

        when(jugadorRepository.findJugadoresByPartidaId(partida2.getId())).thenReturn(jugadoresPartida);
        when(jugadorRepository.save(jugador2)).thenReturn(jugador2);

        Jugador savedJugador = jugadorService.saveJugador(jugador2);
        assertNotNull(savedJugador);
        assertEquals(jugador2, savedJugador);
    }

    @Test
    void shouldNotSaveJugadorWhenPartidaHasMaxPlayers() {
        List<Jugador> jugadoresPartida = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Jugador j = new Jugador();
            j.setId(i);
            j.setPartida(partida);
            jugadoresPartida.add(j);
        }

        when(jugadorRepository.findJugadoresByPartidaId(partida.getId())).thenReturn(jugadoresPartida);

        Jugador newJugador = new Jugador();
        newJugador.setUsuario(usuario3);
        newJugador.setPartida(partida);

        assertThrows(MaximoJugadoresPartidaException.class, () -> jugadorService.saveJugador(newJugador));
    }

    @Test
    void shouldNotSaveJugadorWhenUserAlreadyInPartida() {
        List<Jugador> jugadoresPartida = List.of(jugador, jugador2);

        when(jugadorRepository.findJugadoresByPartidaId(partida2.getId())).thenReturn(jugadoresPartida);

        Jugador newJugador = new Jugador();
        newJugador.setUsuario(usuario);
        newJugador.setPartida(partida2);

        assertThrows(UsuarioMultiplesJugadoresEnPartidaException.class, () -> jugadorService.saveJugador(newJugador));
    }

    /*
    @Test
    void shouldNotSaveJugadorWhenUserInActivePartida() {
        List<Partida> partidas = List.of(partida, partida2); // Partida activa

        when(jugadorRepository.findJugadoresByUsuarioId(usuario.getId())).thenReturn(List.of(jugador));
        // when(partidaRepository.findByOwnerPartidaAndEstado(anyInt(), anyList())).thenReturn(partidas);

        Jugador newJugador = new Jugador();
        newJugador.setId(3);
        newJugador.setUsuario(usuario);
        newJugador.setPartida(partida3);

        assertThrows(UsuarioPartidaEnJuegoEsperandoException.class, () -> jugadorService.saveJugador(newJugador));
    }
    */
    
    /*
    @Test
    public void testSaveJugador_UsuarioYaTieneJugadorEnPartidaEnJuego() {
        // Simulamos que el usuario ya está en una partida en estado "ESPERANDO" (partida2)
        when(jugadorRepository.findJugadoresByUsuarioId(usuario.getId()))
                .thenReturn(Arrays.asList(jugador)); // El usuario ya tiene un jugador en la partida

        // Intentar guardar el nuevo jugador en una nueva partida (partida3)
        // Debe lanzar la excepción UsuarioPartidaEnJuegoEsperandoException
        assertThrows(UsuarioPartidaEnJuegoEsperandoException.class, () -> {
            jugadorService.saveJugador(nuevoJugador); // Se intenta añadir el jugador a una nueva partida
        });
    }
    */


        /*
    @Test
    void shouldnotSave2JugadorWithTheSameUser() {

        Jugador jugador3 = new Jugador();
        jugador.setId(3);
        jugador.setUsuario(usuario);
        jugador.setPartida(partida2);

        List<Jugador> jugadores = List.of(jugador);
        Iterable<Jugador> jugadoresIterable =jugadores;
        when(jugadorRepository.findAll()).thenReturn(jugadoresIterable);
        assertThrows(UsuarioMultiplesJugadoresEnPartidaException.class, ()-> jugadorService.saveJugador(jugador2));
    }
    */

        /* 
    @Test
	void shouldNotSaveAJugadorThatPartidaHave8player() {
        Jugador jugador3 = new Jugador();
        jugador.setId(3);
        jugador.setUsuario(usuario3);
        jugador.setPartida(partida2);
        List<Jugador> jugadores = List.of(jugador,jugador,jugador,jugador,jugador,jugador,jugador,jugador);
        when(jugadorRepository.findJugadoresByPartidaId(any(Integer.class))).thenReturn(jugadores);
        when(jugadorRepository.save(any(Jugador.class))).thenReturn(jugador3);
		assertThrows(MaximoJugadoresPartidaException.class, ()->jugadorService.saveJugador(jugador3));
	}
*/
}
