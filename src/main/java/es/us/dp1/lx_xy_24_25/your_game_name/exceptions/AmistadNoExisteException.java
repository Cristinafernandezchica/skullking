package es.us.dp1.lx_xy_24_25.your_game_name.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AmistadNoExisteException extends RuntimeException{

    private static final long serialVersionUID = -3330551940727004798L;
	
	public AmistadNoExisteException() {
		super("la amistad no ha sido encontrada");
	}
    
}
