package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import jakarta.validation.Valid;

@Service
public class ManoService {
    
private ManoRepository ManoRepository;

    @Autowired
    public ManoService(ManoRepository ManoRepository) {
        this.ManoRepository = ManoRepository;
    }

    //save a Mano en la base de datos
    @Transactional
    public Mano saveMano(Mano Mano) {
        return ManoRepository.save(Mano);
    }
    // listar todos los Manoes de la base de datos
    @Transactional(readOnly = true)
    public Iterable<Mano> findAll() {
        return ManoRepository.findAll();
    }
    //obtener Mano por pk
    @Transactional(readOnly = true)
    public Mano findById(Integer id) {
        return ManoRepository.findById(id).orElse(null);
    }
    //borrar Mano por pk
    @Transactional
    public void deleteMano(Integer id) {
        ManoRepository.deleteById(id);
    }
    
    //actualizar Mano
    @Transactional
	public Mano updateMano(@Valid Mano Mano, Integer idToUpdate) {
		Mano toUpdate = findById(idToUpdate);
		BeanUtils.copyProperties(Mano, toUpdate, "id");
		ManoRepository.save(toUpdate);

		return toUpdate;
	}

    /* 
    @Transactional
    public void calculoPuntaje(Integer numBazas, Integer manoId) {
        Optional<Mano> manoOpt = getManoById(manoId);
        if (!manoOpt.isPresent()) {
            throw new ResourceNotFoundException("Mano no encontrada");
        }

        Mano mano = manoOpt.get();
        Integer apuesta = mano.getApuesta();
        Integer resultado = mano.getResultado();
        Boolean resultApuesta = apuesta.equals(resultado);
        Integer puntajeRonda = 0;

        if(mano.getApuesta()==0){
            if(resultApuesta){
                puntajeRonda += 10*numBazas;
            }else{
                puntajeRonda -= 10*numBazas;
            }
        }else{
            if(resultApuesta){
                puntajeRonda += 20*apuesta;
            }else{
                puntajeRonda -= 10*Math.abs(apuesta-resultado);
            }
        }

        Jugador jugador = mano.getJugador();
        jugador.setPuntos(jugador.getPuntos()+puntajeRonda);

    }
        */

}
