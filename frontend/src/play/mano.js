export default class Mano {
    static async fetchManoDeJugador(jugadorId, jwt) {
        try {
            const response = await fetch(`/api/v1/manos/${jugadorId}`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error("Network response was not ok");
            }

            const data = await response.json();
            return data; // Devuelve los datos de la mano del jugador
        } catch (error) {
            console.error(`Error encontrando la mano del jugador ${jugadorId}:`, error);
            throw error; // Propaga el error
        }
    }

    static async fetchManosDeOtrosJugadores(jugadores, tuId, jwt) {
        try {
            const manos = {};
            for (const jugador of jugadores) {
                if (jugador.id !== tuId) {
                    const data = await this.fetchManoDeJugador(jugador.id, jwt); // Reutiliza el método fetchManoDeJugador
                    manos[jugador.id] = data;
                }
            }
            return manos; // Devuelve un objeto con las manos de otros jugadores
        } catch (error) {
            console.error("Error encontrando las manos de otros jugadores:", error);
            throw error; // Propaga el error
        }
    }
}