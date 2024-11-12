package es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(value = HttpStatus.CONFLICT)
@Getter
public class MinJugadoresPartidaException extends RuntimeException{

    public MinJugadoresPartidaException(String message){
        super(message);
    }
}
