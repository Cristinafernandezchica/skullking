package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = true)
public class Chat extends BaseEntity{
    @ManyToOne
    @JoinColumn(name="jugador_id")
    private Jugador jugador;

    private String mensaje;
}
