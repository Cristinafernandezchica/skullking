package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;


@WebMvcTest(controllers = ChatRestController.class)
@WithMockUser
public class ChatRestControllerTest {

    private static final String BASE_URL = "/api/v1/chats";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;

    private Chat chat;
    private Partida partida;
    private Jugador jugador;

    @BeforeEach
    void setup() {
        partida = new Partida();
        partida.setId(1);
        partida.setNombre("Partida Test");

        jugador = new Jugador();
        jugador.setId(1);
        jugador.setPartida(partida);

        chat = new Chat();
        chat.setId(1);
        chat.setMensaje("Mensaje de prueba");
        chat.setJugador(jugador);
    }

    @Test
    void shouldFindAllChats() throws Exception {
        List<Chat> chats = List.of(chat);

        when(chatService.getAllChats()).thenReturn(chats);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].mensaje").value("Mensaje de prueba"));

        verify(chatService, times(1)).getAllChats();
    }

    @Test
	void shouldFindAllChats_Vacia() throws Exception {
		when(chatService.getAllChats()).thenReturn(List.of());

		mockMvc.perform(get(BASE_URL))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(0));

		verify(chatService, times(1)).getAllChats();
	}

    @Test
    void shouldFindAllChatByPartidaId() throws Exception {
        List<Chat> chats = List.of(chat);

        when(chatService.findAllChatByPartidaId(1)).thenReturn(chats);

        mockMvc.perform(get(BASE_URL + "/{partidaId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].mensaje").value("Mensaje de prueba"));

        verify(chatService, times(1)).findAllChatByPartidaId(1);
    }

    @Test
    void shouldFindAllChatByPartidaId_Vacia() throws Exception {
        when(chatService.findAllChatByPartidaId(99)).thenReturn(List.of()); // Supongamos que la partida ID 99 no tiene chats
    
        mockMvc.perform(get(BASE_URL + "/{partidaId}", 99))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0)); // La lista está vacía
    
        verify(chatService, times(1)).findAllChatByPartidaId(99);
    }

    @Test
    void shouldSendChatMessage() throws Exception {
        when(chatService.enviarMensajes(any(Chat.class))).thenReturn(chat);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chat))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mensaje").value("Mensaje de prueba"));

        verify(chatService, times(1)).enviarMensajes(any(Chat.class));
    }

    @Test
    void shouldSendChatMessage_InternalServerError() throws Exception {
        when(chatService.enviarMensajes(any(Chat.class))).thenThrow(new RuntimeException("Error inesperado")); // Simula error en el servicio

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chat))
                        .with(csrf()))
                .andExpect(status().isInternalServerError()); // Error 500

        verify(chatService, times(1)).enviarMensajes(any(Chat.class));
    }

    @Test
    void shouldSendChatMessage_NoAutentificado() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chat))) // Falta CSRF o autenticación
                .andExpect(status().isForbidden()); // Debería fallar sin autenticación

        verify(chatService, times(0)).enviarMensajes(any(Chat.class));
    }

}

