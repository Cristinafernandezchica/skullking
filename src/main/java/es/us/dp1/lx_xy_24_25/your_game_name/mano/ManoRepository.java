package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface ManoRepository extends CrudRepository<Mano, Integer> {

    // Devuelve la mano de un jugador en una ronda
    @Query("SELECT DISTINCT m FROM Mano m WHERE m.ronda.id = :rondaId AND m.jugador.id = :jugadorId")
	public Optional<Mano> findManoByJugadorIdRondaId(Integer rondaId, Integer jugadorId);

    
    @Query("SELECT m FROM Mano m WHERE m.jugador.id = :jugadorId")
    List<Mano> findAllManoByJugadorId(Integer jugadorId);

    @Query("SELECT m FROM Mano m WHERE m.ronda.id = :rondaId")
    List<Mano> findAllByRondaId(Integer rondaId);
}
