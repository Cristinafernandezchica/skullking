package es.us.dp1.lx_xy_24_25.your_game_name.truco;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.NoCartaDeManoException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.trucoState.CalculoGanadorContext;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.trucoState.CartasPaloState;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.trucoState.PersonajesState;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.trucoState.TriunfosState;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.PaloBaza;
import es.us.dp1.lx_xy_24_25.your_game_name.bazaCartaManoDTO.BazaCartaManoDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;


@Service
public class TrucoService {
		
    private TrucoRepository trucoRepository;
	private BazaService bazaService;
	private JugadorService jugadorService;
	private ManoService manoService;
	private PartidaService partidaService;
	private SimpMessagingTemplate messagingTemplate;

	private final Integer idComodinPirata = 72;
	private final Integer idComodinBanderaBlanca = 71;
	private final Integer idTigresa = 65;


    @Autowired
	public TrucoService(TrucoRepository trucoRepository, BazaService bazaService, ManoService manoService, JugadorService jugadorService, PartidaService partidaService, SimpMessagingTemplate messagingTemplate) { // , @Lazy BazaService bazaService ,ManoRepository manoRepository
		this.trucoRepository = trucoRepository;
        this.bazaService = bazaService;
        this.jugadorService = jugadorService;
		this.manoService = manoService;
		this.partidaService = partidaService;
		this.messagingTemplate = messagingTemplate;
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
		
		Jugador jugador = jugadorService.findById(jugadorId);
		Partida partida = jugador.getPartida();

		Truco trucoIniciado= new Truco();
		trucoIniciado.setBaza(DTO.getBaza());
		trucoIniciado.setMano(DTO.getMano());
		trucoIniciado.setJugador(jugador);
		trucoIniciado.setTurno(DTO.getTurno());
		trucoIniciado.setCarta(DTO.getCarta());
		trucoRepository.save(trucoIniciado);

		messagingTemplate.convertAndSend("/topic/baza/truco/partida/" + partida.getId(), findTrucosByBazaId(trucoIniciado.getBaza().getId()));

		Ronda ronda = trucoIniciado.getBaza().getRonda();
		Baza baza = trucoIniciado.getBaza();
		manoSinCarta(trucoIniciado, partida, ronda);

		Integer numJugadores = jugadorService.findJugadoresByPartidaId(partida.getId()).size();
		Integer numTrucosBaza = findTrucosByBazaId(baza.getId()).size();
		Boolean esUltimoTruco = (numJugadores == numTrucosBaza);
		Boolean noEsUltimoTruco = (numJugadores > numTrucosBaza);

		Baza bazaParaCambio = bazaService.findById(baza.getId());
		if(bazaParaCambio.getPaloBaza() == PaloBaza.sinDeterminar){
			Baza bazaConPaloCambiado = cambiarPaloBaza(baza, trucoIniciado);
			mandarCartasDisabled(bazaConPaloCambiado, ronda, partida);
		}

		if(noEsUltimoTruco){
			actualizarTurno(partida, trucoIniciado);
		}
		else if(esUltimoTruco){
			Jugador ganadorBaza = calculoGanador(baza.getId());
			messagingTemplate.convertAndSend("/topic/ganadorBaza/partida/" + partida.getId(), ganadorBaza);
			messagingTemplate.convertAndSend("/topic/listaTrucos/partida/" + partida.getId(), new ArrayList<>());
			partidaService.siguienteEstado(partida.getId(), baza.getId());
		}

		return trucoIniciado;
	}

	@Transactional
	public void manoSinCarta(Truco trucoIniciado, Partida partida, Ronda ronda){
		Mano manoSinCartaJugada = trucoIniciado.getMano();
		List<Carta> nuevaListaCarta = trucoIniciado.getMano().getCartas().stream().
			filter(cartaJugada -> cartaJugada.getId()!=trucoIniciado.getCarta().getId()).toList();
			
		if(trucoIniciado.getCarta().getId()==idComodinBanderaBlanca || trucoIniciado.getCarta().getId()==idComodinPirata){
			nuevaListaCarta = trucoIniciado.getMano().getCartas().stream().
				filter(cartaJugada -> cartaJugada.getId()!=idTigresa).toList();
		}
		manoSinCartaJugada.setCartas(nuevaListaCarta);
		manoService.saveMano(manoSinCartaJugada);

		messagingTemplate.convertAndSend("/topic/nuevasManos/partida/" + partida.getId(), manoService.findAllManosByRondaId(ronda.getId()));

	}

	@Transactional
	public Baza cambiarPaloBaza(Baza baza, Truco truco){
		PaloBaza paloBaza = PaloBaza.sinDeterminar;
		if(truco.getCarta().getTipoCarta() != TipoCarta.banderaBlanca){
			if(truco.getCarta().getTipoCarta() == TipoCarta.pirata || 
			truco.getCarta().getTipoCarta() == TipoCarta.skullking || 
			truco.getCarta().getTipoCarta() == TipoCarta.sirena){
				paloBaza = PaloBaza.noHayPalo;
			} else{
				if(truco.getCarta().getTipoCarta() == TipoCarta.amarillo){
					paloBaza = PaloBaza.amarillo;
				} else if(truco.getCarta().getTipoCarta() == TipoCarta.morada){
					paloBaza = PaloBaza.morada;
				} else if(truco.getCarta().getTipoCarta() == TipoCarta.triunfo){
					paloBaza = PaloBaza.triunfo;
				} else if(truco.getCarta().getTipoCarta() == TipoCarta.verde){
					paloBaza = PaloBaza.verde;
				}
			}
		}

		baza.setPaloBaza(paloBaza);
		return bazaService.saveBaza(baza);
	}

	@Transactional
	public Map<Integer, List<Carta>> cartasDisabledBaza(Integer rondaId, TipoCarta paloBaza){
		List<Integer> idsManos = manoService.findAllManosByRondaId(rondaId).stream().map(Mano::getId).toList(); 
		Map<Integer, List<Carta>> cartasDisabledPorJugador = new HashMap<>();
    
    	for (Integer idMano : idsManos) {
        	List<Carta> cartasDisabled = manoService.cartasDisabled(idMano, paloBaza);
        	cartasDisabledPorJugador.put(idMano, cartasDisabled);
    	}
    
    	return cartasDisabledPorJugador;
	}

	@Transactional
	public void mandarCartasDisabled(Baza bazaConPaloCambiado, Ronda ronda, Partida partida){
		TipoCarta tipoCarta;
			switch (bazaConPaloCambiado.getPaloBaza()) {
				case amarillo:
					tipoCarta = TipoCarta.amarillo;
					break;
				case morada:
					tipoCarta = TipoCarta.morada;
					break;
				case verde:
					tipoCarta = TipoCarta.verde;
					break;
				case triunfo:
					tipoCarta = TipoCarta.triunfo;
					break;
				default:
					tipoCarta = TipoCarta.sinDeterminar;
					break;
			}
			
			Map<Integer, List<Carta>> cartasDisabled = cartasDisabledBaza(ronda.getId(), tipoCarta);
			messagingTemplate.convertAndSend("/topic/cartasDisabled/partida/" + partida.getId(), cartasDisabled);

	}


	// Método calculoGanador usando Patrón State
    @Transactional
    public Jugador calculoGanador(Integer idBaza) {
        Baza baza = bazaService.findById(idBaza);
        List<Truco> trucosBaza = trucoRepository.findTrucosByBazaId(idBaza);
        CalculoGanadorContext context = new CalculoGanadorContext();
        // Determinar el estado basado en las condiciones
        if (trucosBaza.stream().anyMatch(truco -> truco.getCarta().esPersonaje())) {
            context.setState(new PersonajesState());
        } else if (trucosBaza.stream().anyMatch(truco -> truco.getCarta().esTriunfo())) {
            context.setState(new TriunfosState());
        } else {
            context.setState(new CartasPaloState());
        }
        // Calcular el ganador utilizando el estado
        Truco trucoGanador = context.calcularGanador(baza, trucosBaza);
        // Fallback si no se encuentra un ganador válido
        if (trucoGanador == null && !trucosBaza.isEmpty()) {
            trucoGanador = trucosBaza.get(0);
        }
		
        baza.setCartaGanadora(trucoGanador.getCarta());
        baza.setGanador(trucoGanador.getJugador());
        bazaService.saveBaza(baza);
		return baza.getGanador();
    }


	@Transactional
	public Integer siguienteTurno(List<Integer> turnos, Integer turnoActual){
		Integer indexTurnoActual = turnos.indexOf(turnoActual);
		Integer indexSiguienteTurno = indexTurnoActual + 1;
		return turnos.get(indexSiguienteTurno);
	}

	@Transactional
	public void actualizarTurno(Partida partida, Truco trucoIniciado){
		Integer turnoAntesCambio = partida.getTurnoActual();
		List<Integer> turnos = trucoIniciado.getBaza().getTurnos();
		Integer nuevoTurno = siguienteTurno(turnos, turnoAntesCambio);
		partida.setTurnoActual(nuevoTurno);
		partidaService.update(partida, partida.getId());
		messagingTemplate.convertAndSend("/topic/turnoActual/" + partida.getId(), partida.getTurnoActual());
	}

}
