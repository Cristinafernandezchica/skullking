export default class RondaActual {
    static async fetchRondaActual(partidaId, jwt) {
        try {
            const response = await fetch(`/api/v1/rondas/${partidaId}/partida`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error("Network response was not ok");
            }

            const data = await response.json();
            return data; // Devuelve los datos de la ronda
        } catch (error) {
            console.error("Error fetching ronda actual:", error);
            throw error; // Propaga el error para manejo en el componente que la utiliza
        }
    }
}