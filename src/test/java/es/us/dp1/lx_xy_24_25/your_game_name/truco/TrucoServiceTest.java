package es.us.dp1.lx_xy_24_25.your_game_name.truco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.anyMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.NoCartaDeManoException;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.PaloBaza;
import es.us.dp1.lx_xy_24_25.your_game_name.bazaCartaManoDTO.BazaCartaManoDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import jakarta.persistence.EntityNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TrucoServiceTest {

    @Mock
    private TrucoRepository trucoRepository;

    @Mock
    private BazaRepository bazaRepository;

    @Mock
    private BazaService bazaService;

    @Mock
    private JugadorRepository jugadorRepository;

    @Mock
    private JugadorService jugadorService;

    @Mock
    private ManoRepository manoRepository;

    @InjectMocks
    private TrucoService trucoService;

    @Mock
    private BazaService bazaService2;

    @Mock
    private ManoService manoService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private PartidaService partidaService;

    private Truco truco;
    private Baza baza;
    private Ronda ronda;
    private Mano mano;
    private Jugador jugador;
    private Carta carta;
    private Partida partida;

    private final Integer idComodinPirata = 72;
	private final Integer idComodinBanderaBlanca = 71;
	private final Integer idTigresa = 65;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        partida = new Partida();
        partida.setId(1);

        carta = new Carta();
        carta.setId(1);
        carta.setTipoCarta(TipoCarta.amarillo);
        carta.setNumero(10);

        jugador = new Jugador();
        jugador.setId(1);
        jugador.setPartida(partida);

        mano = new Mano();
        mano.setId(1);
        mano.setJugador(jugador);
        mano.setCartas(List.of(carta));

        ronda = new Ronda();
        ronda.setId(1);

        baza = new Baza();
        baza.setId(1);
        baza.setRonda(ronda);
        baza.setTurnos(new ArrayList<>(List.of(1, 2, 3))); 
        baza.setPaloBaza(PaloBaza.amarillo);

        truco = new Truco();
        truco.setId(1);
        truco.setBaza(baza);
        truco.setJugador(jugador);
        truco.setMano(mano);
        truco.setCarta(carta);
        truco.setTurno(1);

    }




    @Test
    void shouldFindAllTrucos() {
        Iterable<Truco> trucoList = Arrays.asList(truco);
        when(trucoRepository.findAll()).thenReturn(trucoList);

        Iterable<Truco> trucosDevueltos = trucoService.findAllTrucos();
        List<Truco> result = (List<Truco>) trucosDevueltos;

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(truco, result.get(0));
        verify(trucoRepository, times(1)).findAll();
    }

    @Test
    void shouldNotFindAllTrucos() {
        when(trucoRepository.findAll()).thenReturn(Collections.emptyList());

        Iterable<Truco> trucosDevueltos = trucoService.findAllTrucos();
        List<Truco> result = (List<Truco>) trucosDevueltos;

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindTrucoById() {
        when(trucoRepository.findById(truco.getId())).thenReturn(Optional.of(truco));

        Truco result = trucoService.findTrucoById(truco.getId());

        assertNotNull(result);
        assertEquals(truco.getId(), result.getId());
        verify(trucoRepository, times(1)).findById(truco.getId());
    }

    @Test
    void shouldNotFindTrucoById() {
        when(trucoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            trucoService.findTrucoById(999);
        }, "Se esperaba que se lanzara una ResourceNotFoundException cuando no se encuentra el Truco");
    }

	@Test
    public void shouldFindTrucosByBazaId() throws DataAccessException {
        when(trucoRepository.findTrucosByBazaId(baza.getId())).thenReturn(List.of(truco));

        List<Truco> result = trucoService.findTrucosByBazaId(baza.getId());
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(baza, result.get(0).getBaza());

        verify(trucoRepository).findTrucosByBazaId(baza.getId());
    }

    @Test
    public void shouldNotFindTrucosByBazaId(){
        List<Truco> result = trucoService.findTrucosByBazaId(9999);
        assertTrue(result.isEmpty(), "Se esperaba que la lista devuelta sea vacía cuando no existen Trcos de dicha Baza");
    }

    @Test
    public void shouldFindTrucosByJugadorId() throws DataAccessException {
        when(trucoRepository.findByJugadorId(jugador.getId())).thenReturn(List.of(truco));

        List<Truco> result = trucoService.findTrucosByJugadorId(jugador.getId());
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(jugador, result.get(0).getJugador());

        verify(trucoRepository).findByJugadorId(jugador.getId());
    }

    @Test
    public void shouldNotFindTrucosByJugadorId(){
        List<Truco> result = trucoService.findTrucosByJugadorId(999);
        assertTrue(result.isEmpty(), "Se esperaba que la lista devuelta sea vacía cuando no existen Trcos de dicha Jugador");
    }

    @Test
    public void shouldSaveTruco() {
        when(trucoRepository.save(truco)).thenReturn(truco);

        Truco result = trucoService.saveTruco(truco);
        assertEquals(truco.getId(), result.getId());
        verify(trucoRepository).save(truco);
    }

    @Test
    public void shouldNotSaveTruco() {
        Carta newCarta = new Carta();
        newCarta.setId(999);
        truco.setCarta(newCarta);

        assertThrows( NoCartaDeManoException.class, () -> {
            trucoService.saveTruco(truco);
        }, "Se esperaba que se lanzara una NoCartaDeManoException cuando la carta del Trco no es parte de las cartas de la mano");
    }

    @Test
    void testSaveTrucoConCartaNull() {
        Baza baza = new Baza();
        baza.setId(1);

        Mano mano = new Mano();
        mano.setId(1);

        Jugador jugador = new Jugador();
        jugador.setId(1);

        Truco truco = new Truco();
        truco.setBaza(baza);
        truco.setMano(mano);
        truco.setJugador(jugador);
        truco.setTurno(1);
        truco.setCarta(null);

        TrucoService trucoServiceSpy = spy(trucoService);

        when(trucoRepository.save(any(Truco.class))).thenReturn(truco);

        Truco resultado = trucoServiceSpy.saveTruco(truco);

        assertNotNull(resultado, "El resultado no debería ser null");
        assertEquals(truco, resultado);
        verify(trucoRepository).save(truco);
    }

    @Test
    void testSaveTrucoConCartaNoEnMano() {
        Baza baza = new Baza();
        baza.setId(1);

        Carta carta = new Carta();
        carta.setId(1);
        carta.setTipoCarta(TipoCarta.amarillo);

        Carta cartaNoEnMano = new Carta();
        cartaNoEnMano.setId(2);
        cartaNoEnMano.setTipoCarta(TipoCarta.verde);

        Mano mano = new Mano();
        mano.setId(1);
        mano.setCartas(new ArrayList<>(List.of(carta)));

        Jugador jugador = new Jugador();
        jugador.setId(1);

        Truco truco = new Truco();
        truco.setBaza(baza);
        truco.setMano(mano);
        truco.setJugador(jugador);
        truco.setTurno(1);
        truco.setCarta(cartaNoEnMano);

        TrucoService trucoServiceSpy = spy(trucoService);

        assertThrows(NoCartaDeManoException.class, () -> {
            trucoServiceSpy.saveTruco(truco);
        });

        verify(trucoRepository, never()).save(any(Truco.class));
    }

    @Test
    void testSaveTrucoConCartaEnMano() {
        Baza baza = new Baza();
        baza.setId(1);

        Carta carta = new Carta();
        carta.setId(1);
        carta.setTipoCarta(TipoCarta.amarillo);

        Mano mano = new Mano();
        mano.setId(1);
        mano.setCartas(new ArrayList<>(List.of(carta))); 

        Jugador jugador = new Jugador();
        jugador.setId(1);

        Truco truco = new Truco();
        truco.setBaza(baza);
        truco.setMano(mano);
        truco.setJugador(jugador);
        truco.setTurno(1);
        truco.setCarta(carta);

        TrucoService trucoServiceSpy = spy(trucoService);

        when(trucoRepository.save(any(Truco.class))).thenReturn(truco);

        Truco resultado = trucoServiceSpy.saveTruco(truco);

        assertNotNull(resultado, "El resultado no debería ser null");
        assertEquals(truco, resultado);
        verify(trucoRepository).save(truco);
    }


    @Test
    public void shouldUpdateTruco() {
        Carta newCarta = new Carta();
        newCarta.setId(999);

        Mano newMano = new Mano();
        newMano.setId(999);
        newMano.setCartas(List.of(carta, newCarta));

        Truco newTruco = new Truco();
        newTruco.setId(truco.getId());
        newTruco.setBaza(baza);
        newTruco.setJugador(jugador);
        newTruco.setMano(newMano);
        newTruco.setCarta(newCarta);

        when(trucoRepository.findById(truco.getId())).thenReturn(Optional.of(truco));

        Truco updatedTruco = trucoService.updateTruco(newTruco, truco.getId());

        assertNotNull(updatedTruco);
        assertEquals(newTruco.getBaza(), updatedTruco.getBaza());
    }

    @Test
    public void shouldNotUpdateTrucoInexistente() {
        Truco newTruco = new Truco();
        newTruco.setId(999);
        when(trucoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            trucoService.updateTruco(newTruco, 999);
        }, "No se puede actualizar un Truco si su id no esté en la base de datos");
    }

    @Test
    public void shouldDeleteTruco() {
        when(trucoRepository.findById(truco.getId())).thenReturn(Optional.of(truco));

        trucoService.deleteTruco(truco.getId());
        verify(trucoRepository, times(1)).delete(truco);
    }

    @Test
    public void shouldNotDeleteTruco() {
        when(trucoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            trucoService.deleteTruco(999);
        }, "No se puede borrar un Truco que no existe");
    }
    
    @Test
    void shouldThrowExceptionWhenJugadorNotFound() {
        Integer jugadorId = 1;
        when(jugadorService.findById(jugadorId)).thenThrow(new EntityNotFoundException("Jugador no encontrado"));

        BazaCartaManoDTO dto = new BazaCartaManoDTO(baza, carta, mano, 1);

        assertThrows(EntityNotFoundException.class, () -> trucoService.jugarTruco(dto, jugadorId));
        verify(jugadorService, times(1)).findById(jugadorId);
    }

    @Test
    void shouldCreateAndSendTrucoToWebSocket() {
        Integer jugadorId = 1;
    
        when(jugadorService.findById(jugadorId)).thenReturn(jugador);
        when(trucoRepository.save(any(Truco.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trucoService.findTrucosByBazaId(baza.getId())).thenReturn(List.of(truco));
        when(bazaService.findById(baza.getId())).thenReturn(baza); // Mock de bazaService.findById
    
        BazaCartaManoDTO dto = new BazaCartaManoDTO(baza, carta, mano, 1);
    
        Truco resultado = trucoService.jugarTruco(dto, jugadorId);
    
        assertNotNull(resultado);
    
        verify(trucoRepository, times(1)).save(any(Truco.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/baza/truco/partida/" + partida.getId()), anyList());
        verify(bazaService, times(1)).findById(baza.getId());
    }
    

    @Test
    public void testJugarTruco() {
        BazaCartaManoDTO dto = new BazaCartaManoDTO();
        dto.setBaza(baza);
        dto.setMano(mano);
        dto.setTurno(1);
        dto.setCarta(carta);

        when(jugadorService.findById(anyInt())).thenReturn(jugador);
        when(trucoRepository.save(any(Truco.class))).thenReturn(truco);
        when(bazaService.findById(anyInt())).thenReturn(baza);
        when(jugadorService.findJugadoresByPartidaId(anyInt())).thenReturn(List.of(jugador));

        Truco result = trucoService.jugarTruco(dto, 1);

        assertNotNull(result);
        assertEquals(truco, result);
    }


    @Test
    void testJugarTrucoCambiaPaloBaza() {
        Baza baza = new Baza();
        baza.setId(1);
        baza.setPaloBaza(PaloBaza.sinDeterminar);
        Ronda ronda = new Ronda();
        ronda.setId(1);
        baza.setRonda(ronda);

        Carta carta = new Carta();
        carta.setId(1);
        carta.setTipoCarta(TipoCarta.amarillo);

        Mano mano = new Mano();
        mano.setId(1);
        mano.setCartas(new ArrayList<>(List.of(carta)));

        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPartida(partida);

        BazaCartaManoDTO dto = new BazaCartaManoDTO();
        dto.setBaza(baza);
        dto.setMano(mano);
        dto.setCarta(carta);
        dto.setTurno(1);

        Truco truco = new Truco();
        truco.setBaza(baza);
        truco.setMano(mano);
        truco.setJugador(jugador);
        truco.setTurno(1);
        truco.setCarta(carta);

        List<Truco> trucos = List.of(truco);

        TrucoService trucoServiceSpy = spy(trucoService);

        when(jugadorService.findById(1)).thenReturn(jugador);
        when(trucoRepository.save(any(Truco.class))).thenReturn(truco);
        when(bazaService.findById(1)).thenReturn(baza);
        when(jugadorService.findJugadoresByPartidaId(partida.getId())).thenReturn(List.of(jugador));
        when(trucoServiceSpy.findTrucosByBazaId(1)).thenReturn(trucos); // Devolver la lista con el truco y carta no nula
        when(manoService.saveMano(any(Mano.class))).thenReturn(mano); // Configurar para devolver mano
        when(manoService.findAllManosByRondaId(anyInt())).thenReturn(List.of(mano));

        doReturn(baza).when(trucoServiceSpy).cambiarPaloBaza(any(Baza.class), any(Truco.class));
        doNothing().when(trucoServiceSpy).mandarCartasDisabled(any(Baza.class), any(Ronda.class), any(Partida.class));

        Jugador ganadorMock = mock(Jugador.class);
        doReturn(ganadorMock).when(trucoServiceSpy).calculoGanador(anyInt());

        doNothing().when(messagingTemplate).convertAndSend(eq("/topic/baza/truco/partida/" + partida.getId()), anyList());
        doNothing().when(messagingTemplate).convertAndSend(eq("/topic/nuevasManos/partida/" + partida.getId()), anyList());
        doNothing().when(messagingTemplate).convertAndSend(eq("/topic/ganadorBaza/partida/" + partida.getId()), any(Jugador.class));
        doNothing().when(messagingTemplate).convertAndSend(eq("/topic/listaTrucos/partida/" + partida.getId()), anyList());
        doNothing().when(partidaService).siguienteEstado(eq(partida.getId()), eq(baza.getId()));

        Truco trucoIniciado = trucoServiceSpy.jugarTruco(dto, 1);

        verify(trucoServiceSpy).cambiarPaloBaza(eq(baza), eq(truco));
        verify(trucoServiceSpy).mandarCartasDisabled(eq(baza), eq(ronda), eq(partida));
        verify(messagingTemplate).convertAndSend(eq("/topic/baza/truco/partida/" + partida.getId()), anyList());
        verify(messagingTemplate).convertAndSend(eq("/topic/nuevasManos/partida/" + partida.getId()), anyList());
        verify(messagingTemplate).convertAndSend(eq("/topic/ganadorBaza/partida/" + partida.getId()), any(Jugador.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/listaTrucos/partida/" + partida.getId()), anyList());
        verify(partidaService).siguienteEstado(eq(partida.getId()), eq(baza.getId()));
    }


    @Test
    void testJugarTrucoEsUltimoTruco() {
        Baza baza = new Baza();
        baza.setId(1);
        Ronda ronda = new Ronda();
        ronda.setId(1);
        baza.setRonda(ronda);
        baza.setPaloBaza(PaloBaza.amarillo);

        Carta carta = new Carta();
        carta.setId(1);
        carta.setTipoCarta(TipoCarta.amarillo);

        Mano mano = new Mano();
        mano.setId(1);
        mano.setCartas(new ArrayList<>(List.of(carta)));

        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPartida(partida);

        BazaCartaManoDTO dto = new BazaCartaManoDTO();
        dto.setBaza(baza);
        dto.setMano(mano);
        dto.setCarta(carta);
        dto.setTurno(1);

        Truco truco = new Truco();
        truco.setBaza(baza);
        truco.setMano(mano);
        truco.setJugador(jugador);
        truco.setTurno(1);
        truco.setCarta(carta);

        List<Truco> trucos = List.of(truco);

        TrucoService trucoServiceSpy = spy(trucoService);

        when(jugadorService.findById(1)).thenReturn(jugador);
        when(trucoRepository.save(any(Truco.class))).thenReturn(truco);
        when(bazaService.findById(1)).thenReturn(baza);
        when(jugadorService.findJugadoresByPartidaId(partida.getId())).thenReturn(List.of(jugador));
        when(trucoServiceSpy.findTrucosByBazaId(1)).thenReturn(trucos);
        when(manoService.saveMano(any(Mano.class))).thenReturn(mano);
        when(manoService.findAllManosByRondaId(anyInt())).thenReturn(List.of(mano));

        doReturn(jugador).when(trucoServiceSpy).calculoGanador(anyInt());

        doNothing().when(messagingTemplate).convertAndSend(eq("/topic/baza/truco/partida/" + partida.getId()), anyList());
        doNothing().when(messagingTemplate).convertAndSend(eq("/topic/nuevasManos/partida/" + partida.getId()), anyList());
        doNothing().when(messagingTemplate).convertAndSend(eq("/topic/ganadorBaza/partida/" + partida.getId()), any(Jugador.class));
        doNothing().when(messagingTemplate).convertAndSend(eq("/topic/listaTrucos/partida/" + partida.getId()), anyList());
        doNothing().when(partidaService).siguienteEstado(eq(partida.getId()), eq(baza.getId()));

        Truco trucoIniciado = trucoServiceSpy.jugarTruco(dto, 1);

        verify(trucoServiceSpy).calculoGanador(1);
        verify(messagingTemplate).convertAndSend(eq("/topic/ganadorBaza/partida/" + partida.getId()), any(Jugador.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/listaTrucos/partida/" + partida.getId()), anyList());
        verify(partidaService).siguienteEstado(eq(partida.getId()), eq(baza.getId()));
    }


    @Test
    public void testManoSinCarta() {
        trucoService.manoSinCarta(truco, partida, ronda);

        verify(manoService, times(1)).saveMano(any(Mano.class));
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), anyList());
    }

    @Test
    void testManoSinCartaCartaNormal() {
        Carta cartaJugada = new Carta();
        cartaJugada.setId(1);
        cartaJugada.setTipoCarta(TipoCarta.amarillo);

        Carta otraCarta = new Carta();
        otraCarta.setId(2);
        otraCarta.setTipoCarta(TipoCarta.verde);

        Mano mano = new Mano();
        mano.setCartas(List.of(cartaJugada, otraCarta));

        Truco truco = new Truco();
        truco.setCarta(cartaJugada);
        truco.setMano(mano);

        trucoService.manoSinCarta(truco, partida, ronda);

        assertEquals(1, truco.getMano().getCartas().size());
        assertEquals(otraCarta.getId(), truco.getMano().getCartas().get(0).getId());
    }


    @Test
    void testManoSinCartaComodinBanderaBlanca() {
        Carta cartaJugada = new Carta();
        cartaJugada.setId(idComodinBanderaBlanca);
        cartaJugada.setTipoCarta(TipoCarta.banderaBlanca);

        Carta cartaTigresa = new Carta();
        cartaTigresa.setId(idTigresa);
        cartaTigresa.setTipoCarta(TipoCarta.tigresa);

        Carta otraCarta = new Carta();
        otraCarta.setId(4);
        otraCarta.setTipoCarta(TipoCarta.amarillo);

        Mano mano = new Mano();
        mano.setCartas(List.of(cartaTigresa, otraCarta));

        Truco truco = new Truco();
        truco.setCarta(cartaJugada);
        truco.setMano(mano);

        when(manoService.saveMano(any(Mano.class))).thenReturn(mano);
        when(manoService.findAllManosByRondaId(anyInt())).thenReturn(List.of(mano));

        trucoService.manoSinCarta(truco, partida, ronda);

        assertEquals(1, truco.getMano().getCartas().size());
        assertEquals(otraCarta.getId(), truco.getMano().getCartas().get(0).getId());
    }

    @Test
    void testManoSinCartaComodinPirata() {
        Carta cartaJugada = new Carta();
        cartaJugada.setId(idComodinPirata);
        cartaJugada.setTipoCarta(TipoCarta.pirata);

        Carta cartaTigresa = new Carta();
        cartaTigresa.setId(idTigresa);
        cartaTigresa.setTipoCarta(TipoCarta.tigresa);

        Carta otraCarta = new Carta();
        otraCarta.setId(4);
        otraCarta.setTipoCarta(TipoCarta.amarillo);

        Mano mano = new Mano();
        mano.setCartas(List.of(cartaTigresa, otraCarta));

        Truco truco = new Truco();
        truco.setCarta(cartaJugada);
        truco.setMano(mano);

        when(manoService.saveMano(any(Mano.class))).thenReturn(mano);
        when(manoService.findAllManosByRondaId(anyInt())).thenReturn(List.of(mano));

        trucoService.manoSinCarta(truco, partida, ronda);

        assertEquals(1, truco.getMano().getCartas().size());
        assertEquals(otraCarta.getId(), truco.getMano().getCartas().get(0).getId());
    }


    @Test
    public void testCambiarPaloBaza() {
        when(bazaService.saveBaza(any(Baza.class))).thenAnswer(invocation -> invocation.getArgument(0));
    
        Baza result = trucoService.cambiarPaloBaza(baza, truco);
    
        assertNotNull(result);
        assertEquals(baza, result);
        verify(bazaService, times(1)).saveBaza(any(Baza.class));
    }

    @Test
    void testCambiarPaloBazaSinDeterminar() {
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.banderaBlanca);
        Truco truco = new Truco();
        truco.setCarta(carta);

        baza.setPaloBaza(PaloBaza.sinDeterminar);

        when(bazaService.saveBaza(baza)).thenReturn(baza);

        Baza result = trucoService.cambiarPaloBaza(baza, truco);

        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(PaloBaza.sinDeterminar, result.getPaloBaza());
    }


    @Test
    void testCambiarPaloBazaNoHayPaloPirata() {
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.pirata);
        Truco truco = new Truco();
        truco.setCarta(carta);
    
        baza.setPaloBaza(PaloBaza.sinDeterminar);
    
        when(bazaService.saveBaza(baza)).thenReturn(baza);
    
        Baza result = trucoService.cambiarPaloBaza(baza, truco);
    
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(PaloBaza.noHayPalo, result.getPaloBaza());
    }
    

    @Test
    void testCambiarPaloBazaNoHayPaloSkullking() {
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.skullking);
        Truco truco = new Truco();
        truco.setCarta(carta);

        baza.setPaloBaza(PaloBaza.sinDeterminar);

        when(bazaService.saveBaza(baza)).thenReturn(baza);

        Baza result = trucoService.cambiarPaloBaza(baza, truco);

        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(PaloBaza.noHayPalo, result.getPaloBaza());
    }

    @Test
    void testCambiarPaloBazaNoHayPaloSirena() {
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.sirena);
        Truco truco = new Truco();
        truco.setCarta(carta);

        baza.setPaloBaza(PaloBaza.sinDeterminar);

        when(bazaService.saveBaza(baza)).thenReturn(baza);

        Baza result = trucoService.cambiarPaloBaza(baza, truco);

        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(PaloBaza.noHayPalo, result.getPaloBaza());
    }

    @Test
    void testCambiarPaloBazaAmarillo() {
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.amarillo);
        Truco truco = new Truco();
        truco.setCarta(carta);

        baza.setPaloBaza(PaloBaza.sinDeterminar);

        when(bazaService.saveBaza(baza)).thenReturn(baza);

        Baza result = trucoService.cambiarPaloBaza(baza, truco);

        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(PaloBaza.amarillo, result.getPaloBaza());
    }

    @Test
    void testCambiarPaloBazaMorada() {
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.morada);
        Truco truco = new Truco();
        truco.setCarta(carta);

        baza.setPaloBaza(PaloBaza.sinDeterminar);

        when(bazaService.saveBaza(baza)).thenReturn(baza);

        Baza result = trucoService.cambiarPaloBaza(baza, truco);

        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(PaloBaza.morada, result.getPaloBaza());
    }

    @Test
    void testCambiarPaloBazaTriunfo() {
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.triunfo);
        Truco truco = new Truco();
        truco.setCarta(carta);

        baza.setPaloBaza(PaloBaza.sinDeterminar);

        when(bazaService.saveBaza(baza)).thenReturn(baza);

        Baza result = trucoService.cambiarPaloBaza(baza, truco);

        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(PaloBaza.triunfo, result.getPaloBaza());
    }

    @Test
    void testCambiarPaloBazaVerde() {
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.verde);
        Truco truco = new Truco();
        truco.setCarta(carta);

        baza.setPaloBaza(PaloBaza.sinDeterminar);

        when(bazaService.saveBaza(baza)).thenReturn(baza);

        Baza result = trucoService.cambiarPaloBaza(baza, truco);

        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(PaloBaza.verde, result.getPaloBaza());
    }

    
    @Test
    public void testCartasDisabledBaza() {
        when(manoService.findAllManosByRondaId(anyInt())).thenReturn(List.of(mano));

        Map<Integer, List<Carta>> result = trucoService.cartasDisabledBaza(ronda.getId(), TipoCarta.amarillo);

        assertNotNull(result);
        verify(manoService, times(1)).cartasDisabled(anyInt(), any(TipoCarta.class));
    }

    @Test
    public void testMandarCartasDisabledAmarillo() {
        baza.setPaloBaza(PaloBaza.amarillo);
    
        Mockito.doNothing().when(messagingTemplate).convertAndSend(anyString(), anyMap());
    
        trucoService.mandarCartasDisabled(baza, ronda, partida);
    
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), anyMap());
    }

    @Test
    public void testMandarCartasDisabledMorada() {
        baza.setPaloBaza(PaloBaza.morada);
    
        Mockito.doNothing().when(messagingTemplate).convertAndSend(anyString(), anyMap());
    
        trucoService.mandarCartasDisabled(baza, ronda, partida);
    
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), anyMap());
    }

    @Test
    public void testMandarCartasDisabledVerde() {
        baza.setPaloBaza(PaloBaza.verde);
    
        Mockito.doNothing().when(messagingTemplate).convertAndSend(anyString(), anyMap());
    
        trucoService.mandarCartasDisabled(baza, ronda, partida);
    
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), anyMap());
    }

    @Test
    public void testMandarCartasDisabledTriunfo() {
        baza.setPaloBaza(PaloBaza.triunfo);
    
        Mockito.doNothing().when(messagingTemplate).convertAndSend(anyString(), anyMap());
    
        trucoService.mandarCartasDisabled(baza, ronda, partida);
    
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), anyMap());
    }

    @Test
    public void testMandarCartasDisabledSinDeterminar() {
        baza.setPaloBaza(PaloBaza.sinDeterminar);
    
        Mockito.doNothing().when(messagingTemplate).convertAndSend(anyString(), anyMap());
    
        trucoService.mandarCartasDisabled(baza, ronda, partida);
    
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), anyMap());
    }

    
    @Test
    public void testCalculoGanador() {
        when(bazaService.findById(anyInt())).thenReturn(baza);
        when(trucoRepository.findTrucosByBazaId(anyInt())).thenReturn(List.of(truco));
    
        Jugador result = trucoService.calculoGanador(baza.getId());
    
        assertNotNull(result);
        verify(bazaService, times(1)).saveBaza(any(Baza.class));
    }

    @Test
    void testCalculoGanadorConPersonaje() {
        Carta cartaPersonaje = new Carta();
        cartaPersonaje.setTipoCarta(TipoCarta.sirena); 
        Truco trucoPersonaje = new Truco();
        trucoPersonaje.setCarta(cartaPersonaje);
        trucoPersonaje.setJugador(jugador);

        List<Truco> trucosBaza = List.of(trucoPersonaje);
        
        when(trucoRepository.findTrucosByBazaId(baza.getId())).thenReturn(trucosBaza);
        when(bazaService.findById(baza.getId())).thenReturn(baza); 

        Jugador ganador = trucoService.calculoGanador(baza.getId());

        assertNotNull(ganador, "El ganador no debería ser null");
        verify(bazaService).saveBaza(any(Baza.class));
    }


    @Test
    void testCalculoGanadorConTriunfo() {
        Carta cartaTriunfo = new Carta();
        cartaTriunfo.setTipoCarta(TipoCarta.triunfo);
        Truco trucoTriunfo = new Truco();
        trucoTriunfo.setCarta(cartaTriunfo);
        trucoTriunfo.setJugador(jugador);

        List<Truco> trucosBaza = List.of(trucoTriunfo);

        when(trucoRepository.findTrucosByBazaId(baza.getId())).thenReturn(trucosBaza);
        when(bazaService.findById(baza.getId())).thenReturn(baza); 

        Jugador ganador = trucoService.calculoGanador(baza.getId());

        assertNotNull(ganador, "El ganador no debería ser null");
        verify(bazaService).saveBaza(any(Baza.class));
    }


    @Test
    void testCalculoGanadorFallback1() {
        Truco trucoFallback = new Truco();
        trucoFallback.setCarta(carta);
        trucoFallback.setJugador(jugador);

        List<Truco> trucosBaza = new ArrayList<>();
        trucosBaza.add(trucoFallback);
        when(trucoRepository.findTrucosByBazaId(baza.getId())).thenReturn(trucosBaza);
        when(bazaService.findById(baza.getId())).thenReturn(baza);

        Jugador ganador = trucoService.calculoGanador(baza.getId());

        assertNotNull(ganador);
        assertEquals(jugador, ganador);
        verify(bazaService).saveBaza(any(Baza.class));
    }


    @Test
    public void testActualizarTurno() {
        partida.setTurnoActual(1);
        baza.setTurnos(List.of(1, 2, 3));
        when(partidaService.update(any(Partida.class), anyInt())).thenReturn(partida);

        trucoService.actualizarTurno(partida, truco);

        verify(messagingTemplate, times(1)).convertAndSend(anyString(), anyInt());
    }

    @Test
    void shouldChangePaloBazaBasedOnTipoCarta() {
        truco.getCarta().setTipoCarta(TipoCarta.verde);

        when(bazaService.saveBaza(any(Baza.class))).thenReturn(baza);

        Baza result = trucoService.cambiarPaloBaza(baza, truco);

        assertNotNull(result);
        assertEquals(PaloBaza.verde, result.getPaloBaza());
        verify(bazaService, times(1)).saveBaza(any(Baza.class));
    }

    @Test
    void shouldSetPaloBazaToDefaultWhenTipoCartaNotMapped() {
        truco.getCarta().setTipoCarta(null);

        when(bazaService.saveBaza(any(Baza.class))).thenReturn(baza);

        Baza result = trucoService.cambiarPaloBaza(baza, truco);

        assertNotNull(result);
        assertEquals(PaloBaza.sinDeterminar, result.getPaloBaza());
    }
    

    @Test
    public void shouldSiguienteTurno() {
        List<Integer> turnos = Arrays.asList(1, 2, 3);
        Integer turnoActual = 1;

        Integer result = trucoService.siguienteTurno(turnos, turnoActual);
        assertEquals(2, result);
    }
}
