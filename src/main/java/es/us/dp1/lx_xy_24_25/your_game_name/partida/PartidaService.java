package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UsuarioPartidaEnJuegoEsperandoException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.ApuestaNoValidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.MinJugadoresPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.MismoNombrePartidaNoTerminadaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.NoPuedeApostarException;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PartidaService {

    private static final Logger logger = LoggerFactory.getLogger(PartidaService.class);

    PartidaRepository partidaRepository;
    RondaService rondaService;
    JugadorService jugadorService;
    UserService userService;
    // Añadir a Autowired cuando esté todo
    BazaService bazaService;
    ManoService manoService;
    RondaRepository rondaRepository;
    private static final int ULTIMA_RONDA = 10;
    private SimpMessagingTemplate messagingTemplate;


    @Autowired
    public PartidaService(PartidaRepository partidaRepository, RondaService rondaService, JugadorService jugadorService, 
    UserService userService, RondaRepository rondaRepository, BazaService bazaService, ManoService manoService, SimpMessagingTemplate messagingTemplate) {
        this.partidaRepository = partidaRepository;
        this.rondaService = rondaService;
        this.jugadorService = jugadorService;
        this.userService = userService;
        this.rondaRepository = rondaRepository;
        this.bazaService = bazaService;
        this.manoService = manoService;
        this.messagingTemplate = messagingTemplate;
    }

    // Con este método se puede filtrar por nombre y estado
    @Transactional(readOnly=true)
    public List<Partida> getAllPartidas(String nombre, PartidaEstado estado) throws DataAccessException{
        if(nombre != null && estado != null){
            return partidaRepository.findByNombreAndEstado(nombre, estado);
        } else if(nombre != null){
            return partidaRepository.findByNombre(nombre);
        } else if(estado != null){
            return partidaRepository.findByEstado(estado);
        } else {
            Iterable<Partida> iterablePartidas = partidaRepository.findAll();
            List<Partida> listaPartidas = new ArrayList<>(); 
            iterablePartidas.forEach(listaPartidas::add);
            return listaPartidas;
        }
    }

    @Transactional(readOnly = true)
    public Partida getPartidaById(Integer id) throws DataAccessException{
        Optional<Partida> partida = partidaRepository.findById(id);
        if(partida.isPresent()){
            return partida.get();
        } else{
            throw new ResourceNotFoundException("Partida", "id", id);
        }
    }
    
    @Transactional
    public Partida save(Partida p) throws DataAccessException {
        Integer ownerId = p.getOwnerPartida();
        boolean partidaEsperandoJugando = usuarioPartidaEnJuegoEsperando(ownerId);
        if(mismoNombrePartidaNoTerminada(p)){
            throw new MismoNombrePartidaNoTerminadaException("Ya existe una partida no finalizada con ese nombre.");
        } if (partidaEsperandoJugando) {
            throw new UsuarioPartidaEnJuegoEsperandoException("No puede crear otra partida, ya tiene una en espera o en juego.");
        } else if(usuarioJugadorEnPartida(p)){
            throw new UsuarioPartidaEnJuegoEsperandoException("No puede crear una partida, ya tiene una en espera o en juego.");
        }
        return partidaRepository.save(p);
    }


    @Transactional
    public Partida update(@Valid Partida partida, Integer idToUpdate) throws DataAccessException{
        Partida toUpdate = getPartidaById(idToUpdate);
        BeanUtils.copyProperties(partida, toUpdate, "id");
        partidaRepository.save(toUpdate);
        return toUpdate;
    }

    @Transactional
    public void delete(Integer id) throws DataAccessException{
        Partida partida = partidaRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Partida no encontrada.")
        );
    
        // Eliminar jugadores asociados a la partida
        List<Jugador> jugadores = jugadorService.findJugadoresByPartidaId(id);
        if (!jugadores.isEmpty()) {
            for (Jugador jugador : jugadores) {
                jugadorService.deleteJugador(jugador.getId(),false);
            }
        }
        
        // Eliminar dependencias de la partida
		rondaRepository.deleteByPartidaId(id);
    
        partidaRepository.delete(partida);
    }

    public List<Partida> findPartidasByOwnerId(Integer ownerId) {
        return partidaRepository.findByOwnerPartida(ownerId);
    }
    
    // Hacer test
    public void actualizarOwner(Integer partidaId, Integer nuevoOwnerId) {
        Partida partida = getPartidaById(partidaId);

        if (nuevoOwnerId == null || nuevoOwnerId <= 0) {
            throw new IllegalArgumentException("El nuevo ownerPartida debe ser un ID válido.");
        }

        partida.setOwnerPartida(nuevoOwnerId);
        partidaRepository.save(partida);

        Map<String, Object> message = new HashMap<>();
        message.put("status", "ACTUALIZADO");
        message.put("ownerPartida", nuevoOwnerId);
        message.put("jugadores", jugadorService.findJugadoresByPartidaId(partidaId));

        messagingTemplate.convertAndSend("/topic/partida/" + partidaId, message);
    }


    // Lógica de juego

    // Iniciamos la partida
    @Transactional
    public void iniciarPartida(Integer partidaId) {
        logger.info("Iniciando la partida con ID: {}", partidaId);

        Partida partida = getPartidaById(partidaId);
        if (partida == null) {
            logger.error("No se encontró la partida con ID: {}", partidaId);
            throw new ResourceNotFoundException("Partida", "id", partidaId);
        }

        List<Jugador> jugadoresPartida = jugadorService.findJugadoresByPartidaId(partidaId);
        if (jugadoresPartida.size() < 3) {
            logger.warn("Intento de iniciar partida con menos de 3 jugadores. ID: {}", partidaId);
            throw new MinJugadoresPartidaException("Tiene que haber un mínimo de 3 jugadores en la sala para empezar la partida");
        }

        partida.setEstado(PartidaEstado.JUGANDO);
        update(partida, partidaId);

        logger.info("La partida con ID {} ha cambiado su estado a JUGANDO", partidaId);

        Ronda ronda = rondaService.iniciarRonda(partida);
        manoService.iniciarManos(partida.getId(), ronda, jugadoresPartida);
        Baza baza = bazaService.iniciarBaza(ronda, jugadoresPartida);

        partida.setTurnoActual(primerTurno(baza.getTurnos()));
        update(partida, partida.getId());

        logger.info("Turno inicial asignado en la partida con ID: {}", partidaId);

        Map<String, Object> message = new HashMap<>();
        message.put("status", "JUGANDO"); // Estado de la partida

        messagingTemplate.convertAndSend("/topic/partida/" + partidaId, message);

        logger.info("Notificación enviada para la partida con ID: {}", partidaId);
    }

    // Finalizamos la partida
    @Transactional
    public void finalizarPartida(Integer partidaId) {
        logger.info("Intentando finalizar la partida con ID: {}", partidaId);

        Partida partida = getPartidaById(partidaId);
        if (partida == null) {
            // logger.error("Partida con ID {} no encontrada. No se puede finalizar.", partidaId);
            throw new ResourceNotFoundException("Partida", "id", partidaId);
        }

        partida.setEstado(PartidaEstado.TERMINADA);
        partida.setFin(LocalDateTime.now());
        logger.info("La partida con ID {} ha sido marcada como TERMINADA.", partidaId);

        Integer puntosGanador = null;
        List<Jugador> jugadoresPartida = jugadorService.findJugadoresByPartidaId(partidaId);
        for (Jugador jugador : jugadoresPartida) {
            User usuarioJugador = jugador.getUsuario();
            if (usuarioJugador.getNumPuntosGanados() == null) {
                usuarioJugador.setNumPuntosGanados(0);
            }
            if (usuarioJugador.getNumPartidasJugadas() == null) {
                usuarioJugador.setNumPartidasJugadas(0);
            }

            usuarioJugador.setNumPuntosGanados(usuarioJugador.getNumPuntosGanados() + jugador.getPuntos());
            usuarioJugador.setNumPartidasJugadas(usuarioJugador.getNumPartidasJugadas() + 1);
            userService.saveUser(usuarioJugador);

            logger.info("Estadísticas actualizadas para el jugador {}: Puntos totales: {}, Partidas jugadas: {}",
                    usuarioJugador.getUsername(),
                    usuarioJugador.getNumPuntosGanados(),
                    usuarioJugador.getNumPartidasJugadas());

            if (puntosGanador == null || jugador.getPuntos() > puntosGanador) {
                puntosGanador = jugador.getPuntos();
            }
        }

        Integer puntosFinalGanador = puntosGanador;
        List<User> ganadores = jugadoresPartida.stream()
                .filter(j -> j.getPuntos().equals(puntosFinalGanador))
                .map(Jugador::getUsuario)
                .collect(Collectors.toList());

        partida.setGanadores(ganadores.stream().map(u-> u.getUsername()).collect(Collectors.toList()));


        for (User u : ganadores) {
            if (u.getNumPartidasGanadas() == null) {
                u.setNumPartidasGanadas(0);
            }
            u.setNumPartidasGanadas(u.getNumPartidasGanadas() + 1);
            userService.saveUser(u);
            logger.info("El usuario {} ha ganado la partida y ahora tiene {} partidas ganadas.",
                    u.getUsername(),
                    u.getNumPartidasGanadas());
        }

        update(partida, partidaId);
        logger.info("La partida con ID {} ha sido finalizada y actualizada en la base de datos.", partidaId);

        Map<String, Object> message = new HashMap<>();
        message.put("status", "FINALIZADA"); // Estado de la partida
        message.put("ganadores", partida.getGanadores()); // Ganadores de la partida

        // Enviar el mensaje a través de WebSocket
        messagingTemplate.convertAndSend("/topic/partida/" + partidaId, message);
        logger.info("Se envió notificación de finalización de partida con ID {} a través de WebSocket.", partidaId);
    }
    // Para Excepción: Si ya tiene una partida creada en juego o esperando, no podrá crear otra partida
    public Boolean usuarioPartidaEnJuegoEsperando(Integer ownerId){
        List<Partida> partidasEnProgresoEsperando = partidaRepository.findByOwnerPartidaAndEstado(ownerId, List.of(PartidaEstado.ESPERANDO, PartidaEstado.JUGANDO));
        return !partidasEnProgresoEsperando.isEmpty();
    }

    // Excepción: Para que no pueda crear una partida si ya se ha unido a una en juego o esperando (TODO: PROBAR)
    public Boolean usuarioJugadorEnPartida(Partida partidaCrear) throws DataAccessException{
        Boolean lanzarExcepcion = false;
        Iterable<Jugador> jugadores = jugadorService.findAll();
        List<Jugador> jugadoresFiltrados = StreamSupport.stream(jugadores.spliterator(), false)
        .filter(jugador -> jugador.getUsuario().getId() == partidaCrear.getOwnerPartida() 
        && (jugador.getPartida().getEstado().equals(PartidaEstado.ESPERANDO) 
            || jugador.getPartida().getEstado().equals(PartidaEstado.JUGANDO)))
            .collect(Collectors.toList());
        if(jugadoresFiltrados.size() > 0){
            lanzarExcepcion = true;
        }
        return lanzarExcepcion;
    }

    // Excepción: No puede haber dos partidas (no finalizadas) con el mismo nombre TODO: PPROBAR
    public Boolean mismoNombrePartidaNoTerminada(Partida partidaCrear) throws DataAccessException{
        Boolean lanzarExcepcion = false;
        List<Partida> partidasFiltradasEsperando = partidaRepository.findByNombreAndEstado(partidaCrear.getNombre(), PartidaEstado.ESPERANDO);
        List<Partida> partidasFiltradasJugando = partidaRepository.findByNombreAndEstado(partidaCrear.getNombre(), PartidaEstado.JUGANDO);
        if(partidasFiltradasEsperando.size() > 0 || partidasFiltradasJugando.size() > 0){
            lanzarExcepcion = true;
        }
        return lanzarExcepcion;
    }

    public Jugador getJugadorGanador(Integer partidaId){
        List<Jugador> jugadores = jugadorService.findJugadoresByPartidaId(partidaId);
        Jugador ganador = jugadores.stream().max(Comparator.comparing(j -> j.getPuntos())).get();
        return ganador;
    }

    // Este método se llamará desde el frontend cuando sea el último truco de una baza (condición comprobada en frontend)
    @Transactional
    public void siguienteEstado(Integer partidaId, Integer bazaId){ 
        Partida partida = getPartidaById(partidaId);
        List<Jugador> jugadores = jugadorService.findJugadoresByPartidaId(partidaId);
        Baza baza = bazaService.findById(bazaId);
        Integer nextBaza = baza.getNumBaza() + 1;
        Ronda ronda = baza.getRonda();
        messagingTemplate.convertAndSend("/topic/listaTrucos/partida/" + partidaId, new ArrayList<>());
        manoService.actualizarResultadoMano(baza);
        enviarResultadosMano(jugadores, partida.getId());
        messagingTemplate.convertAndSend("/topic/nuevasManos/partida/" + partida.getId(), manoService.findAllManosByRondaId(ronda.getId()));
        if(nextBaza > ronda.getNumBazas()){
            rondaService.finalizarRonda(ronda.getId());
            getPuntaje(ronda.getNumBazas(), ronda.getId());
            Integer nextRonda = ronda.getNumRonda() + 1;
            // messagingTemplate.convertAndSend("/topic/esUltimaBaza/partida/" + partidaId, true);
            // Si es la última ronda, finalizamos partida
            if(nextRonda > ULTIMA_RONDA){
                finalizarPartida(ronda.getPartida().getId());
            // Si no, pasamos de ronda, iniciamos sus manos e iniciamos la primera baza
            } else{
                Integer numJugadores = jugadores.size(); 
                Integer numBazas = manoService.getNumCartasARepartir(nextRonda, numJugadores);
                Ronda newRonda = rondaService.nextRonda(ronda.getId(), numBazas);
                messagingTemplate.convertAndSend("/topic/nuevaRonda/partida/" + partidaId, rondaService.rondaActual(partidaId));
                manoService.iniciarManos(ronda.getPartida().getId(), newRonda, jugadores);
                messagingTemplate.convertAndSend("/topic/nuevasManos/partida/" + partidaId, manoService.findAllManosByRondaId(newRonda.getId()));
                enviarResultadosMano(jugadores, partidaId);
                apuestasJugadoresNegativas(jugadores);
                Baza primeraBaza = bazaService.iniciarBaza(newRonda, jugadores);
                // Renovar baza
                messagingTemplate.convertAndSend("/topic/nuevaBaza/partida/" + partidaId, bazaService.findBazaActualByRondaId(newRonda.getId()));

                partida.setTurnoActual(primerTurno(primeraBaza.getTurnos()));
                update(partida, partida.getId());
                messagingTemplate.convertAndSend("/topic/turnoActual/" + partida.getId(), partida.getTurnoActual());
            }
        // Si no es la última baza de la ronda, cambiamos de baza
        } else{
            Baza newBaza = bazaService.nextBaza(bazaId, jugadores);
            messagingTemplate.convertAndSend("/topic/nuevaBaza/partida/" + partidaId, bazaService.findBazaActualByRondaId(ronda.getId()));
            // Actualizamos aquí el turno actual
            partida.setTurnoActual(primerTurno(newBaza.getTurnos()));
            update(partida, partida.getId());
            messagingTemplate.convertAndSend("/topic/turnoActual/" + partida.getId(), partida.getTurnoActual());
        }
    }

    @Transactional
	public Integer primerTurno(List<Integer> turnos){
		return turnos.get(0);
	}

    // Se ha movido aquí para evitar dependencias con Mano
    @Transactional
    public void getPuntaje(Integer numBazas, Integer rondaId){
         List<Mano> manos = manoService.findAllManosByRondaId(rondaId);
         for(Mano m:manos){
            Integer puntaje = 0;
            Jugador jugador = m.getJugador();
            if(m.getApuesta()==0){
                if(m.getApuesta().equals(m.getResultado())){
                    puntaje += 10*numBazas;
                }else{
                    puntaje -= 10*numBazas;
                }
            }else{
                if(m.getApuesta().equals(m.getResultado())){
                    // Los ptos de bonificacion solo se calcula si se acierta la apuesta
                    Integer ptosBonificacion = bazaService.getPtosBonificacion(rondaId, jugador.getId());
                    puntaje += 20*m.getApuesta() + ptosBonificacion;
                }else{
                    puntaje -= 10*Math.abs(m.getApuesta()-m.getResultado());
                } 
            }
            jugador.setPuntos(jugador.getPuntos() + puntaje);
            jugadorService.updateJugador(jugador, jugador.getId());
         }
    }

    // Para apostar
    @Transactional
    public void apuesta(Integer ap, Integer jugadorId){
        Mano mano = manoService.findLastManoByJugadorId(jugadorId);
        Jugador jugador = jugadorService.findById(jugadorId);
        Partida partida = jugador.getPartida();
        messagingTemplate.convertAndSend("/topic/turnoActual/" + partida.getId(), partida.getTurnoActual());
        if (mano == null) {
            throw new ResourceNotFoundException("Mano", "id", jugadorId);
        }

        if (ap > mano.getCartas().size()) {
            throw new ApuestaNoValidaException("La apuesta no puede ser mayor a " + mano.getCartas().size());
        }

        Map<Integer, Integer> apuestasJugadores = apuestasJugadores(partida.getId());
        Integer apuestaJugador = apuestasJugadores.get(jugadorId);
        if(apuestaJugador != -1) {
            throw new NoPuedeApostarException("Ya has apostado en esta ronda");
        }

        mano.setApuesta(ap);
        jugador.setApuestaActual(ap);
        manoService.saveMano(mano);
        jugadorService.updateJugador(jugador, jugadorId);
        messagingTemplate.convertAndSend("/topic/apuesta/partida/" + partida.getId(), jugadorService.findJugadoresByPartidaId(partida.getId()));
       
    }

    @Transactional
    public void apuestasJugadoresNegativas(List<Jugador> jugadores){
        for(Jugador j : jugadores){
            j.setApuestaActual(-1);
            jugadorService.updateJugador(j, j.getId());
        }
    }

    @Transactional
    public Map<Integer, Integer> apuestasJugadores(Integer partidaId) {
        List<Jugador> jugadores = jugadorService.findJugadoresByPartidaId(partidaId);
        Map<Integer, Integer> apuestas = new HashMap<>();
        for (Jugador j : jugadores) {
            apuestas.put(j.getId(), j.getApuestaActual());
        }
        return apuestas;
    }

    @Transactional
    public void enviarResultadosMano(List<Jugador> jugadores, Integer partidaId){
        Map<Integer, Integer> resultadosManos = new HashMap<Integer, Integer>();
        for(Jugador j: jugadores){
            Mano manoJugador = manoService.findLastManoByJugadorId(j.getId());
            resultadosManos.put(j.getId(), manoJugador.getResultado());
        }
        messagingTemplate.convertAndSend("/topic/resultadosMano/partida/" + partidaId, resultadosManos);
    }

}
