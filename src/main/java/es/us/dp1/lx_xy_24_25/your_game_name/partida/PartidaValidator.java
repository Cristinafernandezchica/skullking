package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PartidaValidator implements Validator{

    private PartidaService partidaService;

    public PartidaValidator(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Partida partida = (Partida) obj;
        Integer ownerId = partida.getOwnerPartida();

        if(ownerId != null) {
            Boolean partidaEsperandoJugando = partidaService.usuarioPartidaEnJuegoEsperando(ownerId);
            if(partidaEsperandoJugando){
                errors.rejectValue("ownerPartida", "field.required", "El usuario ya tiene una partida en espera o en juego.");
            }
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Partida.class.equals(clazz);
    }
}
