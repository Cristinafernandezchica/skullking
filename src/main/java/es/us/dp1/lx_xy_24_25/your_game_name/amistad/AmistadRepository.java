package es.us.dp1.lx_xy_24_25.your_game_name.amistad;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AmistadRepository extends CrudRepository<Amistad,Integer> {

    @Query("SELECT a FROM Amistad a WHERE a.remitente.id = :remitenteId OR a.destinatario.id = :remitenteId")
    List<Amistad> getAllMyAmistad(Integer remitenteId);
    
    @Query("SELECT a FROM Amistad a WHERE (a.remitente.id = :remitenteId AND a.destinatario.id = :destinatarioId) OR (a.remitente.id = :destinatarioId AND a.destinatario.id = :remitenteId)")
    Amistad getOneAmistad(Integer remitenteId, Integer destinatarioId);

}
