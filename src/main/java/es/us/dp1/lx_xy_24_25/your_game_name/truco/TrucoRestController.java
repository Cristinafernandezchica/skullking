package es.us.dp1.lx_xy_24_25.your_game_name.truco;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.bazaCartaManoDTO.BazaCartaManoDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trucos")
@Tag(name = "Trucos", description = "API for the  management of Trucos")
public class TrucoRestController {

    private TrucoService trucoService;

    @Autowired
	public TrucoRestController(TrucoService trucoService) {
		this.trucoService = trucoService;
	}

	@Operation(summary = "Obtener todos los trucos", description = "Devuelve una lista de todos los trucos.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Lista de trucos obtenida")
	})
    @GetMapping
	public ResponseEntity<List<Truco>> findAllTrucos() {
		List<Truco> res = (List<Truco>) this.trucoService.findAllTrucos();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@Operation(summary = "Obtener un truco", description = "Devuleve un truco específico por su ID.")
	@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Truco obtenido con éxito"),
        @ApiResponse(responseCode = "404", description = "Truco no encontrado")
    })
	@GetMapping(value = "{trucoId}")
	public ResponseEntity<Truco> findTrucoById(@PathVariable("trucoId") int id) {
		return new ResponseEntity<>(trucoService.findTrucoById(id), HttpStatus.OK);
	}

	@Operation(summary = "Crear un truco", description = "Crea un nuevo truco.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Truco creado con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos del truco no válidos")
    })
    @PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Truco> createTruco(@RequestBody @Valid Truco truco) throws URISyntaxException {
		Truco newTruco = new Truco();
		BeanUtils.copyProperties(truco, newTruco, "id");
		Truco savedTruco = this.trucoService.saveTruco(newTruco);
		return new ResponseEntity<>(savedTruco, HttpStatus.CREATED);
	}

	@Operation(summary = "Actualizar un truco", description = "Actualiza un truco existente dado su ID.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Truco actualizado con éxito"),
		@ApiResponse(responseCode = "404", description = "Truco no encontrado")
	})
    @PutMapping(value = "{trucoId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Truco> updateTruco(@PathVariable("trucoId") int trucoId, @RequestBody @Valid Truco truco) {
		RestPreconditions.checkNotNull(trucoService.findTrucoById(trucoId), "Truco", "ID", trucoId);
		return new ResponseEntity<>(this.trucoService.updateTruco(truco, trucoId), HttpStatus.OK);
	}

	@Operation(summary = "Eliminar un truco", description = "Elimina un truco existente dado su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Truco eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "Truco no encontrado")
    })
    @DeleteMapping(value = "{trucoId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> deleteTruco(@PathVariable("trucoId") int id) {
		RestPreconditions.checkNotNull(trucoService.findTrucoById(id), "Truco", "ID", id);
		trucoService.deleteTruco(id);
		return new ResponseEntity<>(new MessageResponse("Truco eliminado!"), HttpStatus.OK);
	}

	@Operation(summary = "Obtener trucos por baza", description = "Devuelve una lista de trucos que pertenecen a una baza específica dado el ID de la baza.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de trucos obtenida"),
		@ApiResponse(responseCode = "404", description = "Baza no encontrada")
    })
	@GetMapping(value = "/trucosBaza/{bazaId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<Truco>> findTrucosByBazaId(@PathVariable("bazaId") int bazaId) {
		List<Truco> res = (List<Truco>) this.trucoService.findTrucosByBazaId(bazaId);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@Operation(summary = "Jugar un truco", description = "Permite a un jugador jugar un truco dado el ID del jugador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Truco jugado con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos del truco no válidos")
    })
	@PostMapping(value= "{jugadorId}/jugar")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Truco> jugarTruco(@PathVariable("jugadorId") int jugadorId,@RequestBody @Valid BazaCartaManoDTO DTO) throws URISyntaxException {
		Truco savedTruco = this.trucoService.jugarTruco(DTO, jugadorId);
		return new ResponseEntity<>(savedTruco, HttpStatus.CREATED);
	}

}
