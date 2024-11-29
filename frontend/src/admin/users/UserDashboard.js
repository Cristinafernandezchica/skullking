import React, { useState, useEffect } from "react";
import { Table, Container, Row, Col, Card, CardBody } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";

const jwt = tokenService.getLocalAccessToken();

export default function UserStatisticsDashboard() {
  const [users, setUsers] = useState([]);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [alerts, setAlerts] = useState([]);

  // Fetch the users from the API
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await fetch("/api/v1/users", {
          headers: {
            "Authorization": `Bearer ${jwt}`,
          },
        });

        if (!response.ok) {
          throw new Error("Network response was not ok");
        }

        const data = await response.json();
        // Asegurarse de que los valores null se convierten a 0
        const usersWithDefaults = data.map((user) => ({
          ...user,
          numPartidasJugadas: user.numPartidasJugadas ?? 0,
          numPartidasGanadas: user.numPartidasGanadas ?? 0,
          numPuntosGanados: user.numPuntosGanados ?? 0,
        }));
        setUsers(usersWithDefaults);
      } catch (error) {
        console.error("Error encontrando usuarios:", error);
        setMessage(error.message);
        setVisible(true);
      }
    };

    fetchUsers();
  }, []);

  const totalPartidasJugadas = users.reduce((sum, user) => sum + user.numPartidasJugadas, 0);
  const totalPartidasGanadas = users.reduce((sum, user) => sum + user.numPartidasGanadas, 0);
  const totalPuntosGanados = users.reduce((sum, user) => sum + user.numPuntosGanados, 0);

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="admin-page-container">
      <Container fluid style={{ marginTop: "15px" }}>
        <h1 className="text-center">Estadísticas de usuarios</h1>
        {alerts.map((a) => a.alert)}
        {modal}
        <Row className="mb-4">
          <Col>
            <Card>
              <CardBody>
                <h5>Total de partidas jugadas</h5>
                <p className="stat-value">{totalPartidasJugadas}</p>
              </CardBody>
            </Card>
          </Col>
          <Col>
            <Card>
              <CardBody>
                <h5>Total de partidas ganadas</h5>
                <p className="stat-value">{totalPartidasGanadas}</p>
              </CardBody>
            </Card>
          </Col>
          <Col>
            <Card>
              <CardBody>
                <h5>Total de puntos ganados</h5>
                <p className="stat-value">{totalPuntosGanados}</p>
              </CardBody>
            </Card>
          </Col>
        </Row>
        <Table aria-label="users" className="mt-4">
          <thead>
            <tr>
              <th>Nombre de usuario</th>
              <th>Partidas Jugadas</th>
              <th>Partidas Ganadas</th>
              <th>Puntos Ganados</th>
            </tr>
          </thead>
          <tbody>
            {users.length > 0 ? (
              users.map((user) => (
                <tr key={user.id}>
                  <td>{user.username}</td>
                  <td>{user.numPartidasJugadas}</td>
                  <td>{user.numPartidasGanadas}</td>
                  <td>{user.numPuntosGanados}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="4">Cargando usuarios...</td>
              </tr>
            )}
          </tbody>
        </Table>
      </Container>
    </div>
  );
}