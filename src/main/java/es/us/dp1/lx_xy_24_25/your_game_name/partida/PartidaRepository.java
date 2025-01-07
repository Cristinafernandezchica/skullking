package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaRepository extends CrudRepository<Partida, Integer> {
   

    List<Partida> findByNombre(String nombre);
    // List<Partida> findByJugador(Jugador jugador);

    List<Partida> findByEstado(PartidaEstado estado);
    
    List<Partida> findByNombreAndEstado(String nombre, PartidaEstado estado);

    @Query("SELECT p FROM Partida p WHERE p.ownerPartida = :ownerPartida AND p.estado IN :estados")
    List<Partida> findByOwnerPartidaAndEstado(@Param("ownerPartida") Integer ownerPartida, @Param("estados") List<PartidaEstado> estados);
    
}
