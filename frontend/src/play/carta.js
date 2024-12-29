export default class Carta {
    static async fetchCartasDisabled(idMano, paloActual, jwt) {
        try {
            const response = await fetch(`/api/v1/manos/${idMano}/manoDisabled?tipoCarta=${paloActual}`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Network response was not ok");
            }

            const responseData = await response.text();
            console.log("Response text:", responseData);

            const data = JSON.parse(responseData); // Convierte la respuesta de texto a JSON
            console.log("Cartas disabled:", data);
            return data; // Devuelve las cartas deshabilitadas
        } catch (error) {
            console.error("Error encontrando cartas deshabilitadas:", error);
            throw error; // Propaga el error para manejarlo en el componente que lo utiliza
        }
    }
}