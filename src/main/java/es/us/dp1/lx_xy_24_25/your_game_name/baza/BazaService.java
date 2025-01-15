package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import java.util.stream.Collectors;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.TrucoRepository;
import jakarta.validation.Valid;

@Service
public class BazaService {

    private BazaRepository bazaRepository;
    private TrucoRepository trucoRepository;

    @Autowired
    public BazaService(BazaRepository bazaRepository, TrucoRepository trucoRepository) {
        this.bazaRepository = bazaRepository;
        this.trucoRepository = trucoRepository;
    }

    // Save las bazas en la base de datos
    @Transactional
    public Baza saveBaza(Baza baza) {
        bazaRepository.save(baza);
        return baza;
    }

    // Listar todas las bazas de la base de datos
    @Transactional(readOnly = true)
    public List<Baza> getAllBazas() throws DataAccessException {
        return bazaRepository.findAll();
    }

    // Get una baza por su id
    @Transactional(readOnly = true)
    public Baza findById(Integer id) throws DataAccessException {
        return bazaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Baza", "id", id));
    }

    // Delete una baza por su id
    @Transactional
    public void deleteBaza(Integer id) throws DataAccessException {
        bazaRepository.deleteById(id);
    }

    // Update una baza existente
    @Transactional
    public Baza updateBaza(@Valid Baza baza, Integer idToUpdate) throws DataAccessException {
        Baza toUpdate = findById(idToUpdate);
        BeanUtils.copyProperties(baza, toUpdate, "id");
        bazaRepository.save(toUpdate);

        return toUpdate;
    }

    @Transactional(readOnly = true)
    public Baza findBazaActualByRondaId(Integer rondaId) {
        List<Baza> bazas = bazaRepository.findBazasByRondaId(rondaId);
        Integer posUltimaBaza = bazas.size() - 1;
        return bazas.get(posUltimaBaza);
    }

    // Buscar una Baza por Ronda ID y número de Baza
    @Transactional(readOnly = true)
    public Baza findByRondaIdAndNumBaza(Integer rondaId, Integer numBaza) {
        return bazaRepository.findByRondaIdAndNumBaza(rondaId, numBaza)
                .orElseThrow(() -> new ResourceNotFoundException("Baza", "numBaza", numBaza));
    }

    // Bazas de una ronda que ha ganado un jugador
    @Transactional(readOnly = true)
    public List<Baza> findByIdRondaAndIdJugador(Integer rondaId, Integer jugadorId) {
        return bazaRepository.findByIdRondaAndIdJugador(rondaId, jugadorId);
    }

    @Transactional(readOnly = true)
    public Baza findBazaAnterior(Integer bazaId, Integer rondaId){
        return bazaRepository.findBazaAnterior(bazaId, rondaId).get();
    }
    

    // Iniciar la primera baza de cada ronda
    @Transactional
    public Baza iniciarBaza(Ronda ronda, List<Jugador> jugadores) {
        Partida partida = ronda.getPartida();
        List<Integer> turnos = calcularTurnosNuevaBaza(partida.getId(), null, jugadores);
        Baza baza = new Baza();
        baza.setCartaGanadora(null);
        baza.setNumBaza(1);
        baza.setGanador(null);
        baza.setPaloBaza(PaloBaza.sinDeterminar);
        baza.setRonda(ronda);
        baza.setTurnos(turnos);
        Baza resBaza = bazaRepository.save(baza);
        return resBaza;
    }

    // Next Baza
    @Transactional
    public Baza nextBaza(Integer bazaId, List<Jugador> jugadores) {
        Baza baza = findById(bazaId);
        Ronda ronda = baza.getRonda();
        Integer nextBaza = baza.getNumBaza() + 1;
        Partida partida = ronda.getPartida();

        Baza newBaza = new Baza();
        List<Integer> turnos = calcularTurnosNuevaBaza(partida.getId(), baza, jugadores);
        newBaza.setCartaGanadora(null);
        newBaza.setGanador(null);
        newBaza.setNumBaza(nextBaza);
        newBaza.setRonda(ronda);
        newBaza.setPaloBaza(PaloBaza.sinDeterminar);
        newBaza.setTurnos(turnos);
        return saveBaza(newBaza);
    }

    @Transactional
    public List<Integer> calcularTurnosNuevaBaza(int partidaId, Baza bazaAnterior, List<Jugador> jugadores) {
        // Si es la primera baza, el orden es el orden de unión de los jugadores
        if (bazaAnterior == null) {
            List<Integer> turnosJugadores = jugadores.stream().map(Jugador::getId).collect(Collectors.toList());
            return turnosJugadores;
        }

        // Identificar al ganador de la baza anterior
        Integer ganadorId = bazaAnterior.getGanador().getId();

        // Reorganizar turnos: ganador primero, seguido por el resto
        List<Integer> ordenAnterior = jugadores.stream().map(Jugador::getId).collect(Collectors.toList());
        List<Integer> turnosNuevaBaza = ordenAnterior.stream()
                .dropWhile(id -> !id.equals(ganadorId)) // Los jugadores a partir del ganador
                .collect(Collectors.toList());

        // Añadir los jugadores que estaban antes del ganador al final de la lista
        turnosNuevaBaza.addAll(
                ordenAnterior.stream().takeWhile(id -> !id.equals(ganadorId)).collect(Collectors.toList()));

        return turnosNuevaBaza;
    }

    // Está repetida en partida
    @Transactional
    public Integer primerTurno(List<Integer> turnos) {
        return turnos.get(0);
    }

    @Transactional
    public Integer getPtosBonificacion(Integer idRonda, Integer idJugador) {
        // se cogen las bazas de la ronda en la que el jugador haya ganado
        List<Baza> bazasRondaJugador = findByIdRondaAndIdJugador(idRonda, idJugador);
        Integer ptosBonificacion = 0;
        for (Baza baza : bazasRondaJugador) {
            List<Carta> cartasBaza = trucoRepository.findTrucosByBazaId(baza.getId())
                    .stream().map(t -> t.getCarta()).collect(Collectors.toList());
            Carta cartaGanadora = baza.getCartaGanadora();
            for (Carta carta : cartasBaza) {
                ptosBonificacion += calculoPtosBonificacion(cartaGanadora, carta); // carta.calculoPtosBonificacion(cartaGanadora,
                                                                                   // carta);
            }
        }
        return ptosBonificacion;
    }

    // Mover a entidad Carta como método
    public Integer calculoPtosBonificacion(Carta cartaGanadora, Carta carta) {
        Integer ptosBonificacion = 0;
        TipoCarta cartaTipo = carta.getTipoCarta();

        if (carta.esCatorce())
            ptosBonificacion += 10;
        if (carta.esCatorce() && carta.esTriunfo())
            ptosBonificacion += 20;
        if (cartaGanadora.getTipoCarta().equals(TipoCarta.pirata)) {
            if (cartaTipo.equals(TipoCarta.sirena))
                ptosBonificacion += 20;
        }
        if (cartaGanadora.getTipoCarta().equals(TipoCarta.sirena)) {
            if (cartaTipo.equals(TipoCarta.skullking))
                ptosBonificacion += 40;
        }
        if (cartaGanadora.getTipoCarta().equals(TipoCarta.skullking)) {
            if (cartaTipo.equals(TipoCarta.pirata))
                ptosBonificacion += 30;
        }

        return ptosBonificacion;
    }

}