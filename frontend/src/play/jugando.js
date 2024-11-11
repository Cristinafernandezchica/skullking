import React, { useEffect, useState } from 'react';
import './JugadorInfo.css';
import tokenService from "../services/token.service";
import useFetchState from '../util/useFetchState';
import getIdFromUrl from '../util/getIdFromUrl';
import manito from  'frontend/src/static/images/cartas/morada_1.png'

const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

export default function Jugando() {
    const id = getIdFromUrl(2);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [jugador, setJugador] = useFetchState(
      [],
      `/api/v1/jugadores/${id}`,
      jwt,
      setMessage,
      setVisible
    );
    const [tu,setTu] = useFetchState(null,`/api/v1/jugadores/${user.id}/usuario`,jwt,setMessage,setVisible); 
    const [mano, setMano] = useState(null);
    const [ronda,setRonda] = useState(null);
    const [truco,setTruco] = useState(null);

    useEffect(() => {
      const fetchMano = async (jugadorId) => {
          try {
              const response = await fetch(`/api/v1/manos/${jugadorId}`, {
                  headers: {
                      "Authorization": `Bearer ${jwt}`,
                      'Content-Type': 'application/json'
                  }
              });
              if (!response.ok) {
                  throw new Error("Network response was not ok");
              }
              const data = await response.json();
              setMano(data);
              
              // Fetch jugadores for each partida
          } catch (error) {
              console.error("Error fetching partidas:", error);
              setMessage(error.message);
              setVisible(true);
          }
      };
      if(tu!==null){
      fetchMano(tu.id);}
  }, [tu]);

  /*
  useEffect(() => {
    const fetchRondaActual = async (partidaId) => {
        try {
            const response = await fetch(`/api/v1/rondas/${partidaId}/partida`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                }
            });
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            const data = await response.json();
            setRonda(data);
            
        } catch (error) {
            console.error("Error fetching partidas:", error);
            setMessage(error.message);
            setVisible(true);
        }
    };
    fetchRondaActual(id);
}, []);


  const jugarTruco = async () => {
    try {
      console.log(id);
      const response = await fetch(`/api/v1/`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${jwt}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          
        }),
      });

      if (!response.ok) {
        console.log("algo falla")
        throw new Error('Network response was not ok');
      }


    } catch (error) {
      console.error('Error:', error);
    }
  };

*/
  console.log("mano encontrada",mano);
  console.log("ronda encontrada",ronda);
   console.log(jugador);
  console.log(id);
  return (
    <div>
    <div className="lista-jugadores">
      {jugador!==null  && jugador.map((jugador) => (
        <div key={jugador.id} className="jugador-info" >
          <h3>{jugador.usuario.username}</h3>
          <p>Apuesta: {jugador.apuesta}</p>
          <p>Puntos: {jugador.puntos}</p>
        </div>
      ))}
    </div>
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
      </div>
  );
}
