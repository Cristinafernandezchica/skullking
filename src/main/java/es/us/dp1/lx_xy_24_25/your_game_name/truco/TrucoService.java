package es.us.dp1.lx_xy_24_25.your_game_name.truco;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaRepository;
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
	private BazaRepository bazaRepository;
	private ManoRepository manoRepository;
	private JugadorService jugadorService;
	private UserService userService;


    @Autowired
	public TrucoService(TrucoRepository trucoRepository, BazaRepository bazaRepository,ManoRepository manoRepository, JugadorService jugadorService, UserService userService) {
		this.trucoRepository = trucoRepository;
        this.bazaRepository = bazaRepository;
        this.manoRepository = manoRepository;
        this.jugadorService = jugadorService;
		this.userService = userService;
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
		return trucoRepository.findByBazaId(id);
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

    // REVISAR Y QUIZAS QUITAR
    @Transactional(readOnly = true)
	public Integer findJugadorIdByBazaIdCartaId(int bazaId, int cartaId) throws DataAccessException {
		return trucoRepository.findJugadorIdByBazaIdCartaId(bazaId, cartaId)
				.orElseThrow(() -> new ResourceNotFoundException("Truco", "Baza", bazaId));
	}

    @Transactional
	public Truco saveTruco(Truco truco) throws DataAccessException {
		Boolean cartaEnMano = truco.getMano().getCartas().stream()
            .anyMatch(carta -> carta.getId().equals(truco.getIdCarta()));

    	if (!cartaEnMano) {
        	throw new NoCartaDeManoException();
    	}
        
		trucoRepository.save(truco);
        return truco;
	}

    @Transactional
	public Truco updateTruco(Truco truco, int trucoId) throws DataAccessException {
        Boolean cartaEnMano = truco.getMano().getCartas().stream()
            .anyMatch(carta -> carta.getId().equals(truco.getIdCarta()));
		if (!cartaEnMano) {
			throw new NoCartaDeManoException();
		}

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

	// Crear Trucos de una Baza y guardarlas en la base de datos
    @Transactional
    public void crearTrucosBaza(Integer idBaza) {
        // Determinamos Baza, Ronda, Partida y Jugadores a los que pertenecen los Trucos
        Optional<Baza> posibleBaza = bazaRepository.findById(idBaza);
        Baza baza = null;
        if(posibleBaza != null) {
            baza = posibleBaza.get();
        } else {
            throw new ResourceNotFoundException("Baza", "id", idBaza);
        }
        
        Integer idRonda = baza.getRonda().getId();
        Integer idPartida = baza.getRonda().getPartida().getId();
        List<Jugador> jugadores = jugadorService.findJugadoresByPartidaId(idPartida);

        // Crear y guardar cada instancia de Truco de dicha Baza
        for (int i = 0; i < jugadores.size(); i++) {
            Integer jugador = jugadores.get(i).getId();
            Optional<Mano> posibleMano = manoRepository.findManoByJugadorIdRondaId(idRonda, jugador);
            Mano mano = null;
            if (posibleMano != null) {
                mano = posibleMano.get();
            } else {
                throw new ResourceNotFoundException("Mano", "jugadorId", jugador);
            }
            Integer turno = i+1; 
            Integer idCarta = null;
            
            Truco truco = new Truco(baza, mano, jugador, idCarta, turno);
            trucoRepository.save(truco);
        }
    }

    // Inicial Truco
    // Next Truco
    

}
