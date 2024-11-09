package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@ComponentScan(basePackages = "es.us.dp1.lx_xy_24_25.your_game_name")
public class PartidaServiceTest {

    @Autowired
    private PartidaService partidaService;

    @Autowired
    private PartidaRepository partidaRepository;

    @BeforeEach
    public void setUp() {
        partidaRepository.deleteAll();
    }

    @Test
    public void testUsuarioPartidaEnJuegoEsperando() {
        // Datos de prueba
        Integer ownerId = 1;
        Partida partida1 = new Partida();
        partida1.setId(1);
        partida1.setOwnerPartida(ownerId);
        partida1.setEstado(PartidaEstado.ESPERANDO);

        Partida partida2 = new Partida();
        partida2.setId(2);
        partida2.setOwnerPartida(ownerId);
        partida2.setEstado(PartidaEstado.JUGANDO);

        partidaRepository.save(partida1);
        partidaRepository.save(partida2);

        // Ejecutar el método a probar
        Boolean result = partidaService.usuarioPartidaEnJuegoEsperando(ownerId);

        // Verificar el resultado
        assertThat(result).isTrue();
    }

    @Test
    public void testUsuarioNoTienePartidaEnJuegoEsperando() {
        // Datos de prueba
        Integer ownerId = 2;

        // No añadimos partidas para este usuario

        // Ejecutar el método a probar
        Boolean result = partidaService.usuarioPartidaEnJuegoEsperando(ownerId);

        // Verificar el resultado
        assertThat(result).isFalse();
    }
}
