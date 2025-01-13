package es.us.dp1.lx_xy_24_25.your_game_name.amistad;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AmistadNoExisteException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.SolicitudEnviadaException;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.JugadorService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import jakarta.validation.Valid;

@Service
public class AmistadService {
    private AmistadRepository amistadRepository;
    private UserService userService;
    private SimpMessagingTemplate messagingTemplate;
    private JugadorService jugadorService;

    
    @Autowired
    public AmistadService(AmistadRepository amistadRepository, UserService userService, SimpMessagingTemplate messagingTemplate,JugadorService jugadorService) {
        this.amistadRepository = amistadRepository;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.jugadorService = jugadorService;
    }

    //save a Amistad en la base de datos
    @Transactional
    public Amistad saveAmistad(Amistad amistad) {
        return amistadRepository.save(amistad);
    }
    // listar todas las Amistads de la base de datos
    @Transactional(readOnly = true)
    public Iterable<Amistad> findAll() {
        return amistadRepository.findAll();
    }
    //obtener Amistad por pk
    @Transactional(readOnly = true)
    public Optional<Amistad> findById(Integer id) {
        return amistadRepository.findById(id);
    }
    //borrar Amistad por pk
    @Transactional
    public void deleteAmistad(Integer id) {
        amistadRepository.deleteById(id);
    }
    
    //actualizar Amistad
    @Transactional
	public Amistad updateAmistad(@Valid Amistad Amistad, Integer idToUpdate) {
		Amistad toUpdate = findById(idToUpdate).get();
		BeanUtils.copyProperties(Amistad, toUpdate, "id");
		amistadRepository.save(toUpdate);

		return toUpdate;
	}

    @Transactional(readOnly = true)
    public List<User> getAllMyFriends(Integer remitenteId){
        List<Amistad> misAmistades= amistadRepository.getAllMyAmistad(remitenteId);
        List<User> misAmigos =new ArrayList<>();
        for (Amistad amistad : misAmistades){
            if(amistad.getEstadoAmistad().equals(EstadoAmistad.ACEPTADA)){
                if(amistad.getRemitente().getId()!= remitenteId){
                    misAmigos.add(amistad.getRemitente());
                }
                else if(amistad.getDestinatario().getId()!= remitenteId){
                    misAmigos.add(amistad.getDestinatario());
                }
        }
        }
        return misAmigos;
    }

    @Transactional(readOnly = true)
    public List<User> getAllMyConnectedFriends(Integer remitenteId){
        List<User> result =getAllMyFriends(remitenteId).stream()
        .filter(x->x.getConectado()).toList();
        return result;
    }

    @Transactional(readOnly = true)
    public List<User> getAllMySolicitudes(Integer remitenteId){
        List<Amistad> misAmistades= amistadRepository.getAllMyAmistad(remitenteId);
        List<User> misAmigos =new ArrayList<>();
        for (Amistad amistad : misAmistades){
            if(amistad.getEstadoAmistad().equals(EstadoAmistad.PENDIENTE)){
                if(amistad.getDestinatario().getId()== remitenteId){
                    misAmigos.add(amistad.getRemitente());
                }
        }
        }
        return misAmigos;
    }

    @Transactional(readOnly = true)
    public Amistad getOneAmistad(Integer remitenteId,Integer destinatarioId){
        Amistad a = amistadRepository.getOneAmistad(remitenteId, destinatarioId);
        if(a==null){
            throw new AmistadNoExisteException();
        }
        return a;
    }

    @Transactional
    public Amistad eviarSolicitudDeAmistad(Integer remitenteId, String destinatarioId){
        User remitente = userService.findUser(remitenteId);
        User destinatario = userService.findUser(destinatarioId);
        Amistad solicitud = new Amistad();
        solicitud.setRemitente(remitente);
        solicitud.setDestinatario(destinatario);
        solicitud.setEstadoAmistad(EstadoAmistad.PENDIENTE);
        Amistad laAmistadYaExiste = amistadRepository.getOneAmistad(solicitud.getDestinatario().getId(),
        solicitud.getRemitente().getId());
        
        if(laAmistadYaExiste!=null && laAmistadYaExiste.getEstadoAmistad().equals(EstadoAmistad.ACEPTADA)){
            throw new SolicitudEnviadaException("no puedes enviar una solicitud a alguien que ya es tu amigo");
        }
        else if(laAmistadYaExiste!=null && laAmistadYaExiste.getEstadoAmistad().equals(EstadoAmistad.RECHAZADA)){
            throw new SolicitudEnviadaException("no puedes enviar una solicitud a alguien que ya la rechazo, NO SEAS PESAO");
        }
        else if(laAmistadYaExiste!=null && laAmistadYaExiste.getEstadoAmistad().equals(EstadoAmistad.PENDIENTE)){
            throw new SolicitudEnviadaException("ya tienes una solicitud pendiente");
        }
        if(solicitud.getDestinatario().getId() == solicitud.getRemitente().getId()){
            throw new SolicitudEnviadaException("no puedes ser tu propio amigo");
        }

        Amistad result=amistadRepository.save(solicitud);
        messagingTemplate.convertAndSend("/topic/amistad/" + destinatarioId, getAllMySolicitudes(destinatario.getId()));
        return result;
    }

    @Transactional
    public Amistad aceptarORechazarSolicitudDeAmistad(Integer remitenteId, Integer destinatarioId, Boolean aceptar){
    Amistad amistadAAceptar = amistadRepository.getOneAmistad(remitenteId, destinatarioId);
    if(aceptar==false){
    amistadAAceptar.setEstadoAmistad(EstadoAmistad.RECHAZADA);}
    else if(aceptar==true){
        amistadAAceptar.setEstadoAmistad(EstadoAmistad.ACEPTADA);}

    Amistad result= amistadRepository.save(amistadAAceptar);
    messagingTemplate.convertAndSend("/topic/amistad/" + remitenteId, getAllMyFriends(remitenteId));
    return result;
    }


    @Transactional(readOnly = true)
    public List<User> puedesVerPartida(Integer partidaId,Integer miId){
        List<User> result = new ArrayList<User>();
        List<User> amigos = getAllMyFriends(miId);
        List<User> jugadoresEnPartida = jugadorService.findJugadoresByPartidaId(partidaId).stream().map(x->x.getUsuario()).toList();
        for(User amigo : amigos){
            if(getAllMyFriends(amigo.getId()).containsAll(jugadoresEnPartida)){
                result.add(amigo);
            }
        }
        return result;
    }

}