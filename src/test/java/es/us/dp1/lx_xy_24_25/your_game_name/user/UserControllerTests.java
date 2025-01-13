package es.us.dp1.lx_xy_24_25.your_game_name.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;

/**
 * Test class for the {@link VetController}
 */
@WebMvcTest(controllers = UserRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class UserControllerTests {

	private static final int TEST_USER_ID = 1;
	private static final int TEST_AUTH_ID = 1;
	private static final String BASE_URL = "/api/v1/users";

	@SuppressWarnings("unused")
	@Autowired
	private UserRestController userController;

	@MockBean
	private UserService userService;

	@MockBean
	private AuthoritiesService authService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Authorities auth;
	private User user, logged;

	@BeforeEach
	void setup() {
		auth = new Authorities();
		auth.setId(TEST_AUTH_ID);
		auth.setAuthority("PLAYER");

		user = new User();
		user.setId(1);
		user.setUsername("user");
		user.setPassword("password");
		user.setAuthority(auth);

		when(this.userService.findCurrentUser()).thenReturn(getUserFromDetails(
				(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
	}

	private User getUserFromDetails(UserDetails details) {
		logged = new User();
		logged.setUsername(details.getUsername());
		logged.setPassword(details.getPassword());
		Authorities aux = new Authorities();
		for (GrantedAuthority auth : details.getAuthorities()) {
			aux.setAuthority(auth.getAuthority());
		}
		logged.setAuthority(aux);
		return logged;
	}

	@Test
	@WithMockUser("admin")
	void shouldFindAll() throws Exception {
		User player1 = new User();
		player1.setId(2);
		player1.setUsername("player1");

		User player2 = new User();
		player2.setId(3);
		player2.setUsername("player2");

		when(this.userService.findAll()).thenReturn(List.of(user, player1, player2));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$[?(@.id == 1)].username").value("user"))
				.andExpect(jsonPath("$[?(@.id == 2)].username").value("player1"))
				.andExpect(jsonPath("$[?(@.id == 3)].username").value("player2"));
	}

	@Test
	@WithMockUser("admin")
	void findAll_Vacia() throws Exception {
		when(this.userService.findAll()).thenReturn(List.of());

		mockMvc.perform(get(BASE_URL))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(0));

		verify(userService, times(1)).findAll();
	}


	@Test
	@WithMockUser("admin")
	void shouldFindAllWithAuthority() throws Exception {
		Authorities aux = new Authorities();
		aux.setId(2);
		aux.setAuthority("AUX");

		User player1 = new User();
		player1.setId(2);
		player1.setUsername("player1");
		player1.setAuthority(aux);

		User player2 = new User();
		player2.setId(3);
		player2.setUsername("player2");
		player2.setAuthority(auth);

		when(this.userService.findAllByAuthority(auth.getAuthority())).thenReturn(List.of(user, player2));

		mockMvc.perform(get(BASE_URL).param("auth", "PLAYER")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[?(@.id == 1)].username").value("user"))
				.andExpect(jsonPath("$[?(@.id == 3)].username").value("player2"));
	}

	@Test
	@WithMockUser("admin")
	void shouldFindAllAuths() throws Exception {
		Authorities aux = new Authorities();
		aux.setId(2);
		aux.setAuthority("AUX");

		when(this.authService.findAll()).thenReturn(List.of(auth, aux));

		mockMvc.perform(get(BASE_URL + "/authorities")).andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[?(@.id == 1)].authority").value("PLAYER"))
				.andExpect(jsonPath("$[?(@.id == 2)].authority").value("AUX"));
	}

	@Test
	@WithMockUser("admin")
	void findAllAuths_Vacia() throws Exception {
		when(this.authService.findAll()).thenReturn(List.of());

		mockMvc.perform(get(BASE_URL + "/authorities"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(0));

		verify(authService, times(1)).findAll();
	}


	@Test
	@WithMockUser("admin")
	void shouldReturnUser() throws Exception {
		when(this.userService.findUser(TEST_USER_ID)).thenReturn(user);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_USER_ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(TEST_USER_ID))
				.andExpect(jsonPath("$.username").value(user.getUsername()))
				.andExpect(jsonPath("$.authority.authority").value(user.getAuthority().getAuthority()));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundUser() throws Exception {
		when(this.userService.findUser(TEST_USER_ID)).thenThrow(ResourceNotFoundException.class);
		mockMvc.perform(get(BASE_URL + "/{id}", TEST_USER_ID)).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser("admin")
	void shouldCreateUser() throws Exception {
		User aux = new User();
		aux.setUsername("Prueba");
		aux.setPassword("Prueba");
		aux.setAuthority(auth);

		mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(aux))).andExpect(status().isCreated());
	}

	@Test
	@WithMockUser("admin")
	void shouldUpdateUser() throws Exception {
		user.setUsername("UPDATED");
		user.setPassword("CHANGED");

		when(this.userService.findUser(TEST_USER_ID)).thenReturn(user);
		when(this.userService.updateUser(any(User.class), any(Integer.class))).thenReturn(user);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user))).andExpect(status().isNoContent())
				.andExpect(jsonPath("$.username").value("UPDATED")).andExpect(jsonPath("$.password").value("CHANGED"));
	}

	@Test
	@WithMockUser("admin")
	void shouldReturnNotFoundUpdateUser() throws Exception {
		user.setUsername("UPDATED");
		user.setPassword("UPDATED");

		when(this.userService.findUser(TEST_USER_ID)).thenThrow(ResourceNotFoundException.class);
		when(this.userService.updateUser(any(User.class), any(Integer.class))).thenReturn(user);

		mockMvc.perform(put(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user))).andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser("admin")
	void shouldDeleteUser() throws Exception {
		when(this.userService.findUser(TEST_USER_ID)).thenReturn(user);

		mockMvc.perform(delete(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Usuario eliminado"));

		verify(userService, times(1)).findUser(TEST_USER_ID);
		verify(userService, times(1)).deleteUser(TEST_USER_ID);
	}

	@Test
	@WithMockUser("admin")
	void shouldDeleteUser_NotFound() throws Exception {
		when(this.userService.findUser(TEST_USER_ID)).thenThrow(ResourceNotFoundException.class);

		mockMvc.perform(delete(BASE_URL + "/{id}", TEST_USER_ID).with(csrf()))
				.andExpect(status().isNotFound());

		verify(userService, times(1)).findUser(TEST_USER_ID);
		verify(userService, never()).deleteUser(TEST_USER_ID);
	}

	@Test
	@WithMockUser("admin")
	void shouldGetUsersSortedByPoints() throws Exception {
		User player1 = new User();
		player1.setId(2);
		player1.setUsername("player1");
		player1.setNumPuntosGanados(150);
	
		User player2 = new User();
		player2.setId(3);
		player2.setUsername("player2");
		player2.setNumPuntosGanados(120);
	
		when(userService.getUsersSortedByPoints()).thenReturn(List.of(player1, player2));
	
		mockMvc.perform(get(BASE_URL + "/sorted-by-points"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].username").value("player1"))
				.andExpect(jsonPath("$[1].username").value("player2"));
	}

	@Test
	@WithMockUser("admin")
	void shouldGetUsersSortedByWinPercentage() throws Exception {
		User player1 = new User();
		player1.setId(2);
		player1.setUsername("player1");
		player1.setNumPartidasJugadas(10);
		player1.setNumPartidasGanadas(7);
		player1.setAuthority(auth);

		User player2 = new User();
		player2.setId(3);
		player2.setUsername("player2");
		player2.setNumPartidasJugadas(15);
		player2.setNumPartidasGanadas(9);
		player2.setAuthority(auth);

		when(userService.getUsersSortedByWinPercentage()).thenReturn(List.of(player2, player1));

		mockMvc.perform(get(BASE_URL + "/sorted-by-win-percentage"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].username").value("player2"))
				.andExpect(jsonPath("$[1].username").value("player1"));

		verify(userService, times(1)).getUsersSortedByWinPercentage();
	}

	// En caso de empate se devolverá la lista en el mismo orden en el que se paso
	// si queremos que al empatar se ordene de otra forma hay que especificarlo en el service
	@Test
	@WithMockUser("admin")
	void shouldGetUsersSortedByWinPercentage_ScoreIgual() throws Exception {
		User user1 = new User();
		user1.setId(2);
		user1.setUsername("User1");
		user1.setNumPartidasJugadas(10);
		user1.setNumPartidasGanadas(5);
		user1.setAuthority(auth);
	
		User user2 = new User();
		user2.setId(3);
		user2.setUsername("User2");
		user2.setNumPartidasJugadas(20);
		user2.setNumPartidasGanadas(10);
		user2.setAuthority(auth);
	
		when(userService.getUsersSortedByWinPercentage()).thenReturn(List.of(user1, user2));
	
		mockMvc.perform(get(BASE_URL + "/sorted-by-win-percentage"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].username").value("User1"))
				.andExpect(jsonPath("$[1].username").value("User2"));
	
		verify(userService, times(1)).getUsersSortedByWinPercentage();
	}
	

	@Test
	@WithMockUser("admin")
	void shouldGetUsersSortedByWinPercentage_Vacio() throws Exception {
		when(userService.findAll()).thenReturn(List.of());

		mockMvc.perform(get(BASE_URL + "/sorted-by-win-percentage"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(0));

		verify(userService, times(1)).getUsersSortedByWinPercentage();
	}


	@Test
	@WithMockUser("admin")
	void shouldAceptarORechazarSolicitud() throws Exception {
		user.setConectado(true);

		when(userService.conectarseODesconectarse(TEST_USER_ID, true)).thenReturn(user);

		mockMvc.perform(put(BASE_URL + "/conectarODesconectar/{userId}/{conectar}", TEST_USER_ID, true)
						.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.conectado").value(true));

		verify(userService, times(1)).conectarseODesconectarse(TEST_USER_ID, true);
	}

	@Test
	@WithMockUser("admin")
	void shouldAceptarORechazarSolicitud_NotFound() throws Exception {
		when(userService.conectarseODesconectarse(TEST_USER_ID, true)).thenThrow(ResourceNotFoundException.class);

		mockMvc.perform(put(BASE_URL + "/conectarODesconectar/{userId}/{conectar}", TEST_USER_ID, true)
						.with(csrf()))
				.andExpect(status().isNotFound());

		verify(userService, times(1)).conectarseODesconectarse(TEST_USER_ID, true);
	}

	@Test
	@WithMockUser("admin")
	void shouldFindCurrentUserProfile() throws Exception {
		when(userService.findCurrentUser()).thenReturn(user);

		mockMvc.perform(get(BASE_URL + "/current"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("user"))
				.andExpect(jsonPath("$.id").value(TEST_USER_ID));

		verify(userService, times(1)).findCurrentUser();
	}

	@Test
	@WithMockUser("admin")
	void shouldFindCurrentUserProfile_UserNotFound() throws Exception {
		when(userService.findCurrentUser())
				.thenThrow(new ResourceNotFoundException("User", "username", "admin100000"));

		mockMvc.perform(get(BASE_URL + "/current"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("User no encontrado con username: 'admin100000'"));

		verify(userService, times(1)).findCurrentUser();
	}


	@Test
	@WithMockUser("admin")
	void shouldGetJugadoresByCurrentUser() throws Exception {
		Jugador jugador = new Jugador();
		jugador.setId(1);
		jugador.setPuntos(100);

		Jugador jugador2 = new Jugador();
		jugador2.setId(2);
		jugador2.setPuntos(200);

		when(userService.getJugadoresByCurrentUser()).thenReturn(List.of(jugador, jugador2));

		mockMvc.perform(get(BASE_URL + "/current/jugadores"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(2))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].puntos").value(100))
				.andExpect(jsonPath("$[1].id").value(2))
				.andExpect(jsonPath("$[1].puntos").value(200));

		verify(userService, times(1)).getJugadoresByCurrentUser();
	}

}
