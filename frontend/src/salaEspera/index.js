import React, { useEffect, useState } from "react";
import "../App.css";
import "../static/css/salaEspera/salaEspera.css";
import { Button, Table, Alert } from "reactstrap";
import tokenService from "../services/token.service.js";
import { useNavigate } from "react-router-dom";
import getIdFromUrl from "../util/getIdFromUrl.js";
import useFetchState from "../util/useFetchState.js";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

export default function SalaEspera() {
  const navigate = useNavigate();
  const id = getIdFromUrl(2);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [jugadores, setJugadores] = useState([]);
  const [partida, setPartida] = useFetchState(
    null,
    `/api/v1/partidas/${id}`,
    jwt,
    setMessage,
    setVisible
  );
  const [errors, setErrors] = useState([]);

  const showError = (error) => {
    setErrors([error]);
    setTimeout(() => {
      setErrors([]);
    }, 5000);
  };

  useEffect(() => {
    // Primer useEffect: Cargar la lista de jugadores al inicio
    const fetchJugadores = async () => {
      try {
        const response = await fetch(`/api/v1/partidas/${id}/jugadores`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${jwt}`,
          },
        });

        if (response.ok) {
          const jugadoresData = await response.json();
          setJugadores(jugadoresData); // Actualizamos el estado con la lista de jugadores
          console.log("carga inicial jugadores");
        } else {
          showError("Error al obtener los jugadores de la partida.");
        }
      } catch (error) {
        console.error("Error al obtener jugadores:", error);
      }
    };

    // Llamamos a la función de obtener jugadores
    fetchJugadores();
  }, [id]); // Solo se ejecuta cuando cambia el ID de la partida

  useEffect(() => {
    // Segundo useEffect: Conexión al WebSocket
    const socket = new SockJS("http://localhost:8080/ws");
    const stompClient = Stomp.over(() => socket);

    stompClient.connect({}, (frame) => {
      console.log("Connected: " + frame);

      stompClient.subscribe(`/topic/partida/${id}`, (messageOutput) => {
        const data = JSON.parse(messageOutput.body);
        setJugadores(data); // Actualizamos la lista de jugadores con los datos recibidos

        // Si la partida ha sido iniciada, redirigir a todos los jugadores
        if (data.status === "JUGANDO") {
          console.log("La partida ha comenzado, redirigiendo...");
          navigate(`/tablero/${id}`);
        } else if (data.status === "ACTUALIZADO") {
          console.log("Actualización recibida:", data);
          setJugadores(data.jugadores);
  
          // Actualizar el ownerPartida si cambió
          if (data.ownerPartida) {
            setPartida((prevPartida) => ({
              ...prevPartida,
              ownerPartida: data.ownerPartida,
            }));
          }
        }
      });
    });

    // Cleanup: Desconectar el WebSocket cuando el componente se desmonte
    return () => {
      stompClient.disconnect(() => {
        console.log("Disconnected");
      });
    };
  }, [id]); // Solo se ejecuta cuando cambia el ID de la partida

  const jugadoresList = jugadores.map((jugador) => (
    <tr key={jugador.id}>
      <td>{jugador.usuario.username}</td>
    </tr>
  ));

  const iniciarPartida = async () => {
    try {
      console.log(id);
      const response = await fetch(`/api/v1/partidas/${id}/iniciar-partida`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({}),
      });

      if (!response.ok) {
        const errorData = await response.json();
        showError(errorData.message || errorData);
        throw new Error("Network response was not ok");
      }

      navigate(`/tablero/${id}`);
    } catch (error) {
      console.error("Error:", error);
    }
  };

  const salirDeSala = async () => {
    try {
      if (!partida) return;
  
      // Jugador actual
      const jugadorActual = jugadores.find((j) => j.usuario.id === user.id);
  
      if (!jugadorActual) {
        showError("No se encontró al jugador en la partida.");
        return;
      }
  
      // Si el jugador es el dueño de la partida
      if (partida.ownerPartida === user.id) {
        if (jugadores.length === 1) {
          // El dueño es el único jugador, elimina el jugador y la partida
          await eliminarJugador(jugadorActual.id);
          await eliminarPartida(partida.id);
          navigate("/");
        } else {
          // Hay más jugadores, actualizar el ownerPartida
          const nuevoOwner = jugadores.find((j) => j.id !== jugadorActual.id);
          await actualizarOwnerPartida(partida.id, nuevoOwner.usuario.id);
          await eliminarJugador(jugadorActual.id);
          navigate("/");
        }
      } else {
        // No es el dueño, solo eliminar al jugador
        await eliminarJugador(jugadorActual.id);
        navigate("/");
      }
    } catch (error) {
      console.error("Error al salir de la sala:", error);
      showError("Error al intentar salir de la sala de espera.");
    }
  };
  
  const eliminarJugador = async (jugadorId) => {
    try {
      const response = await fetch(`/api/v1/jugadores/${jugadorId}/websocket`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });
  
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Error al eliminar el jugador.");
      }
      console.log("Jugador eliminado:", jugadorId);
    } catch (error) {
      console.error("Error al eliminar jugador:", error);
      throw error;
    }
  };
  
  const eliminarPartida = async (partidaId) => {
    try {
      const response = await fetch(`/api/v1/partidas/${partidaId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${jwt}`,
        },
      });
  
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Error al eliminar la partida.");
      }
      console.log("Partida eliminada:", partidaId);
    } catch (error) {
      console.error("Error al eliminar partida:", error);
      throw error;
    }
  };
  
  const actualizarOwnerPartida = async (partidaId, nuevoOwnerId) => {
    try {
      const response = await fetch(`/api/v1/partidas/${partidaId}/actualizar-owner`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ ownerPartida: nuevoOwnerId }),
      });
  
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Error al actualizar el owner de la partida.");
      }
      console.log("Owner actualizado a:", nuevoOwnerId);
    } catch (error) {
      console.error("Error al actualizar el owner de la partida:", error);
      throw error;
    }
  };  

  return (
    <>
      <div className="validation-errors">
        {errors.length > 0 &&
          errors.map((error, index) => (
            <Alert key={index} color="danger" className="slide-down-alert">
              {error}
            </Alert>
          ))}
      </div>
      <div className="sala-espera">
        <div className="hero-div-sala-espera">
          <h1>Lobby</h1>
          {partida !== null && partida.ownerPartida === user.id && (
            <div style={{ marginBottom: 20 }}>
              <Button outline color="success" onClick={iniciarPartida}>
                Iniciar Partida
              </Button>
            </div>
          )}
          {partida && (
            <div style={{ marginBottom: 20 }}>
              <Button outline color="danger" onClick={salirDeSala}>
                Salir de la Sala
              </Button>
            </div>
          )}
          <div className="jugadores-lista">
            <Table>
              <thead>
                <tr>
                  <th>Nombre de usuario</th>
                </tr>
              </thead>
              <tbody>{jugadoresList}</tbody>
            </Table>
          </div>
        </div>
      </div>
    </>
  );
}
