import React, { useState, useEffect } from "react";
import {
    Navbar,
    NavbarBrand,
    NavLink,
    NavItem,
    Nav,
    NavbarText,
    NavbarToggler,
    Collapse,
    Dropdown,
    DropdownToggle,
    DropdownMenu,
    DropdownItem,
    Button,
    Alert
} from "reactstrap";
import { Link, Navigate, useNavigate } from "react-router-dom";
import tokenService from "./services/token.service";
import jwt_decode from "jwt-decode";
import logo from "./static/images/gamelogo_sin_fondo.png";
import "./styles.css";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { fetchListaDeSolicitudes, fetchUserDetails,fetchListaDeAmigosConectados,aceptarORechazarSolicitud,
    usuarioConectadoODesconectado, fetchListaDeInvitaciones,
    unirseAPartida,aceptarInvitacion} from "./components/appNavBarModular/AppNavBarModular";

function AppNavbar() {
    const [roles, setRoles] = useState([]);
    const navigate = useNavigate();
    const [username, setUsername] = useState("");
    const [profileImage, setProfileImage] = useState(null);
    const [collapsed, setCollapsed] = useState(true);
    const jwt = tokenService.getLocalAccessToken();
    const usuarioActual = tokenService.getUser();
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [nuevasSolitudes, setNuevasSolitudes] = useState([]);
    const [amigosConectados, setAmigosConectados] = useState([]);
    const [invitaciones, setInvitaciones] = useState([]);
    const [errors, setErrors] = useState([]);

    const toggleDropdown = async () => {
        setDropdownOpen(!dropdownOpen);
        fetchListaDeAmigosConectados(usuarioActual.id,setAmigosConectados,jwt);
        fetchListaDeInvitaciones(usuarioActual.id,setInvitaciones,jwt);
        fetchListaDeSolicitudes(usuarioActual.id, setNuevasSolitudes, jwt);
    }
    const toggleNavbar = () => setCollapsed(!collapsed);

    const showError = (error) => { 
        setErrors([error]); 
        setTimeout(() => { 
          setErrors([]); 
        }, 5000); // La alerta desaparece después de 5000 milisegundos (5 segundos) 
      };

      async function invitacionAceptada(invitacion){
        unirseAPartida(usuarioActual,invitacion.partida,setErrors,showError,jwt);
        aceptarInvitacion(jwt,invitacion.id);
        if(errors.length===0){
            if(invitacion.espectador===true){
                navigate(`/tablero/${invitacion.partida.id}`);
            }
            else if(invitacion.espectador===false){
                navigate(`/salaEspera/${invitacion.partida.id}`);
            }
        }
      }

    useEffect(() => {
        if (jwt) {
            const decodedToken = jwt_decode(jwt);
            setRoles(decodedToken.authorities);
            setUsername(decodedToken.sub);
            fetchUserDetails(jwt, setProfileImage);
            roles.forEach((role) => {
                if (role === "PLAYER") {
                    usuarioConectadoODesconectado(jwt, usuarioActual.id, true);
                }
            });
        }

        return () => { if (usuarioActual) { usuarioConectadoODesconectado(jwt, usuarioActual.id, true); } }
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
                    <Dropdown nav isOpen={dropdownOpen} toggle={
                        toggleDropdown
                    }>
                        <DropdownToggle nav caret style={{ color: "white" }}>
                            Notificaciones
                        </DropdownToggle>
                        <DropdownMenu style={{ maxHeight: "300px", overflowY: "auto" }}>
    {/* Solicitudes */}
    {nuevasSolitudes.length > 0 && (
        <>
            <p><b>Solicitudes</b></p>
            {nuevasSolitudes.map((usuario) => (
                <DropdownItem
                    key={usuario.id}
                    tag="div"
                    className="d-flex justify-content-between align-items-center"
                >
                    <div style={{ display: "flex", alignItems: "center" }}>
                        {usuario.imagenPerfil && (
                            <img
                                src={usuario.imagenPerfil}
                                alt="Perfil"
                                style={{
                                    width: "30px",
                                    height: "30px",
                                    borderRadius: "50%",
                                    marginRight: "10px"
                                }}
                            />
                        )}
                        <span>{usuario.username}</span>
                    </div>
                    <div>
                        <Button
                            className="btn btn-success btn-sm mx-1"
                            onClick={async () => await aceptarORechazarSolicitud(usuarioActual.id, usuario.id, true, jwt)}
                        >
                            ✓
                        </Button>
                        <Button
                            className="btn btn-danger btn-sm"
                            onClick={() => aceptarORechazarSolicitud(usuarioActual.id, usuario.id, false, jwt)}
                        >
                            ✕
                        </Button>
                    </div>
                </DropdownItem>
            ))}
        </>
    )}

    {/* Amigos Conectados */}
    {amigosConectados.length > 0 && (
        <>
            <p><b>Amigos conectados</b></p>
            {amigosConectados.map((usuario) => (
                <DropdownItem 
                    key={usuario.id} 
                    tag="div" 
                    className="d-flex justify-content-between align-items-center"
                >
                    <div style={{ display: "flex", alignItems: "center" }}>
                        {usuario.imagenPerfil && (
                            <img 
                                src={usuario.imagenPerfil} 
                                alt="Perfil" 
                                style={{ 
                                    width: "30px", 
                                    height: "30px", 
                                    borderRadius: "50%", 
                                    marginRight: "10px" 
                                }} 
                            />
                        )}
                        <span>{usuario.username}</span>
                    </div>
                </DropdownItem>
            ))}
        </>
    )}

{invitaciones.length > 0 && (
    <>
        <p><b>Invitaciones</b></p>
        {invitaciones.map((invitacion) => (
            <DropdownItem 
                key={invitacion.id} 
                tag="div" 
                className="d-flex justify-content-between align-items-center"
            >
                {invitacion.espectador ? (
                    <>
                        <span>Espectar a {invitacion.remitente.username}</span>
                        <div className="d-flex align-items-center">
                            <Button 
                                className="btn btn-secondary btn-sm ms-2" 
                                onClick={() => invitacionAceptada(invitacion)}
                            >
                                👁️
                            </Button>
                            <Button 
                                className="btn btn-danger btn-sm ms-2" 
                                onClick={() => aceptarInvitacion(jwt,invitacion.id)} // Lógica para rechazar
                                title="Rechazar invitación"
                            >
                                ✖
                            </Button>
                        </div>
                    </>
                ) : (
                    <>
                        <div className="d-flex align-items-center">
                            <Button 
                                className="btn btn-primary btn-sm me-2" 
                                onClick={() => invitacionAceptada(invitacion)}
                            >
                                🎮
                            </Button>
                            <span>Únete a la partida de {invitacion.remitente.username}</span>
                            <Button 
                                className="btn btn-danger btn-sm ms-2" 
                                onClick={() => aceptarInvitacion(jwt,invitacion.id)} // Lógica para rechazar
                                title="Rechazar invitación"
                            >
                                ✖
                            </Button>
                        </div>
                    </>
                )}
            </DropdownItem>
        ))}
    </>
)}
</DropdownMenu>

                    </Dropdown>
                    <NavItem>
                        <NavLink style={{ color: "white" }} id="instructions" tag={Link} to="/instructions">
                            Instrucciones
                        </NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink style={{ color: "white" }} id="profile" tag={Link} to="/perfil">
                            <div style={{ display: "flex", alignItems: "center", padding: "0" }}>
                                {(profileImage && (
                                    <img src={profileImage} alt="Perfil" style={{ width: "30px", height: "30px", borderRadius: "50%", marginRight: "-5px", marginTop: "-2px", marginBottom: "-5px" }} />
                                )) || (
                                        <img src={"https://blog.tienda-medieval.com/wp-content/uploads/2019/02/Parche-pirata-ojo-derecho.jpg"} alt="Perfil" style={{ width: "30px", height: "30px", borderRadius: "50%", marginRight: "-5px", marginTop: "-2px", marginBottom: "-5px" }} />
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
            <div className="validation-messages">
                {errors.length > 0 && errors.map((error, index) => (
                    <Alert key={index} color="danger">{error}</Alert>
                ))}</div>
        </div>


    );
}

export default AppNavbar;
