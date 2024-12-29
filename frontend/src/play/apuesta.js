export default class Apuesta {
    static async realizarApuesta(jugadorId, apuesta, jwt) {
        try {
            const response = await fetch(`/api/v1/partidas/apuesta/${jugadorId}?apuesta=${apuesta}`, {
                method: 'PUT',
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${jwt}`,
                },
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Error desconocido');
            }

            console.log("Apuesta realizada con éxito");
        } catch (error) {
            console.error("Error realizando apuesta:", error);
            throw error; // Propaga el error para manejo en niveles superiores
        }
    }
}