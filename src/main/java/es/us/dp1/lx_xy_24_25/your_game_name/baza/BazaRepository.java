package es.us.dp1.lx_xy_24_25.your_game_name.baza;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface BazaRepository extends CrudRepository<Baza, Integer>{

    List<Baza> findAll();

    @Query("SELECT b FROM Baza b WHERE b.ronda.id = :rondaId")
    List<Baza> findBazasByRondaId(Integer rondaId);

    @Query("SELECT b FROM Baza b WHERE b.ronda.id = :rondaId AND b.numBaza = :numBaza")
    Optional<Baza> findByRondaIdAndNumBaza(Integer rondaId, Integer numBaza);

}