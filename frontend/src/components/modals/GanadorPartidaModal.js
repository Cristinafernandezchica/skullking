import React, { useEffect } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import '../formGenerator/css/ganadorBazaModal.css';

export default function GanadorPartidaModal({ isVisible, ganador, onClose }) {

    useEffect(() => {
        let autoCloseTimer;
        if (isVisible) {
            autoCloseTimer = setTimeout(() => {
                onClose();
            }, 10000); // Cerrar automáticamente después de 5 segundos
        }

        return () => clearTimeout(autoCloseTimer);
    }, [isVisible, onClose]);



    return (
        <Modal isOpen={isVisible} toggle={onClose} centered>
            <ModalHeader className="ganador-modal-header">
                EL ganador o ganadores de la partida es...
            </ModalHeader>
            <ModalBody className="ganador-modal-body">
                {ganador && ganador.length > 0 ? (
                    <>
                        {ganador.map((g, index) => (
                            <div key={index}>
                                <h3 className="ganador-name">{g}</h3>
                                <p className="ganador-message">¡Felicitaciones!</p>
                            </div>
                        ))}
                    </>
                ) : (
                    <p className="loading-message">Obteniendo el ganador...</p>
                )}
            </ModalBody>
            <ModalFooter className="ganador-modal-footer">
                <Button
                    color="#ffd700"
                    onClick={onClose}
                    className="close-button"
                >
                    ¡Enhorabuena!
                </Button>
            </ModalFooter>
        </Modal>
    );

}