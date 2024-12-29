import React, { useEffect, useState } from 'react';
import './JugadorInfo.css';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap'; 
import tokenService from "../services/token.service";
import useFetchState from '../util/useFetchState';
import getIdFromUrl from '../util/getIdFromUrl';
import ApuestaModal from '../components/modals/ApostarModal';
import ElegirTigresaModal from '../components/modals/ElegirTigresaModal';
import GanadorBazaModal from '../components/modals/GanadorBazaModal';
import Partida from './partidaActual';
import Mano from './manoActual';
import Carta from './carta';
import Trucos from './trucos';
import Jugadores from './jugadores';
import Baza from './bazaActual';
import Ronda from './rondaActual';
import Apuesta from './apuesta';

const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

export default function Jugando() {
    const idPartida = getIdFromUrl(2);
    const [partida, setPartida] = useState(null);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [jugadores, setJugadores] = useFetchState(
      [],
      `/api/v1/jugadores/${idPartida}`,
      jwt,
      setMessage,
      setVisible
    );
    const [modalTigresaOpen, setModalTigresaOpen] = useState(false); 
    const [eleccion, setEleccion] = useState('');
    const [nuevaTigresa, setNuevaTigresa] = useState();
    const [tu,setTu] = useFetchState(null,`/api/v1/jugadores/${user.id}/usuario`,jwt,setMessage,setVisible); 
    const [mano, setMano] = useState(null);
    const [ronda,setRonda] = useState(null);
    const [truco,setTruco] = useState(null);
    const [BazaActual, setBazaActual] = useState(null)
    const [ListaDeTrucos, setListaDeTrucos] = useState([])
    const [seCambiaPalo, setSeCambiaPalo] = useState(true)
    const [buscarUnaVezListaDeTrucos, setBuscarUnaVezListaDeTrucos] =useState(true)

    // para las cartas del resto de jugadores
    const [manosOtrosJugadores, setManosOtrosJugadores] = useState({});
    // Para lógica de apuesta
    
    const [apuestaModalOpen, setApuestaModalOpen] = useState(false);
    const toggleApuestaModal = () => setApuestaModalOpen(!apuestaModalOpen);
    const [visualizandoCartas, setVisualizandoCartas] = useState(true)

    // Mano disabled
    const [cartasDisabled, setCartasDisabled] = useState([]);

    // Mostrar ganador baza
    const [ganadorBazaModal, setGanadorBazaModal] = useState(false);
    const [ganadorBaza, setGanadorBaza] = useState('');
    const [ejecutadoGanadorBaza, setEjecutadoGanadorBaza] = useState(0);


    const fetchPartida = async (idPartida) => {
      try {
          const data = await Partida.fetchPartida(idPartida, jwt); // Usa el método de la clase Partida
          console.log("comprobar partida");
          setPartida(data); // Asigna los datos obtenidos
      } catch (error) {
          console.error("Error fetching partida:", error);
          setMessage(error.message);
          setVisible(true);
      }
    };

    useEffect(() => {
      const intervalo = setInterval(() => {
        fetchPartida(idPartida);
        // fetchBazaActual();
      }, 5000); // Cada 5 segundos
    
      return () => clearInterval(intervalo);
    }, [idPartida, tu]);


    const fetchMano = async (jugadorId) => { 
      try {
          const data = await Mano.fetchMano(jugadorId, jwt); // Llama al método estático de la clase Mano
          setMano(data);
          console.log("Nueva mano", data);
  
          // Después de obtener la mano, se hace la solicitud para obtener las cartas deshabilitadas
          await fetchCartasDisabled(data.id, BazaActual.paloBaza); 
      } catch (error) {
          console.error("Error encontrando mano:", error);
          setMessage(error.message);
          setVisible(true);
      }
    };
 
    const fetchCartasDisabled = async (idMano, paloActual) => { 
      try {
          const data = await Carta.fetchCartasDisabled(idMano, paloActual, jwt); // Llama al método estático de la clase Carta
          setCartasDisabled(data); // Asigna las cartas deshabilitadas obtenidas
          console.log("Cartas deshabilitadas:", data); // Imprime las cartas deshabilitadas
      } catch (error) {
          console.error("Error encontrando cartas deshabilitadas:", error);
          setMessage(error.message);
          setVisible(true);
      }
    };

    const fetchManosOtrosJugadores = async () => {  
      try { 
          const nuevasManos = await Mano.fetchManosOtrosJugadores(jugadores, tu.id, jwt); // Llama al método estático de la clase Mano
          setManosOtrosJugadores(nuevasManos); // Actualiza el estado con las nuevas manos obtenidas
      } catch (error) { 
          console.error("Error encontrando manos de otros jugadores:", error); 
          setMessage(error.message); 
          setVisible(true); 
      } 
    };

    useEffect(() => {
        if (tu !== null) { 
          fetchMano(tu.id); 
          fetchManosOtrosJugadores(); 
        } 
      }, [jugadores, tu]);

    const fetchListaDeTrucos = async (bazaId) => { 
      try {
          const listaDeTrucos = await Trucos.fetchListaDeTrucos(bazaId, jwt); // Llama al método estático de la clase Trucos
          setListaDeTrucos(listaDeTrucos); // Actualiza el estado con la lista de trucos obtenida
      } catch (error) {
          console.error("Error encontrando jugadores:", error); 
          setMessage(error.message); 
          setVisible(true); 
      }
    };

    const fetchJugadores = async () => { 
      try {
          const listaDeJugadores = await Jugadores.fetchJugadores(idPartida, jwt); // Llama al método estático de la clase
          setJugadores(listaDeJugadores); // Actualiza el estado con la lista de jugadores obtenida
      } catch (error) {
          console.error("Error encontrando jugadores:", error); 
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
          // Llama al método estático de la clase Apuesta
          await Apuesta.realizarApuesta(tu.id, ap, jwt);
  
          // Actualiza la interfaz de usuario tras una apuesta exitosa
          toggleApuestaModal();
          await fetchJugadores(); // Asegúrate de que fetchJugadores esté implementado correctamente
      } catch (error) {
          console.error("Error:", error);
          setMessage(error.message); // Muestra el mensaje de error al usuario
          setVisible(true);
      }
  };

    const fetchBazaActual = async () => {
      try {
          const bazaActualData = await Baza.fetchBazaActual(ronda.id, jwt);
  
          // Actualiza el estado con la baza actual obtenida
          setBazaActual(bazaActualData);
          console.log("bazaActual fetchBazaActual: ", bazaActualData);
      } catch (error) {
          console.error("Error encontrando baza:", error);
          setMessage(error.message);
          setVisible(true);
      }
    };
  
  useEffect(() => {
    if (BazaActual !== null)  {
    
    if(buscarUnaVezListaDeTrucos){
    fetchListaDeTrucos(BazaActual.id);
    setInterval(() => {fetchListaDeTrucos(BazaActual.id)}, 3000);
    setBuscarUnaVezListaDeTrucos(false)
    }
  }
  }, [BazaActual]);

  useEffect(() => {
    if (ronda !== null)  {
      console.log("bazaActual por listaTrucos");
      fetchBazaActual();
      // Para modal del ganador de la Baza
      if(jugadores.length === ListaDeTrucos.length && ListaDeTrucos.length > 0 && !(ejecutadoGanadorBaza === 2)) {
        setGanadorBaza(BazaActual.ganador);
        console.log("ganador Baza 1er fetchBaza: ", BazaActual.ganador);
        setGanadorBazaModal(true);
        setEjecutadoGanadorBaza(ejecutadoGanadorBaza + 1);
      }
    }
  }, [ronda,ListaDeTrucos]);


  const fetchRondaActual = async (partidaId) => {
    try {
        // Llama al método estático de la clase Ronda
        const rondaActualData = await Ronda.fetchRondaActual(partidaId, jwt);

        // Actualiza el estado con la ronda actual obtenida
        setRonda(rondaActualData);
    } catch (error) {
        console.error("Error encontrando partidas:", error);
        setMessage(error.message);
        setVisible(true);
    }
  };

    useEffect(() => {

      fetchRondaActual(idPartida);
    }, []);

    const siguienteEstado = async () => {
      try {
          // Llama al método estático de la clase Partida para avanzar al siguiente estado
          const data = await Partida.siguienteEstado(idPartida, BazaActual.id, jwt);
  
          // Actualiza los estados relevantes en la interfaz de usuario
          await fetchRondaActual(idPartida);
          await fetchBazaActual();
          await fetchMano();
          await fetchManosOtrosJugadores();
          await fetchPartida();
  
          return data;
      } catch (error) {
          console.error("Error:", error);
          setMessage(error.message); // Muestra el mensaje de error al usuario
          setVisible(true);
      }
    };

    const jugarTruco = async (cartaAJugar, tipoCarta = eleccion) => {
      try {
          // Llama al método estático de la clase Trucos para gestionar la jugada
          const cartaFinal = await Trucos.jugarTruco(
              cartaAJugar,
              tipoCarta,
              jwt,
              tu.id,
              iniciarTruco,
              fetchMano,
              siguienteEstado,
              ListaDeTrucos,
              jugadores
          );
  
          console.log("Truco jugado con éxito:", cartaFinal);
      } catch (error) {
          console.error("Error jugando truco:", error);
          setMessage(error.message); // Muestra el mensaje de error en la UI
          setVisible(true);
      }
    };

    const iniciarTruco = async (jugadorId, cartaAJugar) => {
      try {
          // Llama al método estático de la clase Trucos para gestionar el inicio del truco
          const data = await Trucos.iniciarTruco(
              jugadorId,
              cartaAJugar,
              BazaActual,
              mano,
              ListaDeTrucos,
              jwt,
              fetchBazaActual
          );
  
          setTruco(data); // Actualiza el truco en el estado
          console.log("Truco iniciado con éxito:", data);
      } catch (error) {
          console.error("Error al iniciar truco:", error);
          setMessage(error.message); // Muestra el mensaje de error en la UI
          setVisible(true);
      }
    };

    const cambiarPaloBaza = async (baza) => {
      try {
          const data = await Baza.cambiarPaloBaza(baza, jwt); // Llama al método de la clase Baza
          setBazaActual(data); // Actualiza la baza actual en el estado
          console.log("La baza con el palo dominante actualizada:", data);
      } catch (error) {
          console.error("Error al cambiar el palo dominante de la baza:", error);
          setMessage(error.message); // Muestra el mensaje de error en la UI
          setVisible(true);
      }
    };

    useEffect(() => {
      if (BazaActual !== null && BazaActual.paloBaza === "sinDeterminar" && truco !== null && truco.carta !== null)  {
        
      if(truco.carta.tipoCarta !== "banderaBlanca"){
        if(truco.carta.tipoCarta === "pirata" || truco.carta.tipoCarta === "skullKing" || truco.carta.tipoCarta === "sirena"){
          BazaActual.paloBaza = "noHayPalo";
          cambiarPaloBaza(BazaActual);
          console.log(BazaActual);
        }
          BazaActual.paloBaza = truco.carta.tipoCarta;
          cambiarPaloBaza(BazaActual);
          console.log(BazaActual);
      
      }
    
  }
    }, [truco]);

    const handleEleccion = (eleccion) => {
      setEleccion(eleccion);
      setModalTigresaOpen(false);
      console.log("hasta aqui llego");
      jugarTruco({ tipoCarta: 'tigresa' }, eleccion); // Pasar la elección al jugarTruco
      console.log("pase", nuevaTigresa);
    };

    /*

    console.log("Ronda encontrada",ronda);
    console.log("Jugadores encontrados",jugadores);
    console.log("Id de la partida",idPartida);

        */
  //  console.log("Mano encontrada",mano);
  //  console.log("Truco",truco);
  // console.log("Baza Actual",BazaActual);
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

      <div className="cartas-trucos">
        {ListaDeTrucos!==null && ListaDeTrucos.map((truco) => (
          <div key={truco.id} className="carta truco">
            <img 
              src={truco.carta.imagenFrontal} 
              alt={`Carta ${truco.carta.tipoCarta}`} 
              className="imagen-carta-trucos"
            />
            <div className="nombre-jugador"><p>{truco.jugador.usuario.username}</p></div>
          </div>
        ))}
      </div>

      <div className="cartas">
          {mano!==null && mano.cartas.map((carta) => (
            <div key={carta.id} className="carta">
              <button className='boton-agrandable'
               disabled={visualizandoCartas || (partida.turnoActual !== tu.id) || cartasDisabled.some(disabledCarta => disabledCarta.id === carta.id) }
               onClick={() => {
                if(carta.tipoCarta === 'tigresa'){
                  setModalTigresaOpen(true);
                }else{jugarTruco(carta);}

                //truco.carta=carta;
                //mano.cartas= mano.cartas.filter((cartaAEliminar) =>carta.id !== cartaAEliminar.id)
                //setTruco(truco);
                //console.log("Modificado",truco);
                //quitarCarta(mano);
              } }
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
              onCancel={toggleApuestaModal}
              onConfirm={apostar}
                    />

      <ElegirTigresaModal 
              isVisible={modalTigresaOpen} 
              onCancel={() => setModalTigresaOpen(false)} 
              onConfirm={handleEleccion}
        />
      
      <GanadorBazaModal
        isVisible={ganadorBazaModal}
        ganador={ganadorBaza}
        onClose={() => setGanadorBazaModal(false)}
      />

    </div>
  );
}
