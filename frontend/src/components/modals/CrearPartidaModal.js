import React, { useState } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Input, FormGroup, Label } from 'reactstrap';
import { useNavigate } from 'react-router-dom'; // Asegúrate de importar useNavigate
import "../../static/css/chat/chat.css";
export default function CrearPartidaModal({ isVisible, onCancel, onConfirm }) {
    const [nombrePartida, setNombrePartida] = useState('');

    const handleConfirmClick = async () => {
        await onConfirm(nombrePartida);
        setNombrePartida('');
    
    };

    return (
        <Modal isOpen={isVisible} toggle={onCancel} className="custom-modal">
            <ModalHeader toggle={onCancel} className="custom-modal-header">Crear partida</ModalHeader>
            <ModalBody>
                <FormGroup>
                    <Label for="nombrePartida">Nombre de la partida</Label>
                    <Input
                        type="text"
                        id="nombrePartida"
                        placeholder="Nombre partida"
                        value={nombrePartida}
                        onChange={(e) => setNombrePartida(e.target.value)}
                    />
                </FormGroup>
            </ModalBody>
            <ModalFooter style={{background: "#112b44",
    color: "#f5d76e"}}>
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
