import React, { useState } from 'react';
import './JugadorInfo.css';
import tokenService from "../services/token.service";
import useFetchState from '../util/useFetchState';
import getIdFromUrl from '../util/getIdFromUrl';
import manito from  'frontend/src/static/images/cartas/morada_1.png'

const jwt = tokenService.getLocalAccessToken();


export default function Jugando() {
    const id = getIdFromUrl(2);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [jugador, setJugador] = useFetchState(
      [],
      `/api/v1/jugadores/${id}`,
      jwt,
      setMessage,
      setVisible,
      id
    );
    


        /*
            const [mano, setMano] = useFetchState(null,
      `/api/v1/manos/4`,
      jwt,
      setMessage,
      setVisible
    );  
    console.log("mano encontrada",mano);

    <div className="cartas">
            {mano!==null && mano.cartas.map((carta) => (
              <div key={carta.id} className="carta">
                <button className='boton-agrandable'>
                <img 
                  src={manito} 
                  alt={`Carta ${carta.tipoCarta}`} 
                  className="imagen-carta" 
                />
                </button>
              </div>
            ))}
          </div>

    */

  return (
    <div>
    <div className="lista-jugadores">
      {jugador.map((jugador) => (
        <div key={jugador.id} className="jugador-info">
          <h3>{jugador.usuario.username}</h3>
          <p>Apuesta: {jugador.apuesta}</p>
          <p>Puntos: {jugador.puntos}</p>
        </div>
      ))}
    </div>
      </div>
  );
}
