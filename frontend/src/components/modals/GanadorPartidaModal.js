import React, { useEffect } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter} from 'reactstrap';
import '../formGenerator/css/ganadorBazaModal.css';
import "../../static/css/chat/chat.css";

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
            <ModalHeader className="custom-modal-header" style={{ textAlign: 'center'}}>
                El ganador o ganadores de la partida es...
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
            <ModalFooter style={{
                background: "#112b44",
                color: "#f5d76e", textAlign: 'center'
            }}>
                ¡Enhorabuena!
            </ModalFooter>
        </Modal>
    );

}