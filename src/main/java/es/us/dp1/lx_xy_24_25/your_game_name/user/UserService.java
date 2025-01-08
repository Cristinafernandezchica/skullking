/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.us.dp1.lx_xy_24_25.your_game_name.user;

import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorRepository;

@Service
public class UserService {

	private UserRepository userRepository;	
	private JugadorRepository jugadorRepository;	

	private final ConcurrentHashMap<Integer, UserStats> userStatsMap = new ConcurrentHashMap<>();

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
		
	}

	@Transactional
	public User saveUser(User user) throws DataAccessException {
    User savedUser = userRepository.save(user);
	// Save realizado antes para que user.getId() tenga un valor antes de que el usuario sea guardado en la base de datos, 
	// computeIfAbsent no contempla el null ( si nos pasa en algún lado algo asi, por si acaso, meto una excepcion ahi)
    if (savedUser.getId() == null) {
        throw new IllegalStateException("El usuario guardado no tiene aún un ID generado");
    }

    UserStats stats = userStatsMap.computeIfAbsent(savedUser.getId(), key -> new UserStats());
    stats.setNumPartidasJugadas(user.getNumPartidasJugadas());
    stats.setNumPartidasGanadas(user.getNumPartidasGanadas());
    stats.setNumPuntosGanados(user.getNumPuntosGanados());

    return savedUser;
}


	@Transactional(readOnly = true)
	public User findUser(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
				
		UserStats stats = userStatsMap.computeIfAbsent(user.getId(), key -> new UserStats());
		user.setNumPartidasJugadas(stats.getNumPartidasJugadas());
		user.setNumPartidasGanadas(stats.getNumPartidasGanadas());
		user.setNumPuntosGanados(stats.getNumPuntosGanados());
		return user;
	}

	@Transactional(readOnly = true)
	public User findUser(Integer id) {
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        UserStats stats = userStatsMap.computeIfAbsent(user.getId(), key -> new UserStats());
        user.setNumPartidasJugadas(stats.getNumPartidasJugadas());
        user.setNumPartidasGanadas(stats.getNumPartidasGanadas());
        user.setNumPuntosGanados(stats.getNumPuntosGanados());
        return user;
    }	

	@Transactional(readOnly = true)
	public User findCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			throw new ResourceNotFoundException("Nobody authenticated!");
		else
			return userRepository.findByUsername(auth.getName())
					.orElseThrow(() -> new ResourceNotFoundException("User", "Username", auth.getName()));
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
		User toDelete = findUser(id);
//		deleteRelations(id, toDelete.getAuthority().getAuthority());
//		this.userRepository.deletePlayerRelation(id);
		this.userRepository.delete(toDelete);
	}

	// Método para obtener usuarios ordenados por puntos totales
	@Transactional(readOnly = true)
	public List<UserStats> getUsersSortedByPoints() {
		List<User> users = (List<User>) findAll();
		return users.stream()
				.map(user -> {
					UserStats stats = userStatsMap.getOrDefault(user.getId(), new UserStats());
					stats.setNumPuntosGanados(user.getNumPuntosGanados());
					return stats;
				})
				.sorted((u1, u2) -> u2.getNumPuntosGanados() - u1.getNumPuntosGanados())
				.collect(Collectors.toList());
	}

	// Nuevo método: Obtener usuarios ordenados por porcentaje de victorias
	@Transactional(readOnly = true)
	public List<UserStats> getUsersSortedByWinPercentage() {
		List<User> users = (List<User>) findAll();
		return users.stream()
				.map(user -> {
					UserStats stats = userStatsMap.getOrDefault(user.getId(), new UserStats());
					int partidasJugadas = stats.getNumPartidasJugadas() != null ? stats.getNumPartidasJugadas() : 0;
					int partidasGanadas = stats.getNumPartidasGanadas() != null ? stats.getNumPartidasGanadas() : 0;
					double porcentajeVictorias = (partidasJugadas > 0)
							? (double) partidasGanadas / partidasJugadas * 100
							: 0.0;
					stats.setWinPercentage(porcentajeVictorias);
					return stats;
				})
				.sorted((u1, u2) -> Double.compare(u2.getWinPercentage(), u1.getWinPercentage()))
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
}

class UserStats {
	private Integer numPartidasJugadas = 0;
	private Integer numPartidasGanadas = 0;
	private Integer numPuntosGanados = 0;
	private Double winPercentage = 0.0;

	// Getters y setters
	public Integer getNumPartidasJugadas() {
		return numPartidasJugadas;
	}

	public void setNumPartidasJugadas(Integer numPartidasJugadas) {
		this.numPartidasJugadas = numPartidasJugadas;
	}

	public Integer getNumPartidasGanadas() {
		return numPartidasGanadas;
	}

	public void setNumPartidasGanadas(Integer numPartidasGanadas) {
		this.numPartidasGanadas = numPartidasGanadas;
	}

	public Integer getNumPuntosGanados() {
		return numPuntosGanados;
	}

	public void setNumPuntosGanados(Integer numPuntosGanados) {
		this.numPuntosGanados = numPuntosGanados;
	}

	public Double getWinPercentage() {
		return winPercentage;
	}

	public void setWinPercentage(Double winPercentage) {
		this.winPercentage = winPercentage;
	}
}
