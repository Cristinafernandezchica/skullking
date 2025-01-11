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

import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
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

    @GetMapping("misInvitaciones/{destinatarioId}")
    public ResponseEntity<List<Invitacion>> getTodasMisInvitaciones(@PathVariable("destinatarioId")Integer destinatarioId) {
        return new ResponseEntity<>(invitacionService.getTodasMisInvitaciones(destinatarioId), HttpStatus.OK);
    }

    @GetMapping("misUnaInvitaciones/{destinatarioId}/{remitente}")
    public ResponseEntity<Invitacion> getUnaInvitaciones(@PathVariable("destinatarioId")Integer destinatarioId,
    @PathVariable("remitente")Integer remitente) {
        return new ResponseEntity<>(invitacionService.getOne(destinatarioId,remitente), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Invitacion> enviarInvitacion(@RequestBody @Valid Invitacion invitacion) {
        Invitacion invitacionRealizada= invitacionService.enviarInvitacion(invitacion);
        return new ResponseEntity<>(invitacionRealizada, HttpStatus.OK);
    }

    @DeleteMapping("/{invitacionId}")
    public void aceptarInvitacion(@PathVariable("invitacionId") Integer invitacionId){
        invitacionService.deleteInvitacion(invitacionId);
    }
}
