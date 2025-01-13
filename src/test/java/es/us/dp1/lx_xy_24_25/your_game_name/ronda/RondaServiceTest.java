package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.PaloBaza;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RondaServiceTest {

    @InjectMocks
    private RondaService rondaService;

    @Mock
    private RondaRepository rondaRepository;

    @Mock
    private PartidaService partidaService;

    @Mock
    private ManoService manoService;

    @Mock
    private BazaService bazaService;

    @Mock
    private JugadorService jugadorService;


    private Baza baza;
    private Baza bazaV;
    private Jugador jugador;
    private Carta carta;
    private Carta cartaV;
    private Ronda ronda;
    private Ronda rondaV;
    private Partida partida;
    private Truco truco;
    private Truco trucoT;
    private Mano mano;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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

        ronda = new Ronda();
        ronda.setId(2);
        ronda.setEstado(RondaEstado.JUGANDO);
        ronda.setNumBazas(4);
        ronda.setNumRonda(4);
        ronda.setPartida(partida);

        rondaV = new Ronda();
        rondaV.setId(1);
        rondaV.setEstado(RondaEstado.FINALIZADA);
        rondaV.setNumBazas(3);
        rondaV.setNumRonda(3);
        rondaV.setPartida(partida);

        partida = new Partida();
        partida.setEstado(PartidaEstado.JUGANDO);
        partida.setFin(null);
        partida.setId(5);
        partida.setInicio(LocalDateTime.now());
        partida.setNombre("Partida Test");
        partida.setOwnerPartida(1); 

        truco = new Truco();
        truco.setId(1);
        truco.setBaza(baza);
        truco.setCarta(carta);
        truco.setJugador(jugador);
        truco.setMano(mano);
        truco.setMano(mano);
        truco.setTurno(1);

        trucoT = new Truco();
        trucoT.setId(2);
        trucoT.setBaza(bazaV);
        trucoT.setCarta(cartaV);
        trucoT.setJugador(jugador);
        trucoT.setMano(mano);
        trucoT.setMano(mano);
        trucoT.setTurno(2);

        baza = new Baza();
        baza.setId(1);
        baza.setPaloBaza(PaloBaza.morada);
        baza.setNumBaza(3);
        baza.setGanador(jugador);
        baza.setCartaGanadora(carta);
        baza.setRonda(ronda);

        bazaV = new Baza();
        bazaV.setId(2);
        bazaV.setPaloBaza(PaloBaza.verde);
        bazaV.setNumBaza(4);
        bazaV.setGanador(jugador);
        bazaV.setCartaGanadora(cartaV);
        bazaV.setRonda(ronda);

        mano =new Mano();
        mano.setApuesta(1);
        mano.setCartas(List.of(carta));
        mano.setId(1);
        mano.setJugador(jugador);
        mano.setResultado(5);
        mano.setRonda(ronda);
    }

    @Test
    void shouldGetAllRondas(){
        List<Ronda> rondaList = Arrays.asList(ronda, ronda, ronda);
        when(rondaRepository.findAll()).thenReturn(rondaList);

        List<Ronda> result = rondaService.getAllRondas();

        assertNotNull(result);
        assertEquals(3,result.size());
        assertEquals(ronda, result.get(0));
    }

    @Test
    void shouldSaveRonda() {
        when(rondaRepository.save(any(Ronda.class))).thenReturn(ronda);

        Ronda savedRonda = rondaService.save(ronda);

        assertNotNull(savedRonda);
        assertEquals(RondaEstado.JUGANDO, savedRonda.getEstado());
        assertEquals(4, savedRonda.getNumBazas());
        assertEquals(4, savedRonda.getNumRonda());
        verify(rondaRepository, times(1)).save(ronda);
    }

    @Test
    void shouldFindById(){
        when(rondaRepository.findById(2)).thenReturn(Optional.of(ronda));

        Ronda foundRonda = rondaService.getRondaById(2);
       
        assertNotNull(foundRonda);
        assertEquals(RondaEstado.JUGANDO, foundRonda.getEstado());
        assertEquals(4, foundRonda.getNumBazas());
        assertEquals(4, foundRonda.getNumRonda());
        verify(rondaRepository, times(1)).findById(2);
    }

    @Test
    void shouldFindByIdNotFound() {
        when(rondaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rondaService.getRondaById(1));
    }

    @Test
    void shouldDeleteRonda() {
        rondaService.delete(1);
        verify(rondaRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldUpdateRonda() {
        Ronda newRonda = new Ronda();
        newRonda.setId(2);
        newRonda.setEstado(RondaEstado.FINALIZADA); 
        newRonda.setNumBazas(4);
        newRonda.setNumRonda(4);
        newRonda.setPartida(partida);

        when(rondaRepository.findById(1)).thenReturn(Optional.of(ronda));
        when(rondaRepository.save(any(Ronda.class))).thenReturn(newRonda);

        Ronda updatedRonda = rondaService.updateRonda(newRonda, 1);

        assertNotNull(updatedRonda);
        assertEquals(RondaEstado.FINALIZADA, updatedRonda.getEstado());
        verify(rondaRepository, times(1)).save(ronda);
    }

    
    @Test
    void shouldIniciarRonda() {
        when(rondaRepository.save(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ronda result = rondaService.iniciarRonda(partida);

        assertNotNull(result); 
        assertEquals(Integer.valueOf(1), result.getNumRonda()); 
        assertEquals(Integer.valueOf(1), result.getNumBazas()); 
        assertEquals(partida, result.getPartida()); 
        assertEquals(RondaEstado.JUGANDO, result.getEstado()); 

        verify(rondaRepository, times(1)).save(any(Ronda.class)); 
    }

    @Test
    void shouldCreateNextRondaSuccessfully() {
        when(rondaRepository.findById(2)).thenReturn(Optional.of(ronda));
        when(rondaRepository.save(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ronda result = rondaService.nextRonda(2, 5);

        assertNotNull(result);
        assertEquals(Integer.valueOf(5), result.getNumBazas());
        assertEquals(Integer.valueOf(5), result.getNumRonda());
        assertEquals(RondaEstado.JUGANDO, result.getEstado());
        assertEquals(ronda.getPartida(), result.getPartida());

        verify(rondaRepository, times(1)).findById(2);
        verify(rondaRepository, times(1)).save(any(Ronda.class));
    }

    @Test
    void shouldThrowExceptionWhenNextRondaRondaIdNotFound() {
        when(rondaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rondaService.nextRonda(99, 5));

        verify(rondaRepository, times(1)).findById(99);
        verify(rondaRepository, never()).save(any(Ronda.class));
    }


    @Test
    void shouldFinalizeRondaSuccessfully() {
        when(rondaRepository.findById(2)).thenReturn(Optional.of(ronda));
        when(rondaRepository.save(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        rondaService.finalizarRonda(2);

        assertEquals(RondaEstado.FINALIZADA, ronda.getEstado());
        verify(rondaRepository, times(1)).findById(2);
        verify(rondaRepository, times(1)).save(ronda);
    }

    @Test
    void shouldThrowExceptionWhenFinalizeRondaRondaIdNotFound() {
        when(rondaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rondaService.finalizarRonda(99));

        verify(rondaRepository, times(1)).findById(99);
        verify(rondaRepository, never()).save(any(Ronda.class));
    }


    @Test
    void shouldReturnRondaActualSuccessfully() {
        List<Ronda> rondas = Arrays.asList(ronda, rondaV);
        when(rondaRepository.findByPartidaId(5)).thenReturn(rondas);

        Ronda result = rondaService.rondaActual(5);

        assertNotNull(result);
        assertEquals(ronda, result);
        verify(rondaRepository, times(1)).findByPartidaId(5);
    }

    @Test
    void shouldThrowExceptionWhenRondasListIsEmpty() {
        when(rondaRepository.findByPartidaId(99)).thenReturn(Collections.emptyList());

        assertThrows(IndexOutOfBoundsException.class, () -> rondaService.rondaActual(99));

        verify(rondaRepository, times(1)).findByPartidaId(99);
    }

}
