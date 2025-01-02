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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JugadorRepository jugadorRepository;

    @Autowired
    public UserService(UserRepository userRepository, JugadorRepository jugadorRepository) {
        this.userRepository = userRepository;
        this.jugadorRepository = jugadorRepository;
    }

    @Transactional
    public User saveUser(User user) throws DataAccessException {
        return userRepository.save(user);
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
        return userRepository.save(toUpdate);
    }

    @Transactional
    public void deleteUser(Integer id) {
        User toDelete = findUser(id);
        userRepository.delete(toDelete);
    }

    // Método para obtener usuarios ordenados por puntos totales
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

    // Nuevo método: Obtener usuarios ordenados por porcentaje de victorias
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

    // Método que devuelve los jugadores asociados a un usuario.
	@Transactional(readOnly = true)
	public List<Jugador> getJugadoresByCurrentUser() {
		User currentUser = findCurrentUser();
		return jugadorRepository.findJugadoresByUsuarioId(currentUser.getId());
	}
}