package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
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
    void shouldThrowExceptionIfBazaNotFoundByRondaAndNum() {
        when(bazaRepository.findByRondaIdAndNumBaza(1, 99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bazaService.findByRondaIdAndNumBaza(1, 99));
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
    void shouldIniciarBaza() {
        Partida partida = new Partida();
        partida.setId(5);

        Ronda ronda = new Ronda();
        ronda.setPartida(partida);

        Jugador jugador = new Jugador();
        jugador.setId(1);
        List<Jugador> jugadores = Arrays.asList(jugador);

        when(bazaRepository.save(any(Baza.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Baza nuevaBaza = bazaService.iniciarBaza(ronda, jugadores);

        assertNotNull(nuevaBaza);
        assertEquals(1, nuevaBaza.getNumBaza());
        assertNull(nuevaBaza.getCartaGanadora());
        assertNull(nuevaBaza.getGanador());
        assertEquals(PaloBaza.sinDeterminar, nuevaBaza.getPaloBaza());
        assertEquals(1, nuevaBaza.getTurnos().size());
        assertEquals(1, nuevaBaza.getTurnos().get(0)); // Verificar que el turno sea el esperado
    }

    @Test
    void shouldNextBaza() {
        when(bazaRepository.findById(1)).thenReturn(Optional.of(baza));
        when(bazaRepository.save(any(Baza.class))).thenReturn(bazaV);

        Baza nuevaBaza = bazaService.nextBaza(1, Arrays.asList(jugador));

        assertNotNull(nuevaBaza);
        assertEquals(4, nuevaBaza.getNumBaza());
        assertNull(nuevaBaza.getCartaGanadora());
        assertEquals(PaloBaza.sinDeterminar, nuevaBaza.getPaloBaza());
        verify(bazaRepository, times(1)).save(any(Baza.class));
    }

    @Test
    void shouldCalcularTurnosNuevaBazaPrimera() {
        // Crear una lista con más de un jugador
        Jugador jugador1 = new Jugador();
        jugador1.setId(1);
        Jugador jugador2 = new Jugador();
        jugador2.setId(2);
        List<Jugador> jugadores = Arrays.asList(jugador1, jugador2);

        List<Integer> turnos = bazaService.calcularTurnosNuevaBaza(5, null, jugadores);

        assertNotNull(turnos);
        assertEquals(2, turnos.size());
        assertEquals(jugador1.getId(), turnos.get(0));
        assertEquals(jugador2.getId(), turnos.get(1));
    }

    @Test
    void shouldCalcularTurnosNuevaBazaNoPrimera() {
        // Crear una lista con más de un jugador
        Jugador jugador1 = new Jugador();
        jugador1.setId(1);
        Jugador jugador2 = new Jugador();
        jugador2.setId(2);
        List<Jugador> jugadores = Arrays.asList(jugador1, jugador2);

        // Configurar la baza anterior con el segundo jugador como ganador
        Baza bazaAnterior = new Baza();
        bazaAnterior.setGanador(jugador2);

        List<Integer> turnos = bazaService.calcularTurnosNuevaBaza(5, bazaAnterior, jugadores);

        assertNotNull(turnos);
        assertEquals(2, turnos.size());
        assertEquals(jugador2.getId(), turnos.get(0)); // El ganador debería ser el primero en la nueva lista de turnos
        assertEquals(jugador1.getId(), turnos.get(1)); // El jugador1 debería ser el siguiente
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
        assertEquals(30, puntos); // Skullking contra Pirata
    }

    @Test
    void shouldCalculoPtosBonificacion_SkullkingVsSirena() {
        Carta cartaGanadora = new Carta();
        cartaGanadora.setTipoCarta(TipoCarta.skullking);
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.sirena);
    
        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);
    
        assertNotNull(puntos);
        assertEquals(0, puntos); // No hay bonificación para Skullking contra Sirena
    }
    
    @Test
    void shouldCalculoPtosBonificacion_PirataVsSirena() {
        Carta cartaGanadora = new Carta();
        cartaGanadora.setTipoCarta(TipoCarta.pirata);
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.sirena);
    
        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);
    
        assertNotNull(puntos);
        assertEquals(20, puntos); // Pirata contra Sirena
    }
    
    @Test
    void shouldCalculoPtosBonificacion_SkullkingVsPirata() {
        Carta cartaGanadora = new Carta();
        cartaGanadora.setTipoCarta(TipoCarta.skullking);
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.pirata);
    
        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);
    
        assertNotNull(puntos);
        assertEquals(30, puntos); // Skullking contra Pirata
    }
    
    @Test
    void shouldCalculoPtosBonificacion_SirenaVsSkullking() {
        Carta cartaGanadora = new Carta();
        cartaGanadora.setTipoCarta(TipoCarta.sirena);
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.skullking);
    
        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);
    
        assertNotNull(puntos);
        assertEquals(40, puntos); // Sirena contra Skullking
    }

    @Test
    void shouldCalculoPtosBonificacion_NoSkullking() {
        Carta cartaGanadora = new Carta();
        cartaGanadora.setTipoCarta(TipoCarta.sirena); // Tipo de carta ganadora que no es skullking
        Carta carta = new Carta();
        carta.setTipoCarta(TipoCarta.pirata); // Tipo de carta que no es skullking

        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);

        assertNotNull(puntos);
        assertEquals(0, puntos); // No hay bonificación para Sirena contra Pirata
    }

    @Test
    void shouldCalculoPtosBonificacion_Catorce() {
        Carta cartaGanadora = mock(Carta.class);
        Carta carta = mock(Carta.class); // Usar un mock en lugar de una instancia real

        when(cartaGanadora.getTipoCarta()).thenReturn(TipoCarta.pirata);
        when(carta.getTipoCarta()).thenReturn(TipoCarta.pirata);
        // Configurar carta como catorce y triunfo
        when(carta.esCatorce()).thenReturn(true);
        when(carta.esTriunfo()).thenReturn(true);

        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);

        assertNotNull(puntos);
        assertEquals(30, puntos); // 10 por catorce y 20 por triunfo
    }


    @Test
    void shouldCalculoPtosBonificacion_CatorceSinTriunfo() {
        Carta cartaGanadora = mock(Carta.class);
        Carta carta = mock(Carta.class); // Usar un mock en lugar de una instancia real

        when(cartaGanadora.getTipoCarta()).thenReturn(TipoCarta.pirata);
        when(carta.getTipoCarta()).thenReturn(TipoCarta.pirata);
        // Configurar carta como catorce pero no triunfo
        when(carta.esCatorce()).thenReturn(true);
        when(carta.esTriunfo()).thenReturn(false);

        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);

        assertNotNull(puntos);
        assertEquals(10, puntos); // Solo 10 por catorce
    }


    @Test
    void shouldCalculoPtosBonificacion_SinBonificacion() {
        Carta cartaGanadora = mock(Carta.class);
        Carta carta = mock(Carta.class); // Usar un mock en lugar de una instancia real

        when(cartaGanadora.getTipoCarta()).thenReturn(TipoCarta.pirata);
        when(carta.getTipoCarta()).thenReturn(TipoCarta.pirata);
        // Configurar carta como no catorce y no triunfo
        when(carta.esCatorce()).thenReturn(false);
        when(carta.esTriunfo()).thenReturn(false);

        Integer puntos = bazaService.calculoPtosBonificacion(cartaGanadora, carta);

        assertNotNull(puntos);
        assertEquals(0, puntos); // Sin bonificaciones
    }


}