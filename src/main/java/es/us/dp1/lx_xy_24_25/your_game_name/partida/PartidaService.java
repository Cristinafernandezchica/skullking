package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.aspectj.bridge.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
import es.us.dp1.lx_xy_24_25.your_game_name.mano.exceptions.ApuestaNoValidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.MinJugadoresPartidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.exceptions.MismoNombrePartidaNoTerminadaException;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import jakarta.validation.Valid;

@Service
public class PartidaService {

    PartidaRepository pr;
    RondaService rondaService;
    JugadorService jugadorService;
    UserService us;
    // Añadir a Autowired cuando esté todo
    BazaService bazaService;
    ManoService manoService;
    private static final int ULTIMA_RONDA = 10;
    private SimpMessagingTemplate messagingTemplate;


    @Autowired
    public PartidaService(PartidaRepository pr, RondaService rondaService, JugadorService jugadorService, UserService us, BazaService bazaService, ManoService manoService, SimpMessagingTemplate messagingTemplate) {
        this.pr = pr;
        this.rondaService = rondaService;
        this.jugadorService = jugadorService;
        this.us = us;
        this.bazaService = bazaService;
        this.manoService = manoService;
        this.messagingTemplate = messagingTemplate;
    }

    // Con este método se puede filtrar por nombre y estado
    @Transactional(readOnly=true)
    public List<Partida> getAllPartidas(String nombre, PartidaEstado estado) throws DataAccessException{
        if(nombre != null && estado != null){
            return pr.findByNombreAndEstado(nombre, estado);
        } else if(nombre != null){
            return pr.findByNombre(nombre);
        } else if(estado != null){
            return pr.findByEstado(estado);
        } else {
            Iterable<Partida> iterablePartidas = pr.findAll();
            List<Partida> listaPartidas = new ArrayList<>(); 
            iterablePartidas.forEach(listaPartidas::add);
            return listaPartidas;
        }
    }

    @Transactional(readOnly = true)
    public Partida getPartidaById(Integer id) throws DataAccessException{
        Optional<Partida> partida = pr.findById(id);
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
        return pr.save(p);
    }


    @Transactional
    public Partida update(@Valid Partida partida, Integer idToUpdate) throws DataAccessException{
        Partida toUpdate = getPartidaById(idToUpdate);
        BeanUtils.copyProperties(partida, toUpdate, "id");
        pr.save(toUpdate);
        return toUpdate;
    }


    @Transactional
    public void delete(Integer id) throws DataAccessException{
        pr.deleteById(id);
    }

    // Lógica de juego

    // Inciamos la partida
    @Transactional
    public void iniciarPartida(Integer partidaId){
        Partida partida = getPartidaById(partidaId);
        if (partida == null) {
            throw new ResourceNotFoundException("Partida", "id", partidaId);
        }

        List<Jugador> jugadoresPartida = jugadorService.findJugadoresByPartidaId(partidaId);
        if(jugadoresPartida.size() < 3) {
            throw new MinJugadoresPartidaException("Tiene que haber un mínimo de 3 jugadores en la sala para empezar la partida");
        }

        partida.setEstado(PartidaEstado.JUGANDO);
        update(partida, partidaId);
        Ronda ronda = rondaService.iniciarRonda(partida);
        messagingTemplate.convertAndSend("/topic/nuevaRonda/partida/" + partidaId, rondaService.rondaActual(partidaId));
        manoService.iniciarManos(partida.getId(), ronda, jugadoresPartida);
        Baza baza = bazaService.iniciarBaza(ronda, jugadoresPartida);
        messagingTemplate.convertAndSend("/topic/nuevaBaza/partida/" + partidaId, bazaService.findBazaActualByRondaId(ronda.getId()));

        // Actualizamos turno actual
        partida.setTurnoActual(primerTurno(baza.getTurnos()));
        update(partida, partida.getId());

        // Crear el mensaje de notificación
        Map<String, Object> message = new HashMap<>();
        message.put("status", "JUGANDO"); // Estado de la partida
        message.put("partidaId", partidaId); // ID de la partida
        message.put("message", "Partida iniciada");

        // Enviar el mensaje a través de WebSocket
        messagingTemplate.convertAndSend("/topic/partida/" + partidaId, message);

    }

    // Finalizamos la partida
    @Transactional
    public void finalizarPartida(Integer partidaId) {
        Partida partida = getPartidaById(partidaId);
        if (partida == null) {
            throw new ResourceNotFoundException("Partida", "id", partidaId);
        }
        partida.setEstado(PartidaEstado.TERMINADA);
        partida.setFin(LocalDateTime.now());

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
            us.saveUser(usuarioJugador);

            if (puntosGanador == null || jugador.getPuntos() > puntosGanador) {
                puntosGanador = jugador.getPuntos();
            }
        }

        Integer puntosFinalGanador = puntosGanador;
        List<User> ganadores = jugadoresPartida.stream()
                .filter(j -> j.getPuntos().equals(puntosFinalGanador))
                .map(Jugador::getUsuario)
                .collect(Collectors.toList());
        for (User u : ganadores) {
            if (u.getNumPartidasGanadas() == null) {
                u.setNumPartidasGanadas(0);
            }
            u.setNumPartidasGanadas(u.getNumPartidasGanadas() + 1);
            us.saveUser(u);
        }
        update(partida, partidaId);
    }

    // Para Excepción: Si ya tiene una partida creada en juego o esperando, no podrá crear otra partida
    public Boolean usuarioPartidaEnJuegoEsperando(Integer ownerId){
        List<Partida> partidasEnProgresoEsperando = pr.findByOwnerPartidaAndEstado(ownerId, List.of(PartidaEstado.ESPERANDO, PartidaEstado.JUGANDO));
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
        List<Partida> partidasFiltradasEsperando = pr.findByNombreAndEstado(partidaCrear.getNombre(), PartidaEstado.ESPERANDO);
        List<Partida> partidasFiltradasJugando = pr.findByNombreAndEstado(partidaCrear.getNombre(), PartidaEstado.JUGANDO);
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

        // Si es la última Baza de la ronda, finalizamos la ronda y actualizamos el resultado de las manos
        if(nextBaza > ronda.getNumBazas()){
            rondaService.finalizarRonda(ronda.getId());
            getPuntaje(ronda.getNumBazas(), ronda.getId());
            manoService.actualizarResultadoMano(baza);
            Integer nextRonda = ronda.getNumRonda() + 1;
            // Si es la última ronda, finalizamos partida
            if(nextRonda > ULTIMA_RONDA){
                finalizarPartida(ronda.getPartida().getId());
            // Si no, pasamos de ronda, iniciamos sus manos e iniciamos la primera baza
            } else{
                Integer numJugadores = jugadorService.findJugadoresByPartidaId(partidaId).size(); 
                Integer numBazas = manoService.getNumCartasARepartir(nextRonda, numJugadores);
                Ronda newRonda = rondaService.nextRonda(ronda.getId(), numBazas);
                messagingTemplate.convertAndSend("/topic/nuevaRonda/partida/" + partidaId, rondaService.rondaActual(partidaId));
                manoService.iniciarManos(ronda.getPartida().getId(), newRonda, jugadores);
                messagingTemplate.convertAndSend("/topic/nuevasManos/partida/" + partidaId, manoService.findAllManosByRondaId(newRonda.getId()));
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
                    puntaje += ULTIMA_RONDA*numBazas;
                }else{
                    puntaje -= ULTIMA_RONDA*numBazas;
                }
            }else{
                if(m.getApuesta().equals(m.getResultado())){
                    // Los ptos de bonificacion solo se calcula si se acierta la apuesta
                    Integer ptosBonificacion = bazaService.getPtosBonificacion(rondaId, jugador.getId());
                    puntaje += 20*m.getApuesta() + ptosBonificacion;
                }else{
                    puntaje -= ULTIMA_RONDA*Math.abs(m.getApuesta()-m.getResultado());
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
        if (mano == null) {
            throw new ResourceNotFoundException("Mano", "id", jugadorId);
        }

        if (ap > mano.getCartas().size()) {
            throw new ApuestaNoValidaException("La apuesta no puede ser mayor a " + mano.getCartas().size());
        }

        mano.setApuesta(ap);
        jugador.setApuestaActual(ap);
        manoService.saveMano(mano);
        jugadorService.updateJugador(jugador, jugadorId);
        messagingTemplate.convertAndSend("/topic/apuesta/partida/" + partida.getId(), jugadorService.findJugadoresByPartidaId(partida.getId()));

    }

}
