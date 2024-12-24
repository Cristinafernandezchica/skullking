package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.BazaService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoService;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import jakarta.validation.Valid;

@Service
public class RondaService {
    
    RondaRepository rondaRepository;
    // PartidaService partidaService;
    //ManoService manoService;
    //BazaService bazaService;
    //JugadorService jugadorService;
    private static final int ULTIMA_RONDA = 10;

    @Autowired
    public RondaService(RondaRepository rondaRepository){ // ManoService manoService, @Lazy BazaService bazaService, JugadorService jugadorService
        this.rondaRepository = rondaRepository;
        //this.manoService = manoService;
        //this.bazaService = bazaService;
        //this.jugadorService = jugadorService;
    }

    @Transactional(readOnly=true)
    public List<Ronda> getAllRondas(){
        return rondaRepository.findAll();
    }

    @Transactional
    public Ronda save(Ronda ronda) {
        rondaRepository.save(ronda);
        return ronda;
    }

    @Transactional(readOnly=true)
    public Ronda getRondaById(Integer id) throws DataAccessException{
        return rondaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ronda", "id", id));
    }

    @Transactional
    public void delete(Integer id) {
        rondaRepository.deleteById(id);
    }

    @Transactional
    public Ronda updateRonda(@Valid Ronda ronda, Integer idToUpdate) throws DataAccessException{
        Ronda toUpdate = getRondaById(idToUpdate);
        BeanUtils.copyProperties(ronda, toUpdate, "id");
        rondaRepository.save(toUpdate);
        return toUpdate;
        
    }

    @Transactional
    public Ronda iniciarRonda (Partida partida) {
        Ronda ronda = new Ronda();
        ronda.setNumBazas(1);
        ronda.setNumRonda(1);
        ronda.setPartida(partida);
        ronda.setEstado(RondaEstado.JUGANDO);
        Ronda res= rondaRepository.save(ronda);
        // Cambios para prueba
        // manoService.iniciarManos(partida.getId(),res);
        // bazaService.iniciarBazas(res);
        return res;
    }

    // Partida accederá a dicha función proporcionándo el id de la ronda actual
    /*
    @Transactional
    public Ronda nextRonda(Integer rondaId) {
        Ronda ronda = rondaRepository.findById(rondaId)
            .orElseThrow(() -> new ResourceNotFoundException("Ronda no encontrada"));
        Integer nextRonda = ronda.getNumRonda() + 1;
        finalizarRonda(rondaId);
        Integer numJugadores = jugadorService.findJugadoresByPartidaId(ronda.getPartida().getId()).size(); 

        Ronda newRonda = new Ronda();

        // Comprobación si es la última ronda
        if(nextRonda > ULTIMA_RONDA){
            partidaService.finalizarPartida(ronda.getPartida().getId());
        } else{
            newRonda.setNumRonda(nextRonda);
            // newRonda.setBazaActual(1); // Quitar atributo
            newRonda.setEstado(RondaEstado.JUGANDO);
            // manoService.iniciarManos(ronda.getPartida().getId(),newRonda);
            newRonda.setNumBazas(manoService.getNumCartasARepartir(nextRonda, numJugadores));
            // bazaService.iniciarBazas(newRonda);
            newRonda.setPartida(ronda.getPartida());
        }
        Ronda result =  rondaRepository.save(newRonda);
        manoService.iniciarManos(ronda.getPartida().getId(),newRonda);
        bazaService.iniciarBazas(newRonda);
        return result;
    }
    */

    // Next Ronda para prueba fin dependencias
    @Transactional
    public Ronda nextRondaPrueba(Integer rondaId, Integer numBazas) {
        Ronda ronda = rondaRepository.findById(rondaId)
            .orElseThrow(() -> new ResourceNotFoundException("Ronda no encontrada"));
        Integer nextRonda = ronda.getNumRonda() + 1;

        Ronda newRonda = new Ronda();
        newRonda.setNumRonda(nextRonda);
        newRonda.setEstado(RondaEstado.JUGANDO);
        newRonda.setNumBazas(numBazas);
        newRonda.setPartida(ronda.getPartida());
        Ronda result =  rondaRepository.save(newRonda);
        return result;
    }
    

    /*
    @Transactional
    public void finalizarRonda(Integer rondaId){
        Ronda ronda = getRondaById(rondaId);

        ronda.setEstado(RondaEstado.FINALIZADA);
        getPuntaje(ronda.getNumBazas(), rondaId);

        rondaRepository.save(ronda);
    }
    */

    @Transactional
    public void finalizarRondaPrueba(Integer rondaId){
        Ronda ronda = getRondaById(rondaId);

        ronda.setEstado(RondaEstado.FINALIZADA);
        // getPuntaje(ronda.getNumBazas(), rondaId);

        rondaRepository.save(ronda);
    }

    @Transactional(readOnly = true)
    public Ronda findRondaActualByPartidaId(Integer partidaId) {
        List<Ronda> rondas = rondaRepository.findByPartidaId(partidaId);
        Ronda rondasOrdenadas =rondas.stream()
                .sorted((j1, j2) -> j2.getId().compareTo(j1.getId())) // Orden descendente
                .findFirst().orElse(null);
        return rondasOrdenadas;
    }

    /*
    // Next Baza
    //@Transactional
    public Baza nextBaza(Integer bazaId) {
        Baza baza = bazaService.findById(bazaId);
        Ronda ronda = baza.getRonda();
        Integer nextBaza = baza.getNumBaza() + 1;
        Partida partida = ronda.getPartida();

        Baza newBaza = new Baza();

        // Comprobación si es la última baza
        if(nextBaza > ronda.getNumBazas()){
            // bazaService.calculoGanador(bazaId);
            Mano manoGanador = manoService.findLastManoByJugadorId(baza.getGanador().getId());
            Integer resultadoSinActualizar = manoGanador.getResultado();
            manoGanador.setResultado(resultadoSinActualizar + 1);
            manoService.updateMano(manoGanador,manoGanador.getId());
            nextRonda(ronda.getId());
        } else{
            List<Integer> turnos = bazaService.calcularTurnosNuevaBaza(partida.getId(), baza);
            // Configurar turno actual de la partida
            partida.setTurnoActual(bazaService.primerTurno(turnos));
            partidaService.update(partida, partida.getId());
            // Configurar para la siguiente baza
            newBaza.setCartaGanadora(null);
            newBaza.setGanador(null);
            newBaza.setNumBaza(nextBaza);
            newBaza.setPaloBaza(null);
            newBaza.setTurnos(turnos);
        }    
        return bazaService.saveBaza(newBaza);
    }
    */
    /*
    @Transactional
    public void getPuntaje(Integer numBazas, Integer rondaId){
         List<Mano> manos = manoService.findAllByRondaId(rondaId);
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
    */

}
