package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
public class Partida extends BaseEntity{
    
    @NotEmpty
    private String nombre;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    @Enumerated(EnumType.STRING)
    private PartidaEstado estado;

    @Nullable
    private  Integer ownerPartida;
}
