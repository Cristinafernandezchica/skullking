package es.us.dp1.lx_xy_24_25.your_game_name.user;

import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.amistad.AmistadRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {

	private UserRepository userRepository;	
	private JugadorRepository jugadorRepository;	
	private JugadorService jugadorService;
    private AmistadRepository amistadRepository;

	@Autowired
	public UserService(UserRepository userRepository, JugadorRepository jugadorRepository, 
    JugadorService jugadorService, AmistadRepository amistadRepository) {
		this.userRepository = userRepository;
		this.jugadorRepository = jugadorRepository;
        this.jugadorService = jugadorService;
        this.amistadRepository = amistadRepository;
	}

    @Transactional
    public User saveUser(User user) throws DataAccessException {
        User savedUser = userRepository.save(user);
        // Save realizado antes para que user.getId() tenga un valor antes de que el usuario sea guardado en la base de datos, 
        // computeIfAbsent no contempla el null ( si nos pasa en algún lado algo asi, por si acaso, meto una excepcion ahi)
        if (savedUser.getId() == null) {
            throw new IllegalStateException("El usuario guardado no tiene aún un ID generado");
        }
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    @Transactional(readOnly = true)
    public User findUser(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional(readOnly = true)
    public User findCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new ResourceNotFoundException("Nobody authenticated!");
        }
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", auth.getName()));
    }

    public Boolean existsUser(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public Iterable<User> findAllByAuthority(String auth) {
        return userRepository.findAllByAuthority(auth);
    }

    @Transactional
    public User updateUser(@Valid User user, Integer idToUpdate) {
        User toUpdate = findUser(idToUpdate);
        BeanUtils.copyProperties(user, toUpdate, "id");
        userRepository.save(toUpdate);
		return toUpdate;
    }

	@Transactional
	public void deleteUser(Integer id) {
		User user = findUser(id);

		// Eliminar jugadores y partidas asociadas directamente desde el repositorio
		jugadorRepository.deletePartidasByOwner(user.getId());

		List<Jugador> jugadores = jugadorRepository.findJugadoresByUsuarioId(id);
		if (!jugadores.isEmpty()) {
			for (Jugador jugador : jugadores) {
				jugadorService.deleteJugador(jugador.getId(), false);
			}
		}

        // Eliminar relaciones de amistad donde el usuario sea remitente o destinatario
        amistadRepository.deleteByRemitenteOrDestinatario(user);

		// Finalmente, eliminar al usuario
		userRepository.delete(user);
	}

    // Método para obtener usuarios ordenados por puntos totales (Ranking)
    @Transactional(readOnly = true)
    public List<User> getUsersSortedByPoints() {
        List<User> users = (List<User>) findAll();
        return users.stream()
                .filter(u -> !u.hasAuthority("ADMIN"))
                .sorted((u1, u2) -> Integer.compare(
                        Optional.ofNullable(u2.getNumPuntosGanados()).orElse(0),
                        Optional.ofNullable(u1.getNumPuntosGanados()).orElse(0)))
                .collect(Collectors.toList());
    }

    // Nuevo método: Obtener usuarios ordenados por porcentaje de victorias (Ranking)
    @Transactional(readOnly = true)
    public List<User> getUsersSortedByWinPercentage() {
        List<User> users = (List<User>) findAll();
        return users.stream()
                .filter(u -> !u.hasAuthority("ADMIN"))
                .sorted((u1, u2) -> {
                    double percentage1 = (Optional.ofNullable(u1.getNumPartidasJugadas()).orElse(0) > 0)
                            ? (double) Optional.ofNullable(u1.getNumPartidasGanadas()).orElse(0)
                            / Optional.ofNullable(u1.getNumPartidasJugadas()).orElse(0)
                            : 0.0;
                    double percentage2 = (Optional.ofNullable(u2.getNumPartidasJugadas()).orElse(0) > 0)
                            ? (double) Optional.ofNullable(u2.getNumPartidasGanadas()).orElse(0)
                            / Optional.ofNullable(u2.getNumPartidasJugadas()).orElse(0)
                            : 0.0;
                    return Double.compare(percentage2, percentage1);
                })
                .collect(Collectors.toList());
    }

	@Transactional
	public User conectarseODesconectarse(Integer userId, Boolean conectado){
		User usuarioAUpdatear= userRepository.findById(userId).orElse(null);
		if(usuarioAUpdatear==null){
			throw new ResourceNotFoundException("User", "id", userId);
		}
		usuarioAUpdatear.setConectado(conectado);
		return userRepository.save(usuarioAUpdatear);
	}

	// Método que devuelve los jugadores asociados a un usuario.
	@Transactional(readOnly = true)
	public List<Jugador> getJugadoresByCurrentUser() {
		User currentUser = findCurrentUser();
		return jugadorRepository.findJugadoresByUsuarioId(currentUser.getId());
	}

    // Métodos para obtener una lista con la duración de cada partida (Estadísticas)
    @Transactional
    public List<Long> getTiempoPartidas(Integer usuarioId) {
        List<Long> tiempoPartidas;
        // Si se pasa un ID de usuario
        if(usuarioId != null) {
            List<Jugador> jugadoresPorUsuario = jugadorService.findJugadoresByUsuarioId(usuarioId);
            
            // Filtramos solo las partidas terminadas
            tiempoPartidas = jugadoresPorUsuario.stream()
                .filter(j -> j.getPartida().getEstado().equals(PartidaEstado.TERMINADA))  // Solo partidas terminadas
                .map(j -> {
                    LocalDateTime inicio = j.getPartida().getInicio();
                    LocalDateTime fin = j.getPartida().getFin() != null ? j.getPartida().getFin() : LocalDateTime.now();  // Si fin es null, usamos el tiempo actual

                    // Convertimos las fechas a segundos desde el epoch (usando UTC)
                    long inicioEnSegundos = inicio.toEpochSecond(java.time.ZoneOffset.UTC);
                    long finEnSegundos = fin.toEpochSecond(java.time.ZoneOffset.UTC);

                    // Calculamos la diferencia en segundos
                    return finEnSegundos - inicioEnSegundos;
                })
                .collect(Collectors.toList());
        } else {
            // Si no se pasa un ID de usuario, se calculan las partidas de todos los jugadores
            List<Jugador> jugadores = new ArrayList<Jugador>();
            for (Jugador j: jugadorService.findAll()){
                jugadores.add(j);
            }
            List<Partida> partidas = jugadores.stream()
                .filter(j -> j.getPartida().getEstado().equals(PartidaEstado.TERMINADA))  // Solo partidas terminadas
                .map(j -> j.getPartida())
                .collect(Collectors.toMap(
                    Partida::getNombre,  // Usamos el nombre para garantizar la unicidad
                    partida -> partida,  // El valor es la partida misma
                    (partida1, partida2) -> partida1  // En caso de duplicados, se elige una
                ))
                .values()  // Extraemos solo los valores (las partidas)
                .stream()
                .collect(Collectors.toList());  // Convertimos de nuevo a una lista 
            
            // Mapeo y cálculo del tiempo de las partidas terminadas
            tiempoPartidas = partidas.stream()
                .map(p -> {
                    LocalDateTime inicio = p.getInicio();
                    LocalDateTime fin = p.getFin() != null ? p.getFin() : LocalDateTime.now();  // Si fin es null, se usa el tiempo actual

                    // Convertimos las fechas a segundos desde el epoch (usando UTC)
                    long inicioEnSegundos = inicio.toEpochSecond(java.time.ZoneOffset.UTC);
                    long finEnSegundos = fin.toEpochSecond(java.time.ZoneOffset.UTC);

                    // Calculamos la diferencia en segundos
                    return finEnSegundos - inicioEnSegundos;
                })
                .collect(Collectors.toList());
        }

        return tiempoPartidas;
    }

    // Métodos para obtener un Map con la duración media de una partida, duración máxima y mínima de una partida y tiempo total invertida en jugar partidas (Estadísticas)
    @Transactional
    public Double getPromedioTiempoPartidas(Integer usuarioId) {
        // Obtener la lista de tiempos en segundos
        List<Long> tiempoPartidas = getTiempoPartidas(usuarioId);

        // Calcular tiempo promedio de las partidas
        Double promedio = tiempoPartidas.stream()
            .mapToLong(Long::longValue)
            .map(x -> x/60)
            .average()                 
            .orElse(0.0);              
        return promedio;
    }

    @Transactional
    public Integer getMaxTiempoPartidas(Integer usuarioId) {
        List<Long> tiempoPartidas = getTiempoPartidas(usuarioId);

        Long max = tiempoPartidas.stream()
            .mapToLong(Long::longValue)
            .map(x -> x/60)
            .max()
            .orElse(0L);
        return max.intValue();
    }

    @Transactional
    public Integer getMinTiempoPartidas(Integer usuarioId) {
        List<Long> tiempoPartidas = getTiempoPartidas(usuarioId);

        Long min = tiempoPartidas.stream()
            .mapToLong(Long::longValue)
            .map(x -> x/60)
            .min()
            .orElse(0L);
        return min.intValue();
    }

    @Transactional
    public Integer getTotalTiempoPartidas(Integer usuarioId) {
        List<Long> tiempoPartidas = getTiempoPartidas(usuarioId);

        Long total = tiempoPartidas.stream()
            .mapToLong(Long::longValue)
            .map(x -> x/60)
            .sum();
        return total.intValue();
    }

    // Métodos para obtener una lista con el número de partidas jugadas por cada usuario (Estadísticas)
    @Transactional
    public List<Integer> getNumPartidas() {
        // Convertir el Iterable<Integer> en una lista para poder trabajar con Streams
        List<Integer> numPartidas = StreamSupport.stream(findAll().spliterator(), false)
            .filter(u -> !(u.getAuthority().getAuthority().equals("ADMIN")))
            .map(u -> u.getNumPartidasJugadas() != null ? u.getNumPartidasJugadas() : 0)  // Asignar 0 si es null
            .collect(Collectors.toList());
    
        return numPartidas;
    }    

    // Métodos para obtener una Map con el número medio de partidas que juega un usuario, el número máximo y mínimo de partidas jugadas por un usuario (Estadísticas)
    @Transactional
    public Double getPromedioPartidas() {
        List<Integer> numPartidas = getNumPartidas();

        Double promedio = numPartidas.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        return promedio;
    }

    @Transactional
    public Integer getMaxPartidas() {
        List<Integer> numPartidas = getNumPartidas();

        Integer max = numPartidas.stream()
            .mapToInt(Integer::intValue)
            .max()
            .orElse(0);
        return max;
    }

    @Transactional
    public Integer getMinPartidas() {
        List<Integer> numPartidas = getNumPartidas();

        Integer min = numPartidas.stream()
            .mapToInt(Integer::intValue)
            .min()
            .orElse(0);
        return min;
    }

}