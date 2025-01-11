package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.CartaService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.ApuestaNoValidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManoServiceTest {

    @Mock
    private ManoRepository manoRepository;

    @Mock
    private CartaService cartaService;

    @Mock
    private JugadorService jugadorService;

    @InjectMocks
    private ManoService manoService;

    private static final Integer ID_TIGRESA_BANDERA_BLANCA = 100;
    private static final Integer ID_TIGRESA_PIRATA = 101;

    private Jugador jugador;
    private Ronda ronda;
    private Partida partida;
    private List<Carta> cartas;
    private Carta carta;
    private Mano mano1;
    private Mano mano2;

    private Mano mano;
    private List<Carta> listaCartas;
    private List<Jugador> jugadores;
    private Jugador jugador3;

    private Mano mano3;
    private List<Carta> cartas2;
    private Carta carta2;
    private Carta carta3;

    private Jugador jugador1;
    private Jugador jugador2;

    @BeforeEach
    public void setUp() {
        jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(12);
        jugador.setUsuario(null);
        jugador.setPartida(partida);

        ronda = new Ronda();
        ronda.setId(1);
        ronda.setEstado(RondaEstado.JUGANDO);
        ronda.setNumBazas(4);
        ronda.setNumRonda(4);
        ronda.setPartida(partida);

        partida = new Partida();
        partida.setEstado(PartidaEstado.JUGANDO);
        partida.setFin(LocalDateTime.now());
        partida.setId(5);
        partida.setInicio(LocalDateTime.now());
        partida.setNombre("Partida Test");
        partida.setOwnerPartida(1);

        carta = new Carta();
        carta.setId(1);
        carta.setImagenFrontal("./images/cartas/morada_1.png");
        carta.setNumero(1);
        carta.setTipoCarta(TipoCarta.morada);
        cartas = List.of(carta);

        mano1 = new Mano();
        mano1.setId(1);
        mano1.setApuesta(3);

        mano2 = new Mano();
        mano2.setId(2);
        mano2.setApuesta(5);


        listaCartas = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Carta carta = new Carta();
            carta.setId(i);
            carta.setTipoCarta(TipoCarta.morada);
            carta.setNumero(i);
            listaCartas.add(carta);
        }

        Carta tigresaBanderaBlanca = new Carta();
        tigresaBanderaBlanca.setId(ID_TIGRESA_BANDERA_BLANCA);

        Carta tigresaPirata = new Carta();
        tigresaPirata.setId(ID_TIGRESA_PIRATA);

        listaCartas.add(tigresaBanderaBlanca);
        listaCartas.add(tigresaPirata);

        Jugador jugador1 = new Jugador();
        jugador1.setId(1);

        Jugador jugador2 = new Jugador();
        jugador2.setId(2);

        jugadores = Arrays.asList(jugador1, jugador2);

        jugador3 = new Jugador(); 
        jugador3.setId(1);

        mano = new Mano();
        mano.setId(1);
        mano.setJugador(jugador);
        mano.setCartas(List.of(new Carta(), new Carta(), new Carta())); // Mano con 3 cartas

        mano3 = new Mano();
        mano3.setId(1);

        cartas2 = new ArrayList<>();
        carta2 = new Carta();
        carta2.setId(2);
        carta2.setTipoCarta(TipoCarta.amarillo);
        cartas2.add(carta2);

        carta3 = new Carta();
        carta3.setId(3);
        carta3.setTipoCarta(TipoCarta.verde);
        cartas2.add(carta3);

        mano3.setCartas(cartas2);

    }

    @Test
    public void shouldSaveMano() {
        Mano mano = new Mano();
        when(manoRepository.save(mano)).thenReturn(mano);

        Mano savedMano = manoService.saveMano(mano);
        assertNotNull(savedMano);
        verify(manoRepository, times(1)).save(mano);
    }

    @Test
    public void shouldFindAll() {
        List<Mano> manos = List.of(new Mano(), new Mano());
        when(manoRepository.findAll()).thenReturn(manos);

        Iterable<Mano> result = manoService.findAll();
        assertNotNull(result);
        assertEquals(2, ((List<Mano>) result).size());
        verify(manoRepository, times(1)).findAll();
    }

    @Test
    public void shouldFindManoById() {
        Mano mano = new Mano();
        mano.setId(1);
        when(manoRepository.findById(1)).thenReturn(Optional.of(mano));

        Mano foundMano = manoService.findManoById(1);
        assertNotNull(foundMano);
        assertEquals(1, foundMano.getId());
        verify(manoRepository, times(1)).findById(1);
    }

    @Test
    public void shouldFindManoByIdNotFound() {
        when(manoRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> manoService.findManoById(1));
    }

    @Test
    public void shouldDeleteMano() {
        manoService.deleteMano(1);
        verify(manoRepository, times(1)).deleteById(1);
    }

    @Test
    public void shouldUpdateMano() {
        Mano existingMano = new Mano();
        existingMano.setId(1);
        existingMano.setApuesta(3);

        Mano updatedMano = new Mano();
        updatedMano.setApuesta(5);

        when(manoRepository.findById(1)).thenReturn(Optional.of(existingMano));
        when(manoRepository.save(existingMano)).thenReturn(existingMano);

        Mano result = manoService.updateMano(updatedMano, 1);
        assertNotNull(result);
        assertEquals(5, result.getApuesta());
        verify(manoRepository, times(1)).save(existingMano);
    }

    @Test
    public void shouldFindLastManoByJugadorId() {
        List<Mano> manos = new ArrayList<>();
        Mano mano1 = new Mano();
        mano1.setId(1);
        manos.add(mano1);
        Mano mano2 = new Mano();
        mano2.setId(2);
        manos.add(mano2);

        when(manoRepository.findAllManoByJugadorId(1)).thenReturn(manos);

        Mano lastMano = manoService.findLastManoByJugadorId(1);
        assertNotNull(lastMano);
        assertEquals(2, lastMano.getId());
    }

    // shoulds findAllByRondaId todas las posibilidades
    @Test
    public void shouldFindAllManosByRondaId() {
        when(manoRepository.findAllByRondaId(1)).thenReturn(Arrays.asList(mano1, mano2));

        List<Mano> result = manoService.findAllManosByRondaId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        verify(manoRepository, times(1)).findAllByRondaId(1);
    }

    @Test
    public void shouldFindAllByRondaIdEmpty() {
        when(manoRepository.findAllByRondaId(1)).thenReturn(Collections.emptyList());

        List<Mano> result = manoService.findAllManosByRondaId(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(manoRepository, times(1)).findAllByRondaId(1);
    }

    @Test
    public void shouldFindAllByRondaIdThrowsDataAccessException() {
        when(manoRepository.findAllByRondaId(1)).thenThrow(new DataAccessException("..."){});

        assertThrows(DataAccessException.class, () -> manoService.findAllManosByRondaId(1));
        verify(manoRepository, times(1)).findAllByRondaId(1);
    }

    // tests para iniciarManos
    @Test
    void shouldIniciarManos() {
        when(cartaService.findAll()).thenReturn(listaCartas);

        List<Jugador> jugadores = Arrays.asList(jugador1, jugador2);

        manoService.iniciarManos(1, ronda, jugadores);

        verify(manoRepository, times(2)).save(any(Mano.class));
    }

    @Test
    void shouldFilterOutComodines() {
        List<Carta> cartasFiltradas = new ArrayList<>(listaCartas);
        cartasFiltradas.removeIf(c -> c.getId().equals(ID_TIGRESA_BANDERA_BLANCA) || c.getId().equals(ID_TIGRESA_PIRATA));
        when(cartaService.findAll()).thenReturn(cartasFiltradas);

        List<Jugador> jugadores = Arrays.asList(jugador1, jugador2);

        manoService.iniciarManos(1, ronda, jugadores);

        verify(manoRepository, times(jugadores.size())).save(argThat(mano -> 
            mano.getCartas().stream().noneMatch(c -> c.getId().equals(ID_TIGRESA_BANDERA_BLANCA) || c.getId().equals(ID_TIGRESA_PIRATA))
        ));
    }



    @Test
    void shouldAssignCartasToJugadores() {
        List<Carta> cartasFiltradas = new ArrayList<>(listaCartas);
        cartasFiltradas.removeIf(c -> c.getId().equals(ID_TIGRESA_BANDERA_BLANCA) || c.getId().equals(ID_TIGRESA_PIRATA));
        when(cartaService.findAll()).thenReturn(cartasFiltradas);

        List<Jugador> jugadores = Arrays.asList(jugador1, jugador2);

        manoService.iniciarManos(1, ronda, jugadores);

        verify(manoRepository, times(2)).save(argThat(mano -> mano.getCartas().size() == 4)); // Assuming 4 cards per round
    }

    @Test
    void shouldClearCartasBaraja() {
        List<Carta> cartasSinComodines = new ArrayList<>(listaCartas);
        cartasSinComodines.removeIf(c -> c.getId().equals(ID_TIGRESA_BANDERA_BLANCA) || c.getId().equals(ID_TIGRESA_PIRATA));
        when(cartaService.findAll()).thenReturn(cartasSinComodines);

        List<Jugador> jugadores = Arrays.asList(jugador1, jugador2);

        manoService.iniciarManos(1, ronda, jugadores);

        verify(manoRepository, times(jugadores.size())).save(argThat(mano ->
            mano.getCartas().stream().noneMatch(c -> c.getId().equals(ID_TIGRESA_BANDERA_BLANCA) || c.getId().equals(ID_TIGRESA_PIRATA))
        ));
    }

    @Test
    void shouldFilterComodinesFromList() {
        List<Carta> cartasConComodines = new ArrayList<>(listaCartas);
        cartasConComodines.add(new Carta() {{
            setId(ID_TIGRESA_BANDERA_BLANCA);
        }});
        cartasConComodines.add(new Carta() {{
            setId(ID_TIGRESA_PIRATA);
        }});

        List<Carta> cartasFiltradas = cartasConComodines.stream()
            .filter(c -> !(c.getId().equals(ID_TIGRESA_BANDERA_BLANCA) || c.getId().equals(ID_TIGRESA_PIRATA)))
            .collect(Collectors.toList());

        for (Carta carta : cartasFiltradas) {
            assertFalse(carta.getId().equals(ID_TIGRESA_BANDERA_BLANCA) || carta.getId().equals(ID_TIGRESA_PIRATA));
        }
    }

    @Test
    void shouldHandleEmptyJugadorList() {
        when(cartaService.findAll()).thenReturn(listaCartas);

        List<Jugador> jugadores = Collections.emptyList();

        manoService.iniciarManos(1, ronda, jugadores);

        verify(manoRepository, never()).save(any(Mano.class));
    }

    // tests numCartasARepartir
    @Test
    public void shouldGetNumCartasARepartirCasoBasico() {
        Integer numRonda = 5;
        Integer numJugadores = 7;
        Integer expected = 5; // numCartasTotales / numJugadores >= numRonda
        Integer result = manoService.getNumCartasARepartir(numRonda, numJugadores);
        assertEquals(expected, result);
    }

    @Test
    public void shouldGetNumCartasARepartirRondasAltas() {
        Integer numRonda = 20;
        Integer numJugadores = 5;
        Integer expected = 14; // numCartasTotales / numJugadores < numRonda
        Integer result = manoService.getNumCartasARepartir(numRonda, numJugadores);
        assertEquals(expected, result);
    }

    @Test
    public void shouldGetNumCartasARepartirMuchosJugadores() {
        Integer numRonda = 3;
        Integer numJugadores = 10;
        Integer expected = 3; // numCartasTotales / numJugadores >= numRonda
        Integer result = manoService.getNumCartasARepartir(numRonda, numJugadores);
        assertEquals(expected, result);
    }

    @Test
    public void shouldGetNumCartasARepartirPocosJugadores() {
        Integer numRonda = 3;
        Integer numJugadores = 2;
        Integer expected = 3; // numCartasTotales / numJugadores >= numRonda
        Integer result = manoService.getNumCartasARepartir(numRonda, numJugadores);
        assertEquals(expected, result);
    }

    @Test
    public void shouldGetNumCartasARepartirCeroJugadores() {
        Integer numRonda = 3;
        Integer numJugadores = 0;
        assertThrows(ArithmeticException.class, () -> manoService.getNumCartasARepartir(numRonda, numJugadores));
    }

    @Test
    public void shouldGetNumCartasARepartirCeroRonda() {
        Integer numRonda = 0;
        Integer numJugadores = 5;
        Integer expected = 0; // numRonda es 0
        Integer result = manoService.getNumCartasARepartir(numRonda, numJugadores);
        assertEquals(expected, result);
    }

    @Test
    public void shouldGetNumCartasARepartirNumRondaMayor() {
        Integer numRonda = 40;
        Integer numJugadores = 2;
        Integer expected = 35; // numCartasTotales / numJugadores < numRonda
        Integer result = manoService.getNumCartasARepartir(numRonda, numJugadores);
        assertEquals(expected, result);
    }

    // tests cartasDisabled
    @Test
    public void shouldReturnDisabledCartasCuandoHasPaloBaza() {
        mano3.setCartas(cartas2); // Mano con cartas de distintos palos
        when(manoRepository.findById(mano3.getId())).thenReturn(Optional.of(mano3));

        List<Carta> disabledCartas = manoService.cartasDisabled(mano3.getId(), TipoCarta.amarillo);

        assertEquals(1, disabledCartas.size());
        assertTrue(disabledCartas.contains(carta3)); // Carta que no es del palo baza
    }

    @Test
    public void shouldReturnCartasEnabledCuandoSinDeterminar() {
        mano3.setCartas(cartas2); 
        when(manoRepository.findById(mano3.getId())).thenReturn(Optional.of(mano3));

        List<Carta> disabledCartas = manoService.cartasDisabled(mano3.getId(), TipoCarta.sinDeterminar);

        assertTrue(disabledCartas.isEmpty()); // Todas las cartas están habilitadas
    }

    @Test
    public void shouldReturnListaVaciaCuandoNoManoCoincidente() {
        when(manoRepository.findById(anyInt())).thenReturn(Optional.empty());

        List<Carta> disabledCartas = manoService.cartasDisabled(999, TipoCarta.amarillo);

        assertNotNull(disabledCartas);
        assertTrue(disabledCartas.isEmpty());
    }

    @Test
    public void shouldDisableCartasWhenHasEspecialAndHasPaloBaza() {
        // Configuramos la mano con cartas especiales y del palo baza
        Carta cartaEspecial = new Carta();
        cartaEspecial.setId(4);
        cartaEspecial.setTipoCarta(TipoCarta.pirata); // Es especial

        Carta cartaPaloBaza = new Carta();
        cartaPaloBaza.setId(5);
        cartaPaloBaza.setTipoCarta(TipoCarta.amarillo); // Es del palo baza

        Carta cartaNoValida = new Carta();
        cartaNoValida.setId(6);
        cartaNoValida.setTipoCarta(TipoCarta.verde); // No es ni especial ni del palo baza

        mano3.setCartas(Arrays.asList(cartaEspecial, cartaPaloBaza, cartaNoValida));

        when(manoRepository.findById(mano3.getId())).thenReturn(Optional.of(mano3));

        List<Carta> disabledCartas = manoService.cartasDisabled(mano3.getId(), TipoCarta.amarillo);

        assertEquals(1, disabledCartas.size()); // Solo la cartaNoValida debe estar deshabilitada
        assertTrue(disabledCartas.contains(cartaNoValida));
    }

    @Test
    public void shouldDisableCartasWhenHasPaloBazaAndNoEspecial() {
        // Configuramos la mano con solo cartas del palo baza y otras no válidas
        Carta cartaPaloBaza = new Carta();
        cartaPaloBaza.setId(7);
        cartaPaloBaza.setTipoCarta(TipoCarta.amarillo); // Es del palo baza

        Carta cartaNoValida = new Carta();
        cartaNoValida.setId(8);
        cartaNoValida.setTipoCarta(TipoCarta.verde); // No es del palo baza

        mano3.setCartas(Arrays.asList(cartaPaloBaza, cartaNoValida));

        when(manoRepository.findById(mano3.getId())).thenReturn(Optional.of(mano3));

        List<Carta> disabledCartas = manoService.cartasDisabled(mano3.getId(), TipoCarta.amarillo);

        assertEquals(1, disabledCartas.size()); // Solo la cartaNoValida debe estar deshabilitada
        assertTrue(disabledCartas.contains(cartaNoValida));
    }

    @Test
    public void shouldReturnCartasEnabledCuandoHasEspecialSinPaloBaza() {
        // Configuramos la mano con una carta especial y ninguna del palo baza
        Carta cartaEspecial = new Carta();
        cartaEspecial.setId(4);
        cartaEspecial.setTipoCarta(TipoCarta.pirata); // Es especial

        Carta cartaNoValida = new Carta();
        cartaNoValida.setId(6);
        cartaNoValida.setTipoCarta(TipoCarta.verde); // No es ni especial ni del palo baza

        mano3.setCartas(Arrays.asList(cartaEspecial, cartaNoValida));

        when(manoRepository.findById(mano3.getId())).thenReturn(Optional.of(mano3));

        List<Carta> disabledCartas = manoService.cartasDisabled(mano3.getId(), TipoCarta.amarillo);

        assertTrue(disabledCartas.isEmpty()); // Todas las cartas están habilitadas porque no hay palo baza
    }

    @Test
    public void shouldReturnCartasEnabledCuandoNoEspecialNiPaloBaza() {
        // Configuramos la mano sin cartas especiales ni del palo baza
        Carta cartaNoValida1 = new Carta();
        cartaNoValida1.setId(6);
        cartaNoValida1.setTipoCarta(TipoCarta.verde); // No es ni especial ni del palo baza

        Carta cartaNoValida2 = new Carta();
        cartaNoValida2.setId(7);
        cartaNoValida2.setTipoCarta(TipoCarta.morada); // No es ni especial ni del palo baza

        mano3.setCartas(Arrays.asList(cartaNoValida1, cartaNoValida2));

        when(manoRepository.findById(mano3.getId())).thenReturn(Optional.of(mano3));

        List<Carta> disabledCartas = manoService.cartasDisabled(mano3.getId(), TipoCarta.amarillo);

        assertTrue(disabledCartas.isEmpty()); // Todas las cartas están habilitadas porque no hay especiales ni palo baza
    }

    @Test
    public void shouldActualizarResultadoMano() {
        // Configuramos el mock de mano con un resultado inicial
        mano.setResultado(0); // Establecemos un valor inicial para resultado
        mano.setId(1); // Aseguramos que el ID coincide con el usado en el método

        when(manoRepository.findAllManoByJugadorId(jugador.getId())).thenReturn(Collections.singletonList(mano));
        when(manoRepository.findById(1)).thenReturn(Optional.of(mano)); // Configuramos el mock para findById
        when(manoRepository.save(any(Mano.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Baza baza = new Baza();
        baza.setGanador(jugador);

        manoService.actualizarResultadoMano(baza);

        ArgumentCaptor<Mano> captor = ArgumentCaptor.forClass(Mano.class);
        verify(manoRepository).save(captor.capture());
        Mano updatedMano = captor.getValue();
        assertEquals(1, updatedMano.getResultado());
    }



}
