export default class Baza {
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
            console.log("bazaActual fetchBazaActual:", data);
            return data; // Devuelve los datos de la baza actual
        } catch (error) {
            console.error("Error encontrando baza actual:", error);
            throw error; // Propaga el error para manejo externo
        }
    }
}