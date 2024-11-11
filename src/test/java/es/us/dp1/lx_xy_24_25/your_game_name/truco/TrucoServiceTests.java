package es.us.dp1.lx_xy_24_25.your_game_name.truco;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.NoCartaDeManoException;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
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

    @InjectMocks
    private TrucoService trucoService;

    private Integer idBaza1;
    private Integer idBaza2;
    private Integer idBazaFalsa;
    private Integer idMano1;
    private Integer idMano2;
    private Integer idManoFalsa;
    private Integer idJugador1;
    private Integer idJugador2;
    private Integer idJugadorFalso;
    private Integer idCarta1;
    private Integer idCarta2;
    private Integer idCartaFalso;
    private Carta carta1;
    private Carta carta2;
    private Baza baza1;
    private Baza baza2;
    private Mano mano1;
    private Mano mano2;
    private Jugador jugador1;
    private Jugador jugador2;
    private Truco truco1;
    private Truco truco2;
    private Truco truco3;
    private User user;
    private List<Truco> trucos1;
    private List<Truco> trucos2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        idBaza1=1;
        idBaza2=2;
        idBazaFalsa=3;
        idMano1=1;
        idMano2=2;
        idManoFalsa=3;
        idJugador1=1;
        idJugador2=2;
        idJugadorFalso=3;
        idCarta1=1;
        idCarta2=2;
        idCartaFalso=3;

        user = new User();
        user.setId(1);
        jugador1 = new Jugador();
        jugador1.setId(idJugador1);
        jugador1.setUsuario(user);
        jugador2 = new Jugador();
        jugador2.setId(idJugador2);

        baza1 = new Baza();
        baza1.setId(idBaza1);
        baza2 = new Baza();
        baza2.setId(idBaza2);

        carta1 = new Carta();
        carta1.setId(idCarta1);
        List<Carta> cartas1 = Arrays.asList(carta1);
        carta2 = new Carta();
        carta2.setId(idCarta2);
        List<Carta> cartas2 = Arrays.asList(carta2);

        mano1 = new Mano();
        mano1.setId(idMano1);
        mano1.setJugador(jugador1);
        mano1.setCartas(cartas1);
        mano2 = new Mano();
        mano2.setId(idMano2);
        mano2.setJugador(jugador2);
        mano2.setCartas(cartas2);

        truco1 = new Truco();
        truco1.setId(1);
        truco1.setBaza(baza1);
        truco1.setJugador(idJugador1);
        truco1.setMano(mano1);
        truco1.setIdCarta(idCarta1);

        truco2 = new Truco();
        truco2.setId(2);
        truco2.setBaza(baza2);
        truco2.setJugador(idJugador1);
        truco2.setMano(mano1);
        truco2.setIdCarta(idCarta1);

        truco3 = new Truco();
        truco3.setId(3);
        truco3.setBaza(baza1);
        truco3.setJugador(idJugador2);
        truco3.setMano(mano2);
        truco3.setIdCarta(idCarta1);

        trucos1 = Arrays.asList(truco1, truco3);
        trucos2 = Arrays.asList(truco1, truco2);

    }

	@Test
    public void shouldFindTrucosByBazaId() throws DataAccessException {
        // Arrange
        when(trucoRepository.findByBazaId(idBaza1)).thenReturn(trucos1);

        // Act
        List<Truco> result = trucoService.findTrucosByBazaId(idBaza1);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(baza1, result.get(0).getBaza());
        assertEquals(3, result.get(1).getId());
        assertEquals(baza1, result.get(1).getBaza());

        verify(trucoRepository).findByBazaId(idBaza1);
    }

    @Test
    public void shouldNotFindTrucosByBazaId(){
        List<Truco> result = trucoService.findTrucosByBazaId(idBazaFalsa);
        assertTrue(result.isEmpty(), "Se esperaba que la lista devuelta sea vacía cuando no existen Trcos de dicha Baza");
    }

    @Test
    public void shouldFindTrucosByJugadorId() throws DataAccessException {
        // Arrange
        when(trucoRepository.findByJugadorId(idJugador1)).thenReturn(trucos2);

        // Act
        List<Truco> result = trucoService.findTrucosByJugadorId(idJugador1);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(jugador1.getId(), result.get(0).getJugador());
        assertEquals(2, result.get(1).getId());
        assertEquals(jugador1.getId(), result.get(1).getJugador());

        verify(trucoRepository).findByJugadorId(idJugador1);
    }

    @Test
    public void shouldNotFindTrucosByJugadorId(){
        List<Truco> result = trucoService.findTrucosByJugadorId(idJugadorFalso);
        assertTrue(result.isEmpty(), "Se esperaba que la lista devuelta sea vacía cuando no existen Trcos de dicha Jugador");
    }

    @Test
    public void shouldFindTrucosByManoId() throws DataAccessException {
        // Arrange
        when(trucoRepository.findByManoId(idMano1)).thenReturn(trucos2);

        // Act
        List<Truco> result = trucoService.findTrucosByManoId(idMano1);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(mano1, result.get(0).getMano());
        assertEquals(2, result.get(1).getId());
        assertEquals(mano1, result.get(1).getMano());

        verify(trucoRepository).findByManoId(idMano1);
    }

    @Test
    public void shouldNotFindTrucosByManoId(){
        List<Truco> result = trucoService.findTrucosByManoId(idManoFalsa);
        assertTrue(result.isEmpty(), "Se esperaba que la lista devuelta sea vacía cuando no existen Trcos de dicha Mano");
    }

    @Test
    public void shouldFindTrucoByBazaIdCartaId() throws DataAccessException {
        // Arrange
        when(trucoRepository.findTrucoByBazaIdCartaId(idBaza1, idCarta1)).thenReturn(Optional.of(truco1));

        // Act
        Truco result = this.trucoService.findTrucoByBazaIdCartaId(idBaza1, idCarta1);

        // Assert
        assertEquals(1, result.getId());
        verify(trucoRepository).findTrucoByBazaIdCartaId(idBaza1, idCarta1);
    }

    @Test
    public void shouldNotFindTrucoByBazaIdCartaId(){
        assertThrows(ResourceNotFoundException.class, () -> {
            trucoService.findTrucoByBazaIdCartaId(idBazaFalsa, idCartaFalso);
        }, "Se esperaba que se lanzara una ResourceNotFoundException cuando no se encuentra el Truco");
    }

    // Truco saveTruco(Truco truco) throws DataAccessException o NoCartaDeManoException

    @Test
    public void shouldSaveTruco() {
        when(trucoRepository.save(truco1)).thenReturn(truco1);

        Truco result = trucoService.saveTruco(truco1);

        assertEquals(truco1.getId(), result.getId());
        verify(trucoRepository).save(truco1);
    }

    @Test
    public void shouldNotSaveTruco() {
        truco1.setIdCarta(idCartaFalso);

        assertThrows( NoCartaDeManoException.class, () -> {
            trucoService.saveTruco(truco1);
        }, "Se esperaba que se lanzara una NoCartaDeManoException cuando la carta del Trco no es parte de las cartas de la mano");
    }

    // Truco updateTruco(Truco truco, int trucoId) throws DataAccessException
    @Test
    public void shouldUpdateTruco() {
        Truco newTruco = new Truco();
        newTruco.setBaza(baza2);
        newTruco.setJugador(jugador1.getId());
        newTruco.setMano(mano1);
        newTruco.setIdCarta(idCarta1);

        when(trucoRepository.findById(1)).thenReturn(Optional.of(truco1));
        when(trucoRepository.save(newTruco)).thenReturn(newTruco);

        newTruco.setBaza(baza1);
        Truco updatedTruco = trucoService.updateTruco(newTruco, 1);

        assertNotNull(updatedTruco);
        assertEquals(newTruco.getBaza(), updatedTruco.getBaza());
        verify(trucoRepository, times(1));
    }



}
