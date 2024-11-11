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
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "trucos")
public class Truco extends BaseEntity{

    // Asociación con la entidad Baza

    // @Valid
	@ManyToOne(optional = false)
    @JoinColumn(name = "baza_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
    // @Column(name = "baza")
	private Baza baza;

    private Integer jugador;

    // Asociación con la entidad Mano

    // @Valid
	@ManyToOne(optional = false)
    @JoinColumn(name = "mano_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Mano mano;


    // Carta sacada de la relación con la entidad Mano
    // @Column(name = "id_carta")
    private Integer idCarta;

    // Turno correspondiente a la posición del id_jugador en la lista listaJugadores del id_partida
    // @Column(name = "turno")
    private Integer turno;

    public Boolean esCartaDeMano() {
		return mano.getCartas().contains(idCarta);
	}

    public Truco() {}

    public Truco(Baza baza, Mano mano, Integer jugador, Integer idCarta, Integer turno) {
        this.baza = baza;
        this.mano = mano;
        this.jugador = jugador;
        this.idCarta = idCarta;
        this.turno = turno;
    }
    
}
