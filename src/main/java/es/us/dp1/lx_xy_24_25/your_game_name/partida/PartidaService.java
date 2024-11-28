package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UsuarioPartidaEnJuegoEsperandoException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.MinJugadoresPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.MismoNombrePartidaNoTerminadaException;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import jakarta.validation.Valid;

@Service
public class PartidaService {

    PartidaRepository pr;
    RondaService rs;
    JugadorService js;
    UserService us;

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

    @Transactional(readOnly = true)
    public Partida getPartidaById(Integer id) throws DataAccessException{
        Optional<Partida> partida = pr.findById(id);
        if(partida.isPresent()){
            return partida.get();
        } else{
            throw new ResourceNotFoundException("Partida", "id", id);
        }
    }
    
    @Transactional
    public Partida save(Partida p) throws DataAccessException {
        Integer ownerId = p.getOwnerPartida();
        boolean partidaEsperandoJugando = usuarioPartidaEnJuegoEsperando(ownerId);
        if(mismoNombrePartidaNoTerminada(p)){
            throw new MismoNombrePartidaNoTerminadaException("Ya existe una partida no finalizada con ese nombre.");
        } if (partidaEsperandoJugando) {
            throw new UsuarioPartidaEnJuegoEsperandoException("No puede crear otra partida, ya tiene una en espera o en juego.");
        } else if(usuarioJugadorEnPartida(p)){
            throw new UsuarioPartidaEnJuegoEsperandoException("No puede crear una partia, ya tiene una en espera o en juego.");
        }
        return pr.save(p);
    }


    @Transactional
    public Partida update(@Valid Partida partida, Integer idToUpdate) throws DataAccessException{
        Partida toUpdate = getPartidaById(idToUpdate);
        BeanUtils.copyProperties(partida, toUpdate, "id");
        pr.save(toUpdate);
        return toUpdate;
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
        update(partida, partidaId);
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

        Integer puntosGanador = null;
        List<Jugador> jugadoresPartida = js.findJugadoresByPartidaId(partidaId);
        for(Jugador jugador : jugadoresPartida){
            User usuarioJugador = jugador.getUsuario();
            usuarioJugador.setNumPuntosGanados(usuarioJugador.getNumPuntosGanados() + jugador.getPuntos());
            usuarioJugador.setNumPartidasJugadas(usuarioJugador.getNumPartidasJugadas() + 1);
            if(puntosGanador == null || jugador.getPuntos() > puntosGanador){
                puntosGanador = jugador.getPuntos();
            }
        }

        Integer puntosFinalGanador = puntosGanador;
        List<User> ganadores = jugadoresPartida.stream().filter(j-> j.getPuntos().equals(puntosFinalGanador)).map(j-> j.getUsuario()).collect(Collectors.toList());
        ganadores.forEach(u-> u.setNumPartidasGanadas(u.getNumPartidasGanadas()+1));

        List<User> usuarios = jugadoresPartida.stream().map(j-> j.getUsuario()).collect(Collectors.toList());
        for(User u : usuarios){
            us.saveUser(u);
        }
        update(partida, partidaId);
    }

    // Para Excepción: Si ya tiene una partida creada en juego o esperando, no podrá crear otra partida
    public Boolean usuarioPartidaEnJuegoEsperando(Integer ownerId){
        List<Partida> partidasEnProgresoEsperando = pr.findByOwnerPartidaAndEstado(ownerId, List.of(PartidaEstado.ESPERANDO, PartidaEstado.JUGANDO));
        return !partidasEnProgresoEsperando.isEmpty();
    }

    // Excepción: Para que no pueda crear una partida si ya se ha unido a una en juego o esperando (TODO: PROBAR)
    public Boolean usuarioJugadorEnPartida(Partida partidaCrear) throws DataAccessException{
        Boolean lanzarExcepcion = false;
        Iterable<Jugador> jugadores = js.findAll();
        List<Jugador> jugadoresFiltrados = StreamSupport.stream(jugadores.spliterator(), false)
        .filter(jugador -> jugador.getUsuario().getId() == partidaCrear.getOwnerPartida() 
        && (jugador.getPartida().getEstado().equals(PartidaEstado.ESPERANDO) 
            || jugador.getPartida().getEstado().equals(PartidaEstado.JUGANDO)))
            .collect(Collectors.toList());
        if(jugadoresFiltrados.size() > 0){
            lanzarExcepcion = true;
        }
        return lanzarExcepcion;
    }

    // Excepción: No puede haber dos partidas (no finalizadas) con el mismo nombre TODO: PPROBAR
    public Boolean mismoNombrePartidaNoTerminada(Partida partidaCrear) throws DataAccessException{
        Boolean lanzarExcepcion = false;
        List<Partida> partidasFiltradasEsperando = pr.findByNombreAndEstado(partidaCrear.getNombre(), PartidaEstado.ESPERANDO);
        List<Partida> partidasFiltradasJugando = pr.findByNombreAndEstado(partidaCrear.getNombre(), PartidaEstado.JUGANDO);
        if(partidasFiltradasEsperando.size() > 0 || partidasFiltradasJugando.size() > 0){
            lanzarExcepcion = true;
        }
        return lanzarExcepcion;
    }

}
