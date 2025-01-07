import React, { useState, useEffect } from "react";
import { Navbar, NavbarBrand, NavLink, NavItem, Nav, NavbarText, NavbarToggler, Collapse} from "reactstrap";
import { Link } from "react-router-dom";
import tokenService from "./services/token.service";
import jwt_decode from "jwt-decode";
import logo from "./static/images/gamelogo_sin_fondo.png";
import "./styles.css";

function AppNavbar() {
    const [roles, setRoles] = useState([]);
    const [username, setUsername] = useState("");
    const [profileImage, setProfileImage] = useState(null);
    const [collapsed, setCollapsed] = useState(true);
    const jwt = tokenService.getLocalAccessToken();

    const toggleNavbar = () => setCollapsed(!collapsed);

    useEffect(() => {
        if (jwt) {
            const decodedToken = jwt_decode(jwt);
            setRoles(decodedToken.authorities);
            setUsername(decodedToken.sub);

            // Fetch detalles del usuario para obtener la imagen de perfil
            async function fetchUserDetails() {
                try {
                    const response = await fetch("/api/v1/users/current", {
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: `Bearer ${jwt}`,
                        },
                    });

                    if (response.ok) {
                        const userData = await response.json();
                        setProfileImage(userData.imagenPerfil);
                    } else {
                        console.error("Error al obtener los detalles del usuario.");
                    }
                } catch (error) {
                    console.error("Error al conectar con el servidor:", error);
                }
            }

            fetchUserDetails();
        }
    }, [jwt]);

    let adminLinks = <></>;
    let userLinks = <></>;
    let userLogout = <></>;
    let adminLogout = <></>;
    let publicLinks = <></>;

    roles.forEach((role) => {
        if (role === "ADMIN") {
            adminLinks = (
                <>
                    <NavItem>
                        <NavLink style={{ color: "white" }} tag={Link} to="/users">
                            Usuarios
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "white" }} tag={Link} to="/partidas">
                            Partidas
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "white" }} tag={Link} to="/users/statistics">
                            Estadísticas
                        </NavLink>
                    </NavItem>
                </>
            );
            adminLogout = (
                <>
                    <NavItem>
                        <NavLink style={{ color: "white" }} id="docs" tag={Link} to="/docs">
                            Documentos
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink
                            style={{ color: "white" }}
                            id="instructions"
                            tag={Link}
                            to="/instructions"
                        >
                            Instrucciones
                        </NavLink>
                    </NavItem>
                    <NavbarText style={{ color: "white" }} className="justify-content-end">
                        {username}
                    </NavbarText>
                    <NavItem className="d-flex">
                        <NavLink style={{ color: "white" }} id="logout" tag={Link} to="/logout">
                            Cerrar sesión
                        </NavLink>
                    </NavItem>
                </>
            );
        } else if (role === "PLAYER") {
            userLinks = (
                <>
                    <NavItem>
                        <NavLink style={{ color: "white" }} tag={Link} to="/users/statistics">
                            Estadísticas
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "white" }} tag={Link} to="/users/partidas">
                            Partidas jugadas
                        </NavLink>
                    </NavItem>
                </>
            );
            userLogout = (
                <>
                    <NavItem>
                        <NavLink style={{ color: "white" }} id="docs" tag={Link} to="/docs">
                            Documentos
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "white" }} id="instructions" tag={Link} to="/instructions">
                            Instrucciones
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "white" }} id="profile" tag={Link} to="/perfil">
                            <div style={{ display: "flex", alignItems: "center", padding: "0" }}>
                                {profileImage && (
                                    <img src={profileImage} alt="Perfil" style={{ width: "30px", height: "30px", borderRadius: "50%", marginRight: "-5px", marginTop: "-2px", marginBottom: "-5px" }} />
                                )}
                            </div>
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "white" }} id="profile" tag={Link} to="/perfil">
                                {username}
                        </NavLink>
                    </NavItem>
                    <NavItem className="d-flex">
                        <NavLink style={{ color: "white" }} id="logout" tag={Link} to="/logout">
                            Cerrar sesión
                        </NavLink>
                    </NavItem>
                </>
            );
        }
    });

    // Rutas públicas para usuarios no autenticados
    if (!jwt) {
        publicLinks = (
            <>
                <NavItem>
                    <NavLink style={{ color: "white" }} id="docs" tag={Link} to="/docs">
                        Documentos
                    </NavLink>
                </NavItem>
                <NavItem>
                    <NavLink style={{ color: "white" }} id="register" tag={Link} to="/register">
                        Registrarse
                    </NavLink>
                </NavItem>
                <NavItem>
                    <NavLink style={{ color: "white" }} id="login" tag={Link} to="/login">
                        Iniciar sesión
                    </NavLink>
                </NavItem>
            </>
        );
    }

    return (
        <div>
            <Navbar expand="md" dark color="dark">
                <NavbarBrand href="/">
                    <img alt="logo" src={logo} className="animated-logo" />
                    Skull King
                </NavbarBrand>
                <NavbarToggler onClick={toggleNavbar} className="ms-2" />
                <Collapse isOpen={!collapsed} navbar>
                    <Nav className="me-auto mb-2 mb-lg-0" navbar>
                        {userLinks}
                        {adminLinks}
                    </Nav>
                    <Nav className="ms-auto mb-2 mb-lg-0" navbar>
                        {publicLinks}
                        {adminLogout}
                        {userLogout}
                    </Nav>
                </Collapse>
            </Navbar>
        </div>
    );
}

export default AppNavbar;
