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

        // verify(manoService, times(1)).iniciarManos(partida.getId(), ronda);
        //verify(bazaService, times(1)).iniciarBazas(ronda);
        verify(rondaRepository, times(1)).save(any(Ronda.class)); 
    }
    
    @Test
    void shouldFinalizarRonda() {
   
        when(rondaRepository.findById(2)).thenReturn(Optional.of(ronda));
        //doNothing().when(rondaService).getPuntaje(anyInt(),anyInt());
        when(rondaRepository.save(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar el método
        //rondaService.finalizarRonda(2); 

        // Verificar los cambios
        verify(rondaRepository, times(1)).findById(2);
        verify(rondaRepository, times(1)).save(any(Ronda.class));
        //verify(rondaService, times(1)).getPuntaje(ronda.getNumBazas(), 2);

        assertEquals(RondaEstado.FINALIZADA, ronda.getEstado());
    }


    /*
    @Test
    void shouldNextRonda_OtraRonda() {
        ronda = new Ronda();
        ronda.setId(2);
        ronda.setEstado(RondaEstado.FINALIZADA);
        ronda.setNumBazas(4);
        ronda.setNumRonda(4);
        ronda.setPartida(partida);

        when(rondaRepository.findById(2)).thenReturn(Optional.of(ronda));
        when(rondaRepository.save(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jugadorService.findJugadoresByPartidaId(ronda.getPartida().getId())).thenReturn(List.of(new Jugador(), new Jugador(), new Jugador()));
        when(manoService.getNumCartasARepartir(anyInt(), anyInt())).thenReturn(5);
    
        Ronda result = rondaService.nextRonda(2);
    
        assertNotNull(result);
        assertEquals(Integer.valueOf(5), result.getNumRonda());
        assertEquals(Integer.valueOf(5), result.getNumBazas());
        assertEquals(RondaEstado.JUGANDO, result.getEstado());
    
        verify(rondaRepository, times(2)).findById(2);
        verify(rondaRepository, times(2)).save(any(Ronda.class));
        verify(manoService, times(1)).iniciarManos(partida.getId(), result);
        verify(bazaService, times(1)).iniciarBazas(result);
        verify(manoService, times(1)).getNumCartasARepartir(eq(5), eq(3));
        verify(partidaService, never()).finalizarPartida(anyInt());
    }
        */
    
    /*
    @Test
    void shouldNextRonda_UltimaRonda() {
        ronda = new Ronda();
        ronda.setId(10);
        ronda.setEstado(RondaEstado.FINALIZADA);
        ronda.setNumBazas(10);
        ronda.setNumRonda(10);
        ronda.setPartida(partida);

        when(rondaRepository.findById(10)).thenReturn(Optional.of(ronda));
        when(rondaRepository.save(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jugadorService.findJugadoresByPartidaId(ronda.getPartida().getId())).thenReturn(List.of(new Jugador(), new Jugador(), new Jugador()));
        when(manoService.getNumCartasARepartir(anyInt(), anyInt())).thenReturn(5);
    
        Ronda result = rondaService.nextRonda(10);
    
        assertNotNull(result);
        assertEquals((null), result.getNumRonda());
        assertEquals((null), result.getNumBazas());
        assertEquals(null, result.getEstado());
    
        verify(rondaRepository, times(2)).findById(10);
        verify(rondaRepository, times(2)).save(any(Ronda.class));
        verify(manoService, times(1)).iniciarManos(partida.getId(), result);
        verify(bazaService, times(1)).iniciarBazas(result);
    }
        */
    
    @Test
    void shouldFindRondaActualByPartidaId() {
        List<Ronda> listaRondas = List.of(ronda, rondaV);
        when(rondaRepository.findByPartidaId(5)).thenReturn(listaRondas);
    
        Ronda result = rondaService.findRondaActualByPartidaId(5);
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals(RondaEstado.JUGANDO, result.getEstado());
    
        verify(rondaRepository, times(1)).findByPartidaId(5);
    }
    


    /*
    @Test
    void shouldNextBaza_CuandoNoUltimaBaza() {
        Integer bazaId = 1;
        Integer nextBazaNum = 2;

        Partida partida = new Partida();
        partida.setId(1);
        partida.setTurnoActual(1);

        Ronda ronda = new Ronda();
        ronda.setId(1);
        ronda.setPartida(partida);
        ronda.setNumBazas(3);

        Baza currentBaza = new Baza();
        currentBaza.setId(bazaId);
        currentBaza.setNumBaza(1);
        currentBaza.setRonda(ronda);

        Baza newBaza = new Baza();
        newBaza.setNumBaza(nextBazaNum);
        newBaza.setTurnos(Arrays.asList(1, 2, 3));

        when(bazaService.findById(bazaId)).thenReturn(currentBaza);
        when(bazaService.calcularTurnosNuevaBaza(partida.getId(), currentBaza)).thenReturn(newBaza.getTurnos());
        when(bazaService.primerTurno(newBaza.getTurnos())).thenReturn(1);
        when(bazaService.saveBaza(any(Baza.class))).thenReturn(newBaza);

        Baza result = rondaService.nextBaza(bazaId);

        assertNotNull(result);
        assertEquals(nextBazaNum, result.getNumBaza());
        verify(bazaService).calcularTurnosNuevaBaza(partida.getId(), currentBaza);
        verify(partidaService).update(partida, partida.getId());
        verify(bazaService).saveBaza(any(Baza.class));
    }
        */
/* 
    @Test
    void shouldNextBaza_WhenLastBaza() {
        // Arrange
        Integer bazaId = 1;

        // Simula una partida
        Partida partida = new Partida();
        partida.setId(1);

        // Simula una ronda con 3 bazas totales
        Ronda ronda = new Ronda();
        ronda.setId(1);
        ronda.setPartida(partida);
        ronda.setNumBazas(3);

        // Simula la baza actual
        Baza currentBaza = new Baza();
        currentBaza.setId(bazaId);
        currentBaza.setNumBaza(3); // Última baza
        currentBaza.setRonda(ronda);

        // Configuración de mocks
        when(bazaService.findById(bazaId)).thenReturn(currentBaza);
        doNothing().when(rondaService).nextRonda(ronda.getId());

        // Act
        Baza result = rondaService.nextBaza(bazaId);

        // Assert
        assertNull(result.getCartaGanadora()); // Verificar que no se configura una nueva baza
        verify(rondaService).nextRonda(ronda.getId());
        verify(bazaService, never()).calcularTurnosNuevaBaza(anyInt(), any(Baza.class)); // No se calculan turnos
    }
*/
    @Test
    void shouldGetPuntaje() {
        // Inicialización de datos
        Jugador jugador = new Jugador();
        jugador.setId(1);
        jugador.setPuntos(0);

        Mano mano = new Mano();
        mano.setId(1);
        mano.setJugador(jugador);
        mano.setApuesta(2);
        mano.setResultado(2);

        Ronda ronda = new Ronda();
        ronda.setId(2);
        ronda.setNumBazas(3);

        // Configuración de mocks
        when(manoService.findAllManosByRondaId(2)).thenReturn(List.of(mano));
        when(bazaService.getPtosBonificacion(2, 1)).thenReturn(10);

        // Act
        //rondaService.getPuntaje(3, 2);

        // Assert
        verify(jugadorService, times(1)).updateJugador(any(Jugador.class), eq(1));
        assertEquals(50, jugador.getPuntos()); // 20*2 + 10 = 50
    }

}
