import React, { useState } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Input, FormGroup, Label } from 'reactstrap';
import { useNavigate } from 'react-router-dom'; // Asegúrate de importar useNavigate

export default function CrearPartidaModal({ isVisible, onCancel, onConfirm }) {
    const [nombrePartida, setNombrePartida] = useState('');

    const handleConfirmClick = async () => {
        const partidaId = await onConfirm(nombrePartida);
        setNombrePartida('');
    
    };

    return (
        <Modal isOpen={isVisible} toggle={onCancel}>
            <ModalHeader toggle={onCancel}>Crear partida</ModalHeader>
            <ModalBody>
                <FormGroup>
                    <Label for="nombrePartida">Nombre de la partida</Label>
                    <Input
                        type="text"
                        id="nombrePartida"
                        placeholder="Game Name"
                        value={nombrePartida}
                        onChange={(e) => setNombrePartida(e.target.value)}
                    />
                </FormGroup>
            </ModalBody>
            <ModalFooter>
                <Button color="secondary" onClick={onCancel}>
                    <i className="fa fa-times" aria-hidden="true"></i> Cancel
                </Button>
                <Button color="primary" onClick={handleConfirmClick}>
                    <i className="fa fa-check" aria-hidden="true"></i> Confirm
                </Button>
            </ModalFooter>
        </Modal>
    );
}
