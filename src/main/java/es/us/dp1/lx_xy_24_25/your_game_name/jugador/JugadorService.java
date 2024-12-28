package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UsuarioPartidaEnJuegoEsperandoException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.exceptions.MaximoJugadoresPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.exceptions.UsuarioMultiplesJugadoresEnPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import jakarta.validation.Valid;

@Service
public class JugadorService {
    private JugadorRepository jugadorRepository;
    private PartidaRepository pr;
    private UserRepository userRepository;
    private static final Integer MAX_JUGADORES = 8;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public JugadorService(JugadorRepository jugadorRepository, PartidaRepository pr, SimpMessagingTemplate messagingTemplate) {
        this.jugadorRepository = jugadorRepository;
        this.pr = pr;
        this.messagingTemplate = messagingTemplate;
    }

    // Get jugador por id
    @Transactional(readOnly = true)
    public Jugador findById(Integer id) {
        return jugadorRepository.findById(id).orElse(null);
    }

    @Transactional
    public Jugador saveJugador(Jugador jugador) throws DataAccessException {
        Partida partida = jugador.getPartida();
        List<Jugador> jugadoresPartida = findJugadoresByPartidaId(partida.getId());

        if(jugadoresPartida.size() == MAX_JUGADORES){
            throw new MaximoJugadoresPartidaException("La partida está completa.");
        } else if(jugador.getUsuario().getId() == partida.getOwnerPartida() || jugadorEnPartida(jugadoresPartida, jugador)){
            throw new UsuarioMultiplesJugadoresEnPartidaException("El usuario ya tiene un jugador en la partida.");
        } else if(partidaEnJuegoEspera(jugador)){
            throw new UsuarioPartidaEnJuegoEsperandoException("El usuario ya tiene un jugador en una partida no finalizada.");
        } else{
            Jugador nuevoJugador = jugadorRepository.save(jugador);
            // Envía la lista actualizada de jugadores al canal WebSocket
            messagingTemplate.convertAndSend("/topic/partida/" + partida.getId(), findJugadoresByPartidaId(partida.getId()));

            return nuevoJugador;
        }
    }

    // Comprueba si el usuario ya tiene un jugador en la partida en la que se quiere meter
    @Transactional
    public Boolean jugadorEnPartida(List<Jugador> jugadoresPartida, Jugador jugadorCrear) throws DataAccessException{
        Boolean lanzarExcepcion = false;
        for (Jugador jugador : jugadoresPartida) {
            if(jugador.getUsuario().getId() == jugadorCrear.getUsuario().getId()){
                lanzarExcepcion = true;
            }
        }
        return lanzarExcepcion;
    }

    // Comprueba si el jugador tiene alguna partida en juego o en espera (CREAR PRUEBA)
    @Transactional
    public Boolean partidaEnJuegoEspera(Jugador jugadorCrear) throws DataAccessException{
        Boolean lanzarExcepcion = false;
        Iterable<Partida> partidas = pr.findAll();
        List<Partida> partidasFiltradas = StreamSupport.stream(partidas.spliterator(), false)
                .filter(partida -> partida.getEstado().equals(PartidaEstado.ESPERANDO) || 
                partida.getEstado().equals(PartidaEstado.JUGANDO))
                .collect(Collectors.toList());
        
        for(Partida pf : partidasFiltradas){
            List<Jugador> jugadoresPartida = findJugadoresByPartidaId(pf.getId());
            for(Jugador j: jugadoresPartida){
                if(jugadorCrear.getUsuario().getId() == j.getUsuario().getId()){
                    lanzarExcepcion = true;
                }
            }
        }

        return lanzarExcepcion;
    }


    // List todos los jugadores de la base de datos
    @Transactional(readOnly = true)
    public Iterable<Jugador> findAll() {
        return jugadorRepository.findAll();
    }
    // Get jugadores por id de partida
    @Transactional
    public List<Jugador> findJugadoresByPartidaId(Integer partidaId) {
        return jugadorRepository.findJugadoresByPartidaId(partidaId);
    }

    // Delete jugador por id
    @Transactional
    public void deleteJugador(Integer id) {
        jugadorRepository.deleteById(id);
    }
    
    // Update jugador
    @Transactional
	public Jugador updateJugador(@Valid Jugador jugador, Integer idToUpdate) {
		Jugador toUpdate = findById(idToUpdate);
		BeanUtils.copyProperties(jugador, toUpdate, "id");
		jugadorRepository.save(toUpdate);

		return toUpdate;
	}

    // Get el jugador mas reciente por id de usuario
    @Transactional(readOnly = true)
    public Jugador findJugadorByUsuarioId(Integer usuarioId) {
       List<Jugador> jugadores =jugadorRepository.findJugadoresByUsuarioId(usuarioId);
       Jugador jugadoresOrdenados = jugadores.stream()
                .sorted((j1, j2) -> j2.getId().compareTo(j1.getId())) // Orden descendente
                .findFirst().orElseThrow(()-> new ResourceNotFoundException("no se encontro ningun jugador cuyo usuarioId sea" + usuarioId));
                return jugadoresOrdenados;
    }

    @Transactional(readOnly = true)
    public Partida findPartidaByUsuarioId(Integer usuarioId){
        List<Jugador> jugadores =jugadorRepository.findJugadoresByUsuarioId(usuarioId);
        Partida partida = new Partida();
        for(Jugador j:jugadores){
            if(!(j.getPartida().getEstado().equals(PartidaEstado.TERMINADA))){
                partida = j.getPartida();
            }
        }
        return partida;
    }


    /*
    // Para ver el turno del jugador
    @Transactional(readOnly = true)
    public Integer findTurnoByJugadorId(Integer jugadorId) {
        Optional<Jugador> jugador = jugadorRepository.findById(jugadorId);
        return jugador.isPresent()? jugador.get().getTurno() : null;
    }
    */

    // Método para verificar si un usuario tiene múltiples jugadores en la misma partida
    /*
    public boolean usuarioMultiplesJugadoresEnPartida(User usuario, Partida partida) {
        List<Jugador> jugadoresUsuario = jugadorRepository.findJugadoresByUsuarioId(usuario.getId());
        long count = jugadoresUsuario.stream()
                .filter(jugador -> jugador.getPartida().equals(partida))
                .count();
        return count > 1;
    }
    */

    // Método para verificar si un usuario tiene una partida en juego o esperando
    /*
    public boolean usuarioPartidaEnJuegoEsperando(Integer usuarioId) {
        List<Partida> partidas = pr.findByOwnerPartidaAndEstado(usuarioId, List.of(PartidaEstado.ESPERANDO, PartidaEstado.JUGANDO));
        return !partidas.isEmpty();
    }
    */
    
}

