package es.us.dp1.lx_xy_24_25.your_game_name.carta;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import jakarta.validation.Valid;

@Service
public class CartaService {
     private CartaRepository cartaRepository;

    @Autowired
    public CartaService(CartaRepository cartaRepository) {
        this.cartaRepository = cartaRepository;
    }

    //save a Carta en la base de datos
    @Transactional
    public Carta saveCarta(Carta Carta) {
        return cartaRepository.save(Carta);
    }
    // listar todas las Cartas de la base de datos
    @Transactional(readOnly = true)
    public Iterable<Carta> findAll() {
        return cartaRepository.findAll();
    }
    //obtener Carta por pk
    @Transactional(readOnly = true)
    public Optional<Carta> findById(Integer id) {
        return cartaRepository.findById(id);
    }
    //borrar Carta por pk
    @Transactional
    public void deleteCarta(Integer id) {
        cartaRepository.deleteById(id);
    }
    
    //actualizar Carta
    @Transactional
	public Carta updateCarta(@Valid Carta Carta, Integer idToUpdate) {
		Carta toUpdate = findById(idToUpdate).get();
		BeanUtils.copyProperties(Carta, toUpdate, "id");
		cartaRepository.save(toUpdate);

		return toUpdate;
	}

}
