package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.ManoRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoRepository;
import jakarta.validation.Valid;

@Service
public class BazaService {
    
    private BazaRepository bazaRepository;
    private TrucoRepository trucoRepository;
    private ManoRepository manoRepository;

    @Autowired
    public BazaService(BazaRepository bazaRepository) {
        this.bazaRepository = bazaRepository;
    }

    //Save las bazas en la base de datos
    @Transactional
    public Baza saveBaza(Baza baza) {
        bazaRepository.save(baza);
        return baza;
    }

    //Listar todas las bazas de la base de datos
    @Transactional(readOnly = true)
    public List<Baza> getAllBazas() {
        return bazaRepository.findAll();
    }

    //Get una baza por su id
    @Transactional(readOnly = true)
    public Baza findById(Integer id) {
        return bazaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Baza", "id", id));
    }

    //Delete una baza por su id
    @Transactional
    public void deleteBaza(Integer id) {
        bazaRepository.deleteById(id);
    }
    
    //Update una baza existente
    @Transactional
    public Baza updateBaza(@Valid Baza baza, Integer idToUpdate) {
        Baza toUpdate = findById(idToUpdate);
        BeanUtils.copyProperties(baza, toUpdate, "id");
        bazaRepository.save(toUpdate);
        return toUpdate;
    }

    // Crear Trucos de una Baza y guardarlas en la base de datos
    @Transactional
    public void crearTrucosBaza(Integer idRonda, Integer idBaza, List<Jugador> jugadores) {
        Baza baza = findById(idBaza);
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
            Integer turno = i; 
            Integer idCarta = null;
            
            Truco truco = new Truco(baza, mano, jugador, idCarta, turno);
            trucoRepository.save(truco);
        }
    }
}