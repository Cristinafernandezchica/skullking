package es.us.dp1.lx_xy_24_25.your_game_name.truco;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;

import java.util.List;

public interface TrucoRepository extends CrudRepository<Truco, Integer> {

    @Query("SELECT t FROM Truco t WHERE (:bazaId IS NULL OR t.baza.id = :bazaId)")
	public List<Truco> findTrucosByBazaId(Integer bazaId);

    @Query("SELECT t.carta FROM Truco t WHERE t.baza.id = :bazaId ORDER BY t.turno ASC")
	public List<Carta> findCartaIdByBazaId(Integer bazaId);

    @Query("SELECT t FROM Truco t WHERE t.mano.jugador.id = :jugadorId")
	public List<Truco> findByJugadorId(Integer jugadorId);

    @Query("SELECT t FROM Truco t WHERE t.mano.id = :manoId")
	public List<Truco> findByManoId(Integer manoId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Truco t WHERE t.jugador.id = :jugadorId")
    void deleteByJugadorId(@Param("jugadorId") Integer jugadorId);

}
