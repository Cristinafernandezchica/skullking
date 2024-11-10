import React, { useState, useEffect } from 'react';
import '../App.css';
import '../static/css/home/home.css';
import logo from '../static/images/gamelogo.png'
import tokenService from './../services/token.service';
import jwt_decode from "jwt-decode";
import { Button } from "reactstrap";
import { Link } from "react-router-dom";

export default function Home(){
    const [roles, setRoles] = useState([]);
    const jwt = tokenService.getLocalAccessToken();
    
    useEffect(() => {
        if (jwt) {
            setRoles(jwt_decode(jwt).authorities);
        }
    }, [jwt])
    return(
        <div className="home-page-container">
            <div className="hero-div">
                <h1>¿Quieres jugar a Skull King?</h1>
                {!jwt &&(
                    <div className = "hero">
                        <Button outline color="success" style={{ borderColor: 'white', color: 'white' }}>
                        <Link
                            to={`/register`}
                            className="btn sm"
                            style={{ textDecoration: "none" }}
                        >
                            Regístrate
                        </Link>
                        </Button>
                        <h3>o</h3>
                        <Button outline color="success" style={{ borderColor: 'white', color: 'white' }}>
                        <Link
                            to={`/login`}
                            className="btn sm"
                            style={{ textDecoration: "none" }}
                        >
                            Inicia sesión
                        </Link>
                        </Button>
                    </div>
                )}
                {roles.includes('PLAYER') && (
                    <Button outline color="success">
                        <Link
                            to={`/play`}
                            className="btn sm"
                            style={{ textDecoration: "none" }}
                        >
                            Ir a lobby de partidas
                        </Link>
                    </Button>
                )}             
            </div>
        </div>
    );
}