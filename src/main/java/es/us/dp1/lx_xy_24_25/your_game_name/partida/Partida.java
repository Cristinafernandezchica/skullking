package es.us.dp1.lx_xy_24_25.your_game_name.partida;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
public class Partida extends BaseEntity{
    
    @NotBlank
    @NotNull
    @Size(min = 1)
    private String nombre;

    @NotNull
    private LocalDateTime inicio;

    private LocalDateTime fin;

    @Enumerated(EnumType.STRING)
    private PartidaEstado estado;

    @Nullable
    private  Integer ownerPartida;
}
