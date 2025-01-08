import React, { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table, Alert } from "reactstrap"; // Importar Alert para las alertas
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function UserListAdmin() {
  const [alert, setAlert] = useState({ message: null, type: null }); // Alerta con tipo (success/error)
  const [users, setUsers] = useFetchState(
    [],
    `/api/v1/users`,
    jwt,
    (msg) => setAlert({ message: msg, type: "error" }),
  );
  const [paginaActual, setPaginaActual] = useState(1);
  const usuariosPorPagina = 6;

  // Ordenar usuarios por autoridad y nombre
  const sortedUsers = [...users].sort((a, b) => {
    const authComparison = a.authority.authority.localeCompare(b.authority.authority);
    if (authComparison !== 0) return authComparison;
    return a.username.localeCompare(b.username);
  });

  
  const indiceUltimoUsuario = paginaActual * usuariosPorPagina;
  const indicePrimerUsuario = indiceUltimoUsuario - usuariosPorPagina;

  // Filtrar usuarios para la página actual
  const usuariosActuales = sortedUsers.slice(indicePrimerUsuario, indiceUltimoUsuario);

  const totalPaginas = Math.ceil(users.length / usuariosPorPagina);

  // Función para eliminar un usuario
  async function eliminarUsuario(user) {
    try {
      const jugadoresResponse = await fetch(`/api/v1/jugadores/${user.id}/usuarios`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });

      if (!jugadoresResponse.ok) {
        if (jugadoresResponse.status === 404) {
          console.warn(`Usuario ${user.username} no tiene jugadores.`);
          await eliminarUsuarioDirectamente(user);
          return;
        }
        throw new Error("Error al verificar jugadores asociados.");
      }

      const jugadores = await jugadoresResponse.json();

      if (jugadores.length === 0) {
        console.log(`Usuario ${user.username} no tiene jugadores. Eliminando...`);
        await eliminarUsuarioDirectamente(user);
        return;
      }

      for (const jugador of jugadores) {
        const partidaEstado = jugador.partida.estado;

        if (partidaEstado === "JUGANDO" || partidaEstado === "ESPERANDO") {
          setAlert({
            message: `No se puede eliminar al usuario ${user.username} porque está en una partida "${partidaEstado}".`,
            type: "error",
          });
          return;
        }

        await fetch(`/api/v1/jugadores/${jugador.id}`, {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        });
      }

      await eliminarUsuarioDirectamente(user);
    } catch (error) {
      setAlert({ message: error.message, type: "error" });
    }
  }

  async function eliminarUsuarioDirectamente(user) {
    try {
      const response = await fetch(`/api/v1/users/${user.id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });

      if (!response.ok){
        throw new Error("Error al eliminar el usuario.");
      }
      // Filtrar usuarios eliminados
      const usuariosRestantes = users.filter((u) => u.id !== user.id);
      setUsers(usuariosRestantes);

      // Verificar si la página actual queda vacía
      const usuariosEnPaginaActual = Math.ceil(usuariosRestantes.length / usuariosPorPagina);
      if (paginaActual > usuariosEnPaginaActual) {
        setPaginaActual((prev) => Math.max(prev - 1, 1)); // Retrocede una página si es necesario
      }
      setAlert({ message: `Usuario ${user.username} eliminado con éxito.`, type: "success" });
    } catch (error) {
      setAlert({ message: error.message, type: "error" });
    }
  }

  const userList = usuariosActuales.map((user) => {
    return (
      <tr key={user.id}>
        <td>{user.username}</td>
        <td>{user.authority.authority}</td>
        <td>
          <ButtonGroup>
            <Button
              size="sm"
              color="primary"
              aria-label={"edit-" + user.id}
              tag={Link}
              to={"/users/" + user.id}
            >
              Editar
            </Button>
            <Button
              size="sm"
              color="danger"
              aria-label={"delete-" + user.id}
              onClick={() => eliminarUsuario(user)}
            >
              Eliminar
            </Button>
          </ButtonGroup>
        </td>
      </tr>
    );
  });

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Usuarios</h1>

      {alert.message && (
        <Alert color={alert.type === "success" ? "success" : "danger"}>
          {alert.message}
        </Alert>
      )}

      <Button color="success" tag={Link} to="/users/new">
        Añadir usuario
      </Button>
      <div>
        <Table aria-label="users" className="mt-4">
          <thead>
            <tr>
              <th>Nombre de usuario</th>
              <th>Autoridad</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>{userList}</tbody>
        </Table>
      </div>
      <div className="d-flex justify-content-between align-items-center mt-3">
        <Button
          disabled={paginaActual === 1}
          onClick={() => setPaginaActual(paginaActual - 1)}
        >
          Página anterior
        </Button>
        <span>
          Página {paginaActual} de {totalPaginas}
        </span>
        <Button
          disabled={paginaActual === totalPaginas}
          onClick={() => setPaginaActual(paginaActual + 1)}
        >
          Página siguiente
        </Button>
      </div>
    </div>
  );
}
