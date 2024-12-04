package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/v1/jugadores")
@SecurityRequirement(name = "bearerAuth")
public class JugadorRestController {
    private final JugadorService jugadorService;


    @Autowired
    public JugadorRestController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    // Para Validator
    /*
    @InitBinder
    public void initJugadorBinder(WebDataBinder dataBinder){
        dataBinder.setValidator(new JugadorValidator(jugadorService));
    }
    */

    //get jugador por id
    @GetMapping(value = "/{partidaId}" )
    public ResponseEntity<List<Jugador>> findJugadoresByPartidaid(@PathVariable("partidaId") Integer partidaId) {
        List<Jugador> res;
        res = jugadorService.findJugadoresByPartidaId(partidaId);
        if(res == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    
    //get jugador por usuarioId
    @GetMapping(value = "/{usuarioId}/usuario" )
    public ResponseEntity<Jugador> findJugadorByUsuarioId(@PathVariable("usuarioId") Integer usuarioId) {
        Jugador res;
        res = jugadorService.findJugadorByUsuarioId(usuarioId);
        if(res == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    
    // get turno por jugadorId
    /*
    @GetMapping(value = "/{jugadorId}/turno")
    public ResponseEntity<Integer> findTurnoByJugadorId(@PathVariable("jugadorId") Integer jugadorId) {
        Integer res;
        res = jugadorService.findTurnoByJugadorId(jugadorId);
        if(res == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    */

    //get todos los jugadores
    @GetMapping
    public ResponseEntity<List<Jugador>> findAll() {
        List<Jugador> res;
        res = (List<Jugador>) jugadorService.findAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    
    //update jugador
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Jugador> putMethodName(@PathVariable("id") Integer id, @RequestBody @Valid Jugador jugador) {
        RestPreconditions.checkNotNull(jugadorService.findById(id), "Jugador", "ID", id);
		return new ResponseEntity<>(this.jugadorService.updateJugador(jugador, id), HttpStatus.OK);
    }

    // crear un nuevo jugador.
    @PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Jugador> create(@RequestBody @Valid Jugador jugador) {
		Jugador savedJugador = jugadorService.saveJugador(jugador);
		return new ResponseEntity<>(savedJugador, HttpStatus.CREATED);
	}

    /*
    @PostMapping
	@ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> create(@RequestBody @Valid Jugador jugador, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            List<String> errorMessages = bindingResult.getAllErrors().stream()
            .map(error -> ((FieldError) error).getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }
        Jugador savedJugador = jugadorService.saveJugador(jugador);
        return new ResponseEntity<>(savedJugador, HttpStatus.CREATED);
        
    }
    */

    // borrar un jugador por id
    @DeleteMapping(value = "{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("id") int id) {
		RestPreconditions.checkNotNull(jugadorService.findById(id), "Jugador", "ID", id);
		jugadorService.deleteJugador(id);
		return new ResponseEntity<>(new MessageResponse("Jugador deleted!"), HttpStatus.OK);
	}
    
    @GetMapping(value="/{usuarioId}/partida")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Partida> findPartidaByUsuarioId(@PathVariable("usuarioId") int usuarioId){
        RestPreconditions.checkNotNull(jugadorService.findPartidaByUsuarioId(usuarioId), "Jugador", "usuarioId", usuarioId);
        return new ResponseEntity<>(this.jugadorService.findPartidaByUsuarioId(usuarioId), HttpStatus.OK);
    }

    @GetMapping(value="/{usuarioId}/partida")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Partida> findPartidaByUsuarioId(@PathVariable("usuarioId") int usuarioId){
        RestPreconditions.checkNotNull(jugadorService.findPartidaByUsuarioId(usuarioId), "Jugador", "usuarioId", usuarioId);
        return new ResponseEntity<>(this.jugadorService.findPartidaByUsuarioId(usuarioId), HttpStatus.OK);
    }
}
