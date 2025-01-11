import React, { useEffect, useState } from "react";
import { Button, Spinner, Alert } from "reactstrap";
import { useNavigate } from "react-router-dom";
import tokenService from "../services/token.service";
import "./Perfil.css";
import { aceptarORechazarSolicitud, fetchListaDeAmigos, invitarAPartida } from "../components/appNavBarModular/AppNavBarModular";


export default function Perfil() {
    const jwt = tokenService.getLocalAccessToken();
    const [user, setUser] = useState(null);
    const [players, setPlayers] = useState([]);
    const [lastPlayer, setLastPlayer] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [hasRestrictedPlayer, setHasRestrictedPlayer] = useState(false);
    const usuarioActual = tokenService.getUser();
    const [errors, setErrors] = useState([]);
    const [inputValue, setInputValue] = useState("");
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [successMessage, setSuccessMessage] = useState(null);
    const [listaDeAmigos, setListaDeAmigos] = useState([]);
    const navigate = useNavigate();

    const showError = (error) => { 
        setErrors([error]); 
        setTimeout(() => { 
          setErrors([]); 
        }, 5000); // La alerta desaparece después de 5000 milisegundos (5 segundos) 
      };

    const showSuccess = (success) => {
        setSuccessMessage(success);
        setTimeout(() => {
            setSuccessMessage(null);
        }, 5000); // La alerta desaparece después de 5000 milisegundos (5 segundos)
    };

    const handleEliminarAmigo = (amigoId) => {
        if (window.confirm("¿Estás seguro de que quieres eliminar a este amigo?")) {
            aceptarORechazarSolicitud(usuarioActual.id,amigoId,false,jwt);
            fetchListaDeAmigos(usuarioActual,setListaDeAmigos,jwt);
            // Aquí implementas la lógica para eliminar el amigo
        }
    };

    async function enviarSolicitud() {
        try {
            const response = await fetch(`/api/v1/amistades/${usuarioActual.id}/${inputValue}`, {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${jwt}`,
                },
                method: "POST"
            });

            if (response.ok) {
                const data = await response.json();
                console.log("la solicitud fue", data);
                showSuccess("Solicitud de amistad enviada.");
            } else {
                const errorData = await response.json();
                showError(errorData.message || "Error al realizar la solicitud.");
            }
        } catch (error) {
            console.error("Error al conectar con el servidor:", error);
            showError("Error al conectar con el servidor.");
        }
    }

    const handleEnviarSolicitud = () => {
        enviarSolicitud();
        setInputValue("");
    };


    useEffect(() => {
        fetchListaDeAmigos(usuarioActual.id,setListaDeAmigos,jwt);
    },[]);

    useEffect(() => {
        async function fetchUserAndPlayers() {
            try {
                // Obtener usuario actual
                const userResponse = await fetch("/api/v1/users/current", {
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${jwt}`,
                    },
                });

                if (userResponse.ok) {
                    const userData = await userResponse.json();
                    setUser(userData);

                    // Obtener jugadores asociados al usuario
                    const playersResponse = await fetch(`/api/v1/jugadores/${userData.id}/usuarios`, {
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: `Bearer ${jwt}`,
                        },
                    });

                    if (playersResponse.ok) {
                        const playerData = await playersResponse.json();
                        setPlayers(playerData);

                        // Verificar si hay jugadores con estado "ESPERANDO" o "JUGANDO"
                        const restricted = playerData.some(player =>
                            ["ESPERANDO", "JUGANDO"].includes(player.partida.estado.trim().toUpperCase())
                        );
                        setHasRestrictedPlayer(restricted);
                    }

                    // Obtener último jugador asociado al usuario
                    const lastPlayerResponse = await fetch(`/api/v1/jugadores/${userData.id}/usuario`, {
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: `Bearer ${jwt}`,
                        },
                    });

                    if (lastPlayerResponse.ok) {
                        const lastPlayerData = await lastPlayerResponse.json();
                        setLastPlayer(lastPlayerData);
                    }
                }
            } catch (error) {
                console.error("Error al conectar con el servidor:", error);
            } finally {
                setIsLoading(false);
            }
        }
        fetchUserAndPlayers();
    }, [jwt]);

    const eliminarCuenta = async () => {
        if (window.confirm("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción es irreversible.")) {
            try {
                // Eliminar jugadores asociados
                const jugadoresResponse = await fetch(`/api/v1/jugadores/${user.id}/usuarios`, {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                });

                if (jugadoresResponse.ok) {
                    const jugadores = await jugadoresResponse.json();

                    for (const jugador of jugadores) {
                        const partidaEstado = jugador.partida.estado;

                        if (["JUGANDO", "ESPERANDO"].includes(partidaEstado)) {
                            alert(`No se puede eliminar la cuenta porque el jugador está en una partida en estado "${partidaEstado}".`);
                            return;
                        }

                        // Eliminar el jugador
                        await fetch(`/api/v1/jugadores/${jugador.id}`, {
                            method: "DELETE",
                            headers: {
                                Authorization: `Bearer ${jwt}`,
                            },
                        });
                    }
                }

                // Eliminar partidas asociadas al usuario
                const partidasResponse = await fetch(`/api/v1/partidas?ownerId=${user.id}`, {
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                });

                if (partidasResponse.ok) {
                    const partidas = await partidasResponse.json();

                    for (const partida of partidas) {
                        console.log(`Eliminando jugadores de la partida ${partida.id}`);
                        const partidaJugadoresResponse = await fetch(`/api/v1/jugadores/${partida.id}`, {
                            headers: {
                                Authorization: `Bearer ${jwt}`,
                            },
                        });

                        if (partidaJugadoresResponse.ok) {
                            const partidaJugadores = await partidaJugadoresResponse.json();
                            for (const jugador of partidaJugadores) {
                                await fetch(`/api/v1/jugadores/${jugador.id}`, {
                                    method: "DELETE",
                                    headers: {
                                        Authorization: `Bearer ${jwt}`,
                                    },
                                });
                            }
                        }

                        console.log(`Eliminando partida ${partida.id}`);
                        await fetch(`/api/v1/partidas/${partida.id}`, {
                            method: "DELETE",
                            headers: {
                                Authorization: `Bearer ${jwt}`,
                            },
                        });
                    }
                }

                // Eliminar el usuario directamente
                await eliminarUsuarioDirectamente(user.id);
            } catch (error) {
                console.error("Error al eliminar la cuenta:", error);
                alert("Error al eliminar la cuenta.");
            }
        }
    };
    
    async function eliminarUsuarioDirectamente(userId) {
        try {
            const response = await fetch(`/api/v1/users/${userId}`, {
                method: "DELETE",
                headers: {
                    Authorization: `Bearer ${jwt}`,
                },
            });

            if (!response.ok) {
                throw new Error("Error al eliminar el usuario.");
            }

            alert("Cuenta eliminada con éxito.");
            tokenService.removeUser();
            navigate("/");
            window.location.reload();
        } catch (error) {
            console.error("Error al eliminar el usuario:", error);
            alert("Error al eliminar el usuario.");
        }
    }
    

    if (isLoading) {
        return (
            <div className="loading-container">
                <Spinner color="primary" />
                <p>Cargando...</p>
            </div>
        );
    }

    if (!user) {
        return <p>No se encontraron datos del usuario.</p>;
    }

    const handleVolverPartida = () => {
        if (lastPlayer && ( lastPlayer.partida.estado === "JUGANDO" || lastPlayer.partida.estado === "ESPERANDO")) {
          navigate(lastPlayer.partida.estado === "ESPERANDO" ? `/salaEspera/${lastPlayer.partida.id}` : `/tablero/${lastPlayer.partida.id}`)
        } else {
          alert("No estás jugando ninguna partida.");
        }
      };

    return (
        <div className="perfil-container">
            <h2 className="perfil-title">Mi Perfil</h2>

            <div className="validation-messages">
                {errors.length > 0 && errors.map((error, index) => (
                    <Alert key={index} color="danger">{error}</Alert>
                ))}
                {successMessage && (
                    <Alert color="success">{successMessage}</Alert>
                )}
            </div>

            <div className="perfil-content">
                <img
                    className="perfil-image"
                    src={user.imagenPerfil || "https://blog.tienda-medieval.com/wp-content/uploads/2019/02/Parche-pirata-ojo-derecho.jpg"}
                    alt="Imagen de perfil"
                />
                <h3 className="perfil-username">{user.username}</h3>
                <p className="perfil-description">{user.descripcionPerfil}</p>
            </div>

            {lastPlayer && (
                <div className="player-status-message">
                    {lastPlayer.partida.estado === "ESPERANDO" && (
                        <p>Se encuentra en una partida en espera.</p>
                    )}
                    {lastPlayer.partida.estado === "JUGANDO" && (
                        <p>Se encuentra en una partida en curso.</p>
                    )}
                </div>
            )}

            <div className="perfil-actions" style={{ marginTop: "20px", marginBottom:"20px" }}>
                {!hasRestrictedPlayer && (
                    <>
                        <Button
                            className="perfil-button edit-button"
                            onClick={() => navigate("/editarPerfil")}
                        >
                            Editar Perfil
                        </Button>
                        <Button
                            className="perfil-button delete-button"
                            onClick={eliminarCuenta}
                        >
                            Eliminar Perfil
                        </Button>
                    </>
                )}

                {hasRestrictedPlayer && (
                    <>
                        <Button outline color="success" onClick={handleVolverPartida}>
                            Volver a partida
                        </Button>
                    </>
                )}
                <Button
                    className="perfil-button back-button"
                    onClick={() => navigate("/")}
                >
                    Volver al Inicio
                </Button>
            </div>

            <div className="solicitud-container" style={{ marginBottom: "20px" }}>
                <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                    <input
                        type="text"
                        value={inputValue}
                        onChange={(e) => setInputValue(e.target.value)}
                        placeholder="Escribe el nombre de usuario"
                        className="solicitud-input"
                        style={{
                            padding: "10px",
                            border: "1px solid #ccc",
                            borderRadius: "5px",
                            flexGrow: 1,
                        }}
                    />
                    <Button color="primary" onClick={handleEnviarSolicitud}>
                        Enviar Solicitud
                    </Button>
                </div>
            </div>

<div className="amigos-lista" style={{ maxHeight: '300px', overflowY: 'auto' }}>
    <h3>Lista de Amigos</h3>
    <ul style={{ padding: 0, listStyleType: "none" }}>
        {listaDeAmigos.map((amigo) => (
            <li 
                key={amigo.id} 
                className="amigo-item" 
                style={{ 
                    display: "flex", 
                    alignItems: "center", 
                    justifyContent: "space-between", 
                    marginBottom: "10px" 
                }}
            >
                <div style={{ display: "flex", alignItems: "center" }}>
                    {amigo.imagenPerfil && (
                        <img 
                            src={amigo.imagenPerfil} 
                            alt="Perfil" 
                            style={{ 
                                width: "40px", 
                                height: "40px", 
                                borderRadius: "50%", 
                                marginRight: "10px" 
                            }} 
                        />
                    )}
                    <span style={{ fontSize: "18px", fontWeight: "bold", textAlign: "left" }}>
                        {amigo.username}
                    </span>
                    {amigo.conectado && (
                        <span 
                            className="status-circle" 
                            style={{ 
                                backgroundColor: "green", 
                                width: "10px", 
                                height: "10px", 
                                borderRadius: "50%", 
                                marginLeft: "10px" 
                            }}
                        />
                    )}
                </div>

                <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                    {/* Botón para eliminar amigo */}
                    <button 
                        className="unfriend-button" 
                        onClick={() => handleEliminarAmigo(amigo.id)}
                        title="Eliminar amigo"
                    >
                        🚫
                    </button>

                    {/* Botón de acción según el estado de la partida */}
                    {lastPlayer != null && lastPlayer.partida.estado === "ESPERANDO" && (
                        <button 
                            className="partida-button" 
                            title="Volver a la sala de espera"
                            onClick={() => invitarAPartida(usuarioActual,amigo,lastPlayer.partida,false,jwt)}
                        >
                            🎮
                        </button>
                    )}
                    {lastPlayer != null && lastPlayer.partida.estado === "JUGANDO" && (
                        <button 
                            className="partida-button" 
                            title="Volver al tablero"
                            onClick={() => invitarAPartida(usuarioActual,amigo,lastPlayer.partida,true,jwt)}
                        >
                            👁️
                        </button>
                    )}
                </div>
            </li>
        ))}
    </ul>
</div>
        </div>
    );
}
