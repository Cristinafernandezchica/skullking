package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class RondaService {
    
    RondaRepository rr;

    @Autowired
    public RondaService(RondaRepository rr){
        this.rr = rr;
    }

    @Transactional(readOnly=true)
    public List<Ronda> getAllRondas(){
        return rr.findAll();
    }

    @Transactional
    public Ronda save(Ronda r) {
        rr.save(r);
        return r;
    }

    @Transactional(readOnly=true)
    public Optional<Ronda> getRondaById(Integer id){
        return rr.findById(id);
    }

    @Transactional(readOnly=true)
    public Optional<Ronda> getRondasByPartidaId(Integer id) {
        return rr.findByPartidaId(id);
    }

    @Transactional
    public void delete(Integer id) {
        rr.deleteById(id);
    }

    @Transactional
    public Ronda inicial(Partida partida) {
        Ronda ronda = new Ronda();
        ronda.setNumBazas(1);
        ronda.setNumRonda(1);
        ronda.setBazaActual(1);
        ronda.setPartida(partida);
        return rr.save(ronda);
    }

    // Parida accederá a dicha función proporcionándo el id de la ronda actual
    @Transactional
    public Ronda nextRonda(Integer id) {
        Ronda ronda = rr.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ronda no encontrada"));
        int nextRonda = ronda.getNumRonda() + 1;

        // IMPORTANTE: habrá que implementar ua comprobación en partida
        // para cuando se cree la ronda con numRonda = 11 cambiar el estado
        // de la partida a "finalizada"

        ronda.setNumBazas(nextRonda);
        ronda.setNumRonda(nextRonda);
        ronda.setBazaActual(nextRonda);

        return rr.save(ronda);
    }

    @Transactional
    public void finalizarPartida(Integer rondaId){
        Optional<Ronda> rondaOpt = getRondaById(rondaId);
        if (!rondaOpt.isPresent()) {
            throw new ResourceNotFoundException("Ronda no encontrada");
        }

        Ronda ronda = rondaOpt.get();
        ronda.setEstado(RondaEstado.FINALIZADA);

        rr.save(ronda);
    }

}
