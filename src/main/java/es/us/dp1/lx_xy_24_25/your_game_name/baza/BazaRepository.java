package es.us.dp1.lx_xy_24_25.your_game_name.baza;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BazaRepository extends CrudRepository<Baza, Integer>{

    List<Baza> findAll();

    @Query("SELECT b FROM Baza b WHERE b.ronda.id = :rondaId")
    List<Baza> findBazasByRondaId(Integer rondaId);

    @Query("SELECT b FROM Baza b WHERE b.ronda.id = :rondaId AND b.numBaza = :numBaza")
    Optional<Baza> findByRondaIdAndNumBaza(Integer rondaId, Integer numBaza);

    // Para calculo de turnos
    @Query("SELECT b FROM Baza b WHERE b.ronda.id = :rondaId AND b.id < :bazaId ORDER BY b.id DESC")
    Optional<Baza> findBazaAnterior(@Param("bazaId") Integer bazaId, @Param("rondaId") Integer rondaId);

    // Para el calculo de ptos de bonificación
    @Query("SELECT b FROM Baza b WHERE b.ronda.id = :rondaId AND b.ganador.id = :jugadorId")
    List<Baza> findByIdRondaAndIdJugador(@Param("rondaId") Integer rondaId, @Param("jugadorId") Integer jugadorId);
}