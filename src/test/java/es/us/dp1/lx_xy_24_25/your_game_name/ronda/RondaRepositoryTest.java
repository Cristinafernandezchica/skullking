package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaEstado;

@ExtendWith(MockitoExtension.class)
public class RondaRepositoryTest {

    @Mock
    private RondaRepository rondaRepository;

    private Ronda ronda1;
    private Ronda ronda2;
    private Partida partida;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        partida = new Partida();
        partida.setId(1);
        partida.setNombre("Partida Test");
        partida.setEstado(PartidaEstado.JUGANDO);

        ronda1 = new Ronda();
        ronda1.setId(1);
        ronda1.setNumRonda(1);
        ronda1.setEstado(RondaEstado.JUGANDO);
        ronda1.setPartida(partida);

        ronda2 = new Ronda();
        ronda2.setId(2);
        ronda2.setNumRonda(2);
        ronda2.setEstado(RondaEstado.FINALIZADA);
        ronda2.setPartida(partida);
    }

    @Test
    public void shouldFindAll() {
        when(rondaRepository.findAll()).thenReturn(List.of(ronda1, ronda2));

        List<Ronda> result = rondaRepository.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(rondaRepository, times(1)).findAll();
    }

    @Test
    public void shouldFindById() {
        Integer rondaId = ronda1.getId();
        when(rondaRepository.findById(rondaId)).thenReturn(Optional.of(ronda1));

        Optional<Ronda> result = rondaRepository.findById(rondaId);

        assertTrue(result.isPresent());
        assertEquals(rondaId, result.get().getId());
        verify(rondaRepository, times(1)).findById(rondaId);
    }

    @Test
    public void shouldFindByPartidaId() {
        Integer partidaId = partida.getId();
        when(rondaRepository.findByPartidaId(partidaId)).thenReturn(List.of(ronda1, ronda2));

        List<Ronda> result = rondaRepository.findByPartidaId(partidaId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getPartida().getId().equals(partidaId)));
        verify(rondaRepository, times(1)).findByPartidaId(partidaId);
    }

    @Test
    public void shouldDeleteByPartidaId() {
        Integer partidaId = partida.getId();

        rondaRepository.deleteByPartidaId(partidaId);
        assertEquals(rondaRepository.findById(1), Optional.empty());
        assertEquals(rondaRepository.findById(2), Optional.empty());
        verify(rondaRepository, times(1)).deleteByPartidaId(partidaId);
    }
}

