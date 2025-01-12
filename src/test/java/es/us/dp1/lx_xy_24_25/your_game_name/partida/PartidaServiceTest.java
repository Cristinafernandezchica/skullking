package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UsuarioPartidaEnJuegoEsperandoException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.MinJugadoresPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.MismoNombrePartidaNoTerminadaException;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private RondaRepository rondaRepository;

    @Mock
    private RondaService rondaService;

    @Mock
    private JugadorService jugadorService;

    @MockBean
    private ManoService manoService;

    @Mock
    private BazaService bazaService;

    @Mock
    private UserService userService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private PartidaService partidaService;

    private Partida partida;
    private Jugador jugador;
    private Jugador jugador2;
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
        jugador.setPartida(partida);

        jugador2 = new Jugador();
        jugador2.setId(2);
        jugador2.setUsuario(new User());
        jugador2.setPartida(partida);

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
    void shouldGetAllPartidas() {
        when(partidaRepository.findAll()).thenReturn(Arrays.asList(partida));

        List<Partida> result = partidaService.getAllPartidas(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(partidaRepository, times(1)).findAll();
    }

    // Test para obtener todas las partidas (con filtros)
    @Test
    void shouldGetAllPartidasFiltradasPorNombreYEstado() {
        when(partidaRepository.findByNombreAndEstado("Partida Test", PartidaEstado.ESPERANDO))
                .thenReturn(Arrays.asList(partida));

        List<Partida> result = partidaService.getAllPartidas("Partida Test", PartidaEstado.ESPERANDO);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Partida Test", result.get(0).getNombre());
        verify(partidaRepository, times(1)).findByNombreAndEstado("Partida Test", PartidaEstado.ESPERANDO);
    }

    @Test
    void shouldGetAllPartidasConNombre() {
        when(partidaRepository.findByNombre("Partida Test")).thenReturn(Arrays.asList(partida));

        List<Partida> result = partidaService.getAllPartidas("Partida Test", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Partida Test", result.get(0).getNombre());
        verify(partidaRepository, times(1)).findByNombre("Partida Test");
    }

    @Test
    void shouldGetAllPartidasConEstado() {
        when(partidaRepository.findByEstado(PartidaEstado.ESPERANDO)).thenReturn(Arrays.asList(partida));

        List<Partida> result = partidaService.getAllPartidas(null, PartidaEstado.ESPERANDO);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PartidaEstado.ESPERANDO, result.get(0).getEstado());
        verify(partidaRepository, times(1)).findByEstado(PartidaEstado.ESPERANDO);
    }

    @Test
    void shouldGetAllPartidasSinResultados() {
        when(partidaRepository.findAll()).thenReturn(Collections.emptyList());

        List<Partida> result = partidaService.getAllPartidas(null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(partidaRepository, times(1)).findAll();
    }

    @Test
    void shouldGetAllPartidasConNombreYEstadoSinResultados() {
        when(partidaRepository.findByNombreAndEstado("No Existe", PartidaEstado.TERMINADA))
                .thenReturn(Collections.emptyList());

        List<Partida> result = partidaService.getAllPartidas("No Existe", PartidaEstado.TERMINADA);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(partidaRepository, times(1)).findByNombreAndEstado("No Existe", PartidaEstado.TERMINADA);
    }

    // Test para obtener una partida por ID (caso positivo)
    @Test
    void shouldGetPartidaById() {
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));

        Partida result = partidaService.getPartidaById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(partidaRepository, times(1)).findById(1);
    }

    // Test para obtener una partida por ID (excepción)
    @Test
    void shouldGetPartidaByIdNoExiste() {
        when(partidaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partidaService.getPartidaById(99));
    }

    // Test para guardar una partida (caso exitoso)
    @Test
    void shouldSavePartida() {
        when(partidaRepository.findByOwnerPartidaAndEstado(eq(1), anyList())).thenReturn(List.of());
        when(partidaRepository.save(partida)).thenReturn(partida);

        Partida result = partidaService.save(partida);

        assertNotNull(result);
        verify(partidaRepository, times(1)).save(partida);
    }

    @Test
    void shouldSavePartidaMismoNombrePartidaNoTerminadaException() {
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
    void shouldSavePartidaExcepcionEstado() {
        when(partidaRepository.findByOwnerPartidaAndEstado(eq(1), anyList()))
                .thenReturn(List.of(partida));

        assertThrows(UsuarioPartidaEnJuegoEsperandoException.class, () -> partidaService.save(partida));
    }

    @Test
    void shouldUpdatePartida() {
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(partidaRepository.save(any(Partida.class))).thenReturn(partida);

        Partida updatedPartida = new Partida();
        updatedPartida.setEstado(PartidaEstado.JUGANDO);

        Partida result = partidaService.update(updatedPartida, 1);

        assertNotNull(result);
        assertEquals(PartidaEstado.JUGANDO, result.getEstado());
        verify(partidaRepository, times(1)).save(partida);
    }

    @Test
    void shouldUpdatePartida_NotFound() {
        when(partidaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partidaService.update(new Partida(), 99));
        verify(partidaRepository, times(0)).save(any(Partida.class));
    }

    @Test
    void shouldDeletePartida() {
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(List.of(jugador, jugador2));
    
        partidaService.delete(1);
    
        verify(jugadorService, times(2)).deleteJugador(anyInt(), eq(false));
        verify(rondaRepository, times(1)).deleteByPartidaId(1);
        verify(partidaRepository, times(1)).delete(partida);
    }
    
    @Test
    void shouldDeletePartida_NotFound() {
        when(partidaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partidaService.delete(99));
        verify(partidaRepository, times(0)).delete(any(Partida.class));
    }

    @Test
    void shouldFindPartidasByOwnerId() {
        when(partidaRepository.findByOwnerPartida(1)).thenReturn(List.of(partida));

        List<Partida> result = partidaService.findPartidasByOwnerId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getOwnerPartida());
        verify(partidaRepository, times(1)).findByOwnerPartida(1);
    }

    @Test
    void shouldFindPartidasByOwnerId_Vacia() {
        when(partidaRepository.findByOwnerPartida(1)).thenReturn(List.of());

        List<Partida> result = partidaService.findPartidasByOwnerId(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(partidaRepository, times(1)).findByOwnerPartida(1);
    }

    @Test
    void shouldActualizarOwner() {
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(List.of(jugador, jugador2));
    
        partidaService.actualizarOwner(1, 2);
    
        verify(partidaRepository, times(1)).save(partida);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/partida/1"), anyMap());
    }

    @Test
    void shouldActualizarOwner_InvalidOwner() {
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));

        assertThrows(IllegalArgumentException.class, () -> partidaService.actualizarOwner(1, -1));
        verify(partidaRepository, times(0)).save(any(Partida.class));
    }

    // Test para iniciar una partida (caso positivo)
    @Test
    void shouldIniciarPartida() {
        Ronda ronda = new Ronda();
        ronda.setId(1);
    
        Baza baza = new Baza();
        baza.setId(1);
        baza.setTurnos(List.of(1, 2));
    
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(List.of(jugador, jugador2, jugador2));
        when(rondaService.iniciarRonda(partida)).thenReturn(ronda);
        when(bazaService.iniciarBaza(ronda, List.of(jugador, jugador2, jugador2))).thenReturn(baza);
    
        partidaService.iniciarPartida(1);
    
        verify(partidaRepository, times(2)).save(partida);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/partida/1"), anyMap());
    }

    @Test
    void shouldIniciarPartida_NullPartida() {
        when(partidaRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partidaService.iniciarPartida(1));

        verify(partidaRepository, times(1)).findById(1);
        verifyNoInteractions(jugadorService, rondaService, manoService, bazaService, messagingTemplate);
    }


    @Test
    void shouldIniciarPartidaMenosDeTresJugadores() {
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(Arrays.asList(new Jugador(), new Jugador()));

        assertThrows(MinJugadoresPartidaException.class, () -> partidaService.iniciarPartida(1));
        verify(rondaService, times(0)).iniciarRonda(partida);
        verify(partidaRepository, times(0)).save(partida);
    }


    // Test para finalizar una partida (caso positivo)
    @Test
    void shouldFinalizarPartida() {
        User ganador = new User();
        ganador.setUsername("Ganador");

        jugador.setPuntos(100);
        jugador.setUsuario(ganador);

        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(List.of(jugador));

        partidaService.finalizarPartida(1);

        verify(userService, times(1)).saveUser(ganador);
        verify(partidaRepository, times(1)).save(partida);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/partida/1"), anyMap());
    }

    @Test
    void shouldFinalizarPartida_NotFound() {
        when(partidaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partidaService.finalizarPartida(99));

        verify(partidaRepository, times(1)).findById(99);
        verify(partidaRepository, times(0)).save(any(Partida.class));
        verify(jugadorService, times(0)).findJugadoresByPartidaId(anyInt());
    }

    

    @Test
    void shouldMismoNombrePartidaNoTerminada() {
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
    void shouldMismoNombrePartidaTerminada() {
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
    void shouldGetJugadorGanador() {
        Jugador jugador1 = new Jugador();
        jugador1.setPuntos(100);
        Jugador jugador2 = new Jugador();
        jugador2.setPuntos(150);
        Jugador jugador3 = new Jugador();
        jugador3.setPuntos(120);

        List<Jugador> jugadores = Arrays.asList(jugador1, jugador2, jugador3);
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(jugadores);

        Jugador ganador = partidaService.getJugadorGanador(1);

        assertNotNull(ganador);
        assertEquals(jugador2, ganador);
        verify(jugadorService, times(1)).findJugadoresByPartidaId(1);
    }

    @Test
    void shouldGetJugadorGanadorConPuntajesIguales() {
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
    void shouldGetJugadorGanadorListaVacia() {
        // Arrange
        List<Jugador> jugadores = Arrays.asList();
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(jugadores);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> partidaService.getJugadorGanador(1));
        verify(jugadorService, times(1)).findJugadoresByPartidaId(1);
    }

    @Test
    void shouldUsuarioJugadorEnPartidaEnEspera() {
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
    void shouldUsuarioJugadorEnPartidaJugando() {
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
    void shouldUsuarioJugadorEnPartidaTerminada() {
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
    void shouldUsuarioEnOtraPartida() {
        User otroUsuario = new User();
        otroUsuario.setId(2);
        Jugador otroJugador = new Jugador();
        otroJugador.setUsuario(otroUsuario);
        Partida partidaEnEspera = new Partida();
        partidaEnEspera.setEstado(PartidaEstado.ESPERANDO);
        otroJugador.setPartida(partidaEnEspera);

        when(jugadorService.findAll()).thenReturn(List.of(otroJugador));

        Boolean result = partidaService.usuarioJugadorEnPartida(partidaCrear);

        assertFalse(result);
        verify(jugadorService, times(1)).findAll();
    }
    
    @Test
    void shouldSiguienteEstado() {
        Ronda ronda = new Ronda();
        ronda.setNumBazas(1);
        ronda.setNumRonda(1);
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(List.of(jugador));
        when(bazaService.findById(1)).thenReturn(new Baza());

        partidaService.siguienteEstado(1, 1);

        verify(bazaService, times(1)).nextBaza(1, List.of(jugador));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/nuevaBaza/partida/1"), anyMap());
    }


    @Test
    void shouldSiguienteEstado_NewRonda() {
        Ronda ronda = new Ronda();
        ronda.setNumBazas(1);
        ronda.setNumRonda(1);
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(bazaService.findById(1)).thenReturn(new Baza());

        partidaService.siguienteEstado(1, 1);
        List<Jugador> jugadores = List.of(jugador, jugador2);
        verify(bazaService, times(1)).nextBaza(1, jugadores);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/nuevaBaza/partida/1"), anyMap());
    }

    @Test
    void shouldSiguienteEstado_FinalizarPartida() {
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(bazaService.findById(1)).thenReturn(new Baza());
        //when(rondaService.finalizarRonda(anyInt())).thenReturn(true);

        partidaService.siguienteEstado(1, 1);

        verify(partidaRepository, times(1)).save(partida);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/partida/1"), anyMap());
    }

    @Test
    void shouldPrimerTurno() {
        List<Integer> turnos = List.of(1, 2, 3);
    
        Integer result = partidaService.primerTurno(turnos);
    
        assertNotNull(result);
        assertEquals(1, result);
    }

    @Test
    void shouldPrimerTurno_Vacio() {
        List<Integer> turnos = List.of();
    
        assertThrows(IndexOutOfBoundsException.class, () -> partidaService.primerTurno(turnos));
    }
    
}
/*
     // Tests apostar --> LO COMENTO SE HA MOVIDO A Partida
    /*
    @Test
    public void shouldApuestaExito() {
        // Crear la lista de manos y asegurar que findLastManoByJugadorId devuelve la última mano
        List<Mano> manos = List.of(mano);
        when(manoRepository.findAllManoByJugadorId(jugador3.getId())).thenReturn(manos);
        when(jugadorService.findById(jugador3.getId())).thenReturn(jugador3);

        manoService.apuesta(2, jugador3.getId());

        assertEquals(2, mano.getApuesta());
        assertEquals(2, jugador3.getApuestaActual());
        verify(manoRepository, times(1)).save(mano);
        verify(jugadorService, times(1)).updateJugador(jugador3, jugador3.getId());
    }

    @Test
    public void shouldApuestaManoNoEncontrada() {
        when(manoRepository.findAllManoByJugadorId(jugador3.getId())).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> manoService.apuesta(2, jugador3.getId()));
    }

    @Test
    public void shouldApuestaMayorQueNumCartas() {
        List<Mano> manos = List.of(mano);
        when(manoRepository.findAllManoByJugadorId(jugador3.getId())).thenReturn(manos);
        when(jugadorService.findById(jugador3.getId())).thenReturn(jugador3);

        assertThrows(ApuestaNoValidaException.class, () -> manoService.apuesta(5, jugador3.getId()));
    }

    @Test
    public void shouldApuestaExactaNumCartas() {
        List<Mano> manos = List.of(mano);
        when(manoRepository.findAllManoByJugadorId(jugador3.getId())).thenReturn(manos);
        when(jugadorService.findById(jugador3.getId())).thenReturn(jugador3);

        manoService.apuesta(3, jugador3.getId());

        assertEquals(3, mano.getApuesta());
        assertEquals(3, jugador3.getApuestaActual());
        verify(manoRepository, times(1)).save(mano);
        verify(jugadorService, times(1)).updateJugador(jugador3, jugador3.getId());
    }

    @Test
    public void shouldApuestaCero() {
        List<Mano> manos = List.of(mano);
        when(manoRepository.findAllManoByJugadorId(jugador3.getId())).thenReturn(manos);
        when(jugadorService.findById(jugador3.getId())).thenReturn(jugador3);

        manoService.apuesta(0, jugador3.getId());

        assertEquals(0, mano.getApuesta());
        assertEquals(0, jugador3.getApuestaActual());
        verify(manoRepository, times(1)).save(mano);
        verify(jugadorService, times(1)).updateJugador(jugador3, jugador3.getId());
    }
        
 */