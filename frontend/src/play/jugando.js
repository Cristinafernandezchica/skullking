import React, { useEffect, useState } from "react";
import "./JugadorInfo.css";
import { Alert } from "reactstrap";
import tokenService from "../services/token.service";
import useFetchState from '../util/useFetchState';
import getIdFromUrl from '../util/getIdFromUrl';
import ApuestaModal from '../components/modals/ApostarModal';
import ElegirTigresaModal from '../components/modals/ElegirTigresaModal';
import GanadorPartidaModal from '../components/modals/GanadorPartidaModal';
import GanadorBazaModal from '../components/modals/GanadorBazaModal';
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import ChatModal from '../components/modals/ChatModal';
import { useNavigate } from "react-router-dom";
import '../components/formGenerator/css/Temporizador.css';

const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

export default function Jugando() {
  const idPartida = getIdFromUrl(2);
  const navigate = useNavigate();
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

  // Modal Tigresa
  const [modalTigresaOpen, setModalTigresaOpen] = useState(false);
  const [eleccion, setEleccion] = useState('');
  const [nuevaTigresa, setNuevaTigresa] = useState();

  // Datos de partida
  const [tu, setTu] = useFetchState(null, `/api/v1/jugadores/${user.id}/usuario`, jwt, setMessage, setVisible);
  const [mano, setMano] = useState(null);
  const [ronda, setRonda] = useState(null);
  const [truco, setTruco] = useState(null);
  const [BazaActual, setBazaActual] = useState(null)
  const [ListaDeTrucos, setListaDeTrucos] = useState([])

  // para las cartas del resto de jugadores
  const [manosOtrosJugadores, setManosOtrosJugadores] = useState({});

  // Para lógica de apuesta
  const [apuestaModalOpen, setApuestaModalOpen] = useState(false);
  const toggleApuestaModal = () => setApuestaModalOpen(!apuestaModalOpen);
  const [visualizandoCartas, setVisualizandoCartas] = useState(true);

  // Para turno
  const [turnoAct, setTurnoAct] = useState(null);

  // Para chat
  const [chatModalVisible, setChatModalVisible] = useState(false);
  const toggleChatModal = () => setChatModalVisible(!chatModalVisible);

  // Cartas mano disabled
  const [cartasDisabled, setCartasDisabled] = useState({});

  // Mostrar ganador baza
  const [ganadorBazaModal, setGanadorBazaModal] = useState(false);
  const [ganadorBaza, setGanadorBaza] = useState("");

  // Mostrar ganador partida
  const [ganadorPartidaModal, setGanadorPartidaModal] = useState(false);
  const [ganadorPartida, setGanadorPartida] = useState([]);

  // Mostrar alerta nueva Ronda/Baza
  const [alertaRondaBaza, setAlertaRondaBaza] = useState('');
  const [alertVisible, setAlertVisible] = useState(false);

  // Para resultados Mano jugadores
  const [resultadosMano, setResultadosMano] = useState({});

  const fetchPartida = async (idPartida) => {
    try {
      const response = await fetch(`/api/v1/partidas/${idPartida}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const data = await response.json();
      console.log("comprobar partida");
      setPartida(data);
    } catch (error) {
      console.error("Error fetching partida:", error);
      setMessage(error.message);
      setVisible(true);
    }
  };

  const fetchListaTrucos = async () => {
    try {
      const response = await fetch(`/api/v1/trucos/trucosBaza/${BazaActual.id}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const data = await response.json();
      console.log("Obteniendo lista trucos:", data);
      setListaDeTrucos(data);
    } catch (error) {
      console.error("Error fetching lista trucos:", error);
      setMessage(error.message);
      setVisible(true);
    }
  };

  // Carga inicial partida
  useEffect(() => {
    fetchPartida(idPartida);
  }, []);

  const fetchManosJugadores = async () => {
    try {
      const nuevasManos = {};
      let nuevaMano = null;

      // Ejecutar todas las peticiones en paralelo
      const fetchPromises = jugadores.map(async (jugador) => {
        try {
          const response = await fetch(`/api/v1/manos/${jugador.id}`, {
            headers: {
              Authorization: `Bearer ${jwt}`,
              "Content-Type": "application/json",
            },
          });

          if (!response.ok) {
            throw new Error(`Error al obtener la mano del jugador ${jugador.id}`);
          }

          const data = await response.json();
          if (jugador.id !== tu.id) {
            nuevasManos[jugador.id] = data;
          } else {
            nuevaMano = data;
          }
        } catch (error) {
          console.error(`Error procesando el jugador ${jugador.id}:`, error);
        }
      });

      await Promise.all(fetchPromises);

      setManosOtrosJugadores(nuevasManos);
      setMano(nuevaMano);
    } catch (error) {
      console.error("Error general al obtener las manos:", error);
      setMessage(error.message);
      setVisible(true);
    }
  };

  // Carga inicial manos
  useEffect(() => {
    if (tu !== null) {
      fetchManosJugadores();
    }
  }, [jugadores, tu]);

  useEffect(() => {
    // Segundo useEffect: Conexión al WebSocket
    const socket = new SockJS("http://localhost:8080/ws");
    const stompClient = Stomp.over(() => socket);

    stompClient.connect({}, (frame) => {
      console.log("Connected: " + frame);

      // Para trucos
      stompClient.subscribe(
        `/topic/baza/truco/partida/${idPartida}`,
        (messageOutput) => {
          const data = JSON.parse(messageOutput.body);
          console.log("Mensaje recibido: ", data); // Verifica que el mensaje se reciba correctamente

          setListaDeTrucos(data); // Actualizamos la lista de trucos con los datos recibidos
        }
      );

      // Para ronda
      stompClient.subscribe(
        `/topic/nuevaRonda/partida/${idPartida}`,
        (messageOutput) => {
          const data = JSON.parse(messageOutput.body);
          console.log("Nueva ronda: ", data);

          const timerNuevaRonda = setTimeout(() => {
            setRonda(data);
          }, 5000);

          return () => clearTimeout(timerNuevaRonda);
        }
      );

      // Para baza
      stompClient.subscribe(
        `/topic/nuevaBaza/partida/${idPartida}`,
        (messageOutput) => {
          const data = JSON.parse(messageOutput.body);
          console.log("Nueva baza: ", data);

          const timerNuevaBaza = setTimeout(() => {
            setBazaActual(data); // Actualizamos la baza actual con los datos recibidos
            console.log("vaciar lista trucos");
            setListaDeTrucos([]); // Vaciamos la lista de trucos
            console.log("lista trucos vacia: ", ListaDeTrucos);
            setCartasDisabled({});

          }, 5000);

          return () => clearTimeout(timerNuevaBaza);
        }
      );

      // Para ganador Baza
      stompClient.subscribe(
        `/topic/ganadorBaza/partida/${idPartida}`,
        (messageOutput) => {
          const data = JSON.parse(messageOutput.body);
          console.log("Ganador baza: ", data);

          setGanadorBaza(data); // Actualizamos el ganador de la baza
          setGanadorBazaModal(true); // Abrimos el modal de visualización del ganador
        }
      );

      // Para turno
      stompClient.subscribe(
        `/topic/turnoActual/${idPartida}`,
        (messageOutput) => {
          const data = JSON.parse(messageOutput.body);
          console.log("Nuevo turno: ", data);

          setTurnoAct(data); // Actualizamos el turno actual con los datos recibidos
        }
      );

      // Para manos
      stompClient.subscribe(
        `/topic/nuevasManos/partida/${idPartida}`,
        (messageOutput) => {
          const data = JSON.parse(messageOutput.body);
          console.log("Nuevas manos: ", data);
          const nuevasManosOtros = {};
          for (const d of data) {
            if (d.jugador.id === tu.id) {
              console.log("mi mano con if: ", d);
              setMano(d);
            } else {
              nuevasManosOtros[d.jugador.id] = d;
            }
            setManosOtrosJugadores(nuevasManosOtros);
            console.log("manos otros jugadores actualizadas: ", nuevasManosOtros);
          }
        }
      );

      // Para finalizar partida
      stompClient.subscribe(`/topic/partida/${idPartida}`, (messageOutput) => {
        const data = JSON.parse(messageOutput.body);

        if (data.status === "FINALIZADA") {
          setGanadorPartida(data.ganadores)
          setGanadorPartidaModal(true);
        }
      });

      stompClient.subscribe(`/topic/cartasDisabled/partida/${idPartida}`, (messageOutput) => {
        const data = JSON.parse(messageOutput.body);

        setCartasDisabled(data);
        console.log("Cartas Disabled: ", data);
      });

      stompClient.subscribe(`/topic/resultadosMano/partida/${idPartida}`, (messageOutput) => {
        const data = JSON.parse(messageOutput.body);

        setResultadosMano(data);
        console.log("Resultados Mano: ", data);
      });

      stompClient.subscribe(`/topic/apuesta/partida/${idPartida}`, (messageOutput) => {
        const data = JSON.parse(messageOutput.body);

        setJugadores(data);
        console.log("Jugadores apuestas: ", data);
      });

    });

    // Cleanup: Desconectar el WebSocket cuando el componente se desmonte
    return () => {
      stompClient.disconnect(() => {
        console.log("Disconnected");
      });
    };
  }, [tu]); // Solo se ejecuta una vez

  const fetchJugadores = async () => {
    try {
      const response = await fetch(`/api/v1/jugadores/${idPartida}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
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
  }, [ronda, tu]);

  // Para actualizar la visualización de la apuesta en todos los jugadores
  useEffect(() => {
    const timerCerrarApuestas = setTimeout(() => {
      setVisualizandoCartas(false);
      fetchJugadores();
    }, 25000); // Hay que cambiarlo a 60000 (60 segundos entre ver cartas y apostar)

    return () => clearTimeout(timerCerrarApuestas);
  }, [ronda]);

  useEffect(() => {
    if (ronda && BazaActual) {
      fetchListaTrucos();
      setGanadorBazaModal(false);
      const nuevaAlerta = `Ronda: ${ronda.numRonda}  ||  Baza: ${BazaActual.numBaza}`;
      setAlertaRondaBaza(nuevaAlerta);
      setAlertVisible(true); // Muestra la alerta

      // Ocultar la alerta después de 3 segundos
      const timer = setTimeout(() => {
        setAlertVisible(false);
      }, 3000);

      return () => clearTimeout(timer); // Limpia el temporizador
    }
  }, [ronda, BazaActual]);



  const apostar = async (ap) => {
    try {
      const response = await fetch(
        `/api/v1/partidas/apuesta/${tu.id}?apuesta=${ap}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwt}`,
          },
        }
      );
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Error desconocido");
      }

      console.log("Apuesta realizada con éxito");
      toggleApuestaModal();
    } catch (error) {
      console.error("Error:", error);
      throw error;
    }
  };

  const fetchBazaActual = async () => {
    try {
      const response = await fetch(`/api/v1/bazas/${ronda.id}/bazaActual`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const data = await response.json();
      setBazaActual(data);
      // setIdBaza(data.id);
      console.log("bazaActual fetchBazaActual: ", data);
    } catch (error) {
      console.error("Error encontrando baza:", error);
      setMessage(error.message);
      setVisible(true);
    }
  };

  // Carga inicial baza
  useEffect(() => {
    if (ronda !== null) {
      fetchBazaActual();
    }
  }, [tu]);

  const fetchRondaActual = async (partidaId) => {
    try {
      const response = await fetch(`/api/v1/rondas/${partidaId}/partida`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
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

  // Carga inicial ronda
  useEffect(() => {
    fetchRondaActual(idPartida);
  }, []);

  const jugarTruco = async (cartaAJugar, tipoCarta = eleccion) => {
    let cartaFinal = cartaAJugar;
    if (cartaAJugar.tipoCarta == "tigresa" && tipoCarta) {
      try {
        const response = await fetch(`/api/v1/cartas/tigresa/${tipoCarta}`, {
          headers: {
            Authorization: `Bearer ${jwt}`,
            "Content-Type": "application/json",
          },
        });
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        const data = await response.json();
        console.log("Se esta jugando tigresa", data);
        setNuevaTigresa(data);
        cartaFinal = data;
      } catch (error) {
        console.error("Error fetching cambioTigresa:", error);
        setMessage(error.message);
        setVisible(true);
      }
    }
    console.log("Carta a jugar:", cartaFinal);
    await iniciarTruco(tu.id, cartaFinal);
    console.log("Truco a jugar:", cartaFinal);
  };

  const iniciarTruco = async (jugadorId, cartaAJugar) => {
    const BazaCartaManoDTO = {};
    BazaCartaManoDTO.baza = BazaActual;
    BazaCartaManoDTO.mano = mano;
    BazaCartaManoDTO.carta = cartaAJugar;
    BazaCartaManoDTO.turno = ListaDeTrucos.length + 1;
    try {
      const response = await fetch(`/api/v1/trucos/${jugadorId}/jugar`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(BazaCartaManoDTO),
      });

      if (!response.ok) {
        console.log("Fallo al crear el truco");
        throw new Error("Network response was not ok");
      }

      const data = await response.json();
      setTruco(data);
      console.log("Dime que se creo el truco", data);

      return data;
    } catch (error) {
      console.error("Error:", error);
    }
  };


  const handleEleccion = (eleccion) => {
    setEleccion(eleccion);
    setModalTigresaOpen(false);
    console.log("hasta aqui llego");
    jugarTruco({ tipoCarta: "tigresa" }, eleccion); // Pasar la elección al jugarTruco
    console.log("pase", nuevaTigresa);
  };

  return (
    <>
      <div className="validation-errors">
        <Alert color="primary" className="slide-down-alert" isOpen={alertVisible}>
          {alertaRondaBaza}
        </Alert>
      </div>
      <div className="tablero">
        <div className="lista-jugadores">
          {jugadores !== null &&
            jugadores.map((jugador) => (
              <div key={jugador.id} className="jugador-info">
                <div style={{ display: "flex", alignItems: "center" }}>
                  <img src={jugador.usuario.imagenPerfil} alt="Perfil" style={{ width: "30px", height: "30px", borderRadius: "50%", marginRight: "10px" }} />
                  <h3>{jugador.usuario.username}</h3>
                </div>
                <p>Apuesta: {jugador.apuestaActual}</p>
                <p>Puntos: {jugador.puntos}</p>
                {jugador !== null && <p>Bazas ganadas: {resultadosMano[jugador.id]}</p>}
              </div>
            ))}
        </div>

        <div className="cartas-otros-jugadores">
          {Object.keys(manosOtrosJugadores).map((jugadorId) => (
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
          {ListaDeTrucos !== null &&
            ListaDeTrucos.map((truco) => (
              <div key={truco.id} className="carta truco">
                <img
                  src={truco.carta.imagenFrontal}
                  alt={`Carta ${truco.carta.tipoCarta}`}
                  className="imagen-carta-trucos"
                />
                <div className="nombre-jugador">
                  <p>{truco.jugador.usuario.username}</p>
                </div>
              </div>
            ))}
        </div>

        <div className="cartas">
          {mano !== null &&
            mano.cartas.map((carta) => (
              <div key={carta.id} className="carta">
                <button
                  className="boton-agrandable"
                  disabled={
                    visualizandoCartas ||
                    turnoAct !== tu.id ||
                    (cartasDisabled[mano.id]?.some((disabledCarta) => disabledCarta.id === carta.id) ?? false)
                  }
                  onClick={() => {
                    if (carta.tipoCarta === "tigresa") {
                      setModalTigresaOpen(true);
                    } else {
                      jugarTruco(carta);
                    }
                  }

                    //truco.carta=carta;
                    //mano.cartas= mano.cartas.filter((cartaAEliminar) =>carta.id !== cartaAEliminar.id)
                    //setTruco(truco);
                    //console.log("Modificado",truco);
                    //quitarCarta(mano);
                  }
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

        <button
          className="boton-flotante-chat"
          onClick={() => setChatModalVisible(true)}
        >
          💬
        </button>

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

        <GanadorPartidaModal
          isVisible={ganadorPartidaModal}
          ganador={ganadorPartida}
          onClose={() => { setGanadorPartidaModal(false); navigate('/play') }}
        />

        <ChatModal
          isVisible={chatModalVisible}
          onCancel={toggleChatModal}
          partida={idPartida}
          jugadorQueEscribe={tu}
        />

      </div>
    </>
  );
}
