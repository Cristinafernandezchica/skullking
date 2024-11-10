package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoService;
import jakarta.validation.Valid;

@Service
public class BazaService {
    
    private BazaRepository bazaRepository;
    private TrucoService trucoService;

    @Autowired
    public BazaService(BazaRepository bazaRepository, TrucoService trucoService) {
        this.bazaRepository = bazaRepository;
        this.trucoService = trucoService;
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

    @Transactional
    public Baza iniciarBaza(Ronda ronda) {
        Baza bazaIniciada= new Baza();
        bazaIniciada.setTipoCarta(null);
        bazaIniciada.setGanador(null);
        bazaIniciada.setCartaGanadora(null);
        bazaIniciada.setRonda(ronda);
        Baza res = bazaRepository.save(bazaIniciada);
        trucoService.iniciarTruco(res, ronda.getPartida().getId());
        return res;
    }

    @Transactional(readOnly = true)
    public Baza findUltimaBazaByRondaId(Integer rondaId){
               List<Baza> Bazas =bazaRepository.findBazasByRondaId(rondaId);
       Baza BazasOrdenadas = Bazas.stream()
                .sorted((j1, j2) -> j2.getId().compareTo(j1.getId())) // Orden descendente
                .findFirst().orElse(null);
                return BazasOrdenadas;
    }
}