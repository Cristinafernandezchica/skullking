package es.us.dp1.lx_xy_24_25.your_game_name.ronda;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import java.util.List;

public interface RondaRepository extends CrudRepository<Ronda, Integer>{

    List<Ronda> findAll();

    Optional<Ronda> findById(Integer id);

    @Query("SELECT r FROM Ronda r WHERE r.partida.id = :id")
    List<Ronda> findByPartidaId(@Param("id") Integer id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Ronda r WHERE r.partida.id = :partidaId")
    void deleteByPartidaId(@Param("partidaId") Integer partidaId);


}
