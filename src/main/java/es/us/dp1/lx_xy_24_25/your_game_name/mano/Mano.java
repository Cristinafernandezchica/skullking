package es.us.dp1.lx_xy_24_25.your_game_name.mano;

import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.Ronda;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@EqualsAndHashCode(of = "id", callSuper = true)
public class Mano extends BaseEntity{
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "jugador_id")
    private Jugador jugador;

    @PositiveOrZero(message = "El valor de la apuesta no puede ser menor que 0")
    private Integer apuesta;

    @PositiveOrZero
    private Integer resultado;

    @ManyToOne(optional = true)
    @JoinColumn(name = "ronda_id")
    private Ronda ronda;
    

    @ManyToMany
    @JoinTable(
        name = "carta_mano", // Nombre de la tabla intermedia
        joinColumns = @JoinColumn(name = "mano_id"), // Columna que hace referencia a mano
        inverseJoinColumns = @JoinColumn(name = "carta_id") // Columna que hace referencia a carta
    )
    private List<Carta> cartas; // Relación Many-to-Many con Carta

    public Boolean esApuestaIgualResultado(){
        return apuesta == resultado;
    }
    
}
