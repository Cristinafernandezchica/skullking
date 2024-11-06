package es.us.dp1.lx_xy_24_25.your_game_name.truco;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.persistence.JoinColumn;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;

@Entity
@Getter
@Setter
// @EqualsAndHashCode(of = "id", callSuper = true)
@Table(name = "trucos")
public class Truco extends BaseEntity{

    // Asociación con la entidad Baza

    // @Valid
	@ManyToOne(optional = false)
    @JoinColumn(name = "baza_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
    // @Column(name = "baza")
	private Baza baza;


    // Asociación con la entidad Mano

    // @Valid
	@ManyToOne(optional = false)
    @JoinColumn(name = "mano_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
    // @Column(name = "mano")
	private Mano mano;

    // Jugador sacado de la relación con la entidad Mano
    // @Column(name = "id_jugador")
    private Integer jugador;


    // Carta sacada de la relación con la entidad Mano
    // @Column(name = "id_carta")
    private Integer idCarta;

    // Turno correspondiente a la posición del id_jugador en la lista listaJugadores del id_partida
    // @Column(name = "turno")
    private Integer turno;

    public Boolean esCartaDeMano() {
		return mano.getCartas().contains(idCarta);
	}

    
}
