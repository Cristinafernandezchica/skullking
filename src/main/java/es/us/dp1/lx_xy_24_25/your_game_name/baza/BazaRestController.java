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
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;



import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bazas")
@Tag(name = "Bazas", description = "API para el manejo de Bazas")
@SecurityRequirement(name = "bearerAuth")

public class BazaRestController {
    
    private final BazaService bs;
    private TrucoService trucoService;

    @Autowired
    public BazaRestController(BazaService bs){
        this.bs=bs;
    }

    // Get todas las bazas
    @GetMapping
    public ResponseEntity<List<Baza>> getAllBazas(){
        List<Baza> listaAux= bs.getAllBazas();
        return new ResponseEntity<>(listaAux, HttpStatus.OK);
    }

    // Get de una baza en función de su id
    @GetMapping("/{id}")
    public ResponseEntity<Baza> getBazaById(@PathVariable("id")Integer id){
        Baza bazaById = bs.findById(id);
        return new ResponseEntity<>(bazaById, HttpStatus.OK);
    }

    // Create una nueva baza
    @PostMapping()
    public ResponseEntity<Baza> createBaza(@Valid @RequestBody Baza b){
        Baza bAux =bs.saveBaza(b);
        return new ResponseEntity<>(bAux, HttpStatus.CREATED);
    }

    // Update una baza existente
    @PutMapping(value="/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Baza> updateBaza(@Valid @RequestBody Baza b,@PathVariable("id")Integer id){
        RestPreconditions.checkNotNull(bs.findById(id), "Baza", "id", id);
		return new ResponseEntity<>(this.bs.updateBaza(b,id), HttpStatus.OK);

    }

    // Delete una baza existente
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> deleteBaza(@PathVariable("id") int id) {
		RestPreconditions.checkNotNull(bs.findById(id), "Baza", "ID", id);
		bs.deleteBaza(id);
		return new ResponseEntity<>(new MessageResponse("Baza deleted!"), HttpStatus.OK);
	}


    @GetMapping("/{id}/ganador")
    public Jugador findBazaByIdGanador(@PathVariable(value = "id") int id) {
        Baza bazaById = bs.findById(id);
        return bazaById.getGanador();
    }

    // PETICIÓN PARA OBTENER LOS TRUCOS DE UNA BAZA CONCRETA
    @GetMapping(value = "{bazaId}/trucos")
	public ResponseEntity<List<Truco>> findTrucosByBazaId(@PathVariable("bazaId") int id) {
		return new ResponseEntity<>(trucoService.findTrucosByBazaId(id), HttpStatus.OK);
	}

    // PETICIÓN PARA OBTENER LAS CARTAS DE UNA BAZA POR JUGADOR ORDENADAS POR TURNO
    @GetMapping(value = "{bazaId}/cartasJugadores")
	public ResponseEntity<Map<Integer, Integer>> findCartasPorJugadorByBazaId(@PathVariable("bazaId") int id) {
		return new ResponseEntity<>(trucoService.getCartaByJugador(id), HttpStatus.OK);
	}

}