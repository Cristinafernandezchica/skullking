package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaRestController;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;

@WebMvcTest(controllers = RondaRestController.class)
public class RondaControllerTest {

    private static final String BASE_URL = "/api/v1/rondas";
    private static final int TEST_RONDA_ID = 1;


    @Autowired
	private RondaRestController rc;

	@MockBean
	private RondaService rs;

    @MockBean
    private PartidaService partidaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Ronda ronda1;
    private Ronda ronda2;
    private Partida partida;
    private Baza baza;
    private Baza bazaV;

    @Autowired
	private MockMvc mockMvc;

    @BeforeEach
    void setUp(){

        ronda2 = new Ronda();
        ronda2.setId(2);
        ronda2.setEstado(RondaEstado.JUGANDO);
        ronda2.setNumBazas(4);
        ronda2.setNumRonda(4);
        ronda2.setPartida(partida);

        ronda1 = new Ronda();
        ronda1.setId(1);
        ronda1.setEstado(RondaEstado.FINALIZADA);
        ronda1.setNumBazas(3);
        ronda1.setNumRonda(3);
        ronda1.setPartida(partida);

        // Configuración de la entidad Baza
        baza = new Baza();
        baza.setId(1);
        baza.setTipoCarta(TipoCarta.morada);
        baza.setNumBaza(3);

        // Configuración de la entidad Baza
        bazaV = new Baza();
        bazaV.setId(2);
        bazaV.setTipoCarta(TipoCarta.verde);
        bazaV.setNumBaza(4);

    }

    @Test
    @WithMockUser("player")
    void shoudlFindAll() throws Exception {
        when(rs.getAllRondas()).thenReturn(List.of(ronda1, ronda2));

        mockMvc.perform(get(BASE_URL)) 
            .andExpect(status().isOk()) 
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[?(@.id == 2)].numRonda").value(4)) 
            .andExpect(jsonPath("$[?(@.id == 2)].numBazas").value(4)) 
            .andExpect(jsonPath("$[?(@.id == 2)].estado").value(RondaEstado.JUGANDO.toString())) 
            .andExpect(jsonPath("$[?(@.id == 1)].numRonda").value(3)) 
            .andExpect(jsonPath("$[?(@.id == 1)].numBazas").value(3)) 
            .andExpect(jsonPath("$[?(@.id == 1)].estado").value(RondaEstado.FINALIZADA.toString()));
    }

    @Test
    @WithMockUser("player")
    void shouldGetRondaById() throws Exception {
        when(rs.getRondaById(TEST_RONDA_ID)).thenReturn(ronda1);

        mockMvc.perform(get(BASE_URL + "/{id}", TEST_RONDA_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id == 1)].numRonda").value(3)) 
            .andExpect(jsonPath("$[?(@.id == 1)].numBazas").value(3)) 
            .andExpect(jsonPath("$[?(@.id == 1)].estado").value(RondaEstado.FINALIZADA.toString()));
    }

    @Test
    @WithMockUser("player")
    void shouldReturnNotFounRonda() throws Exception {
        when(rs.getRondaById(TEST_RONDA_ID)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(BASE_URL + "/{id}", TEST_RONDA_ID))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("player")
    void shouldCreateRonda() throws Exception {
        Ronda newRonda = new Ronda();
        newRonda.setEstado(RondaEstado.JUGANDO);
        newRonda.setNumBazas(4);
        newRonda.setNumRonda(4);
        newRonda.setPartida(partida);

        when(rs.save(any(Ronda.class))).thenReturn(newRonda);

        mockMvc.perform(post(BASE_URL).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newRonda)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numRonda").value(4));
    }

    @Test
@WithMockUser("player")
void shouldUpdateRonda() throws Exception {
    Ronda updatedRonda = new Ronda();
    updatedRonda.setEstado(RondaEstado.FINALIZADA);
    updatedRonda.setNumBazas(4);
    updatedRonda.setNumRonda(4);
    updatedRonda.setPartida(partida);

    when(rs.getRondaById(2)).thenReturn(ronda2);
    when(rs.save(any(Ronda.class))).thenReturn(updatedRonda);

    mockMvc.perform(put(BASE_URL + "/{id}", 2).with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updatedRonda)))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$.message").value("Ronda actualizada"));

}


    @Test
    @WithMockUser("player")
    void shouldDeleteRonda() throws Exception {
        when(rs.getRondaById(2)).thenReturn(ronda2);
        doNothing().when(rs).delete(2);

        mockMvc.perform(delete(BASE_URL + "/{id}", 2).with(csrf()))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.message").value("Ronda eliminada"));
    }

    @Test
    void testNextBaza_Success() {
        // Arrange
        Integer bazaId = baza.getId();

        when(rs.nextBaza(bazaId)).thenReturn(bazaV);

        ResponseEntity<Baza> response = rc.nextBaza(bazaId);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(bazaV, response.getBody());
        verify(rs).nextBaza(bazaId);
    }

    @Test
    void testNextBaza_ErrorHandling() {
        Integer bazaId = bazaV.getId();
        when(rs.nextBaza(bazaId)).thenThrow(new RuntimeException("Error al calcular la próxima baza"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rc.nextBaza(bazaId);
        });

        // Assert
        assertNotNull(exception);
        assertEquals("Error al calcular la próxima baza", exception.getMessage());
        verify(rs).nextBaza(bazaId);
    }

}
