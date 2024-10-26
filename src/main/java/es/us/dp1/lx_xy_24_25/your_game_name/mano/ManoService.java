package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

}
