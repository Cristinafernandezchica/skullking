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
    Button
} from "reactstrap";
import { Link } from "react-router-dom";
import tokenService from "./services/token.service";
import jwt_decode from "jwt-decode";
import logo from "./static/images/gamelogo_sin_fondo.png";
import "./styles.css";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { fetchListaDeSolicitudes, fetchUserDetails,fetchListaDeAmigosConectados,aceptarORechazarSolicitud, usuarioConectadoODesconectado} from "./components/appNavBarModular/AppNavBarModular";

function AppNavbar() {
    const [roles, setRoles] = useState([]);
    const [username, setUsername] = useState("");
    const [profileImage, setProfileImage] = useState(null);
    const [collapsed, setCollapsed] = useState(true);
    const jwt = tokenService.getLocalAccessToken();
    const usuarioActual = tokenService.getUser();
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [nuevasSolitudes, setNuevasSolitudes] = useState([]);
    const [amigosConectados, setAmigosConectados] = useState([]);

    const toggleDropdown = async () => {
        setDropdownOpen(!dropdownOpen);
        fetchListaDeAmigosConectados(usuarioActual.id,setAmigosConectados,jwt);
    }
    const toggleNavbar = () => setCollapsed(!collapsed);

    useEffect(() => {
        if (jwt) {
            const decodedToken = jwt_decode(jwt);
            setRoles(decodedToken.authorities);
            setUsername(decodedToken.sub);
            fetchListaDeSolicitudes(usuarioActual.id,setNuevasSolitudes,jwt);
            // Fetch detalles del usuario para obtener la imagen de perfil
            fetchUserDetails(jwt,setProfileImage);
            roles.forEach((role) => {
                if(role === "PLAYER"){
                    usuarioConectadoODesconectado(jwt,usuarioActual.id,true);
                }
            });
        }
        
        return() =>{ if(usuarioActual){ usuarioConectadoODesconectado(jwt,usuarioActual.id,true);}}
    }, [jwt]);

    useEffect(() => {

        // Segundo useEffect: Conexión al WebSocket
          const socket = new SockJS("http://localhost:8080/ws");
          const stompClient = Stomp.over(() => socket);
          
          stompClient.connect({}, (frame) => {
            console.log("Connected: " + frame);
    
            stompClient.subscribe(
              `/topic/amistad/${usuarioActual.id}`,
              (messageOutput) => {
                const data = JSON.parse(messageOutput.body);
                console.log("Mensaje recibido: ", data); // Verifica que el mensaje se reciba correctamente
                setNuevasSolitudes(data); // Actualizamos la lista de nuevas solitudes con los datos recibidos
              }
            );
          });
          // Cleanup: Desconectar el WebSocket cuando el componente se desmonte
          return () => {

            stompClient.disconnect(() => {
              console.log("Disconnected");
            });
          };
        
      }, []); // Solo se ejecuta cuando cambia de baza

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
                        <DropdownMenu>
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
        </div>
    );
}

export default AppNavbar;
