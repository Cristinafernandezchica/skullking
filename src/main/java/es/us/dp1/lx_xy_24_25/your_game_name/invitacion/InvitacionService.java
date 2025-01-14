package es.us.dp1.lx_xy_24_25.your_game_name.invitacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.InvitacionException;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import jakarta.validation.Valid;

@Service
public class InvitacionService {
    private InvitacionRepository InvitacionRepository;
    private SimpMessagingTemplate messagingTemplate;
    private UserService userService;
    
    @Autowired
    public InvitacionService(InvitacionRepository InvitacionRepository, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.InvitacionRepository = InvitacionRepository;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    //save a Invitacion en la base de datos
    @Transactional
    public Invitacion saveInvitacion(Invitacion Invitacion) {
        return InvitacionRepository.save(Invitacion);
    }
    // listar todas las Invitacions de la base de datos
    @Transactional(readOnly = true)
    public Iterable<Invitacion> findAll() {
        return InvitacionRepository.findAll();
    }
    //obtener Invitacion por pk
    @Transactional(readOnly = true)
    public Optional<Invitacion> findById(Integer id) {
        return InvitacionRepository.findById(id);
    }
    //borrar Invitacion por pk
    @Transactional
    public void deleteInvitacion(Integer id) {
        InvitacionRepository.deleteById(id);
    }
    
    //actualizar Invitacion
    @Transactional
	public Invitacion updateInvitacion(@Valid Invitacion Invitacion, Integer idToUpdate) {
		Invitacion toUpdate = findById(idToUpdate).get();
		BeanUtils.copyProperties(Invitacion, toUpdate, "id");
		InvitacionRepository.save(toUpdate);
		return toUpdate;
	}

    @Transactional(readOnly = true)
    public List<Invitacion> getTodasMisInvitaciones(Integer destinatarioId){
        return InvitacionRepository.getAllMyInvitaciones(destinatarioId);
    }
    
    @Transactional(readOnly = true)
    public Invitacion getOne(Integer remitenteId, Integer destinatarioId){
        return InvitacionRepository.getOneInvitacion(destinatarioId, remitenteId);
    }

    @Transactional
    public Invitacion enviarInvitacion(Invitacion invitacion) {
        if(invitacion.getDestinatario().equals(invitacion.getRemitente())){
            throw new InvitacionException("No puedes enviar una invitacion a ti mismo");
        }
        
        Invitacion yaExiste = InvitacionRepository.getOneInvitacion(invitacion.getDestinatario().getId(), invitacion.getRemitente().getId());
        
        if(yaExiste != null){
            throw new InvitacionException("Ya existe una invitacion");
        }
        return InvitacionRepository.save(invitacion);
    }
}