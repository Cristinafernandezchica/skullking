package es.us.dp1.lx_xy_24_25.your_game_name.carta;

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
@RequestMapping("/api/v1/cartas")
@SecurityRequirement(name = "bearerAuth")
public class CartaRestController {

    private final CartaService CartaService;


    @Autowired
    public CartaRestController(CartaService CartaService, AuthoritiesService authService) {
        this.CartaService = CartaService;
    }
    
    //get todas las Cartas
    @GetMapping
    public ResponseEntity<List<Carta>> findAll() {
        List<Carta> res;
        res = (List<Carta>) CartaService.findAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    
    //update Carta
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Carta> putMethodName(@PathVariable Integer id, @RequestBody @Valid Carta Carta) {
        RestPreconditions.checkNotNull(CartaService.findById(id), "Carta", "ID", id);
		return new ResponseEntity<>(this.CartaService.updateCarta(Carta, id), HttpStatus.OK);
    }

    // crear un nuevo Carta
    @PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Carta> create(@RequestBody @Valid Carta Carta) {
		Carta savedCarta = CartaService.saveCarta(Carta);
		return new ResponseEntity<>(savedCarta, HttpStatus.CREATED);
	}
    // borrar un Carta por id
    @DeleteMapping(value = "{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("id") int id) {
		RestPreconditions.checkNotNull(CartaService.findById(id), "Carta", "ID", id);
		CartaService.deleteCarta(id);
		return new ResponseEntity<>(new MessageResponse("Carta deleted!"), HttpStatus.OK);
	}
}
