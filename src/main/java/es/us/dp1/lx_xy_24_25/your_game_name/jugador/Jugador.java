package es.us.dp1.lx_xy_24_25.your_game_name.jugador;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of="id", callSuper = true)
public class Jugador extends BaseEntity {
    
    private Integer puntuacion;

    @Column(name = "partida_id")
    private Integer partidaId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer turno;

    
}
