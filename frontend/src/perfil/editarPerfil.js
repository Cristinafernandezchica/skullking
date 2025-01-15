import React, { useEffect, useState } from "react";
import { Form, Input, Label, Button, Spinner } from "reactstrap";
import { useNavigate } from "react-router-dom";
import tokenService from "../services/token.service";
import {sendLogoutRequest} from '../auth/logout';
import "./EditarPerfil.css";

export default function EditarPerfil() {
    const [user, setUser] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const navigate = useNavigate();
    const jwt = tokenService.getLocalAccessToken();

    // Fetch del usuario actual
    useEffect(() => {
        async function fetchUser() {
            try {
                const response = await fetch("/api/v1/users/current", {
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${jwt}`,
                    },
                });

                if (response.ok) {
                    const userData = await response.json();
                    setUser(userData);
                } else {
                    console.error("Error al obtener los datos del usuario.");
                }
            } catch (error) {
                console.error("Error al conectar con el servidor:", error);
            } finally {
                setIsLoading(false);
            }
        }
        fetchUser();
    }, [jwt]);

    // Actualizar perfil
    const actualizarPerfil = async (event) => {
        event.preventDefault();
        try {
            const response = await fetch(`/api/v1/users/${user.id}`, {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${jwt}`,
                },
                method: "PUT",
                body: JSON.stringify(user),
            });

            if (response.ok) {
                sendLogoutRequest();
            } else {
                alert("Error al actualizar el perfil.");
            }
        } catch (error) {
            console.error("Error al actualizar el perfil:", error);
        }
    };

    // Manejo de cambios en los inputs
    const handleChange = (event) => {
        const { name, value } = event.target;
        setUser((prevUser) => ({
            ...prevUser,
            [name]: value,
        }));
    };

    if (isLoading) {
        return (
            <div className="loading-container">
                <Spinner color="primary" />
                <p>Cargando...</p>
            </div>
        );
    }

    if (!user) {
        return <p>No se encontraron datos del usuario.</p>;
    }

    return (
        <div
        className="background-container"
    >
        <div className="editar-perfil-container">
            <h2 className="editar-perfil-title">Editar Perfil</h2>
            <Form onSubmit={actualizarPerfil} className="editar-perfil-form">
                <div className="form-group">
                    <Label for="username" className="form-label">
                        Nombre de Usuario:
                    </Label>
                    <Input
                        type="text"
                        id="username"
                        name="username"
                        value={user.username || ""}
                        onChange={handleChange}
                        required
                        className="form-input"
                    />
                </div>

                <div className="form-group">
                    <Label for="descripcionPerfil" className="form-label">
                        Descripción del Perfil:
                    </Label>
                    <Input
                        type="text"
                        id="descripcionPerfil"
                        name="descripcionPerfil"
                        value={user.descripcionPerfil || ""}
                        onChange={handleChange}
                        required
                        className="form-input"
                    />
                </div>

                <div className="form-group">
                    <Label for="imagenPerfil" className="form-label">
                        Imagen de Perfil (URL):
                    </Label>
                    <Input
                        type="text"
                        id="imagenPerfil"
                        name="imagenPerfil"
                        value={user.imagenPerfil || ""}
                        onChange={handleChange}
                        required
                        className="form-input"
                    />
                </div>

                <div className="form-buttons">
                    <Button type="submit" color="primary" className="form-button save-button">
                        Guardar Cambios
                    </Button>
                    <Button
                        type="button"
                        className="form-button cancel-button"
                        onClick={() => navigate("/perfil")}
                    >
                        Cancelar
                    </Button>
                </div>
            </Form>
        </div>
        </div>
    );
}
