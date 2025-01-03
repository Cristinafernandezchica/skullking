import { formValidators } from "../../../validators/formValidators";

export const registerFormUserInput = [
  {
    tag: "Nombre de usuario",
    name: "username",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "Contraseña",
    name: "password",
    type: "password",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "Nombre",
    name: "firstName",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "Apellido",
    name: "lastName",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "Descripción",
    name: "descripcionPerfil",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.descriptionValidator],
  },
  {
    tag: "Foto de perfil",
    name: "imagenPerfil",
    type: "text",
    defaultValue: "https://blog.tienda-medieval.com/wp-content/uploads/2019/02/Parche-pirata-ojo-derecho.jpg",
    isRequired: true,
    validators: [formValidators.pictureValidator],
  },
];
