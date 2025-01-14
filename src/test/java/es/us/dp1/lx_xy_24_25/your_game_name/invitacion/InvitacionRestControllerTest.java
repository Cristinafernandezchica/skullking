package es.us.dp1.lx_xy_24_25.your_game_name.invitacion;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@WebMvcTest(controllers = InvitacionRestController.class)
@WithMockUser
public class InvitacionRestControllerTest {

    private static final String BASE_URL = "/api/v1/invitaciones";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvitacionService invitacionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Invitacion invitacion;
    private User remitente;
    private User destinatario;
    private Partida partida;
    private Authorities autoridad;

    @BeforeEach
    void setup() {
        autoridad = new Authorities();
        autoridad.setId(1);
        autoridad.setAuthority("PLAYER");

        remitente = new User();
        remitente.setId(1);
        remitente.setUsername("Remitente");
        remitente.setAuthority(autoridad);

        destinatario = new User();
        destinatario.setId(2);
        destinatario.setUsername("Destinatario");
        destinatario.setAuthority(autoridad);

        partida = new Partida();
        partida.setId(1);
        partida.setNombre("Partida Test");
        partida.setOwnerPartida(1);
        partida.setEstado(PartidaEstado.ESPERANDO);
        partida.setInicio(LocalDateTime.now());
        partida.setTurnoActual(3);

        invitacion = new Invitacion();
        invitacion.setId(1);
        invitacion.setRemitente(remitente);
        invitacion.setDestinatario(destinatario);
        invitacion.setPartida(partida);
        invitacion.setEspectador(true);

    }

    @Test
    void shouldGetTodasMisInvitaciones() throws Exception {
        List<Invitacion> invitaciones = List.of(invitacion);
        when(invitacionService.getTodasMisInvitaciones(2)).thenReturn(invitaciones);

        mockMvc.perform(get(BASE_URL + "/misInvitaciones/{destinatarioId}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].remitente.username").value("Remitente"))
                .andExpect(jsonPath("$[0].destinatario.username").value("Destinatario"));

        verify(invitacionService, times(1)).getTodasMisInvitaciones(2);
    }

    @Test
    void shouldGetTodasMisInvitaciones_Vacia() throws Exception {
        when(invitacionService.getTodasMisInvitaciones(2)).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/misInvitaciones/{destinatarioId}", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        verify(invitacionService, times(1)).getTodasMisInvitaciones(2);
    }

    @Test
    void shouldGetUnaInvitaciones() throws Exception {
        when(invitacionService.getOne(2, 1)).thenReturn(invitacion);

        mockMvc.perform(get(BASE_URL + "/misUnaInvitaciones/{destinatarioId}/{remitente}", 2, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remitente.username").value("Remitente"))
                .andExpect(jsonPath("$.destinatario.username").value("Destinatario"));

        verify(invitacionService, times(1)).getOne(2, 1);
    }

    @Test
    void shouldGetUnaInvitaciones_NotFound() throws Exception {
        when(invitacionService.getOne(2, 1)).thenThrow(new ResourceNotFoundException("Invitacion no encontrada"));

        mockMvc.perform(get(BASE_URL + "/misUnaInvitaciones/{destinatarioId}/{remitente}", 2, 1))
                .andExpect(status().isNotFound());

        verify(invitacionService, times(1)).getOne(2, 1);
    }

    @Test
    void shouldEnviarInvitacion() throws Exception {
        when(invitacionService.enviarInvitacion(invitacion)).thenReturn(invitacion);

        mockMvc.perform(post(BASE_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invitacion)))
                .andExpect(status().isOk());

        verify(invitacionService, times(1)).enviarInvitacion(any(Invitacion.class));
    }

    @Test
    void shouldEnviarInvitacion_PartidaInvalida() throws Exception {
        invitacion.setPartida(null);

        mockMvc.perform(post(BASE_URL).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invitacion)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


    @Test
    void shouldAceptarInvitacion() throws Exception {
        doNothing().when(invitacionService).deleteInvitacion(1);

        mockMvc.perform(delete(BASE_URL + "/{invitacionId}", 1).with(csrf()))
                .andExpect(status().isOk());

        verify(invitacionService, times(1)).deleteInvitacion(1);
    }

    @Test
    void shouldFailAceptarInvitacion_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Invitacion no encontrada"))
                .when(invitacionService).deleteInvitacion(999);

        mockMvc.perform(delete(BASE_URL + "/{invitacionId}", 999).with(csrf()))
                .andExpect(status().isNotFound());

        verify(invitacionService, times(1)).deleteInvitacion(999);
    }

}

