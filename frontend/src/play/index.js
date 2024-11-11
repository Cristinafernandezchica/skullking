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
      await createJugador(partida)
      navigate(`/salaEspera/${partida.id}`);
      console.log('Partida creada:', partida);
      handleCloseModal();
      
  } catch (error) {
      console.error('Error:', error);
  }
}

// Crear el jugador que ha creado la partida
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
              partida: partidaId,
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


    if (!response.ok) {
      const errorData = await response.json();
      setErrors(errorData);
      handleCloseUnionModal();
      showError(errorData.message || errorData);
      // throw new Error('Error creando jugador:', errorData);
      // throw new Error('Network response was not ok');
      // TODO: Tener en cuenta que puede que esté en juego y que entonces tenga que navegar a la pantalla de juego
      setTimeout(() => {
        navigate('/salaEspera/' + partidaId);
      }, 5000);
    }

    const jugador = await response.json();
    console.log('Jugador creado en partida:', jugador);
    navigate('/salaEspera/' + partidaId); 
    handleCloseUnionModal(); // Cierra el modal después de unirse a la partida
  } catch (error) {
    console.error('Error creando jugador:', error);
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
            <h3>---</h3>
            <div style = {{marginBottom: 20}}>
              <Button outline color="success" onClick={handleOpenModal}>Crear partida</Button>
            </div>
            <div style = {{marginBottom: 20}}>
              <Button outline color="success" onClick={handleOpenUnionModal}>Unirse a una partida</Button>
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