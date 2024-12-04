package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
import java.util.List;

public class CalculoGanadorContext {

    private CalculoGanadorState state;

    public void setState(CalculoGanadorState state) {
        this.state = state;
    }

    public Truco calcularGanador(Baza baza, List<Truco> trucosBaza) {
        if (state == null) {
            throw new IllegalStateException("El estado no está definido.");
        }
        return state.calcularGanador(baza, trucosBaza);
    }
    
}
