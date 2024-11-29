export default class JugadoresPartida {
    static async fetchJugadores(idPartida, jwt) {
        try {
            const response = await fetch(`/api/v1/jugadores/${idPartida}`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error("Network response was not ok");
            }

            const data = await response.json();
            return data; // Devuelve los datos de los jugadores
        } catch (error) {
            console.error("Error encontrando jugadores:", error);
            throw error; // Propaga el error para manejo en el componente que la utiliza
        }
    }
}