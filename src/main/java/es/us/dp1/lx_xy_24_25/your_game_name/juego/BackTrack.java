package es.us.dp1.lx_xy_24_25.your_game_name.juego;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.us.dp1.lx_xy_24_25.your_game_name.partida.PartidaService;
import es.us.dp1.lx_xy_24_25.your_game_name.ronda.RondaService;

@Service
public class BackTrack {
    private RondaService rondaService;
    private PartidaService partidaService;

    @Autowired
    public BackTrack(RondaService rondaService, PartidaService partidaService) {
        this.rondaService = rondaService;
        this.partidaService = partidaService;
    }

    
    
}
