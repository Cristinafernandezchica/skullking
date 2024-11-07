import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table, Input, Container, Row, Col } from "reactstrap";
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
    const [filtered, setFiltered] = useState([]); 
    const [search, setSearch] = useState("");
    const [filter, setFilter] = useState("");
    const [alerts, setAlerts] = useState([]);

    function handleSearch(event) {
        const value = event.target.value;
        let filteredPartidas;
        if (value === "") {
            if (filter !== "")
                filteredPartidas = partidas.filter((i) => i.estado === filter);
            else
                filteredPartidas = partidas;
        } else {
            if (filter !== "")
                filteredPartidas = partidas.filter((i) => i.estado === filter && i.nombre.includes(value));
            else
                filteredPartidas = partidas.filter((i) => i.nombre.includes(value) );
        }
        setFiltered(filteredPartidas);
        setSearch(value);
    }

    function handleClear() {
        setFiltered(partidas);
        setSearch("");
        setFilter("");
    }

    function handleFilter(event) {
        const value = event.target.value;
        let filteredPartidas;
        if (value === "") {
            if (search !== "")
                filteredPartidas = partidas.filter((i) => i.nombre.toLowerCase().includes(search));
            else
                filteredPartidas = partidas;
        } else {
            if (search !== "")
                filteredPartidas = partidas.filter((i) => i.estado === value &&  i.nombre.toLowerCase().includes(search));
            else
                filteredPartidas = partidas.filter((i) => i.estado === value);
        }
        setFiltered(filteredPartidas);
        setFilter(value);
    }

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

    let partidasAux;
        if (filtered.length > 0) partidasAux = filtered;
        else partidasAux = partidas;
    let partidaList;
    if (filtered.length === 0 && (filter !== "" || search !== "")) partidaList =
        <tr>
            <td>No hay consultas con esos filtros y parámetros de búsqueda.</td>
        </tr>
    else partidaList = partidasAux.map((partida) => {
        const jugadoresList = jugadores[partida.id]?.map((jugador) => (
            <div key={jugador.id}>{jugador.usuario.username}</div>
        )) || <div>Cargando jugadores...</div>;
//    if (filtered.length > 0) partidaList = filtered;
        return (
            <tr key={partida.id}>
                <td>{partida.nombre}</td>
                <td>{partida.estado}</td>
                <td>{jugadoresList}</td>
            </tr>
        );
    });

    const modal = getErrorModal(setVisible, visible, message);

    return (
<div className="admin-page-container">
<Container fluid style={{ marginTop: "15px" }}>
    <h1 className="text-center">Partidas</h1>
    {alerts.map((a) => a.alert)}
    {modal}
    <Row className="row-cols-auto g-3 align-items-center">
        <Col>
            <Button aria-label='jugando-filter' color="link" onClick={handleFilter} value="JUGANDO">Jugando</Button>
            <Button aria-label='esperando-filter' color="link" onClick={handleFilter} value="ESPERANDO">Esperando</Button>
            <Button aria-label='terminada-filter' color="link" onClick={handleFilter} value="TERMINADA">Terminada</Button>
            <Button aria-label='all-filter' color="link" onClick={handleFilter} value="">Todas</Button>
        </Col>
        <Col className="search-bar">
            <Input type="search" aria-label='search' placeholder="Buscar por nombre" value={search || ''}
                onChange={handleSearch} />
        </Col>
        <Col className="clear-option">
            <Button aria-label='clear-all' color="link" onClick={handleClear} >Borrar todo</Button>
        </Col>
    </Row>
    <Table aria-label='partidas' className="mt-4">
        <thead>
            <tr>
                <th>Nombre</th>
                <th>Estado</th>
                <th>Jugadores</th>
            </tr>
        </thead>
        <tbody>{partidaList}</tbody>
    </Table>
</Container>
</div>
    );
}
