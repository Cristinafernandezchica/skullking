package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.CartaService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.exceptions.ApuestaNoValidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    public void shouldFindAllByRondaId() {
        when(manoRepository.findAllByRondaId(1)).thenReturn(Arrays.asList(mano1, mano2));

        List<Mano> result = manoService.findAllByRondaId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        verify(manoRepository, times(1)).findAllByRondaId(1);
    }

    @Test
    public void shouldFindAllByRondaIdEmpty() {
        when(manoRepository.findAllByRondaId(1)).thenReturn(Collections.emptyList());

        List<Mano> result = manoService.findAllByRondaId(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(manoRepository, times(1)).findAllByRondaId(1);
    }

    @Test
    public void shouldFindAllByRondaIdThrowsDataAccessException() {
        when(manoRepository.findAllByRondaId(1)).thenThrow(new DataAccessException("..."){});

        assertThrows(DataAccessException.class, () -> manoService.findAllByRondaId(1));
        verify(manoRepository, times(1)).findAllByRondaId(1);
    }

    // tests para iniciarManos
    @Test
    public void shouldIniciarManos() {
        when(cartaService.findAll()).thenReturn(listaCartas);
        when(jugadorService.findJugadoresByPartidaId(partida.getId())).thenReturn(jugadores);

        manoService.iniciarManos(partida.getId(), ronda, jugadores);

        verify(cartaService, times(1)).findAll();
        verify(jugadorService, times(1)).findJugadoresByPartidaId(partida.getId());
        verify(manoRepository, times(2)).save(any(Mano.class));
    }

    @Test
    public void shouldIniciarManosFiltrarCartasEspeciales() {
        // Crear nuevas cartas con IDs correctos
        Carta cartaEspecial1 = new Carta();
        cartaEspecial1.setId(ID_TIGRESA_BANDERA_BLANCA);
        
        Carta cartaEspecial2 = new Carta();
        cartaEspecial2.setId(ID_TIGRESA_PIRATA);

        listaCartas.add(cartaEspecial1);
        listaCartas.add(cartaEspecial2);

        for (int i = 0; i < listaCartas.size(); i++) {
            if (listaCartas.get(i).getId() == null) {
                listaCartas.get(i).setId(i + 1); // Asignar un ID válido
            }
        }

        when(cartaService.findAll()).thenReturn(listaCartas);
        when(jugadorService.findJugadoresByPartidaId(partida.getId())).thenReturn(jugadores);

        manoService.iniciarManos(partida.getId(), ronda, jugadores);

        // Verifica que las cartas especiales no están presentes
        List<Carta> cartasFiltradas = listaCartas.stream()
                .filter(c -> !(c.getId().equals(ID_TIGRESA_BANDERA_BLANCA) || c.getId().equals(ID_TIGRESA_PIRATA)))
                .collect(Collectors.toList());
        assertEquals(10, cartasFiltradas.size()); // Originalmente hay 12 cartas

        verify(cartaService, times(1)).findAll();
        verify(jugadorService, times(1)).findJugadoresByPartidaId(partida.getId());
        verify(manoRepository, times(2)).save(any(Mano.class));
    }


    @Test
    public void shouldIniciarManosDistribuirCartas() {
        when(cartaService.findAll()).thenReturn(listaCartas);
        when(jugadorService.findJugadoresByPartidaId(partida.getId())).thenReturn(jugadores);

        manoService.iniciarManos(partida.getId(), ronda, jugadores);

        verify(cartaService, times(1)).findAll();
        verify(jugadorService, times(1)).findJugadoresByPartidaId(partida.getId());
        verify(manoRepository, times(2)).save(any(Mano.class));
    }

    @Test
    public void shouldIniciarManosBarajarCartas() {
        when(cartaService.findAll()).thenReturn(listaCartas);
        when(jugadorService.findJugadoresByPartidaId(partida.getId())).thenReturn(jugadores);

        // Crear una copia de los IDs de las cartas antes de barajar
        List<Integer> copiaIdsCartas = listaCartas.stream().map(Carta::getId).collect(Collectors.toList());

        manoService.iniciarManos(partida.getId(), ronda, jugadores);

        verify(cartaService, times(1)).findAll();
        verify(jugadorService, times(1)).findJugadoresByPartidaId(partida.getId());
        verify(manoRepository, times(2)).save(any(Mano.class));

        // Capturar las manos guardadas para verificar
        ArgumentCaptor<Mano> manoCaptor = ArgumentCaptor.forClass(Mano.class);
        verify(manoRepository, times(2)).save(manoCaptor.capture());

        List<Mano> manosGuardadas = manoCaptor.getAllValues();
        
        // Verificar que las cartas dentro de las manos no están en el mismo orden que la copia original
        for (Mano mano : manosGuardadas) {
            List<Integer> idsCartasMano = mano.getCartas().stream().map(Carta::getId).collect(Collectors.toList());
            assertNotEquals(copiaIdsCartas, idsCartasMano, "La lista de cartas no ha sido barajada correctamente");
        }
    }



    @Test
    public void shouldIniciarManosAsignarCartas() {
        when(cartaService.findAll()).thenReturn(listaCartas);
        when(jugadorService.findJugadoresByPartidaId(partida.getId())).thenReturn(jugadores);

        manoService.iniciarManos(partida.getId(), ronda, jugadores);

        verify(cartaService, times(1)).findAll();
        verify(jugadorService, times(1)).findJugadoresByPartidaId(partida.getId());

        // Capturar todas las invocaciones de save para Mano
        ArgumentCaptor<Mano> manoCaptor = ArgumentCaptor.forClass(Mano.class);
        verify(manoRepository, times(2)).save(manoCaptor.capture());

        List<Mano> manosGuardadas = manoCaptor.getAllValues();

        assertEquals(jugadores.size(), manosGuardadas.size());

        for (int i = 0; i < jugadores.size(); i++) {
            Mano mano = manosGuardadas.get(i);
            assertNotNull(mano.getCartas());
            assertTrue(mano.getCartas().size() > 0);
            assertEquals(0, mano.getApuesta());
            assertEquals(0, mano.getResultado());
            assertEquals(jugadores.get(i), mano.getJugador());
            assertEquals(ronda, mano.getRonda());
        }
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

    // Tests apostar --> LO COMENTO SE HA MOVIDO A Partida
    /*
    @Test
    public void shouldApuestaExito() {
        // Crear la lista de manos y asegurar que findLastManoByJugadorId devuelve la última mano
        List<Mano> manos = List.of(mano);
        when(manoRepository.findAllManoByJugadorId(jugador3.getId())).thenReturn(manos);
        when(jugadorService.findById(jugador3.getId())).thenReturn(jugador3);

        manoService.apuesta(2, jugador3.getId());

        assertEquals(2, mano.getApuesta());
        assertEquals(2, jugador3.getApuestaActual());
        verify(manoRepository, times(1)).save(mano);
        verify(jugadorService, times(1)).updateJugador(jugador3, jugador3.getId());
    }

    @Test
    public void shouldApuestaManoNoEncontrada() {
        when(manoRepository.findAllManoByJugadorId(jugador3.getId())).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> manoService.apuesta(2, jugador3.getId()));
    }

    @Test
    public void shouldApuestaMayorQueNumCartas() {
        List<Mano> manos = List.of(mano);
        when(manoRepository.findAllManoByJugadorId(jugador3.getId())).thenReturn(manos);
        when(jugadorService.findById(jugador3.getId())).thenReturn(jugador3);

        assertThrows(ApuestaNoValidaException.class, () -> manoService.apuesta(5, jugador3.getId()));
    }

    @Test
    public void shouldApuestaExactaNumCartas() {
        List<Mano> manos = List.of(mano);
        when(manoRepository.findAllManoByJugadorId(jugador3.getId())).thenReturn(manos);
        when(jugadorService.findById(jugador3.getId())).thenReturn(jugador3);

        manoService.apuesta(3, jugador3.getId());

        assertEquals(3, mano.getApuesta());
        assertEquals(3, jugador3.getApuestaActual());
        verify(manoRepository, times(1)).save(mano);
        verify(jugadorService, times(1)).updateJugador(jugador3, jugador3.getId());
    }

    @Test
    public void shouldApuestaCero() {
        List<Mano> manos = List.of(mano);
        when(manoRepository.findAllManoByJugadorId(jugador3.getId())).thenReturn(manos);
        when(jugadorService.findById(jugador3.getId())).thenReturn(jugador3);

        manoService.apuesta(0, jugador3.getId());

        assertEquals(0, mano.getApuesta());
        assertEquals(0, jugador3.getApuestaActual());
        verify(manoRepository, times(1)).save(mano);
        verify(jugadorService, times(1)).updateJugador(jugador3, jugador3.getId());
    }
        */

    // tests cartasDisabled
    @Test
    public void shouldCartasDisabledManoNoEncontrada() {
        when(manoRepository.findById(mano3.getId())).thenReturn(Optional.empty());

        List<Carta> result = manoService.cartasDisabled(mano3.getId(), TipoCarta.morada);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldCartasDisabledSinTipoCarta() {
        when(manoRepository.findById(mano3.getId())).thenReturn(Optional.of(mano3));

        List<Carta> result = manoService.cartasDisabled(mano3.getId(), TipoCarta.sinDeterminar);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldCartasDisabledConTipoCarta() {
        Carta carta1 =  new Carta();
        carta1 = new Carta();
        carta1.setId(1);
        carta1.setTipoCarta(TipoCarta.morada);
        cartas2.add(carta1);

        when(manoRepository.findById(mano3.getId())).thenReturn(Optional.of(mano3));

        List<Carta> result = manoService.cartasDisabled(mano3.getId(), TipoCarta.morada);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(carta2));
        assertTrue(result.contains(carta3));
    }

    @Test
    public void shouldCartasDisabledConCartaEspecial() {
        Carta cartaEspecial = new Carta();
        cartaEspecial.setId(4);
        cartaEspecial.setTipoCarta(TipoCarta.pirata); // Definimos esta carta como especial según el método esCartaEspecial

        // Crear una copia mutable de la lista de cartas
        List<Carta> cartasMutable = new ArrayList<>(cartas2);
        cartasMutable.add(cartaEspecial);
        mano3.setCartas(cartasMutable);

        when(manoRepository.findById(mano3.getId())).thenReturn(Optional.of(mano3));

        List<Carta> result = manoService.cartasDisabled(mano3.getId(), TipoCarta.morada);
        assertNotNull(result);
        assertEquals(2, result.size()); // deberían estar deshabilitadas carta2 y carta3
        assertTrue(result.contains(carta2)); // carta2
        assertTrue(result.contains(carta3)); // carta3
        assertFalse(result.contains(cartaEspecial)); // cartaEspecial no debería estar deshabilitada
    }

    @Test
    public void shouldCartasDisabledConEspecialYPalobaza() {
        Carta cartaEspecial = new Carta();
        cartaEspecial.setId(4);
        cartaEspecial.setTipoCarta(TipoCarta.pirata); // Carta especial

        Carta cartaMorada = new Carta();
        cartaMorada.setId(5);
        cartaMorada.setTipoCarta(TipoCarta.morada); // Carta del tipo especificado

        // Crear una copia mutable de la lista de cartas
        List<Carta> cartasMutable = new ArrayList<>(cartas2);
        cartasMutable.add(cartaEspecial);
        cartasMutable.add(cartaMorada);
        mano3.setCartas(cartasMutable);

        when(manoRepository.findById(mano3.getId())).thenReturn(Optional.of(mano3));

        List<Carta> result = manoService.cartasDisabled(mano3.getId(), TipoCarta.morada);
        assertNotNull(result);
        assertEquals(2, result.size()); // Deberían estar deshabilitadas carta2 y carta3
        assertTrue(result.contains(carta2)); // carta2
        assertTrue(result.contains(carta3)); // carta3
        assertFalse(result.contains(cartaEspecial)); // cartaEspecial no debería estar deshabilitada
        assertFalse(result.contains(cartaMorada)); // cartaMorada no debería estar deshabilitada
    }

    // con este test hemos detectado un fallo en el cálculo de las cartas que deben estar deshabilitadas

}
