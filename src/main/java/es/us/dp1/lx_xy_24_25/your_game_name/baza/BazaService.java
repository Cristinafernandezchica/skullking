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
    private TrucoService trucoService;

    @Autowired
    public BazaService(BazaRepository bazaRepository,TrucoService trucoService) {
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
/* 
    // Next Baza
    @Transactional
    public Baza nextBaza(Integer id) {
        Baza baza = findById(id);
        Ronda ronda = baza.getRonda();
        Integer nextBaza = baza.getNumBaza() + 1;

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
            List<Carta> cartasBaza = trucoService.findTrucosByBazaId(baza.getId())
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