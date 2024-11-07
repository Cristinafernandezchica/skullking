package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaEstado;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoService;
import jakarta.validation.Valid;

@Service
public class BazaService {
    
    private BazaRepository bazaRepository;
    private RondaService rondaService;
    private PartidaService partidaService;
    private TrucoService trucoService;

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

    @Transactional(readOnly = true)
    public Baza findUltimaBazaByRondaId(Integer rondaId){
               List<Baza> Bazas =bazaRepository.findBazasByRondaId(rondaId);
       Baza BazasOrdenadas = Bazas.stream()
                .sorted((j1, j2) -> j2.getId().compareTo(j1.getId())) // Orden descendente
                .findFirst().orElse(null);
                return BazasOrdenadas;
    }


/*
    // Iniciar una Baza
    @Transactional
    public Baza iniciarBazas (Ronda ronda) {
        Baza baza = new Baza();
        baza.setCartaGanadora(null);
        baza.setNumBaza(1);
        baza.setGanador(null);
        baza.setTipoCarta(null);
        baza.setRonda(ronda);
        trucoService.iniciarTrucos();
        return bazaRepository.save(baza);
    }
*/
    // Next Baza
    @Transactional
    public Baza nextBaza(Integer id) {
        Baza baza = bazaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Baza", "id", id));
        Ronda ronda = baza.getRonda();
        Integer nextBaza = baza.getNumBaza() + 1;
        Integer nextRonda = ronda.getNumRonda() + 1;

        // Comprobación si es la última baza
        if(nextBaza > ronda.getNumBazas()){
            if(nextRonda > 10){
                partidaService.finalizarPartida(ronda.getPartida().getId());
                ronda.setEstado(RondaEstado.FINALIZADA);
            } else {
                rondaService.iniciarRonda(ronda.getPartida());
            }
        } else{
            baza.setCartaGanadora(null);
            baza.setGanador(null);
            baza.setNumBaza(nextBaza);
            baza.setTipoCarta(null);
        }    
        return bazaRepository.save(baza);
    }
    
        // Terminar baza = se define la prop idGanador y debe llamar a la función api/v1/rondas/idRonda/manos pra obtener List<Mano>
        // Recorro esa lista y me quedo con la mano cuya propiedad idJugador = idGanador y le hago un update para sumarle 1 a la propiedad resultado
        
        // Ademas hay q definir idCartaGanadora, llamando la funcion de truco que devuelve la list<trucos> y me quedo con la propiedad id carta    


}