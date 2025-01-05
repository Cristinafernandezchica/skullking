import React, { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function UserListAdmin() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [users, setUsers] = useFetchState(
    [],
    `/api/v1/users`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);
  const [paginaActual, setPaginaActual] = useState(1);
  const usuariosPorPagina = 6; // Número fijo de usuarios por página

  // Calcular el índice de la última y la primera fila de usuarios en la página actual
  const indiceUltimoUsuario = paginaActual * usuariosPorPagina;
  const indicePrimerUsuario = indiceUltimoUsuario - usuariosPorPagina;

  // Filtrar usuarios para la página actual
  const usuariosActuales = users.slice(indicePrimerUsuario, indiceUltimoUsuario);

  // Calcular el número total de páginas
  const totalPaginas = Math.ceil(users.length / usuariosPorPagina);

  // Función para eliminar un usuario
  async function eliminarUsuario(user) {
    try {
      // Verificar jugadores asociados al usuario
      const jugadoresResponse = await fetch(`/api/v1/jugadores/${user.id}/usuarios`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });
  
      if (!jugadoresResponse.ok) {
        // Si el error es porque no hay jugadores asociados, proceder con la eliminación del usuario
        if (jugadoresResponse.status === 404) {
          console.warn(`El usuario ${user.username} no tiene jugadores asociados.`);
          await eliminarUsuarioDirectamente(user);
          return;
        }
        throw new Error("Error al verificar jugadores asociados");
      }
  
      const jugadores = await jugadoresResponse.json();
  
      // Si no hay jugadores asociados, eliminar directamente al usuario
      if (jugadores.length === 0) {
        console.log(`El usuario ${user.username} no tiene jugadores asociados. Eliminando...`);
        await eliminarUsuarioDirectamente(user);
        return;
      }
  
      // Verificar jugadores en estado "JUGANDO"
      const jugadoresEnJuego = jugadores.some(
        (jugador) => jugador.partida.estado === "JUGANDO"
      );
  
      if (jugadoresEnJuego) {
        setMessage(
          `No se puede eliminar al usuario ${user.username} porque tiene jugadores en estado JUGANDO.`
        );
        setVisible(true);
        return;
      }
  
      // Eliminar jugadores asociados en estado "ESPERANDO" u otros
      for (const jugador of jugadores) {
        await fetch(`/api/v1/jugadores/${jugador.id}`, {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        });
      }
  
      // Finalmente, eliminar el usuario
      await eliminarUsuarioDirectamente(user);
    } catch (error) {
      setMessage(error.message);
      setVisible(true);
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
  
      if (!response.ok) {
        throw new Error(`Error al eliminar usuario ${user.username}`);
      }
  
      // Actualizar la lista de usuarios tras la eliminación
      setUsers(users.filter((u) => u.id !== user.id));
      console.log(`Usuario ${user.username} eliminado con éxito.`);
    } catch (error) {
      setMessage(error.message);
      setVisible(true);
    }
  }
  

  // Renderizar usuarios de la página actual
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
  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="admin-page-container">
      <h1 className="text-center">Usuarios</h1>
      {alerts.map((a) => a.alert)}
      {modal}
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
