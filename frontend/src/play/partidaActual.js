export default class Partida {
    static async fetchPartida(idPartida, jwt) {
        try {
            const response = await fetch(`/api/v1/partidas/${idPartida}`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Network response was not ok");
            }

            const data = await response.json();
            return data; // Devuelve los datos de la partida
        } catch (error) {
            console.error("Error fetching partida:", error);
            throw error; // Propaga el error para manejarlo en el componente que lo utiliza
        }
    }
}