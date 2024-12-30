import React, { useEffect, useState } from "react";
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Input, FormGroup, Label } from "reactstrap";

export default function ChatModal({ isVisible, onCancel, onSend }) {
    const [newMessage, setNewMessage] = useState("");
    const [chatList, setChatList] = useState([]);

    const handleSendMessage = () => {
        if (newMessage.trim() === "") {
            alert("El mensaje no puede estar vacío.");
            return;
        }
        onSend(newMessage); // Enviar el mensaje al componente padre o actualizar el estado
        setNewMessage(""); // Limpiar el campo de entrada
    };


    return (
        <Modal isOpen={isVisible} toggle={onCancel}>
            <ModalHeader toggle={onCancel}>Chat</ModalHeader>
            <ModalBody>
                <div style={{ maxHeight: "300px", overflowY: "auto", marginBottom: "1rem" }}>
                    {chatList.length > 0 ? (
                        chatList.map((chat, index) => (
                            <div key={index} style={{ marginBottom: "0.5rem" }}>
                                <strong>{chat.jugador}:</strong> {chat.mensaje}
                            </div>
                        ))
                    ) : (
                        <p>No hay mensajes aún.</p>
                    )}
                </div>
                <FormGroup>
                    <Label for="newMessage">Escribe un mensaje:</Label>
                    <Input
                        type="text"
                        id="newMessage"
                        placeholder="Escribir mensaje"
                        value={newMessage}
                        onChange={(e) => setNewMessage(e.target.value)}
                    />
                </FormGroup>
            </ModalBody>
            <ModalFooter>
                <Button color="primary" onClick={handleSendMessage}>
                    Enviar
                </Button>
                <Button color="secondary" onClick={onCancel}>
                    Cancelar
                </Button>
            </ModalFooter>
        </Modal>
    );
}