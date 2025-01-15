package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UsuarioPartidaEnJuegoEsperandoException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.ApuestaNoValidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.MinJugadoresPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.MismoNombrePartidaNoTerminadaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.NoPuedeApostarException;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private RondaRepository rondaRepository;

    @Mock
    private RondaService rondaService;

    @Mock
    private JugadorService jugadorService;

    @Mock
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
    private static final int ULTIMA_RONDA = 10;

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

        Mano mano = new Mano();
        mano.setId(1);
        mano.setResultado(0);
        mano.setJugador(jugador);
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
        
        when(partidaRepository.findByNombreAndEstado("Partida Test", PartidaEstado.ESPERANDO))
            .thenReturn(List.of(new Partida()));

        assertThrows(MismoNombrePartidaNoTerminadaException.class, () -> partidaService.save(partida));

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


    @Test
    void shouldFinalizarPartida() {
        User ganador = new User();
        ganador.setUsername("Ganador");
    
        User user2 = new User();
        user2.setId(2);
        user2.setUsername("User2");
    
        Jugador jugador2 = new Jugador();
        jugador2.setId(2);
        jugador2.setUsuario(user2);
        jugador2.setPuntos(50);
        jugador2.setPartida(partida);
    
        jugador.setPuntos(100);
        jugador.setUsuario(ganador);
    
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(List.of(jugador, jugador2));
    
        partidaService.finalizarPartida(1);
    
        verify(userService, times(3)).saveUser(any(User.class)); // Se llama tres veces
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
    void shouldFinalizarPartidaNumPartidasGanadasEsNull() {
        Partida partida = new Partida();
        partida.setId(1);
    
        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPartida(partida);
        jugador.setPuntos(50);
    
        User usuarioJugador = new User();
        usuarioJugador.setId(1);
        usuarioJugador.setNumPartidasGanadas(null);
        jugador.setUsuario(usuarioJugador);
    
        List<Jugador> jugadoresPartida = List.of(jugador);
    
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(jugadoresPartida);
    
        partidaService.finalizarPartida(1);
    
        assertEquals(1, usuarioJugador.getNumPartidasGanadas().intValue());
    
        verify(userService, times(2)).saveUser(usuarioJugador);
    }
    

    @Test
    void shouldFinalizarPartidaNumPuntosGanadosYPartidasJugadasEsNull() {
        Partida partida = new Partida();
        partida.setId(1);
    
        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPartida(partida);
        jugador.setPuntos(50);
    
        User usuarioJugador = new User();
        usuarioJugador.setId(1);
        usuarioJugador.setNumPuntosGanados(null);
        usuarioJugador.setNumPartidasJugadas(null);
        jugador.setUsuario(usuarioJugador);
    
        List<Jugador> jugadoresPartida = List.of(jugador);
    
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(jugadoresPartida);
    
        partidaService.finalizarPartida(1);
    
        assertEquals(50, usuarioJugador.getNumPuntosGanados().intValue());
        assertEquals(1, usuarioJugador.getNumPartidasJugadas().intValue());
    
        verify(userService, times(2)).saveUser(usuarioJugador);
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
        Jugador jugador1 = new Jugador();
        jugador1.setPuntos(150);
        Jugador jugador2 = new Jugador();
        jugador2.setPuntos(150);

        List<Jugador> jugadores = Arrays.asList(jugador1, jugador2);
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(jugadores);

        Jugador ganador = partidaService.getJugadorGanador(1);

        assertNotNull(ganador);
        assertTrue(jugador1.equals(ganador) || jugador2.equals(ganador));
        verify(jugadorService, times(1)).findJugadoresByPartidaId(1);
    }

    @Test
    void shouldGetJugadorGanadorListaVacia() {
        List<Jugador> jugadores = Arrays.asList();
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(jugadores);

        assertThrows(NoSuchElementException.class, () -> partidaService.getJugadorGanador(1));
        verify(jugadorService, times(1)).findJugadoresByPartidaId(1);
    }

    @Test
    void shouldUsuarioJugadorEnPartidaEnEspera() {
        Jugador jugador = new Jugador();
        jugador.setUsuario(owner);
        Partida partida = new Partida();
        partida.setEstado(PartidaEstado.ESPERANDO);
        jugador.setPartida(partida);

        when(jugadorService.findAll()).thenReturn(List.of(jugador));

        Boolean result = partidaService.usuarioJugadorEnPartida(partidaCrear);

        assertTrue(result);
        verify(jugadorService, times(1)).findAll();
    }

    @Test
    void shouldUsuarioJugadorEnPartidaJugando() {
        Jugador jugador = new Jugador();
        jugador.setUsuario(owner);
        Partida partida = new Partida();
        partida.setEstado(PartidaEstado.JUGANDO);
        jugador.setPartida(partida);

        when(jugadorService.findAll()).thenReturn(List.of(jugador));

        Boolean result = partidaService.usuarioJugadorEnPartida(partidaCrear);

        assertTrue(result);
        verify(jugadorService, times(1)).findAll();
    }


    @Test
    void shouldUsuarioJugadorEnPartidaTerminada() {
        Jugador jugador = new Jugador();
        jugador.setUsuario(owner);
        Partida partida = new Partida();
        partida.setEstado(PartidaEstado.TERMINADA);
        jugador.setPartida(partida);

        when(jugadorService.findAll()).thenReturn(List.of(jugador));

        Boolean result = partidaService.usuarioJugadorEnPartida(partidaCrear);

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
    void shouldSiguienteEstadoCambiaBaza() {
        Partida partida = new Partida();
        partida.setId(1);
        partida.setTurnoActual(1);
    
        Jugador jugador = new Jugador();
        jugador.setId(1);
    
        Mano mano = new Mano();
        mano.setId(1);
        mano.setResultado(0);
        mano.setJugador(jugador);
    
        Ronda ronda = new Ronda();
        ronda.setId(1);
        ronda.setNumRonda(1);
        ronda.setNumBazas(5);
        ronda.setPartida(partida);
    
        Baza baza = new Baza();
        baza.setId(1);
        baza.setNumBaza(1);
        baza.setRonda(ronda);
    
        Baza newBaza = new Baza();
        newBaza.setId(2);
        newBaza.setTurnos(List.of(1, 2, 3));
    
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida)); // Mockear el repository para devolver la partida
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(List.of(jugador));
        when(bazaService.findById(1)).thenReturn(baza);
        when(bazaService.nextBaza(1, List.of(jugador))).thenReturn(newBaza);
        when(bazaService.findBazaActualByRondaId(1)).thenReturn(newBaza);
        when(manoService.findAllManosByRondaId(1)).thenReturn(List.of(mano)); // Mockear la respuesta de findAllManosByRondaId
        when(manoService.findLastManoByJugadorId(jugador.getId())).thenReturn(mano); // Mockear para obtener la mano del jugador
        doNothing().when(manoService).actualizarResultadoMano(any(Baza.class));
    
        partidaService.siguienteEstado(1, 1);
    
        verify(messagingTemplate).convertAndSend(eq("/topic/listaTrucos/partida/1"), anyList());
        verify(manoService).actualizarResultadoMano(baza);
        verify(messagingTemplate).convertAndSend(eq("/topic/nuevasManos/partida/1"), anyList());
        verify(bazaService).nextBaza(1, List.of(jugador));
        verify(messagingTemplate).convertAndSend(eq("/topic/nuevaBaza/partida/1"), any(Baza.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/turnoActual/1"), eq(1));
    }
    

    @Test
    void shouldSiguienteEstadoFinalizaRondaPeroNoPartida() {
        Partida partida = new Partida();
        partida.setId(1);
        partida.setTurnoActual(1);
    
        Jugador jugador = new Jugador();
        jugador.setId(1);
    
        Mano mano = new Mano();
        mano.setId(1);
        mano.setResultado(0);
        mano.setJugador(jugador);
    
        Ronda ronda = new Ronda();
        ronda.setId(1);
        ronda.setNumRonda(1);
        ronda.setNumBazas(1);
        ronda.setPartida(partida);
    
        Baza baza = new Baza();
        baza.setId(1);
        baza.setNumBaza(1);
        baza.setRonda(ronda);
    
        Baza primeraBaza = new Baza();
        primeraBaza.setId(2);
        primeraBaza.setTurnos(List.of(1, 2, 3));
    
        Ronda nuevaRonda = new Ronda();
        nuevaRonda.setId(2);
        nuevaRonda.setNumRonda(2);
        nuevaRonda.setNumBazas(5);
        nuevaRonda.setPartida(partida);
    
        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida)); // Mockear el repository para devolver la partida
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(List.of(jugador));
        when(bazaService.findById(1)).thenReturn(baza);
        doNothing().when(rondaService).finalizarRonda(1);
        when(manoService.getNumCartasARepartir(2, 1)).thenReturn(5);
        when(rondaService.nextRonda(1, 5)).thenReturn(nuevaRonda); // Asegurar que devuelve la nueva ronda
        when(bazaService.iniciarBaza(nuevaRonda, List.of(jugador))).thenReturn(primeraBaza);
        when(bazaService.findBazaActualByRondaId(nuevaRonda.getId())).thenReturn(primeraBaza);
        when(manoService.findLastManoByJugadorId(jugador.getId())).thenReturn(mano); // Mockear para obtener la mano del jugador
        doNothing().when(manoService).actualizarResultadoMano(any(Baza.class));
    
        partidaService.siguienteEstado(1, 1);
    
        verify(manoService).actualizarResultadoMano(baza);
        verify(rondaService).finalizarRonda(1);
        verify(manoService).getNumCartasARepartir(2, 1);
        verify(rondaService).nextRonda(1, 5);
        verify(manoService).iniciarManos(1, nuevaRonda, List.of(jugador));
        verify(bazaService).iniciarBaza(nuevaRonda, List.of(jugador));
        verify(messagingTemplate).convertAndSend(eq("/topic/apuesta/partida/1"), anyList());
    }
    

    @Test
    void shouldSiguienteEstadoFinalizaPartida() {
        PartidaService partidaServiceSpy = spy(partidaService);

        Partida partida = new Partida();
        partida.setId(1);
        partida.setTurnoActual(1);

        Jugador jugador = new Jugador();
        jugador.setId(1);

        Mano mano = new Mano();
        mano.setId(1);
        mano.setResultado(0);
        mano.setJugador(jugador);

        Ronda ronda = new Ronda();
        ronda.setId(1);
        ronda.setNumRonda(ULTIMA_RONDA);
        ronda.setNumBazas(1);
        ronda.setPartida(partida);

        Baza baza = new Baza();
        baza.setId(1);
        baza.setNumBaza(1);
        baza.setRonda(ronda);

        when(partidaRepository.findById(1)).thenReturn(Optional.of(partida));
        when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(List.of(jugador));
        when(bazaService.findById(1)).thenReturn(baza);
        doNothing().when(rondaService).finalizarRonda(1);
        doNothing().when(partidaServiceSpy).finalizarPartida(1); 
        when(manoService.findLastManoByJugadorId(jugador.getId())).thenReturn(mano); 

        partidaServiceSpy.siguienteEstado(1, 1);

        verify(manoService).actualizarResultadoMano(baza);
        verify(rondaService).finalizarRonda(1);
        verify(partidaServiceSpy).finalizarPartida(1);
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

    @Test
    void shouldGetPuntajeApuestaIgualResultadoCero() {
        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(0);

        Mano mano = new Mano();
        mano.setId(1);
        mano.setApuesta(0);
        mano.setResultado(0);
        mano.setJugador(jugador);

        List<Mano> manos = List.of(mano);

        when(manoService.findAllManosByRondaId(1)).thenReturn(manos);

        partidaService.getPuntaje(5, 1);

        assertEquals(50, jugador.getPuntos());
        verify(jugadorService).updateJugador(jugador, 1);
    }

    @Test
    void shouldGetPuntajeApuestaDiferenteResultadoCero() {
        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(0);

        Mano mano = new Mano();
        mano.setId(1);
        mano.setApuesta(0);
        mano.setResultado(1);
        mano.setJugador(jugador);

        List<Mano> manos = List.of(mano);

        when(manoService.findAllManosByRondaId(1)).thenReturn(manos);

        partidaService.getPuntaje(5, 1);

        assertEquals(-50, jugador.getPuntos());
        verify(jugadorService).updateJugador(jugador, 1);
    }

    @Test
    void shouldGetPuntajeApuestaIgualResultadoConBonificacion() {
        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(0);

        Mano mano = new Mano();
        mano.setId(1);
        mano.setApuesta(3);
        mano.setResultado(3);
        mano.setJugador(jugador);

        List<Mano> manos = List.of(mano);

        when(manoService.findAllManosByRondaId(1)).thenReturn(manos);
        when(bazaService.getPtosBonificacion(1, 1)).thenReturn(10); // Bonificación

        partidaService.getPuntaje(5, 1);

        assertEquals(70, jugador.getPuntos()); // 20 * 3 + 10
        verify(jugadorService).updateJugador(jugador, 1);
    }

    @Test
    void shouldGetPuntajeApuestaDiferenteResultadoConPenalizacion() {
        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(0);

        Mano mano = new Mano();
        mano.setId(1);
        mano.setApuesta(3);
        mano.setResultado(1);
        mano.setJugador(jugador);

        List<Mano> manos = List.of(mano);

        when(manoService.findAllManosByRondaId(1)).thenReturn(manos);

        partidaService.getPuntaje(5, 1);

        assertEquals(-20, jugador.getPuntos()); // -10 * abs(3-1)
        verify(jugadorService).updateJugador(jugador, 1);
    }

    @Test
    void shouldApuestaManoNoEncontrada() {
        when(manoService.findLastManoByJugadorId(1)).thenReturn(null);
    
        Partida partida = new Partida();
        partida.setId(1);
    
        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPartida(partida);
    
        when(jugadorService.findById(1)).thenReturn(jugador);
    
        assertThrows(ResourceNotFoundException.class, () -> {
            partidaService.apuesta(2, 1);
        });
    }
    

    @Test
    void shouldApuestaMayorQueCartas() {
        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPartida(new Partida());

        Mano mano = new Mano();
        mano.setId(1);
        mano.setCartas(new ArrayList<>(List.of(new Carta(), new Carta())));
        mano.setJugador(jugador);

        when(manoService.findLastManoByJugadorId(1)).thenReturn(mano);
        when(jugadorService.findById(1)).thenReturn(jugador);

        assertThrows(ApuestaNoValidaException.class, () -> {
            partidaService.apuesta(3, 1); 
        });
    }

    @Test
    void shouldApuestaValida() {
        Partida partida = new Partida();
        partida.setId(1);
        partida.setTurnoActual(1);
    
        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPartida(partida);
    
        Mano mano = new Mano();
        mano.setId(1);
        mano.setCartas(new ArrayList<>(List.of(new Carta(), new Carta(), new Carta()))); // Mano con 3 cartas
        mano.setJugador(jugador);
    
        when(manoService.findLastManoByJugadorId(1)).thenReturn(mano);
        when(jugadorService.findById(1)).thenReturn(jugador);
        
        doAnswer(invocation -> {
            invocation.getArgument(0);
            return null;
        }).when(manoService).saveMano(any(Mano.class));
    
        doAnswer(invocation -> {
            invocation.getArgument(0);
            return null;
        }).when(jugadorService).updateJugador(any(Jugador.class), anyInt());
    
        partidaService.apuesta(2, 1);
    
        assertEquals(2, mano.getApuesta().intValue());
        assertEquals(2, jugador.getApuestaActual().intValue());
        verify(manoService).saveMano(mano);
        verify(jugadorService).updateJugador(jugador, 1);
    }

    @Test
    void testApuestaJugadorYaHaApostado() {
        Partida partida = new Partida();
        partida.setId(1);

        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPartida(partida);
        jugador.setHaApostado(true); 

        Mano mano = new Mano();
        mano.setId(1);
        mano.setCartas(new ArrayList<>(List.of(new Carta(), new Carta(), new Carta()))); 
        mano.setJugador(jugador);

        when(manoService.findLastManoByJugadorId(1)).thenReturn(mano);
        when(jugadorService.findById(1)).thenReturn(jugador);

        assertThrows(NoPuedeApostarException.class, () -> {
            partidaService.apuesta(2, 1);
        });

        verify(manoService).findLastManoByJugadorId(1);
        verify(jugadorService).findById(1);
    }

    
}
