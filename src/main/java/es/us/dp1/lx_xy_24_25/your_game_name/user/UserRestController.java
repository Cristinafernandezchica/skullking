package es.us.dp1.lx_xy_24_25.your_game_name.user;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
class UserRestController {

	private final UserService userService;
	private final AuthoritiesService authService;

	@Autowired
	public UserRestController(UserService userService, AuthoritiesService authService) {
		this.userService = userService;
		this.authService = authService;
	}

	@GetMapping
	public ResponseEntity<List<User>> findAll(@RequestParam(required = false) String auth) {
		List<User> res;
		if (auth != null) {
			res = (List<User>) userService.findAllByAuthority(auth);
		} else
			res = (List<User>) userService.findAll();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping("authorities")
	public ResponseEntity<List<Authorities>> findAllAuths() {
		List<Authorities> res = (List<Authorities>) authService.findAll();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping(value = "{id}")
	public ResponseEntity<User> findById(@PathVariable("id") Integer id) {
		return new ResponseEntity<>(userService.findUser(id), HttpStatus.OK);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<User> create(@RequestBody @Valid User user) {
		User savedUser = userService.saveUser(user);
		return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
	}

	@PutMapping(value = "{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<User> update(@PathVariable("userId") Integer id, @RequestBody @Valid User user) {
		RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);
		return new ResponseEntity<>(this.userService.updateUser(user, id), HttpStatus.NO_CONTENT);
	}

	@DeleteMapping(value = "{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<MessageResponse> delete(@PathVariable("userId") int id) {
		RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);
		userService.deleteUser(id);
		return new ResponseEntity<>(new MessageResponse("Usuario eliminado"), HttpStatus.OK);
	}

	//AÑADIDOS PARA LAS ESTADÍSTICAS

	// Método para obtener usuarios ordenados por puntos totales
	@GetMapping("/sorted-by-points")
	public ResponseEntity<List<UserStats>> getUsersSortedByPoints() {
		List<UserStats> usersByPoints = userService.getUsersSortedByPoints();
		return new ResponseEntity<>(usersByPoints, HttpStatus.OK);
	}

	// Método para obtener usuarios ordenados por porcentaje de victorias
	@GetMapping("/sorted-by-win-percentage")
	public ResponseEntity<List<UserStats>> getUsersSortedByWinPercentage() {
		List<UserStats> usersByWinPercentage = userService.getUsersSortedByWinPercentage();
		return new ResponseEntity<>(usersByWinPercentage, HttpStatus.OK);
	}

	// Añadido para facilitar el editar perfil
	@GetMapping("/current")
	public ResponseEntity<User> findCurrentUserProfile() {
    	User currentUser = userService.findCurrentUser();
    	return new ResponseEntity<>(currentUser, HttpStatus.OK);
	}

	@GetMapping("/current/jugadores")
	public ResponseEntity<List<Jugador>> getJugadoresByCurrentUser() {
    	List<Jugador> jugadores = userService.getJugadoresByCurrentUser();
    	return new ResponseEntity<>(jugadores, HttpStatus.OK);
	}

	@PutMapping(value="conectarODesconectar/{userId}/{conectar}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> aceptarORechazarSolicitud(
        @PathVariable("userId")Integer userId,
        @PathVariable("conectar") Boolean conectar ){
		return new ResponseEntity<>(userService.conectarseODesconectarse(userId, conectar), HttpStatus.OK);
    }


}
