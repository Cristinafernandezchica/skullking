import React, { useEffect, useState } from 'react';
import '../App.css';
import '../static/css/salaEspera/salaEspera.css';
import { Button, Table } from "reactstrap";
import tokenService from '../services/token.service.js';
import { useNavigate } from 'react-router-dom';
import getIdFromUrl from '../util/getIdFromUrl.js';
import useFetchState from '../util/useFetchState.js'; // Importar correctamente

const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

export default function SalaEspera() {
  const navigate = useNavigate();
  const id = getIdFromUrl(2);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [jugadores, setJugadores] = useFetchState([], `/api/v1/partidas/${id}/jugadores`, jwt, setMessage, setVisible);
  const [partida, setPartida] = useFetchState(null, `/api/v1/partidas/${id}`, jwt, setMessage, setVisible)


  const jugadoresList = jugadores.map((jugador) => (
    <tr key={jugador.id}>
      <td>{jugador.usuario.username}</td>
      <td>{jugador.turno}</td>
    </tr>
  ));

  const iniciarPartida = async () => {
    try {
      console.log(id);
      const response = await fetch(`/api/v1/partidas/${id}/iniciar-partida`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${jwt}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
        }),
      });

      if (!response.ok) {
        console.log("algo falla")
        throw new Error('Network response was not ok');
      }

      navigate(`/tablero/${id}`);

    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <div className="sala-espera">
      <div className="hero-div-sala-espera">
        <h1>Lobby</h1>
       { partida!==null && partida.ownerPartida ===user.id && <div style={{ marginBottom: 20 }}>
            <Button outline color="success" onClick={iniciarPartida}>Iniciar Partida</Button>

        </div>}
        <div className="tabla-container">
        <Table aria-label="users" className="mt-4">
          <thead>
            <tr>
              <th>Username</th>
              <th>Turno</th>
            </tr>
          </thead>
          <tbody>{jugadoresList}</tbody>
        </Table>
      </div>
      </div>

    </div>
  );
}
