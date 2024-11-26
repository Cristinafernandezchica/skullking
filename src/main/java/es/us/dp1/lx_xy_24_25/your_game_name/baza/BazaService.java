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
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoService;
import jakarta.validation.Valid;

@Service
public class BazaService {
    
    private BazaRepository bazaRepository;
    private RondaService rondaService;
    private TrucoService trucoService;

    @Autowired
    public BazaService(BazaRepository bazaRepository, @Lazy RondaService rondaService, @Lazy TrucoService trucoService) {
        this.bazaRepository = bazaRepository;
        this.rondaService = rondaService;
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
        Baza baza = new Baza();
        baza.setTrucoGanador(null);
        baza.setNumBaza(1);
        baza.setGanador(null);
        baza.setTipoCarta(null);
        baza.setRonda(ronda);
        Baza resBaza = bazaRepository.save(baza);
        trucoService.crearTrucosBazaConTurno(baza.getId()); // cambiado para turnos
        return resBaza;
    }

    // Next Baza
    @Transactional
    public Baza nextBaza(Integer id) {
        Baza baza = findById(id);
        Ronda ronda = baza.getRonda();
        Integer nextBaza = baza.getNumBaza() + 1;

        // Comprobación si es la última baza
        if(nextBaza > ronda.getNumBazas()){
            rondaService.nextRonda(ronda.getId());
        } else{
            // Configurar para la siguiente baza
            baza.setTrucoGanador(null);
            baza.setGanador(null);
            baza.setNumBaza(nextBaza);
            baza.setTipoCarta(null);
        }    
        return bazaRepository.save(baza);
    }
    
        // Terminar baza = se define la prop idGanador y debe llamar a la función api/v1/rondas/idRonda/manos pra obtener List<Mano>
        // Recorro esa lista y me quedo con la mano cuya propiedad idJugador = idGanador y le hago un update para sumarle 1 a la propiedad resultado
        
        // Ademas hay q definir idCartaGanadora, llamando la funcion de truco que devuelve la list<trucos> y me quedo con la propiedad id carta    


    @Transactional
    public void calculoGanador(Integer idBaza){
        Baza baza = findById(idBaza);
        Truco trucoGanador = null;
        List<Truco> trucosBaza = trucoService.findTrucosByBazaId(idBaza);
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

        Truco primeraSirena = primeraSirena(sirenas);  
        Truco primerPirata = primerPirata(piratas);
        Truco triunfoMayorNum = triunfoMayorTruco(triunfos);
        Truco cartaPaloMayorNum = cartaPaloMayorNum(cartasPalo);
        Truco primerTruco = primerTruco(cartasPalo);

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

     public Truco primeraSirena(List<Truco> sirenas){
        return sirenas.stream().collect(Collectors.minBy(
            Comparator.comparingInt(t -> t.getTurno()))).get();
     }

     public Truco primerPirata(List<Truco> piratas){
        return piratas.stream().collect(Collectors.minBy(
            Comparator.comparingInt(t -> t.getTurno()))).get();
     }

     public Truco triunfoMayorTruco(List<Truco> triunfos){
        return triunfos.stream().collect(Collectors.maxBy(
            Comparator.comparingInt(t -> t.getCarta().getNumero()))).get();
     }

     public Truco cartaPaloMayorNum(List<Truco> cartasPalo){
        return cartasPalo.stream().collect(Collectors.maxBy(
            Comparator.comparingInt(t -> t.getCarta().getNumero()))).get();
     }

     public Truco primerTruco(List<Truco> cartasPalo){
        return cartasPalo.stream().collect(Collectors.maxBy(
            Comparator.comparingInt(t -> t.getCarta().getNumero()))).get();
     }

}