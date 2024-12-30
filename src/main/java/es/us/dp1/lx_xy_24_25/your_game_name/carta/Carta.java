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

    private String imagenFrontal;

    private String imagenTrasera="./images/cartas/parte_trasera.png";


    public Boolean esPersonaje(){
        return tipoCarta == TipoCarta.pirata||
           tipoCarta == TipoCarta.sirena ||
           tipoCarta == TipoCarta.skullking ||
           tipoCarta == TipoCarta.tigresa;
    }

    public Boolean esCartaEspecial(){
        return tipoCarta == TipoCarta.pirata ||
        tipoCarta == TipoCarta.sirena ||
        tipoCarta == TipoCarta.skullking ||
        tipoCarta == TipoCarta.tigresa ||
        tipoCarta == TipoCarta.banderaBlanca;
    }

    public Boolean esTriunfo(){
        return tipoCarta == TipoCarta.triunfo;
    }

    public Boolean esPaloColor(){
        return tipoCarta == TipoCarta.morada || 
                tipoCarta == TipoCarta.amarillo || 
                tipoCarta == TipoCarta.verde;
    }

    /*
    public Boolean esCatorce(){
        return numero == 14;
    }
    */

    public Boolean esCatorce(){
        Boolean esCatorce = false;
        if(numero != null && numero == 14){
            esCatorce = true;
        }
        return esCatorce;
    }

    

}
