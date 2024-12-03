import React, { useState } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Input, FormGroup, Label } from 'reactstrap';

// TODO: incluir una propiedad que se modifique y redirija a la 
// pantalla del juego a todos los jugadores

export default function InicioPartidaModal({ isVisible, onCancel, onConfirm }) {
    const handleConfirmClick = () => {
        onConfirm();
    };

    return (
        <Modal isOpen={isVisible} toggle={onCancel}>
            <ModalHeader toggle={onCancel}>Confirmar inicio de partida</ModalHeader>
            <ModalBody>
                ¿Estás seguro de que deseas iniciar la partida?
            </ModalBody>
            <ModalFooter>
                <Button color="secondary" onClick={onCancel}>
                    <i className="fa fa-times" aria-hidden="true"></i> Cancelar
                </Button>
                <Button color="primary" onClick={handleConfirmClick}>
                    <i className="fa fa-check" aria-hidden="true"></i> Confirmar
                </Button>
            </ModalFooter>
        </Modal>
    );
}
