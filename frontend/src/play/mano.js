export default class Mano {
    static async fetchMano(jugadorId, jwt) {
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
            return data; // Devuelve los datos de la mano
        } catch (error) {
            console.error("Error fetching mano:", error);
            throw error; // Propaga el error para manejo en el componente que la utiliza
        }
    }
}