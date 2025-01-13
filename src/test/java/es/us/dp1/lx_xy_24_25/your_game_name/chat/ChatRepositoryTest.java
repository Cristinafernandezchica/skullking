package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
public class ChatRepositoryTest {

    @Autowired
    private ChatRepository chatRepository;

    @Test
    void shouldFindAllChats() {
        List<Chat> chats = chatRepository.findAll();
        assertNotNull(chats);
        assertFalse(chats.isEmpty());
    }

    @Test
    void shouldFindAllChatByPartidaId() {
        List<Chat> chats = chatRepository.findAllChatByPartidaId(3);
        assertNotNull(chats);
        assertFalse(chats.isEmpty());
    }

    @Test
    void shouldDevolverChatVacioParaPartidaInvalida() {
        List<Chat> chats = chatRepository.findAllChatByPartidaId(999);
        assertTrue(chats.isEmpty());
    }

    @Test
    @Transactional
    void shouldDeleteChatsByJugadorId() {
        chatRepository.deleteByJugadorId(1);

        List<Chat> chats = chatRepository.findAllChatByPartidaId(2);
        assertTrue(chats.isEmpty());
    }
    
}

