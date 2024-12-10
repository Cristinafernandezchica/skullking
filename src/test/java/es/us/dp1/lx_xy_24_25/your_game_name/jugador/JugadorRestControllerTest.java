package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@WebMvcTest(controllers = JugadorRestController.class)
public class JugadorRestControllerTest {

    private static final String BASE_URL = "/api/v1/jugadores";

    @Autowired
    private JugadorRestController jugadorController;

    @MockBean
    private JugadorService jugadorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private User user;
    private User user2;
    private Jugador jugador;
    private Jugador jugador2;
    private Partida partida;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1);
        user.setUsername("testUser");

        user2 = new User();
        user2.setId(2);
        user2.setUsername("testUser2");

        partida = new Partida();
        partida.setId(1);
        partida.setNombre("Partida Test");
        partida.setOwnerPartida(1);
        partida.setEstado(PartidaEstado.ESPERANDO);
        
        jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(100);
        jugador.setUsuario(user);
        jugador.setPartida(partida);
        
        jugador2 = new Jugador();
        jugador2.setId(2);
        jugador2.setPuntos(120);
        jugador2.setUsuario(user2);
        jugador2.setPartida(partida);

    }

    @Test
    @WithMockUser("player")
    void shouldFindJugadoresByPartidaId() throws Exception {
        List<Jugador> jugadores = List.of(jugador,jugador2);

        when(jugadorService.findJugadoresByPartidaId(any(Integer.class))).thenReturn(jugadores);

        mockMvc.perform(get(BASE_URL+"/1")).andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].usuario.username").value("testUser"))
        .andExpect(jsonPath("$.[1].usuario.username").value("testUser2"));
    }

    @Test
    @WithMockUser("player")
    void shouldFindJugadorByUsuarioId() throws Exception {
        when(jugadorService.findJugadorByUsuarioId(any(Integer.class))).thenReturn(jugador);

        mockMvc.perform(get(BASE_URL+"/1/usuario")).andExpect(status().isOk())
        .andExpect(jsonPath("$.usuario.username").value("testUser"));
    }

    @Test
    @WithMockUser("player")
    void shouldFindAllJugadores() throws Exception {
        
        List<Jugador> jugadores = List.of(jugador,jugador2);
        when(jugadorService.findAll()).thenReturn(jugadores);

        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].usuario.username").value("testUser"))
        .andExpect(jsonPath("$.[1].usuario.username").value("testUser2"));
    }

    @Test
    @WithMockUser("player")
    void shouldUpdateJugador() throws Exception {

        when(jugadorService.findById(any(Integer.class))).thenReturn(jugador);
        when(jugadorService.updateJugador(any(Jugador.class), any(Integer.class))).thenReturn(jugador);


        mockMvc.perform(put(BASE_URL + "/{id}",1)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jugador)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puntos").value(100));
    }

    @Test
    @WithMockUser("player")
    void shouldCreateJugador() throws Exception {

        User user3 = new User();
        user3.setId(3);
        user3.setUsername("testUser3");

        Jugador jugador3 = new Jugador();
        jugador3.setId(3);
        jugador3.setPuntos(120);
        jugador3.setUsuario(user3);
    
        when(jugadorService.saveJugador(any(Jugador.class))).thenReturn(jugador3);

        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jugador3)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.puntos").value(120));
    }

    @Test
    @WithMockUser("player")
    void shouldDeleteJugador() throws Exception {
        when(jugadorService.findById(any(Integer.class))).thenReturn(jugador);

        mockMvc.perform(delete(BASE_URL + "/{id}",1)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("player")
    void shouldFindPartidaByUsuarioId() throws Exception {
        int usuarioId = 123;

        // Simular el comportamiento del servicio
        when(jugadorService.findPartidaByUsuarioId(usuarioId)).thenReturn(partida);

        // Realizar la solicitud GET y validar la respuesta
        mockMvc.perform(get("/api/v1/jugadores/{usuarioId}/partida", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(partida.getId()))
                .andExpect(jsonPath("$.nombre").value(partida.getNombre()));

        // Verificar que el servicio fue invocado
        verify(jugadorService, times(2)).findPartidaByUsuarioId(usuarioId);
    }

    @Test
    @WithMockUser("player")
    void shouldFindPartidaByUsuarioId_NotFound() throws Exception {
        int usuarioId = 999;

        // Simular el comportamiento cuando no se encuentra la partida
        when(jugadorService.findPartidaByUsuarioId(usuarioId)).thenReturn(null);

        // Realizar la solicitud GET y validar que lanza un error 404
        mockMvc.perform(get("/api/v1/jugadores/{usuarioId}/partida", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Verificar que el servicio fue invocado
        verify(jugadorService, times(1)).findPartidaByUsuarioId(usuarioId);
    }

}
