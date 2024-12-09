package es.us.dp1.lx_xy_24_25.your_game_name.truco;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.Map;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.NoCartaDeManoException;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.PaloBaza;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TrucoServiceTests {

    // List<Truco> findTrucosByBazaId(int bazaId) throws DataAccessException
    // List<Truco> findTrucosByJugadorId(int jugadorId) throws DataAccessException
    // List<Truco> findTrucosByManoId(int manoId) throws DataAccessException
    // Truco findTrucoByBazaIdCartaId(int bazaId, int cartaId) throws DataAccessException o ResourceNotFoundException
    // Truco saveTruco(Truco truco) throws DataAccessException o NoCartaDeManoException
    // Truco updateTruco(Truco truco, int trucoId) throws DataAccessException
    // Map<Integer, Integer> getCartaByJugador(int bazaId)
    // void crearTrucosBaza(Integer idBaza) ResourceNotFoundException

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

    @InjectMocks
    private BazaService bazaService2;

    private Truco truco;
    private Baza baza;
    private Ronda ronda;
    private Mano mano;
    private Jugador jugador;
    private Carta carta;
    private Partida partida;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        partida = new Partida();

        carta = new Carta();
        carta.setId(1);
        jugador = new Jugador();
        jugador.setId(1);
        mano = new Mano();
        mano.setId(1);
        mano.setJugador(jugador);
        mano.setCartas(List.of(carta));
        baza = new Baza();
        baza.setId(1);
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
        // Arrange
        when(trucoRepository.findTrucosByBazaId(baza.getId())).thenReturn(List.of(truco));

        // Act
        List<Truco> result = trucoService.findTrucosByBazaId(baza.getId());

        // Assert
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
        // Arrange
        when(trucoRepository.findByJugadorId(jugador.getId())).thenReturn(List.of(truco));

        // Act
        List<Truco> result = trucoService.findTrucosByJugadorId(jugador.getId());

        // Assert
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
    public void shouldFindTrucosByManoId() throws DataAccessException {
        // Arrange
        when(trucoRepository.findByManoId(mano.getId())).thenReturn(List.of(truco));

        // Act
        List<Truco> result = trucoService.findTrucosByManoId(mano.getId());

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(mano, result.get(0).getMano());

        verify(trucoRepository).findByManoId(mano.getId());
    }

    public void shouldNotFindTrucosByManoId(){
        List<Truco> result = trucoService.findTrucosByManoId(999);
        assertTrue(result.isEmpty(), "Se esperaba que la lista devuelta sea vacía cuando no existen Trcos de dicha Mano");
    }

    @Test
    public void shouldFindTrucoByBazaIdCartaId() throws DataAccessException {
        // Arrange
        when(trucoRepository.findTrucoByBazaIdCartaId(baza.getId(), carta.getId())).thenReturn(Optional.of(truco));

        // Act
        Truco result = this.trucoService.findTrucoByBazaIdCartaId(baza.getId(), carta.getId());

        // Assert
        assertEquals(1, result.getId());
        verify(trucoRepository).findTrucoByBazaIdCartaId(baza.getId(), carta.getId());
    }

    @Test
    public void shouldNotFindTrucoByBazaIdCartaId(){
        when(trucoRepository.findTrucoByBazaIdCartaId(999, 999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            trucoService.findTrucoByBazaIdCartaId(999, 999);
        }, "Se esperaba que se lanzara una ResourceNotFoundException cuando no se encuentra el Truco");
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

/*
    @Test
    public void shouldNotUpdateTrucoIncorrecto() {
        when(trucoRepository.findById(truco.getId())).thenReturn(Optional.of(truco));

        Carta newCarta = new Carta();
        newCarta.setId(999);

        Truco newTruco = new Truco();
        newTruco.setId(truco.getId());
        newTruco.setBaza(baza);
        newTruco.setJugador(jugador);
        newTruco.setMano(mano);
        newTruco.setCarta(newCarta);

        assertThrows(NoCartaDeManoException.class, () -> {
            trucoService.updateTruco(newTruco, truco.getId());
        }, "No se puede actualizar un Truco si el idCarta no está en las cartas de la mano");

    }
*/

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
    void testCalculoGanador() {
        Baza baza2 = new Baza();
        baza2.setId(1);
        baza2.setPaloBaza(PaloBaza.morada);
        baza2.setNumBaza(1);
        baza2.setGanador(null);
        baza2.setCartaGanadora(null);
        baza2.setRonda(ronda);

        Carta carta2 = new Carta();
        carta2.setId(30);
        carta2.setImagenFrontal("./images/cartas/verde_1.png");
        carta2.setImagenTrasera("./images/cartas/parte_trasera.png");
        carta2.setNumero(10);
        carta2.setTipoCarta(TipoCarta.morada);

        Carta carta1 = new Carta();
        carta1.setId(15);
        carta1.setImagenFrontal("./images/cartas/verde_1.png");
        carta1.setImagenTrasera("./images/cartas/parte_trasera.png");
        carta1.setNumero(3);
        carta1.setTipoCarta(TipoCarta.morada);

        Truco truco2 = new Truco();
        truco2.setId(10);
        truco2.setBaza(baza2);
        truco2.setCarta(carta2);
        truco2.setJugador(jugador);
        truco2.setMano(mano);
        truco2.setTurno(2);

        Truco truco1 = new Truco();
        truco1.setId(15);
        truco1.setBaza(baza2);
        truco1.setCarta(carta1);
        truco1.setJugador(jugador);
        truco1.setMano(mano);
        truco1.setTurno(3);

        when(bazaRepository.findById(baza2.getId())).thenReturn(Optional.of(baza2));
        when(trucoRepository.findTrucosByBazaId(baza2.getId())).thenReturn(List.of(truco1, truco2));
    
        trucoService.calculoGanador(baza2.getId());
        Baza bazaActualizada = bazaService2.findById(baza2.getId());
    
        assertEquals(bazaActualizada.getCartaGanadora(), truco2.getCarta());
    
        verify(bazaRepository, times(1)).save(baza2);
    }

    // void crearTrucosBaza(Integer idBaza) ResourceNotFoundException
    // @Test
    // public void shouldCrearTrucosBaza() {

        // ronda.setPartida(partida);
        // baza1.setRonda(ronda);
        // when(bazaRepository.findById(baza1.getId())).thenReturn(Optional.of(baza1));
        // when(bazaService.findById(baza1.getId())).thenReturn(baza1);
        // when(jugadorRepository.findJugadoresByPartidaId(partida.getId())).thenReturn(List.of(jugador1, jugador2));
        // when(manoRepository.findAllManoByJugadorId(jugador1.getId())).thenReturn(List.of(mano1));
        // when(manoRepository.findAllManoByJugadorId(jugador2.getId())).thenReturn(List.of(mano2));
        // when(jugadorService.findJugadoresByPartidaId(partida.getId())).thenReturn(List.of(jugador1, jugador2));
        // trucoService.crearTrucosBaza(baza1.getId());
        // verify(trucoRepository, times(2)).save(any(Truco.class));
    // }

}
