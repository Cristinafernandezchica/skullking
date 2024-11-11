package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UsuarioPartidaEnJuegoEsperandoException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.MinJugadoresPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;

@Service
public class PartidaService {

    PartidaRepository pr;
    RondaService rs;
    JugadorService js;

    @Autowired
    public PartidaService(PartidaRepository pr, RondaService rs, JugadorService js) {
        this.pr = pr;
        this.rs = rs;
        this.js = js;
    }

    // Con este método se puede filtrar por nombre y estado
    @Transactional(readOnly=true)
    public List<Partida> getAllPartidas(String nombre, PartidaEstado estado) throws DataAccessException{
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

    /*
    @Transactional(readOnly = true)
    public Optional<Partida> getPartidaById(Integer id){
        return pr.findById(id);
    }
    */

    @Transactional(readOnly = true)
    public Partida getPartidaById(Integer id) throws DataAccessException{
        Optional<Partida> partida = pr.findById(id);
        if(partida.isPresent()){
            return partida.get();
        } else{
            throw new ResourceNotFoundException("Partida", "id", id);
        }
    }

    /*
    @Transactional
    public Partida save(Partida p) throws DataAccessException{
        pr.save(p);
        return p;
    }
    */

    @Transactional
    public Partida save(Partida p) throws DataAccessException {
        Integer ownerId = p.getOwnerPartida();
        boolean partidaEsperandoJugando = usuarioPartidaEnJuegoEsperando(ownerId);
        if (partidaEsperandoJugando) {
            throw new UsuarioPartidaEnJuegoEsperandoException("El usuario ya tiene una partida en espera o en juego.");
        }
        return pr.save(p);
    }


    @Transactional
    public void delete(Integer id) throws DataAccessException{
        pr.deleteById(id);
    }

    // Lógica de juego

    // Inciamos la partida
    @Transactional
    public void iniciarPartida(Integer partidaId){
        Partida partida = getPartidaById(partidaId);
        if (partida == null) {
            throw new ResourceNotFoundException("Partida", "id", partidaId);
        }

        Integer numJugadoresPartida = js.findJugadoresByPartidaId(partidaId).size();
        if(numJugadoresPartida < 3) {
            throw new MinJugadoresPartidaException("Tiene que haber un mínimo de 3 jugadores en la sala para empezar la partida");
        }

        partida.setEstado(PartidaEstado.JUGANDO);
        partida.setInicio(LocalDateTime.now());
        partida.setOwnerPartida(null);
        save(partida);
        rs.iniciarRonda(partida);

    }

    // Finalizamos la partida
    @Transactional
    public void finalizarPartida(Integer partidaId){
        Partida partida = getPartidaById(partidaId);
        if (partida == null) {
            throw new ResourceNotFoundException("Partida", "id", partidaId);
        }

        partida.setEstado(PartidaEstado.TERMINADA);
        partida.setFin(LocalDateTime.now());
        save(partida);

    }

    // Para Excepción
    public Boolean usuarioPartidaEnJuegoEsperando(Integer ownerId){
        List<Partida> partidasEnProgresoEsperando = pr.findByOwnerPartidaAndEstado(ownerId, List.of(PartidaEstado.ESPERANDO, PartidaEstado.JUGANDO));
        return !partidasEnProgresoEsperando.isEmpty();
    }

}
