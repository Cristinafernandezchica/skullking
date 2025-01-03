package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import jakarta.validation.Valid;

@Service
public class RondaService {
    
    RondaRepository rondaRepository;

    @Autowired
    public RondaService(RondaRepository rondaRepository){
        this.rondaRepository = rondaRepository;
    }

    @Transactional(readOnly=true)
    public List<Ronda> getAllRondas(){
        return rondaRepository.findAll();
    }

    @Transactional
    public Ronda save(Ronda ronda) {
        rondaRepository.save(ronda);
        return ronda;
    }

    @Transactional(readOnly=true)
    public Ronda getRondaById(Integer id) throws DataAccessException{
        return rondaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ronda", "id", id));
    }

    @Transactional
    public void delete(Integer id) {
        rondaRepository.deleteById(id);
    }

    @Transactional
    public Ronda updateRonda(@Valid Ronda ronda, Integer idToUpdate) throws DataAccessException{
        Ronda toUpdate = getRondaById(idToUpdate);
        BeanUtils.copyProperties(ronda, toUpdate, "id");
        rondaRepository.save(toUpdate);
        return toUpdate;
        
    }

    @Transactional
    public Ronda iniciarRonda(Partida partida) {
        Ronda ronda = new Ronda();
        ronda.setNumBazas(1);
        ronda.setNumRonda(1);
        ronda.setPartida(partida);
        ronda.setEstado(RondaEstado.JUGANDO);
        Ronda res= rondaRepository.save(ronda);
        return res;
    }

    // Para pasar de ronda
    @Transactional
    public Ronda nextRonda(Integer rondaId, Integer numBazas) {
        Ronda ronda = rondaRepository.findById(rondaId)
            .orElseThrow(() -> new ResourceNotFoundException("Ronda no encontrada"));
        Integer nextRonda = ronda.getNumRonda() + 1;

        Ronda newRonda = new Ronda();
        newRonda.setNumRonda(nextRonda);
        newRonda.setEstado(RondaEstado.JUGANDO);
        newRonda.setNumBazas(numBazas);
        newRonda.setPartida(ronda.getPartida());
        Ronda result =  rondaRepository.save(newRonda);
        return result;
    }
    

    // Para finalizar la ronda
    @Transactional
    public void finalizarRonda(Integer rondaId){
        Ronda ronda = getRondaById(rondaId);

        ronda.setEstado(RondaEstado.FINALIZADA);

        rondaRepository.save(ronda);
    }

    @Transactional(readOnly = true)
    public Ronda findRondaActualByPartidaId(Integer partidaId) {
        List<Ronda> rondas = rondaRepository.findByPartidaId(partidaId);
        Ronda rondasOrdenadas =rondas.stream()
                .sorted((j1, j2) -> j2.getId().compareTo(j1.getId())) // Orden descendente
                .findFirst().orElse(null);
        return rondasOrdenadas;
    }

    @Transactional(readOnly = true)
    public Ronda rondaActual(Integer partidaId){
        List<Ronda> rondas = rondaRepository.findByPartidaId(partidaId);
        Ronda rondaActual = rondas.get(rondas.size()-1);
        return rondaActual;
    }
    

}
