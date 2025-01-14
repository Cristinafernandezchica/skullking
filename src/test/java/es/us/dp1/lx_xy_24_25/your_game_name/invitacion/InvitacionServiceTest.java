package es.us.dp1.lx_xy_24_25.your_game_name.invitacion;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.InvitacionException;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@ExtendWith(MockitoExtension.class)
class InvitacionServiceTest {

    @Mock
    private InvitacionRepository invitacionRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private UserService userService;

    @InjectMocks
    private InvitacionService invitacionService;

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
void shouldSaveInvitacion() {
    when(invitacionRepository.save(any(Invitacion.class))).thenReturn(invitacion);

    Invitacion result = invitacionService.saveInvitacion(invitacion);

    assertNotNull(result);
    assertEquals(invitacion.getId(), result.getId());
    assertEquals(invitacion.getRemitente(), result.getRemitente());
    assertEquals(invitacion.getDestinatario(), result.getDestinatario());

    verify(invitacionRepository, times(1)).save(invitacion);
}

@Test
void shouldFindAll() {
    when(invitacionRepository.findAll()).thenReturn(List.of(invitacion));

    Iterable<Invitacion> result = invitacionService.findAll();

    assertNotNull(result);
    assertTrue(((List<Invitacion>) result).contains(invitacion));

    verify(invitacionRepository, times(1)).findAll();
}

@Test
void shouldFindAll_Vacia() {
    when(invitacionRepository.findAll()).thenReturn(List.of());

    Iterable<Invitacion> result = invitacionService.findAll();

    assertNotNull(result);
    assertTrue(((List<Invitacion>) result).isEmpty());

    verify(invitacionRepository, times(1)).findAll();
}

@Test
void shouldFindById() {
    when(invitacionRepository.findById(1)).thenReturn(Optional.of(invitacion));

    Optional<Invitacion> result = invitacionService.findById(1);

    assertTrue(result.isPresent());
    assertEquals(invitacion.getId(), result.get().getId());

    verify(invitacionRepository, times(1)).findById(1);
}

@Test
void shouldFindById_NotFound() {
    when(invitacionRepository.findById(99)).thenReturn(Optional.empty());

    Optional<Invitacion> result = invitacionService.findById(99);

    assertTrue(result.isEmpty());

    verify(invitacionRepository, times(1)).findById(99);
}

@Test
void shouldDeleteInvitacion() {
    doNothing().when(invitacionRepository).deleteById(1);

    invitacionService.deleteInvitacion(1);

    Optional<Invitacion> result = invitacionService.findById(1);

    assertEquals(result, Optional.empty());

    verify(invitacionRepository, times(1)).deleteById(1);
}

@Test
void shouldUpdateInvitacion() {
    Invitacion updatedInvitacion = new Invitacion();
    updatedInvitacion.setRemitente(remitente);
    updatedInvitacion.setDestinatario(destinatario);

    when(invitacionRepository.findById(1)).thenReturn(Optional.of(invitacion));
    when(invitacionRepository.save(any(Invitacion.class))).thenReturn(updatedInvitacion);

    Invitacion result = invitacionService.updateInvitacion(updatedInvitacion, 1);

    assertNotNull(result);
    assertEquals(remitente, result.getRemitente());
    assertEquals(destinatario, result.getDestinatario());

    verify(invitacionRepository, times(1)).findById(1);
    verify(invitacionRepository, times(1)).save(any(Invitacion.class));
}

@Test
void shouldUpdateInvitacion_NotExist() {
    Invitacion updatedInvitacion = new Invitacion();

    when(invitacionRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> invitacionService.updateInvitacion(updatedInvitacion, 99));

    verify(invitacionRepository, times(1)).findById(99);
    verify(invitacionRepository, times(0)).save(any(Invitacion.class));
}

@Test
void shouldGetTodasMisInvitaciones() {
    when(invitacionRepository.getAllMyInvitaciones(2)).thenReturn(List.of(invitacion));

    List<Invitacion> result = invitacionService.getTodasMisInvitaciones(2);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(destinatario, result.get(0).getDestinatario());

    verify(invitacionRepository, times(1)).getAllMyInvitaciones(2);
}

@Test
void shouldGetTodasMisInvitaciones_Vacia() {
    when(invitacionRepository.getAllMyInvitaciones(2)).thenReturn(List.of());

    List<Invitacion> result = invitacionService.getTodasMisInvitaciones(2);

    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(invitacionRepository, times(1)).getAllMyInvitaciones(2);
}

@Test
void shouldGetOneInvitacion() {
    when(invitacionRepository.getOneInvitacion(destinatario.getId(), remitente.getId())).thenReturn(invitacion);

    Invitacion result = invitacionService.getOne(remitente.getId(), destinatario.getId());

    assertNotNull(result);
    assertEquals(invitacion.getId(), result.getId());
    assertEquals(remitente, result.getRemitente());
    assertEquals(destinatario, result.getDestinatario());

    verify(invitacionRepository, times(1)).getOneInvitacion(destinatario.getId(), remitente.getId());
}

@Test
void shouldGetOneInvitacion_NotFound() {
    when(invitacionRepository.getOneInvitacion(destinatario.getId(), remitente.getId())).thenReturn(null);

    Invitacion result = invitacionService.getOne(remitente.getId(), destinatario.getId());

    assertNull(result);

    verify(invitacionRepository, times(1)).getOneInvitacion(destinatario.getId(), remitente.getId());
}

@Test
void shouldEnviarInvitacion() {
    when(invitacionRepository.getOneInvitacion(destinatario.getId(), remitente.getId())).thenReturn(null);
    when(invitacionRepository.save(any(Invitacion.class))).thenReturn(invitacion);

    Invitacion result = invitacionService.enviarInvitacion(invitacion);

    assertNotNull(result);
    assertEquals(invitacion.getId(), result.getId());
    assertEquals(remitente, result.getRemitente());
    assertEquals(destinatario, result.getDestinatario());

    verify(invitacionRepository, times(1)).getOneInvitacion(destinatario.getId(), remitente.getId());
    verify(invitacionRepository, times(1)).save(invitacion);
}

@Test
void shouldEnviarInvitacion_YaExiste() {
    when(invitacionRepository.getOneInvitacion(destinatario.getId(), remitente.getId())).thenReturn(invitacion);

    InvitacionException exception = assertThrows(InvitacionException.class, () -> 
        invitacionService.enviarInvitacion(invitacion)
    );

    assertEquals("Ya existe una invitacion", exception.getMessage());

    verify(invitacionRepository, times(1)).getOneInvitacion(destinatario.getId(), remitente.getId());
    verify(invitacionRepository, times(0)).save(any(Invitacion.class));
}

@Test
void shouldEnviarInvitacion_ATiMismo() {
    invitacion.setDestinatario(remitente);

    InvitacionException exception = assertThrows(InvitacionException.class, () -> 
        invitacionService.enviarInvitacion(invitacion)
    );

    assertEquals("No puedes enviar una invitacion a ti mismo", exception.getMessage());

    verify(invitacionRepository, times(0)).getOneInvitacion(any(Integer.class), any(Integer.class));
    verify(invitacionRepository, times(0)).save(any(Invitacion.class));
}





}
