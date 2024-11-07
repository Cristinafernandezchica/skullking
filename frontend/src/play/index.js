import React, {useEffect, useState} from 'react';
import '../App.css';
import '../static/css/home/home.css';
import FormGenerator from "../components/formGenerator/formGenerator";
import { Button, Table } from "reactstrap";
import Modal from '../components/modals/informacionSala.js';
import { Link } from "react-router-dom";
import { loginFormInputs } from "../play/form/crearSalaInputs.js";
import tokenService from '../services/token.service.js';
import CrearPartidaModal from '../components/modals/CrearPartidaModal.js';
import { useNavigate } from 'react-router-dom';
import useFetchState from '../util/useFetchState.js';
import UnirPartidaModal from '../components/modals/UnirPartidaModal.js'

const user = tokenService.getUser();
const jwt = tokenService.getLocalAccessToken();
export default function Play(){
  // Para modal creación partida
  const [isModalOpen, setModalOpen] = useState(false);
  const handleOpenModal = () => setModalOpen(true);
  const handleCloseModal = () => setModalOpen(false);
  const [jugadores, setJugadores] = useState([{id:1 ,puntuacion: "estoy de manera ilustrativa, no funciono :c "}]);

  const[partida, setPartida] = useState();
  const navigate = useNavigate();
  //const [jugador, setJugador] = useFetchState([], `/api/v1/`, jwt);

  // Para modal unirse a partida
  const [isUnionModalOpen, setUnionModalOpen] = useState(false);
  const handleOpenUnionModal = () => setUnionModalOpen(true);
  const handleCloseUnionModal = () => setUnionModalOpen(false);

  /*
  useEffect(() => {
    fetchJugadores();
  }, []);

const fetchJugadores = async () => {
  try {
    const response = await fetch('/api/v1/jugadores/3', jwt);
    const jugadores = await response.json();
    setJugadores(jugadores);
  } catch (error) {
    console.error('Error:', error);
  }
}

const jugadoresList = jugadores.map((jugador) => {
  return (
    <tr key={jugador.id}>
      <td>{jugador.puntuacion}</td>
    </tr>
  )
});
*/
// para crear la partida
const crearPartida = async (nombrePartida) => {
  try {
      const response = await fetch('/api/v1/partidas', {
          method: 'POST',
          headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${jwt}`,
          },
          body: JSON.stringify({
              nombre: nombrePartida,
              inicio: new Date().toISOString(), // Fecha actual en formato ISO
              estado: "ESPERANDO",
              // ownerPartida: user
          }),
      });

      if (!response.ok) {
          throw new Error('Network response was not ok');
      }

      const partida = await response.json();
      console.log('Partida creada:', partida);

      // Crear el jugador que ha creado la partida  -->  Modificación en backend
      await createJugador(partida)

      /*
      console.log('Redireccionando a la sala de espera...');
      navigate('/salaEspera/' + partidaCreada.id);
      */

      handleCloseModal();
      return partida.id;
  } catch (error) {
      console.error('Error:', error);
      console.log('Partida creada:', partida);
  }
}

// Crear el jugador que ha creado la partida
//TODO: se crean 20 jugadores por la cara -->  ?????
const createJugador = async (partidaId) => {
  try {
      const response = await fetch('/api/v1/jugadores', {
          method: 'POST',
          headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${jwt}`,
          },
          body: JSON.stringify({
              puntos: 0,
              partida: partida,
              usuario: user,
              turno: 0  // se crea con turno 0, ya que se supone que la partida aún no ha comenzado
          }),
      });

      if (!response.ok) {
          throw new Error('Network response was not ok');
      }

      const jugador = await response.json();
      console.log('Jugador creado:', jugador);
  } catch (error) {
      console.error('Error creando jugador:', error);
  }
};

// Confirmar unión a partida con el id específico
const unirseAPartida = async (partidaId) => {
  try {
    const response = await fetch('/api/v1/jugadores', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwt}`,
      },
      body: JSON.stringify({
        puntos: 0,
        partida: { id: partidaId },
        usuario: user,
        turno: 0
      }),
    });


    if (!response.ok) throw new Error('Network response was not ok');


    const jugador = await response.json();
    console.log('Jugador creado en partida:', jugador);
    handleCloseUnionModal(); // Cierra el modal después de unirse a la partida
  } catch (error) {
    console.error('Error creando jugador:', error);
  }
};

    
    return (
      <div className="home-page-container">
        <div className="hero-div">
          <h1>Crear sala</h1>
          <div style = {{marginBottom: 20}}>
            <Button outline color="success" onClick={handleOpenModal}>Crear partida</Button>
          </div>
          <div style = {{marginBottom: 20}}>
            <Button outline color="success" onClick={handleOpenUnionModal}>Unirse a una partida</Button>
          </div>

          <Table aria-label="jugadores" className="mt-4">
          <thead>
            <tr>
              <th>Puntuaciones</th>
            </tr>
          </thead>
          {/*<tbody>{jugadoresList}</tbody>*/}
        </Table>
        <CrearPartidaModal
          isVisible={isModalOpen}
          onCancel={handleCloseModal}
          onConfirm={crearPartida}
                />
        <UnirPartidaModal
          isVisible={isUnionModalOpen}
          onCancel={handleCloseUnionModal}
          onConfirm={unirseAPartida}
        />
        </div>
      </div>
    );
}