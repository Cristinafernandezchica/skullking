import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Form, Input, Label, Button } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";

const jwt = tokenService.getLocalAccessToken();

export default function UserCreateAdmin() {
  const emptyItem = {
    username: "",
    password: "",
    descripcionPerfil: "",
    imagenPerfil: "https://blog.tienda-medieval.com/wp-content/uploads/2019/02/Parche-pirata-ojo-derecho.jpg",
    authority: null,
  };
  const [user, setUser] = useState(emptyItem);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [auths, setAuths] = useState([]);

  useEffect(() => {
    // Fetch available authorities
    fetch(`/api/v1/users/authorities`, {
      headers: {
        Authorization: `Bearer ${jwt}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Error al obtener autoridades");
        }
        return response.json();
      })
      .then((data) => setAuths(data))
      .catch((error) => {
        setMessage(error.message);
        setVisible(true);
      });
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;

    if (name === "authority") {
      const selectedAuth = auths.find((auth) => auth.id === Number(value));
      setUser({ ...user, authority: selectedAuth });
    } else {
      setUser({ ...user, [name]: value });
    }
  }

  function handleSubmit(event) {
    event.preventDefault();
  
    // Validar campos requeridos
    if (!user.username || !user.password || !user.authority) {
      setMessage("Todos los campos obligatorios deben estar completos.");
      setVisible(true);
      return;
    }
  
    // Preparar el objeto para enviar al backend
    const userToCreate = {
      ...user,
      authority: user.authority?.authority, // Enviar solo el nombre de la autoridad
    };
  
    fetch(`/api/v1/auth/signup`, {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
        Authorization: `Bearer ${jwt}`, // Para asegurarse de que solo los administradores puedan crear usuarios
      },
      body: JSON.stringify(userToCreate),
    })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((error) => {
            throw new Error(error.message || "Error al crear el usuario");
          });
        }
        return response.json();
      })
      .then((data) => {
        alert("Usuario creado exitosamente!");
        window.location.href = "/users";
      })
      .catch((error) => {
        setMessage(error.message);
        setVisible(true);
      });
  }

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="auth-page-container">
      <h2>Crear nuevo usuario</h2>
      {modal}
      <div className="auth-form-container">
        <Form onSubmit={handleSubmit}>
          <div className="custom-form-input">
            <Label for="username" className="custom-form-input-label">
              Nombre de usuario
            </Label>
            <Input
              type="text"
              required
              name="username"
              id="username"
              value={user.username}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="password" className="custom-form-input-label">
              Contraseña
            </Label>
            <Input
              type="password"
              required
              name="password"
              id="password"
              value={user.password}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="descripcionPerfil" className="custom-form-input-label">
              Descripción del perfil
            </Label>
            <Input
              type="textarea"
              name="descripcionPerfil"
              id="descripcionPerfil"
              value={user.descripcionPerfil}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="imagenPerfil" className="custom-form-input-label">
              Imagen del perfil (URL)
            </Label>
            <Input
              type="url"
              name="imagenPerfil"
              id="imagenPerfil"
              value={user.imagenPerfil}
              onChange={handleChange}
              className="custom-input"
            />
          </div>
          <div className="custom-form-input">
            <Label for="authority" className="custom-form-input-label">
              Autoridad
            </Label>
            <Input
              type="select"
              required
              name="authority"
              id="authority"
              value={user.authority?.id || ""}
              onChange={handleChange}
              className="custom-input"
            >
              <option value="">Seleccionar autoridad</option>
              {auths.map((auth) => (
                <option key={auth.id} value={auth.id}>
                  {auth.authority}
                </option>
              ))}
            </Input>
          </div>
          <div className="custom-button-row">
            <Button type="submit" color="primary" className="auth-button">
              Guardar
            </Button>
            <Link
              to="/users"
              className="auth-button"
              style={{ textDecoration: "none" }}
            >
              Cancelar
            </Link>
          </div>
        </Form>
      </div>
    </div>
  );
}
