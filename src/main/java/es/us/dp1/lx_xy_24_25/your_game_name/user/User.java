package es.us.dp1.lx_xy_24_25.your_game_name.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "appusers")
public class User extends BaseEntity {

	@Column(unique = true)
	String username;

	String password;

    @Column(name = "descripcion_perfil")
	@Size(max = 100)
	private String descripcionPerfil;

	@Lob
    @Column(name = "imagen_perfil")
	private String imagenPerfil;


	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "authority")
	Authorities authority;

	private Boolean conectado;

	// Propiedades para las estadísticas de los usuarios
	@Column(name = "num_partidas_jugadas", nullable = true)
	private Integer numPartidasJugadas = 0;

	@Column(name = "num_partidas_ganadas", nullable = true)
	private Integer numPartidasGanadas = 0;

	@Column(name = "num_puntos_ganados", nullable = true)
	private Integer numPuntosGanados = 0;

	public Boolean hasAuthority(String auth) {
		return authority.getAuthority().equals(auth);
	}

	public Boolean hasAnyAuthority(String... authorities) {
		Boolean cond = false;
		for (String auth : authorities) {
			if (auth.equals(authority.getAuthority()))
				cond = true;
		}
		return cond;
	}

}
