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
    
    RondaRepository rr;
    PartidaService ps;
    ManoService ms;
    BazaService bs;
    JugadorService js;

    @Autowired
    public RondaService(RondaRepository rr, ManoService ms, @Lazy BazaService bs, JugadorService js){
        this.rr = rr;
        this.ms = ms;
        this.bs = bs;
        this.js = js;
    }

    @Transactional(readOnly=true)
    public List<Ronda> getAllRondas(){
        return rr.findAll();
    }

    @Transactional
    public Ronda save(Ronda r) {
        rr.save(r);
        return r;
    }

    @Transactional(readOnly=true)
    public Ronda getRondaById(Integer id) throws DataAccessException{
        return rr.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ronda", "id", id));
    }

    @Transactional
    public void delete(Integer id) {
        rr.deleteById(id);
    }

    @Transactional
    public Ronda updateRonda(@Valid Ronda ronda, Integer idToUpdate) throws DataAccessException{
        Ronda toUpdate = getRondaById(idToUpdate);
        BeanUtils.copyProperties(ronda, toUpdate, "id");
        rr.save(toUpdate);
        return toUpdate;
        
    }

    @Transactional
    public Ronda iniciarRonda (Partida partida) {
        Ronda ronda = new Ronda();
        ronda.setNumBazas(1);
        ronda.setNumRonda(1);
        ronda.setBazaActual(1);
        ronda.setPartida(partida);
        ronda.setEstado(RondaEstado.JUGANDO);
        Ronda res= rr.save(ronda);
        ms.iniciarManos(partida.getId(),res);
        bs.iniciarBazas(ronda);
        return res;
    }

    // Partida accederá a dicha función proporcionándo el id de la ronda actual
    @Transactional
    public Ronda nextRonda(Integer rondaId) {
        Ronda ronda = rr.findById(rondaId)
            .orElseThrow(() -> new ResourceNotFoundException("Ronda no encontrada"));
        Integer nextRonda = ronda.getNumRonda() + 1;
        finalizarRonda(rondaId);

        Ronda newRonda = new Ronda();

        // Comprobación si es la última ronda
        if(nextRonda > 10){
            ps.finalizarPartida(ronda.getPartida().getId());
        } else{
            newRonda.setNumRonda(nextRonda);
            newRonda.setBazaActual(1);
            newRonda.setEstado(RondaEstado.JUGANDO);
            ms.iniciarManos(ronda.getPartida().getId(),newRonda);
            newRonda.setNumBazas(ms.getNumCartasARepartir(newRonda.getNumRonda(), 
                    js.findJugadoresByPartidaId(newRonda.getPartida().getId()).size()));
            bs.iniciarBazas(newRonda);
        }
        return rr.save(newRonda);
    }
    

    @Transactional
    public void finalizarRonda(Integer rondaId){
        Ronda ronda = getRondaById(rondaId);

        ronda.setEstado(RondaEstado.FINALIZADA);
        // bucle para cada baza -> bs.calculoGanador(); -->  No va aquí, esto es para calcular el ganado r de la BAZA no de la ronda
        getPuntaje(ronda.getNumBazas(), rondaId);

        rr.save(ronda);
    }

    @Transactional(readOnly = true)
    public Ronda findRondaActualByPartidaId(Integer partidaId) {
        List<Ronda> rondas = rr.findByPartidaId(partidaId);
        Ronda rondasOrdenadas =rondas.stream()
                .sorted((j1, j2) -> j2.getId().compareTo(j1.getId())) // Orden descendente
                .findFirst().orElse(null);
        return rondasOrdenadas;
    }

    // Next Baza
    @Transactional
    public Baza nextBaza(Integer bazaId) {
        Baza baza = bs.findById(bazaId);
        Ronda ronda = baza.getRonda();
        Integer nextBaza = baza.getNumBaza() + 1;
        Partida partida = ronda.getPartida();

        Baza newBaza = new Baza();

        // Comprobación si es la última baza
        if(nextBaza > ronda.getNumBazas()){
            nextRonda(ronda.getId());
        } else{
            List<Integer> turnos = bs.calcularTurnosNuevaBaza(partida.getId(), baza);
            // Configurar turno actual de la partida
            partida.setTurnoActual(bs.primerTurno(turnos));
            ps.update(partida, partida.getId());
            // Configurar para la siguiente baza
            newBaza.setTrucoGanador(null);
            newBaza.setGanador(null);
            newBaza.setNumBaza(nextBaza);
            newBaza.setTipoCarta(null);
            newBaza.setTurnos(turnos);
        }    
        return bs.saveBaza(newBaza);
    }

    @Transactional
    public void getPuntaje(Integer numBazas, Integer rondaId){
         List<Mano> manos = ms.findAllByRondaId(rondaId);
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

}
