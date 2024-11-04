package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/rondas")
@Tag(name = "Rondas", description =  "API for the management of Rondas")

public class RondaController {
    
    RondaService rs;

    @Autowired
    public RondaController(RondaService rs) {
        this.rs = rs;
    }


    @GetMapping
    public List<Ronda> getAllRondas(){
        return rs.getAllRondas();
    }

    @GetMapping("/{id}")
    public Ronda getRondaById(@PathVariable("id") Integer id) {
        Optional<Ronda> r = rs.getRondaById(id);
        if (!r.isPresent())
            throw new ResourceNotFoundException("Ronda", "id", id);
        return r.get();
    }

    @PostMapping()
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
    public ResponseEntity<Void> updateRonda(@Valid @RequestBody Ronda r, @PathVariable("id") Integer id) {
        Ronda rToUpdate = getRondaById(id);
        BeanUtils.copyProperties(r, rToUpdate, "id");
        rs.save(rToUpdate);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRonda(@PathVariable("id") Integer id) {
        if (getRondaById(id) != null)
            rs.delete(id);
        return ResponseEntity.noContent().build();
    }

}
