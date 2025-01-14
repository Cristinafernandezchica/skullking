import React, { useEffect } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter} from 'reactstrap';
import '../formGenerator/css/ganadorBazaModal.css';
import "../../static/css/chat/chat.css";

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
        <Modal isOpen={isVisible} centered>
            <ModalHeader className="custom-modal-header" style={{ textAlign: 'center' }}>
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
            <ModalFooter style={{
                background: "#112b44",
                color: "#f5d76e", textAlign: 'center'
            }}>
                ¡Enhorabuena!
            </ModalFooter>
        </Modal>
    );
}
