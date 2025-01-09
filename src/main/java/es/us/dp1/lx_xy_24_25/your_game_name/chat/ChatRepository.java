package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;



public interface ChatRepository extends CrudRepository<Chat,Integer> {

    @Query("SELECT c FROM Chat c WHERE c.jugador.partida.id = :partidaId")
    List<Chat> findAllChatByPartidaId(Integer partidaId);
    
    List<Chat> findAll();

    @Modifying
    @Transactional
    @Query("DELETE FROM Chat c WHERE c.jugador.id = :jugadorId")
    void deleteByJugadorId(@Param("jugadorId") Integer jugadorId);
}
