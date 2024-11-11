package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;

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

    @Mock
    private BazaRepository bazaRepository;


    @InjectMocks
    private BazaService bazaService;
    private RondaService rondaService;
    private PartidaService partidaService;

    private Baza baza;
    private Baza bazaV;
    private Jugador jugador;
    private Carta carta;
    private Carta cartaV;
    private Ronda ronda;
    private Partida partida;

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

        ronda = new Ronda();
        ronda.setId(1);
        ronda.setBazaActual(3);
        ronda.setEstado(RondaEstado.JUGANDO);
        ronda.setNumBazas(4);
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
        baza.setCartaGanadora(carta);
        baza.setRonda(ronda);

        // Configuración de la entidad Baza
        bazaV = new Baza();
        bazaV.setId(2);
        bazaV.setTipoCarta(TipoCarta.verde);
        bazaV.setNumBaza(4);
        bazaV.setGanador(jugador);
        bazaV.setCartaGanadora(carta);
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
        assertEquals(1, result.get(0).getCartaGanadora().getNumero());
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
    /*
    // Test: Iniciar nueva ronda cuando se supera el número de bazas de la ronda actual
    @Test
    void testNextBaza_IniciarNuevaRonda() {
        baza.setNumBaza(5); // Última baza de la ronda
        when(bazaRepository.findById(1)).thenReturn(Optional.of(baza));

        bazaService.nextBaza(1);

        verify(rondaService, times(1)).iniciarRonda(partida);
        verify(partidaService, never()).finalizarPartida(anyInt());
    }

    // Test: Finalizar partida cuando se supera el número de rondas (más de 10)
    @Test
    void testNextBaza_FinalizarPartida() {
        ronda.setNumRonda(10); // Última ronda
        baza.setNumBaza(10); // Última baza
        when(bazaRepository.findById(1)).thenReturn(Optional.of(baza));

        bazaService.nextBaza(1);

        verify(partidaService, times(1)).finalizarPartida(partida.getId());
        verify(rondaService, times(1)).save(ronda);
    }
    */
    // Test: Excepción cuando la baza no se encuentra
    @Test
    void testNextBaza_BazaNoEncontrada() {
        when(bazaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bazaService.nextBaza(99));
    }

}
