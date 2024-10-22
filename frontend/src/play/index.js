import React, {useState} from 'react';
import '../App.css';
import '../static/css/home/home.css';
import FormGenerator from "../components/formGenerator/formGenerator";
import { Button } from "reactstrap";
import Modal from '../components/modals/informacionSala.js';
import { Link } from "react-router-dom";
import { loginFormInputs } from "../play/form/crearSalaInputs.js";

export default function Play(){
  const [isModalOpen, setModalOpen] = useState(false);

  const handleOpenModal = () => setModalOpen(true);
  const handleCloseModal = () => setModalOpen(false);
  async function handleSubmit({ values }) {
  }
  const loginFormRef = React.createRef();  
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
        </div>
        <Modal show={isModalOpen} handleClose={handleCloseModal}>
        
        <h2>Crear sala</h2>
        <div className="auth-form-container">
          <FormGenerator
            ref={loginFormRef}
            inputs={loginFormInputs}
            onSubmit={handleSubmit}
            numberOfColumns={1}
            listenEnterKey
            buttonText="crear sala"
            buttonClassName="auth-button"
          />
        </div>
      </Modal>
      </div>
    );
}