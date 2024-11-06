package es.us.dp1.lx_xy_24_25.your_game_name.mano;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/manos")
@SecurityRequirement(name = "bearerAuth")
public class ManoRestController {
    
     private final ManoService manoService;


    @Autowired
    public ManoRestController(ManoService manoService) {
        this.manoService = manoService;
    }
    //get todos los Manoes
    @GetMapping
    public ResponseEntity<List<Mano>> findAll() {
        List<Mano> res;
        res = (List<Mano>) manoService.findAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    
    //update Mano
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Mano> putMethodName(@PathVariable Integer id, @RequestBody @Valid Mano Mano) {
        RestPreconditions.checkNotNull(manoService.findManoById(id), "Mano", "ID", id);
		return new ResponseEntity<>(this.manoService.updateMano(Mano, id), HttpStatus.OK);
    }

    // crear un nuevo Mano
    @PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Mano> create(@RequestBody @Valid Mano Mano) {
		Mano savedMano = manoService.saveMano(Mano);
		return new ResponseEntity<>(savedMano, HttpStatus.CREATED);
	}
    // borrar un Mano por id
    @DeleteMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("id") int id) {
		RestPreconditions.checkNotNull(manoService.findManoById(id), "Mano", "ID", id);
		manoService.deleteMano(id);
		return new ResponseEntity<>(new MessageResponse("Mano deleted!"), HttpStatus.OK);
	}

    @GetMapping("/{jugadorId}")
    public ResponseEntity<Mano> findManoByJugadorId(@PathVariable("jugadorId") Integer jugadorId) {
        Mano res;
        res = manoService.findLastManoByJugadorId(jugadorId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // provisional para apostar, se necesita en controller para que la pueda llamar 
    // el frontend ya que es algo que eligen los usuarios
    // PROVISIONAL -->  NO ES CORRECTA
    /*
    @PostMapping("/apostar/{manoId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Mano> apostar(@PathVariable Integer manoId) {
        Mano mano = manoService.findManoById(manoId);
        RestPreconditions.checkNotNull(mano, "Mano", "ID", manoId);
        return new ResponseEntity<>(manoService.apostar(mano.getId()), HttpStatus.OK);
    }
    */

}
