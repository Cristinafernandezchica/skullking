package es.us.dp1.lx_xy_24_25.your_game_name.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthoritiesService authService;

    @Test
    void shouldFindUserByUsername() {
        Optional<User> userOpt = userRepository.findByUsername("player1");
        assertTrue(userOpt.isPresent());
        assertEquals("player1", userOpt.get().getUsername());
    }

    @Test
    void shouldFindUserByUsername_NotValid() {
        Optional<User> userOpt = userRepository.findByUsername("player999");
        assertFalse(userOpt.isPresent());
    }

    @Test
    void shouldExistUserByUsername() {
        assertTrue(userRepository.existsByUsername("player1"));
    }

    @Test
    void shouldExistUserByUsername_NotFound() {
        assertFalse(userRepository.existsByUsername("player999"));
    }

    @Test
    void shouldFindUserById() {
        Optional<User> userOpt = userRepository.findById(1);
        assertTrue(userOpt.isPresent());
        assertEquals(1, userOpt.get().getId());
    }

    @Test
    void shouldNotFindUserById_NotValid() {
        Optional<User> userOpt = userRepository.findById(999);
        assertFalse(userOpt.isPresent());
    }

    @Test
    void shouldFindAllUsersByAuthority() {
        Iterable<User> players = userRepository.findAllByAuthority("PLAYER");
        List<User> playerList = new ArrayList<>();
        players.forEach(playerList::add);
        assertFalse(playerList.isEmpty());
    }

    @Test
    void shouldFindUsersByInvalidAuthority_NotExist() {
        Iterable<User> users = userRepository.findAllByAuthority("DESARROLLADOR");
        List<User> userList = new ArrayList<>();
        users.forEach(userList::add);
        assertTrue(userList.isEmpty());
    }
}
