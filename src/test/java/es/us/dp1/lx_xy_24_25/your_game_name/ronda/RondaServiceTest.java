package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
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
    private RondaService rs;

    @Mock
    private RondaRepository rr;

    @Mock
    private PartidaService ps;

    @Mock
    private ManoService ms;

    @Mock
    private BazaService bs;

    @Mock
    private JugadorService js;


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

        // Configuración de la entidad Baza
        baza = new Baza();
        baza.setId(1);
        baza.setTipoCarta(TipoCarta.morada);
        baza.setNumBaza(3);
        baza.setGanador(jugador);
        baza.setTrucoGanador(truco);
        baza.setRonda(ronda);

        // Configuración de la entidad Baza
        bazaV = new Baza();
        bazaV.setId(2);
        bazaV.setTipoCarta(TipoCarta.verde);
        bazaV.setNumBaza(4);
        bazaV.setGanador(jugador);
        bazaV.setTrucoGanador(truco);
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
    void testGetAllRondas(){
        List<Ronda> rondaList = Arrays.asList(ronda, ronda, ronda);
        when(rr.findAll()).thenReturn(rondaList);

        List<Ronda> result = rs.getAllRondas();

        assertNotNull(result);
        assertEquals(3,result.size());
        assertEquals(ronda, result.get(0));
    }

    @Test
    void testSaveRonda() {
        when(rr.save(any(Ronda.class))).thenReturn(ronda);

        Ronda savedRonda = rs.save(ronda);

        assertNotNull(savedRonda);
        assertEquals(RondaEstado.JUGANDO, savedRonda.getEstado());
        assertEquals(4, savedRonda.getNumBazas());
        assertEquals(4, savedRonda.getNumRonda());
        verify(rr, times(1)).save(ronda);
    }

    @Test
    void testFindById(){
        when(rr.findById(2)).thenReturn(Optional.of(ronda));

        Ronda foundRonda = rs.getRondaById(2);
       
        assertNotNull(foundRonda);
        assertEquals(RondaEstado.JUGANDO, foundRonda.getEstado());
        assertEquals(4, foundRonda.getNumBazas());
        assertEquals(4, foundRonda.getNumRonda());
        verify(rr, times(1)).findById(2);
    }

    @Test
    void testFindByIdNotFound() {
        when(rr.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rs.getRondaById(1));
    }

    @Test
    void testDeleteRonda() {
        rs.delete(1);
        verify(rr, times(1)).deleteById(1);
    }

    @Test
    void testUpdateRonda() {
        Ronda newRonda = new Ronda();
        newRonda.setId(2);
        newRonda.setEstado(RondaEstado.FINALIZADA); 
        newRonda.setNumBazas(4);
        newRonda.setNumRonda(4);
        newRonda.setPartida(partida);

        when(rr.findById(1)).thenReturn(Optional.of(ronda));
        when(rr.save(any(Ronda.class))).thenReturn(newRonda);

        Ronda updatedRonda = rs.updateRonda(newRonda, 1);

        assertNotNull(updatedRonda);
        assertEquals(RondaEstado.FINALIZADA, updatedRonda.getEstado());
        verify(rr, times(1)).save(ronda);
    }

    
    @Test
    void testIniciarRonda() {
        when(rr.save(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ronda result = rs.iniciarRonda(partida);

        assertNotNull(result); 
        assertEquals(Integer.valueOf(1), result.getNumRonda()); 
        assertEquals(Integer.valueOf(1), result.getNumBazas()); 
        assertEquals(partida, result.getPartida()); 
        assertEquals(RondaEstado.JUGANDO, result.getEstado()); 

        verify(ms, times(1)).iniciarManos(partida.getId(), ronda);
        // verify(bs, times(1)).iniciarBazas(ronda);
        verify(rr, times(1)).save(any(Ronda.class)); 
    }
    
    @Test
    void testFinalizarRonda() {
   
        when(rr.findById(2)).thenReturn(Optional.of(ronda));
        when(rr.save(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ejecutar el método
        rs.finalizarRonda(2); 

        // Verificar los cambios
        verify(rr, times(1)).findById(2);
        verify(rr, times(1)).save(any(Ronda.class));
        verify(rs, times(1)).getPuntaje(ronda.getNumBazas(), 2);

        assertEquals(RondaEstado.FINALIZADA, ronda.getEstado());
    }


    @Test
    void testNextRonda() {

        ronda = new Ronda();
        ronda.setId(2);
        ronda.setEstado(RondaEstado.FINALIZADA);
        ronda.setNumBazas(4);
        ronda.setNumRonda(4);
        ronda.setPartida(partida);

        when(rr.findById(2)).thenReturn(Optional.of(ronda));
        when(rr.save(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(js.findJugadoresByPartidaId(partida.getId())).thenReturn(List.of(new Jugador(), new Jugador(), new Jugador()));
        when(ms.getNumCartasARepartir(anyInt(), anyInt())).thenReturn(5);

        // Ejecutar el método
        Ronda result = rs.nextRonda(2); 

        // Verificar los cambios
        assertNotNull(result);
        assertEquals(Integer.valueOf(5), result.getNumRonda()); 
        assertEquals(Integer.valueOf(5), result.getNumBazas());
        assertEquals(RondaEstado.JUGANDO, result.getEstado());

        // Verificar que los métodos del servicio se llamaron
        verify(rr, times(2)).findById(2);
        verify(rr, times(2)).save(any(Ronda.class));
        verify(ms, times(1)).iniciarManos(ronda.getPartida().getId(), ronda);
        verify(bs, times(1)).iniciarBazas(ronda);
        verify(ms, times(1)).getNumCartasARepartir(ronda.getNumRonda(), js.findJugadoresByPartidaId(ronda.getPartida().getId()).size());
        verify(ps, never()).finalizarPartida(anyInt());
    }

    @Test
    void testNextBaza_CuandoNoUltimaBaza() {
        // Arrange
        Integer bazaId = 1;
        Integer nextBazaNum = 2;

        // Simula una partida
        Partida partida = new Partida();
        partida.setId(1);
        partida.setTurnoActual(1);

        // Simula una ronda con 3 bazas totales
        Ronda ronda = new Ronda();
        ronda.setId(1);
        ronda.setPartida(partida);
        ronda.setNumBazas(3);

        // Simula la baza actual
        Baza currentBaza = new Baza();
        currentBaza.setId(bazaId);
        currentBaza.setNumBaza(1);
        currentBaza.setRonda(ronda);

        // Simula una nueva baza
        Baza newBaza = new Baza();
        newBaza.setNumBaza(nextBazaNum);

        // Lista de turnos para la nueva baza
        List<Integer> turnos = Arrays.asList(1, 2, 3);

        when(bs.findById(bazaId)).thenReturn(currentBaza);
        when(bs.calcularTurnosNuevaBaza(partida.getId(), currentBaza)).thenReturn(turnos);
        when(bs.primerTurno(turnos)).thenReturn(1);
        when(bs.saveBaza(any(Baza.class))).thenReturn(newBaza);

        // Act
        Baza result = rs.nextBaza(bazaId);

        // Assert
        assertNotNull(result);
        assertEquals(nextBazaNum, result.getNumBaza());
        verify(bs).calcularTurnosNuevaBaza(partida.getId(), currentBaza);
        verify(ps).update(partida, partida.getId());
        verify(bs).saveBaza(any(Baza.class));
    }

    @Test
    void testNextBaza_WhenLastBaza() {
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
        when(bs.findById(bazaId)).thenReturn(currentBaza);
        doNothing().when(rs).nextRonda(ronda.getId());

        // Act
        Baza result = rs.nextBaza(bazaId);

        // Assert
        assertNull(result.getTrucoGanador()); // Verificar que no se configura una nueva baza
        verify(rs).nextRonda(ronda.getId());
        verify(bs, never()).calcularTurnosNuevaBaza(anyInt(), any(Baza.class)); // No se calculan turnos
    }



}
