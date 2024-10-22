package partida;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.model.NamedEntity;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
public class Partida extends NamedEntity{

    List<Jugador> listaJugador;
    
}
