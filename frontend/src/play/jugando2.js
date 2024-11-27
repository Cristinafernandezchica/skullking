import React, { useEffect, useState } from 'react';
import './JugadorInfo.css';
import tokenService from "../services/token.service";
import useFetchState from '../util/useFetchState';
import getIdFromUrl from '../util/getIdFromUrl';
import RondaActual from './rondaActual';
import BazaActual from './bazaActual';
import ApuestaModal from '../components/modals/ApostarModal';
import JugadoresPartida from './jugadores';
import Truco from './truco';
import Mano from './mano';

const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

export default function Jugando2() {
  const idPartida = getIdFromUrl(2);

  // Estados
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [jugadores, setJugadores] = useFetchState(
    [],
    `/api/v1/jugadores/${idPartida}`,
    jwt,
    setMessage,
    setVisible
  );
  const [tu, setTu] = useFetchState(
    null,
    `/api/v1/jugadores/${user.id}/usuario`,
    jwt,
    setMessage,
    setVisible
  );
  const [mano, setMano] = useState(null);
  const [manosOtrosJugadores, setManosOtrosJugadores] = useState({});
  const [ronda, setRonda] = useState(null);
  const [truco, setTruco] = useState(null);
  const [bazaActual, setBazaActual] = useState(null);
  const [apuestaModalOpen, setApuestaModalOpen] = useState(false);
  const [visualizandoCartas, setVisualizandoCartas] = useState(true);

  // Constantes de tiempo
  const TIEMPO_VER_CARTAS = 5000; // Cambiar a 30000 si es necesario
  const TIEMPO_REALIZAR_APUESTA = 16000; // Cambiar a 60000 si es necesario

  // Función para abrir el modal de apuestas
  const abrirModalApuestas = () => {
    setApuestaModalOpen(true);
  };

  const cerrarVisualizacionCartas = async () => {
    try {
        setVisualizandoCartas(false);
        const jugadoresData = await JugadoresPartida.fetchJugadores(idPartida, jwt); // Llama a la clase Jugadores
        setJugadores(jugadoresData); // Actualiza el estado con los datos de los jugadores para actualizar estado puntos de cada Jugador
    } catch (error) {
        console.error("Error al cerrar visualización de cartas:", error);
        setMessage(error.message);
        setVisible(true);
    }
};

// Temporizador para abrir el modal de apuestas
useEffect(() => {
    const timerAbrirApuestas = setTimeout(abrirModalApuestas, TIEMPO_VER_CARTAS);
    return () => clearTimeout(timerAbrirApuestas);
}, []);

// Temporizador para cerrar visualización de cartas y actualizar jugadores
useEffect(() => {
    const timerCerrarApuestas = setTimeout(cerrarVisualizacionCartas, TIEMPO_REALIZAR_APUESTA);
    return () => clearTimeout(timerCerrarApuestas);
}, []);

// Función para realizar una apuesta
const apostar = async (ap) => {
    try {
        const response = await fetch(`/api/v1/manos/apuesta/${tu.id}?apuesta=${ap}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`,
            },
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(`Error al hacer la apuesta: ${errorData.message || 'Error desconocido'}`);
        }

        console.log("Apuesta realizada con éxito");
        setApuestaModalOpen(false); // Cierra el modal después de apostar

        const jugadoresData = await JugadoresPartida.fetchJugadores(idPartida, jwt); // Llama a la clase Jugadores
        setJugadores(jugadoresData); // Actualiza el estado con los datos de los jugadores
    } catch (error) {
        console.error('Error al realizar la apuesta:', error);
        setMessage(error.message);
        setVisible(true);
    }
};

  useEffect(() => {
    const fetchDatosManos = async () => {
        try {
            if (tu !== null) {
                // Obtiene la mano del usuario actual
                const miMano = await Mano.fetchManoDeJugador(tu.id, jwt);
                setMano(miMano); // Actualiza el estado con la mano del jugador actual

                // Obtiene las manos de los otros jugadores
                const manosDeOtros = await Mano.fetchManosDeOtrosJugadores(jugadores, tu.id, jwt);
                setManosOtrosJugadores(manosDeOtros); // Actualiza el estado con las manos de los otros jugadores
            }
        } catch (error) {
            setMessage(error.message); // Muestra el mensaje de error en el UI
            setVisible(true); // Controla la visibilidad del mensaje de error
        }
    };

    fetchDatosManos(); // Llama a la función asíncrona
}, [jugadores, tu]); // Se ejecuta cuando cambian los jugadores o el usuario actual (`tu`)

  // Fetch de la ronda actual
  useEffect(() => {
    const fetchRonda = async () => {
      try {
        const rondaData = await RondaActual.fetchRondaActual(idPartida, jwt);
        setRonda(rondaData);
      } catch (error) {
        setMessage(error.message);
        setVisible(true);
      }
    };

    fetchRonda();
  }, [idPartida]);

  // Fetch de la baza actual
  useEffect(() => {
    const fetchBaza = async () => {
      try {
        if (ronda !== null) {
          const bazaData = await BazaActual.fetchBazaActual(ronda.id, jwt);
          setBazaActual(bazaData);
        }
      } catch (error) {
        setMessage(error.message);
        setVisible(true);
      }
    };

    fetchBaza();
  }, [ronda]);

  useEffect(() => {
    const fetchTruco = async () => {
        if (bazaActual !== null && tu !== null) {
            try {
                const trucoMio = await Truco.fetchTrucoMio(BazaActual.id, jwt, tu.usuario.id); // Llama al método estático
                if (trucoMio) {
                    setTruco(trucoMio); // Actualiza el estado con el truco del usuario
                }
            } catch (error) {
                setMessage(error.message); // Maneja el error
                setVisible(true);
            }
        }
    };

    fetchTruco(); // Llama la función asíncrona
}, [bazaActual, tu]); // Dependencias del efecto


const jugarTruco = async (trucoAJugar) => {
    try {
      const response = await fetch(`/api/v1/trucos/${trucoAJugar.id}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${jwt}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(trucoAJugar),
      });

      if (!response.ok) {
        console.log("algo falla")
        throw new Error('Network response was not ok');
      }

      const data = await response.json();
      console.log("tengo muchisimos console.log",data);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const  quitarCarta = async (miMano) => {
    try {
      const response = await fetch(`/api/v1/manos/${miMano.id}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${jwt}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(miMano),
      });

      if (!response.ok) {
        console.log("algo falla")
        throw new Error('Network response was not ok');
      }

      const data = await response.json();
      console.log("mano cambiada",data);
      setMano(data);
    } catch (error) {
      console.error('Error:', error);
    }
  };

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
        {mano !== null &&
          mano.cartas.map((carta) => (
            <div key={carta.id} className="carta">
              <button
                className="boton-agrandable"
                disabled={visualizandoCartas}
                onClick={() => {
                  truco.carta = carta;
                  mano.cartas = mano.cartas.filter((cartaAEliminar) => carta.id !== cartaAEliminar.id);
                  setTruco(truco);
                  console.log("modificado", truco);
                  // Funciones `jugarTruco` y `quitarCarta` deben estar definidas
                  jugarTruco(truco);
                  quitarCarta(mano);
                }}
              >
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
        onCancel={() => setApuestaModalOpen(false)}
        onConfirm={apostar}
      />
    </div>
  );
}