package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import jakarta.validation.Valid;

@Service
public class RondaService {
    
    RondaRepository rr;
    PartidaService ps;
    ManoService ms;
    BazaService bs;
    JugadorService js;

    @Autowired
    public RondaService(RondaRepository rr, ManoService ms, @Lazy BazaService bs, JugadorService js){
        this.rr = rr;
        this.ms = ms;
        this.bs = bs;
        this.js = js;
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
    public Ronda getRondaById(Integer id) throws DataAccessException{
        return rr.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ronda", "id", id));
    }

    @Transactional
    public void delete(Integer id) {
        rr.deleteById(id);
    }

    @Transactional
    public Ronda updateRonda(@Valid Ronda ronda, Integer idToUpdate) throws DataAccessException{
        Ronda toUpdate = getRondaById(idToUpdate);
        BeanUtils.copyProperties(ronda, toUpdate, "id");
        rr.save(toUpdate);
        return toUpdate;
    }

    @Transactional
    public Ronda iniciarRonda (Partida partida) {
        Ronda ronda = new Ronda();
        ronda.setNumBazas(1);
        ronda.setNumRonda(1);
        ronda.setBazaActual(1);
        ronda.setPartida(partida);
        ronda.setEstado(RondaEstado.JUGANDO);
        Ronda res= rr.save(ronda);
        ms.iniciarManos(partida.getId(),res);
        // bs.iniciarBazas(ronda);
        return res;
    }

    // Partida accederá a dicha función proporcionándo el id de la ronda actual
    @Transactional
    public Ronda nextRonda(Integer rondaId) {
        Ronda ronda = rr.findById(rondaId)
            .orElseThrow(() -> new ResourceNotFoundException("Ronda no encontrada"));
        Integer nextRonda = ronda.getNumRonda() + 1;
        finalizarRonda(rondaId);

        // Comprobación si es la última ronda
        if(nextRonda > 10){
            ps.finalizarPartida(ronda.getPartida().getId());
        } else{
            ronda.setNumRonda(nextRonda);
            ronda.setBazaActual(1);
            ronda.setEstado(RondaEstado.JUGANDO);
            ms.iniciarManos(ronda.getPartida().getId(),ronda);
            ronda.setNumBazas(ms.getNumCartasARepartir(ronda.getNumRonda(), 
                    js.findJugadoresByPartidaId(ronda.getPartida().getId()).size()));
            bs.iniciarBazas(ronda);
        }
        
        return rr.save(ronda);
    }

    @Transactional
    public void finalizarRonda(Integer rondaId){
        Ronda ronda = getRondaById(rondaId);

        ronda.setEstado(RondaEstado.FINALIZADA);
        ms.calculoPuntaje(ronda.getNumBazas(), rondaId);

        rr.save(ronda);
    }

    @Transactional(readOnly = true)
    public Ronda findRondaActualByPartidaId(Integer partidaId) {
        List<Ronda> rondas = rr.findByPartidaId(partidaId);
        Ronda rondasOrdenadas =rondas.stream()
                .sorted((j1, j2) -> j2.getId().compareTo(j1.getId())) // Orden descendente
                .findFirst().orElse(null);
        return rondasOrdenadas;
    }

}
