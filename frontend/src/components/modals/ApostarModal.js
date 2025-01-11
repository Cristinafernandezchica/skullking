import React, { useState, useEffect } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Input, FormGroup, Label, Alert } from 'reactstrap';
import '../formGenerator/css/Temporizador.css';

export default function ApuestaModal({ isVisible, onCancel, onConfirm, tiempoRestante }) {
    const [apuesta, setApuesta] = useState(0);
    const [errorMessage, setErrorMessage] = useState('');

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
        } finally {
            let timerCerrarTrasError;
            timerCerrarTrasError = setTimeout(() => {
                setErrorMessage('');
                onCancel();
            }, 3000);

            return () => clearTimeout(timerCerrarTrasError);
        }

    };

    return (
        <Modal isOpen={isVisible} toggle={onCancel}>
            <ModalHeader toggle={onCancel}>Realizar Apuesta</ModalHeader>
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
            <ModalFooter>
                <Button color="primary" onClick={handleConfirmClick}>
                    <i className="fa fa-check" aria-hidden="true"></i> Confirmar
                </Button>
            </ModalFooter>
            {/* Barra de cuenta regresiva */}
            {isVisible && (
                <div className="barra-cuenta-regresiva-fija">
                    <div
                        className="barra-progreso"
                        style={{ width: `${(tiempoRestante / 15) * 100}%` }} // Asumiendo 15 segundos como total
                    ></div>
                </div>
            )}
        </Modal>
    );
}
