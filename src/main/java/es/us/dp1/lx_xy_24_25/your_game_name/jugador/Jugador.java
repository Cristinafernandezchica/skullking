package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of="id", callSuper = true)
public class Jugador extends BaseEntity {
    
    @NotNull
    private Integer puntos;


    @ManyToOne
    @JoinColumn(name = "partida_id")
    private Partida partida;      

    // hacer pull
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User usuario;

    @PositiveOrZero
    private Integer apuestaActual = 0;

}
