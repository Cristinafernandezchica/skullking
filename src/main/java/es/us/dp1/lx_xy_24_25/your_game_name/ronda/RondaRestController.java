package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/rondas")
@Tag(name = "Rondas", description =  "API for the management of Rondas")

public class RondaRestController {
    
    RondaService rs;

    @Autowired
    public RondaRestController(RondaService rs) {
        this.rs = rs;
    }


    @GetMapping
    public List<Ronda> getAllRondas(){
        return rs.getAllRondas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ronda> getRondaById(@PathVariable("id") Integer id) {
        Ronda ronda = rs.getRondaById(id);
        return new ResponseEntity<Ronda>(ronda, HttpStatus.OK);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Ronda> createRonda(@Valid @RequestBody Ronda r) {
        r = rs.save(r);
        URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(r.getId())
                .toUri();
        return ResponseEntity.created(location).body(r);
    }

    @PutMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MessageResponse>  updateRonda(@Valid @RequestBody Ronda r, @PathVariable("id") Integer id) {
        RestPreconditions.checkNotNull(rs.getRondaById(id), "Ronda", "ID", id);
        Ronda rToUpdate = rs.getRondaById(id);
        BeanUtils.copyProperties(r, rToUpdate, "id");
        rs.save(rToUpdate);
        return new ResponseEntity<>(new MessageResponse("Ronda actualizada"), HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MessageResponse> deleteRonda(@PathVariable("id") Integer id) {
        RestPreconditions.checkNotNull(rs.getRondaById(id), "Ronda", "ID", id);
        rs.delete(id);
        return new ResponseEntity<>(new MessageResponse("Ronda eliminada"), HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{partidaId}/partida")
    public Ronda getRondaByPartidaId(@PathVariable("partidaId") Integer partidaId) {
        return rs.findRondaActualByPartidaId(partidaId);
    }

    @PostMapping("/bazaActual/{bazaId}/next-baza") 
    public ResponseEntity<Baza> nextBaza(@PathVariable Integer bazaId) { 
    Baza newBaza = rs.nextBaza(bazaId); 
    return ResponseEntity.ok(newBaza);
    }

}
