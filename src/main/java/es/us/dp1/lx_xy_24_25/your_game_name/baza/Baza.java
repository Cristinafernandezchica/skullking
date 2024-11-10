package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
// import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.Ronda;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id", callSuper = true)

public class Baza extends BaseEntity{

    @Enumerated(EnumType.STRING)
    private TipoCarta tipoCarta;


    @ManyToOne(optional = true)
    @JoinColumn(name = "jugador_id")
	private Jugador ganador;

    @ManyToOne(optional = true)
	@JoinColumn(name = "carta_id")
    private Carta cartaGanadora;


    //@ManyToOne(optional = true)
	//@JoinColumn(name = "ronda_id")
	//private Ronda ronda;

    public Baza() {}

}
