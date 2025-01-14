package es.us.dp1.lx_xy_24_25.your_game_name.partida;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UsuarioPartidaEnJuegoEsperandoException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoRestController;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaRestController;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
@WebMvcTest(controllers = PartidaRestController.class)
public class PartidaControllerTest {

    private static final int TEST_PARTIDA_ID = 1;
    private static final int TEST_JUGADOR_ID = 1;
    private static final String BASE_URL = "/api/v1/partidas";

    @SuppressWarnings("unused")
    @Autowired
    private PartidaRestController partidaController;

    @MockBean
    private PartidaService partidaService;

    @MockBean
    private JugadorService jugadorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private Partida partida;
    private User user;
    private Jugador jugadorGanador;

    @BeforeEach
    void setup() {
        partida = new Partida();
        partida.setId(TEST_PARTIDA_ID);
        partida.setNombre("Partida Test");
        partida.setEstado(PartidaEstado.ESPERANDO);
        partida.setOwnerPartida(1);

        jugadorGanador = new Jugador();
        jugadorGanador.setId(TEST_JUGADOR_ID);
        jugadorGanador.setPuntos(150);
        jugadorGanador.setPartida(partida);

        user = new User();
        user.setId(1);
        user.setUsername("testUser");
    }

    @Test
    @WithMockUser("admin")
    void shouldGetAllPartidas() throws Exception {
        Partida partida1 = new Partida();
        partida1.setId(2);
        partida1.setNombre("Partida 1");

        Partida partida2 = new Partida();
        partida2.setId(3);
        partida2.setNombre("Partida 2");

        when(partidaService.getAllPartidas(null, null)).thenReturn(List.of(partida, partida1, partida2));

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(3))
            .andExpect(jsonPath("$[?(@.id == 1)].nombre").value("Partida Test"))
            .andExpect(jsonPath("$[?(@.id == 2)].nombre").value("Partida 1"))
            .andExpect(jsonPath("$[?(@.id == 3)].nombre").value("Partida 2"));
    }


    @Test
    @WithMockUser("admin")
    void shouldGetPartidaById() throws Exception {
        when(partidaService.getPartidaById(TEST_PARTIDA_ID)).thenReturn(partida);

        mockMvc.perform(get(BASE_URL + "/{id}", TEST_PARTIDA_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(TEST_PARTIDA_ID))
            .andExpect(jsonPath("$.nombre").value(partida.getNombre()));
    }

    @Test
    @WithMockUser("admin")
    void shouldReturnNotFoundPartida() throws Exception {
        when(partidaService.getPartidaById(TEST_PARTIDA_ID)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(BASE_URL + "/{id}", TEST_PARTIDA_ID))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("player")
    void shouldCreatePartida() throws Exception {
        Partida newPartida = new Partida();
        newPartida.setNombre("Nueva Partida");
        newPartida.setInicio(LocalDateTime.now());
        newPartida.setOwnerPartida(1);
        newPartida.setEstado(PartidaEstado.ESPERANDO);
    
        when(partidaService.save(any(Partida.class))).thenReturn(newPartida);
    
        mockMvc.perform(post(BASE_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPartida)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Nueva Partida"));
    
        verify(partidaService, times(1)).save(any(Partida.class));
    }

    @Test
    @WithMockUser("admin")
    void shouldUpdatePartida() throws Exception {
        partida.setInicio(LocalDateTime.now());
        partida.setEstado(PartidaEstado.ESPERANDO);

        when(partidaService.getPartidaById(TEST_PARTIDA_ID)).thenReturn(partida);
        when(partidaService.update(any(Partida.class), eq(TEST_PARTIDA_ID))).thenReturn(partida);

        mockMvc.perform(put(BASE_URL + "/{id}", TEST_PARTIDA_ID).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partida)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value(partida.getNombre()))
                .andExpect(jsonPath("$.estado").value(partida.getEstado().toString()))
                .andExpect(jsonPath("$.ownerPartida").value(partida.getOwnerPartida()));
    }



    @Test
    @WithMockUser("admin")
    void shouldDeletePartida() throws Exception {
        when(partidaService.getPartidaById(TEST_PARTIDA_ID)).thenReturn(partida);
        doNothing().when(partidaService).delete(TEST_PARTIDA_ID);

        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_PARTIDA_ID).with(csrf()))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.message").value("Partida eliminada"));
    }

    @Test
    @WithMockUser("admin")
    void shouldActualizarOwner() throws Exception {
        Map<String, Integer> body = new HashMap<>();
        body.put("ownerPartida", 2);

        doNothing().when(partidaService).actualizarOwner(1, 2);

        mockMvc.perform(put("/api/v1/partidas/1/actualizar-owner").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Owner actualizado con éxito."));
    }

    @Test
    @WithMockUser("admin")
    void shouldActualizarOwner_NotFound() throws Exception {
        Map<String, Integer> body = new HashMap<>();
        body.put("ownerPartida", 2);

        doThrow(new NoSuchElementException()).when(partidaService).actualizarOwner(1, 2);

        mockMvc.perform(put("/api/v1/partidas/1/actualizar-owner").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Partida no encontrada."));
    }

    @Test
    @WithMockUser("admin")
    void shouldActualizarOwner_BadRequest() throws Exception {
        Map<String, Integer> body = new HashMap<>();

        doThrow(new IllegalArgumentException("Owner inválido")).when(partidaService).actualizarOwner(1, null);

        mockMvc.perform(put("/api/v1/partidas/1/actualizar-owner").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Owner inválido"));
    }

    @Test
    @WithMockUser("admin")
    void shouldGetJugadoresByPartidaId() throws Exception {
        Jugador jugador1 = new Jugador();
        jugador1.setId(1);
        jugador1.setPuntos(100);

        Jugador jugador2 = new Jugador();
        jugador2.setId(2);
        jugador2.setPuntos(200);

        when(jugadorService.findJugadoresByPartidaId(TEST_PARTIDA_ID)).thenReturn(List.of(jugador1, jugador2));

        mockMvc.perform(get(BASE_URL + "/{id}/jugadores", TEST_PARTIDA_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[?(@.id == 1)].puntos").value(100))
            .andExpect(jsonPath("$[?(@.id == 2)].puntos").value(200));
    }

    @Test
    @WithMockUser("admin")
    void shouldFindPartidasByOwnerId() throws Exception {
        when(partidaService.findPartidasByOwnerId(1)).thenReturn(List.of(partida));

        mockMvc.perform(get("/api/v1/partidas").param("ownerId", "1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldFindPartidasByOwnerId_NotFound() throws Exception {
        when(partidaService.findPartidasByOwnerId(1)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/partidas").param("ownerId", "1").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("admin")
    void shouldGanadorPartida() throws Exception {
        when(partidaService.getJugadorGanador(TEST_PARTIDA_ID)).thenReturn(jugadorGanador);

        mockMvc.perform(get(BASE_URL + "/{id}/jugadorGanador", TEST_PARTIDA_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(TEST_JUGADOR_ID))
            .andExpect(jsonPath("$.puntos").value(150));
    }

    @Test
    @WithMockUser("admin")
    void shouldIniciarPartida() throws Exception {
        doNothing().when(partidaService).iniciarPartida(TEST_PARTIDA_ID);

        mockMvc.perform(put(BASE_URL + "/{id}/iniciar-partida", TEST_PARTIDA_ID).with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin")
    void shouldSiguienteEstado() throws Exception {
        doNothing().when(partidaService).siguienteEstado(1, 1);

        mockMvc.perform(post("/api/v1/partidas/1/bazas/1/siguiente-estado").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin")
    void shouldApostar() throws Exception {
        doNothing().when(partidaService).apuesta(5, 1);

        mockMvc.perform(put("/api/v1/partidas/apuesta/1").with(csrf())
                        .param("apuesta", "5"))
                .andExpect(status().isOk());

    }
}
