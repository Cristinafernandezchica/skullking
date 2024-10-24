package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JugadorService {
    private JugadorRepository jugadorRepository;

    @Autowired
    public JugadorService(JugadorRepository jugadorRepository) {
        this.jugadorRepository = jugadorRepository;
    }

    @Transactional
    public Jugador crearJugador(Jugador jugador) {
        return jugadorRepository.save(jugador);
    }

    @Transactional(readOnly = true)
    public Iterable<Jugador> listarJugadores() {
        return jugadorRepository.findAll();
    }

    @Transactional
    public Iterable<Jugador> findJugadorByPartidaId(Integer partidaId) {
        return jugadorRepository.findByPartidaId(partidaId);
    }
}
