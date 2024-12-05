package es.us.dp1.lx_xy_24_25.your_game_name.truco.trucoState;

import es.us.dp1.lx_xy_24_25.your_game_name.baza.Baza;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
import java.util.List;

public interface CalculoGanadorState {

    Truco calcularGanador(Baza baza, List<Truco> trucosBaza);

}