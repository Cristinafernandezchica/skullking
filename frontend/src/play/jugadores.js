export default class Jugadores {
    static async fetchJugadores(idPartida, jwt) {
        try {
            const response = await fetch(`/api/v1/jugadores/${idPartida}`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Network response was not ok");
            }

            const data = await response.json();
            console.log("Jugadores obtenidos:", data);
            return data; // Devuelve la lista de jugadores
        } catch (error) {
            console.error("Error encontrando jugadores:", error);
            throw error; // Propaga el error para manejarlo en el componente que lo utiliza
        }
    }
}