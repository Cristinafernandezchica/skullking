package es.us.dp1.lx_xy_24_25.your_game_name.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthoritiesRepositoryTest {

    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    @Test
    void shouldFindByName() {
        Authorities authority = new Authorities();
        authority.setAuthority("OWNER");
        authoritiesRepository.save(authority);

        Optional<Authorities> result = authoritiesRepository.findByName("OWNER");

        assertTrue(result.isPresent());
        assertEquals("OWNER", result.get().getAuthority());
    }

    @Test
    void shouldNotFindByName() {
        // Test para autoridad inexistente
        Optional<Authorities> result = authoritiesRepository.findByName("DESARROLLADOR");

        assertTrue(result.isEmpty());
    }
}
