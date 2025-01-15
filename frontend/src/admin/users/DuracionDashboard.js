import React, { useState, useEffect } from "react";
import { Table, Container, Row, Col, Button, Card, CardBody } from "reactstrap";
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";

const jwt = tokenService.getLocalAccessToken();

export default function DuracionStatisticsDashboard() {
  const [globalStats, setGlobalStats] = useState(null);
  const [userStats, setUserStats] = useState([]);
  const [users, setUsers] = useState([]);
  const [view, setView] = useState("global"); // 'global' or 'byUser'
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);

  // Fetch global stats (promedio, max, min, total) on load
  useEffect(() => {
    const fetchGlobalStats = async () => {
      try {
        const responses = await Promise.all([
          fetch("/api/v1/users/promedio-tiempo-partidas", {
            headers: { Authorization: `Bearer ${jwt}` },
          }),
          fetch("/api/v1/users/max-tiempo-partidas", {
            headers: { Authorization: `Bearer ${jwt}` },
          }),
          fetch("/api/v1/users/min-tiempo-partidas", {
            headers: { Authorization: `Bearer ${jwt}` },
          }),
          fetch("/api/v1/users/total-tiempo-partidas", {
            headers: { Authorization: `Bearer ${jwt}` },
          }),
        ]);

        const [avg, max, min, total] = await Promise.all(responses.map((res) => res.json()));

        setGlobalStats({
          promedio: avg,
          maximo: max,
          minimo: min,
          total: total,
        });
      } catch (error) {
        console.error("Error al obtener estadísticas globales:", error);
        setMessage(error.message);
        setVisible(true);
      }
    };

    // Fetch users when viewing statistics by user
    const fetchUsers = async () => {
      try {
        const response = await fetch("/api/v1/users", {
          headers: { Authorization: `Bearer ${jwt}` },
        });
        const data = await response.json();
        setUsers(data);
      } catch (error) {
        console.error("Error al obtener usuarios:", error);
        setMessage(error.message);
        setVisible(true);
      }
    };

    if (view === "global") {
      fetchGlobalStats();
    } else if (view === "byUser") {
      fetchUsers();
    }
  }, [view]);

  // Fetch user-specific stats
  useEffect(() => {
    if (view === "byUser" && users.length > 0) {
      const fetchUserStats = async () => {
        try {
          const userStatsPromises = users.map(async (user) => {
            const userStatsResponse = await Promise.all([
              fetch(`/api/v1/users/${user.id}/promedio-tiempo-partidas`, {
                headers: { Authorization: `Bearer ${jwt}` },
              }),
              fetch(`/api/v1/users/${user.id}/max-tiempo-partidas`, {
                headers: { Authorization: `Bearer ${jwt}` },
              }),
              fetch(`/api/v1/users/${user.id}/min-tiempo-partidas`, {
                headers: { Authorization: `Bearer ${jwt}` },
              }),
              fetch(`/api/v1/users/${user.id}/total-tiempo-partidas`, {
                headers: { Authorization: `Bearer ${jwt}` },
              }),
            ]);

            const [avg, max, min, total] = await Promise.all(userStatsResponse.map((res) => res.json()));

            return {
              ...user,
              promedio: avg,
              maximo: max,
              minimo: min,
              total: total,
            };
          });

          const stats = await Promise.all(userStatsPromises);
          setUserStats(stats);
        } catch (error) {
          console.error("Error al obtener estadísticas de usuarios:", error);
          setMessage(error.message);
          setVisible(true);
        }
      };

      fetchUserStats();
    }
  }, [users, view]);

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
              Estadísticas Globales
            </Button>
            <Button
              color={view === "byUser" ? "primary" : "secondary"}
              onClick={() => setView("byUser")}
            >
              Estadísticas por Usuario
            </Button>
          </Col>
        </Row>

        {/* Display global stats */}
        {view === "global" && globalStats && (
          <Row className="mb-4">
            <Col>
              <Card>
                <CardBody>
                  <h5>Estadísticas Globales</h5>
                  <Table striped>
                    <thead>
                      <tr>
                        <th>Promedio (segundos)</th>
                        <th>Máximo (segundos)</th>
                        <th>Mínimo (segundos)</th>
                        <th>Total (segundos)</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td>{globalStats.promedio}</td>
                        <td>{globalStats.maximo}</td>
                        <td>{globalStats.minimo}</td>
                        <td>{globalStats.total}</td>
                      </tr>
                    </tbody>
                  </Table>
                </CardBody>
              </Card>
            </Col>
          </Row>
        )}

        {/* Display user-specific stats */}
        {view === "byUser" && userStats.length > 0 && (
          <Row className="mb-4">
            <Col>
              <Card>
                <CardBody>
                  <h5>Estadísticas por Usuario</h5>
                  <Table striped>
                    <thead>
                      <tr>
                        <th>Nombre de Usuario</th>
                        <th>Promedio (segundos)</th>
                        <th>Máximo (segundos)</th>
                        <th>Mínimo (segundos)</th>
                        <th>Total (segundos)</th>
                      </tr>
                    </thead>
                    <tbody>
                      {userStats.map((userStat) => (
                        <tr key={userStat.id}>
                          <td>{userStat.username}</td>
                          <td>{userStat.promedio}</td>
                          <td>{userStat.maximo}</td>
                          <td>{userStat.minimo}</td>
                          <td>{userStat.total}</td>
                        </tr>
                      ))}
                    </tbody>
                  </Table>
                </CardBody>
              </Card>
            </Col>
          </Row>
        )}
      </Container>
    </div>
  );
}
