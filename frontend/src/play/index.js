import React, {useEffect, useState} from 'react';
import '../App.css';
import '../static/css/home/home.css';
import { Button, Table } from "reactstrap";
import { Link, useNavigate } from "react-router-dom";
import tokenService from '../services/token.service.js';
import CrearPartidaModal from '../components/modals/CrearPartidaModal.js';


const jwt = tokenService.getLocalAccessToken();
export default function Play(){
  const [isModalOpen, setModalOpen] = useState(false);
  const handleOpenModal = () => setModalOpen(true);
  const handleCloseModal = () => setModalOpen(false);
  const navigate = useNavigate();

  // Usuario completo para crear al jugador
  const user = tokenService.getUser();

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
          throw new Error('Network response was not ok');
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





    return (
      <div className="home-page-container">
        <div className="hero-div">
          <h1>Lobby</h1>
          <h3>---</h3>
          <div style = {{marginBottom: 20}}>
            <Button outline color="success" onClick={handleOpenModal}>Crear partida</Button>
          </div>
          <Button outline color="success">
            <Link
              to={`/play`}
              className="btn sm"
              style={{ textDecoration: "none" }}
            >
              Unirse a una partida
            </Link>
          </Button>
        <CrearPartidaModal
                    isVisible={isModalOpen}
                    onCancel={handleCloseModal}
                    onConfirm={crearPartida}
                />
        </div>
      </div>
    );
}