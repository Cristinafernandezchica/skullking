package es.us.dp1.lx_xy_24_25.your_game_name.invitacion;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
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
    @ManyToOne
    private User remitente;

    @NotNull
    @ManyToOne
    private User destinatario;

    private String link;

    @NotNull
    private Boolean espectador;
}
