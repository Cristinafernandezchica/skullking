package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UsuarioPartidaEnJuegoEsperandoException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.exceptions.MaximoJugadoresPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.exceptions.UsuarioMultiplesJugadoresEnPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.util.EntityUtils;

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
