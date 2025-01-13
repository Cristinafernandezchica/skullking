package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;

@ExtendWith(MockitoExtension.class)
@WithMockUser
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatService chatService;

    private Chat chat;
    private Chat updatedChat;
    private List<Chat> chatList;

    @BeforeEach
    void setup() {
        chat = new Chat();
        chat.setId(1);
        chat.setMensaje("Hola!");
        chat.setJugador(new Jugador());

        updatedChat = new Chat();
        updatedChat.setId(1);
        updatedChat.setMensaje("Actualizado!");

        chatList = List.of(chat);
    }

    @Test
    void shouldSaveChat() {
        when(chatRepository.save(chat)).thenReturn(chat);

        Chat result = chatService.saveChat(chat);

        assertNotNull(result);
        assertEquals(chat.getId(), result.getId());
        assertEquals(chat.getMensaje(), result.getMensaje());
        verify(chatRepository, times(1)).save(chat);
    }

    @Test
    void shouldGetAllChats() {
        when(chatRepository.findAll()).thenReturn(chatList);

        List<Chat> result = chatService.getAllChats();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(chat.getMensaje(), result.get(0).getMensaje());
        verify(chatRepository, times(1)).findAll();
    }

    @Test
	void shouldGetAllChats_Vacia() throws Exception {
		when(chatRepository.findAll()).thenReturn(List.of());

        List<Chat> result = chatService.getAllChats();

        assertEquals(result, List.of());
        assertEquals(0, result.size());
        verify(chatRepository, times(1)).findAll();	}

    @Test
    void shouldFindChatById() {
        when(chatRepository.findById(1)).thenReturn(Optional.of(chat));

        Chat result = chatService.findById(1);

        assertNotNull(result);
        assertEquals(chat.getId(), result.getId());
        verify(chatRepository, times(1)).findById(1);
    }

    @Test
    void shouldFindChatById_NotFound() {
        when(chatRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> chatService.findById(1));
        verify(chatRepository, times(1)).findById(1);
    }

    @Test
    void shouldDeleteChat() {
        doNothing().when(chatRepository).deleteById(1);

        chatService.deleteChat(1);

        verify(chatRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldUpdateChat() {
        when(chatRepository.findById(1)).thenReturn(Optional.of(chat));
        when(chatRepository.save(any(Chat.class))).thenReturn(updatedChat);

        Chat result = chatService.updateChat(updatedChat, 1);

        assertNotNull(result);
        assertEquals(updatedChat.getMensaje(), result.getMensaje());
        verify(chatRepository, times(1)).save(any(Chat.class));
    }

    @Test
    void shouldFindAllChatByPartidaId() {
        Integer partidaId = 1;
        when(chatRepository.findAllChatByPartidaId(partidaId)).thenReturn(chatList);

        List<Chat> result = chatService.findAllChatByPartidaId(partidaId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(chat.getMensaje(), result.get(0).getMensaje());
        verify(chatRepository, times(1)).findAllChatByPartidaId(partidaId);
    }

    @Test
    void shouldEnviarMensajes() {
        Partida partida = new Partida();
        partida.setId(1);
        partida.setNombre("Partida Test");

        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPartida(partida);

        chat.setJugador(jugador);

        when(chatRepository.save(chat)).thenReturn(chat);
        when(chatRepository.findAllChatByPartidaId(partida.getId())).thenReturn(chatList);

        Chat result = chatService.enviarMensajes(chat);

        assertNotNull(result);
        assertEquals(chat.getId(), result.getId());
        assertEquals(chat.getMensaje(), result.getMensaje());

        verify(chatRepository, times(1)).save(chat);
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/topic/chats/" + partida.getId()), eq(chatList));
    }

}
