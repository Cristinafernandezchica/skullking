package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlProcessor.MatchStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/partidas")
@Tag(name = "Partidas", description = "API par el manejo de las Partidas")
@SecurityRequirement(name = "bearerAuth")
public class PartidaController {

    PartidaService ps;
    JugadorService js;
    RondaService rs;

    @Autowired
    public PartidaController(PartidaService ps) {
        this.ps = ps;
        this.js = js;
        this.rs = rs;
    }

    // @RequestParam es para filtrar por esos valores, por tanto no hacen falta los métodos PartidasByName y PartidasByEstado
    @GetMapping
    public List<Partida> getAllPartidas(@ParameterObject() @RequestParam(value="nombre", required = false) String nombre, @ParameterObject @RequestParam(value="estado",required = false) PartidaEstado estado){
        return ps.getAllPartidas(nombre, estado);
    }

    @GetMapping("/{id}")
    public Partida getPartidaById(@PathVariable("id")Integer id){
        Optional<Partida> p = ps.getPartidaById(id);
        if(!p.isPresent())
            throw new ResourceNotFoundException("Partida", "id", id);
        return p.get();
    }

    @PostMapping()
    public ResponseEntity<Partida> createPartida(@Valid @RequestBody Partida p){
        p=ps.save(p);
        URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(p.getId())
                .toUri();
        return ResponseEntity.created(location).body(p);
    }

    @PutMapping(value="/{id}")
    public ResponseEntity<Void> updatePartida(@Valid @RequestBody Partida p, @PathVariable("id") Integer id){
        Partida pToUpdate = getPartidaById(id);
        BeanUtils.copyProperties(p, pToUpdate, "id");
        ps.save(pToUpdate);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartida(@PathVariable("id")Integer id){
        if(getPartidaById(id)!=null)
            ps.delete(id);
        return ResponseEntity.noContent().build();
        
    }

    // Relación de uno a muchos con la clase Jugador, mirar los nombres de los métodos
    @GetMapping("/{id}/jugadores")
    public List<Jugador> getJugadoresByPartidaId(@PathVariable("id")Integer id){
        return js.getJugadoresByPartidaId(id);
    }

    // Relación de uno a 10 con la clase Ronda, mirar los nombres de los métodos
    @GetMapping("/{id}/rondas")
    public List<Ronda> getRondasByPartidaId(@PathVariable("id")Integer id){
        return rs.getRondasByPartidaId(id);
    }

    // Para iniciar una partida desde frontend
    @PostMapping("/{id}/iniciar-partida")
    public ResponseEntity<Void> iniciarPartida(@PathVariable("id")Integer id){
        ps.iniciarPartida(id);
        return ResponseEntity.ok().build();
    }
    

}
