package es.us.dp1.lx_xy_24_25.your_game_name.invitacion;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface InvitacionRepository extends CrudRepository<Invitacion,Integer> {

    @Query("SELECT i FROM Invitacion i WHERE  i.destinatario.id = :destinatarioId")
    List<Invitacion> getAllMyInvitaciones(Integer destinatarioId);

    @Query("SELECT i FROM Invitacion i WHERE ( i.remitente.id =:destinatarioId OR i.destinatario.id = :destinatarioId) AND (i.remitente.id =:remitenteId OR i.destinatario.id = :remitenteId)")
    Invitacion getOneInvitacion(Integer destinatarioId,Integer remitenteId);
}
