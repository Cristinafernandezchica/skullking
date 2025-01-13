package es.us.dp1.lx_xy_24_25.your_game_name.truco;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.PaloBaza;
import es.us.dp1.lx_xy_24_25.your_game_name.bazaCartaManoDTO.BazaCartaManoDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;

@WebMvcTest(controllers = TrucoRestController.class)
@WithMockUser
public class TrucoRestControllerTest {

    private static final String BASE_URL = "/api/v1/trucos";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrucoService trucoService;

    @Autowired
    private ObjectMapper objectMapper;

    
    private Truco truco;
    private Truco truco2;
    private Baza baza;
    private Jugador jugador;
    private Mano mano;
    private Carta carta;

    @BeforeEach
    void setup() {
        carta = new Carta();
        carta.setId(10);
        carta.setNumero(1);
        carta.setTipoCarta(TipoCarta.morada);

        baza = new Baza();
        baza.setId(1);
        baza.setNumBaza(1);
        baza.setPaloBaza(PaloBaza.sinDeterminar);

        mano = new Mano();
        mano.setId(5);
        mano.setCartas(List.of(carta));

        jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(100);

        truco = new Truco();
        truco.setId(1);
        truco.setBaza(baza);
        truco.setMano(mano);
        truco.setTurno(1);
        truco.setCarta(carta);
        truco.setJugador(jugador);
 
        truco2 = new Truco();
        truco2.setId(2);
        truco2.setTurno(2);
        truco2.setBaza(baza);

        baza = new Baza();
        baza.setId(1);
        baza.setPaloBaza(PaloBaza.morada);
        baza.setNumBaza(3);

    }

    @Test
    void shouldFindAllTrucos() throws Exception {
        List<Truco> trucos = List.of(truco, truco2);
        when(trucoService.findAllTrucos()).thenReturn(trucos);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].turno").value("1"));

        verify(trucoService, times(1)).findAllTrucos();
    }

    @Test
	void shouldFindAllTrucos_Vacia() throws Exception {
		when(trucoService.findAllTrucos()).thenReturn(List.of());

		mockMvc.perform(get(BASE_URL))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(0));

		verify(trucoService, times(1)).findAllTrucos();
	}

    @Test
    void shouldFindTrucoById() throws Exception {
        when(trucoService.findTrucoById(1)).thenReturn(truco);

        mockMvc.perform(get(BASE_URL + "/{trucoId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.turno").value("1"));

        verify(trucoService, times(1)).findTrucoById(1);
    }

    @Test
    void shouldFindTrucoById_NotFound() throws Exception {
        when(trucoService.findTrucoById(1)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(BASE_URL + "/{trucoId}", 1))
                .andExpect(status().isNotFound());

        verify(trucoService, times(1)).findTrucoById(1);
    }

    @Test
    void shouldCreateTruco() throws Exception {
        when(trucoService.saveTruco(any(Truco.class))).thenReturn(truco);

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(truco)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.turno").value("1"));

        verify(trucoService, times(1)).saveTruco(any(Truco.class));
    }

    @Test
    void shouldUpdateTruco() throws Exception {
        when(trucoService.findTrucoById(1)).thenReturn(truco);
        when(trucoService.updateTruco(new Truco(), 1)).thenReturn(truco);

        mockMvc.perform(put(BASE_URL + "/{trucoId}", 1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(truco)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.turno").value("1"));

        verify(trucoService, times(1)).findTrucoById(1);
        verify(trucoService, times(1)).updateTruco(new Truco(), 1);
    }

    @Test
    void shouldDeleteTruco() throws Exception {
        when(trucoService.findTrucoById(1)).thenReturn(truco);
        doNothing().when(trucoService).deleteTruco(1);

        mockMvc.perform(delete(BASE_URL + "/{trucoId}", 1)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Truco eliminado!"));

        verify(trucoService, times(1)).findTrucoById(1);
        verify(trucoService, times(1)).deleteTruco(1);
    }

    @Test
    void shouldFindTrucosByBazaId() throws Exception {
        List<Truco> trucos = List.of(truco, truco2);
        when(trucoService.findTrucosByBazaId(1)).thenReturn(trucos);

        mockMvc.perform(get(BASE_URL + "/trucosBaza/{bazaId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].turno").value("1"));

        verify(trucoService, times(1)).findTrucosByBazaId(1);
    }

    @Test
    void shouldJugarTruco() throws Exception {
        BazaCartaManoDTO dto = new BazaCartaManoDTO();
        dto.setBaza(baza);
        dto.setMano(mano);
        dto.setTurno(1);
        dto.setCarta(carta);

        when(trucoService.jugarTruco(any(BazaCartaManoDTO.class), eq(1))).thenReturn(truco);

        mockMvc.perform(post(BASE_URL + "/{jugadorId}/jugar", 1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.turno").value(1))
                .andExpect(jsonPath("$.baza.id").value(1))
                .andExpect(jsonPath("$.mano.id").value(5))
                .andExpect(jsonPath("$.carta.id").value(10))
                .andExpect(jsonPath("$.jugador.id").value(1));

        verify(trucoService, times(1)).jugarTruco(any(BazaCartaManoDTO.class), eq(1));
    }

    @Test
    void shouldJugarTruco_JugadorIdInvalido() throws Exception {
        BazaCartaManoDTO dto = new BazaCartaManoDTO();
        dto.setBaza(baza);
        dto.setMano(mano);
        dto.setTurno(1);
        dto.setCarta(carta);

        doThrow(new ResourceNotFoundException("Jugador", "id", 99))
                .when(trucoService).jugarTruco(any(BazaCartaManoDTO.class), eq(99));

        mockMvc.perform(post(BASE_URL + "/{jugadorId}/jugar", 99)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());

        verify(trucoService, times(1)).jugarTruco(any(BazaCartaManoDTO.class), eq(99));
    }
}
