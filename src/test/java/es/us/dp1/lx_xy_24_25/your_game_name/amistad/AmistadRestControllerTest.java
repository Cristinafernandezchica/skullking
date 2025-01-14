package es.us.dp1.lx_xy_24_25.your_game_name.amistad;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;


import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import es.us.dp1.lx_xy_24_25.your_game_name.user.User;


@WebMvcTest(AmistadRestController.class)
@WithMockUser
class AmistadRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AmistadService amistadService;

    private User remitente;
    private User destinatario1;
    private Amistad amistadPendiente;
    private Amistad amistadAceptada;

    @BeforeEach
    void setup() {
        remitente = new User();
        remitente.setId(1);
        remitente.setUsername("Remitente");

        destinatario1 = new User();
        destinatario1.setId(2);
        destinatario1.setUsername("Destinatario1");

        amistadPendiente = new Amistad();
        amistadPendiente.setId(1);
        amistadPendiente.setRemitente(remitente);
        amistadPendiente.setDestinatario(destinatario1);
        amistadPendiente.setEstadoAmistad(EstadoAmistad.PENDIENTE);

        amistadAceptada = new Amistad();
        amistadAceptada.setId(2);
        amistadAceptada.setRemitente(remitente);
        amistadAceptada.setDestinatario(destinatario1);
        amistadAceptada.setEstadoAmistad(EstadoAmistad.ACEPTADA);
    }

    @Test
    void shouldGetAllMyFriends() throws Exception {
        when(amistadService.getAllMyFriends(1)).thenReturn(List.of(destinatario1));

        mockMvc.perform(get("/api/v1/amistades/misAmigos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].username").value("Destinatario1"));
    }

    @Test
    void shouldGetAllMyFriends_Vacia() throws Exception {
        when(amistadService.getAllMyFriends(1)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/amistades/misAmigos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void shouldEnviarSolicitud() throws Exception {
        when(amistadService.enviarSolicitudDeAmistad(1, "2")).thenReturn(amistadPendiente);

        mockMvc.perform(post("/api/v1/amistades/1/2").with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estadoAmistad").value("PENDIENTE"));
    }

    @Test
    void shouldAceptarORechazarSolicitud_AceptarSolicitud() throws Exception {
        when(amistadService.aceptarORechazarSolicitudDeAmistad(1, 2, true)).thenReturn(amistadAceptada);

        mockMvc.perform(put("/api/v1/amistades/aceptarORechazarSolicitud/1/2/true").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoAmistad").value("ACEPTADA"));
    }

    @Test
    void shouldAceptarORechazarSolicitud_RechazarSolicitud() throws Exception {
        amistadPendiente.setEstadoAmistad(EstadoAmistad.RECHAZADA);
        when(amistadService.aceptarORechazarSolicitudDeAmistad(1, 2, false)).thenReturn(amistadPendiente);

        mockMvc.perform(put("/api/v1/amistades/aceptarORechazarSolicitud/1/2/false").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoAmistad").value("RECHAZADA"));
    }

    @Test
    void shouldGetAllMyConnectedFriends() throws Exception {
        destinatario1.setConectado(true);
        when(amistadService.getAllMyConnectedFriends(1)).thenReturn(List.of(destinatario1));

        mockMvc.perform(get("/api/v1/amistades/amigosConectados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].username").value("Destinatario1"));
    }

    @Test
    void shouldGetAllMyConnectedFriends_Vacio() throws Exception {
        when(amistadService.getAllMyConnectedFriends(1)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/amistades/amigosConectados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void shouldGetAllMySolicitudes() throws Exception {
        when(amistadService.getAllMySolicitudes(1)).thenReturn(List.of(destinatario1));

        mockMvc.perform(get("/api/v1/amistades/misSolicitudes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].username").value("Destinatario1"));
    }

    @Test
    void shouldGetAllMySolicitudes_Vacia() throws Exception {
        when(amistadService.getAllMySolicitudes(1)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/amistades/misSolicitudes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
}
