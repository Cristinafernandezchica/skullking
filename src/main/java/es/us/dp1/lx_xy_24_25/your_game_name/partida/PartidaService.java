package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;

@Service
public class PartidaService {

    PartidaRepository pr;
    RondaService rs;

    @Autowired
    public PartidaService(PartidaRepository pr) {
        this.pr = pr;
    }

    // Con este método se puede filtrar por nombre y estado
    @Transactional(readOnly=true)
    public List<Partida> getAllPartidas(String nombre, PartidaEstado estado){
        if(nombre != null && estado != null){
            return pr.findByNombreAndEstado(nombre, estado);
        } else if(nombre != null){
            return pr.findByNombre(nombre);
        } else if(estado != null){
            return pr.findByEstado(estado);
        } else {
            Iterable<Partida> iterablePartidas = pr.findAll();
            List<Partida> listaPartidas = new ArrayList<>(); 
            iterablePartidas.forEach(listaPartidas::add);
            return listaPartidas;
        }
    }

    @Transactional(readOnly = true)
    public Optional<Partida> getPartidaById(Integer id){
        return pr.findById(id);
    }

    @Transactional
    public Partida save(Partida p){
        pr.save(p);
        return p;
    }

    @Transactional
    public void delete(Integer id){
        pr.deleteById(id);
    }

    // Lógica de juego

    // Inciamos la partida
    @Transactional
    public void iniciarPartida(Integer partidaId){
        Optional<Partida> partidaOpt = getPartidaById(partidaId);
        if (!partidaOpt.isPresent()) {
            throw new ResourceNotFoundException("Partida", "id", partidaId);
        }

        Partida partida = partidaOpt.get();
        partida.setEstado(PartidaEstado.JUGANDO);
        partida.setInicio(LocalDateTime.now());
        partida.setOwnerPartida(null);
        save(partida);
        rs.iniciarRonda(partida);      // Llama al método que inicia la primera ronda (en RondaService) --> TODO: Revisar nombre método llamada y si contiene atributos a pasar

    }

    // Finalizamos la partida
    @Transactional
    public void finalizarPartida(Integer partidaId){
        Optional<Partida> partidaOpt = getPartidaById(partidaId);
        if (!partidaOpt.isPresent()) {
            throw new ResourceNotFoundException("Partida", "id", partidaId);
        }

        Partida partida = partidaOpt.get();
        partida.setEstado(PartidaEstado.TERMINADA);
        partida.setFin(LocalDateTime.now());
        save(partida);

    }

}
