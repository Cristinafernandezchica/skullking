package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/jugadores")
@SecurityRequirement(name = "bearerAuth")
public class JugadorRestController {
    private final JugadorService jugadorService;
	private final AuthoritiesService authService;

    @Autowired
    public JugadorRestController(JugadorService jugadorService, AuthoritiesService authService) {
        this.jugadorService = jugadorService;
        this.authService = authService;
    }
    @GetMapping(value = "{id}" )
    public ResponseEntity<List<Jugador>> findJugadorByPartidaId(@PathVariable("id") Integer partidaId) {
        List<Jugador> res;
        res = (List<Jugador>) jugadorService.findJugadorByPartidaId(partidaId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    
    @GetMapping
    public ResponseEntity<List<Jugador>> listarJugadores() {
        List<Jugador> res;
        res = (List<Jugador>) jugadorService.listarJugadores();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    

}
