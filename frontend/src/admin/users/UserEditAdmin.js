import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Form, Input, Label, Button } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import {sendLogoutRequest} from '../../auth/logout';
import jwt_decode from "jwt-decode"; // Decodificar el token JWT

const jwt = tokenService.getLocalAccessToken();

export default function UserEditAdmin() {
  const currentUser = jwt_decode(jwt).sub; // Extraer el usuario actual desde el token
  const emptyItem = {
    id: null,
    username: "",
    descripcionPerfil: "",
    imagenPerfil: "https://blog.tienda-medieval.com/wp-content/uploads/2019/02/Parche-pirata-ojo-derecho.jpg",
    authority: null,
  };

  const id = getIdFromUrl(2);
  const [user, setUser] = useState(emptyItem);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [auths, setAuths] = useState([]);

  useEffect(() => {
    if (id) {
      // Obtener datos del usuario por ID
      fetch(`/api/v1/users/${id}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error("Error al obtener datos del usuario");
          }
          return response.json();
        })
        .then((data) => setUser(data))
        .catch((error) => {
          setMessage(error.message);
          setVisible(true);
        });
    }

    // Obtener lista de autoridades
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
  }, [id]);

  function handleChange(event) {
    const { name, value } = event.target;

    if (name === "authority") {
      const selectedAuth = auths.find((auth) => auth.id === Number(value));
      setUser({ ...user, authority: selectedAuth });
    } else if (name !== "password") {
      // Ignorar cambios en la contraseña
      setUser({ ...user, [name]: value });
    }
  }

  function handleSubmit(event) {
    event.preventDefault();
  
    const userToUpdate = { ...user };
    delete userToUpdate.password; // Omitir contraseña
  
    fetch(`/api/v1/users/${user.id}`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userToUpdate),
    })
      .then((response) => {
        if (response.status === 204) return; // Manejar respuestas sin contenido
        if (!response.ok) {
          throw new Error("Error al guardar el usuario");
        }
        return response.json();
      })
      .then(() => {
        if (user.username === currentUser && user.authority?.authority !== jwt_decode(jwt).authority) {
          // Redirigir al usuario actual a "/" si cambió su autoridad
          sendLogoutRequest();
          window.location.reload();
          window.location.href = "/";

        } else {
          window.location.href = "/users";
        }
      })
      .catch((error) => {
        setMessage(error.message);
        setVisible(true);
      });
  }

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="auth-page-container">
      <h2>{"Editar usuario"}</h2>
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
              value={user.username || ""}
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
              value={user.descripcionPerfil || ""}
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
              value={user.imagenPerfil || ""}
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
