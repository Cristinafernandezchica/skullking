package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

public class BazaValidatorTest {

    private BazaValidator bazaValidator;

    @Mock
    private BazaService bazaService;

    private Baza baza;
    private Ronda ronda;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        bazaValidator = new BazaValidator(bazaService);

        ronda = new Ronda();
        ronda.setId(1);
        ronda.setNumBazas(5);

        baza = new Baza();
        baza.setId(1);
        baza.setNumBaza(3);
        baza.setRonda(ronda);
    }

    @Test
    void shouldAcceptValidBaza() {
        Errors errors = new BeanPropertyBindingResult(baza, "baza");
        bazaValidator.validate(baza, errors);

        assertFalse(errors.hasErrors());
    }

    // El número de baza excede el máximo permitido en la ronda
    @Test
    void shouldRejectBazaExcedeMaxNumBazas() {
        baza.setNumBaza(6);
        Errors errors = new BeanPropertyBindingResult(baza, "baza");
        bazaValidator.validate(baza, errors);

        assertTrue(errors.hasFieldErrors("numBaza"));
        assertEquals("numBaza.exceeds", errors.getFieldError("numBaza").getCode());
    }

    // Existe una baza duplicada con el mismo número en la misma ronda
    @Test
    void shouldRejectDuplicateBaza() {
        Baza existingBaza = new Baza();
        existingBaza.setId(2);
        existingBaza.setNumBaza(3);
        existingBaza.setRonda(ronda);

        when(bazaService.findByRondaIdAndNumBaza(ronda.getId(), baza.getNumBaza()))
            .thenReturn(existingBaza);

        Errors errors = new BeanPropertyBindingResult(baza, "baza");
        bazaValidator.validate(baza, errors);

        assertTrue(errors.hasFieldErrors("numBaza"));
        assertEquals("numBaza.duplicate", errors.getFieldError("numBaza").getCode());

        verify(bazaService, times(1)).findByRondaIdAndNumBaza(ronda.getId(), baza.getNumBaza());
    }

    // La baza actual es la misma que la encontrada
    @Test
    void shouldAceptarMismaBaza_NoDuplicadoError() {
        when(bazaService.findByRondaIdAndNumBaza(ronda.getId(), baza.getNumBaza()))
            .thenReturn(baza); // Mismo ID

        Errors errors = new BeanPropertyBindingResult(baza, "baza");
        bazaValidator.validate(baza, errors);

        assertFalse(errors.hasErrors());
        verify(bazaService, times(1)).findByRondaIdAndNumBaza(ronda.getId(), baza.getNumBaza());
    }

    // Ronda es null
    @Test
    void shouldManejarNullRonda() {
        baza.setRonda(null);
        Errors errors = new BeanPropertyBindingResult(baza, "baza");
        bazaValidator.validate(baza, errors);

        assertFalse(errors.hasErrors());
    }

    // Número de baza es null
    @Test
    void shouldManejarNullNumBaza() {
        baza.setNumBaza(null);
        Errors errors = new BeanPropertyBindingResult(baza, "baza");
        bazaValidator.validate(baza, errors);

        assertFalse(errors.hasErrors());
    }

    // Número máximo de bazas en la ronda es null
    @Test
    void shouldManejarNullNumBazasInRonda() {
        ronda.setNumBazas(null);
        Errors errors = new BeanPropertyBindingResult(baza, "baza");
        bazaValidator.validate(baza, errors);

        assertFalse(errors.hasErrors());
    }

    // No existe baza duplicada
    @Test
    void shouldManejarNullBazaExistente() {
        when(bazaService.findByRondaIdAndNumBaza(ronda.getId(), baza.getNumBaza())).thenReturn(null);

        Errors errors = new BeanPropertyBindingResult(baza, "baza");
        bazaValidator.validate(baza, errors);

        assertFalse(errors.hasErrors());
        verify(bazaService, times(1)).findByRondaIdAndNumBaza(ronda.getId(), baza.getNumBaza());
    }
}

