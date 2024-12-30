import React, { useEffect, useState, useRef } from "react";
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Input, FormGroup, Label } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/chat/chat.css";

export default function ChatModal({ isVisible, onCancel, partida, jugadorQueEscribe }) {
    const jwt = tokenService.getLocalAccessToken();
    const [texto, setTexto] = useState("");
    const [chatList, setChatList] = useState([]);
    const chatEndRef = useRef(null); // Crear una referencia para el final del chat
    const intervalRef = useRef(null); // Crear una referencia para el intervalo

    const fetchChat = async () => {
        try {
            const response = await fetch(`/api/v1/chats/${partida}`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
            });
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            const data = await response.json();
            setChatList(data);
        } catch (error) {
            console.error("Error encontrando partidas:", error);
        }
    };

    useEffect(() => {
        fetchChat();
        intervalRef.current = setInterval(() => {
            fetchChat();
            console.log("me sigo ejecutando");
        }, 5000); // Cada 5 segundos
    }, []); // Solo se ejecuta una vez cuando se monta el componente

    // Efecto para desplazar al último mensaje después de que la lista de chats se actualiza
    useEffect(() => {
        chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [chatList]); // Ejecutar el scroll cada vez que chatList cambia

    const handleConfirmClick = async () => {
        if (texto.trim() === "") {
            alert("El mensaje no puede estar vacío.");
            return;
        }
        const Chat = { jugador: jugadorQueEscribe, mensaje: texto };
        try {
            const response = await fetch(`/api/v1/chats`, {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(Chat),
            });
            if (!response.ok) {
                console.log("Algo falla");
                throw new Error("Network response was not ok");
            }
            setTexto(""); // Limpiar el campo de entrada
            fetchChat(); // Volver a obtener el chat después de enviar un mensaje
        } catch (error) {
            console.error("Error:", error);
        }
    };

    // Limpia el intervalo al cerrar el modal
    const handleClose = () => {
        clearInterval(intervalRef.current); // Limpiar el intervalo si está en ejecución
        onCancel(); // Llamar a la función onCancel para cerrar el modal
    };

    return (
        <Modal isOpen={isVisible} toggle={handleClose}>
            <ModalHeader toggle={handleClose}>Chat</ModalHeader>
            <ModalBody>
                <div className="chat-list" style={{ maxHeight: "300px", overflowY: "auto" }}>
                    {chatList.length > 0 ? (
                        chatList.map((chat, index) => (
                            <div key={index} style={{ marginBottom: "0.5rem" }}>
                                <span style={{ color: 'red' }}>{chat.jugador.usuario.username}</span>: {chat.mensaje}
                            </div>
                        ))
                    ) : (
                        <p>No hay mensajes aún.</p>
                    )}
                    <div ref={chatEndRef} /> {/* Este div actúa como un marcador para el scroll */}
                </div>
                <FormGroup>
                    <Label for="newMessage">Escribe un mensaje:</Label>
                    <Input
                        type="text"
                        placeholder="Escribir mensaje"
                        value={texto}
                        onChange={(e) => setTexto(e.target.value)}
                    />
                </FormGroup>
            </ModalBody>
            <ModalFooter>
                <Button color="primary" onClick={handleConfirmClick}>
                    Enviar
                </Button>
                <Button color="secondary" onClick={handleClose}>
                    Cerrar Chat
                </Button>
            </ModalFooter>
        </Modal>
    );
}