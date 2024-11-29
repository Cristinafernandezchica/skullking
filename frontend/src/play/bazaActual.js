export default class BazaActual {
    static async fetchBazaActual(rondaId, jwt) {
        try {
            const response = await fetch(`/api/v1/bazas/${rondaId}/bazaActual`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Network response was not ok");
            }

            const data = await response.json();
            return data; // Devuelve los datos de la baza actual
        } catch (error) {
            console.error("Error fetching baza actual:", error);
            throw error; // Propaga el error para manejarlo en el componente que lo utiliza
        }
    }
}