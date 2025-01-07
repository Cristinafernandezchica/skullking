import React, { useEffect } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import '../formGenerator/css/ganadorBazaModal.css';

export default function GanadorBazaModal({ isVisible, ganador, onClose }) {

    useEffect(() => {
        let autoCloseTimer;
        if (isVisible) {
            autoCloseTimer = setTimeout(() => {
                onClose();
            }, 5000); // Cerrar automáticamente después de 5 segundos
        }

        return () => clearTimeout(autoCloseTimer);
    }, [isVisible, onClose]);

    return (
        <Modal isOpen={isVisible} toggle={onClose} centered>
            <ModalHeader className="ganador-modal-header">
                ¡Tenemos al ganador de la baza!
            </ModalHeader>
            <ModalBody className="ganador-modal-body">
                {ganador && ganador.usuario ? (
                    <>
                        <h3 className="ganador-name">{ganador.usuario.username}</h3>
                        <p className="ganador-message">¡Felicitaciones!</p>
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
