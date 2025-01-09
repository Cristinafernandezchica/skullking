package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id", callSuper = true)

public class Baza extends BaseEntity{

    @Enumerated(EnumType.STRING)
    private PaloBaza paloBaza;

    @NotNull
    @Min(0)
    @Max(10)
    private Integer numBaza;

    @ManyToOne(optional = true, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "jugador_id")
	private Jugador ganador;

    @ManyToOne(optional = true, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "carta_id")
    private Carta cartaGanadora;

    @ManyToOne(optional = true, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "ronda_id")
	private Ronda ronda;

    @NotNull
    private List<Integer> turnos;
}
