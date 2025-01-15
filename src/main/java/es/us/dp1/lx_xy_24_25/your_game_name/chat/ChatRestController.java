package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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

    @Operation(summary = "Obtener todos los chats", description = "Devuelve una lista de todos los chats.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de chats obtenida")
    })
    @GetMapping
    public ResponseEntity<List<Chat>> findAll() {
        List<Chat> res;
        res = (List<Chat>) chatService.getAllChats();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Obtener todos los chats de una partida", description = "Devuelve una lista de todos los chats de una partida específica por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de chats obtenida"),
        @ApiResponse(responseCode = "404", description = "Chats no encontrados")
    })
    @GetMapping("/{partidaId}")
    public List<Chat> findAllChatByPartidaId(@PathVariable("partidaId") Integer partidaId) {
        return chatService.findAllChatByPartidaId(partidaId);
    }

    @Operation(summary = "Enviar un mensaje", description = "Envía un mensaje en una partida específica por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mensaje enviado con éxito"),
        @ApiResponse(responseCode = "404", description = "Partida no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos del mensaje no válidos")
    })
    @PostMapping
    public Chat enviarMensaje(@RequestBody @Valid Chat chat) {
        return chatService.enviarMensajes(chat);
    }

}