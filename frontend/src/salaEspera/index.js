import React, { useEffect, useState } from 'react';
import '../App.css';
import '../static/css/salaEspera/salaEspera.css';
import { Button, Table } from "reactstrap";
import tokenService from '../services/token.service.js';
import { useNavigate } from 'react-router-dom';
import getIdFromUrl from '../util/getIdFromUrl.js';
import useFetchState from '../util/useFetchState.js'; 
import InicioPartidaModal from '../components/modals/InicioPartidaModal.js';

const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

export default function SalaEspera() {

  const navigate = useNavigate();
  const id = getIdFromUrl(2);
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  //const [jugadores, setJugadores] = useFetchState([], `/api/v1/partidas/${id}/jugadores`, jwt, setMessage, setVisible);
  const [partida, setPartida] = useFetchState(null, `/api/v1/partidas/${id}`, jwt, setMessage, setVisible);
  const [jugadores, setJugadores] = useState([]);

  const fetchJugadores = async () => {
    try {
      const response = await fetch(`/api/v1/partidas/${id}/jugadores`, {
        headers: {
          'Authorization': `Bearer ${jwt}`
        }
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      setJugadores(data);
    } catch (error) {
      console.error('Error fetching jugadores:', error);
      setMessage('Error fetching jugadores');
      setVisible(true);
    }
  };

  // Para que se actualice la lista de jugadores que se van uniendo
  useEffect(() => {
    fetchJugadores();

    const intervalId = setInterval(fetchJugadores, 3000);

    return () => clearInterval(intervalId);
  }, []);


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
        <div className="jugadores-lista"> 
          <Table> 
            <thead> 
              <tr> 
                <th>Username</th> 
                <th>Turno</th> 
              </tr> 
            </thead> 
            <tbody> 
              {jugadoresList} 
            </tbody> 
          </Table> 
        </div>
      </div>
    </div>
  );
}
