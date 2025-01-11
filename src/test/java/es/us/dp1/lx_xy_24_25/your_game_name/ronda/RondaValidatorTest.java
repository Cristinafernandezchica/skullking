package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@ExtendWith(MockitoExtension.class)
public class RondaValidatorTest {

    @Mock
    private RondaService rondaService;

    private RondaValidator rondaValidator;

    @BeforeEach
    void setup() {
        rondaValidator = new RondaValidator(rondaService);
    }

    @Test
    void shouldSupportRondaClass() {
        assertTrue(rondaValidator.supports(Ronda.class));
        assertFalse(rondaValidator.supports(Object.class));
    }

    @Test
    void shouldRejectNumRondaNull() {
        Ronda ronda = new Ronda();
        ronda.setNumRonda(null);

        Errors errors = new BeanPropertyBindingResult(ronda, "ronda");
        rondaValidator.validate(ronda, errors);

        assertTrue(errors.hasFieldErrors("numRonda"));
        assertEquals("El número de ronda es obligatorio.", errors.getFieldError("numRonda").getDefaultMessage());
    }

    @Test
    void shouldRejectNumRondaGreaterThan10() {
        Ronda ronda = new Ronda();
        ronda.setNumRonda(11);

        Errors errors = new BeanPropertyBindingResult(ronda, "ronda");
        rondaValidator.validate(ronda, errors);

        assertTrue(errors.hasFieldErrors("numRonda"));
        assertEquals("El número de ronda no puede ser mayor que 10.", errors.getFieldError("numRonda").getDefaultMessage());
    }

    @Test
    void shouldRejectNumBazasNull() {
        Ronda ronda = new Ronda();
        ronda.setNumRonda(5);
        ronda.setNumBazas(null);

        Errors errors = new BeanPropertyBindingResult(ronda, "ronda");
        rondaValidator.validate(ronda, errors);

        assertTrue(errors.hasFieldErrors("numBazas"));
        assertEquals("El número de bazas es obligatorio.", errors.getFieldError("numBazas").getDefaultMessage());
    }

    @Test
    void shouldRejectNumBazasLessThanNumRonda() {
        Ronda ronda = new Ronda();
        ronda.setNumRonda(5);
        ronda.setNumBazas(4);

        Errors errors = new BeanPropertyBindingResult(ronda, "ronda");
        rondaValidator.validate(ronda, errors);

        assertTrue(errors.hasFieldErrors("numBazas"));
        assertEquals("Las bazas de una ronda serán igual o menor que el número de ronda.", 
            errors.getFieldError("numBazas").getDefaultMessage());
    }

    @Test
    void shouldAcceptValidRonda() {
        Ronda ronda = new Ronda();
        ronda.setNumRonda(5);
        ronda.setNumBazas(6);

        Errors errors = new BeanPropertyBindingResult(ronda, "ronda");
        rondaValidator.validate(ronda, errors);

        assertFalse(errors.hasErrors());
    }

}
