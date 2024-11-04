import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table, Input } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";

const jwt = tokenService.getLocalAccessToken();

export default function PartidaListAdmin() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [partidas, setPartidas] = useState([]);
    const [jugadores, setJugadores] = useState({});
    const [search, setSearch] = useState("");
    const [alerts, setAlerts] = useState([]);

    useEffect(() => {
        const fetchPartidas = async () => {
            try {
                const response = await fetch("/api/v1/partidas", {
                    headers: {
                        "Authorization": `Bearer ${jwt}`
                    }
                });
                if (!response.ok) {
                    throw new Error("Network response was not ok");
                }
                const data = await response.json();
                setPartidas(data);
                
                // Fetch jugadores for each partida
                data.forEach(async (partida) => {
                    const jugadoresResponse = await fetch(`/api/v1/partidas/${partida.id}/jugadores`, {
                        headers: {
                            "Authorization": `Bearer ${jwt}`
                        }
                    });
                    if (jugadoresResponse.ok) {
                        const jugadoresData = await jugadoresResponse.json();
                        setJugadores(prev => ({ ...prev, [partida.id]: jugadoresData }));
                    }
                });
            } catch (error) {
                console.error("Error fetching partidas:", error);
                setMessage(error.message);
                setVisible(true);
            }
        };

        fetchPartidas();
    }, []);

    useEffect(() => {
        const lowercasedFilter = search.toLowerCase();
        const filteredData = partidas.filter(item => {
            return (
                item.nombre.toLowerCase().includes(lowercasedFilter) ||
                item.estado.toLowerCase().includes(lowercasedFilter)
            );
        });
        setPartidas(filteredData);
    }, [search]);

    const partidaList = partidas.map((partida) => {
        const jugadoresList = jugadores[partida.id]?.map((jugador) => (
            <div key={jugador.id}>{jugador.usuario.username}</div>
        )) || <div>Cargando jugadores...</div>;

        return (
            <tr key={partida.id}>
                <td>{partida.nombre}</td>
                <td>{partida.estado}</td>
                <td>{jugadoresList}</td>
                <td>
                    <ButtonGroup>
                        <Button
                            size="sm"
                            color="primary"
                            aria-label={"edit-" + partida.id}
                            tag={Link}
                            to={"/partidas/" + partida.id}
                        >
                            Editar
                        </Button>
                        <Button
                            size="sm"
                            color="danger"
                            aria-label={"delete-" + partida.id}
                            onClick={() =>
                                deleteFromList(
                                    `/api/v1/partidas/${partida.id}`,
                                    partida.id,
                                    [partidas, setPartidas],
                                    [alerts, setAlerts],
                                    setMessage,
                                    setVisible
                                )
                            }
                        >
                            Borrar
                        </Button>
                    </ButtonGroup>
                </td>
            </tr>
        );
    });

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div className="admin-page-container">
            <h1 className="text-center">Partidas</h1>
            {alerts.map((a) => a.alert)}
            {modal}
            <div className="search-bar">
                <Input
                    type="text"
                    placeholder="Buscar por nombre o estado"
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                />
            </div>
            <Table aria-label="partidas" className="mt-4">
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Estado</th>
                        <th>Jugadores</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>{partidaList}</tbody>
            </Table>
        </div>
    );
}
