import React, { useState, useEffect } from "react";
import { Table, Container, Row, Col, Button, Card, CardBody } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";

const jwt = tokenService.getLocalAccessToken();

export default function UserStatisticsDashboard() {
  const [users, setUsers] = useState([]);
  const [view, setView] = useState("default"); // default (by points), winPercentage
  const [topUser, setTopUser] = useState(null); // Stores top user info for the current view
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);

  // Fetch users based on the selected view
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const endpoint =
          view === "winPercentage"
            ? "/api/v1/users/sorted-by-win-percentage"
            : "/api/v1/users/sorted-by-points";
        const response = await fetch(endpoint, {
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        });

        if (!response.ok) {
          throw new Error("Network response was not ok");
        }

        const data = await response.json();
        const usersWithDefaults = data.map((user) => ({
          ...user,
          numPartidasJugadas: user.numPartidasJugadas ?? 0,
          numPartidasGanadas: user.numPartidasGanadas ?? 0,
          numPuntosGanados: user.numPuntosGanados ?? 0,
          winPercentage:
            user.numPartidasJugadas > 0
              ? ((user.numPartidasGanadas / user.numPartidasJugadas) * 100).toFixed(2)
              : "0.00",
        }));
        setUsers(usersWithDefaults);

        // Determine the top user for the current view
        if (view === "default") {
          const top = usersWithDefaults[0]; // First user has the most points
          setTopUser({
            username: top.username,
            value: top.numPuntosGanados,
            label: "Puntos Ganados",
          });
        } else if (view === "winPercentage") {
          const top = usersWithDefaults[0]; // First user has the highest win percentage
          setTopUser({
            username: top.username,
            value: top.winPercentage,
            label: "Porcentaje de Victorias (%)",
          });
        }
      } catch (error) {
        console.error("Error encontrando usuarios:", error);
        setMessage(error.message);
        setVisible(true);
      }
    };

    fetchUsers();
  }, [view]);

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="admin-page-container">
      <Container fluid style={{ marginTop: "15px" }}>
        <h1 className="text-center">Estadísticas de usuarios</h1>
        {modal}

        {/* Display top user */}
        {topUser && (
          <Row className="mb-4">
            <Col>
              <Card>
                <CardBody>
                  <h5>
                    {view === "default"
                      ? "Usuario con más puntos"
                      : "Usuario con mayor porcentaje de victorias"}
                  </h5>
                  <p className="stat-value">
                    {topUser.username} - {topUser.value} {topUser.label}
                  </p>
                </CardBody>
              </Card>
            </Col>
          </Row>
        )}

        {/* Buttons to toggle views */}
        <Row className="mb-3">
          <Col>
            <Button
              color={view === "default" ? "primary" : "secondary"}
              onClick={() => setView("default")}
              className="me-2"
            >
              Ordenar por puntos
            </Button>
            <Button
              color={view === "winPercentage" ? "primary" : "secondary"}
              onClick={() => setView("winPercentage")}
            >
              Ordenar por porcentaje de victorias
            </Button>
          </Col>
        </Row>

        {/* User table */}
        <Table aria-label="users" className="mt-4">
          <thead>
            <tr>
              <th>Nombre de usuario</th>
              {view === "default" && <th>Puntos Ganados</th>}
              {view === "winPercentage" && <th>Porcentaje de Victorias (%)</th>}
            </tr>
          </thead>
          <tbody>
            {users.length > 0 ? (
              users.map((user) => (
                <tr key={user.id}>
                  <td>{user.username}</td>
                  {view === "default" && <td>{user.numPuntosGanados}</td>}
                  {view === "winPercentage" && <td>{user.winPercentage}</td>}
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="2">Cargando usuarios...</td>
              </tr>
            )}
          </tbody>
        </Table>
      </Container>
    </div>
  );
}