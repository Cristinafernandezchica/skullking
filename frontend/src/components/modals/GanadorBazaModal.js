import React, { useEffect } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Label } from 'reactstrap';
import '../formGenerator/css/ganadorBazaModal.css';

export default function GanadorBazaModal({ isVisible, ganador, onClose }) {
    // El cierre automático no está funcionando, hay que mirarlo
    useEffect(() => {
        let autoCloseTimer;
        if (isVisible) {
            autoCloseTimer = setTimeout(() => {
                onClose();
            }, 10000); // Cerrar automáticamente después de 10 segundos
        }

        return () => clearTimeout(autoCloseTimer);
    }, [isVisible, onClose]);

    return (
        <Modal isOpen={isVisible} toggle={onClose} centered className="ganador-modal">
            <ModalHeader className="ganador-modal-header">
                🎉 ¡Tenemos al ganador de la baza! 🎉
            </ModalHeader>
            <ModalBody className="ganador-modal-body">
                {ganador && ganador.usuario ? (
                    <>
                        <h3 className="ganador-name">🏆 {ganador.usuario.username} 🏆</h3>
                        <p className="ganador-message">¡Felicitaciones!</p>
                    </>
                ) : (
                    <p className="loading-message">Obteniendo el ganador...</p>
                )}
            </ModalBody>
            <ModalFooter className="ganador-modal-footer">
                <Button
                    color="#f5d76e"
                    onClick={onClose}
                    className="close-button"
                >
                    ¡Enhorabuena!
                </Button>
            </ModalFooter>
        </Modal>
    );
}
