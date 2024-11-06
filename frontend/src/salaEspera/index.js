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
  const [partida, setPartida] = useState(null);

  useEffect(() => {
    const fetchJugadores = async () => {
      try {
        const response = await fetch(`/api/v1/partidas/${id}/jugadores`, {
          headers: {
            'Authorization': `Bearer ${jwt}`,
          }
        });
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        const data = await response.json();
        setJugadores(data);
      } catch (error) {
        console.error('Error fetching partida', error);
      }
    };

    const fetchPartida = async () => {
        try {
          const response = await fetch(`/api/v1/partidas/${id}`, {
            headers: {
              'Authorization': `Bearer ${jwt}`,
            }
          });
          if (!response.ok) {
            throw new Error("Network response was not ok");
          }
          const data = await response.json();
          setPartida(data);
        } catch (error) {
          console.error('Error fetching partida', error);
        }
      };

    fetchJugadores();
    fetchPartida();
  }, [id, jwt]);

  const jugadoresList = jugadores.map((jugador) => (
    <tr key={jugador.id}>
      <td>{jugador.usuario.username}</td>
      <td>{jugador.turno}</td>
    </tr>
  ));

  const iniciarPartida = async () => {
    try {
      const response = await fetch(`/api/v1/partidas/${id}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${jwt}`,
        },
        body: JSON.stringify({
          estado: 'JUGANDO',
        }),
      });

      if (!response.ok) {
        throw new Error('Network response was not ok');
      }

      const partidaIniciada = await response.json();
      navigate(`/tablero/${id}`);
      console.log('Partida iniciada:', partidaIniciada);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <div className="sala-espera">
      <div className="hero-div-sala-espera">
        <h1>Lobby</h1>
        <div style={{ marginBottom: 20 }}>
           
            <Button outline color="success" onClick={iniciarPartida}>Iniciar Partida</Button>

        </div>
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
