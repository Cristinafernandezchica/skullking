package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.util.EntityUtils;
import jakarta.validation.ConstraintViolationException;

@SpringBootTest
@AutoConfigureTestDatabase
public class JugadorServiceTest {

    @Autowired
    protected JugadorService jugadorService;

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
    void getPlayerByNonExistentUserId() {

			assertThrows(ResourceNotFoundException.class, () -> {
				jugadorService.findJugadorByUsuarioId(90);
			});
	}
}
