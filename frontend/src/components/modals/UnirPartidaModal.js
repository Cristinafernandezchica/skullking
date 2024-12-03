import React, { useState, useEffect } from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter, Table } from 'reactstrap';
import tokenService from '../../services/token.service';

const jwt = tokenService.getLocalAccessToken();

export default function UnirPartidaModal({ isVisible, onCancel, onConfirm }) {
    const [partidas, setPartidas] = useState([]);

    useEffect(() => {
        if (isVisible) {
            const fetchPartidas = async () => {
                try {
                    const response = await fetch("/api/v1/partidas?estado=ESPERANDO", {
                        headers: {
                            "Authorization": `Bearer ${jwt}`
                        }
                    });
                    if (!response.ok) {
                        throw new Error("Network response was not ok");
                    }
                    const data = await response.json();
                    setPartidas(data);
                } catch (error) {
                    console.error("Error encontrando partidas:", error);
                }
            };
            fetchPartidas();
        }
    }, [isVisible]);

    const modalStyles = { 
        backgroundColor: '#002147', // Color azul marino 
        color: '#ffffff' // Color de las letras en blanco 
        };

    return (
        <Modal isOpen={isVisible} toggle={onCancel} >
            <ModalHeader toggle={onCancel} style={{ backgroundColor: '#002147', color: '#ffffff' }}>Partidas disponibles</ModalHeader>
            <ModalBody style={{ backgroundColor: '#002147', color: '#ffffff'}}>
                <Table>
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {partidas.map((partida) => (
                            <tr key={partida.id}>
                                <td>{partida.nombre}</td>
                                <td>{partida.estado}</td>
                                <td>
                                    <Button
                                        color="primary"
                                        onClick={() => onConfirm(partida.id)} // Llama a onConfirm con el id de la partida
                                    >
                                        Unirse
                                    </Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </ModalBody>
            <ModalFooter style={{ backgroundColor: '#002147', color: '#ffffff' }}>
                <Button color="secondary" onClick={onCancel}>Cerrar</Button>
            </ModalFooter>
        </Modal>
    );
}
