import React, {useEffect, useState} from 'react';
import '../App.css';
import '../static/css/home/home.css';
import FormGenerator from "../components/formGenerator/formGenerator";
import { Button, Table } from "reactstrap";
import Modal from '../components/modals/informacionSala.js';
import { Link } from "react-router-dom";
import { loginFormInputs } from "../play/form/crearSalaInputs.js";
import tokenService from '../services/token.service.js';


const jwt = tokenService.getLocalAccessToken();
export default function Play(){
  const [isModalOpen, setModalOpen] = useState(false);
  const handleOpenModal = () => setModalOpen(true);
  const handleCloseModal = () => setModalOpen(false);
  const [jugadores, setJugadores] = useState([{id:1 ,puntuacion: "estoy de manera ilustrativa, no funciono :c "}]);

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



    return (
      <div className="home-page-container">
        <div className="hero-div">
          <h1>Lobby</h1>
          <h3>---</h3>
          <Button style={{ textDecoration: "none" }} onClick={handleOpenModal}>crear partida</Button>
          <Button outline color="success">
            <Link
              to={`/play`}
              className="btn sm"
              style={{ textDecoration: "none" }}
            >
              Unirse a una partida
            </Link>
          </Button>
          <Table aria-label="jugadores" className="mt-4">
          <thead>
            <tr>
              <th>Puntuaciones</th>
            </tr>
          </thead>
          <tbody>{jugadoresList}</tbody>
        </Table>
        </div>
      </div>
    );
}