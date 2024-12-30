package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;



public interface ChatRepository extends CrudRepository<Chat,Integer> {

    @Query("SELECT c FROM Chat c WHERE c.jugador.partida.id = :partidaId")
    List<Chat> findAllChatByPartidaId(Integer partidaId);
    
    List<Chat> findAll();
}
