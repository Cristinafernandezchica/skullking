package es.us.dp1.lx_xy_24_25.your_game_name.bazaCartaManoDTO;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.carta.Carta;
import es.us.dp1.lx_xy_24_25.your_game_name.mano.Mano;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BazaCartaManoDTO {
    private Baza baza;
    private Carta carta;
    private Mano mano;
    private Integer turno;

    public BazaCartaManoDTO(){}

    public BazaCartaManoDTO(Baza baza,Carta carta, Mano mano,Integer turno){
        this.baza=baza;
        this.carta = carta;
        this.mano=mano;
        this.turno=turno;
    }
    
}
