package es.us.dp1.lx_xy_24_25.your_game_name.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import org.json.JSONPropertyIgnore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import es.us.dp1.lx_xy_24_25.your_game_name.jugador.Jugador;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Transient;

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

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "authority")
	Authorities authority;

	// Propiedades para las estadísticas de los usuarios
	@Transient
    private Integer numPartidasJugadas = 0;

    @Transient
    private Integer numPartidasGanadas = 0;

    @Transient
    private Integer numPuntosGanados = 0;

    // Optional utility methods for dynamic initialization
    public Integer getNumPartidasJugadas() {
        return numPartidasJugadas != null ? numPartidasJugadas : 0;
    }

    public Integer getNumPartidasGanadas() {
        return numPartidasGanadas != null ? numPartidasGanadas : 0;
    }

    public Integer getNumPuntosGanados() {
        return numPuntosGanados != null ? numPuntosGanados : 0;
    }

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
