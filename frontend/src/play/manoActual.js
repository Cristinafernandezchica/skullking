export default class Mano {
    static async fetchMano(jugadorId, jwt) {
        try {
            const response = await fetch(`/api/v1/manos/${jugadorId}`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Network response was not ok");
            }

            const data = await response.json();

            console.log("Nueva mano", data);
            return data; // Devuelve los datos de la mano
        } catch (error) {
            console.error("Error encontrando mano:", error);
            throw error; // Propaga el error para manejarlo en el componente que lo utiliza
        }
    }

    static async fetchManosOtrosJugadores(jugadores, tuId, jwt) {
        try {
            const nuevasManos = {};

            for (const jugador of jugadores) {
                if (jugador.id !== tuId) {
                    const response = await fetch(`/api/v1/manos/${jugador.id}`, {
                        headers: {
                            "Authorization": `Bearer ${jwt}`,
                            "Content-Type": "application/json",
                        },
                    });

                    if (!response.ok) {
                        throw new Error("Network response was not ok");
                    }

                    const data = await response.json();
                    nuevasManos[jugador.id] = data; // Asocia la mano obtenida con el jugador correspondiente
                }
            }

            return nuevasManos; // Devuelve el objeto con las manos de los otros jugadores
        } catch (error) {
            console.error("Error encontrando manos de otros jugadores:", error);
            throw error; // Propaga el error para manejarlo en el componente que lo utiliza
        }
    }
}