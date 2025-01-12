export default class Trucos {
    static async fetchListaDeTrucos(bazaId, jwt) {
        try {
            const response = await fetch(`/api/v1/trucos/trucosBaza/${bazaId}`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Network response was not ok");
            }

            const data = await response.json();
            console.log("Este es la lista de trucos:", data);
            return data; // Devuelve la lista de trucos
        } catch (error) {
            console.error("Error encontrando trucos:", error);
            throw error; // Propaga el error para manejarlo en el componente que lo utiliza
        }
    }

    static async jugarTruco(cartaAJugar, tipoCarta, jwt, tuId, iniciarTruco, fetchMano, siguienteEstado, ListaDeTrucos, jugadores) {
        let cartaFinal = cartaAJugar;

        if (cartaAJugar.tipoCarta === "tigresa" && tipoCarta) {
            try {
                const response = await fetch(`/api/v1/cartas/tigresa/${tipoCarta}`, {
                    headers: {
                        "Authorization": `Bearer ${jwt}`,
                        "Content-Type": "application/json",
                    },
                });

                if (!response.ok) {
                    throw new Error("Network response was not ok");
                }

                const data = await response.json();
                console.log("Se está jugando tigresa:", data);
                cartaFinal = data;
            } catch (error) {
                console.error("Error fetching cambioTigresa:", error);
                throw error; // Propaga el error para manejarlo en niveles superiores
            }
        }

        console.log("Carta a jugar:", cartaFinal);
        await iniciarTruco(tuId, cartaFinal);
        console.log("Truco a jugar:", cartaFinal);
        await fetchMano(tuId);

        if (ListaDeTrucos.length + 1 === jugadores.length) {
            await siguienteEstado();
        }

        return cartaFinal; // Devuelve la carta final jugada
    }

    static async iniciarTruco(jugadorId, cartaAJugar, BazaActual, mano, ListaDeTrucos, jwt, fetchBazaActual) {
        const BazaCartaManoDTO = {
            baza: BazaActual,
            mano: mano,
            carta: cartaAJugar,
            turno: ListaDeTrucos.length + 1,
        };

        try {
            const response = await fetch(`/api/v1/trucos/${jugadorId}/jugar`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(BazaCartaManoDTO),
            });

            if (!response.ok) {
                console.log("Algo falla");
                throw new Error("Network response was not ok");
            }

            const data = await response.json();
            console.log("Dime que se creó el truco:", data);
            await fetchBazaActual(); // Actualiza la baza actual
            return data; // Devuelve el truco creado
        } catch (error) {
            console.error("Error al iniciar truco:", error);
            throw error;
        }
    }
}