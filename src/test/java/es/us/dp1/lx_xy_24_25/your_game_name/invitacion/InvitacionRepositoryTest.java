package es.us.dp1.lx_xy_24_25.your_game_name.invitacion;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@ExtendWith(MockitoExtension.class)
public class InvitacionRepositoryTest {

    @Mock
    private InvitacionRepository invitacionRepository;

    private Invitacion invitacion;
    private User remitente;
    private User destinatario;

    @BeforeEach
    void setup() {
        remitente = new User();
        remitente.setId(1);
        remitente.setUsername("Remitente");

        destinatario = new User();
        destinatario.setId(2);
        destinatario.setUsername("Destinatario");

        invitacion = new Invitacion();
        invitacion.setId(1);
        invitacion.setRemitente(remitente);
        invitacion.setDestinatario(destinatario);
    }

    @Test
    void shouldGetAllMyInvitaciones() {
        when(invitacionRepository.getAllMyInvitaciones(2)).thenReturn(List.of(invitacion));

        List<Invitacion> result = invitacionRepository.getAllMyInvitaciones(2);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(destinatario.getId(), result.get(0).getDestinatario().getId());

        verify(invitacionRepository, times(1)).getAllMyInvitaciones(2);
    }

    @Test
    void shouldGetAllMyInvitaciones_Vacia() {
        when(invitacionRepository.getAllMyInvitaciones(2)).thenReturn(List.of());

        List<Invitacion> result = invitacionRepository.getAllMyInvitaciones(2);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(invitacionRepository, times(1)).getAllMyInvitaciones(2);
    }

    @Test
    void shouldGetOneInvitacion() {
        when(invitacionRepository.getOneInvitacion(2, 1)).thenReturn(invitacion);
    
        Invitacion result = invitacionRepository.getOneInvitacion(2, 1);
    
        assertNotNull(result);
        assertEquals(remitente, result.getRemitente());
        assertEquals(destinatario, result.getDestinatario());
    
        verify(invitacionRepository, times(1)).getOneInvitacion(2, 1);
    }
        
    @Test
    void shouldGetOneInvitacion_NoTExists() {
        when(invitacionRepository.getOneInvitacion(2, 1)).thenReturn(null);

        Invitacion result = invitacionRepository.getOneInvitacion(2, 1);

        assertNull(result);

        verify(invitacionRepository, times(1)).getOneInvitacion(2, 1);
    }

    @Test
    void shouldGetOneInvitacion_NoTExistsDestinatario() {
        when(invitacionRepository.getOneInvitacion(3, 1)).thenReturn(null);

        Invitacion result = invitacionRepository.getOneInvitacion(3, 1);

        assertNull(result);

        verify(invitacionRepository, times(1)).getOneInvitacion(3, 1);
    }

    @Test
    void shouldGetOneInvitacion_NoTExistsRemitente() {
        when(invitacionRepository.getOneInvitacion(2, 4)).thenReturn(null);

        Invitacion result = invitacionRepository.getOneInvitacion(2, 4);

        assertNull(result);

        verify(invitacionRepository, times(1)).getOneInvitacion(2, 4);
    }

}

