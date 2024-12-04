package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoRepository;
import jakarta.validation.Valid;

@Service
public class BazaService {
    
    private BazaRepository bazaRepository;
    private TrucoRepository trucoRepository;
    private PartidaService partidaService;
    private JugadorService jugadorService;

    @Autowired
    public BazaService(BazaRepository bazaRepository,TrucoRepository trucoRepository, PartidaService partidaService, JugadorService jugadorService) {
        this.bazaRepository = bazaRepository;
        this.trucoRepository = trucoRepository;
        this.partidaService = partidaService;
        this.jugadorService = jugadorService;
    }

    //Save las bazas en la base de datos
    @Transactional
    public Baza saveBaza(Baza baza) {
        bazaRepository.save(baza);
        return baza;
    }

    //Listar todas las bazas de la base de datos
    @Transactional(readOnly = true)
    public List<Baza> getAllBazas() throws DataAccessException{
        return bazaRepository.findAll();
    }

    //Get una baza por su id
    @Transactional(readOnly = true)
    public Baza findById(Integer id) throws DataAccessException {
        return bazaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Baza", "id", id));
    }

    //Delete una baza por su id
    @Transactional
    public void deleteBaza(Integer id) throws DataAccessException{
        bazaRepository.deleteById(id);
    }
    
    //Update una baza existente
    @Transactional
    public Baza updateBaza(@Valid Baza baza, Integer idToUpdate) throws DataAccessException{
        Baza toUpdate = findById(idToUpdate);
		BeanUtils.copyProperties(baza, toUpdate, "id");
		bazaRepository.save(toUpdate);

        return toUpdate;
    }
//te encuentra la baza que tenga menor id y no haya finalizado (no tenga un truco ganador)
    @Transactional(readOnly = true)
    public Baza findBazaActualByRondaId(Integer rondaId){
        List<Baza> Bazas =bazaRepository.findBazasByRondaId(rondaId);
        Bazas= Bazas.stream().filter(x->x.getTrucoGanador()==null).toList();
       Baza BazasOrdenadas = Bazas.stream()
                        .sorted((j1, j2) -> j1.getId().compareTo(j2.getId())) // Orden ascendente
                        .findFirst().orElse(null);
                return BazasOrdenadas;
    }

    // Buscar una Baza por Ronda ID y número de Baza
    @Transactional(readOnly = true)
    public Baza findByRondaIdAndNumBaza(Integer rondaId, Integer numBaza) {
        return bazaRepository.findByRondaIdAndNumBaza(rondaId, numBaza)
                .orElseThrow(() -> new ResourceNotFoundException("Baza", "numBaza", numBaza));
    }

    // Se empleará para obtener aquellas bazas de una ronda que haya ganado un jugador
    @Transactional(readOnly = true)
    public List<Baza> findByIdRondaAndIdJugador(Integer rondaId, Integer jugadorId) {
        return bazaRepository.findByIdRondaAndIdJugador(rondaId, jugadorId);
    }

    // Si se usa este método se crea dependencia circular en Truco
    /*
    @Transactional(readOnly = true)
    public Baza findBazaAnterior(Integer bazaId, Integer rondaId){
        return bazaRepository.findBazaAnterior(bazaId, rondaId).get();
    }
    */

    // Iniciar una Baza
    @Transactional
    public Baza iniciarBazas (Ronda ronda) {
         Partida partida = ronda.getPartida();
        List<Integer> turnos  = calcularTurnosNuevaBaza(partida.getId(), null);
        Baza baza = new Baza();
        baza.setTrucoGanador(null);
        baza.setNumBaza(1);
        baza.setGanador(null);
        baza.setTipoCarta(TipoCarta.sinDeterminar);
        baza.setRonda(ronda);
        baza.setTurnos(turnos);
        Baza resBaza = bazaRepository.save(baza);
        partida.setTurnoActual(primerTurno(turnos));
        partidaService.update(partida, partida.getId());
        //trucoService.crearTrucosBazaConTurno(baza.getId()); // cambiado para turnos
        return resBaza;
    }

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

    @Transactional
	public Integer primerTurno(List<Integer> turnos){
		return turnos.get(0);
	}
/* 
    // Next Baza
    @Transactional
    public Baza nextBaza(Integer id) {
        Baza baza = findById(id);
        Ronda ronda = baza.getRonda();
        Integer nextBaza = baza.getNumBaza() + 1;

        // Actualiza la propiedad ganador de la instancia de la Baza anterior
        // El valor de esta propiedad se usa en el cálculo del puntaje
        calculoGanador(baza.getId());
        // Posible nueva baza de la misma ronda
        Baza newBaza = new Baza();

        // Comprobación si es la última baza
        if(nextBaza > ronda.getNumBazas()){
            rondaService.nextRonda(ronda.getId());
        } else{
            // Configurar para la siguiente baza
            newBaza.setTrucoGanador(null);
            newBaza.setGanador(null);
            newBaza.setNumBaza(nextBaza);
            newBaza.setTipoCarta(null);
        }    
        return bazaRepository.save(newBaza);
    }
*/
    @Transactional
    public Integer getPtosBonificacion(Integer idRonda, Integer idJugador){
        // se cogen las bazas de la ronda en la que el jugador haya ganado
        List<Baza> bazasRondaJugador = findByIdRondaAndIdJugador(idRonda,idJugador);
        Integer ptosBonificacion = 0;
        for(Baza baza: bazasRondaJugador){
            List<Carta> cartasBaza = trucoRepository.findTrucosByBazaId(baza.getId())
                .stream().map(t -> t.getCarta()).collect(Collectors.toList());
            Carta cartaGanadora = baza.getTrucoGanador().getCarta();
            for(Carta carta: cartasBaza){
                ptosBonificacion += calculoPtosBonificacion(cartaGanadora, carta);  // carta.calculoPtosBonificacion(cartaGanadora, carta);
            }
        }
        return ptosBonificacion;
    }

    // Mover a entidad Carta como método
    public Integer calculoPtosBonificacion(Carta cartaGanadora, Carta carta){
        Integer ptosBonificacion = 0;
        TipoCarta cartaTipo = carta.getTipoCarta();

        if(carta.esCatorce()) ptosBonificacion += 10;
        if(carta.esCatorce()&& carta.esTriunfo()) ptosBonificacion += 20;
        if(cartaGanadora.getTipoCarta().equals(TipoCarta.pirata)) {
            if(cartaTipo.equals(TipoCarta.sirena)) ptosBonificacion += 20;
        }
        if(cartaGanadora.getTipoCarta().equals(TipoCarta.sirena)) {
            if(cartaTipo.equals(TipoCarta.skullking)) ptosBonificacion += 40;
        }
        if(cartaGanadora.getTipoCarta().equals(TipoCarta.skullking)) {
            if(cartaTipo.equals(TipoCarta.pirata)) ptosBonificacion += 30;
        }

        return ptosBonificacion;
    }
    
        // Terminar baza = se define la prop idGanador y debe llamar a la función api/v1/rondas/idRonda/manos pra obtener List<Mano>
        // Recorro esa lista y me quedo con la mano cuya propiedad idJugador = idGanador y le hago un update para sumarle 1 a la propiedad resultado
        
        // Ademas hay q definir idCartaGanadora, llamando la funcion de truco que devuelve la list<trucos> y me quedo con la propiedad id carta    


    /*
    @Transactional
    public void calculoGanador(Integer idBaza){
        Baza baza = findById(idBaza);
        Truco trucoGanador = null;
        List<Truco> trucosBaza = trucoRepository.findTrucosByBazaId(idBaza);
        Integer personajes=0;
        List<Truco> triunfos = new ArrayList<Truco>();
        List<Truco> cartasPalo = new ArrayList<Truco>();
        List<Truco> skullKing = new ArrayList<Truco>();
        List<Truco> sirenas = new ArrayList<Truco>();
        List<Truco> piratas = new ArrayList<Truco>();

        for(Truco truco : trucosBaza){
            Carta carta = truco.getCarta();
            if(carta.esPersonaje()){
                personajes++;
                if(carta.getTipoCarta().equals(TipoCarta.skullking)) skullKing.add(truco);
                if(carta.getTipoCarta().equals(TipoCarta.sirena)) sirenas.add(truco);
                if(carta.getTipoCarta().equals(TipoCarta.skullking)) piratas.add(truco);
            } 
            if(carta.esTriunfo()) triunfos.add(truco);
            if(baza.getTipoCarta().equals(carta.getTipoCarta())) cartasPalo.add(truco);
        }

        Truco primeraSirena = getPrimeraSirena(sirenas);  
        Truco primerPirata = getPrimerPirata(piratas);
        Truco triunfoMayorNum = getTriunfoMayorTruco(triunfos);
        Truco cartaPaloMayorNum = getCartaPaloMayorNum(cartasPalo);
        Truco primerTruco = getPrimerTruco(cartasPalo);

        if(personajes != 0){
            if(personajes >=1){
                if(skullKing.size()!=0){
                    if(sirenas.size()!=0){
                        trucoGanador = primeraSirena;
                    }else{
                        trucoGanador = skullKing.get(0);
                    }
                }
            } else if(sirenas.size()!=0){
                if(piratas.size()!=0){
                    trucoGanador = primerPirata;
                }else{
                    trucoGanador = primeraSirena;
                }
            } else{
                trucoGanador = primerPirata;
            }
        } else{
            if(triunfos.size()!=0){
                if(triunfos.size() >= 1){
                    trucoGanador = triunfoMayorNum;
                }else{
                    trucoGanador = triunfos.get(0);
                }
            }else{
                if(cartasPalo.size()!=0){
                    if(cartasPalo.size()>=1){
                        trucoGanador = cartaPaloMayorNum;
                    }else{
                        trucoGanador = cartasPalo.get(0);
                    }
                } else{
                    trucoGanador = primerTruco;
                }
            }
        }
        
        baza.setTrucoGanador(trucoGanador);

    }
    */

    // Método calculoGanador usando Patrón State
    @Transactional
    public void calculoGanador(Integer idBaza) {
        Baza baza = findById(idBaza);
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

        baza.setTrucoGanador(trucoGanador);
        baza.setGanador(trucoGanador.getJugador());
        bazaRepository.save(baza);
    }

     public Truco getPrimeraSirena(List<Truco> sirenas){
        return sirenas.stream().collect(Collectors.minBy(
            Comparator.comparingInt(t -> t.getTurno()))).get();
     }

     public Truco getPrimerPirata(List<Truco> piratas){
        return piratas.stream().collect(Collectors.minBy(
            Comparator.comparingInt(t -> t.getTurno()))).get();
     }

     public Truco getTriunfoMayorTruco(List<Truco> triunfos){
        return triunfos.stream().collect(Collectors.maxBy(
            Comparator.comparingInt(t -> t.getCarta().getNumero()))).get();
     }

     public Truco getCartaPaloMayorNum(List<Truco> cartasPalo){
        return cartasPalo.stream().collect(Collectors.maxBy(
            Comparator.comparingInt(t -> t.getCarta().getNumero()))).get();
     }

     public Truco getPrimerTruco(List<Truco> cartasPalo){
        return cartasPalo.stream().collect(Collectors.maxBy(
            Comparator.comparingInt(t -> t.getCarta().getNumero()))).get();
     }


}