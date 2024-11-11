package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import jakarta.validation.Valid;

@Service
public class JugadorService {
    private JugadorRepository jugadorRepository;

    @Autowired
    public JugadorService(JugadorRepository jugadorRepository) {
        this.jugadorRepository = jugadorRepository;
    }

    //save a jugador en la base de datos
    @Transactional
    public Jugador saveJugador(Jugador jugador) {
        return jugadorRepository.save(jugador);
    }
    // listar todos los jugadores de la base de datos
    @Transactional(readOnly = true)
    public Iterable<Jugador> findAll() {
        return jugadorRepository.findAll();
    }
    //obtener jugadores por id de partida
    @Transactional
    public List<Jugador> findJugadoresByPartidaId(Integer partidaId) {
        return jugadorRepository.findJugadoresByPartidaId(partidaId);
    }
    //obtener jugador por pk
    @Transactional(readOnly = true)
    public Jugador findById(Integer id) {
        return jugadorRepository.findById(id).orElse(null);
    }

    //borrar jugador por pk
    @Transactional
    public void deleteJugador(Integer id) {
        jugadorRepository.deleteById(id);
    }
    
    //actualizar jugador
    @Transactional
	public Jugador updateJugador(@Valid Jugador jugador, Integer idToUpdate) {
		Jugador toUpdate = findById(idToUpdate);
		BeanUtils.copyProperties(jugador, toUpdate, "id");
		jugadorRepository.save(toUpdate);

		return toUpdate;
	}

    //obtener mas jugador reciente por id de usuario
    @Transactional(readOnly = true)
    public Jugador findJugadorByUsuarioId(Integer usuarioId) {
       List<Jugador> jugadores =jugadorRepository.findJugadoresByUsuarioId(usuarioId);
       Jugador jugadoresOrdenados = jugadores.stream()
                .sorted((j1, j2) -> j2.getId().compareTo(j1.getId())) // Orden descendente
                .findFirst().orElseThrow(()-> new ResourceNotFoundException("no se encontro ningun jugador cuyo usuarioId sea" + usuarioId));
                return jugadoresOrdenados;
    }


    // Para Validator
    public Boolean usuarioMultiplesJugadoresEnPartida(User usuario, Partida partida){
        List<Jugador> jugadoresUsuario =jugadorRepository.findJugadoresByUsuarioId(usuario.getId());
        long count = jugadoresUsuario.stream()
                .filter(jugador -> jugador.getPartida().equals(partida))
                .count();
        return count > 1;
    }
}
