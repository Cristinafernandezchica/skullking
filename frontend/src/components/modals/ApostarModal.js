import React, { useState, useEffect } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Input, FormGroup, Label, Alert } from 'reactstrap';
import '../formGenerator/css/Temporizador.css';
import "../../static/css/chat/chat.css";


export default function ApuestaModal({ isVisible, onCancel, onConfirm}) {
    const [apuesta, setApuesta] = useState(0);
    const [errorMessage, setErrorMessage] = useState('');
    const [errorTimer, setErrorTimer] = useState(null);


    useEffect(() => {
        let autoApuestaTimer;
        if (isVisible) {
            autoApuestaTimer = setTimeout(() => {
                setApuesta(0);
                handleConfirmClick();
            }, 20000); // 30 (30000) segundos para realizar la apuesta
        }

        return () => clearTimeout(autoApuestaTimer);
    }, [isVisible]);

    const handleConfirmClick = async () => {
        if (apuesta === '') {
            setErrorMessage('El campo no puede estar vacío.');
            return;
        }
        try {
            await onConfirm(apuesta);
            setApuesta(0);
            setErrorMessage('');
        } catch (error) {
            setErrorMessage(error.message);
            startErrorTimer();
        }

    };

    const startErrorTimer = () => {
        // Iniciar un temporizador para cerrar el modal
        const timer = setTimeout(() => {
            setErrorMessage('');
            onCancel(); // Cierra el modal después de 3 segundos
        }, 3000);
        setErrorTimer(timer);
    };

    return (
        <Modal isOpen={isVisible} className="custom-modal">
            <ModalHeader toggle={onCancel} className="custom-modal-header"> Realizar Apuesta</ModalHeader>
            <ModalBody>
                {errorMessage && <Alert color="danger">{errorMessage}</Alert>}
                <FormGroup>
                    <Label for="apuesta">Apuesta</Label>
                    <Input
                        type="number"
                        id="apuesta"
                        placeholder="Ingrese su apuesta"
                        value={apuesta}
                        onChange={(e) => setApuesta(e.target.value)}
                        min="0"
                    />
                </FormGroup>
            </ModalBody>
            <ModalFooter style={{background: "#112b44",
    color: "#f5d76e"}}>
                <Button color="primary" onClick={handleConfirmClick}>
                    <i className="fa fa-check" aria-hidden="true"></i> Confirmar
                </Button>
            </ModalFooter>
        </Modal>
    );
}
