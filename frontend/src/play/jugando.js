import React, { useEffect, useState } from 'react';
import './JugadorInfo.css';
import tokenService from "../services/token.service";
import useFetchState from '../util/useFetchState';
import getIdFromUrl from '../util/getIdFromUrl';
import ApuestaModal from '../components/modals/ApostarModal';
// import manito from  'frontend/src/static/images/cartas/morada_1.png'


//hola 
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
    const [BazaActual, setBazaActual] = useState(null)
    const [ListaDeTrcuos, setListaDeTrcuos] = useState([])
    const [seCambiaPalo, setSeCambiaPalo] = useState(true)
    const [buscarUnaVezListaDeTrcuos, setBuscarUnaVezListaDeTrucos] =useState(true)

    // para las cartas del resto de jugadores
    const [manosOtrosJugadores, setManosOtrosJugadores] = useState({});
    // Para lógica de apuesta
    
    const [apuestaModalOpen, setApuestaModalOpen] = useState(false);
    const toggleApuestaModal = () => setApuestaModalOpen(!apuestaModalOpen);
    const [visualizandoCartas, setVisualizandoCartas] = useState(true)


    // manejo turno
    const [turnoActual, setTurnoActual] = useState(null);


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
          console.log("Nueva mano", mano)
          // Fetch jugadores for each partida
      } catch (error) {
          console.error("Error encontrando partidas:", error);
          setMessage(error.message);
          setVisible(true);
      }
  };
    useEffect(() => {

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
          console.error("Error encontrando turno actual:", error);
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
          console.error("Error encontrando manos de otros jugadores:", error); 
          setMessage(error.message); setVisible(true); 
        } 
      }; 
        if (tu !== null) { 
          fetchMano(tu.id); 
          fetchManosOtrosJugadores(); 
        } 
      }, [jugadores, tu]);

      const fetchListaDeTrucos = async (bazaId) => {
        try {
            const response = await fetch(`/api/v1/bazas/${bazaId}/trucos`, {
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    'Content-Type': 'application/json'
                }
            });
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            const data = await response.json();
            console.log("Este es la lista de trucos",data)
            setListaDeTrcuos(data);
        } catch (error) {
            console.error("Error encontrando jugadores:", error);
            setMessage(error.message);
            setVisible(true);
        }
      };


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
          const response = await fetch(`/api/v1/manos/apuesta/${tu.id}?apuesta=${ap}`, {
              method: 'PUT',
              headers: {
                  'Content-Type': 'application/json',
                  'Authorization': `Bearer ${jwt}`,
              }
          });

          if (!response.ok) {
              const errorData = await response.json();
              throw new Error(errorData.message || 'Error desconocido');
          }

        //  console.log("Apuesta realizada con éxito");
          toggleApuestaModal();
          fetchJugadores();

      } catch (error) {
          console.error('Error:', error);
          throw error;
      }
    };


    const fetchBazaActual = async () => {
      try {
          const response = await fetch(`/api/v1/bazas/${ronda.id}/bazaActual`, {
              headers: {
                  "Authorization": `Bearer ${jwt}`,
                  'Content-Type': 'application/json'
              }
          });
          if (!response.ok) {
              throw new Error("Network response was not ok");
          }
          const data = await response.json();
          setBazaActual(data);
       //   console.log("baza??",data);
      } catch (error) {
          console.error("Error encontrando partidas:", error);
          setMessage(error.message);
          setVisible(true);
      }
  };
  

  useEffect(() => {
    if (BazaActual !== null)  {
    
    if(buscarUnaVezListaDeTrcuos){
    fetchListaDeTrucos(BazaActual.id);
    setInterval(() => {fetchListaDeTrucos(BazaActual.id)}, 3000);
    setBuscarUnaVezListaDeTrucos(false)
    }
  }
  }, [BazaActual]);

  useEffect(() => {
    if (ronda !== null)  {
    fetchBazaActual();
  }
  }, [ronda,ListaDeTrcuos]);


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
          console.log("Algo falla")
          throw new Error('Network response was not ok');
        }

        const data = await response.json();
        setTurnoActual(data.turnoActual);

      } catch (error) {
        console.error('Error:', error);
      }
    };
    */


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
              console.error("Error encontrando partidas:", error);
              setMessage(error.message);
              setVisible(true);
          }
      };
      fetchRondaActual(idPartida);
    }, []);


    const jugarTruco = async (cartaAJugar) => {
      await iniciarTruco(tu.id,cartaAJugar);
      await fetchMano(tu.id);
    };

    const iniciarTruco = async (jugadorId,cartaAJugar) => {
      const BazaCartaManoDTO= {}
      BazaCartaManoDTO.baza = BazaActual
      BazaCartaManoDTO.mano = mano
      BazaCartaManoDTO.carta = cartaAJugar
      BazaCartaManoDTO.turno = ListaDeTrcuos.length +1
      try {
        const response = await fetch(`/api/v1/trucos/${jugadorId}/jugar`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${jwt}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(BazaCartaManoDTO),
        });

        if (!response.ok) {
          console.log("Algo falla")
          throw new Error('Network response was not ok');
        }

        const data = await response.json();
        setTruco(data);
        console.log("Dime que se creo el truco",data);
      } catch (error) {
        console.error('Error:', error);
      }
    };

    const cambiarPaloBaza = async (baza) => {
      try {
        const response = await fetch(`/api/v1/bazas/${baza.id}`, {
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${jwt}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(baza),
        });

        if (!response.ok) {
          console.log("Algo falla")
          throw new Error('Network response was not ok');
        }

        const data = await response.json();
        setBazaActual(data);
        console.log("La baza con el palo dominante",data);
      } catch (error) {
        console.error('Error:', error);
      }
    };



    useEffect(() => {
      if (BazaActual !== null && BazaActual.tipoCarta === "sinDeterminar" && truco !== null && truco.carta !== null)  {
        
        if(truco.carta.tipoCarta !== "banderaBlanca"){
        BazaActual.tipoCarta = truco.carta.tipoCarta;
      cambiarPaloBaza(BazaActual);
      console.log(BazaActual);}
    
  }
    }, [truco]);



    /*

    console.log("Ronda encontrada",ronda);
    console.log("Jugadores encontrados",jugadores);
    console.log("Id de la partida",idPartida);

        */
  //  console.log("Mano encontrada",mano);
  //  console.log("Truco",truco);
  console.log("Baza Actual",BazaActual);

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
                <button className='boton-agrandable'
                 disabled={visualizandoCartas}
                 onClick={() => {
                  jugarTruco(carta);
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
      </div>
    );
}
