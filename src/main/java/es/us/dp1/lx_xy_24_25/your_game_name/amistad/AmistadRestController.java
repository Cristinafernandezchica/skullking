package es.us.dp1.lx_xy_24_25.your_game_name.amistad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @Operation(summary = "Obtener todos mis amigos", description = "Devuelve la lista de todos mis amigos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de amigos encontrada"),
            @ApiResponse(responseCode = "401", description = "No está autorizado"),
            @ApiResponse(responseCode = "404", description = "No se encontró la lista de amigos")
    
    })
    @GetMapping("misAmigos/{remitenteId}")
    public ResponseEntity<List<User>> getAllMyFriends(@PathVariable("remitenteId")Integer remitenteId) {
        return new ResponseEntity<>(amistadService.getAllMyFriends(remitenteId), HttpStatus.OK);
    }

    @Operation(summary = "Obtener amigos que pueden ver la partida", description = "Devuelve la lista de amigos que pueden ver la partida.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de amigos encontrada"),
            @ApiResponse(responseCode = "401", description = "No está autorizado"),
            @ApiResponse(responseCode = "404", description = "No se encontró la lista de amigos que pueden ver la partida")
    })
    @GetMapping("puedeVerLaPartida/{partidaId}/{miId}")
    public ResponseEntity<List<User>> getAmigosQuePuedenVerLaPartida(@PathVariable("partidaId")Integer partidaId,
    @PathVariable("miId")Integer miId) {
        return new ResponseEntity<>(amistadService.puedesVerPartida(partidaId,miId), HttpStatus.OK);
    }

    @Operation(summary = "Enviar solicitud de amistad", description = "Envía una solicitud de amistad a otro usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitud de amistad enviada"),
            @ApiResponse(responseCode = "401", description = "No está autorizado"),
            @ApiResponse(responseCode = "404", description = "No se encontró el destinatario")
    })
    @PostMapping("/{remitenteId}/{destinatarioId}")
    public ResponseEntity<Amistad> enviarSolicitud(
    @PathVariable("remitenteId")Integer remitenteId,
    @PathVariable("destinatarioId") String destinatarioId){
    return new ResponseEntity<>(amistadService.enviarSolicitudDeAmistad(remitenteId, destinatarioId), HttpStatus.CREATED);
    }
    
    @Operation(summary = "Aceptar o rechazar solicitud de amistad", description = "Acepta o rechaza una solicitud de amistad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud de amistad aceptada o rechazada"),
            @ApiResponse(responseCode = "401", description = "No está autorizado"),
            @ApiResponse(responseCode = "404", description = "No se encontró la solicitud de amistad")
    })
    @PutMapping(value="aceptarORechazarSolicitud/{remitenteId}/{destinatarioId}/{aceptar}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Amistad> aceptarORechazarSolicitud(
        @PathVariable("remitenteId")Integer remitenteId,
        @PathVariable("destinatarioId")Integer destinatarioId,
        @PathVariable("aceptar") Boolean aceptar ){
        Amistad a = amistadService.getOneAmistad(remitenteId, destinatarioId);
		return new ResponseEntity<>(amistadService.aceptarORechazarSolicitudDeAmistad(remitenteId, destinatarioId,aceptar), HttpStatus.OK);
    }

    @Operation(summary = "Obtener amigos conectados", description = "Devuelve la lista de amigos conectados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de amigos conectados encontrada"),
            @ApiResponse(responseCode = "401", description = "No está autorizado"),
            @ApiResponse(responseCode = "404", description = "No se encontró la lista de amigos conectados")
    })
    @GetMapping("amigosConectados/{remitenteId}")
    public ResponseEntity<List<User>> getAllMyConnectedFriends(@PathVariable("remitenteId")Integer remitenteId) {
        List<User> todosMisAmigosConectados = amistadService.getAllMyConnectedFriends(remitenteId);
        return new ResponseEntity<>(todosMisAmigosConectados, HttpStatus.OK);
    }


    @Operation(summary = "Obtener mis solicitudes de amistad", description = "Devuelve la lista de mis solicitudes de amistad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitudes de amistad encontradas"),
            @ApiResponse(responseCode = "401", description = "No está autorizado"),
            @ApiResponse(responseCode = "404", description = "No se encontraron solicitudes de amistad")
    })
    @GetMapping("misSolicitudes/{remitenteId}")
    public ResponseEntity<List<User>> getAllMySolicitudes(@PathVariable("remitenteId")Integer remitenteId) {
        List<User> todasMisSolicitudes = amistadService.getAllMySolicitudes(remitenteId);
        return new ResponseEntity<>(todasMisSolicitudes, HttpStatus.OK);
    }

}