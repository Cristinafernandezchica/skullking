package es.us.dp1.lx_xy_24_25.your_game_name.partida;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

import java.time.LocalDateTime;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UsuarioPartidaEnJuegoEsperandoException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
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
    void shouldIniciarPartida() throws Exception {
        doNothing().when(partidaService).iniciarPartida(TEST_PARTIDA_ID);

        mockMvc.perform(put(BASE_URL + "/{id}/iniciar-partida", TEST_PARTIDA_ID).with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin")
    void shouldGetJugadorGanador() throws Exception {
        when(partidaService.getJugadorGanador(TEST_PARTIDA_ID)).thenReturn(jugadorGanador);

        mockMvc.perform(get(BASE_URL + "/{id}/jugadorGanador", TEST_PARTIDA_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(TEST_JUGADOR_ID))
            .andExpect(jsonPath("$.puntos").value(150));
    }


}
/*
 // COMENTO TEST APUESTA PORQUE SE HA MOVIDO A Partida
    /*
    @Test
    void shouldApuesta_ValidInput() {
        // Configurar mocks
        doNothing().when(manoService).apuesta(10, 1);

        // Llamar al método del controlador
        ResponseEntity<Void> response = new ManoRestController(manoService, trucoService).apuesta(10, 1);

        // Verificar
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(manoService).apuesta(10, 1);
    }

    @Test
    void shouldApuesta_NullApuesta() {
        // Llamar al método con apuesta nula y capturar excepción
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ManoRestController(manoService, trucoService).apuesta(null, 1)
        );

        // Verificar el mensaje de excepción
        assertEquals("Apuesta no puede ser nula", exception.getMessage());
        verify(manoService, Mockito.never()).apuesta(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void shouldApuesta_NegativeApuesta() {
        // Simular un error al llamar al servicio con una apuesta negativa
        doThrow(new IllegalArgumentException("Apuesta debe ser un número positivo"))
            .when(manoService).apuesta(-5, 1);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ManoRestController(manoService, trucoService).apuesta(-5, 1)
        );

        // Verificar el mensaje de excepción
        assertEquals("Apuesta debe ser un número positivo", exception.getMessage());
        verify(manoService).apuesta(-5, 1);
    }

    @Test
    void shouldApuesta_JugadorNotFound() {
        // Simular el caso donde el jugador no existe
        doThrow(new NoSuchElementException("Jugador no encontrado"))
            .when(manoService).apuesta(10, 99);

        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> new ManoRestController(manoService, trucoService).apuesta(10, 99)
        );

        // Verificar el mensaje de excepción
        assertEquals("Jugador no encontrado", exception.getMessage());
        verify(manoService).apuesta(10, 99);
    }

    @Test
    void shouldApuesta_ServiceError() {
        // Simular un error genérico del servicio
        doThrow(new RuntimeException("Error interno del servicio"))
            .when(manoService).apuesta(10, 1);

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> new ManoRestController(manoService, trucoService).apuesta(10, 1)
        );

        // Verificar el mensaje de excepción
        assertEquals("Error interno del servicio", exception.getMessage());
        verify(manoService).apuesta(10, 1);
    }
    
 */