package es.us.dp1.lx_xy_24_25.your_game_name.amistad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithMockUser;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AmistadNoExisteException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.SolicitudEnviadaException;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@SpringBootTest
@WithMockUser
public class AmistadServiceTest {

    @Mock
    private AmistadRepository amistadRepository;

    @Mock
    private UserService userService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private AmistadService amistadService;

    private User remitente;
    private User destinatario1;
    private User destinatario2;
    private Amistad amistad1;
    private Amistad amistad2;
    private Amistad amistad3;
    
    @BeforeEach
    void setup() {
        remitente = new User();
        remitente.setId(1);
        remitente.setUsername("Remitente");
        remitente.setConectado(true);

        destinatario1 = new User();
        destinatario1.setId(2);
        destinatario1.setUsername("Destinatario1");
        destinatario1.setConectado(true);

        destinatario2 = new User();
        destinatario2.setId(3);
        destinatario2.setUsername("Destinatario2");
        destinatario2.setConectado(false);

        amistad1 = new Amistad();
        amistad1.setId(1);
        amistad1.setRemitente(remitente);
        amistad1.setDestinatario(destinatario1);
        amistad1.setEstadoAmistad(EstadoAmistad.ACEPTADA);

        amistad2 = new Amistad();
        amistad2.setId(2);
        amistad2.setRemitente(remitente);
        amistad2.setDestinatario(destinatario2);
        amistad2.setEstadoAmistad(EstadoAmistad.ACEPTADA);

        amistad3 = new Amistad();
        amistad3.setId(3);
        amistad3.setRemitente(destinatario1);
        amistad3.setDestinatario(remitente);
        amistad3.setEstadoAmistad(EstadoAmistad.PENDIENTE);
    }

    @Test
    void shouldSaveAmistad() {
        when(amistadRepository.save(any(Amistad.class))).thenReturn(amistad1);

        Amistad result = amistadService.saveAmistad(amistad1);

        assertNotNull(result);
        assertEquals(amistad1.getId(), result.getId());
        verify(amistadRepository, times(1)).save(amistad1);
    }

    @Test
    void shouldFindAllAmistades() {
        List<Amistad> amistades = List.of(amistad1);
        when(amistadRepository.findAll()).thenReturn(amistades);

        Iterable<Amistad> result = amistadService.findAll();

        assertNotNull(result);
        assertEquals(1, ((List<Amistad>) result).size());
        verify(amistadRepository, times(1)).findAll();
    }

    @Test
    void shouldFindAmistadById() {
        when(amistadRepository.findById(1)).thenReturn(Optional.of(amistad1));

        Optional<Amistad> result = amistadService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(amistad1.getId(), result.get().getId());
        verify(amistadRepository, times(1)).findById(1);
    }

    @Test
    void shouldFindAmistadById_NotFound() {
        when(amistadRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Amistad> result = amistadService.findById(1);

        assertTrue(result.isEmpty());
        verify(amistadRepository, times(1)).findById(1);
    }

    @Test
    void shouldDeleteAmistad() {
        doNothing().when(amistadRepository).deleteById(1);

        amistadService.deleteAmistad(1);

        verify(amistadRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldDeleteAmistad_NotFound() {
        doThrow(new IllegalArgumentException("No existe la amistad")).when(amistadRepository).deleteById(1);

        assertThrows(IllegalArgumentException.class, () -> amistadService.deleteAmistad(1));

        verify(amistadRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldUpdateAmistad() {

        Amistad updatedAmistad = new Amistad();
        updatedAmistad.setEstadoAmistad(EstadoAmistad.PENDIENTE);

        when(amistadRepository.findById(1)).thenReturn(Optional.of(amistad1));
        when(amistadRepository.save(eq(amistad1))).thenReturn(amistad1);

        Amistad result = amistadService.updateAmistad(updatedAmistad, 1);

        verify(amistadRepository, times(1)).findById(1);
        verify(amistadRepository, times(1)).save(amistad1);

        assertNotNull(result);
        assertEquals(EstadoAmistad.PENDIENTE, result.getEstadoAmistad());
        assertEquals(1, result.getId());
    }



    @Test
    void shouldUpdateAmistad_NotFound() {
        Amistad updatedAmistad = new Amistad();
        updatedAmistad.setId(1);

        when(amistadRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> amistadService.updateAmistad(updatedAmistad, 1));

        verify(amistadRepository, times(1)).findById(1);
        verify(amistadRepository, times(0)).save(updatedAmistad);
    }

    @Test
    void shouldGetAllMyFriends() {
        when(amistadRepository.getAllMyAmistad(1)).thenReturn(List.of(amistad1, amistad2));

        List<User> result = amistadService.getAllMyFriends(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(destinatario1));
        assertTrue(result.contains(destinatario2));

        verify(amistadRepository, times(1)).getAllMyAmistad(1);
    }

    @Test
    void shouldGetAllMyFriends_VaciaSiNoTieneAmigos() {
        when(amistadRepository.getAllMyAmistad(1)).thenReturn(List.of());

        List<User> result = amistadService.getAllMyFriends(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(amistadRepository, times(1)).getAllMyAmistad(1);
    }

    @Test
    void shouldGetAllMyFriends_ExcluyendoAmistadesNoAcecptadas() {
        amistad1.setEstadoAmistad(EstadoAmistad.PENDIENTE);
    
        when(amistadRepository.getAllMyAmistad(1)).thenReturn(List.of(amistad1, amistad2));
    
        List<User> result = amistadService.getAllMyFriends(1);
    
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(destinatario2));
    
        verify(amistadRepository, times(1)).getAllMyAmistad(1);
    }

    @Test
    void shouldGetAllMyConnectedFriends() {
        when(amistadRepository.getAllMyAmistad(1)).thenReturn(List.of(amistad1, amistad2));

        List<User> result = amistadService.getAllMyConnectedFriends(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(destinatario1));
        assertFalse(result.contains(destinatario2));

        verify(amistadRepository, times(1)).getAllMyAmistad(1);
    }

    @Test
    void shouldGetAllMyConnectedFriends_SinAmigosConetados() {
        destinatario1.setConectado(false);
        destinatario2.setConectado(false);

        when(amistadRepository.getAllMyAmistad(1)).thenReturn(List.of(amistad1, amistad2));

        List<User> result = amistadService.getAllMyConnectedFriends(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(amistadRepository, times(1)).getAllMyAmistad(1);
    }

    @Test
    void shouldGetAllMyConnectedFriends_VaciaSiNoTieneAmigos() {
        when(amistadRepository.getAllMyAmistad(1)).thenReturn(List.of());

        List<User> result = amistadService.getAllMyConnectedFriends(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(amistadRepository, times(1)).getAllMyAmistad(1);
    }

    @Test
    void shouldGetAllMySolicitudes_SolicitudesPendientes() {
        when(amistadRepository.getAllMyAmistad(remitente.getId())).thenReturn(List.of(amistad1, amistad3));

        List<User> pendingRequests = amistadService.getAllMySolicitudes(remitente.getId());

        assertNotNull(pendingRequests);
        assertEquals(1, pendingRequests.size());
        assertEquals(destinatario1.getUsername(), pendingRequests.get(0).getUsername());
    }

    @Test
    void getAllMySolicitudes_VaciaSiNoHayAmistadesPendientes() {
        when(amistadRepository.getAllMyAmistad(remitente.getId())).thenReturn(List.of());

        List<User> pendingRequests = amistadService.getAllMySolicitudes(remitente.getId());

        assertNotNull(pendingRequests);
        assertTrue(pendingRequests.isEmpty());
    }

    @Test
    void shouldGetOneAmistad() {
        when(amistadRepository.getOneAmistad(remitente.getId(), destinatario1.getId())).thenReturn(amistad1);

        Amistad amistad = amistadService.getOneAmistad(remitente.getId(), destinatario1.getId());

        assertNotNull(amistad);
        assertEquals(remitente.getId(), amistad.getRemitente().getId());
        assertEquals(destinatario1.getId(), amistad.getDestinatario().getId());
        assertEquals(EstadoAmistad.ACEPTADA, amistad.getEstadoAmistad());
    }

    @Test
    void shouldGetOneAmistad_NotFound() {
        when(amistadRepository.getOneAmistad(1, 99)).thenReturn(null);

        assertThrows(AmistadNoExisteException.class, () -> amistadService.getOneAmistad(1, 99));
    }

    @Test
    void shouldEnviarSolicitudDeAmistad() {
        when(userService.findUser(1)).thenReturn(remitente);
        when(userService.findUser("3")).thenReturn(destinatario2);
        when(amistadRepository.getOneAmistad(3, 1)).thenReturn(null);
        when(amistadRepository.save(any(Amistad.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Amistad result = amistadService.enviarSolicitudDeAmistad(1, "3");

        assertNotNull(result);
        assertEquals(remitente, result.getRemitente());
        assertEquals(destinatario2, result.getDestinatario());
        assertEquals(EstadoAmistad.PENDIENTE, result.getEstadoAmistad());
        verify(messagingTemplate).convertAndSend(eq("/topic/amistad/3"), any(List.class));
    }

    @Test
    void shouldEnviarSolicitudDeAmistad_NoAmigoExistente() {
        when(userService.findUser(1)).thenReturn(remitente);
        when(userService.findUser("2")).thenReturn(destinatario1);
        when(amistadRepository.getOneAmistad(2, 1)).thenReturn(amistad1);

        SolicitudEnviadaException exception = assertThrows(SolicitudEnviadaException.class, () ->
                amistadService.enviarSolicitudDeAmistad(1, "2"));

        assertEquals("no puedes enviar una solicitud a alguien que ya es tu amigo", exception.getMessage());
    }

    @Test
    void shouldEnviarSolicitudDeAmistad_NoSolicitudDuplicada() {
        when(userService.findUser(1)).thenReturn(remitente);
        when(userService.findUser("2")).thenReturn(destinatario1);
        when(amistadRepository.getOneAmistad(2, 1)).thenReturn(amistad3);

        SolicitudEnviadaException exception = assertThrows(SolicitudEnviadaException.class, () ->
                amistadService.enviarSolicitudDeAmistad(1, "2"));

        assertEquals("ya tienes una solicitud pendiente", exception.getMessage());
    }

    @Test
    void shouldEnviarSolicitudDeAmistad_NoSolicitudASiMismo() {
        when(userService.findUser(1)).thenReturn(remitente);
        when(userService.findUser("1")).thenReturn(remitente);

        SolicitudEnviadaException exception = assertThrows(SolicitudEnviadaException.class, () ->
                amistadService.enviarSolicitudDeAmistad(1, "1"));

        assertEquals("no puedes ser tu propio amigo", exception.getMessage());
    }

    @Test
    void shouldAceptarORechazarSolicitudDeAmistad_AceptaSolicitud() {
        when(amistadRepository.getOneAmistad(2, 1)).thenReturn(amistad3);
        when(amistadRepository.save(any(Amistad.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Amistad result = amistadService.aceptarORechazarSolicitudDeAmistad(2, 1, true);

        assertNotNull(result);
        assertEquals(EstadoAmistad.ACEPTADA, result.getEstadoAmistad());
        verify(messagingTemplate).convertAndSend(eq("/topic/amistad/2"), any(List.class));
    }

    @Test
    void shouldAceptarORechazarSolicitudDeAmistad_RechazaSolicitud() {
        when(amistadRepository.getOneAmistad(2, 1)).thenReturn(amistad3);
        when(amistadRepository.save(any(Amistad.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Amistad result = amistadService.aceptarORechazarSolicitudDeAmistad(2, 1, false);

        assertNotNull(result);
        assertEquals(EstadoAmistad.RECHAZADA, result.getEstadoAmistad());
        verify(messagingTemplate).convertAndSend(eq("/topic/amistad/2"), any(List.class));
    }

}
