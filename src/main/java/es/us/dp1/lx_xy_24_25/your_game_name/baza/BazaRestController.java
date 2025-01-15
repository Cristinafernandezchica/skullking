package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoService;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bazas")
@Tag(name = "Bazas", description = "API para el manejo de Bazas")
@SecurityRequirement(name = "bearerAuth")

public class BazaRestController {
    
    private final BazaService bazaService;
    private TrucoService trucoService;

    @Autowired
    public BazaRestController(BazaService bazaService, TrucoService trucoService){
        this.bazaService=bazaService;
        this.trucoService=trucoService;
    }

    @Operation(summary = "Obtener todas las bazas", description = "Devuelve una lista de todas las bazas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de bazas obtenida")
    })
    @GetMapping
    public ResponseEntity<List<Baza>> getAllBazas(){
        List<Baza> listaAux= bazaService.getAllBazas();
        return new ResponseEntity<>(listaAux, HttpStatus.OK);
    }

    @Operation(summary = "Obtener una baza por ID", description = "Devuelve una baza específica por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Baza obtenida"),
        @ApiResponse(responseCode = "404", description = "Baza no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Baza> getBazaById(@PathVariable("id")Integer id){
        Baza bazaById = bazaService.findById(id);
        return new ResponseEntity<>(bazaById, HttpStatus.OK);
    }

    @Operation(summary = "Crear una nueva baza", description = "Crea una nueva baza.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Baza creada con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos de la baza no válidos")
    })
    @PostMapping()
    public ResponseEntity<Baza> createBaza(@Valid @RequestBody Baza b){
        Baza bAux =bazaService.saveBaza(b);
        return new ResponseEntity<>(bAux, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar una baza existente", description = "Actualizar una baza existente dado su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Baza actualizada con éxito"),
        @ApiResponse(responseCode = "404", description = "Baza no encontrada")
    })
    @PutMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Baza> updateBaza(@Valid @RequestBody Baza b,@PathVariable("id")Integer id){
        RestPreconditions.checkNotNull(bazaService.findById(id), "Baza", "id", id);
		return new ResponseEntity<>(this.bazaService.updateBaza(b,id), HttpStatus.NO_CONTENT);

    }

    @Operation(summary = "Eliminar una baza existente", description = "Elimina una baza dado su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Baza eliminada con éxito"),
        @ApiResponse(responseCode = "404", description = "Baza no encontrada")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<MessageResponse> deleteBaza(@PathVariable("id") int id) {
		RestPreconditions.checkNotNull(bazaService.findById(id), "Baza", "ID", id);
		bazaService.deleteBaza(id);
		return new ResponseEntity<>(new MessageResponse("Baza eliminada"), HttpStatus.NO_CONTENT);
	}

    @Operation(summary = "Obtener el ganador de una baza", description = "Devuelve el ganador de una baza específica por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ganador obtenido"),
        @ApiResponse(responseCode = "404", description = "Baza no encontrada")
    })
    @GetMapping("/{id}/ganador")
    public Jugador findBazaByIdGanador(@PathVariable(value = "id") int id) {
        Baza bazaById = bazaService.findById(id);
        return bazaById.getGanador();
    }

    @Operation(summary = "Obtener los trucos de una baza concreta", description = "Devuelve la lista de trucos de una baza específica por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de trucos obtenida"),
        @ApiResponse(responseCode = "404", description = "Baza no encontrada")
    })
    @GetMapping(value = "{bazaId}/trucos")
	public ResponseEntity<List<Truco>> findTrucosByBazaId(@PathVariable("bazaId") int id) {
		return new ResponseEntity<>(trucoService.findTrucosByBazaId(id), HttpStatus.OK);
	}


    
    @Operation(summary = "Obtener la última baza de una ronda concreta", description = "Devuelve la última baza de una ronda específica por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Última baza obtenida"),
        @ApiResponse(responseCode = "404", description = "Ronda no encontrada")
    })
    @GetMapping(value = "{rondaId}/bazaActual")
    public ResponseEntity<Baza> findBazaActualByRondaId(@PathVariable("rondaId") Integer rondaId) {
        return new ResponseEntity<>(bazaService.findBazaActualByRondaId(rondaId), HttpStatus.OK);
    }
    
}