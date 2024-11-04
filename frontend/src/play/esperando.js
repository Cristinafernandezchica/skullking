import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import tokenService from "../services/token.service";


const jwt = tokenService.getLocalAccessToken();
export default function Play() {

    const [jugadores, setjugadores] = useState([]);
    const {PartidaId} = useParams();
    const [message, setMessage] = useState(null);

    useEffect(() => {
        fetchJugadores(PartidaId);

    },[]);

    const fetchJugadores = async (PartidaId) => {
        try {
            const response = await fetch("/api/v1/jugadores/${id}/partida", {
              headers: { "Authorization": `Bearer ${jwt}` },
              method: "GET"
            });
            if (response.ok) {
              const data = await response.json();
              setjugadores(data);
            } else {
              setMessage("Error al obtener las partidas.");
            }
          } catch (error) {
            setMessage("Error de red al obtener las partidas.");
          }
    }

    return (
        <div className="play-page-container">
          <h1>Partida ID: {PartidaId}</h1>
          <h2>Lista de Jugadores</h2>
          {message && <p className="error-message">{message}</p>}
          {jugadores.length > 0 ? (
            <ul>
              {jugadores.map((player) => (
                <li key={player.id}>{player.nombre}</li>
              ))}
            </ul>
          ) : (
            <p>No hay jugadores en esta partida.</p>
          )}
        </div>
      );

}