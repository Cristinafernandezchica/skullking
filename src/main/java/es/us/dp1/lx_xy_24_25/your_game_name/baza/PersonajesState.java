package es.us.dp1.lx_xy_24_25.your_game_name.baza;

import es.us.dp1.lx_xy_24_25.your_game_name.tipoCarta.TipoCarta;
import es.us.dp1.lx_xy_24_25.your_game_name.truco.Truco;
import java.util.List;

public class PersonajesState implements CalculoGanadorState{
    @Override
    public Truco calcularGanador(Baza baza, List<Truco> trucosBaza) {
        List<Truco> skullKing = trucosBaza.stream()
            .filter(truco -> truco.getCarta().getTipoCarta() == TipoCarta.skullking)
            .toList();

        List<Truco> sirenas = trucosBaza.stream()
            .filter(truco -> truco.getCarta().getTipoCarta() == TipoCarta.sirena)
            .toList();

        List<Truco> piratas = trucosBaza.stream()
            .filter(truco -> truco.getCarta().getTipoCarta() == TipoCarta.pirata)
            .toList();

        if (!skullKing.isEmpty() && !sirenas.isEmpty()) {
            return sirenas.get(0);
        } else if (!skullKing.isEmpty()) {
            return skullKing.get(0);
        } else if (!sirenas.isEmpty()) {
            if (!piratas.isEmpty()) {
                return piratas.get(0);
            } else {
                return sirenas.get(0);
            }
        } else if (!piratas.isEmpty()) {
            return piratas.get(0);
        }
        return null;
    }
}
