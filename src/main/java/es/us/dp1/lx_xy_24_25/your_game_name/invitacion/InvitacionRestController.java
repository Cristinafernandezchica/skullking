package es.us.dp1.lx_xy_24_25.your_game_name.invitacion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/invitaciones")
@Tag(name = "Amistad", description = "API para el manejo de las Invitaciones")
@SecurityRequirement(name = "bearerAuth")
public class InvitacionRestController {
    

    private InvitacionService invitacionService;

    @Autowired
    public InvitacionRestController(InvitacionService invitacionService) {
        this.invitacionService = invitacionService;
    }

    @Operation(summary = "Obtener todas mis invitaciones", description = "Devuelve una lista de todas las invitaciones recibidas por un usuario en específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de inivtaciones obtenida"),
        @ApiResponse(responseCode = "404", description = "No se encontraron invitaciones para el usuario")
    })
    @GetMapping("misInvitaciones/{destinatarioId}")
    public ResponseEntity<List<Invitacion>> getTodasMisInvitaciones(@PathVariable("destinatarioId")Integer destinatarioId) {
        return new ResponseEntity<>(invitacionService.getTodasMisInvitaciones(destinatarioId), HttpStatus.OK);
    }

    @Operation(summary = "Obtener una invitación", description = "Devuelve una invitación específica según el ID de remitente y destinatario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invitación obtenida"),
        @ApiResponse(responseCode = "404", description = "No se encontró la invitación")
    })
    @GetMapping("misUnaInvitaciones/{destinatarioId}/{remitente}")
    public ResponseEntity<Invitacion> getUnaInvitaciones(@PathVariable("destinatarioId")Integer destinatarioId,
    @PathVariable("remitente")Integer remitente) {
        return new ResponseEntity<>(invitacionService.getOne(destinatarioId,remitente), HttpStatus.OK);
    }

    @Operation(summary = "Enviar una invitación", description = "Permite enviar una invitación a un usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invitación enviada con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos de la incvitación no válidos"),
        @ApiResponse(responseCode = "404", description = "No se encontró el destinatario")
    })
    @PostMapping()
    public ResponseEntity<Invitacion> enviarInvitacion(@RequestBody @Valid Invitacion invitacion) {
        Invitacion invitacionRealizada= invitacionService.enviarInvitacion(invitacion);
        return new ResponseEntity<>(invitacionRealizada, HttpStatus.OK);
    }

    @Operation(summary = "Aceptar o rechazar una invitacion", description = "Permite aceptar o rechazar una invitación dado su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Invitación aceptada o rechazada con éxito"),
        @ApiResponse(responseCode = "404", description = "No se encontró la invitación")
    })
    @DeleteMapping("/{invitacionId}")
    public void aceptarInvitacion(@PathVariable("invitacionId") Integer invitacionId){
        invitacionService.deleteInvitacion(invitacionId);
    }
}