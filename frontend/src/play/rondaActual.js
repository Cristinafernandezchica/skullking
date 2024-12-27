export default class Ronda {
    static async fetchRondaActual(partidaId, jwt) {
        try {
            const response = await fetch(`/api/v1/rondas/${partidaId}/partida`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Network response was not ok");
            }

            const data = await response.json();
            console.log("Ronda actual obtenida:", data);
            return data; // Devuelve los datos de la ronda actual
        } catch (error) {
            console.error("Error encontrando la ronda actual:", error);
            throw error; // Propaga el error para manejo en niveles superiores
        }
    }
}