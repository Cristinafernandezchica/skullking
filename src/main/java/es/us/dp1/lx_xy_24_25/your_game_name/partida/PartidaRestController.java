package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Obtiene todas las partidas", description = "Devuelve una lista de todas las partidas. Se puede filtrar por nombre y estado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partidas obtenidas correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontraron partidas")
    })
    @GetMapping
    public List<Partida> getAllPartidas(@ParameterObject() @RequestParam(value="nombre", required = false) String nombre, @ParameterObject @RequestParam(value="estado",required = false) PartidaEstado estado){
        return partidaService.getAllPartidas(nombre, estado);
    }

    @Operation(summary = "Obtiene una partida por ID", description = "Devuelve una partida dada su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partida obtenida correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró la partida")
    })
    @GetMapping("/{id}")
    public Partida getPartidaById(@PathVariable("id")Integer id){
        return partidaService.getPartidaById(id);
    }

    @Operation(summary = "Crea una nueva partida", description = "Crea una nueva partida y decuelve la partida creada.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Partida creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Error en los datos de la partida")
    })
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

    @Operation(summary = "Actualiza una partida existente", description = "Actualiza una partida existente dado su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partida actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró la partida"),
        @ApiResponse(responseCode = "400", description = "Error en los datos de la partida")
    })
    @PutMapping(value="/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Partida> updatePartida(@Valid @RequestBody Partida p, @PathVariable("id") Integer id){
        RestPreconditions.checkNotNull(partidaService.getPartidaById(id), "Partida", "ID", id);
        return new ResponseEntity<>(this.partidaService.update(p,id), HttpStatus.OK);
    }

    @Operation(summary = "Elimina una partida", description = "Elimina una partida dado su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Partida eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró la partida")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MessageResponse> deletePartida(@PathVariable("id")Integer id){
        RestPreconditions.checkNotNull(partidaService.getPartidaById(id), "Partida", "ID", id);
        partidaService.delete(id);
        return new ResponseEntity<>(new MessageResponse("Partida eliminada"), HttpStatus.NO_CONTENT); 
    }

    @Operation(summary = "Actualiza el propietario de una partida", description = "Actualiza el propietario de una partida dado su ID y el nuevo propietario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Owner actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró la partida"),
        @ApiResponse(responseCode = "400", description = "Error en los datos del nuevo propietario")
    })
    @PutMapping("/{id}/actualizar-owner")
    public ResponseEntity<MessageResponse> actualizarOwner(@PathVariable Integer id, @RequestBody Map<String, Integer> body) {
        try {
            Integer nuevoOwnerId = body.get("ownerPartida");
            partidaService.actualizarOwner(id, nuevoOwnerId);
            return new ResponseEntity<>(new MessageResponse("Owner actualizado con éxito."), HttpStatus.OK); 
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(new MessageResponse("Partida no encontrada."), HttpStatus.NOT_FOUND);             
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST); 
        }
    }

    @Operation(summary = "Obtiene los jugadores de una partida", description = "Devuelve una lista de jugadores èrtenecientes a una partida dada su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jugadores obtenidos correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró la partida")
    })
    @GetMapping("/{id}/jugadores")
    public ResponseEntity<List<Jugador>> getJugadoresByPartidaId(@PathVariable("id")Integer id){
        List<Jugador> jugadoresPartida = jugadorService.findJugadoresByPartidaId(id);
        return ResponseEntity.ok(jugadoresPartida);
    }


    @Operation(summary = "Obtiene las partidas de un propietario", description = "Devuelve una lista de partidas dado el Id de un propietario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partidas obtenidas correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró el propietario")
    })
    @GetMapping(params = "ownerId")
    public ResponseEntity<List<Partida>> findPartidasByOwnerId(@RequestParam("ownerId") Integer ownerId) {
        List<Partida> partidas = partidaService.findPartidasByOwnerId(ownerId);
        if (partidas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(partidas, HttpStatus.OK);
    }

    @Operation(summary = "Obtiene el jugador ganador de la partida", description = "Devuelve el ganador de la partida especificada por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jugador ganador obtenido correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró la partida o el jugador ganador")
    })
    @GetMapping("/{id}/jugadorGanador")
    public ResponseEntity<Jugador> ganadorPartida (@PathVariable("id") Integer id){
        partidaService.getJugadorGanador(id);
        return new ResponseEntity<>(partidaService.getJugadorGanador(id), HttpStatus.OK);
    }

    @Operation(summary = "Iniciar una partida", description = "Inicia la partida especificada por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partida iniciada correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró la partida")
    })
    @PutMapping("/{id}/iniciar-partida")
    public ResponseEntity<Void> iniciarPartida(@PathVariable("id") Integer id){
        partidaService.iniciarPartida(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cambiar el estado de una partida", description = "Cambiar el estado de la partida especificando ID de la partida y de la baza actual.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado cambiado con éxito"),
        @ApiResponse(responseCode = "404", description = "No se encontró la partida")
    })
    @PostMapping("/{partidaId}/bazas/{bazaId}/siguiente-estado")
    public ResponseEntity<Void> siguienteEstado(@PathVariable("partidaId") Integer partidaId, @PathVariable("bazaId") Integer bazaId){
        partidaService.siguienteEstado(partidaId, bazaId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Realizar una apuesta", description = "Permite a un jugador realizar una apuesta.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Apuesta realizada con éxito"),
        @ApiResponse(responseCode = "400", description = "Apuesta no puede ser nula"),
        @ApiResponse(responseCode = "404", description = "No se encontró el jugador o la partida")
    })
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
