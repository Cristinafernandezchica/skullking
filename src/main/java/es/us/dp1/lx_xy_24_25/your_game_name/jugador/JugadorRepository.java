package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface JugadorRepository extends CrudRepository<Jugador,Integer>{
    
    @Query("SELECT j FROM Jugador j WHERE j.partida.id = :partidaId")
    List<Jugador> findJugadoresByPartidaId(Integer partidaId);

    @Query("SELECT j FROM Jugador j WHERE j.usuario.id = :usuarioId")
    List<Jugador> findJugadoresByUsuarioId(Integer usuarioId);

    Optional<Jugador> findById(Integer id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Partida p WHERE p.ownerPartida = :userId")
    void deletePartidasByOwner(@Param("userId") Integer userId);
    
}
