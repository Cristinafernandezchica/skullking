import React, { useEffect, useState } from "react";
import { Button, Spinner } from "reactstrap";
import { useNavigate } from "react-router-dom";
import tokenService from "../services/token.service";
import "./Perfil.css";

export default function Perfil() {
    const [user, setUser] = useState(null);
    const [players, setPlayers] = useState([]);
    const [lastPlayer, setLastPlayer] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [hasRestrictedPlayer, setHasRestrictedPlayer] = useState(false);
    const navigate = useNavigate();
    const jwt = tokenService.getLocalAccessToken();

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
                    } else {
                        console.error("Error al obtener los jugadores asociados.");
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
                    } else {
                        console.error("Error al obtener el último jugador asociado.");
                    }
                } else {
                    console.error("Error al obtener los datos del usuario.");
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
                const response = await fetch(`/api/v1/users/${user.id}`, {
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${jwt}`,
                    },
                    method: "DELETE",
                });

                if (response.ok) {
                    alert("Cuenta eliminada con éxito.");
                    tokenService.removeUser();
                    navigate("/");
                    window.location.reload();
                } else {
                    alert("Error al eliminar la cuenta.");
                }
            } catch (error) {
                console.error("Error al eliminar la cuenta:", error);
            }
        }
    };

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

    return (
        <div className="perfil-container">
            <h2 className="perfil-title">Mi Perfil</h2>
            <div className="perfil-content">
                <img
                    className="perfil-image"
                    src={user.imagenPerfil || "https://blog.tienda-medieval.com/wp-content/uploads/2019/02/Parche-pirata-ojo-derecho.jpg"}
                    alt="Imagen de perfil"
                />
                <h3 className="perfil-username">{user.username}</h3>
                <p className="perfil-description">{user.descripcionPerfil}</p>
            </div>

            {/* Mostrar frase basada en el estado del último jugador */}
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

            <div className="perfil-actions">
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
                <Button
                    className="perfil-button back-button"
                    onClick={() => navigate("/")}
                >
                    Volver al Inicio
                </Button>
            </div>
        </div>
    );
}
