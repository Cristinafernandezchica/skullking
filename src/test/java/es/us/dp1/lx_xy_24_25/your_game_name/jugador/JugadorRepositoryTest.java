package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@ExtendWith(MockitoExtension.class)
public class JugadorRepositoryTest {

    @Mock
    private JugadorRepository jugadorRepository;

    private Jugador jugador1;
    private Jugador jugador2;
    private Partida partida;
    private User usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Crear usuario simulado
        usuario = new User();
        usuario.setId(1);
        usuario.setUsername("usuario_test");

        // Crear partida simulada
        partida = new Partida();
        partida.setId(1);
        partida.setNombre("Partida Test");
        partida.setEstado(PartidaEstado.JUGANDO);
        partida.setOwnerPartida(usuario.getId());

        // Crear jugadores asociados
        jugador1 = new Jugador();
        jugador1.setId(1);
        jugador1.setUsuario(usuario);
        jugador1.setPartida(partida);

        jugador2 = new Jugador();
        jugador2.setId(2);
        jugador2.setPartida(partida);
        jugador2.setUsuario(usuario);
    }

    @Test
    public void shouldFindJugadoresByPartidaId() {
        Integer partidaId = partida.getId();
        when(jugadorRepository.findJugadoresByPartidaId(partidaId)).thenReturn(List.of(jugador1, jugador2));

        List<Jugador> result = jugadorRepository.findJugadoresByPartidaId(partidaId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(j -> j.getPartida().getId().equals(partidaId)));
        verify(jugadorRepository, times(1)).findJugadoresByPartidaId(partidaId);
    }

    @Test
    public void shouldFindJugadoresByUsuarioId() {
        Integer usuarioId = usuario.getId();
        when(jugadorRepository.findJugadoresByUsuarioId(usuarioId)).thenReturn(List.of(jugador1, jugador2));

        List<Jugador> result = jugadorRepository.findJugadoresByUsuarioId(usuarioId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(j -> j.getUsuario().getId().equals(usuarioId)));
        verify(jugadorRepository, times(1)).findJugadoresByUsuarioId(usuarioId);
    }

    @Test
    public void shouldFindById() {
        Integer jugadorId = jugador1.getId();
        when(jugadorRepository.findById(jugadorId)).thenReturn(Optional.of(jugador1));

        Optional<Jugador> result = jugadorRepository.findById(jugadorId);

        assertTrue(result.isPresent());
        assertEquals(jugadorId, result.get().getId());
        verify(jugadorRepository, times(1)).findById(jugadorId);
    }

    @Test
    public void shouldDeletePartidasByOwner() {
        Integer userId = usuario.getId();

        jugadorRepository.deletePartidasByOwner(userId);
        assertEquals(jugadorRepository.findById(1), Optional.empty());
        assertEquals(jugadorRepository.findById(2), Optional.empty());
        verify(jugadorRepository, times(1)).deletePartidasByOwner(userId);
    }
}

