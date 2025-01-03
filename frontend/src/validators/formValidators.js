export const formValidators = {
    notEmptyValidator: {
        validate: (value) => {
            return value.trim().length > 0;
        },
        message: "El campo no puede estar vacio"
    },
    notNoneTypeValidator: {
        validate: (value) => {
            return value !== "None";
        },
        message: "Por favor, selecciona un tipo"
    },
    descriptionValidator: {
        validate: (value) => {
            return value.toString().trim().length > 0 && value.toString().trim().length <= 100;
        },
        message: "La descripción del perfil no puede ser mayor a 100 caracteres."
    },
    pictureValidator: {
        validate: (value) => {
            const maxBytes = 51200;  // 50 KB (50 × 1024 bytes)
            const length = value.toString().trim().length;
            if(length === 0) { 
                alert("Por favor, seleccione una imagen.");
            } else if (length > maxBytes) {
                alert("La imagen es demasiado grande, máximo 50KB");
            }
            return length > 0 && length <= maxBytes;
        },
        message: "Por favor, seleccione una imagen."
    },
}