package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UsuarioPartidaEnJuegoEsperandoException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.MinJugadoresPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PartidaServiceTest {

    @Mock
    private PartidaRepository partidaRepository;

    @Mock
    private RondaService rondaService;

    @Mock
    private JugadorService jugadorService;

    @InjectMocks
    private PartidaService partidaService;

    private Partida partida;
    private Partida partidaEsperando;
    private Partida partidaJugando;
    private Partida partida1;
    private Partida partida2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        partida = new Partida();
        partida.setId(1);
        partida.setNombre("Partida Test");
        partida.setEstado(PartidaEstado.ESPERANDO);
        partida.setInicio(LocalDateTime.now());
        partida.setOwnerPartida(1);

        partidaEsperando = new Partida();
        partidaEsperando.setId(2);
        partidaEsperando.setNombre("Partida Esperando");
        partidaEsperando.setEstado(PartidaEstado.ESPERANDO);
        partidaEsperando.setInicio(LocalDateTime.now());
        partidaEsperando.setOwnerPartida(1);

        partidaJugando = new Partida();
        partidaJugando.setId(3);
        partidaJugando.setNombre("Partida Jugando");
        partidaJugando.setEstado(PartidaEstado.JUGANDO);
        partidaJugando.setInicio(LocalDateTime.now());
        partidaJugando.setOwnerPartida(1);

        partida1 = new Partida();
        partida1.setId(1);
        partida1.setNombre("Partida 1");
        partida1.setEstado(PartidaEstado.ESPERANDO);
        partida1.setInicio(LocalDateTime.now());
        partida1.setOwnerPartida(1);

        partida2 = new Partida();
        partida2.setId(2);
        partida2.setNombre("Juego 2");
        partida2.setEstado(PartidaEstado.ESPERANDO);
        partida2.setInicio(LocalDateTime.now());
        partida2.setOwnerPartida(1);
    }

    // Test para obtener todas las partidas
    @Test
    void testGetAllPartidas() {
        List<Partida> partidaList = Arrays.asList(partida);
        when(partidaRepository.findAll()).thenReturn(partidaList);

        List<Partida> result = partidaService.getAllPartidas(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(partidaRepository, times(1)).findAll();
    }

    // Test para filtrar partidas por estado y nombre
    @Test
    void testGetAllPartidasFiltrado() {
        // Configurar el comportamiento del mock del repositorio
        when(partidaRepository.findByNombreAndEstado("Juego 2", PartidaEstado.ESPERANDO)).thenReturn(Arrays.asList(partida2, partida1));
    
        // Llamada al servicio con el filtro de estado y nombre
        List<Partida> result = partidaService.getAllPartidas("Juego 2", PartidaEstado.ESPERANDO);
    
        // Verificación de resultados
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Juego 2", result.get(0).getNombre());
        assertEquals(PartidaEstado.ESPERANDO, result.get(0).getEstado());
        verify(partidaRepository, times(1)).findByNombreAndEstado("Juego 2", PartidaEstado.ESPERANDO);
    }


    // Test para obtener una partida por ID (caso positivo)
    @Test
    void testGetPartidaById() {
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));

        Partida foundPartida = partidaService.getPartidaById(1);

        assertNotNull(foundPartida);
        assertEquals(1, foundPartida.getId());
        verify(partidaRepository, times(1)).findById(1);
    }

    // Test para obtener una partida por ID (excepción)
    @Test
    void testGetPartidaByIdNotFound() {
        when(partidaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partidaService.getPartidaById(99));
    }

    // Test para guardar una partida (caso positivo)
    @Test
    public void testSavePartida() {
        Partida partida = new Partida();
        partida.setOwnerPartida(1);

        // Simula que el usuario no tiene partidas en juego o esperando
        when(partidaRepository.findByOwnerPartidaAndEstado(1, List.of(PartidaEstado.ESPERANDO, PartidaEstado.JUGANDO)))
                .thenReturn(List.of());

        when(partidaRepository.save(partida)).thenReturn(partida);

        Partida savedPartida = partidaService.save(partida);
        assertNotNull(savedPartida);
        assertEquals(partida, savedPartida);
    }

    // Test para guardar una partida (excepción)
    @Test
    public void testSavePartidaConUsuarioEnPartidaEnJuegoEsperando() {
        Partida partida = new Partida();
        partida.setOwnerPartida(1); // ID del usuario
    
        // Simula que el usuario ya tiene una partida en juego o esperando
        when(partidaRepository.findByOwnerPartidaAndEstado(1, List.of(PartidaEstado.ESPERANDO, PartidaEstado.JUGANDO)))
                .thenReturn(List.of(new Partida()));
    
        assertThrows(UsuarioPartidaEnJuegoEsperandoException.class, () -> partidaService.save(partida));
    }

    // Test para eliminar una partida
    @Test
    void testDeletePartida() {
        partidaService.delete(1);
        verify(partidaRepository, times(1)).deleteById(1);
    }

    // Test para iniciar una partida (caso positivo)
    @Test
    public void testIniciarPartidaConTresOMasJugadores() {
        Partida partida = new Partida();
        partida.setId(1);

        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(List.of(new Jugador(), new Jugador(), new Jugador())); // 3 jugadores
        when(partidaRepository.save(partida)).thenReturn(partida);

        partidaService.iniciarPartida(1);

        assertEquals(PartidaEstado.JUGANDO, partida.getEstado());
        verify(rondaService).iniciarRonda(partida);
    }

    // Test para iniciar una partida (exception)
    @Test
    public void testIniciarPartidaConMenosDeTresJugadores() {
        Partida partida = new Partida();
        partida.setId(1);

        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(List.of(new Jugador(), new Jugador())); // Solo 2 jugadores

        assertThrows(MinJugadoresPartidaException.class, () -> partidaService.iniciarPartida(1));
    }

    // Test para finalizar una partida
    @Test
    void testFinalizarPartida() {
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));

        partidaService.finalizarPartida(1);

        assertEquals(PartidaEstado.TERMINADA, partida.getEstado());
        verify(partidaRepository, times(1)).save(partida);
    }

    // Test para validar si el usuario tiene partidas en espera o en juego
    @Test
    void testUsuarioPartidaEnJuegoEsperando() {
        when(partidaRepository.findByOwnerPartidaAndEstado(1, Arrays.asList(PartidaEstado.ESPERANDO, PartidaEstado.JUGANDO)))
            .thenReturn(Arrays.asList(partidaEsperando, partidaJugando));

        Boolean result = partidaService.usuarioPartidaEnJuegoEsperando(1);

        assertTrue(result);
        verify(partidaRepository, times(1)).findByOwnerPartidaAndEstado(1, Arrays.asList(PartidaEstado.ESPERANDO, PartidaEstado.JUGANDO));
    }
}
