import React, {useEffect, useState} from 'react';
import '../App.css';
import '../static/css/home/home.css';
import { Alert, Button, Table } from "reactstrap";
import { Link, useNavigate } from "react-router-dom";
import tokenService from '../services/token.service.js';
import CrearPartidaModal from '../components/modals/CrearPartidaModal.js';
import UnirPartidaModal from '../components/modals/UnirPartidaModal.js'
import './slideAlerts.css';


const jwt = tokenService.getLocalAccessToken();
export default function Play(){
  // Para modal creación partida
  const [isModalOpen, setModalOpen] = useState(false);
  const handleOpenModal = () => setModalOpen(true);
  const handleCloseModal = () => setModalOpen(false);
  const navigate = useNavigate();
  const user = tokenService.getUser();
  // Para modal unirse a partida
  const [isUnionModalOpen, setUnionModalOpen] = useState(false);
  const handleOpenUnionModal = () => setUnionModalOpen(true);
  const handleCloseUnionModal = () => setUnionModalOpen(false);
  // Para manejo de errores (unirse a partida)
  const [errors, setErrors] = useState([]);
  const [partidaJugador,setPartidaJugador] = useState(false);


  const showError = (error) => { 
    setErrors([error]); 
    setTimeout(() => { 
      setErrors([]); 
    }, 5000); // La alerta desaparece después de 5000 milisegundos (5 segundos) 
  };


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
              ownerPartida: user.id
          }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        setErrors(errorData);
        handleCloseModal();
        showError(errorData.message || errorData);
        // throw new Error('Error creando jugador:', errorData);
        // throw new Error('Network response was not ok');
        // TODO: Navegar a pantalla de sala de espera correspondiente o a la pantalla de juego (se va a necesitar método en backend creo)
      }

      const partida = await response.json();
      console.log('Partida creada:', partida);
      // Crear el jugador que ha creado la partida  -->  Modificación en backend
      /*
      await createJugador(partida)
      navigate(`/salaEspera/${partida.id}`);
      console.log('Partida creada:', partida);
      handleCloseModal();
      */
      // Crear el jugador de la partida creada (si salta excepción, borra la partida)
      try {
        await createJugador(partida);
        navigate(`/salaEspera/${partida.id}`);
        handleCloseModal();
    } catch (error) {
        // Si falla la creación del jugador, eliminar la partida
        await eliminarPartida(partida.id);
        showError("Error al crear jugador: " + error.message);
        console.error("Error al crear jugador:", error);
    }
      
  } catch (error) {
      console.error('Error:', error);
  }
}

// Crear el jugador que ha creado la partida
const createJugador = async (partida) => {
  try {
      const response = await fetch('/api/v1/jugadores', {
          method: 'POST',
          headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${jwt}`,
          },
          body: JSON.stringify({
              puntos: 0,
              partida: { id: partida.id },
              usuario: { id: user.id },
          }),
      });

      if (!response.ok) {
          const errorData = await response.json();
          showError(errorData.message || errorData);
          throw new Error('Network response was not ok');
      }

      const jugador = await response.json();
      console.log('Jugador creado:', jugador);
  } catch (error) {
      console.error('Error creando jugador:', error);
  }
};

// Eliminar partida en caso de que el jugador ya tenga una
const eliminarPartida = async (partidaId) => {
  try {
      const response = await fetch(`/api/v1/partidas/${partidaId}`, {
          method: 'DELETE',
          headers: {
              'Authorization': `Bearer ${jwt}`,
          },
      });

      if (!response.ok) {
          console.error('Error eliminando la partida:', await response.json());
          showError("Error al intentar borrar la partida después del fallo.");
      } else {
          console.log('Partida eliminada exitosamente:', partidaId);
      }
  } catch (error) {
      console.error('Error en la solicitud de eliminación de partida:', error);
      showError("Error en la conexión al intentar borrar la partida.");
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
        usuario: user
      }),
    });


    if (!response.ok) {
      const errorData = await response.json();
      setErrors(errorData);
      handleCloseUnionModal();
      showError(errorData.message || errorData);
      // TODO: Mirar como navegar a la pantalla correspondiente
      /*
      setTimeout(() => {
        navigate('/salaEspera/' + partidaId);
      }, 5000);
      */
    }

    const jugador = await response.json();
    console.log('Jugador creado en partida:', jugador);
    navigate('/salaEspera/' + partidaId); 
    handleCloseUnionModal(); // Cierra el modal después de unirse a la partida
  } catch (error) {
    console.error('Error creando jugador:', error);
  }
};
const fetchPartidaJugador = async () => {
  try {
      const response = await fetch(`/api/v1/jugadores/${user.id}/partida`, {
          headers: {
              "Authorization": `Bearer ${jwt}`
          }
      });
      if (!response.ok) {
          throw new Error("Network response was not ok");
      }
      const data = await response.json();
      setPartidaJugador(data);
  } catch (error) {
      console.error("Error buscando partidas en juego:", error);
  }
};
useEffect(() => {
  fetchPartidaJugador();
}, []);

const handleVolverPartida = () => {
  if (partidaJugador && ( partidaJugador.estado === "JUGANDO" || partidaJugador.estado === "ESPERANDO")) {
    navigate(partidaJugador.estado === "ESPERANDO" ? `/salaEspera/${partidaJugador.id}` : `/tablero/${partidaJugador.id}`)
  } else {
    showError("No estás jugando ninguna partida.");
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
        <div className="home-page-container">
          <div className="hero-div">
            <h1>Lobby</h1>
            <div style = {{marginBottom: 20}}>
              <Button outline color="success" onClick={handleOpenModal}>Crear partida</Button>
            </div>
            <div style = {{marginBottom: 20}}>
              <Button outline color="success" onClick={handleOpenUnionModal}>Unirse a una partida</Button>
            </div>

            <div style={{ marginBottom: 20 }}>
              {partidaJugador  && (
                <Button outline color="success" onClick={handleVolverPartida}>Volver a partida</Button>
              )}
            </div>

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
      </>
    );
}