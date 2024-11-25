/*
package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

public class JugadorValidator implements Validator {

    private JugadorService jugadorService;

    public JugadorValidator(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }


    @Override
    public void validate(Object obj, Errors errors) {
        Jugador jugador = (Jugador) obj;
        User usuario = jugador.getUsuario();
        Partida partida = jugador.getPartida();

        if (usuario != null && partida != null) {
            boolean tieneMultiplesJugadores = jugadorService.usuarioMultiplesJugadoresEnPartida(usuario, partida);
            if (tieneMultiplesJugadores) {
                errors.rejectValue("usuario", "field.duplicate", "El usuario no puede tener múltiples jugadores en la misma partida.");
            }
        }
    }

    
    @Override
    public boolean supports(Class<?> clazz) {
        return Jugador.class.equals(clazz);
    }
}
*/

