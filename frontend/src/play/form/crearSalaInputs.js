import { formValidators } from "../../validators/formValidators";

export const loginFormInputs = [
  {
    tag: "nombre de la sala",
    name: "nombre de la sala",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
  {
    tag: "numero maximo de jugadores",
    name: "numero maximo de jugadores",
    type: "text",
    defaultValue: "",
    isRequired: true,
    validators: [formValidators.notEmptyValidator],
  },
];