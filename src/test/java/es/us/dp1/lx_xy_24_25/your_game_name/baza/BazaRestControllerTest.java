package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoService;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.SecurityConfiguration;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaRestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.CsrfRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(controllers = BazaRestController.class)
@ActiveProfiles("test") // Usa un perfil de configuración de prueba
@AutoConfigureMockMvc(addFilters = false) // Deshabilita los filtros de seguridad
public class BazaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BazaRestController bazaRestController;

    @MockBean
    private BazaService bazaService; // Usar @MockBean en lugar de @Mock

    @MockBean
    private TrucoService trucoService; // Usar @MockBean en lugar de @Mock

    @Autowired
    private ObjectMapper objectMapper;

    private Baza baza;
    private Jugador jugador;
    private Truco truco;
    private Carta carta;
    private Partida partida;
    private Ronda ronda;
    private Mano mano;


    @BeforeEach
    void setUp() {

        jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(12);
        jugador.setUsuario(null);
        jugador.setPartida(partida);
    
        carta = new Carta();
        carta.setId(1);
        carta.setImagenFrontal("./images/cartas/morada_1.png");
        carta.setNumero(1);
        carta.setTipoCarta(TipoCarta.morada);
    
        ronda = new Ronda();
        ronda.setId(1);
        ronda.setEstado(RondaEstado.JUGANDO);
        ronda.setNumBazas(4);
        ronda.setNumRonda(4);
        ronda.setPartida(partida);
    
        partida = new Partida();
        partida.setEstado(PartidaEstado.JUGANDO);
        partida.setFin(LocalDateTime.now());
        partida.setId(5);
        partida.setInicio(LocalDateTime.now());
        partida.setNombre("Partida Test");
        partida.setOwnerPartida(1);
    
        baza = new Baza();
        baza.setId(1);
        baza.setTipoCarta(TipoCarta.morada);
        baza.setNumBaza(3);
        baza.setGanador(jugador);
        baza.setTrucoGanador(truco);
        baza.setRonda(ronda);

        mano =new Mano();
        mano.setApuesta(1);
        mano.setCartas(List.of(carta));
        mano.setId(1);
        mano.setJugador(jugador);
        mano.setResultado(5);
        mano.setRonda(ronda);
        
        truco = new Truco();
        truco.setId(1);
        truco.setBaza(baza);
        truco.setCarta(carta);
        truco.setJugador(jugador);
        truco.setMano(null);
        truco.setTurno(1);
        
    }

    @Test
    void testGetAllBazas() throws Exception {
        List<Baza> bazas = Arrays.asList(baza);
        when(bazaService.getAllBazas()).thenReturn(bazas);

        mockMvc.perform(get("/api/v1/bazas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(baza.getId()));
    }

    @Test
    void testGetBazaById() throws Exception {
        when(bazaService.findById(anyInt())).thenReturn(baza);

        mockMvc.perform(get("/api/v1/bazas/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(baza.getId()))
                .andExpect(jsonPath("$.numBaza").value(baza.getNumBaza()));
    }

    @Test
    void testCreateBaza() throws Exception {
        when(bazaService.saveBaza(any(Baza.class))).thenReturn(baza);

        mockMvc.perform(post("/api/v1/bazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baza)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(baza.getId()));
    }

    @Test
    void testUpdateBaza() throws Exception {
        Baza updatedBaza = new Baza();
        updatedBaza.setTrucoGanador(truco);
        updatedBaza.setGanador(jugador);
        updatedBaza.setNumBaza(5);
        updatedBaza.setRonda(ronda);
        updatedBaza.setTipoCarta(TipoCarta.pirata);

        when(bazaService.findById(1)).thenReturn(baza);
        when(bazaService.saveBaza(any(Baza.class))).thenReturn(updatedBaza);

        mockMvc.perform(put("/api/v1/bazas/{id}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedBaza)))
            .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteBaza() throws Exception {
        when(bazaService.findById(1)).thenReturn(baza);
        doNothing().when(bazaService).deleteBaza(1);

        mockMvc.perform(delete("/api/v1/bazas/{id}", 1))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.message").value("Baza eliminada"));
    }

    @Test
    void testFindBazaByIdGanador() throws Exception {
        when(bazaService.findById(anyInt())).thenReturn(baza);

        mockMvc.perform(get("/api/v1/bazas/{id}/ganador", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(jugador.getId()));
    }

    @Test
    void testFindTrucosByBazaId() throws Exception {
        List<Truco> trucos = Arrays.asList(truco);
        when(trucoService.findTrucosByBazaId(1)).thenReturn(trucos);

        mockMvc.perform(get("/api/v1/bazas/1/trucos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(truco.getId()));
    }


    // Test para obtener la última baza de una ronda
    /* 
    @Test
    void testFindUltimaBazaByRondaId() throws Exception {
        when(bazaService.findUltimaBazaByRondaId(1)).thenReturn(baza);

        mockMvc.perform(get("/api/v1/bazas/{rondaId}/ultimaBaza", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(baza.getId()));
    }
    */
}
