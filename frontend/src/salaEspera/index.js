import React, { useEffect, useState } from 'react';
import '../App.css';
import '../static/css/salaEspera/salaEspera.css';
import { Button, Table, Alert } from "reactstrap";
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

  const [errors, setErrors] = useState([]);

  const showError = (error) => {
    setErrors([error]);
    setTimeout(() => {
      setErrors([]);
    }, 5000); // La alerta desaparece después de 5000 milisegundos (5 segundos)
  };

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
      console.error('Error encontrando jugadores:', error);
      setMessage('Error encontrando jugadores');
      setVisible(true);
    }
  };

  const fetchPartidaStatus = async () => {
    try {
      const response = await fetch(`/api/v1/partidas/${id}`, {
        headers: {
          'Authorization': `Bearer ${jwt}`
        }
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      if (data.estado === 'JUGANDO') {
        navigate(`/tablero/${id}`);
      }
    } catch (error) {
      console.error('Error encontrando el estado de la partida:', error);
    }
  };


  // Para que se actualice la lista de jugadores que se van uniendo y gestionar cambio a tablero
  useEffect(() => {
    fetchJugadores();
    const intervalIdJugadores = setInterval(fetchJugadores, 3000);
    const intervalIdPartida = setInterval(fetchPartidaStatus, 3000);

    return () => {
      clearInterval(intervalIdJugadores);
      clearInterval(intervalIdPartida);
    };
  }, []);


  const jugadoresList = jugadores.map((jugador) => (
    <tr key={jugador.id}>
      <td>{jugador.usuario.username}</td>
    </tr>
  ));


  const iniciarPartida = async () => {
    try {
      console.log(id);
      const response = await fetch(`/api/v1/partidas/${id}/iniciar-partida`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${jwt}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
        }),
      });


      if (!response.ok) {
        const errorData = await response.json();
        showError(errorData.message || errorData);
        console.log("Error al iniciar una partida")
        throw new Error('Network response was not ok');
      }
      //setPartida(response.json())
      //console.log(partida);

      navigate(`/tablero/${id}`);


    } catch (error) {
      console.error('Error:', error);
    }
  };


  return (
    <>
      <div className="validation-errors">
          {errors.length > 0 && errors.map((error, index) => (
            <Alert key={index} color="danger" className="slide-down-alert">
              {error}
            </Alert>
          ))}
        </div>
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
                  <th>Nombre de usuario</th>
                </tr>
              </thead>
              <tbody>
                {jugadoresList}
              </tbody>
            </Table>
          </div>
        </div>
      </div>
    </>
  );
}