package es.us.dp1.lx_xy_24_25.your_game_name.truco;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.NoCartaDeManoException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.NoCartaDeManoException;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.bazaCartaManoDTO.BazaCartaManoDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class TrucoService {
    
	private final Integer idTigresa=65;
	private final Integer idComodinBanderaBlanca=71;
	private final Integer idComodinPirata=72;
	
	
    private TrucoRepository trucoRepository;
	private BazaRepository bazaRepository;
	// private ManoRepository manoRepository;
	private JugadorService jugadorService;
	private ManoService manoService;
	// private BazaService bazaService;	// Para turnos
	private PartidaService partidaService;
	private RondaService rondaService;


    @Autowired
	public TrucoService(TrucoRepository trucoRepository,  BazaRepository bazaRepository,  ManoService manoService, JugadorService jugadorService, @Lazy PartidaService partidaService, RondaService rondaService) { // , @Lazy BazaService bazaService ,ManoRepository manoRepository
		this.trucoRepository = trucoRepository;
        this.bazaRepository = bazaRepository;
        // this.manoRepository = manoRepository;
        this.jugadorService = jugadorService;
		// this.bazaService = bazaService;
		this.manoService = manoService;
		this.partidaService = partidaService;
		this.rondaService = rondaService;
	}

    @Transactional(readOnly = true)
	public Iterable<Truco> findAllTrucos() throws DataAccessException {
		return trucoRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Truco findTrucoById(int id) throws DataAccessException {
		return trucoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Truco", "ID", id));
	}

    // Para BazaRestController
    @Transactional(readOnly = true)
	public List<Truco> findTrucosByBazaId(int id) throws DataAccessException {
		return trucoRepository.findTrucosByBazaId(id);
	}

    // Para JugadorRestController
    @Transactional(readOnly = true)
	public List<Truco> findTrucosByJugadorId(int jugadorId) throws DataAccessException {
		return trucoRepository.findByJugadorId(jugadorId);
	}

    // Para ManoRestController
    @Transactional(readOnly = true)
	public List<Truco> findTrucosByManoId(int manoId) throws DataAccessException {
		return trucoRepository.findByManoId(manoId);
	}

    // REVISAR Y QUIZAS QUITAR
    @Transactional(readOnly = true)
	public Truco findTrucoByBazaIdCartaId(int bazaId, int cartaId) throws DataAccessException {
		return trucoRepository.findTrucoByBazaIdCartaId(bazaId, cartaId)
				.orElseThrow(() -> new ResourceNotFoundException("Truco", "Baza", bazaId));
	}

    @Transactional
	public Truco saveTruco(Truco truco) throws DataAccessException {
		Boolean cartaEnMano = truco.getCarta() == null || 
			truco.getMano().getCartas().stream().anyMatch(carta -> carta.equals(truco.getCarta()));

    	if (!cartaEnMano) {
        	throw new NoCartaDeManoException();
    	}
        
		trucoRepository.save(truco);
        return truco;
	}



    @Transactional
	public Truco updateTruco(Truco truco, int trucoId) throws DataAccessException {
		Truco toUpdate = findTrucoById(trucoId);

		BeanUtils.copyProperties(truco, toUpdate, "id");
		trucoRepository.save(toUpdate);

		return toUpdate;
	}

    @Transactional
	public void deleteTruco(int trucoId) throws DataAccessException {
		Truco toDelete = findTrucoById(trucoId);
		trucoRepository.delete(toDelete);
	}

	@Transactional
	public Truco jugarTruco(BazaCartaManoDTO DTO, Integer jugadorId){
		Jugador jugador =jugadorService.findById(jugadorId);

		Truco trucoIniciado= new Truco();
		trucoIniciado.setBaza(DTO.getBaza());
		trucoIniciado.setMano(DTO.getMano());
		trucoIniciado.setJugador(jugador);
		trucoIniciado.setTurno(DTO.getTurno());
		trucoIniciado.setCarta(DTO.getCarta());
		trucoRepository.save(trucoIniciado);

		Mano manoSinCartaJugada = trucoIniciado.getMano();
		List<Carta> nuevaListaCarta = trucoIniciado.getMano().getCartas().stream().
			filter(cartaJugada -> cartaJugada.getId()!=trucoIniciado.getCarta().getId()).toList();
			
		if(trucoIniciado.getCarta().getId()==idComodinBanderaBlanca || trucoIniciado.getCarta().getId()==idComodinPirata){
			nuevaListaCarta = trucoIniciado.getMano().getCartas().stream().
				filter(cartaJugada -> cartaJugada.getId()!=idTigresa).toList();
		}
		manoSinCartaJugada.setCartas(nuevaListaCarta);
		manoService.saveMano(manoSinCartaJugada);

		Partida partida = trucoIniciado.getBaza().getRonda().getPartida();
		Integer numJugadores = jugadorService.findJugadoresByPartidaId(partida.getId()).size();
		Integer bazaId = trucoIniciado.getBaza().getId();
		Integer numTrucosBaza = findTrucosByBazaId(bazaId).size();

		// Si no es el último truco de la Baza, se actualiza el turno
		if(numJugadores != numTrucosBaza){
			List<Integer> turnos = trucoIniciado.getBaza().getTurnos();
			Integer turnoAntesCambio = partida.getTurnoActual();
			partida.setTurnoActual(siguienteTurno(turnos, turnoAntesCambio));
			partidaService.update(partida, partida.getId());
		}
		// Si es el último truco de la Baza, se llama a nextBaza
		/*
		else if(numJugadores == numTrucosBaza){
			rondaService.nextBaza(bazaId);
		}
		*/

		return trucoIniciado;
	}


    // Para BazaRestController
    public Map<Carta, Jugador> getCartaByJugador(Integer bazaId) {
        List<Truco> trucos = findTrucosByBazaId(bazaId);
        List<Truco> trucosOrdenados = trucos.stream().sorted(Comparator.comparing(t-> t.getTurno())).toList();
        return trucosOrdenados.stream()
            .collect(Collectors.toMap(
                Truco::getCarta,   
                Truco::getJugador,    
                (v1, v2) -> v1,      
                LinkedHashMap::new
            ));
    }
	

	/*	
	// Crear Trucos de una Baza y guardarlas en la base de datos
    @Transactional
    public void crearTrucosBaza(Integer idBaza) {
        // Determinamos Baza, Ronda, Partida y Jugadores a los que pertenecen los Trucos
        Optional<Baza> posibleBaza = bazaRepository.findById(idBaza);
        if(!posibleBaza.isPresent()) {
			throw new ResourceNotFoundException("Baza", "id", idBaza);
        }

		Baza baza = posibleBaza.get();
        Integer idRonda = baza.getRonda().getId();
        Integer idPartida = baza.getRonda().getPartida().getId();
        List<Jugador> jugadores = jugadorService.findJugadoresByPartidaId(idPartida);

        // Crear y guardar cada instancia de Truco de dicha Baza
        for (int i = 0; i < jugadores.size(); i++) {
            Integer jugador = jugadores.get(i).getId();
            Optional<Mano> posibleMano = manoRepository.findManoByJugadorIdRondaId(idRonda, jugador);
            if (!posibleMano.isPresent()) {
                throw new ResourceNotFoundException("Mano", "jugadorId", jugador);
            }
			Mano mano = posibleMano.get();
            Integer turno = i+1; 
            Integer idCarta = null;
            
            Truco truco = new Truco(baza, mano, jugador, idCarta, turno);
            trucoRepository.save(truco);
        }
    }
	*/

	@Transactional
    public void crearTrucosBaza(Integer idBaza) {
        // Determinamos Baza, Ronda, Partida y Jugadores a los que pertenecen los Trucos
        Optional<Baza> posibleBaza = bazaRepository.findById(idBaza);
        if(!posibleBaza.isPresent()) {
			throw new ResourceNotFoundException("Baza", "id", idBaza);
        }
		Baza baza = posibleBaza.get();

        Integer idPartida = baza.getRonda().getPartida().getId();
        List<Jugador> jugadores = jugadorService.findJugadoresByPartidaId(idPartida);
		//List<Jugador> jugadores = jugadorService.findJugadoresOrdenadosByPartidaId(idPartida);

        // Crear y guardar cada instancia de Truco de dicha Baza
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);
			Mano mano = manoService.findLastManoByJugadorId(jugador.getId());
            Integer turno = i+1; 
            Carta carta = null;

            Truco truco = new Truco(baza, mano, jugador, carta, turno);
            trucoRepository.save(truco);
        }
	}

    // Inicial Truco

    // Next Truco
    
	// La he creado de nuevo por si no funciona tener la anterior
	// Crear trucos con asociación de turnos correcta
	/*
	@Transactional
	public void crearTrucosBazaConTurno(Integer idBaza) {
		Optional<Baza> posibleBaza = bazaRepository.findById(idBaza);
		if (!posibleBaza.isPresent()) {
			throw new ResourceNotFoundException("Baza", "id", idBaza);
		}
		Baza baza = posibleBaza.get();
		Integer idPartida = baza.getRonda().getPartida().getId();
		Baza bazaAnterior = bazaRepository.findBazaAnterior(baza.getId(), baza.getRonda().getId()).orElse(null);

		// Calcular turnos
		List<Integer> turnos = calcularTurnosNuevaBaza(idPartida, bazaAnterior);

		for (int i = 0; i < turnos.size(); i++) {
			Integer jugadorId = turnos.get(i);
			Jugador jugador = jugadorService.findById(jugadorId);
			/*
			Optional<Mano> posibleMano = manoRepository.findManoByJugadorIdRondaId(baza.getRonda().getId(), jugadorId);
			if (!posibleMano.isPresent()) {
				throw new ResourceNotFoundException("Mano", "jugadorId", jugadorId);
			}
			Mano mano = posibleMano.get();
			
			Mano mano = manoService.findLastManoByJugadorId(jugador.getId());

			Truco truco = new Truco(baza, mano, jugador, null, i + 1); // Turno = posición en la lista
			trucoRepository.save(truco);
		}
	}
	*/
	// Caluclar primer turno (Comprobar si se pueden meter directamente en la entidad Baza)
	/*
	@Transactional
	public Integer primerTurno(List<Integer> turnos){
		return turnos.get(0);
	}
	*/

	@Transactional
	public Integer siguienteTurno(List<Integer> turnos, Integer turnoActual){
		Integer indexTurnoActual = turnos.indexOf(turnoActual);
		Integer indexSiguienteTurno = indexTurnoActual + 1;
		return turnos.get(indexSiguienteTurno);
	}

	// Calcular turnos
	/*
	@Transactional
    public List<Integer> calcularTurnosNuevaBaza(int partidaId, Baza bazaAnterior) {
        List<Jugador> jugadores = jugadorService.findJugadoresByPartidaId(partidaId);

        // Si es la primera baza, el orden es el orden de unión de los jugadores
        if (bazaAnterior == null) {
			List<Integer> turnosJugadores = jugadores.stream().map(Jugador::getId).collect(Collectors.toList());
			asignarTurnoAJugadores(jugadores, turnosJugadores);
            return turnosJugadores;
        }

        // Identificar al ganador de la baza anterior
        Integer ganadorId = bazaAnterior.getGanador().getId();

        // Reorganizar turnos: ganador primero, seguido por el resto
        List<Integer> ordenAnterior = jugadores.stream().map(Jugador::getId).collect(Collectors.toList());
        List<Integer> turnosNuevaBaza = ordenAnterior.stream()
            .dropWhile(id -> !id.equals(ganadorId))  // Los jugadores a partir del ganador
            .collect(Collectors.toList());

        // Añadir los jugadores que estaban antes del ganador al final de la lista
        turnosNuevaBaza.addAll(
            ordenAnterior.stream().takeWhile(id -> !id.equals(ganadorId)).collect(Collectors.toList())
        );
		asignarTurnoAJugadores(jugadores, turnosNuevaBaza);

        return turnosNuevaBaza;
    }
	

	@Transactional
	public void asignarTurnoAJugadores(List<Jugador> jugadores, List<Integer> turnos){
		Integer turno = 1;
		for (Integer turnoJugadorId: turnos){
			for(Jugador jugador: jugadores){
				if(turnoJugadorId == jugador.getId()){
					jugador.setTurno(turno);
                    jugadorService.updateJugador(jugador, jugador.getId());
				}
			}
			turno++;
		}

	}
	*/


}
