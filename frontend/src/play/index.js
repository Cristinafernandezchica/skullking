import React from 'react';
import '../App.css';
import '../static/css/home/home.css';
import logo from '../static/images/gamelogo.png'
import { Button } from "reactstrap";
import { Link } from "react-router-dom";

export default function Play(){
    return (
      <div className="home-page-container">
        <div className="hero-div">
          <h1>Lobby</h1>
          <h3>---</h3>
          <Button outline color="success">
            <Link
              to={`/play`}
              className="btn sm"
              style={{ textDecoration: "none" }}
            >
              Crear partida
            </Link>
          </Button>
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
      </div>
    );
}