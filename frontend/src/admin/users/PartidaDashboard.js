import React, { useState, useEffect } from "react";
import { Table, Container, Row, Col, Button, Card, CardBody } from "reactstrap";
import tokenService from "../../services/token.service"; // Asumimos que tienes un servicio para obtener el token
import "../../static/css/admin/adminPage.css"; // Tu CSS correspondiente
import getErrorModal from "../../util/getErrorModal"; // Para mostrar el modal de errores

const jwt = tokenService.getLocalAccessToken(); // Obtener token JWT desde un servicio

export default function PartidasStatisticsDashboard() {
  const [globalStats, setGlobalStats] = useState(null); // Almacena las estadísticas globales
  const [users, setUsers] = useState([]); // Almacena los usuarios
  const [view, setView] = useState("global"); // Determina la vista actual: global o por usuario
  const [message, setMessage] = useState(null); // Mensaje de error
  const [visible, setVisible] = useState(false); // Controla la visibilidad del modal de error

  // Fetch global statistics
  const fetchGlobalStats = async () => {
    try {
      const responsePromedio = await fetch("/api/v1/users/promedio-partidas", {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });
      const responseMax = await fetch("/api/v1/users/max-partidas", {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });
      const responseMin = await fetch("/api/v1/users/min-partidas", {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });

      if (!responsePromedio.ok || !responseMax.ok || !responseMin.ok) {
        throw new Error("Network response was not ok");
      }

      const promedio = await responsePromedio.json();
      const max = await responseMax.json();
      const min = await responseMin.json();

      setGlobalStats({ promedio, max, min });
    } catch (error) {
      console.error("Error obteniendo estadísticas globales:", error);
      setMessage(error.message);
      setVisible(true);
    }
  };

  // Fetch users statistics
  const fetchUsersStats = async () => {
    try {
      const response = await fetch("/api/v1/users", {
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });

      if (!response.ok) {
        throw new Error("Network response was not ok");
      }

      const data = await response.json();
      setUsers(data);
    } catch (error) {
      console.error("Error obteniendo usuarios:", error);
      setMessage(error.message);
      setVisible(true);
    }
  };

  // Fetch data when the component mounts or view changes
  useEffect(() => {
    if (view === "global") {
      fetchGlobalStats();
    } else {
      fetchUsersStats();
    }
  }, [view]);

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="admin-page-container">
      <Container fluid style={{ marginTop: "15px" }}>
        <h1 className="text-center">Estadísticas de Partidas</h1>
        {modal}

        {/* Buttons to toggle views */}
        <Row className="mb-3">
          <Col>
            <Button
              color={view === "global" ? "primary" : "secondary"}
              onClick={() => setView("global")}
              className="me-2"
            >
              Estadísticas globales
            </Button>
            <Button
              color={view === "user" ? "primary" : "secondary"}
              onClick={() => setView("user")}
            >
              Estadísticas por usuario
            </Button>
          </Col>
        </Row>

        {/* Display global stats */}
        {view === "global" && globalStats && (
          <Table aria-label="global-stats" className="mt-4">
            <thead>
              <tr>
                <th>Promedio de Partidas</th>
                <th>Máximo de Partidas</th>
                <th>Mínimo de Partidas</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>{globalStats.promedio}</td>
                <td>{globalStats.max}</td>
                <td>{globalStats.min}</td>
              </tr>
            </tbody>
          </Table>
        )}

        {/* Display users stats */}
        {view === "user" && (
          <Table aria-label="users-stats" className="mt-4">
            <thead>
              <tr>
                <th>Nombre de Usuario</th>
                <th>Número de Partidas Jugadas</th>
              </tr>
            </thead>
            <tbody>
              {users.length > 0 ? (
                users.map((user) => (
                  <tr key={user.id}>
                    <td>{user.username}</td>
                    <td>{user.numPartidasJugadas}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="2">Cargando usuarios...</td>
                </tr>
              )}
            </tbody>
          </Table>
        )}
      </Container>
    </div>
  );
}
