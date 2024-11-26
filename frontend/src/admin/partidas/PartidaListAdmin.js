import React, { useState, useEffect } from "react";
import { Button, Table, Input, Container, Row, Col } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
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
    const [owner, setOwner] = useState([]);
    const [paginaActual, setPaginaActual] = useState(1);
    const [partidasPorPagina, setPartidasPorPagina] = useState("");

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
                filteredPartidas = partidas.filter((i) => i.nombre.includes(value));
        }
        setFiltered(filteredPartidas);
        setSearch(value);
        setPaginaActual(1);
    }

    function handlePartidasPorPagina(event) {
        const value = event.target.value;
        setPartidasPorPagina(value);
        setPaginaActual(1);
    }

    function handleClear() {
        setFiltered(partidas);
        setSearch("");
        setFilter("");
        setPaginaActual(1);
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
                filteredPartidas = partidas.filter((i) => i.estado === value && i.nombre.toLowerCase().includes(search));
            else
                filteredPartidas = partidas.filter((i) => i.estado === value);
        }
        setFiltered(filteredPartidas);
        setFilter(value);
        setPaginaActual(1);
    }

    useEffect(() => {
        const fetchOwner = async (id) => {
            try {
                const response = await fetch(`/api/v1/users/${id}`, {
                    headers: {
                        "Authorization": `Bearer ${jwt}`
                    }
                });
                if (!response.ok) {
                    throw new Error("Network response was not ok");
                }
                const ownerData = await response.json();
                setOwner(prev => ({ ...prev, [id]: ownerData }));
            } catch (error) {
                console.error("Error fetching owner data:", error);
            }
        };

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
                data.sort((a, b) => new Date(b.inicio) - new Date(a.inicio)); // Ordenar por fecha de inicio del mas reciente al que menos
                setPartidas(data);

                data.forEach(async (partida) => {
                    await fetchOwner(partida.ownerPartida);

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

    // Calcular el índice de la última partida de la página actual
    const indiceUltimaPartida = paginaActual * (partidasPorPagina ? parseInt(partidasPorPagina) : partidas.length);
    // Calcular el índice de la primera partida de la página actual
    const indicePrimeraPartida = indiceUltimaPartida - (partidasPorPagina ? parseInt(partidasPorPagina) : partidas.length);
    // Obtener las partidas de la página actual
    const partidasActuales = (filtered.length > 0 ? filtered : partidas).slice(indicePrimeraPartida, indiceUltimaPartida);

    const numeroPaginas = [];
    const totalPartidas = filtered.length > 0 ? filtered.length : partidas.length;
    const totalPaginas = partidasPorPagina ? Math.ceil(totalPartidas / parseInt(partidasPorPagina)) : 1;

    for (let i = 1; i <= totalPaginas; i++) {
        numeroPaginas.push(i);
    }

    function formatearFecha(fecha) {
        const opciones = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' };
        return new Date(fecha).toLocaleDateString('es-ES', opciones).replace(',', ' -');
    }

    let partidaList;
    if (filtered.length === 0 && (filter !== "" || search !== "")) partidaList =
        <tr>
            <td colSpan="5">No hay consultas con esos filtros y parámetros de búsqueda.</td>   
        </tr>
    else partidaList = partidasActuales.map((partida) => {
        const jugadoresList = jugadores[partida.id]?.map((jugador) => (
            <div key={jugador.id}>{jugador.usuario.username}</div>
        )) || <div>Cargando jugadores...</div>;
        const ownerName = owner[partida.ownerPartida]?.username || "Cargando owner...";
        return (
            <tr key={partida.id}>
                <td>{partida.nombre}</td>
                <td>{partida.estado}</td>
                <td>{jugadoresList}</td>
                <td>{ownerName}</td>
                <td>{formatearFecha(partida.inicio)}</td>
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
                    <Col className="partidas-per-page">
                        <Input
                            type="number"
                            aria-label='partidas-por-pagina'
                            placeholder="Partidas por página"
                            value={partidasPorPagina}
                            onChange={handlePartidasPorPagina}
                            min="1"
                        />
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
                            <th>Creador</th>
                            <th>Fecha de creación</th>
                        </tr>
                    </thead>
                    <tbody>{partidaList}</tbody>
                </Table>
                <Row className="mt-3 justify-content-between">
                    <Col>
                        <Button disabled={paginaActual === 1} onClick={() => setPaginaActual(paginaActual - 1)}>
                            Página anterior
                        </Button>
                    </Col>
                    <Col className="text-center">
                        Página {paginaActual} de {totalPaginas}
                    </Col>
                    <Col className="text-end">
                        <Button
                            disabled={paginaActual >= totalPaginas}
                            onClick={() => setPaginaActual(paginaActual + 1)}
                        >
                            Página siguiente
                        </Button>
                    </Col>
                </Row>
            </Container>
        </div>
    );
}
