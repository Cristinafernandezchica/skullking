package es.us.dp1.lx_xy_24_25.your_game_name.invitacion;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import es.us.dp1.lx_xy_24_25.your_game_name.amistad.Amistad;

public interface InvitacionRepository extends CrudRepository<Invitacion,Integer> {
    

    @Query("SELECT i FROM Invitacion i WHERE  OR i.destinatario.id = :destinatarioId")
    List<Invitacion> getAllMyInvitaciones(Integer destinatarioId);
}
