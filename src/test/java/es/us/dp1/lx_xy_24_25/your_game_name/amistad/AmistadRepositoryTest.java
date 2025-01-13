package es.us.dp1.lx_xy_24_25.your_game_name.amistad;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserRepository;

@SpringBootTest
@AutoConfigureTestDatabase
public class AmistadRepositoryTest {

    @Autowired
    private AmistadRepository amistadRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldGetAllMyAmistad() {
        User user = userRepository.findById(6).orElseThrow();
        List<Amistad> amistades = amistadRepository.getAllMyAmistad(user.getId());

        assertNotNull(amistades);
        assertFalse(amistades.isEmpty());
    }

    @Test
    void shouldDevolverVacioSiNoTieneAmistad() {
        User user = userRepository.findById(1).orElseThrow();
        List<Amistad> amistades = amistadRepository.getAllMyAmistad(user.getId());

        assertTrue(amistades.isEmpty());
    }

    @Test
    void shouldGetOneAmistad() {
        Amistad amistad = amistadRepository.getOneAmistad(5, 6);
        assertNotNull(amistad);
        assertEquals(5, amistad.getRemitente().getId());
        assertEquals(6, amistad.getDestinatario().getId());
    }

    @Test
    void shouldGetOneAmistad_NotFound() {
        Amistad amistad = amistadRepository.getOneAmistad(1, 999);
        assertNull(amistad);
    }

    @Test
    @Transactional
    void shouldDeleteByRemitenteOrDestinatario() {
        User user = userRepository.findById(5).orElseThrow();
        amistadRepository.deleteByRemitenteOrDestinatario(user);

        List<Amistad> amistades = amistadRepository.getAllMyAmistad(user.getId());
        assertTrue(amistades.isEmpty());
    }
}
