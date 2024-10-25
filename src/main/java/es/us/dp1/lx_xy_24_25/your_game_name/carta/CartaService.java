package es.us.dp1.lx_xy_24_25.your_game_name.carta;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import jakarta.validation.Valid;

@Service
public class CartaService {
     private CartaRepository CartaRepository;

    @Autowired
    public CartaService(CartaRepository CartaRepository) {
        this.CartaRepository = CartaRepository;
    }

    //save a Carta en la base de datos
    @Transactional
    public Carta saveCarta(Carta Carta) {
        return CartaRepository.save(Carta);
    }
    // listar todas las Cartas de la base de datos
    @Transactional(readOnly = true)
    public Iterable<Carta> findAll() {
        return CartaRepository.findAll();
    }
    //obtener Carta por pk
    @Transactional(readOnly = true)
    public Carta findById(Integer id) {
        return CartaRepository.findById(id).orElse(null);
    }
    //borrar Carta por pk
    @Transactional
    public void deleteCarta(Integer id) {
        CartaRepository.deleteById(id);
    }
    
    //actualizar Carta
    @Transactional
	public Carta updateCarta(@Valid Carta Carta, Integer idToUpdate) {
		Carta toUpdate = findById(idToUpdate);
		BeanUtils.copyProperties(Carta, toUpdate, "id");
		CartaRepository.save(toUpdate);

		return toUpdate;
	}

}
