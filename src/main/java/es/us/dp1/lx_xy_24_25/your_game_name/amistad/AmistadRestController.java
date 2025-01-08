package es.us.dp1.lx_xy_24_25.your_game_name.amistad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("/api/v1/amistades")
@Tag(name = "Amistad", description = "API para el manejo de las Amistades")
@SecurityRequirement(name = "bearerAuth")
public class AmistadRestController {
    
    private AmistadService amistadService;

    @Autowired
    public AmistadRestController(AmistadService amistadService) {
        this.amistadService = amistadService;
    }

    @GetMapping("misAmigos/{remitenteId}")
    public ResponseEntity<List<User>> getAllMyFriends(@PathVariable("remitenteId")Integer remitenteId) {
        return new ResponseEntity<>(amistadService.getAllMyFriends(remitenteId), HttpStatus.OK);
    }

    @PostMapping("/{remitenteId}/{destinatarioId}")
    public ResponseEntity<Amistad> enviarSolicitud(
    @PathVariable("remitenteId")Integer remitenteId,
    @PathVariable("destinatarioId") String destinatarioId){
    return new ResponseEntity<>(amistadService.eviarSolicitudDeAmistad(remitenteId, destinatarioId), HttpStatus.CREATED);
    }
    
    @PutMapping(value="aceptarORechazarSolicitud/{remitenteId}/{destinatarioId}/{aceptar}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Amistad> aceptarORechazarSolicitud(
        @PathVariable("remitenteId")Integer remitenteId,
        @PathVariable("destinatarioId")Integer destinatarioId,
        @PathVariable("aceptar") Boolean aceptar ){
        Amistad a = amistadService.getOneAmistad(remitenteId, destinatarioId);
		return new ResponseEntity<>(amistadService.aceptarORechazarSolicitudDeAmistad(remitenteId, destinatarioId,aceptar), HttpStatus.OK);
    }

    @GetMapping("amigosConectados/{remitenteId}")
    public ResponseEntity<List<User>> getAllMyConnectedFriends(@PathVariable("remitenteId")Integer remitenteId) {
        List<User> todosMisAmigosConectados = amistadService.getAllMyConnectedFriends(remitenteId);
        return new ResponseEntity<>(todosMisAmigosConectados, HttpStatus.OK);
    }


    @GetMapping("misSolicitudes/{remitenteId}")
    public ResponseEntity<List<User>> getAllMySolicitudes(@PathVariable("remitenteId")Integer remitenteId) {
        List<User> todasMisSolicitudes = amistadService.getAllMySolicitudes(remitenteId);
        return new ResponseEntity<>(todasMisSolicitudes, HttpStatus.OK);
    }

}