package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.CartaService;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import jakarta.validation.Valid;

@Service
public class ManoService {
    
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
    public Mano findManoById(Integer id) {
        return manoRepository.findById(id).orElse(null);
    }
    //borrar Mano por pk
    @Transactional
    public void deleteMano(Integer id) {
        manoRepository.deleteById(id);
    }
    
    //actualizar Mano
    @Transactional
	public Mano updateMano(@Valid Mano Mano, Integer idToUpdate) {
		Mano toUpdate = findManoById(idToUpdate);
		BeanUtils.copyProperties(Mano, toUpdate, "id");
		manoRepository.save(toUpdate);

		return toUpdate;
	}

    // para iniciar las manos de los jugadores
    @Transactional
    public void iniciarManos(Integer partidaId, Integer numRonda){
        Iterable<Carta> iterableCartas = cs.findAll();
        List<Carta> listaCartas = new ArrayList<>(); 
        iterableCartas.forEach(listaCartas::add);
        Collections.shuffle(listaCartas);   // Barajar cartas
        List<Jugador> jugadores = js.findJugadoresByPartidaId(partidaId);
        for(Jugador jugador: jugadores){
            Mano mano = new Mano();
            List<Carta> cartasBaraja = listaCartas.subList(0,getNumCartasARepartir(numRonda, jugadores.size()));
            mano.setJugador(jugador);
            mano.setApuesta(null);  // Esto lo elige el usuario
            mano.setResultado(null);    // El resultado se establecerá más tarde
            mano.setCartas(cartasBaraja);
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

    // Crear función apuesta, para poner las apuestas de cada uno de los jugadores
    // Hay que ponerla en el controlador para que se pueda llamar desde frontend
    // ya que las apuestas se pondrán en función de lo que elija el usuario
    // Esta es provisional -->  NO ES CORRECTA
    public void apostar(Integer apuesta, Integer manoId){
        Mano mano = findManoById(manoId);
        mano.setApuesta(apuesta);
        manoRepository.save(mano);
    }
    @Transactional(readOnly = true)
    public Mano findLastManoByJugadorId(Integer jugadorId){
        List<Mano> res =manoRepository.findAllManoByJugadorId(jugadorId);
        Mano ultimaManoCreada = res.stream()
        .sorted((j1, j2) -> j2.getId().compareTo(j1.getId())) // Orden descendente
        .findFirst().orElse(null);
        return ultimaManoCreada;
    }


}
