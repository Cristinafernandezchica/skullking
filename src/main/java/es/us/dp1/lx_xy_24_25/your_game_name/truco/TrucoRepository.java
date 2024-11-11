package es.us.dp1.lx_xy_24_25.your_game_name.truco;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;


public interface TrucoRepository extends CrudRepository<Truco, Integer> {

    @Query("SELECT t FROM Truco t WHERE t.baza.id = :bazaId")
	public List<Truco> findByBazaId(Integer bazaId);

    // Poner en TrucoService y luego en BazaRestController
    @Query("SELECT t.idCarta FROM Truco t WHERE t.baza.id = :bazaId ORDER BY t.turno ASC")
	public List<Integer> findCartaIdByBazaId(Integer bazaId);

    
    @Query("SELECT t FROM Truco t WHERE t.mano.jugador.id = :jugadorId")
	public List<Truco> findByJugadorId(Integer jugadorId);

    @Query("SELECT t FROM Truco t WHERE t.mano.id = :manoId")
	public List<Truco> findByManoId(Integer manoId);

    /* 
    // REVISAR Y QUIZAS QUITAR
    @Query("SELECT DISTINCT t FROM Truco t WHERE t.baza.id = :bazaId AND t.idCarta = :cartaId")
	public Optional<Truco> findTrucoByBazaIdCartaId(Integer bazaId, Integer cartaId);

    // REVISAR Y QUIZAS QUITAR
    @Query("SELECT DISTINCT t.jugador FROM Truco t WHERE t.baza.id = :bazaId AND t.idCarta = :cartaId")
	public Optional<Integer> findJugadorIdByBazaIdCartaId(Integer bazaId, Integer cartaId);
    */
}
