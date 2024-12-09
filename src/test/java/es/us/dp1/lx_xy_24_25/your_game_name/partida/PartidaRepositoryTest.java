package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PartidaRepositoryTest {

    @Mock
    private PartidaRepository partidaRepository;

    private Partida partida;
    private Partida partidaEsperando;
    private Partida partidaJugando;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        partida = new Partida();
        partida.setId(1);
        partida.setNombre("Partida Test");
        partida.setEstado(PartidaEstado.ESPERANDO);
        partida.setOwnerPartida(1);

        partidaEsperando = new Partida();
        partidaEsperando.setId(2);
        partidaEsperando.setNombre("Partida Esperando");
        partidaEsperando.setEstado(PartidaEstado.ESPERANDO);
        partidaEsperando.setOwnerPartida(1);

        partidaJugando = new Partida();
        partidaJugando.setId(3);
        partidaJugando.setNombre("Partida Jugando");
        partidaJugando.setEstado(PartidaEstado.JUGANDO);
        partidaJugando.setOwnerPartida(1);
    }

    @Test
    public void testFindByNombre() {
        String nombre = "Partida Test";
        when(partidaRepository.findByNombre(nombre)).thenReturn(List.of(partida));

        List<Partida> result = partidaRepository.findByNombre(nombre);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(nombre, result.get(0).getNombre());
        verify(partidaRepository, times(1)).findByNombre(nombre);
    }

    @Test
    public void testFindByEstado() {
        PartidaEstado estado = PartidaEstado.ESPERANDO;
        when(partidaRepository.findByEstado(estado)).thenReturn(List.of(partida, partidaEsperando));

        List<Partida> result = partidaRepository.findByEstado(estado);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getEstado() == estado));
        verify(partidaRepository, times(1)).findByEstado(estado);
    }

    @Test
    public void testFindByNombreAndEstado() {
        String nombre = "Partida Test";
        PartidaEstado estado = PartidaEstado.ESPERANDO;

        when(partidaRepository.findByNombreAndEstado(nombre, estado)).thenReturn(List.of(partida));

        List<Partida> result = partidaRepository.findByNombreAndEstado(nombre, estado);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(nombre, result.get(0).getNombre());
        assertEquals(estado, result.get(0).getEstado());
        verify(partidaRepository, times(1)).findByNombreAndEstado(nombre, estado);
    }

    @Test
    public void testFindByOwnerPartidaAndEstado() {
        Integer ownerPartida = 1;
        List<PartidaEstado> estados = List.of(PartidaEstado.ESPERANDO, PartidaEstado.JUGANDO);

        when(partidaRepository.findByOwnerPartidaAndEstado(ownerPartida, estados)).thenReturn(List.of(partida, partidaEsperando));

        List<Partida> result = partidaRepository.findByOwnerPartidaAndEstado(ownerPartida, estados);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> estados.contains(p.getEstado())));
        verify(partidaRepository, times(1)).findByOwnerPartidaAndEstado(ownerPartida, estados);
    }
}
