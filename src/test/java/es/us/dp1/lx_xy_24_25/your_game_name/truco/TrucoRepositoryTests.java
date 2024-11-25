package es.us.dp1.lx_xy_24_25.your_game_name.truco;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertTrue;
@DataJpaTest
public class TrucoRepositoryTests {
    
    @Autowired
    TrucoRepository trucoRepository;
    // public List<Truco> findByBazaId(Integer bazaId);
    // public List<Carta> findCartaIdByBazaId(Integer bazaId);
	// public List<Truco> findByJugadorId(Integer jugadorId);
	// public List<Truco> findByManoId(Integer manoId);
    // public Optional<Truco> findTrucoByBazaIdCartaId(Integer bazaId, Integer cartaId);
    @Test
    public void findBazaByNullId(){
        List<Truco> trucos = trucoRepository.findByBazaId(null);
        assertTrue(trucos.isEmpty());
    }
    @Test
    public void findBazaByNonExistentId(){
        List<Truco> trucos = trucoRepository.findByBazaId(1);
        assertTrue(!trucos.isEmpty());
    }
}