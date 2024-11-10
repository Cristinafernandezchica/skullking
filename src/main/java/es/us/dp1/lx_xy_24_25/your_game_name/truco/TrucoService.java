package es.us.dp1.lx_xy_24_25.your_game_name.truco;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoService;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.exceptions.NoCartaDeManoException;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class TrucoService {
    
    private TrucoRepository trucoRepository;
	private JugadorService jugadorService;
	private ManoService manoService;

    @Autowired
	public TrucoService(TrucoRepository trucoRepository,JugadorService jugadorService,ManoService manoService) {
		this.trucoRepository = trucoRepository;
		this.jugadorService = jugadorService;
		this.manoService = manoService;
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
	public List<Truco> findTrucosByBazaId(int bazaId) throws DataAccessException {
		return trucoRepository.findByBazaId(bazaId);
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

	/* 
    // REVISAR Y QUIZAS QUITAR
    @Transactional(readOnly = true)
	public Truco findTrucoByBazaIdCartaId(int bazaId, int cartaId) throws DataAccessException {
		return trucoRepository.findTrucoByBazaIdCartaId(bazaId, cartaId)
				.orElseThrow(() -> new ResourceNotFoundException("Truco", "Baza", bazaId));
	}

    // REVISAR Y QUIZAS QUITAR
    @Transactional(readOnly = true)
	public Integer findJugadorIdByBazaIdCartaId(int bazaId, int cartaId) throws DataAccessException {
		return trucoRepository.findJugadorIdByBazaIdCartaId(bazaId, cartaId)
				.orElseThrow(() -> new ResourceNotFoundException("Truco", "Baza", bazaId));
	}
 */
    @Transactional
	public Truco saveTruco(Truco truco) throws DataAccessException {
        Truco trucoComprobado = getTrucoWithImposibleIdCarta(truco);
		if (trucoComprobado.equals(null)) {
			throw new NoCartaDeManoException();
		} else
            //trucoRepository.save(truco);
            trucoRepository.save(trucoComprobado);
		// return truco;
        return trucoComprobado;
	}

    private Truco getTrucoWithImposibleIdCarta(Truco truco) {
        List<Integer> cartas = truco.getMano().getCartas().stream().map(Carta::getId).collect(Collectors.toList());
		Integer idCarta = truco.getIdCarta();
        Boolean cartaValida = false;
		for (Integer c : cartas) {
			if (idCarta.equals(c)) {
				cartaValida = true;
			}
		}
        Truco res = cartaValida.equals(false) ? null : truco;
		return res;
	}

    @Transactional
	public Truco updateTruco(Truco truco, int trucoId) throws DataAccessException {
		Truco toUpdate = findTrucoById(trucoId);
        Truco trucoComprobado = getTrucoWithImposibleIdCarta(truco);
		if (trucoComprobado.equals(null)) {
			throw new NoCartaDeManoException();
		} else
            BeanUtils.copyProperties(trucoComprobado, toUpdate, "id", "jugador");
		    trucoRepository.save(toUpdate);

		return toUpdate;
	}

    @Transactional
	public void deleteTruco(int trucoId) throws DataAccessException {
		Truco toDelete = findTrucoById(trucoId);
		trucoRepository.delete(toDelete);
	}

	@Transactional
	public void iniciarTruco(Baza Baza, Integer partidaId){
		List<Jugador> jugadores =jugadorService.findJugadoresByPartidaId(partidaId);
		Integer turno = 1;
		for(Jugador jugador : jugadores){
			Truco trucoIniciado= new Truco();
			trucoIniciado.setBaza(Baza);
			Mano mano =manoService.findLastManoByJugadorId(jugador.getId());
			trucoIniciado.setMano(mano);
			trucoIniciado.setTurno(turno);
			trucoIniciado.setIdCarta(null);
			trucoRepository.save(trucoIniciado);
			turno += 1;
		}
	}
	

	/*
    // Para BazaRestController
    public Map<Integer, Integer> getCartaByJugador(int bazaId) {
        List<Truco> trucos = trucoRepository.findByBazaId(bazaId);
        List<Truco> trucosOrdenados = trucos.stream().sorted(Comparator.comparing(t-> t.getTurno())).toList();
        return trucosOrdenados.stream()
            .collect(Collectors.toMap(
                Truco::getJugador,   
                Truco::getIdCarta,    
                (v1, v2) -> v1,      
                LinkedHashMap::new
            ));
    }
	*/

    // Inicial Truco

    // Next Truco
    

}
