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
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.MismoNombrePartidaNoTerminadaException;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    @Mock
    private UserService userService;

    @InjectMocks
    private PartidaService partidaService;

    private Partida partida;
    private Jugador jugador;
    private User user;
    private Partida partidaCrear;
    private User owner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setNumPartidasGanadas(0);
        user.setNumPartidasJugadas(0);
        user.setNumPuntosGanados(0);

        jugador = new Jugador();
        jugador.setId(1);
        jugador.setUsuario(user);
        jugador.setPuntos(100);

        partida = new Partida();
        partida.setId(1);
        partida.setNombre("Partida Test");
        partida.setEstado(PartidaEstado.ESPERANDO);
        partida.setInicio(LocalDateTime.now());
        partida.setOwnerPartida(1);

        owner = new User();
        owner.setId(1);

        partidaCrear = new Partida();
        partidaCrear.setOwnerPartida(1);
    }

    // Test para obtener todas las partidas (sin filtros)
    @Test
    void testGetAllPartidas() {
        when(partidaRepository.findAll()).thenReturn(Arrays.asList(partida));

        List<Partida> result = partidaService.getAllPartidas(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(partidaRepository, times(1)).findAll();
    }

    // Test para obtener todas las partidas (con filtros)
    @Test
    void testGetAllPartidasFiltradasPorNombreYEstado() {
        when(partidaRepository.findByNombreAndEstado("Partida Test", PartidaEstado.ESPERANDO))
                .thenReturn(Arrays.asList(partida));

        List<Partida> result = partidaService.getAllPartidas("Partida Test", PartidaEstado.ESPERANDO);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Partida Test", result.get(0).getNombre());
        verify(partidaRepository, times(1)).findByNombreAndEstado("Partida Test", PartidaEstado.ESPERANDO);
    }

    @Test
    void testGetAllPartidasConNombre() {
        // Arrange
        when(partidaRepository.findByNombre("Partida Test")).thenReturn(Arrays.asList(partida));

        // Act
        List<Partida> result = partidaService.getAllPartidas("Partida Test", null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Partida Test", result.get(0).getNombre());
        verify(partidaRepository, times(1)).findByNombre("Partida Test");
    }

    @Test
    void testGetAllPartidasConEstado() {
        // Arrange
        when(partidaRepository.findByEstado(PartidaEstado.ESPERANDO)).thenReturn(Arrays.asList(partida));

        // Act
        List<Partida> result = partidaService.getAllPartidas(null, PartidaEstado.ESPERANDO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PartidaEstado.ESPERANDO, result.get(0).getEstado());
        verify(partidaRepository, times(1)).findByEstado(PartidaEstado.ESPERANDO);
    }

    @Test
    void testGetAllPartidasSinResultados() {
        // Arrange
        when(partidaRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Partida> result = partidaService.getAllPartidas(null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(partidaRepository, times(1)).findAll();
    }

    @Test
    void testGetAllPartidasConNombreYEstadoSinResultados() {
        // Arrange
        when(partidaRepository.findByNombreAndEstado("No Existe", PartidaEstado.TERMINADA))
                .thenReturn(Collections.emptyList());

        // Act
        List<Partida> result = partidaService.getAllPartidas("No Existe", PartidaEstado.TERMINADA);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(partidaRepository, times(1)).findByNombreAndEstado("No Existe", PartidaEstado.TERMINADA);
    }

    // Test para obtener una partida por ID (caso positivo)
    @Test
    void testGetPartidaById() {
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));

        Partida result = partidaService.getPartidaById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(partidaRepository, times(1)).findById(1);
    }

    // Test para obtener una partida por ID (excepción)
    @Test
    void testGetPartidaByIdNoExiste() {
        when(partidaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partidaService.getPartidaById(99));
    }

    // Test para guardar una partida (caso exitoso)
    @Test
    void testSavePartida() {
        when(partidaRepository.findByOwnerPartidaAndEstado(eq(1), anyList())).thenReturn(List.of());
        when(partidaRepository.save(partida)).thenReturn(partida);

        Partida result = partidaService.save(partida);

        assertNotNull(result);
        verify(partidaRepository, times(1)).save(partida);
    }

    @Test
    void testSavePartidaMismoNombrePartidaNoTerminadaException() {
        Partida partida = new Partida();
        partida.setNombre("Partida Test");
        partida.setOwnerPartida(1);
        
        // Simula que ya existe una partida no terminada con el mismo nombre
        when(partidaRepository.findByNombreAndEstado("Partida Test", PartidaEstado.ESPERANDO))
            .thenReturn(List.of(new Partida()));

        // Verifica que se lanza la excepción esperada
        assertThrows(MismoNombrePartidaNoTerminadaException.class, () -> partidaService.save(partida));

        // Verifica que no se guarda la partida
        verify(partidaRepository, times(0)).save(any(Partida.class));
    }


    // Test para guardar una partida (excepción por estado)
    @Test
    void testSavePartidaExcepcionEstado() {
        when(partidaRepository.findByOwnerPartidaAndEstado(eq(1), anyList()))
                .thenReturn(List.of(partida));

        assertThrows(UsuarioPartidaEnJuegoEsperandoException.class, () -> partidaService.save(partida));
    }

    // Test para iniciar una partida (caso positivo)
    @Test
    void testIniciarPartida() {
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(Arrays.asList(new Jugador(), new Jugador(), new Jugador()));

        partidaService.iniciarPartida(1);

        assertEquals(PartidaEstado.JUGANDO, partida.getEstado());
        verify(rondaService, times(1)).iniciarRonda(partida);
        verify(partidaRepository, times(1)).save(partida);
    }

    @Test
    void testIniciarPartidaMenosDeTresJugadores() {
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(Arrays.asList(new Jugador(), new Jugador()));

        assertThrows(MinJugadoresPartidaException.class, () -> partidaService.iniciarPartida(1));
        verify(rondaService, times(0)).iniciarRonda(partida);
        verify(partidaRepository, times(0)).save(partida);
    }


    // Test para finalizar una partida (caso positivo)
    @Test
    void testFinalizarPartida() {
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(Arrays.asList(jugador));

        partidaService.finalizarPartida(1);

        assertEquals(PartidaEstado.TERMINADA, partida.getEstado());
        assertNotNull(partida.getFin());
        assertEquals(1, user.getNumPartidasJugadas());
        assertEquals(100, user.getNumPuntosGanados());
        verify(userService, times(1)).saveUser(user);
    }

    @Test
    public void testFinalizarPartidaNoExiste() {
        // Simular que la partida no existe
        when(partidaRepository.findById(99)).thenReturn(Optional.empty());

        // Verificar que se lanza la excepción ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> partidaService.finalizarPartida(99));

        // Verificar que no se realizaron otras acciones
        verify(partidaRepository, times(1)).findById(99);
        verify(partidaRepository, times(0)).save(any(Partida.class));
        verify(jugadorService, times(0)).findJugadoresByPartidaId(anyInt());
    }

    // Test para eliminar una partida
    @Test
    void testEliminarPartida() {
        doNothing().when(partidaRepository).deleteById(1);

        partidaService.delete(1);

        verify(partidaRepository, times(1)).deleteById(1);
    }


    @Test
    void testUsuarioNoEsJugadorEnNingunaPartida() {
        Partida partidaCrear = new Partida();
        partidaCrear.setOwnerPartida(1);

        // Simula que no hay jugadores que coincidan con las condiciones
        when(jugadorService.findAll()).thenReturn(List.of());

        Boolean resultado = partidaService.usuarioJugadorEnPartida(partidaCrear);

        assertFalse(resultado);
        verify(jugadorService, times(1)).findAll();
    }

    @Test
    void testMismoNombrePartidaNoTerminada() {
        Partida partida = new Partida();
        partida.setNombre("Partida Test");

        when(partidaRepository.findByNombreAndEstado("Partida Test", PartidaEstado.ESPERANDO)).thenReturn(List.of(partida));
        when(partidaRepository.findByNombreAndEstado("Partida Test", PartidaEstado.JUGANDO)).thenReturn(List.of());

        Boolean result = partidaService.mismoNombrePartidaNoTerminada(partida);

        assertTrue(result);
        verify(partidaRepository, times(1)).findByNombreAndEstado("Partida Test", PartidaEstado.ESPERANDO);
        verify(partidaRepository, times(1)).findByNombreAndEstado("Partida Test", PartidaEstado.JUGANDO);
    }

    @Test
    void testMismoNombrePartidaTerminada() {
        Partida partida = new Partida();
        partida.setNombre("Partida Test");

        when(partidaRepository.findByNombreAndEstado("Partida Test", PartidaEstado.ESPERANDO)).thenReturn(List.of());
        when(partidaRepository.findByNombreAndEstado("Partida Test", PartidaEstado.JUGANDO)).thenReturn(List.of());

        Boolean result = partidaService.mismoNombrePartidaNoTerminada(partida);

        assertFalse(result);
        verify(partidaRepository, times(1)).findByNombreAndEstado("Partida Test", PartidaEstado.ESPERANDO);
        verify(partidaRepository, times(1)).findByNombreAndEstado("Partida Test", PartidaEstado.JUGANDO);
    }

    @Test
    void testGetJugadorGanador() {
        // Arrange
        Jugador jugador1 = new Jugador();
        jugador1.setPuntos(100);
        Jugador jugador2 = new Jugador();
        jugador2.setPuntos(150);
        Jugador jugador3 = new Jugador();
        jugador3.setPuntos(120);

        List<Jugador> jugadores = Arrays.asList(jugador1, jugador2, jugador3);
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(jugadores);

        // Act
        Jugador ganador = partidaService.getJugadorGanador(1);

        // Assert
        assertNotNull(ganador);
        assertEquals(jugador2, ganador);
        verify(jugadorService, times(1)).findJugadoresByPartidaId(1);
    }

    @Test
    void testGetJugadorGanadorConPuntajesIguales() {
        // Arrange
        Jugador jugador1 = new Jugador();
        jugador1.setPuntos(150);
        Jugador jugador2 = new Jugador();
        jugador2.setPuntos(150);

        List<Jugador> jugadores = Arrays.asList(jugador1, jugador2);
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(jugadores);

        // Act
        Jugador ganador = partidaService.getJugadorGanador(1);

        // Assert
        assertNotNull(ganador);
        assertTrue(jugador1.equals(ganador) || jugador2.equals(ganador));
        verify(jugadorService, times(1)).findJugadoresByPartidaId(1);
    }

    @Test
    void testGetJugadorGanadorListaVacia() {
        // Arrange
        List<Jugador> jugadores = Arrays.asList();
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(jugadores);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> partidaService.getJugadorGanador(1));
        verify(jugadorService, times(1)).findJugadoresByPartidaId(1);
    }

    @Test
    void testUsuarioJugadorEnPartidaEnEspera() {
        // Arrange
        Jugador jugador = new Jugador();
        jugador.setUsuario(owner);
        Partida partida = new Partida();
        partida.setEstado(PartidaEstado.ESPERANDO);
        jugador.setPartida(partida);

        when(jugadorService.findAll()).thenReturn(List.of(jugador));

        // Act
        Boolean result = partidaService.usuarioJugadorEnPartida(partidaCrear);

        // Assert
        assertTrue(result);
        verify(jugadorService, times(1)).findAll();
    }

    @Test
    void testUsuarioJugadorEnPartidaJugando() {
        // Arrange
        Jugador jugador = new Jugador();
        jugador.setUsuario(owner);
        Partida partida = new Partida();
        partida.setEstado(PartidaEstado.JUGANDO);
        jugador.setPartida(partida);

        when(jugadorService.findAll()).thenReturn(List.of(jugador));

        // Act
        Boolean result = partidaService.usuarioJugadorEnPartida(partidaCrear);

        // Assert
        assertTrue(result);
        verify(jugadorService, times(1)).findAll();
    }


    @Test
    void testUsuarioJugadorEnPartidaTerminada() {
        // Arrange
        Jugador jugador = new Jugador();
        jugador.setUsuario(owner);
        Partida partida = new Partida();
        partida.setEstado(PartidaEstado.TERMINADA);
        jugador.setPartida(partida);

        when(jugadorService.findAll()).thenReturn(List.of(jugador));

        // Act
        Boolean result = partidaService.usuarioJugadorEnPartida(partidaCrear);

        // Assert
        assertFalse(result);
        verify(jugadorService, times(1)).findAll();
    }

    @Test
    void testUsuarioEnOtraPartida() {
        // Arrange
        User otroUsuario = new User();
        otroUsuario.setId(2);
        Jugador otroJugador = new Jugador();
        otroJugador.setUsuario(otroUsuario);
        Partida partidaEnEspera = new Partida();
        partidaEnEspera.setEstado(PartidaEstado.ESPERANDO);
        otroJugador.setPartida(partidaEnEspera);

        when(jugadorService.findAll()).thenReturn(List.of(otroJugador));

        // Act
        Boolean result = partidaService.usuarioJugadorEnPartida(partidaCrear);

        // Assert
        assertFalse(result);
        verify(jugadorService, times(1)).findAll();
    }
    

}
