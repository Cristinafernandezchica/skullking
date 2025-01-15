package es.us.dp1.lx_xy_24_25.your_game_name.user;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista de todos los usuarios pudiendo filtrar por athority.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida"),
        @ApiResponse(responseCode = "404", description = "No se encontraron usuarios")
    })
    @GetMapping
    public ResponseEntity<List<User>> findAll(@RequestParam(required = false) String auth) {
        List<User> res;
        if (auth != null) {
            res = (List<User>) userService.findAllByAuthority(auth);
        } else {
            res = (List<User>) userService.findAll();
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Obtener todas las authorities", description = "Devuelve una lista de todas las authorities")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de authorities obtenida"),
    })
    @GetMapping("authorities")
    public ResponseEntity<List<Authorities>> findAllAuths() {
        List<Authorities> res = (List<Authorities>) authService.findAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Obtener un usuario por ID", description = "Devuelve un usuario con el ID proporcionado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario obtenido"),
        @ApiResponse(responseCode = "404", description = "No se encontró el usuario")
    })
    @GetMapping(value = "{id}")
    public ResponseEntity<User> findById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(userService.findUser(id), HttpStatus.OK);
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Crea un nuevo usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos del usuario no válidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> create(@RequestBody @Valid User user) {
        User savedUser = userService.saveUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un usuario existente", description = "Actualiza un usuario existente dado su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario actualizado con éxito"),
        @ApiResponse(responseCode = "404", description = "No se encontró el usuario")
    })
    @PutMapping(value = "{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<User> update(@PathVariable("userId") Integer id, @RequestBody @Valid User user) {
        RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);
        return new ResponseEntity<>(this.userService.updateUser(user, id), HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Eliminar un usuario", description = "Eliminar un usuario existente dado su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "No se encontró el usuario")
    })
    @DeleteMapping(value = "{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MessageResponse> delete(@PathVariable("userId") int id) {
        RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);
        userService.deleteUser(id);
        return new ResponseEntity<>(new MessageResponse("Usuario eliminado"), HttpStatus.OK);
    }

    @Operation(summary = "Obtener uduarios ordenados por puntos totales", description = "Devuelve una lista de los usuarios ordenados por puntos totales.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista usuarios por puntos obtenida")
    })
    @GetMapping("/sorted-by-points")
    public ResponseEntity<List<User>> getUsersSortedByPoints() {
        List<User> usersByPoints = userService.getUsersSortedByPoints();
        return new ResponseEntity<>(usersByPoints, HttpStatus.OK);
    }

    @Operation(summary = "Obtener usuarios ordenados por porcentaje de victorias", description = "Devuelve una lista de usuarios ordenados por porcentaje de victorias.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista usuarios por porcentaje de victorias obtenida")
    })
    @GetMapping("/sorted-by-win-percentage")
    public ResponseEntity<List<User>> getUsersSortedByWinPercentage() {
        List<User> usersByWinPercentage = userService.getUsersSortedByWinPercentage();
        return new ResponseEntity<>(usersByWinPercentage, HttpStatus.OK);
    }

    @Operation(summary = "Conectarse o desconectarse", description = "Permite a un usuario conectarse o desconectarse.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado del usuario actualizado con éxito"),
        @ApiResponse(responseCode = "401", description = "El usuario no está autenticado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
	@PutMapping(value="conectarODesconectar/{userId}/{conectar}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> aceptarORechazarSolicitud(
        @PathVariable("userId")Integer userId,
        @PathVariable("conectar") Boolean conectar ){
		return new ResponseEntity<>(userService.conectarseODesconectarse(userId, conectar), HttpStatus.OK);
    }

    @Operation(summary = "Obtener el perfil del usuario actual", description = "Devuelve el perfil del usuario actual autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil del usuario obtenido"),
        @ApiResponse(responseCode = "401", description = "El usuario no está autenticado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/current")
    public ResponseEntity<User> findCurrentUserProfile() {
        User currentUser = userService.findCurrentUser();
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }

    @Operation(summary = "Obtener los jugadores del usuario actual", description = "Devuelve la lista de jugadores del usuario actual autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jugadores del usuario obtenidos"),
        @ApiResponse(responseCode = "401", description = "El usuario no está autenticado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/current/jugadores")
    public ResponseEntity<List<Jugador>> getJugadoresByCurrentUser() {
        List<Jugador> jugadores = userService.getJugadoresByCurrentUser();
        return new ResponseEntity<>(jugadores, HttpStatus.OK);
    }

    @Operation(summary = "Obtener promedio de tiempo de partidas", description = "Devuelve el promendio de tiempo de todas las partidas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promedio de tiempo de partidas obtenido")
    })
    @GetMapping("/promedio-tiempo-partidas")
    public ResponseEntity<Double> getPromedioTiempoPartidas() {
        Double promedio = userService.getPromedioTiempoPartidas(null);
        return new ResponseEntity<>(promedio, HttpStatus.OK);
    }

    @Operation(summary = "Obtener promedio tiempo partidas por usuario", description = "Devuelve el promedio de tiempo de todas las partidas de un usuaio concreto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promedio de tiempo de partidas obtenido"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}/promedio-tiempo-partidas")
    public ResponseEntity<Double> getPromedioTiempoPartidasByUserId(@PathVariable Integer userId) {
        Double promedio = userService.getPromedioTiempoPartidas(userId);
        return new ResponseEntity<>(promedio, HttpStatus.OK);
    }

    @Operation(summary = "Obtener el tiempo máximo de partidas", description = "Devuelve el tiempo máximo de todas las partidas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tiempo máximo de partidas obtenido")
    })
    @GetMapping("/max-tiempo-partidas")
    public ResponseEntity<Integer> getMaxTiempoPartidas() {
        Integer max = userService.getMaxTiempoPartidas(null);
        return new ResponseEntity<>(max, HttpStatus.OK);
    }

    @Operation(summary = "Obtener el tiempo máximo partidas por usuario", description = "Devuelve el tiempo máximo de partidas de un usuario concreto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tiempo máximo de partidas obtenido"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}/max-tiempo-partidas")
    public ResponseEntity<Integer> getMaxTiempoPartidasByUserId(@PathVariable Integer userId) {
        Integer max = userService.getMaxTiempoPartidas(userId);
        return new ResponseEntity<>(max, HttpStatus.OK);
    }

    @Operation(summary = "Obtener el tiempo mínimo de partidas", description = "Devuelve el tiempo mínimo de todas las partidas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tiempo mínimo de partidas obtenido")
    })
    @GetMapping("/min-tiempo-partidas")
    public ResponseEntity<Integer> getMinTiempoPartidas() {
        Integer min = userService.getMinTiempoPartidas(null);
        return new ResponseEntity<>(min, HttpStatus.OK);
    }

    @Operation(summary = "Obtener el tiempo mínimo partidas por usuario", description = "Devuelve el tiempo mínimo de partidas de un usuario concreto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tiempo mínimo de partidas obtenido"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}/min-tiempo-partidas")
    public ResponseEntity<Integer> getMinTiempoPartidasByUserId(@PathVariable Integer userId) {
        Integer min = userService.getMinTiempoPartidas(userId);
        return new ResponseEntity<>(min, HttpStatus.OK);
    }

    @Operation(summary = "Obtener el tiempo total de partidas", description = "Devuelve el tiempo total de todas las partidas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tiempo total de partidas obtenido")
    })
    @GetMapping("/total-tiempo-partidas")
    public ResponseEntity<Integer> getTotalTiempoPartidas() {
        Integer total = userService.getTotalTiempoPartidas(null);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @Operation(summary = "Obtener el tiempo total de partidas por usuario", description = "Devuelve el tiempo total de partidas de un usuario concreto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tiempo total de partidas obtenido"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}/total-tiempo-partidas")
    public ResponseEntity<Integer> getTotalTiempoPartidasByUserId(@PathVariable Integer userId) {
        Integer total = userService.getTotalTiempoPartidas(userId);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @Operation(summary = "Obtener el promedio de partidas", description = "Devuelve el promedio de partidas jugadas por usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promedio de partidas obtenido")
    })
    @GetMapping("/promedio-partidas")
    public ResponseEntity<Double> getPromedioPartidas() {
        Double promedio = userService.getPromedioPartidas();
        return new ResponseEntity<>(promedio, HttpStatus.OK);
    }

    @Operation(summary = "Obtener el máximo de partidas", description = "Devuelve el máximo de partidas jugadas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Máximo de partidas obtenido")
    })
    @GetMapping("/max-partidas")
    public ResponseEntity<Integer> getMaxPartidas() {
        Integer max = userService.getMaxPartidas();
        return new ResponseEntity<>(max, HttpStatus.OK);
    }

    @Operation(summary = "Obtener el mínimo de partidas", description = "Devuelve el mínimo de partidas jugadas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mínimo de partidas obtenido")
    })
    @GetMapping("/min-partidas")
    public ResponseEntity<Integer> getMinPartidas() {
        Integer min = userService.getMinPartidas();
        return new ResponseEntity<>(min, HttpStatus.OK);
    }

}
