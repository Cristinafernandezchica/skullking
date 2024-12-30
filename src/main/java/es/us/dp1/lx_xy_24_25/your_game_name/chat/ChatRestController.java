package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/chats")
@Tag(name = "Chats", description = "API para el manejo de Chats")
@SecurityRequirement(name = "bearerAuth")
public class ChatRestController {
    

    private ChatService chatService;

    @Autowired
    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{partidaId}")
    public List<Chat> findAllChatByPartidaId(@PathVariable("partidaId") Integer partidaId) {
        return chatService.findAllChatByPartidaId(partidaId);
    }
    
    @PostMapping
    public Chat enviarMensaje(@RequestBody @Valid Chat chat) {
        return chatService.enviarMensajes(chat);
    }

}