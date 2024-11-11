package es.us.dp1.lx_xy_24_25.your_game_name.ronda;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.partida.Partida;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")

public class Ronda extends BaseEntity{
    
    //@NotEmpty
    private Integer numRonda;

    // TO DO : revision de clases 
    //@NotEmpty
    private Integer bazaActual;

    
    //@NotEmpty
    private Integer numBazas;

    @Enumerated(EnumType.STRING)
    private RondaEstado estado;

    @ManyToOne(optional=false)
    @JoinColumn(name = "partida_id")
    private Partida partida;

}
