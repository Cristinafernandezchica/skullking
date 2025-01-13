package es.us.dp1.lx_xy_24_25.your_game_name.carta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;

public class CartaServiceTest {

    @InjectMocks
    private CartaService cartaService;

    @Mock
    private CartaRepository cartaRepository;

    private Carta carta;
    private final Integer ID_TIGRESA_BANDERA_BLANCA = 71;
    private final Integer ID_TIGRESA_PIRATA = 72;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        carta = new Carta();
        carta.setId(1);
        carta.setNumero(5);
        carta.setTipoCarta(TipoCarta.pirata);
        carta.setImagenFrontal("./images/cartas/pirata_5.png");
    }

    @Test
    void shouldSaveCarta() {
        when(cartaRepository.save(any(Carta.class))).thenReturn(carta);

        Carta savedCarta = cartaService.saveCarta(carta);

        assertNotNull(savedCarta);
        assertEquals(1, savedCarta.getId());
        verify(cartaRepository, times(1)).save(carta);
    }

    @Test
    void shouldFindAllCartas() {
        when(cartaRepository.findAll()).thenReturn(List.of(carta));

        Iterable<Carta> cartas = cartaService.findAll();

        assertNotNull(cartas);
        assertEquals(1, ((List<Carta>) cartas).size());
        verify(cartaRepository, times(1)).findAll();
    }

    @Test
    void shouldFindCartaById() {
        when(cartaRepository.findById(1)).thenReturn(Optional.of(carta));

        Optional<Carta> foundCarta = cartaService.findById(1);

        assertTrue(foundCarta.isPresent());
        assertEquals(1, foundCarta.get().getId());
        verify(cartaRepository, times(1)).findById(1);
    }

    @Test
    void shouldFindCartaById_NotFound() {
        when(cartaRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Carta> foundCarta = cartaService.findById(99);

        assertFalse(foundCarta.isPresent());
        verify(cartaRepository, times(1)).findById(99);
    }

    @Test
    void shouldDeleteCarta() {
        doNothing().when(cartaRepository).deleteById(1);

        cartaService.deleteCarta(1);
        assertEquals(cartaService.findById(1),Optional.empty());
        verify(cartaRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldUpdateCarta() {
        Carta updatedCarta = new Carta();
        updatedCarta.setNumero(10);
        updatedCarta.setTipoCarta(TipoCarta.banderaBlanca);
        updatedCarta.setImagenFrontal("./images/cartas/blanca_10.png");

        when(cartaRepository.findById(1)).thenReturn(Optional.of(carta));
        when(cartaRepository.save(any(Carta.class))).thenReturn(updatedCarta);

        Carta result = cartaService.updateCarta(updatedCarta, 1);

        assertNotNull(result);
        assertEquals(10, result.getNumero());
        assertEquals(TipoCarta.banderaBlanca, result.getTipoCarta());
        verify(cartaRepository, times(1)).findById(1);
        verify(cartaRepository, times(1)).save(any(Carta.class));
    }

    @Test
    void shouldUpdateCarta_NotFound() {
        Carta updatedCarta = new Carta();
        updatedCarta.setNumero(10);
        updatedCarta.setTipoCarta(TipoCarta.banderaBlanca);
        updatedCarta.setImagenFrontal("./images/cartas/blanca_10.png");

        when(cartaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> cartaService.updateCarta(updatedCarta, 1));
        verify(cartaRepository, times(1)).findById(1);
        verify(cartaRepository, times(0)).save(any(Carta.class));
    }

    @Test
    void shouldCambioTigresa_ToPirata() {
        Carta tigresaPirata = new Carta();
        tigresaPirata.setId(ID_TIGRESA_PIRATA);
        tigresaPirata.setNumero(72);
        tigresaPirata.setTipoCarta(TipoCarta.pirata);

        when(cartaRepository.findById(ID_TIGRESA_PIRATA)).thenReturn(Optional.of(tigresaPirata));

        Carta result = cartaService.cambioTigresa("pirata");

        assertNotNull(result);
        assertEquals(ID_TIGRESA_PIRATA, result.getId());
        assertEquals(TipoCarta.pirata, result.getTipoCarta());
        verify(cartaRepository, times(1)).findById(ID_TIGRESA_PIRATA);
    }

    @Test
    void shouldCambioTigresa_ToBanderaBlanca() {
        Carta tigresaBlanca = new Carta();
        tigresaBlanca.setId(ID_TIGRESA_BANDERA_BLANCA);
        tigresaBlanca.setNumero(71);
        tigresaBlanca.setTipoCarta(TipoCarta.banderaBlanca);

        when(cartaRepository.findById(ID_TIGRESA_BANDERA_BLANCA)).thenReturn(Optional.of(tigresaBlanca));

        Carta result = cartaService.cambioTigresa("blanca");

        assertNotNull(result);
        assertEquals(ID_TIGRESA_BANDERA_BLANCA, result.getId());
        assertEquals(TipoCarta.banderaBlanca, result.getTipoCarta());
        verify(cartaRepository, times(1)).findById(ID_TIGRESA_BANDERA_BLANCA);
    }

}
