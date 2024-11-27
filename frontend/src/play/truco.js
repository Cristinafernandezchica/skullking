export default class Truco {
    static async fetchTrucoMio(bazaId, jwt, userId) {
        try {
            const response = await fetch(`/api/v1/bazas/${bazaId}/trucos`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error("Network response was not ok");
            }

            const data = await response.json();

            // Filtra el truco correspondiente al usuario actual
            const trucoMio = data.find((objeto) => objeto.jugador.usuario.id === userId);
            return trucoMio || null; // Devuelve el truco del usuario o null si no lo encuentra
        } catch (error) {
            console.error("Error fetching truco:", error);
            throw error; // Propaga el error para manejo en el componente que lo utiliza
        }
    }
}