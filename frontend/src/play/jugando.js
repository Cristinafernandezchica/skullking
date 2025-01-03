import React, { useEffect, useState } from "react";
import "./JugadorInfo.css";
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from "reactstrap";
import tokenService from "../services/token.service";
import useFetchState from '../util/useFetchState';
import getIdFromUrl from '../util/getIdFromUrl';
import ApuestaModal from '../components/modals/ApostarModal';
import ElegirTigresaModal from '../components/modals/ElegirTigresaModal';
import GanadorBazaModal from '../components/modals/GanadorBazaModal';
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import ChatModal from '../components/modals/ChatModal';

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
  const [tu, setTu] = useFetchState(null, `/api/v1/jugadores/${user.id}/usuario`, jwt, setMessage, setVisible);
  const [mano, setMano] = useState(null);
  const [ronda, setRonda] = useState(null);
  const [truco, setTruco] = useState(null);
  const [BazaActual, setBazaActual] = useState(null)
  const [ListaDeTrucos, setListaDeTrucos] = useState([])
  const [seCambiaPalo, setSeCambiaPalo] = useState(true)
  const [buscarUnaVezListaDeTrucos, setBuscarUnaVezListaDeTrucos] = useState(true)
  const [idBaza, setIdBaza] = useState(null);

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

  // Mano disabled
  const [cartasDisabled, setCartasDisabled] = useState([]);

  // Mostrar ganador baza
  const [ganadorBazaModal, setGanadorBazaModal] = useState(false);
  const [ganadorBaza, setGanadorBaza] = useState("");
  const [ejecutadoGanadorBaza, setEjecutadoGanadorBaza] = useState(0);

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

  //preguntar que hace
  useEffect(() => {
    fetchPartida(idPartida);
  }, []);
  /*
  useEffect(() => {
    const intervalo = setInterval(() => {
      fetchPartida(idPartida);
      // fetchBazaActual();
    }, 5000); // Cada 5 segundos
    return () => clearInterval(intervalo);
  }, [idPartida, tu]);
  */

  const fetchMano = async (jugadorId) => {
    try {
      const response = await fetch(`/api/v1/manos/${jugadorId}`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const data = await response.json();
      setMano(data);
      console.log("Nueva mano", mano);
      // Fetch jugadores for each partida
      await fetchCartasDisabled(data.id, BazaActual.paloBaza);
    } catch (error) {
      console.error("Error encontrando mano:", error);
      setMessage(error.message);
      setVisible(true);
    }
  };

  const fetchCartasDisabled = async (idMano, paloActual) => {
    try {
      const response = await fetch(
        `/api/v1/manos/${idMano}/manoDisabled?tipoCarta=${paloActual}`,
        {
          headers: {
            Authorization: `Bearer ${jwt}`,
            "Content-Type": "application/json",
          },
        }
      );
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }

      const responseData = await response.text();
      console.log("Response text:", responseData);

      const data = JSON.parse(responseData);
      setCartasDisabled(data);
      console.log("Cartas disabled:", data);
    } catch (error) {
      console.error("Error encontrando partidas:", error);
      setMessage(error.message);
      setVisible(true);
    }
  };

  const fetchManosJugadores = async () => {
    try {
      const nuevasManos = {};
      let nuevaMano = null;
      for (const jugador of jugadores) {
        const response = await fetch(`/api/v1/manos/${jugador.id}`, {
          headers: {
            Authorization: `Bearer ${jwt}`,
            "Content-Type": "application/json",
          },
        });
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }

        if (jugador.id !== tu.id) {
          const data = await response.json();
          nuevasManos[jugador.id] = data;
        }else{
          nuevaMano = await response.json();
          await fetchCartasDisabled(nuevaMano.id, BazaActual.paloBaza);
        }
      }
      setManosOtrosJugadores(nuevasManos);
      setMano(nuevaMano);
    } catch (error) {
      console.error("Error encontrando manos de otros jugadores:", error);
      setMessage(error.message);
      setVisible(true);
    }
  };

  useEffect(() => {
    if (tu !== null) {
      //fetchMano(tu.id);
      fetchManosJugadores();
    }
  }, [jugadores, tu]);

  const fetchListaDeTrucos = async (bazaId) => {
    try {
      const response = await fetch(`/api/v1/bazas/${bazaId}/trucos`, {
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
      });
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      const data = await response.json();
      console.log("Este es la lista de trucos", data);
      setListaDeTrucos(data);
    } catch (error) {
      console.error("Error encontrando jugadores:", error);
      setMessage(error.message);
      setVisible(true);
    }
  };

  useEffect(() => {
    // Segundo useEffect: Conexión al WebSocket
    if (true) { //BazaActual !== null
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

            setRonda(data); // Actualizamos la ronda actual con los datos recibidos
          }
        );

        // Para baza
        stompClient.subscribe(
          `/topic/nuevaBaza/partida/${idPartida}`,
          (messageOutput) => {
            const data = JSON.parse(messageOutput.body);
            console.log("Nueva baza: ", data);

            setBazaActual(data); // Actualizamos la baza actual con los datos recibidos
          }
        );

        // Para renovar lista trucos al cambiar de baza
        stompClient.subscribe(
          `/topic/listaTrucos/partida/${idPartida}`,
          (messageOutput) => {
            const data = JSON.parse(messageOutput.body);
            console.log("Lista trucos vacía: ", data);

            setListaDeTrucos(data); // Actualizacion lista trucos
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
            for (const d of data) {
              if (d.jugador.id === tu.id) {
                console.log("mi mano: ", d)
                setMano(d);
              } else if (d.jugador.id !== tu.id) {
                let nuevasManosOtros = [];
                nuevasManosOtros.push(d);
                setManosOtrosJugadores(nuevasManosOtros);
                console.log("manos otros jugadores: ", manosOtrosJugadores);
                // setManosOtrosJugadores({ ...manosOtrosJugadores, [d.jugador.id]: d });
                // console.log("manos otros jugadores: ", manosOtrosJugadores);
              }
            }
          }
        );

        // Para apuestas
        stompClient.subscribe(
          `/topic/apuesta/partida/${idPartida}`,
          (messageOutput) => {
            const data = JSON.parse(messageOutput.body);
            console.log("Nueva apuesta: ", data);

            setJugadores(data); // Actualizamos el turno actual con los datos recibidos
          }
        );
      });

      // Cleanup: Desconectar el WebSocket cuando el componente se desmonte
      return () => {
        stompClient.disconnect(() => {
          console.log("Disconnected");
        });
      };
    }
  }, [tu]); // Solo se ejecuta una vez
  /*
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
    */

  // Para abrir el modal de apuesta
  useEffect(() => {
    const timerAbrirApuestas = setTimeout(() => {
      setApuestaModalOpen(true);
    }, 5000); // Cambiar a 30 (30000)

    return () => clearTimeout(timerAbrirApuestas);
  }, [ronda]);

  // Para actualizar la visualización de la apuesta en todos los jugadores
  useEffect(() => {
    const timerCerrarApuestas = setTimeout(() => {
      setVisualizandoCartas(false);
      // fetchJugadores();
    }, 16000); // Hay que cambiarlo a 60000 (60 segundos entre ver cartas y apostar)

    return () => clearTimeout(timerCerrarApuestas);
  }, [ronda]);

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

      //  console.log("Apuesta realizada con éxito");
      toggleApuestaModal();
      // fetchJugadores();
      setTurnoAct(partida.turnoActual);
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
      setIdBaza(data.id);
      console.log("bazaActual fetchBazaActual: ", data);
    } catch (error) {
      console.error("Error encontrando baza:", error);
      setMessage(error.message);
      setVisible(true);
    }
  };

  useEffect(() => {
    if (ronda !== null) {
      console.log("bazaActual por listaTrucos");
      fetchBazaActual();
      // Para modal del ganador de la Baza
      if (
        jugadores.length === ListaDeTrucos.length &&
        ListaDeTrucos.length > 0 &&
        !(ejecutadoGanadorBaza === 2)
      ) {
        setGanadorBaza(BazaActual.ganador);
        console.log("ganador Baza 1er fetchBaza: ", BazaActual.ganador);
        setGanadorBazaModal(true);
        setEjecutadoGanadorBaza(ejecutadoGanadorBaza + 1);
      }
    }
  }, [ronda, ListaDeTrucos]);

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

  const siguienteEstado = async () => {
    try {
      const response = await fetch(
        `/api/v1/partidas/${idPartida}/bazas/${BazaActual.id}/siguiente-estado`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${jwt}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify(),
        }
      );

      if (!response.ok) {
        console.log("Fallo al cambiar de estado");
        throw new Error("Network response was not ok");
      }

      const data = await response.json();
      console.log("Se cambió de estado", data);

      await fetchRondaActual(idPartida);
      await fetchBazaActual();
      await fetchMano();
      await fetchManosOtrosJugadores();
      await fetchPartida();

      return data;
    } catch (error) {
      console.error("Error:", error);
    }
  };

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
    await fetchMano(tu.id);

    if (ListaDeTrucos.length + 1 === jugadores.length) {
      await siguienteEstado();
      await fetchBazaActual();
    }
      */
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
        console.log("Algo falla");
        throw new Error("Network response was not ok");
      }

      const data = await response.json();
      setTruco(data);
      console.log("Dime que se creo el truco", data);
      // Para mostrar el ganador de la baza, SIN ESTO NO FUNCIONA NO QUITAR
      await fetchBazaActual();
      if (BazaActual !== null) await fetchListaDeTrucos(BazaActual.id);

      return data;
    } catch (error) {
      console.error("Error:", error);
    }
  };

  const cambiarPaloBaza = async (baza) => {
    try {
      const response = await fetch(`/api/v1/bazas/${baza.id}`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${jwt}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(baza),
      });

      if (!response.ok) {
        console.log("Algo falla");
        throw new Error("Network response was not ok");
      }

      const data = await response.json();
      setBazaActual(data);
      console.log("La baza con el palo dominante", data);
    } catch (error) {
      console.error("Error:", error);
    }
  };

  useEffect(() => {
    if (
      BazaActual !== null &&
      BazaActual.paloBaza === "sinDeterminar" &&
      truco !== null &&
      truco.carta !== null
    ) {
      if (truco.carta.tipoCarta !== "banderaBlanca") {
        if (
          truco.carta.tipoCarta === "pirata" ||
          truco.carta.tipoCarta === "skullKing" ||
          truco.carta.tipoCarta === "sirena"
        ) {
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
    jugarTruco({ tipoCarta: "tigresa" }, eleccion); // Pasar la elección al jugarTruco
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
                  turnoAct !== tu.id /*||
                  cartasDisabled.some(
                    (disabledCarta) => disabledCarta.id === carta.id
                  )*/
                }
                onClick={() => {
                  if (carta.tipoCarta === "tigresa") {
                    setModalTigresaOpen(true);
                  } else {
                    jugarTruco(carta);
                  }

                  //truco.carta=carta;
                  //mano.cartas= mano.cartas.filter((cartaAEliminar) =>carta.id !== cartaAEliminar.id)
                  //setTruco(truco);
                  //console.log("Modificado",truco);
                  //quitarCarta(mano);
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

      <ChatModal
        isVisible={chatModalVisible}
        onCancel={toggleChatModal}
        partida={idPartida}
        jugadorQueEscribe={tu}
      />

    </div>
  );
}
