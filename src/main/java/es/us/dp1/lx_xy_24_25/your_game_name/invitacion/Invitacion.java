package es.us.dp1.lx_xy_24_25.your_game_name.invitacion;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id", callSuper = true)
public class Invitacion extends BaseEntity{
    
    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private User remitente;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private User destinatario;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private Partida partida;

    @NotNull
    private Boolean espectador;
}
