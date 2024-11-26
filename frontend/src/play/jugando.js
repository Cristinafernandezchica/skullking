import React, { useEffect, useState } from 'react';
import './JugadorInfo.css';
import tokenService from "../services/token.service";
import useFetchState from '../util/useFetchState';
import getIdFromUrl from '../util/getIdFromUrl';
import ApuestaModal from '../components/modals/ApostarModal';
// import manito from  'frontend/src/static/images/cartas/morada_1.png'

const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

export default function Jugando() {
    const idPartida = getIdFromUrl(2);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [jugadores, setJugadores] = useFetchState(
      [],
      `/api/v1/jugadores/${idPartida}`,
      jwt,
      setMessage,
      setVisible
    );
    const [tu,setTu] = useFetchState(null,`/api/v1/jugadores/${user.id}/usuario`,jwt,setMessage,setVisible); 
    const [mano, setMano] = useState(null);
    const [ronda,setRonda] = useState(null);
    const [truco,setTruco] = useState(null);
    // para las cartas del resto de jugadores
    const [manosOtrosJugadores, setManosOtrosJugadores] = useState({});
    
    // Para lógica de apuesta
    const [apuestaModalOpen, setApuestaModalOpen] = useState(false);
    const toggleApuestaModal = () => setApuestaModalOpen(!apuestaModalOpen);
    const [visualizandoCartas, setVisualizandoCartas] = useState(true)


    // manejo turno
    const [turnoActual, setTurnoActual] = useState(null);

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
      /*
      const fetchTurnoActual = async (jugadorId) => {
        try {
          const response = await fetch(`/api/v1/jugadores/${jugadorId}/turno`, {
            headers: {
              "Authorization": `Bearer ${jwt}`,
              'Content-Type': 'application/json'
            }
          });
          if (!response.ok) {
            throw new Error("Network response was not ok");
          }
          const data = await response.json();
          setTurnoActual(data);
        } catch (error) {
          console.error("Error fetching turno actual:", error);
          setMessage(error.message);
          setVisible(true);
        }
      };
      */
/*
      if(tu!==null){
        fetchMano(tu.id);
        // fetchTurnoActual(tu.id);
      }
    }, [tu]);
*/

    const fetchManosOtrosJugadores = async () => { 
      try { 
        const nuevasManos = {}; 
        for (const jugador of jugadores) { 
          if (jugador.id !== tu.id) { 
            const response = await fetch(`/api/v1/manos/${jugador.id}`, { 
              headers: { "Authorization": `Bearer ${jwt}`, 
              'Content-Type': 'application/json' } }); 
              if (!response.ok) { 
                throw new Error("Network response was not ok"); 
              } 
              const data = await response.json(); 
              nuevasManos[jugador.id] = data; 
          } 
        } 
        setManosOtrosJugadores(nuevasManos); 
        } catch (error) { 
          console.error("Error fetching manos de otros jugadores:", error); 
          setMessage(error.message); setVisible(true); 
        } 
      }; 
        if (tu !== null) { 
          fetchMano(tu.id); 
          fetchManosOtrosJugadores(); 
        } 
      }, [jugadores, tu]);


    const fetchJugadores = async () => {
      try {
          const response = await fetch(`/api/v1/jugadores/${idPartida}`, {
              headers: {
                  "Authorization": `Bearer ${jwt}`,
                  'Content-Type': 'application/json'
              }
          });
          if (!response.ok) {
              throw new Error("Network response was not ok");
          }
          const data = await response.json();
          setJugadores(data);
      } catch (error) {
          console.error("Error fetching jugadores:", error);
          setMessage(error.message);
          setVisible(true);
      }
    };

    // Para abrir el modal de apuesta
    useEffect(() => {
      const timerAbrirApuestas = setTimeout(() => {
        setApuestaModalOpen(true);
      }, 5000); // Cambiar a 30 (30000)

      return () => clearTimeout(timerAbrirApuestas);
    }, []);

    // Para actualizar la visualización de la apuesta en todos los jugadores
    useEffect(() => {
      const timerCerrarApuestas = setTimeout(() => {
        setVisualizandoCartas(false);
        fetchJugadores();
      }, 16000); // Hay que cambiarlo a 60000 (60 segundos entre ver cartas y apostar)

      return () => clearTimeout(timerCerrarApuestas);
    }, []);

    const apostar = async (ap) => {
      try {
          const response = await fetch(`/api/v1/manos/apuesta/${tu.id}?apuesta=${ap}`, {
              method: 'PUT',
              headers: {
                  'Content-Type': 'application/json',
                  'Authorization': `Bearer ${jwt}`,
              }
          });

          if (!response.ok) {
              const errorData = await response.json();
              throw new Error(`Error al hacer la apuesta: ${errorData.message || 'Error desconocido'}`);
          }

          console.log("Apuesta realizada con éxito");
          toggleApuestaModal();
          fetchJugadores();

      } catch (error) {
          console.error('Error:', error);
          throw error;
      }
    };

    /*
    const jugarTruco = async (carta) => {
      if (visualizandoCartas){
        console.log("Aún no puedes jugar una carta, espera a que terminen las apuestas y sea tu turno")
      }
      if (tu.id !== turnoActual) {
        console.log("No es tu turno");
        return;
      }

      try {
        const response = await fetch(`/api/v1/trucos/${id}`, {
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${jwt}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            jugadorId: tu.id,
            cartaId: carta.id
          })
        });

        if (!response.ok) {
          console.log("algo falla")
          throw new Error('Network response was not ok');
        }

        const data = await response.json();
        setTurnoActual(data.turnoActual);

      } catch (error) {
        console.error('Error:', error);
      }
    };
    */

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
    console.log(jugadores);
    console.log(idPartida);


    return (
      <div className = "tablero">
        <div className="lista-jugadores">
          {jugadores!==null  && jugadores.map((jugador) => (
            <div key={jugador.id} className="jugador-info" >
              <h3>{jugador.usuario.username}</h3>
              <p>Apuesta: {jugador.apuestaActual}</p>
              <p>Puntos: {jugador.puntos}</p>
            </div>
          ))}
        </div>

        <div className="cartas-otros-jugadores">
          {Object.keys(manosOtrosJugadores).map(jugadorId => (
            <div key={jugadorId} className="carta-otros-jugadores">
              {manosOtrosJugadores[jugadorId].cartas.map((carta) => (
                <img 
                  key={carta.id}
                  src={carta.imagenTrasera}
                  alt={`Carta ${carta.tipoCarta}`}
                  className="imagen-carta-otras"
                />
              ))}
            </div>
          ))}
        </div>


        <div className="cartas">
            {mano!==null && mano.cartas.map((carta) => (
              <div key={carta.id} className="carta">
                <button className='boton-agrandable' disabled={visualizandoCartas}> {/*onClick={() => jugarTruco(carta)}*/}
                <img 
                  src={carta.imagenFrontal} 
                  alt={`Carta ${carta.tipoCarta}`} 
                  className="imagen-carta" 
                />
                </button>
              </div>
            ))}
        </div>

        <ApuestaModal
                isVisible={apuestaModalOpen}
                onCancel={toggleApuestaModal}
                onConfirm={apostar}
                      />
      </div>
    );
}
