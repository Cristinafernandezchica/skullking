package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UsuarioPartidaEnJuegoEsperandoException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.exceptions.MaximoJugadoresPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.exceptions.UsuarioMultiplesJugadoresEnPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.util.EntityUtils;

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

    @BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);

    usuario = new User();
    usuario.setId(1);
    usuario.setUsername("testuser");

	usuario2 = new User();
    usuario2.setId(2);
    usuario2.setUsername("testuser2");

    partida = new Partida();
    partida.setId(1);
    partida.setNombre("Test Partida");
    partida.setOwnerPartida(1);
    partida.setEstado(PartidaEstado.TERMINADA);

    partida2 = new Partida();
    partida2.setId(2);
    partida2.setNombre("Test Partida2");
    partida2.setOwnerPartida(1);
    partida2.setEstado(PartidaEstado.ESPERANDO);

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
		List<Jugador> jugadores = this.jugadorService.findJugadoresByPartidaId(3);

		Jugador jugador1 = EntityUtils.getById(jugadores, Jugador.class, 1);
		Jugador jugador2 = EntityUtils.getById(jugadores, Jugador.class, 2);
		Jugador jugador3 = EntityUtils.getById(jugadores, Jugador.class, 3);
		assertEquals("player2", jugador1.getUsuario().getUsername());
		assertEquals("player3", jugador2.getUsuario().getUsername());
		assertEquals("player1", jugador3.getUsuario().getUsername());
	}

	@Test
	void  shouldNotFindAJugadorThatUserDoesntExists() {
        assertThrows(ResourceNotFoundException.class, ()-> jugadorService.findJugadorByUsuarioId(90));
    }

	@Test
	void shouldFindAJugadorWithAExistingUser() {
		Jugador jugador = jugadorService.findJugadorByUsuarioId(6);
		assertEquals("player3", jugador.getUsuario().getUsername());
    }
	
@Test
	void shouldSaveAJugadorWithcorrect() {
		// Asegúrate de que el nombre del mock es el correcto
		when(jugadorRepository.save(jugador2)).thenReturn(jugador2);
		Jugador jugador = jugadorService.saveJugador(jugador2);  // Asegúrate de pasar el jugador correctamente
		assertEquals(jugador2, jugador);
	}

}
