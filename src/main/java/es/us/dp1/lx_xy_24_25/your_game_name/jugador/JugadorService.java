package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
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
    //obtener jugador por id de partida
    @Transactional
    public Iterable<Jugador> findByPartidaId(Integer partidaId) {
        return jugadorRepository.findByPartidaId(partidaId);
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

    
}
