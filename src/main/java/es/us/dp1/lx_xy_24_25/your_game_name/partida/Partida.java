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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
public class Partida extends BaseEntity{
    
    @NotBlank(message = "El nombre de la partida no puede estar vacío")
    @NotNull
    @Size(min = 1, max = 50, message = "El nombre de la partida puede tener como máximo 50 caracteres")
    private String nombre;

    @NotNull
    private LocalDateTime inicio = LocalDateTime.now();

    @Nullable
    private LocalDateTime fin;

    @Enumerated(EnumType.STRING)
    private PartidaEstado estado;

    @NotNull
    private Integer ownerPartida;

    private Integer turnoActual;
}
