package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import java.util.ArrayList;
import java.util.List;

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

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoService;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/manos")
@SecurityRequirement(name = "bearerAuth")
public class ManoRestController {

    private final ManoService manoService;
    private final TrucoService trucoService;

    @Autowired
    public ManoRestController(ManoService manoService, TrucoService trucoService) {
        this.manoService = manoService;
        this.trucoService = trucoService;
    }


    @Operation(summary = "Obtener todas las manos", description = "Devuelve una lista de todas las manos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de manos obtenida con éxito"),
        @ApiResponse(responseCode = "404", description = "No se encontraron manos")
    })
    @GetMapping
    public ResponseEntity<List<Mano>> findAll() {
        List<Mano> res;
        res = (List<Mano>) manoService.findAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar una mano", description = "Actualiza una mano existente dado su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mano actualizada con éxito"),
        @ApiResponse(responseCode = "404", description = "No se encontró la mano"),
        @ApiResponse(responseCode = "400", description = "Los datos de la mano son inválidos")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Mano> update(@PathVariable Integer id, @RequestBody @Valid Mano Mano) {
        RestPreconditions.checkNotNull(manoService.findManoById(id), "Mano", "ID", id);
        return new ResponseEntity<>(this.manoService.updateMano(Mano, id), HttpStatus.OK);
    }

    @Operation(summary = "Crear una nueva mano", description = "Crear una nueva mano.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Mano creada con éxito"),
        @ApiResponse(responseCode = "400", description = "Los datos de la mano son inválidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Mano> create(@RequestBody @Valid Mano Mano) {
        Mano savedMano = manoService.saveMano(Mano);
        return new ResponseEntity<>(savedMano, HttpStatus.CREATED);
    }

    @Operation(summary = "Eliminar una mano", description = "Elimina una mano existente por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mano eliminada con éxito"),
        @ApiResponse(responseCode = "404", description = "No se encontró la mano")
    })
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> delete(@PathVariable("id") int id) {
        RestPreconditions.checkNotNull(manoService.findManoById(id), "Mano", "ID", id);
        manoService.deleteMano(id);
        return new ResponseEntity<>(new MessageResponse("Mano eliminada!"), HttpStatus.OK);
    }

    @Operation(summary = "Obtener la última mano de un jugador", description = "Devuelve la ñultima mano de un jugador por el ID del jugador.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Última mano obtenida con éxito"),
        @ApiResponse(responseCode = "404", description = "No se encontró la última mano")
    })
    @GetMapping("/{jugadorId}")
    public ResponseEntity<Mano> findLastManoByJugadorId(@PathVariable("jugadorId") Integer jugadorId) {
        Mano res = manoService.findLastManoByJugadorId(jugadorId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Obtener las cartas deshabilitadas de una mano", description = "Devuelve las cartas deshabilitadas de una mano por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cartas deshabilitadas obtenidas con éxito"),
        @ApiResponse(responseCode = "404", description = "Mano no encontrada")
    })
    @GetMapping(value = "/{manoId}/manoDisabled")
    public ResponseEntity<List<Carta>> cartasDisabled(@PathVariable("manoId") int id,
            @RequestParam TipoCarta tipoCarta) {
        List<Carta> res = manoService.cartasDisabled(id, tipoCarta);
        return new ResponseEntity<>(res != null ? res : new ArrayList<>(), HttpStatus.OK);
    }

    @Operation(summary = "Obtener las manos de un jugador por ronda", description = "Devuelve las manos de un jugador por el ID de la ronda.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Manos de la ronda obtenidas con éxito"),
        @ApiResponse(responseCode = "404", description = "Ronda no encontrada")
    })
    @GetMapping(value = "/rondas/{rondaId}")
    public ResponseEntity<List<Mano>> findManoByRondaId(@PathVariable("rondaId") Integer rondaId) {
        List<Mano> res = manoService.findAllManosByRondaId(rondaId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
