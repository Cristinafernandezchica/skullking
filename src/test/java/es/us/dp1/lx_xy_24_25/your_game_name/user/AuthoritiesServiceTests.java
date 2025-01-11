package es.us.dp1.lx_xy_24_25.your_game_name.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;

@SpringBootTest
@AutoConfigureTestDatabase
class AuthoritiesServiceTests {

	@Autowired
	private AuthoritiesService authService;

	@Test
	void shouldFindAllAuthorities() {
		List<Authorities> auths = (List<Authorities>) this.authService.findAll();
		assertEquals(2, auths.size());
	}

	@Test
	void shouldFindAuthoritiesByAuthority() {
		Authorities auth = this.authService.findByAuthority("ADMIN");
		assertEquals("ADMIN", auth.getAuthority());
	}

	@Test
	void shouldNotFindAuthoritiesByIncorrectAuthority() {
		assertThrows(ResourceNotFoundException.class, () -> this.authService.findByAuthority("authnotexists"));
	}

}
