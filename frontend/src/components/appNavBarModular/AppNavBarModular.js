// `amistadesService.js` - Un archivo para exportar funciones relacionadas con solicitudes de amistad.
export async function aceptarORechazarSolicitud(usuarioActual,remitente, aceptar,jwt) {
    try {
        const response = await fetch(`/api/v1/amistades/aceptarORechazarSolicitud/${remitente}/${usuarioActual}/${aceptar}`, {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${jwt}`,
            },
            method: "PUT"
        });

        if (response.ok) {
            const data = await response.json();
            console.log("La solicitud fue:", data);
            return data;
        } else {
            console.error("Error al realizar la solicitud.");
        }
    } catch (error) {
        console.error("Error al conectar con el servidor:", error);
    }
}

export async function fetchListaDeAmigosConectados(usuarioActual, setAmigosConectados, jwt) {
    try {
        const response = await fetch(`/api/v1/amistades/amigosConectados/${usuarioActual}`, {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${jwt}`,
            },
        });

        if (response.ok) {
            const data = await response.json();
            setAmigosConectados(data);
            console.log("Lista de amigos conectados obtenida:", data);
            return data;
        } else {
            console.error("Error al obtener los detalles de amigos conectados.");
        }
    } catch (error) {
        console.error("Error al conectar con el servidor:", error);
    }
}

export async function fetchListaDeSolicitudes(usuarioActual, setNuevasSolicitudes,jwt) {
    try {
        const response = await fetch(`/api/v1/amistades/misSolicitudes/${usuarioActual}`, {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${jwt}`,
            },
        });

        if (response.ok) {
            const data = await response.json();
            setNuevasSolicitudes(data);
            console.log("Lista de nuevas solicitudes obtenida:", data);
            return data;
        } else {
            console.error("Error al obtener los detalles de las solicitudes.");
        }
    } catch (error) {
        console.error("Error al conectar con el servidor:", error);
    }
}

export  async function fetchUserDetails(jwt,setProfileImage) {
                try {
                    const response = await fetch("/api/v1/users/current", {
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: `Bearer ${jwt}`,
                        },
                    });

                    if (response.ok) {
                        const userData = await response.json();
                        setProfileImage(userData.imagenPerfil);
                    } else {
                        console.error("Error al obtener los detalles del usuario.");
                    }
                } catch (error) {
                    console.error("Error al conectar con el servidor:", error);
                }
            }


export async function usuarioConectadoODesconectado(jwt,usuario,conectado) {
            try {
                const response = await fetch(`/api/v1/users/conectarODesconectar/${usuario}/${conectado}`, {
                    method: "PUT",
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                        "Content-Type": "application/json",
                    },
                });
                if (!response.ok) {
                    console.log("Algo falla");
                    throw new Error("Network response was not ok");
                }
            } catch (error) {
                console.error("Error:", error);
            }
        }

        export async function fetchListaDeAmigos(usuarioActual, setAmigos, jwt) {
            try {
                const response = await fetch(`/api/v1/amistades/misAmigos/${usuarioActual}`, {
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${jwt}`,
                    },
                });
                if (response.ok) {
                    const data = await response.json();
                    setAmigos(data);
                    console.log("Lista de amigos obtenida:", data);
                    return data;
                } else {
                    console.error("Error al obtener los detalles de amigos conectados.");
                }
            } catch (error) {
                console.error("Error al conectar con el servidor:", error);
            }
        }

        export async function fetchListaDeInvitaciones(usuarioActual, setInvitaciones, jwt) {
            try {
                const response = await fetch(`/api/v1/invitaciones/misInvitaciones/${usuarioActual}`, {
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${jwt}`,
                    },
                });
                if (response.ok) {
                    const data = await response.json();
                    setInvitaciones(data);
                    console.log("Lista de invitaciones obtenida:", data);
                    return data;
                } else {
                    console.error("Error al obtener los detalles de las invitaciones.");
                }
            } catch (error) {
                console.error("Error al conectar con el servidor:", error);
            }
        }

        export async function invitarAPartida(usuarioActual,amigo, partidaId ,espectador, jwt) {
            let linkPartida=""
            if(espectador ) {
                linkPartida = `/tablero/${partidaId}`
            }
            else{
                linkPartida = `/salaEspera/${partidaId}`
            }
            const Invitacion ={remitente: usuarioActual, destinatario: amigo,link:linkPartida, espectador}
            try {
                const response = await fetch(`/api/v1/invitaciones`, {
                    method: "POST",
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(Invitacion),
                });
                if (!response.ok) {
                    console.log("Algo falla");
                    throw new Error("Network response was not ok");
                }
            } catch (error) {
                console.error("Error:", error);
            }
        }

        export async function aceptarInvitacion(jwt,invitacionId) {
            try {
                const response = await fetch(`/api/v1/invitaciones/${invitacionId}`, {
                    method: "DELETE",
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                        "Content-Type": "application/json",
                    },
                });
                if (!response.ok) {
                    console.log("Algo falla");
                    throw new Error("Network response was not ok");
                }
            } catch (error) {
                console.error("Error:", error);
            }
        }