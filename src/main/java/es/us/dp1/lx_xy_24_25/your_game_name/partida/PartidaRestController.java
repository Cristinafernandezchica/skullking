package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import java.net.URI;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/partidas")
@Tag(name = "Partidas", description = "API par el manejo de las Partidas")
@SecurityRequirement(name = "bearerAuth")
public class PartidaRestController {

    PartidaService partidaService;
    JugadorService jugadorService;

    @Autowired
    public PartidaRestController(PartidaService partidaService, JugadorService jugadorService) {
        this.partidaService = partidaService;
        this.jugadorService = jugadorService;
    }

    // @RequestParam es para filtrar por esos valores, por tanto no hacen falta los métodos PartidasByName y PartidasByEstado
    @GetMapping
    public List<Partida> getAllPartidas(@ParameterObject() @RequestParam(value="nombre", required = false) String nombre, @ParameterObject @RequestParam(value="estado",required = false) PartidaEstado estado){
        return partidaService.getAllPartidas(nombre, estado);
    }

    @GetMapping("/{id}")
    public Partida getPartidaById(@PathVariable("id")Integer id){
        Partida p = partidaService.getPartidaById(id);
        return p;
    }

    
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Partida> createPartida(@Valid @RequestBody Partida p){
        p=partidaService.save(p);
        URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(p.getId())
                .toUri();
        return ResponseEntity.created(location).body(p);
    }

    @PutMapping(value="/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Partida> updatePartida(@Valid @RequestBody Partida p, @PathVariable("id") Integer id){
        RestPreconditions.checkNotNull(partidaService.getPartidaById(id), "Partida", "ID", id);
        return new ResponseEntity<>(this.partidaService.update(p,id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MessageResponse> deletePartida(@PathVariable("id")Integer id){
        RestPreconditions.checkNotNull(partidaService.getPartidaById(id), "Partida", "ID", id);
        partidaService.delete(id);
        return new ResponseEntity<>(new MessageResponse("Partida eliminada"), HttpStatus.NO_CONTENT); 
    }

    // Relación de uno a muchos con la clase Jugador, mirar los nombres de los métodos
    // TENER EN CUENTA  -->  Habrá que hacer un DTO seguramente
    // Te devulve el jugador con la contraseña incluida, para frontend solo queremos el username
    @GetMapping("/{id}/jugadores")
    public ResponseEntity<List<Jugador>> getJugadoresByPartidaId(@PathVariable("id")Integer id){
        List<Jugador> jugadoresPartida = jugadorService.findJugadoresByPartidaId(id);
        return ResponseEntity.ok(jugadoresPartida);
    }

    // Relación de uno a 10 con la clase Ronda, mirar los nombres de los métodos
    /* 
    @GetMapping("/{id}/rondas")
    public List<Ronda> getRondasByPartidaId(@PathVariable("id")Integer id){
        return rondaService.getRondasByPartidaId(id);
    }
    */

    // Para iniciar una partida desde frontend
    @PutMapping("/{id}/iniciar-partida")
    public ResponseEntity<Void> iniciarPartida(@PathVariable("id") Integer id){
        partidaService.iniciarPartida(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/jugadorGanador")
    public ResponseEntity<Jugador> ganadorPartida (@PathVariable("id") Integer id){
        partidaService.getJugadorGanador(id);
        return new ResponseEntity<>(partidaService.getJugadorGanador(id), HttpStatus.OK);
    }

    // Para iniciar una partida desde frontend
    @PutMapping("/{id}/iniciar-partida-prueba")
    public ResponseEntity<Void> iniciarPartidaPrueba(@PathVariable("id") Integer id){
        partidaService.iniciarPartidaPrueba(id);
        return ResponseEntity.ok().build();
    }

    // Para cambiar el estado de una partida desde frontend
    @PostMapping("/{partidaId}/bazas/{bazaId}/siguiente-estado")
    public ResponseEntity<Void> siguienteEstado(@PathVariable("partidaId") Integer partidaId, @PathVariable("bazaId") Integer bazaId){
        partidaService.siguienteEstado(partidaId, bazaId);
        return ResponseEntity.ok().build();
    }

    // Para apostar
    @PutMapping("/apuesta/{jugadorId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> apuesta(@RequestParam Integer apuesta, @PathVariable Integer jugadorId) {
        if (apuesta == null) {
            throw new IllegalArgumentException("Apuesta no puede ser nula");
        }
        partidaService.apuesta(apuesta, jugadorId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    

}
