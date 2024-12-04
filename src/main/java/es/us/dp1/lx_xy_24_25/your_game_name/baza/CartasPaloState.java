package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
import java.util.List;
import java.util.Comparator;

public class CartasPaloState implements CalculoGanadorState{
    @Override
    public Truco calcularGanador(Baza baza, List<Truco> trucosBaza) {
        return trucosBaza.stream()
            .filter(truco -> truco.getCarta().getTipoCarta().equals(baza.getTipoCarta()))
            .max(Comparator.comparing(t -> t.getCarta().getNumero()))
            .orElse(null);
    }
}
