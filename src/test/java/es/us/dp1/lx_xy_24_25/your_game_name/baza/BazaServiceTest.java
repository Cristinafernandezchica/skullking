package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BazaServiceTest {

    @MockBean
    private BazaRepository bazaRepository;

    @MockBean
    private TrucoRepository trucoRepository;

    @MockBean
    private RondaService rondaService;

    @MockBean
    private TrucoService trucoService;

    @MockBean
    private JugadorService jugadorService;

    @InjectMocks
    private BazaService bazaService;

    private Baza baza;
    private Baza bazaV;
    private Jugador jugador;
    private Jugador jugador2;
    private Carta carta;
    private Carta cartaV;
    private Truco truco;
    private Truco trucoT;
    private Ronda ronda;
    private Partida partida;
    private Mano mano;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configuración de entidades relacionadas
        jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(12);
        jugador.setUsuario(null);
        jugador.setPartida(partida);

        jugador2 = new Jugador();
        jugador2.setId(2);
        jugador2.setPuntos(25);
        jugador2.setUsuario(null);
        jugador2.setPartida(partida);

        carta = new Carta();
        carta.setId(1);
        carta.setImagenFrontal("./images/cartas/morada_1.png");
        carta.setNumero(1);
        carta.setTipoCarta(TipoCarta.morada);

        cartaV = new Carta();
        cartaV.setId(29);
        cartaV.setImagenFrontal("./images/cartas/verde_1.png");
        cartaV.setNumero(1);
        cartaV.setTipoCarta(TipoCarta.verde);

        truco = new Truco();
        truco.setId(1);
        truco.setBaza(baza);
        truco.setCarta(carta);
        truco.setJugador(jugador);
        truco.setMano(mano);
        truco.setTurno(1);

        trucoT = new Truco();
        trucoT.setId(2);
        trucoT.setBaza(bazaV);
        trucoT.setCarta(cartaV);
        trucoT.setJugador(jugador);
        trucoT.setMano(mano);
        trucoT.setTurno(2);

        mano = new Mano();
        mano.setApuesta(1);
        mano.setCartas(List.of(carta));
        mano.setId(1);
        mano.setJugador(jugador);
        mano.setResultado(5);
        mano.setRonda(ronda);

        partida = new Partida();
        partida.setEstado(PartidaEstado.JUGANDO);
        partida.setFin(null);
        partida.setId(5);
        partida.setInicio(LocalDateTime.now());
        partida.setNombre("Partida Test");
        partida.setOwnerPartida(1);

        ronda = new Ronda();
        ronda.setId(1);
        ronda.setEstado(RondaEstado.JUGANDO);
        ronda.setNumBazas(3);
        ronda.setNumRonda(4);
        ronda.setPartida(partida);

        baza = new Baza();
        baza.setId(1);
        baza.setPaloBaza(PaloBaza.morada);
        baza.setNumBaza(3);
        baza.setGanador(jugador);
        baza.setTurnos(List.of());
        baza.setCartaGanadora(carta);
        baza.setRonda(ronda);

        bazaV = new Baza();
        bazaV.setId(2);
        bazaV.setPaloBaza(PaloBaza.verde);
        bazaV.setNumBaza(4);
        bazaV.setGanador(jugador);
        bazaV.setTurnos(List.of());
        bazaV.setCartaGanadora(cartaV);
        bazaV.setRonda(ronda);
    }

    // Test para guardar una Baza: MANTENER
    @Test
    void shouldSaveBaza() {
        when(bazaRepository.save(any(Baza.class))).thenReturn(baza);

        Baza savedBaza = bazaService.saveBaza(baza);

        assertNotNull(savedBaza);
        assertEquals(PaloBaza.morada, savedBaza.getPaloBaza());
        assertEquals(3, savedBaza.getNumBaza());
        assertEquals(1, savedBaza.getGanador().getId());
        verify(bazaRepository, times(1)).save(baza);
    }

    // Test para listar todas las Bazas: MANTENER
    @Test
    void shouldGetAllBazas() {
        List<Baza> bazaList = Arrays.asList(baza);
        when(bazaRepository.findAll()).thenReturn(bazaList);

        List<Baza> result = bazaService.getAllBazas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getCartaGanadora().getNumero());
        verify(bazaRepository, times(1)).findAll();
    }

    // Test para obtener una Baza por ID: MANTENER
    @Test
    void shouldFindById() {
        when(bazaRepository.findById(1)).thenReturn(Optional.of(baza));

        Baza foundBaza = bazaService.findById(1);

        assertNotNull(foundBaza);
        assertEquals(1, foundBaza.getId());
        assertEquals(PaloBaza.morada, foundBaza.getPaloBaza());
        verify(bazaRepository, times(1)).findById(1);
    }

    // Test para obtener una Baza por ID (Excepción): MANTENER
    @Test
    void shouldFindByIdNotFound() {
        when(bazaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bazaService.findById(99));
    }

    // Test para eliminar una Baza por ID: MANTENER
    @Test
    void shouldDeleteBaza() {
        bazaService.deleteBaza(1);
        verify(bazaRepository, times(1)).deleteById(1);
    }

    // Test para actualizar una Baza existente: MANTENER
    @Test
    void shouldUpdateBaza() {
        Baza newBaza = new Baza();
        newBaza.setPaloBaza(PaloBaza.amarillo);
        newBaza.setNumBaza(5);
        newBaza.setGanador(jugador);

        when(bazaRepository.findById(1)).thenReturn(Optional.of(baza));
        when(bazaRepository.save(any(Baza.class))).thenReturn(newBaza);

        Baza updatedBaza = bazaService.updateBaza(newBaza, 1);

        assertNotNull(updatedBaza);
        assertEquals(PaloBaza.amarillo, updatedBaza.getPaloBaza());
        assertEquals(5, updatedBaza.getNumBaza());
        verify(bazaRepository, times(1)).save(baza);
    }

    @Test
    void shouldFindBazaActualByRondaId() {
        List<Baza> bazas = Arrays.asList(baza);
        when(bazaRepository.findBazasByRondaId(1)).thenReturn(bazas);

        Baza bazaActual = bazaService.findBazaActualByRondaId(1);

        assertNotNull(bazaActual);
        assertEquals(1, bazaActual.getId());
        verify(bazaRepository, times(1)).findBazasByRondaId(1);
    }

    @Test
    void shouldFindByRondaIdAndNumBaza() {
        when(bazaRepository.findByRondaIdAndNumBaza(1, 3)).thenReturn(Optional.of(baza));

        Baza foundBaza = bazaService.findByRondaIdAndNumBaza(1, 3);

        assertNotNull(foundBaza);
        assertEquals(1, foundBaza.getId());
        assertEquals(3, foundBaza.getNumBaza());
    }

    @Test
    void shouldFindByRondaIdAndNumBazaNotFound() {
        when(bazaRepository.findByRondaIdAndNumBaza(99, 99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bazaService.findByRondaIdAndNumBaza(99, 99));
    }
    
    @Test
    void shouldFindByIdRondaAndIdJugador() {
        List<Baza> bazas = Arrays.asList(baza);
        when(bazaRepository.findByIdRondaAndIdJugador(1, 1)).thenReturn(bazas);

        List<Baza> resultado = bazaService.findByIdRondaAndIdJugador(1, 1);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(bazaRepository, times(1)).findByIdRondaAndIdJugador(1, 1);
    }

    @Test
    void shouldFindBazaAnterior() {
        when(bazaRepository.findBazaAnterior(1, 1)).thenReturn(Optional.of(baza));

        Baza bazaAnterior = bazaService.findBazaAnterior(1, 1);

        assertNotNull(bazaAnterior);
        assertEquals(1, bazaAnterior.getId());
        verify(bazaRepository, times(1)).findBazaAnterior(1, 1);
    }

    @Test
    void shouldIniciarBaza() {
        
        when(bazaRepository.save(any(Baza.class))).thenAnswer(invocation -> invocation.getArgument(0));
        List<Jugador> jugadores = List.of(jugador, jugador2);
        Baza nuevaBaza = bazaService.iniciarBaza(ronda, jugadores);

        assertNotNull(nuevaBaza);
        assertEquals(1, nuevaBaza.getNumBaza());
        assertNull(nuevaBaza.getCartaGanadora());
        assertNull(nuevaBaza.getGanador());
        assertEquals(ronda, nuevaBaza.getRonda());
        assertEquals(PaloBaza.sinDeterminar, nuevaBaza.getPaloBaza());
        assertEquals(jugadores.stream().map(Jugador::getId).collect(Collectors.toList()), nuevaBaza.getTurnos());

        verify(bazaRepository, times(1)).save(nuevaBaza);
    }

    @Test
    void shouldNextBaza_IncrementarNumBaza() {
        baza.setNumBaza(1);
        List<Jugador> jugadores = List.of(jugador, jugador2);

        when(bazaRepository.findById(1)).thenReturn(Optional.of(baza));
        when(bazaRepository.save(any(Baza.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Baza resultado = bazaService.nextBaza(1, jugadores);

        assertNotNull(resultado);
        assertEquals(2, resultado.getNumBaza());
        assertNull(resultado.getCartaGanadora());
        assertNull(resultado.getGanador());
        assertEquals(ronda, resultado.getRonda());
        assertEquals(PaloBaza.sinDeterminar, resultado.getPaloBaza());

        List<Integer> expectedTurnos = jugadores.stream().map(Jugador::getId).collect(Collectors.toList());
        assertEquals(expectedTurnos, resultado.getTurnos());

        verify(bazaRepository, times(1)).save(any(Baza.class));
    }

    @Test
    void shouldNextBaza_BazaNoEncontrada() {
        List<Jugador> jugadores = List.of(jugador, jugador2);
        when(bazaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bazaService.nextBaza(99, jugadores));
    }

    @Test
    void shouldCalcularTurnosNuevaBaza_PrimeraBaza() {
        List<Jugador> jugadores = Arrays.asList(jugador, jugador2);

        List<Integer> turnos = bazaService.calcularTurnosNuevaBaza(1, null, jugadores);

        assertNotNull(turnos);
        assertEquals(2, turnos.size());
        assertEquals(Arrays.asList(jugador.getId(), jugador2.getId()), turnos);
    }

    @Test
    void shouldCalcularTurnosNuevaBaza_ConGanador() {
        List<Jugador> jugadores = Arrays.asList(jugador, jugador2);
        Baza bazaAnterior = new Baza();
        bazaAnterior.setGanador(jugador2);

        List<Integer> turnos = bazaService.calcularTurnosNuevaBaza(1, bazaAnterior, jugadores);

        assertNotNull(turnos);
        assertEquals(2, turnos.size());
        assertEquals(Arrays.asList(jugador2.getId(), jugador.getId()), turnos);
    }

    @Test
    void shouldPrimerTurno() {
        List<Integer> turnos = Arrays.asList(1, 2, 3);
        Integer primerTurno = bazaService.primerTurno(turnos);
   
        assertNotNull(primerTurno);
        assertEquals(1, primerTurno);
    }

    @Test
    void shouldGetPtosBonificacion() {
        List<Baza> bazas = Arrays.asList(baza);
        List<Truco> trucos = Arrays.asList(truco);

        when(bazaRepository.findByIdRondaAndIdJugador(1, 1)).thenReturn(bazas);
        when(trucoRepository.findTrucosByBazaId(1)).thenReturn(trucos);

        Integer puntos = bazaService.getPtosBonificacion(1, 1);

        assertNotNull(puntos);
        verify(bazaRepository, times(1)).findByIdRondaAndIdJugador(1, 1);
        verify(trucoRepository, times(1)).findTrucosByBazaId(1);
    }
    @Test
    void shouldCalculoPtosBonificacion() {
        Carta cartaGanadora = mock(Carta.class);
        Carta carta = mock(Carta.class);

        when(cartaGanadora.getTipoCarta()).thenReturn(TipoCarta.skullking);
        when(carta.getTipoCarta()).thenReturn(TipoCarta.pirata);

        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);

        assertNotNull(puntos);
        assertEquals(30, puntos);
    }

    @Test
    void shouldCalculoPtosBonificacion_SkullkingVsSirena() {
        Carta cartaGanadora = new Carta();
        cartaGanadora.setTipoCarta(TipoCarta.skullking);
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.sirena);
    
        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);
    
        assertNotNull(puntos);
        assertEquals(0, puntos);
    }
    
    @Test
    void shouldCalculoPtosBonificacion_PirataVsSirena() {
        Carta cartaGanadora = new Carta();
        cartaGanadora.setTipoCarta(TipoCarta.pirata);
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.sirena);
    
        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);
    
        assertNotNull(puntos);
        assertEquals(20, puntos);
    }
    
    @Test
    void shouldCalculoPtosBonificacion_SkullkingVsPirata() {
        Carta cartaGanadora = new Carta();
        cartaGanadora.setTipoCarta(TipoCarta.skullking);
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.pirata);
    
        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);
    
        assertNotNull(puntos);
        assertEquals(30, puntos);
    }
    
    @Test
    void shouldCalculoPtosBonificacion_SirenaVsSkullking() {
        Carta cartaGanadora = new Carta();
        cartaGanadora.setTipoCarta(TipoCarta.sirena);
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.skullking);
    
        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);
    
        assertNotNull(puntos);
        assertEquals(40, puntos);
    }

    @Test
    void shouldCalculoPtosBonificacion_NoSkullking() {
        Carta cartaGanadora = new Carta();
        cartaGanadora.setTipoCarta(TipoCarta.sirena);
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.pirata);

        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);

        assertNotNull(puntos);
        assertEquals(0, puntos);
    }

    @Test
    void shouldCalculoPtosBonificacion_Catorce() {
        Carta cartaGanadora = mock(Carta.class);
        Carta carta = mock(Carta.class);

        when(cartaGanadora.getTipoCarta()).thenReturn(TipoCarta.pirata);
        when(carta.getTipoCarta()).thenReturn(TipoCarta.pirata);
        when(carta.esCatorce()).thenReturn(true);
        when(carta.esTriunfo()).thenReturn(true);

        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);

        assertNotNull(puntos);
        assertEquals(30, puntos);
    }


    @Test
    void shouldCalculoPtosBonificacion_CatorceSinTriunfo() {
        Carta cartaGanadora = mock(Carta.class);
        Carta carta = mock(Carta.class);

        when(cartaGanadora.getTipoCarta()).thenReturn(TipoCarta.pirata);
        when(carta.getTipoCarta()).thenReturn(TipoCarta.pirata);
        when(carta.esCatorce()).thenReturn(true);
        when(carta.esTriunfo()).thenReturn(false);

        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);

        assertNotNull(puntos);
        assertEquals(10, puntos);
    }


    @Test
    void shouldCalculoPtosBonificacion_SinBonificacion() {
        Carta cartaGanadora = mock(Carta.class);
        Carta carta = mock(Carta.class);

        when(cartaGanadora.getTipoCarta()).thenReturn(TipoCarta.pirata);
        when(carta.getTipoCarta()).thenReturn(TipoCarta.pirata);
        when(carta.esCatorce()).thenReturn(false);
        when(carta.esTriunfo()).thenReturn(false);

        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);

        assertNotNull(puntos);
        assertEquals(0, puntos);
    }
}
