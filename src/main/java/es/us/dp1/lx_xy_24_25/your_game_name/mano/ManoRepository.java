package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface ManoRepository extends CrudRepository<Mano, Integer> {
    
    @Query("SELECT m FROM Mano m WHERE m.jugador.id = :jugadorId")
    List<Mano> findAllManoByJugadorId(Integer jugadorId);
}
