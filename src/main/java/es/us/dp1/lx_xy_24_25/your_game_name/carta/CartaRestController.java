package es.us.dp1.lx_xy_24_25.your_game_name.carta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    private final CartaService cartaService;


    @Autowired
    public CartaRestController(CartaService cartaService, AuthoritiesService authService) {
        this.cartaService = cartaService;
    }
    
    //get todas las Cartas
    @GetMapping
    public ResponseEntity<List<Carta>> findAll() {
        List<Carta> res;
        res = (List<Carta>) cartaService.findAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    
    //update Carta
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Carta> update(@PathVariable Integer id, @RequestBody @Valid Carta carta) {
        RestPreconditions.checkNotNull(cartaService.findById(id), "Carta", "ID", id);
		return new ResponseEntity<>(this.cartaService.updateCarta(carta, id), HttpStatus.OK);
    }

    // crear un nuevo Carta
    @PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Carta> create(@RequestBody @Valid Carta carta) {
		Carta savedCarta = cartaService.saveCarta(carta);
		return new ResponseEntity<>(savedCarta, HttpStatus.CREATED);
	}
    // borrar un Carta por id
    @DeleteMapping(value = "{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("id") int id) {
		RestPreconditions.checkNotNull(cartaService.findById(id), "Carta", "ID", id);
		cartaService.deleteCarta(id);
		return new ResponseEntity<>(new MessageResponse("Carta eliminada!"), HttpStatus.OK);
	}

    // Para que se puedan ver las cartas
    @GetMapping("/carta/{id}")
    public ResponseEntity<?> getCarta(@PathVariable Integer id) {
        Optional<Carta> cartaOpt = cartaService.findById(id);
        if (!cartaOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Carta no encontrada");
        }

        Carta carta = cartaOpt.get();
        Map<String, String> response = new HashMap<>();
        response.put("imagenFrontal", carta.getImagenFrontal());
        response.put("imagenTrasera", carta.getImagenTrasera());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/tigresa/{tipoCarta}")
    public ResponseEntity<Carta> cambioTigresa(@PathVariable String tipoCarta){
        RestPreconditions.checkNotNull(cartaService.cambioTigresa(tipoCarta), "Carta", "tipoCarta", tipoCarta);
        return new ResponseEntity<>(this.cartaService.cambioTigresa(tipoCarta), HttpStatus.OK);
    }
    
}
