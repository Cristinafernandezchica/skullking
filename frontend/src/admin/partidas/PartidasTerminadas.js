import React, { useEffect, useState } from 'react';
import '../../App.css';
import '../../static/css/home/home.css';
import { Table } from 'reactstrap';

export default function PartidasTerminadas() {
    const [partidas, setPartidas] = useState([]);

    useEffect(() => {
        // Aquí deberías hacer una llamada a tu API para obtener las partidas terminadas
        // Por ejemplo:
        // fetch('/api/partidas/terminadas')
        //     .then(response => response.json())
        //     .then(data => setPartidas(data));

        // Datos de ejemplo
        const exampleData = [
            {
                id: 1,
                creador: 'Jugador1',
                participantes: ['Jugador1', 'Jugador2', 'Jugador3']
            },
            {
                id: 2,
                creador: 'Jugador4',
                participantes: ['Jugador4', 'Jugador5']
            }
        ];
        setPartidas(exampleData);
    }, []);

    return (
        <div className="home-page-container">
            <div className="hero-div">
                <h1>Partidas Terminadas</h1>
                <Table striped>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Creador</th>
                            <th>Participantes</th>
                        </tr>
                    </thead>
                    <tbody>
                        {partidas.map((partida, index) => (
                            <tr key={partida.id}>
                                <th scope="row">{index + 1}</th>
                                <td>{partida.creador}</td>
                                <td>{partida.participantes.join(', ')}</td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </div>
        </div>
    );
}
