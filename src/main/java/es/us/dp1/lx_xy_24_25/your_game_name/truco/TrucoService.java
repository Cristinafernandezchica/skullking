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
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.NoCartaDeManoException;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
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
	private BazaService bazaService;
	private JugadorService jugadorService;
	private ManoService manoService;


    @Autowired
	public TrucoService(TrucoRepository trucoRepository, BazaService bazaService, JugadorService jugadorService) {
		this.trucoRepository = trucoRepository;
        this.bazaService = bazaService;
        this.jugadorService = jugadorService;
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

		Boolean cartaEnMano = truco.getCarta() == null || 
			truco.getMano().getCartas().stream().anyMatch(carta -> carta.equals(truco.getCarta()));

		if (!cartaEnMano) {
			throw new NoCartaDeManoException();
		}

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
	public void iniciarTruco(Baza Baza, Integer partidaId){
		List<Jugador> jugadores =jugadorService.findJugadoresByPartidaId(partidaId);
		Integer turno = 1;
		for(Jugador jugador : jugadores){
			Truco trucoIniciado= new Truco();
			trucoIniciado.setBaza(Baza);
			Mano mano =manoService.findLastManoByJugadorId(jugador.getId());
			trucoIniciado.setMano(mano);
			trucoIniciado.setJugador(jugador);
			trucoIniciado.setTurno(turno);
			trucoIniciado.setCarta(null);
			trucoRepository.save(trucoIniciado);
			turno += 1;
		}
	}

    // Para BazaRestController
    public Map<Carta, Jugador> getCartaByJugador(int bazaId) {
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
	


	// Crear Trucos de una Baza y guardarlas en la base de datos
    @Transactional
    public void crearTrucosBaza(Integer idBaza) {
        // Determinamos Baza, Ronda, Partida y Jugadores a los que pertenecen los Trucos
        Baza baza = bazaService.findById(idBaza);
        Integer idPartida = baza.getRonda().getPartida().getId();
        // List<Jugador> jugadores = jugadorService.findJugadoresByPartidaId(idPartida);
		List<Jugador> jugadores = jugadorService.findJugadoresOrdenadosByPartidaId(idPartida);

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

    // Next Truco
    

}
