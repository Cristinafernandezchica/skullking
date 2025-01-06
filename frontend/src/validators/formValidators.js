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
    }
}