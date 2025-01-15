package es.us.dp1.lx_xy_24_25.your_game_name.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;

@SpringBootTest
@AutoConfigureTestDatabase
class UserServiceTests {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthoritiesService authService;

	@Test
	void shouldSaveUser() {
		int cantUsers = ((List<User>) this.userService.findAll()).size();

		User user = new User();
		user.setUsername("Sam");
		user.setPassword("password");
		user.setAuthority(authService.findByAuthority("ADMIN"));

		this.userService.saveUser(user);
		assertNotNull(user.getId());

		int cantUsersFinal = ((List<User>) this.userService.findAll()).size();
		assertEquals(cantUsers + 1, cantUsersFinal);
	}

	@Test
	@WithMockUser(username = "player1", password = "0wn3r")
	void shouldFindCurrentUser() {
		User user = this.userService.findCurrentUser();
		assertEquals("player1", user.getUsername());
	}

	@Test
	@WithMockUser(username = "prueba")
	void shouldNotFindCorrectCurrentUser() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findCurrentUser());
	}

	@Test
	void shouldNotFindAuthenticated() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findCurrentUser());
	}

	@Test
	void shouldFindAllUsers() {
		List<User> users = (List<User>) this.userService.findAll();
		assertEquals(17, users.size());
	}

	@Test
	void shouldFindUsersByUsername() {
		User user = this.userService.findUser("player1");
		assertEquals("player1", user.getUsername());
	}

	@Test
    void shouldNotFindUserByIncorrectUsername() {
        assertThrows(ResourceNotFoundException.class, () -> userService.findUser("userNotExists"));
    }

	@Test
	void shouldFindSingleUser() {
		User user = this.userService.findUser(4);
		assertEquals("player1", user.getUsername());
	}

	@Test
	void shouldNotFindSingleUserWithBadID() {
		assertThrows(ResourceNotFoundException.class, () -> this.userService.findUser(10000));
	}

	@Test
	void shouldExistUser() {
		assertEquals(true, this.userService.existsUser("player1"));
	}

	@Test
	void shouldNotExistUser() {
		assertEquals(false, this.userService.existsUser("player10000"));
	}

	@Test
	void shouldFindUsersByAuthority() {
		List<User> players = (List<User>) userService.findAllByAuthority("PLAYER");
        assertEquals(16, players.size());

        List<User> admins = (List<User>) userService.findAllByAuthority("ADMIN");
        assertEquals(1, admins.size());
		}

	@Test
    void shouldFallarAlInsertarUserSinUsername() {
        User user = new User();
        user.setPassword("password");
        assertThrows(Exception.class, () -> userService.saveUser(user));
    }

	@Test
	void shouldUpdateUser() {
		int idToUpdate = 1;
		String newName="Change";
		User user = this.userService.findUser(idToUpdate);
		user.setUsername(newName);
		userService.updateUser(user, idToUpdate);
		user = this.userService.findUser(idToUpdate);
		assertEquals(newName, user.getUsername());
	}

	@Test
    void shouldNotUpdateNoUserExistente() {
        User user = new User();
        user.setUsername("player222");
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(user, 999));
    }

	@Test
    void shouldDeleteUser() {
        User user = new User();
        user.setUsername("player222");
        user.setPassword("password");
        user.setAuthority(authService.findByAuthority("PLAYER"));
        userService.saveUser(user);

        userService.deleteUser(user.getId());
        assertThrows(ResourceNotFoundException.class, () -> userService.findUser(user.getId()));
    }

	@Test
    void shouldNotDeleteNonExistingUser() {
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(999));
    }

	@Test
    void shouldGetUsersSortedByPoints() {
        List<User> users = userService.getUsersSortedByPoints();
        assertTrue(users.get(0).getNumPuntosGanados() >= users.get(1).getNumPuntosGanados());
    }

	@Test
    void shouldGetUsersSortedByWinPercentage() {
        List<User> users = userService.getUsersSortedByWinPercentage();
        assertTrue(users.get(0).getNumPartidasGanadas() >= users.get(1).getNumPartidasGanadas());
    }

	@Test
    void shouldConectarseODesconectarse() {
        User user = userService.findUser(1);
        userService.conectarseODesconectarse(user.getId(), true);
        assertTrue(userService.findUser(1).getConectado());

        userService.conectarseODesconectarse(user.getId(), false);
        assertFalse(userService.findUser(1).getConectado());
    }

	@Test
    void shouldNoConectarseODesconectarse_NotFound() {
        assertThrows(ResourceNotFoundException.class, () -> userService.conectarseODesconectarse(999, true));
    }

	@Test
    @WithMockUser(username = "player1")
    void shouldGetJugadoresByCurrentUser() {
        List<Jugador> jugadores = userService.getJugadoresByCurrentUser();
        assertNotNull(jugadores);
    }

	@Test
    void shouldNotGetJugadoresSinAutenticacion() {
        assertThrows(ResourceNotFoundException.class, () -> userService.getJugadoresByCurrentUser());
    }

	@Test
	void shouldGetTiempoPartidasForAllUsers() {
		List<Long> tiempos = userService.getTiempoPartidas(null);
		assertNotNull(tiempos);
		assertTrue(tiempos.size() > 0); // Como hay usuarios con partidas en estado TERMINADA ebe haber al menos una partida
		assertTrue(tiempos.stream().allMatch(t -> t >= 0)); // Los tiempos deben ser no negativos
	}

	@Test
	void shouldGetTiempoPartidasForUser() {
		Integer userId = 5; // ID de un usuario con partidas con estado TERMINADA
		List<Long> tiempos = userService.getTiempoPartidas(userId);
		assertNotNull(tiempos);
		assertTrue(tiempos.size() > 0); // Debe haber al menos una partida
		assertTrue(tiempos.stream().allMatch(t -> t >= 0)); // Los tiempos deben ser no negativos
	}

	@Test 
	void shouldNotGetTiempoPartidasForUser() {
		Integer userId = 13; // ID de un usuario sin partidas con estado TERMINADA
		List<Long> tiempos = userService.getTiempoPartidas(userId);
		assertNotNull(tiempos);
		assertTrue(tiempos.isEmpty()); // La lista debe estar vacía
	}

	@Test
	void shouldGetPromedioTiempoPartidasForAllUsers() {
		Double promedio = userService.getPromedioTiempoPartidas(null);
		assertNotNull(promedio);
		assertTrue(promedio > 0); // Como hay usuarios con partidas en estado TERMINADA, el promedio debe ser mayor que 0
	}

	@Test
	void shouldGetPromedioTiempoPartidasForUser() {
		Integer userId = 5; // ID de un usuario con partidas con estado TERMINADA
		Double promedio = userService.getPromedioTiempoPartidas(userId);
		assertNotNull(promedio);
		assertTrue(promedio > 0); // El promedio debe ser mayor que 0
	}

	@Test
	void shouldNotGetPromedioTiempoPartidasForUser() {
		Integer userId = 13; // ID de un usuario sin partidas con estado TERMINADA
		Double promedio = userService.getPromedioTiempoPartidas(userId);
		assertNotNull(promedio);
		assertEquals(0.0, promedio); // El promedio debe ser 0.0
	}

	@Test
	void shouldGetMaxTiempoPartidasForAllUsers() {
		Integer max = userService.getMaxTiempoPartidas(null);
		assertNotNull(max);
		assertTrue(max > 0); // Como hay usuarios con partidas en estado TERMINADA, el máximo debe ser mayor que 0
	}

	@Test
	void shouldGetMaxTiempoPartidasForUser() {
		Integer userId = 5; // ID de un usuario con partidas con estado TERMINADA
		Integer max = userService.getMaxTiempoPartidas(userId);
		assertNotNull(max);
		assertTrue(max > 0); // El máximo debe ser mayor que 0
	}

	@Test
	void shouldNotGetMaxTiempoPartidasForUser() {
		Integer userId = 13; // ID de un usuario sin partidas con estado TERMINADA
		Integer max = userService.getMaxTiempoPartidas(userId);
		assertNotNull(max);
		assertEquals(0, max); // El máximo debe ser 0
	}

	@Test
	void shouldGetMinTiempoPartidasForAllUsers() {
		Integer min = userService.getMinTiempoPartidas(null);
		assertNotNull(min);
		assertTrue(min > 0); // Como hay usuarios con partidas en estado TERMINADA, el mínimo debe ser mayor que 0
	}

	@Test
	void shouldGetMinTiempoPartidasForUser() {
		Integer userId = 5; // ID de un usuario con partidas con estado TERMINADA
		Integer min = userService.getMinTiempoPartidas(userId);
		assertNotNull(min);
		assertTrue(min > 0); // El mínimo debe ser mayor que 0
	}

	@Test
	void shouldNotGetMinTiempoPartidasForUser() {
		Integer userId = 13; // ID de un usuario sin partidas con estado TERMINADA
		Integer min = userService.getMinTiempoPartidas(userId);
		assertNotNull(min);
		assertEquals(0, min); // El mínimo debe ser 0
	}

	@Test
	void shouldGetMaxMayorIgualMinTiempo() {
		Integer max = userService.getMaxTiempoPartidas(null);
		Integer min = userService.getMinTiempoPartidas(null);
		
		assertNotNull(max);
		assertNotNull(min);
		assertTrue(max >= min, "El valor máximo debe ser mayor o igual al mínimo");
	}

	@Test
	void shouldGetTotalTiempoPartidasForAllUsers() {
		Integer total = userService.getTotalTiempoPartidas(null);
		assertNotNull(total);
		assertTrue(total > 0); // Como hay usuarios con partidas en estado TERMINADA, el total debe ser mayor que 0
	}

	@Test
	void shouldGetTotalTiempoPartidasForUser() {
		Integer userId = 5; // ID de un usuario con partidas con estado TERMINADA
		Integer total = userService.getTotalTiempoPartidas(userId);
		assertNotNull(total);
		assertTrue(total > 0); // El total debe ser mayor que 0
	}

	@Test
	void shouldNotGetTotalTiempoPartidasForUser() {
		Integer userId = 13; // ID de un usuario sin partidas con estado TERMINADA
		Integer total = userService.getTotalTiempoPartidas(userId);
		assertNotNull(total);
		assertEquals(0, total); // El total debe ser 0
	}

	@Test
	void shouldGetNumPartidas() {
		List<Integer> numPartidas = userService.getNumPartidas();
		assertNotNull(numPartidas);
		assertTrue(numPartidas.size() > 0); // Debe haber al menos un usuario, ya que hay usuarios guardados en la base de datos
		assertTrue(numPartidas.stream().allMatch(n -> n >= 0)); // El número de partidas de cada usuario debe ser no negativo
	}

	@Test
	void shouldGetPromedioPartidas() {
		Double promedio = userService.getPromedioPartidas();
		assertNotNull(promedio);
		assertTrue(promedio > 0); // El promedio debe ser mayor que 0, porque hay al menos un usuario con partidas jugadas en la base de datos
	}

	@Test
	void shouldGetMaxPartidas() {
		Integer max = userService.getMaxPartidas();
		assertNotNull(max);
		assertTrue(max > 0); // El máximo debe ser mayor que 0, porque hay al menos un usuario con partidas jugadas en la base de datos
	}

	@Test
	void shouldGetMinPartidas() {
		Integer min = userService.getMinPartidas();
		assertNotNull(min);
		assertEquals(0, min); // El mínimo debe ser 0, porque hay al menos un usuario sin partidas jugadas en la base de datos
	}

	@Test
	void shouldGetMaxMayorIgualMinPartidas() {
		Integer max = userService.getMaxPartidas();
		Integer min = userService.getMinPartidas();
		
		assertNotNull(max);
		assertNotNull(min);
		assertTrue(max >= min, "El valor máximo debe ser mayor o igual al mínimo");
	}

}
