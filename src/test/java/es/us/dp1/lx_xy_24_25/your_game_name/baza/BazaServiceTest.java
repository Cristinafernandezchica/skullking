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
    private RondaService rondaService;

    @MockBean
    private TrucoService trucoService;

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
        jugador.setTurno(1);
        jugador.setUsuario(null);
        jugador.setPartida(partida);


        carta = new Carta();
        carta.setId(1);
        carta.setImagenFrontal("./images/cartas/morada_1.png");
        carta.setImagenTrasera("./images/cartas/parte_trasera.png");
        carta.setNumero(1);
        carta.setTipoCarta(TipoCarta.morada);

        cartaV = new Carta();
        cartaV.setId(29);
        cartaV.setImagenFrontal("./images/cartas/verde_1.png");
        cartaV.setImagenTrasera("./images/cartas/parte_trasera.png");
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

        ronda = new Ronda();
        ronda.setId(1);
        ronda.setBazaActual(3);
        ronda.setEstado(RondaEstado.JUGANDO);
        ronda.setNumBazas(3);
        ronda.setNumRonda(4);
        ronda.setPartida(partida);

        partida = new Partida();
        partida.setEstado(PartidaEstado.JUGANDO);
        partida.setFin(null);
        partida.setId(5);
        partida.setInicio(LocalDateTime.now());
        partida.setNombre("Partida Test");
        partida.setOwnerPartida(1);

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
        bazaV.setTrucoGanador(trucoT);
        bazaV.setRonda(ronda);
    }

    // Test para guardar una Baza
    @Test
    void testSaveBaza() {
        when(bazaRepository.save(any(Baza.class))).thenReturn(baza);

        Baza savedBaza = bazaService.saveBaza(baza);

        assertNotNull(savedBaza);
        assertEquals(TipoCarta.morada, savedBaza.getTipoCarta());
        assertEquals(3, savedBaza.getNumBaza());
        assertEquals(1, savedBaza.getGanador().getId());
        verify(bazaRepository, times(1)).save(baza);
    }

    // Test para listar todas las Bazas
    @Test
    void testGetAllBazas() {
        List<Baza> bazaList = Arrays.asList(baza);
        when(bazaRepository.findAll()).thenReturn(bazaList);

        List<Baza> result = bazaService.getAllBazas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getTrucoGanador().getCarta().getNumero());
        verify(bazaRepository, times(1)).findAll();
    }

    // Test para obtener una Baza por ID
    @Test
    void testFindById() {
        when(bazaRepository.findById(1)).thenReturn(Optional.of(baza));

        Baza foundBaza = bazaService.findById(1);

        assertNotNull(foundBaza);
        assertEquals(1, foundBaza.getId());
        assertEquals(TipoCarta.morada, foundBaza.getTipoCarta());
        verify(bazaRepository, times(1)).findById(1);
    }

    // Test para obtener una Baza por ID (Excepción)
    @Test
    void testFindByIdNotFound() {
        when(bazaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bazaService.findById(99));
    }

    // Test para eliminar una Baza por ID
    @Test
    void testDeleteBaza() {
        bazaService.deleteBaza(1);
        verify(bazaRepository, times(1)).deleteById(1);
    }

    // Test para actualizar una Baza existente
    @Test
    void testUpdateBaza() {
        Baza newBaza = new Baza();
        newBaza.setTipoCarta(TipoCarta.amarillo);
        newBaza.setNumBaza(5);
        newBaza.setGanador(jugador);

        when(bazaRepository.findById(1)).thenReturn(Optional.of(baza));
        when(bazaRepository.save(any(Baza.class))).thenReturn(newBaza);

        Baza updatedBaza = bazaService.updateBaza(newBaza, 1);

        assertNotNull(updatedBaza);
        assertEquals(TipoCarta.amarillo, updatedBaza.getTipoCarta());
        assertEquals(5, updatedBaza.getNumBaza());
        verify(bazaRepository, times(1)).save(baza);
    }

    // Test para obtener la última Baza por Ronda ID
    @Test
    void testFindUltimaBazaByRondaId() {
        Baza baza1 = new Baza();
        baza1.setId(1);
        Baza baza2 = new Baza();
        baza2.setId(2);

        List<Baza> bazaList = Arrays.asList(baza1, baza2);

        when(bazaRepository.findBazasByRondaId(1)).thenReturn(bazaList);

        Baza ultimaBaza = bazaService.findUltimaBazaByRondaId(1);

        assertNotNull(ultimaBaza);
        assertEquals(2, ultimaBaza.getId()); // Debería devolver la última baza por ID (orden descendente)
        verify(bazaRepository, times(1)).findBazasByRondaId(1);
    }
    
    
    @Test
    void testFindByRondaIdAndNumBaza() {
        when(bazaRepository.findByRondaIdAndNumBaza(1, 3)).thenReturn(Optional.of(baza));

        Baza foundBaza = bazaService.findByRondaIdAndNumBaza(1, 3);

        assertNotNull(foundBaza);
        assertEquals(1, foundBaza.getId());
        assertEquals(3, foundBaza.getNumBaza());
    }

       // Test para iniciar una nueva Baza
       @Test
       void testIniciarBazas() {
           when(bazaRepository.save(any(Baza.class))).thenAnswer(invocation -> invocation.getArgument(0));
   
           Baza nuevaBaza = bazaService.iniciarBazas(ronda);
   
           assertNotNull(nuevaBaza);
           assertEquals(1, nuevaBaza.getNumBaza());
           assertNull(nuevaBaza.getTrucoGanador());
           assertNull(nuevaBaza.getGanador());
           assertEquals(ronda, nuevaBaza.getRonda());
   
           //Cuando esté hecho iniciarTrucos, descomentar y probar (antes de esta implementacion funciona todo :D)
           verify(trucoService, times(1)).crearTrucosBazaConTurno(nuevaBaza.getId());

           verify(bazaRepository, times(1)).save(nuevaBaza);
       }


    // Test para nextBaza: Incrementar numBaza dentro de la misma ronda
    @Test
    void testNextBaza_IncrementarNumBaza() {
        baza.setNumBaza(1);
        when(bazaRepository.findById(1)).thenReturn(Optional.of(baza));
        when(bazaRepository.save(any(Baza.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Baza resultado = bazaService.nextBaza(1);

        assertNotNull(resultado);
        assertEquals(2, resultado.getNumBaza());
        assertNull(resultado.getTrucoGanador());
        assertNull(resultado.getGanador());

        verify(bazaRepository, times(1)).save(baza);
        verify(rondaService, never()).nextRonda(anyInt());
    }

    // Test para nextBaza: Cambiar a la siguiente ronda cuando se alcanza la última baza
    @Test
    void testNextBaza_CambiarARondaSiguiente() {
        baza.setNumBaza(3);
        when(bazaRepository.findById(1)).thenReturn(Optional.of(baza));
        when(bazaRepository.save(any(Baza.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Baza resultado = bazaService.nextBaza(1);

        assertNotNull(resultado);
        assertEquals(3, resultado.getNumBaza());

        verify(rondaService, times(1)).nextRonda(ronda.getId());
        verify(bazaRepository, times(1)).save(baza);
    }
    
    // Test: Excepción cuando la baza no se encuentra
    @Test
    void testNextBaza_BazaNoEncontrada() {
        when(bazaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bazaService.nextBaza(99));
    }

}
