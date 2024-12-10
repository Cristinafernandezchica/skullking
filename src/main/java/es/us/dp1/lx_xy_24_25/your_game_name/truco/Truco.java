package es.us.dp1.lx_xy_24_25.your_game_name.truco;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.JoinColumn;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Truco extends BaseEntity{

    // @Valid
	@ManyToOne(optional = false)
    @JoinColumn(name = "baza_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Baza baza;

    @ManyToOne(optional = false)
    @JoinColumn(name = "jugador_id")
    private Jugador jugador;

    // @Valid
	@ManyToOne(optional = false)
    @JoinColumn(name = "mano_id")
	private Mano mano;


    // Carta sacada de la relación con la entidad Mano
    @ManyToOne(optional = true)
    @JoinColumn(name = "carta_id")
    private Carta carta;

    // Turno correspondiente a la posición del id_jugador en la lista listaJugadores del id_partida
    private Integer turno;
}
