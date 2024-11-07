package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface JugadorRepository extends CrudRepository<Jugador,Integer>{
    
    @Query("SELECT j FROM Jugador j WHERE j.partidaId = :partidaId")
    List<Jugador> findJugadoresByPartidaId(Integer partidaId);

    @Query("SELECT j FROM Jugador j WHERE j.usuario.id = :usuarioId")
    List<Jugador> findJugadorByUsuarioId(Integer usuarioId);
    
    Optional<Jugador> findById(Integer id);
}
