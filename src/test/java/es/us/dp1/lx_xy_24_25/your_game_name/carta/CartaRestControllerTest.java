package es.us.dp1.lx_xy_24_25.your_game_name.carta;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartaRestController.class)
@WithMockUser
class CartaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartaService cartaService;

    @MockBean
    private AuthoritiesService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private Carta carta;

    @BeforeEach
    void setUp() {
        carta = new Carta();
        carta.setId(1);
        carta.setNumero(14);
        carta.setTipoCarta(TipoCarta.pirata);
        carta.setImagenFrontal("imagen_frontal.png");
    }

    @Test
    void testFindAllCartas() throws Exception {
        when(cartaService.findAll()).thenReturn(Arrays.asList(carta));

        mockMvc.perform(get("/api/v1/cartas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].numero").value(14))
                .andExpect(jsonPath("$[0].tipoCarta").value("pirata"))
                .andExpect(jsonPath("$[0].imagenFrontal").value("imagen_frontal.png"))
                .andExpect(jsonPath("$[0].imagenTrasera").value("./images/cartas/parte_trasera.png"));
    }

    @Test
    void testFindAllCartas_ListaVacia() throws Exception {
        when(cartaService.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/cartas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void testGetCartaById() throws Exception {
        when(cartaService.findById(1)).thenReturn(Optional.of(carta));

        mockMvc.perform(get("/api/v1/cartas/carta/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imagenFrontal").value("imagen_frontal.png"))
                .andExpect(jsonPath("$.imagenTrasera").value("./images/cartas/parte_trasera.png"));
    }

    @Test
    void testGetCartaById_NotFound() throws Exception {
        when(cartaService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/cartas/carta/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Carta no encontrada"));
    }

    @Test
    void testCreateCarta() throws Exception {
        when(cartaService.saveCarta(Mockito.any(Carta.class))).thenReturn(carta);

        mockMvc.perform(post("/api/v1/cartas")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value(14))
                .andExpect(jsonPath("$.tipoCarta").value("pirata"));
    }

    @Test
    void testUpdateCarta() throws Exception {
        when(cartaService.findById(1)).thenReturn(Optional.of(carta));
        when(cartaService.updateCarta(Mockito.any(Carta.class), Mockito.eq(1))).thenReturn(carta);

        mockMvc.perform(put("/api/v1/cartas/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(14))
                .andExpect(jsonPath("$.tipoCarta").value("pirata"));
    }

    @Test
    void testDeleteCarta() throws Exception {
        when(cartaService.findById(1)).thenReturn(Optional.of(carta));
        doNothing().when(cartaService).deleteCarta(1);

        mockMvc.perform(delete("/api/v1/cartas/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Carta eliminada!"));
    }

    @Test
    void testCambioTigresa() throws Exception {
        when(cartaService.cambioTigresa("tigresa")).thenReturn(carta);

        mockMvc.perform(get("/api/v1/cartas/tigresa/tigresa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoCarta").value("pirata"));
    }

    @Test
    void testCambioTigresa_NotFound() throws Exception {
        when(cartaService.cambioTigresa("inexistente")).thenReturn(null);

        mockMvc.perform(get("/api/v1/cartas/tigresa/inexistente"))
                .andExpect(status().isNotFound());
    }
}
