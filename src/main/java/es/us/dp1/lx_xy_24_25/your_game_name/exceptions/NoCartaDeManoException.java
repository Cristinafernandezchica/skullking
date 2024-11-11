package es.us.dp1.lx_xy_24_25.your_game_name.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoCartaDeManoException extends RuntimeException{

    private static final long serialVersionUID = -3330551940727004798L;
	
	public NoCartaDeManoException() {
		super("No puedes echar una carta que no sea de tu mano");
	}
    
}
