export default class Trucos {
    static async fetchListaDeTrucos(bazaId, jwt) {
        try {
            const response = await fetch(`/api/v1/bazas/${bazaId}/trucos`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Network response was not ok");
            }

            const data = await response.json();
            console.log("Este es la lista de trucos:", data);
            return data; // Devuelve la lista de trucos
        } catch (error) {
            console.error("Error encontrando trucos:", error);
            throw error; // Propaga el error para manejarlo en el componente que lo utiliza
        }
    }
}