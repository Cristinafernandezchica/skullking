package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Component
public class BazaValidator implements Validator{

    private BazaService bazaService;

    public BazaValidator(BazaService bazaService) {
        this.bazaService = bazaService;
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Baza baza = (Baza) obj;
        
        // Validar la Ronda y el número máximo de bazas permitido
        Ronda ronda = baza.getRonda();
        if (ronda != null) {
            if (baza.getNumBaza() != null && baza.getNumBaza() > ronda.getNumBazas()) {
                errors.rejectValue("numBaza", "numBaza.exceeds", "El número de baza excede el máximo permitido para la ronda.");
            }
        }

        // Validar que no haya bazas duplicadas en la misma ronda con el mismo numBaza
        if (ronda != null && baza.getNumBaza() != null) {
            Baza existingBaza = bazaService.findByRondaIdAndNumBaza(ronda.getId(), baza.getNumBaza());
            if (existingBaza.getId().equals(baza.getId())) {
                errors.rejectValue("numBaza", "numBaza.duplicate", "Ya existe una baza con el mismo número en esta ronda.");
            }
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Baza.class.isAssignableFrom(clazz);
    }
    
}
