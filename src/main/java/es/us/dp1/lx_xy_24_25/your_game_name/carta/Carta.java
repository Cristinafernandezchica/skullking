package es.us.dp1.lx_xy_24_25.your_game_name.carta;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@EqualsAndHashCode(of = "id"  , callSuper = true)
public class Carta extends BaseEntity {
    
    private Integer numero;

    @Enumerated(EnumType.STRING)
    private TipoCarta tipoCarta;

}
