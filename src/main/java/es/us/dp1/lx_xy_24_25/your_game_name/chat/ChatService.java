package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;



@Service
public class ChatService {
    
    private ChatRepository chatRepository;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatService(ChatRepository chatRepository, SimpMessagingTemplate messagingTemplate) {
        this.chatRepository = chatRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Chat saveChat(Chat chat) {
        chatRepository.save(chat);
        return chat;
    }

    //Listar todas las Chats de la base de datos
    @Transactional(readOnly = true)
    public List<Chat> getAllChats() throws DataAccessException{
        return chatRepository.findAll().stream().toList();
    }

    //Get una Chat por su id
    @Transactional(readOnly = true)
    public Chat findById(Integer id) throws DataAccessException {
        return chatRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Chat", "id", id));
    }

    //Delete una Chat por su id
    @Transactional
    public void deleteChat(Integer id) throws DataAccessException{
        chatRepository.deleteById(id);
    }
    
    //Update una Chat existente
    @Transactional
    public Chat updateChat(@Valid Chat Chat, Integer idToUpdate) throws DataAccessException{
        Chat toUpdate = findById(idToUpdate);
		BeanUtils.copyProperties(Chat, toUpdate, "id");
		chatRepository.save(toUpdate);

        return toUpdate;
    }
    @Transactional(readOnly = true)
    public List<Chat> findAllChatByPartidaId(Integer partidaId) throws DataAccessException{
        return chatRepository.findAllChatByPartidaId(partidaId).stream()
        .sorted(Comparator.comparing(Chat::getId)).toList();// Orden descendente;
    }

    @Transactional
    public Chat enviarMensajes(Chat chat){
        Chat nuevoChat=chatRepository.save(chat);
        messagingTemplate.convertAndSend("/topic/chats/" + chat.getJugador().getPartida().getId(), findAllChatByPartidaId(nuevoChat.getJugador().getPartida().getId()));
        return nuevoChat;
    }
}
