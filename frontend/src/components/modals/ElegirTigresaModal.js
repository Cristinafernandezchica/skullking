import React, { useState, useEffect } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Alert } from 'reactstrap';

export default function ElegirTigresaModal({ isVisible, onCancel, onConfirm }) {
    const [carta, setCarta] = useState(''); // Inicializar como string vacío
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        let autoCambioTigresaTimer;
        if (isVisible) {
            autoCambioTigresaTimer = setTimeout(() => {
                setCarta("banderaBlanca");
                handleConfirmClick("banderaBlanca");
            }, 20000); 
        }

        return () => clearTimeout(autoCambioTigresaTimer);
    }, [isVisible]);

    const handleConfirmClick = async (carta) => {
        try {
            await onConfirm(carta);
            setCarta(carta);
            setErrorMessage('');
        } catch (error) {
            setErrorMessage(error.message);
        }
    };

    return (
        <Modal isOpen={isVisible} toggle={onCancel}>
            <ModalHeader toggle={onCancel}>Elige una opción</ModalHeader>
            <ModalBody>
                {errorMessage && <Alert color="danger">{errorMessage}</Alert>}
                <button
                    className='boton-agrandable'
                    onClick={() => {
                        handleConfirmClick('pirata');
                    }}
                >
                    <img 
                        src={'http://localhost:8080/resources/images/cartas/tigresa_pirata.png'} 
                        alt="Carta Tigresa" 
                        className="imagen-carta-tigresa" 
                    />
                </button>
                <button
                    className='boton-agrandable'
                    onClick={() => {
                        handleConfirmClick('banderaBlanca');
                    }}
                >
                    <img 
                        src={'http://localhost:8080/resources/images/cartas/tigresa_banderaBlanca.png'} 
                        alt="Carta Bandera Blanca" 
                        className="imagen-carta-tigresa" 
                    />
                </button>
            </ModalBody>
            <ModalFooter>
                <Button color="danger" onClick={onCancel}>Cancelar</Button>
            </ModalFooter>
        </Modal>
    );
}
