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

    static async siguienteEstado(idPartida, bazaId, jwt) {
        try {
            const response = await fetch(`/api/v1/partidas/${idPartida}/bazas/${bazaId}/siguiente-estado`, {
                method: 'POST',
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(),
            });

            if (!response.ok) {
                console.log("Fallo al crear la nueva baza");
                throw new Error("Network response was not ok");
            }

            const data = await response.json();
            console.log("Dime que se creó la nueva baza:", data);

            return data; // Devuelve los datos de la nueva baza
        } catch (error) {
            console.error("Error avanzando al siguiente estado:", error);
            throw error; // Propaga el error para manejo externo
        }
    }
}