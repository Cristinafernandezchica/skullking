package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.CartaService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.exceptions.ApuestaNoValidaException;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoService;
import jakarta.validation.Valid;

@Service
public class ManoService {
private final Integer ID_TIGRESA_BANDERA_BLANCA = 71;
private final Integer ID_TIGRESA_PIRATA = 72;

private ManoRepository manoRepository;
private CartaService cs;
private JugadorService js;


    @Autowired
    public ManoService(ManoRepository manoRepository, CartaService cs, JugadorService js) {
        this.manoRepository = manoRepository;
        this.cs = cs;
        this.js = js;
    }


    //save a Mano en la base de datos
    @Transactional
    public Mano saveMano(Mano mano) {
        return manoRepository.save(mano);
    }
    // listar todos los Manoes de la base de datos
    @Transactional(readOnly = true)
    public Iterable<Mano> findAll() {
        return manoRepository.findAll();
    }
    //obtener Mano por pk
    @Transactional(readOnly = true)
    public Mano findManoById(Integer id) throws DataAccessException{
        return manoRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Mano","id",(id)));
    }
    //borrar Mano por pk
    @Transactional
    public void deleteMano(Integer id) {
        manoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Mano> findAllByRondaId(Integer rondaId){
        return manoRepository.findAllByRondaId(rondaId);
    }
    
    //actualizar Mano
    @Transactional
	public Mano updateMano(@Valid Mano mano, Integer idToUpdate) {
		Mano toUpdate = findManoById(idToUpdate);
		BeanUtils.copyProperties(mano, toUpdate, "id");
		manoRepository.save(toUpdate);
		return toUpdate;
	}

    @Transactional(readOnly = true)
    public Mano findLastManoByJugadorId(Integer jugadorId){
        List<Mano> res =manoRepository.findAllManoByJugadorId(jugadorId);
        Mano ultimaManoCreada = res.stream()
        .sorted((j1, j2) -> j2.getId().compareTo(j1.getId())) // Orden descendente
        .findFirst().orElse(null);
        return ultimaManoCreada;
    }

    // para iniciar las manos de los jugadores
    @Transactional
    public void iniciarManos(Integer partidaId, Ronda ronda){
        List<Carta> listaCartas =(List<Carta>) cs.findAll();
        // para que no se cojan las cartas comodines
        listaCartas.stream().filter(c -> !( c.getId().equals(ID_TIGRESA_BANDERA_BLANCA) || c.getId().equals(ID_TIGRESA_PIRATA))).collect(Collectors.toList());
        Collections.shuffle(listaCartas);   // Barajar cartas
        List<Jugador> jugadores = js.findJugadoresByPartidaId(partidaId);
        for(Jugador jugador: jugadores){
            Mano mano = new Mano();
            List<Carta> cartasBaraja = listaCartas.subList(0,getNumCartasARepartir(ronda.getNumRonda(), jugadores.size()));
            mano.setJugador(jugador);
            mano.setApuesta(0);  // Esto lo elige el usuario, si no lo elige será 0
            mano.setResultado(0);    // El resultado se establecerá más tarde
            List<Carta> cartaMano= new ArrayList<Carta>();
            cartaMano.addAll(cartasBaraja);
            mano.setCartas(cartaMano);
            mano.setRonda(ronda);
            manoRepository.save(mano);
            cartasBaraja.clear();   // Borramos las cartas de la baraja, para repartir al siguiente jugador
            
        }
    }

    // Numero de cartas a repartir en la ronda
    public Integer getNumCartasARepartir(Integer numRonda, Integer numJugadores){
        Integer numCartasTotales = 70;
        Integer cartasARepartir;
        if(numCartasTotales / numJugadores >= numRonda){
            cartasARepartir = numRonda;
        } else{
            cartasARepartir = numCartasTotales / numJugadores;
        }
        return cartasARepartir;
    }

    // Para apostar
    @Transactional
    public void apuesta(Integer ap, Integer jugadorId){
        Mano mano = findLastManoByJugadorId(jugadorId);
        Jugador jugador = js.findById(jugadorId);
        if (mano == null) {
            throw new ResourceNotFoundException("Mano", "id", jugadorId);
        }

        if (ap > mano.getCartas().size()) {
            throw new ApuestaNoValidaException("La apuesta no puede ser mayor a " + mano.getCartas().size());
        }

        mano.setApuesta(ap);
        jugador.setApuestaActual(ap);
        manoRepository.save(mano);
        js.updateJugador(jugador, jugadorId);
    }

    public List<Carta> cartasDisabled(Integer idMano, TipoCarta tipoCarta) {
        Mano manoActual = manoRepository.findById(idMano).orElse(null);
        if (manoActual == null) {
            return new ArrayList<>(); // o lanza una excepción si prefieres
        }
        
        List<Carta> cartas = manoActual.getCartas();
        Boolean hasPaloBaza = cartas.stream().anyMatch(c -> c.getTipoCarta().equals(tipoCarta));
        Boolean hasEspecial = cartas.stream().anyMatch(Carta::esCartaEspecial);
        List<Carta> result = new ArrayList<>();
        
        for (Carta c : cartas) {
            if (hasEspecial && hasPaloBaza) {
                // Caso 3: Mano con alguna especial y alguna del palobaza
                if (!(c.getTipoCarta().equals(tipoCarta) || c.esCartaEspecial())) {
                    result.add(c);
                }
            } else if (hasEspecial && !hasPaloBaza) {
                // Caso 2: Mano con alguna especial sin palobaza
                if (!c.esCartaEspecial()) {
                    result.add(c);
                }
            } else if (hasPaloBaza && !hasEspecial) {
                // Caso 4: Mano con alguna del palobaza y ninguna especial
                if (!c.getTipoCarta().equals(tipoCarta)) {
                    result.add(c);
                }
            }
            // Caso 1: Mano sin especiales ni palobaza - `result` permanece vacío (todas las cartas habilitadas).
        }
        
        return result;
    }    
    
/* 
    @Transactional
    public void getPuntaje(Integer numBazas, Integer rondaId){
         List<Mano> manos = manoRepository.findAllByRondaId(rondaId);
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
                    Integer ptosBonificacion = bs.getPtosBonificacion(rondaId, jugador.getId());
                    puntaje += 20*m.getApuesta() + ptosBonificacion;
                }else{
                    puntaje -= 10*Math.abs(m.getApuesta()-m.getResultado());
                } 
            }
            jugador.setPuntos(jugador.getPuntos() + puntaje);
         }
    }
*/
    /* 
    @Transactional
    public Integer getPtosBonificacion(Integer idRonda, Integer idJugador){
        // se cogen las bazas de la ronda en la que el jugador haya ganado
        List<Baza> bazasRondaJugador = bs.findByIdRondaAndIdJugador(idRonda,idJugador);
        Integer ptosBonificacion = 0;
        for(Baza baza: bazasRondaJugador){
            List<Carta> cartasBaza = ts.findTrucosByBazaId(baza.getId())
                .stream().map(t -> t.getCarta()).collect(Collectors.toList());
            Carta cartaGanadora = baza.getTrucoGanador().getCarta();
            for(Carta carta: cartasBaza){
                calculoPtosBonificacion(cartaGanadora, carta);
            }
        }
        return ptosBonificacion;
    }

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
    */
    
}
