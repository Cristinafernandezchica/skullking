package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import java.net.URI;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/rondas")
@Tag(name = "Rondas", description =  "API for the management of Rondas")

public class RondaRestController {
    
    RondaService rondaService;

    @Autowired
    public RondaRestController(RondaService rondaService) {
        this.rondaService = rondaService;
    }

    @Operation(summary = "Obtener todas las rondas", description = "Devuleve una lista de todas las rondas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de rondas obtenida")
    })
    @GetMapping
    public List<Ronda> getAllRondas(){
        return rondaService.getAllRondas();
    }

    @Operation(summary = "Obtener una ronda por ID", description = "Devuelve una ronda según el ID proporcionado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ronda obtenida"),
        @ApiResponse(responseCode = "404", description = "Ronda no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Ronda> getRondaById(@PathVariable("id") Integer id) {
        Ronda ronda = rondaService.getRondaById(id);
        return new ResponseEntity<Ronda>(ronda, HttpStatus.OK);
    }

    @Operation(summary = "Crear una nueva ronda", description = "Crea una nueva ronda con los datos proporcionados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ronda creada con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos de la ronda no válidos")
    })
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Ronda> createRonda(@Valid @RequestBody Ronda r) {
        r = rondaService.save(r);
        URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(r.getId())
                .toUri();
        return ResponseEntity.created(location).body(r);
    }

    @Operation(summary = "Actualizar una ronda", description = "Actualiza una ronda existente por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ronda actualizada con éxito"),
        @ApiResponse(responseCode = "404", description = "Ronda no encontrada")
    })
    @PutMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MessageResponse>  updateRonda(@Valid @RequestBody Ronda r, @PathVariable("id") Integer id) {
        RestPreconditions.checkNotNull(rondaService.getRondaById(id), "Ronda", "ID", id);
        Ronda rToUpdate = rondaService.getRondaById(id);
        BeanUtils.copyProperties(r, rToUpdate, "id");
        rondaService.save(rToUpdate);
        return new ResponseEntity<>(new MessageResponse("Ronda actualizada"), HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Eliminar una ronda", description = "Elimina una ronda existente por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ronda eliminada con éxito"),
        @ApiResponse(responseCode = "404", description = "Ronda no encontrada")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MessageResponse> deleteRonda(@PathVariable("id") Integer id) {
        RestPreconditions.checkNotNull(rondaService.getRondaById(id), "Ronda", "ID", id);
        rondaService.delete(id);
        return new ResponseEntity<>(new MessageResponse("Ronda eliminada"), HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Obtener la ronda actual por la partida", description = "Devuelve la ronda actual según la partida dado su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ronda obtenida"),
        @ApiResponse(responseCode = "404", description = "Ronda no encontrada")
    })
    @GetMapping("/{partidaId}/partida")
    public Ronda getRondaByPartidaId(@PathVariable("partidaId") Integer partidaId) {
        return rondaService.rondaActual(partidaId);
    }

}
