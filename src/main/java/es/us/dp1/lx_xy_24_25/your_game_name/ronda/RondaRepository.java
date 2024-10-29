package es.us.dp1.lx_xy_24_25.your_game_name.ronda;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

import java.util.List;

public interface RondaRepository extends CrudRepository<Ronda, Integer>{

    List<Ronda> findAll();
    List<Ronda> findByName(String name);

    // Para mayor eficiencia en la busqueda por partida
    @Query("SELECT r FROM Ronda r WHERE r.partida.id = :id")
    Optional<Ronda> findByPartidaId(@Param("id") Integer id);

}
