package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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
import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoService;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoService;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/partidas")
@Tag(name = "Partidas", description = "API par el manejo de las Partidas")
@SecurityRequirement(name = "bearerAuth")
public class PartidaRestController {

    PartidaService ps;
    JugadorService js;
    private ManoService ms;
    private BazaService bs;
    // RondaService rs;

    @Autowired
    public PartidaRestController(PartidaService ps, JugadorService js) {
        this.ps = ps;
        this.js = js;
        // this.rs = rs;
    }

    // @RequestParam es para filtrar por esos valores, por tanto no hacen falta los métodos PartidasByName y PartidasByEstado
    @GetMapping
    public List<Partida> getAllPartidas(@ParameterObject() @RequestParam(value="nombre", required = false) String nombre, @ParameterObject @RequestParam(value="estado",required = false) PartidaEstado estado){
        return ps.getAllPartidas(nombre, estado);
    }

    @GetMapping("/{id}")
    public Partida getPartidaById(@PathVariable("id")Integer id){
        Partida p = ps.getPartidaById(id);
        return p;
    }

    
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Partida> createPartida(@Valid @RequestBody Partida p){
        p=ps.save(p);
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
        RestPreconditions.checkNotNull(ps.getPartidaById(id), "Partida", "ID", id);
        return new ResponseEntity<>(this.ps.update(p,id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MessageResponse> deletePartida(@PathVariable("id")Integer id){
        RestPreconditions.checkNotNull(ps.getPartidaById(id), "Partida", "ID", id);
        ps.delete(id);
        return new ResponseEntity<>(new MessageResponse("Partida eliminada"), HttpStatus.NO_CONTENT); 
    }

    // Relación de uno a muchos con la clase Jugador, mirar los nombres de los métodos
    // TENER EN CUENTA  -->  Habrá que hacer un DTO seguramente
    // Te devulve el jugador con la contraseña incluida, para frontend solo queremos el username
    @GetMapping("/{id}/jugadores")
    public ResponseEntity<List<Jugador>> getJugadoresByPartidaId(@PathVariable("id")Integer id){
        List<Jugador> jugadoresPartida = js.findJugadoresByPartidaId(id);
        return ResponseEntity.ok(jugadoresPartida);
    }

    // Relación de uno a 10 con la clase Ronda, mirar los nombres de los métodos
    /* 
    @GetMapping("/{id}/rondas")
    public List<Ronda> getRondasByPartidaId(@PathVariable("id")Integer id){
        return rs.getRondasByPartidaId(id);
    }
    */

    // Para iniciar una partida desde frontend
    @PutMapping("/{id}/iniciar-partida")
    public ResponseEntity<Void> iniciarPartida(@PathVariable("id") Integer id){
        ps.iniciarPartida(id);
        return ResponseEntity.ok().build();
    }
    

}
