package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class RondaValidator implements Validator{

    private RondaService rondaService;

    public RondaValidator(RondaService rondaService) {
        this.rondaService = rondaService;
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Ronda ronda = (Ronda) obj;

        if (ronda.getNumRonda() == null) { 
            errors.rejectValue("numRonda", "field.required", "El número de ronda es obligatorio."); 
        } 
        if(ronda.getNumRonda()<=10){
            errors.rejectValue("numRonda", "field.invalid", "El número de ronda no puede ser mayor que 10");
        }

        if (ronda.getNumBazas() == null) { 
            errors.rejectValue("numBazas", "field.required", "El número de bazas es obligatorio."); 
        }

        if(ronda.getNumBazas()<=ronda.getNumRonda()){
            errors.rejectValue("numBazas", "field.invalid", "Las bazas de una ronda será igual o menor que el número de ronda"); 
        }

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Ronda.class.equals(clazz);
    }

}
