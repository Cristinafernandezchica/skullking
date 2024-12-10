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

        mano =new Mano();
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
        /*
        partida = new Partida();
        partida.setEstado(PartidaEstado.JUGANDO);
        partida.setFin(null);
        partida.setId(5);
        partida.setInicio(LocalDateTime.now());
        partida.setNombre("Partida Test");
        partida.setOwnerPartida(1);
        */

        // Configuración de la entidad Baza
        baza = new Baza();
        baza.setId(1);
        baza.setPaloBaza(PaloBaza.morada);
        baza.setNumBaza(3);
        baza.setGanador(jugador);
        baza.setTurnos(List.of());
        baza.setCartaGanadora(carta);
        baza.setRonda(ronda);

        // Configuración de la entidad Baza
        bazaV = new Baza();
        bazaV.setId(2);
        bazaV.setPaloBaza(PaloBaza.verde);
        bazaV.setNumBaza(4);
        bazaV.setGanador(jugador);
        bazaV.setTurnos(List.of());
        bazaV.setCartaGanadora(cartaV);
        bazaV.setRonda(ronda);
    }

    // Test para guardar una Baza
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

    // Test para listar todas las Bazas
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

    // Test para obtener una Baza por ID
    @Test
    void shouldFindById() {
        when(bazaRepository.findById(1)).thenReturn(Optional.of(baza));

        Baza foundBaza = bazaService.findById(1);

        assertNotNull(foundBaza);
        assertEquals(1, foundBaza.getId());
        assertEquals(PaloBaza.morada, foundBaza.getPaloBaza());
        verify(bazaRepository, times(1)).findById(1);
    }

    // Test para obtener una Baza por ID (Excepción)
    @Test
    void shouldFindByIdNotFound() {
        when(bazaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bazaService.findById(99));
    }

    // Test para eliminar una Baza por ID
    @Test
    void shouldDeleteBaza() {
        bazaService.deleteBaza(1);
        verify(bazaRepository, times(1)).deleteById(1);
    }

    // Test para actualizar una Baza existente
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
    void shouldFindByIdRondaAndIdJugador() {
        List<Baza> bazas = Arrays.asList(baza);
        when(bazaRepository.findByIdRondaAndIdJugador(1, 1)).thenReturn(bazas);

        List<Baza> resultado = bazaService.findByIdRondaAndIdJugador(1, 1);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(bazaRepository, times(1)).findByIdRondaAndIdJugador(1, 1);
    }

    //TODO: findBazaAnterior() cuando no cree dependencia circular
    /*
    @Test
    void shouldFindBazaAnterior() {
        when(bazaRepository.findBazaAnterior(1, 1)).thenReturn(Optional.of(baza));

        Baza bazaAnterior = bazaService.findBazaAnterior(1, 1);

        assertNotNull(bazaAnterior);
        assertEquals(1, bazaAnterior.getId());
        verify(bazaRepository, times(1)).findBazaAnterior(1, 1);
    }
    */




       // Test para iniciar una nueva Baza
       @Test
       void shouldIniciarBazas() {
           when(bazaRepository.save(any(Baza.class))).thenAnswer(invocation -> invocation.getArgument(0));
   
           Baza nuevaBaza = bazaService.iniciarBazas(ronda);
   
           assertNotNull(nuevaBaza);
           assertEquals(1, nuevaBaza.getNumBaza());
           assertNull(nuevaBaza.getCartaGanadora());
           assertNull(nuevaBaza.getGanador());
           assertEquals(ronda, nuevaBaza.getRonda());
   
           //Cuando esté hecho iniciarTrucos, descomentar y probar (antes de esta implementacion funciona todo :D)
           // verify(trucoService, times(1)).crearTrucosBazaConTurno(nuevaBaza.getId());

           verify(bazaRepository, times(1)).save(nuevaBaza);
       }


       @Test
       void shouldCalcularTurnosNuevaBaza_PrimeraBaza() {
           List<Jugador> jugadores = Arrays.asList(jugador);
           when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(jugadores);
   
           List<Integer> turnos = bazaService.calcularTurnosNuevaBaza(1, null);
   
           assertNotNull(turnos);
           assertEquals(1, turnos.size());
           assertEquals(1, turnos.get(0));
           verify(jugadorService, times(1)).findJugadoresByPartidaId(1);
       }
   
       @Test
       void shouldCalcularTurnosNuevaBaza_ConGanador() {
           List<Jugador> jugadores = Arrays.asList(jugador);
           when(jugadorService.findJugadoresByPartidaId(1)).thenReturn(jugadores);
   
           baza.setGanador(jugador);
           List<Integer> turnos = bazaService.calcularTurnosNuevaBaza(1, baza);
   
           assertNotNull(turnos);
           assertEquals(1, turnos.size());
           assertEquals(1, turnos.get(0));
           verify(jugadorService, times(1)).findJugadoresByPartidaId(1);
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
       void shouldGetPrimeraSirena() {
           List<Truco> sirenas = Arrays.asList(truco);
           Truco resultado = bazaService.getPrimeraSirena(sirenas);
   
           assertNotNull(resultado);
           assertEquals(1, resultado.getId());
       }
   
       @Test
       void shouldGetPrimerPirata() {
           List<Truco> piratas = Arrays.asList(truco);
           Truco resultado = bazaService.getPrimerPirata(piratas);
   
           assertNotNull(resultado);
           assertEquals(1, resultado.getId());
       }
   
       @Test
       void shouldGetTriunfoMayorTruco() {
           truco.getCarta().setNumero(10);
           List<Truco> triunfos = Arrays.asList(truco);
           Truco resultado = bazaService.getTriunfoMayorTruco(triunfos);
   
           assertNotNull(resultado);
           assertEquals(1, resultado.getId());
       }
   
       @Test
       void shouldGetCartaPaloMayorNum() {
           truco.getCarta().setNumero(10);
           List<Truco> cartasPalo = Arrays.asList(truco);
           Truco resultado = bazaService.getCartaPaloMayorNum(cartasPalo);
   
           assertNotNull(resultado);
           assertEquals(1, resultado.getId());
       }

   }


/*
    // Test para nextBaza: Incrementar numBaza dentro de la misma ronda
    @Test
    void shouldNextBaza_IncrementarNumBaza() {
        baza.setNumBaza(1);
        when(bazaRepository.findById(1)).thenReturn(Optional.of(baza));
        when(bazaRepository.save(any(Baza.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Baza resultado = rondaService.nextBaza(1);

        assertNotNull(resultado);
        assertEquals(2, resultado.getNumBaza());
        assertNull(resultado.getCartaGanadora());
        assertNull(resultado.getGanador());

        verify(bazaRepository, times(1)).save(baza);
        verify(rondaService, never()).nextRonda(anyInt());
    }

    // Test para nextBaza: Cambiar a la siguiente ronda cuando se alcanza la última baza
    @Test
    void shouldNextBaza_CambiarARondaSiguiente() {
        baza.setNumBaza(3);
        when(bazaRepository.findById(1)).thenReturn(Optional.of(baza));
        when(bazaRepository.save(any(Baza.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Baza resultado = rondaService.nextBaza(1);

        assertNotNull(resultado);
        assertEquals(3, resultado.getNumBaza());

        verify(rondaService, times(1)).nextRonda(ronda.getId());
        verify(bazaRepository, times(1)).save(baza);
    }
    
    // Test: Excepción cuando la baza no se encuentra
    @Test
    void shouldNextBaza_BazaNoEncontrada() {
        when(bazaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rondaService.nextBaza(99));
    }

    /*
        @Test
        void shouldCalculoGanador() {
            Baza baza2 = new Baza();
            baza2.setId(1);
            baza2.setTipoCarta(TipoCarta.morada);
            baza2.setNumBaza(1);
            baza2.setGanador(null);
            baza2.setTrucoGanador(null);
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
    
            bazaService.calculoGanador(baza2.getId());
            Baza bazaActualizada = bazaService.findById(baza2.getId());
    
            assertEquals(truco2.getCarta(), bazaActualizada.getTrucoGanador().getCarta());
    
            verify(bazaRepository, times(1)).save(baza2);
        }
    */
