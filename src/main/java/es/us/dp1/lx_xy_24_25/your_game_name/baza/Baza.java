package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
// import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.Ronda;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id", callSuper = true)

public class Baza extends BaseEntity{

    // TipoCarta debería ser paloBaza y solo puede ser palo, ninguna carta especial
    // además, si la primera carta es una bandera blanca, la segunda carta
    @Enumerated(EnumType.STRING)
    private TipoCarta tipoCarta;

    @NotNull
    @Min(0)
    @Max(10)
    private Integer numBaza;

    @ManyToOne(optional = true)
    @JoinColumn(name = "jugador_id")
	private Jugador ganador;

    @ManyToOne(optional = true)
	@JoinColumn(name = "truco_id")
    private Truco trucoGanador;

    @ManyToOne(optional = true)
	@JoinColumn(name = "ronda_id")
	private Ronda ronda;

}
