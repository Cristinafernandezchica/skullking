# Plan de Pruebas

## 1. Introducción

Este documento describe el plan de pruebas para el proyecto **Skull King** desarrollado en el marco de la asignatura **Diseño y Pruebas 1** por el grupo **L7-05**. El objetivo del plan de pruebas es garantizar que el software desarrollado cumple con los requisitos especificados en las historias de usuario y que se han realizado las pruebas necesarias para validar su funcionamiento.

## 2. Alcance

El alcance de este plan de pruebas incluye:

- Pruebas unitarias.
  - Pruebas unitarias de backend incluyendo pruebas servicios o repositorios.
  - Pruebas unitarias de frontend: pruebas de las funciones javascript creadas en frontend.
  - Pruebas unitarias de interfaz de usuario. Usan la interfaz de  usuario de nuestros componentes frontend.
- Pruebas de integración: En nuestro caso principalmente son pruebas de controladores.

## 3. Estrategia de Pruebas

### 3.1 Tipos de Pruebas

#### 3.1.1 Pruebas Unitarias
Las pruebas unitarias se realizarán para verificar el correcto funcionamiento de los componentes individuales del software. Se utilizarán herramientas de automatización de pruebas como **JUnit** o **Mockito** en background. Aún está por determinar la herramienta a usar en frontend.

Actualmente, tenemos implementadas pruebas unitarias para todas las entidades excepto para Carta, pues esta entidad solo tiene implementadas las funciones CRUD, las cuales no se usan pues las cartas se han añadido directamente a la base de datos. En la matriz de trazabilidad, por el momento, solo hemos incluído aquellas pruebas que están directamente relacionadas con las historias de usuario ya implementadas.

#### 3.1.2 Pruebas de Integración
Las pruebas de integración se enfocarán en evaluar la interacción entre los distintos módulos o componentes del sistema, nosotros las realizaremos a nivel de API, probando nuestros controladores Spring.

## 4. Herramientas y Entorno de Pruebas

### 4.1 Herramientas
- **Maven**: Gestión de dependencias y ejecución de las pruebas.
- **JUnit**: Framework de pruebas unitarias.
- **Jacoco**: Generación de informes de cobertura de código.
- **Jest**: Framework para pruebas unitarias en javascript.
- **React-test**: Liberaría para la creación de pruebas unitarias de componentes React.
- **Mockito**: Librería de Java para la creación de mocks en pruebas unitarias.

### 4.2 Entorno de Pruebas
Las pruebas se ejecutarán en el entorno de desarrollo y, eventualmente, en el entorno de pruebas del servidor de integración continua.

## 5. Planificación de Pruebas

### 5.1 Cobertura de Pruebas

El informe de cobertura de pruebas se puede consultar [aquí](
https://html-preview.github.io/?url=https://raw.githubusercontent.com/gii-is-DP1/group-project-seed/main/target/site/jacoco/index.html).

### 5.2 Matriz de Trazabilidad

| Historia de Usuario        | Prueba                                                                                     | Descripción                                                                                     | Estado        | Tipo                  |
|----------------------------|-------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------|---------------|-----------------------|
| HGJ1: Registrar usuario     | [UTB-1:AuthControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/auth/AuthControllerTests.java#L131shouldRegisterUser) | Verifica que un nuevo usuario puede registrarse en el sistema.                                  | Implementada  | Unitaria en backend   |
| HGJ2: Iniciar sesión        | [UTB-1:AuthControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/auth/AuthControllerTests.java#L99shouldAuthenticateUser) | Verifica que un usuario puede iniciar sesión con credenciales válidas.                         | Implementada  | Unitaria en backend   |
| HGJ3: Cerrar sesión         | [UTB-1:AuthControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/auth/AuthControllerTests.java#L122shouldNotValidateToken) | Verifica que un usuario puede cerrar sesión correctamente observando que su token ya no es válido tras cerrar sesión.                                      | Implementada  | Unitaria en backend|
| HGJ4: Ver perfil de usuario | [UTB-2:UserControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java#L383shouldFindCurrentUserProfile) | Verifica que un usuario puede ver su perfil para interactuar con su información.                  | Implementada  | Unitaria en backend   |
| HGJ5: Editar perfil de usuario        | [UTB-2:UserControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java#L210shouldUpdateUser) | Verifica que un usuario puede editar su perfil.                                                | Implementada  | Unitaria en backend|
| HGJ6: Eliminar perfil de usuario | [UTB-2:UserControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java#L237shouldDeleteUser) | Verifica que un usuario puede eliminar su propia cuenta.                                        | Implementada  | Unitaria en backend|
| HGA1: Listado usuarios registrados     | [UTB-3:UserServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserServiceTests.java#L65shouldFindAllUsers) | Verifica que se obtengan todos los usuarios registrados.                                        | Implementada  | Unitaria en backend   |
| HGA1: Listado usuarios registrados     | [UTB-4:UserListAdmin](../../frontend\src\admin\users\UserListAdmin.test.js) | Verifica que se obtengan y visualicen todos los usuarios registrados.                                        | Implementada  | En frontend   | 
| HGA2: Borrar un usuario     | [UTB-2:UserControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java#L237shouldDeleteUser) | Verifica que se pueda borrar un usuario.                                                       | Implementada  | Unitaria en backend   |
| HGA3: Editar un usuario     | [UTB-2:UserControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java#L210shouldUpdateUser) | Verifica que se pueda editar un usuario por un administrador.                                  | Implementada  | Unitaria en backend|
| HGA4: Crear un usuario      | [UTB-2:UserControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java#L198shouldCreateUser) | Verifica que un administrador pueda crear un usuario.                                           | Implementada  | Unitaria en backend|
| HJA1: Ver partidas filtradas| [UTB-5:PartidaServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/partida/PartidaServiceTest.java#L130shouldGetAllPartidasFiltradasPorNombreYEstado) | Verifica que se pueden obtener las partidas pudiendo filtrar por estado y/o nombre.            | Implementada  | Unitaria en backend   |
| HJA1: Ver partidas filtradas| [UTB-6:PartidaListAdmin.test](../../frontend\src\admin\partidas\PartidaListAdmin.test.js) | Verifica que se pueden obtener las partidas pudiendo filtrar por estado y/o nombre.                                          |  Implementada  | En frontend   |
| HJA2: Ver estadísticas      | [UTB-7:UserDashBoard.test](../../frontend\src\admin\users\UserDashboard.test.js) | Verifica que un usuario cualquiera puede ver las estadísticas de los usuarios.                      | Por implementar  | En frontend|
| HJJ1: Jugar una carta durante mi turno | [UTB-8:ManoRestControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/mano/ManoRestControllerTest.java#L163shouldCartasDisabled_SuccessNonEmpty) | Verifica que un jugador pueda jugar una carta en su turno. | Implementada | Unitaria en backend |
| HJJ2: Ver puntuación actualizada | [UTB-9:RondaServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/ronda/RondaServiceTest.java#L412shouldGetPuntaje) | Verifica que la puntuación se actualice correctamente tras cada ronda. | Implementada | Unitaria en backend |
| HJJ3: Usar cartas especiales | [UTB-10:ManoServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/mano/ManoServiceTest.java#L509shouldReturnCartasEnabledCuandoHasEspecialSinPaloBaza) | Verifica que el jugador pueda usar cartas especiales. | Implementada | Unitaria en backend |
| HJJ4: Usar carta bandera blanca/de escape | [UTB-10:ManoServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/mano/ManoServiceTest.java#L509shouldReturnCartasEnabledCuandoHasEspecialSinPaloBaza) | Verifica que el jugador pueda usar una carta bandera blanca/de escape. | Implementada | Unitaria en backend |
| HJJ5: Ver apuestas de la ronda | [UTB-5:PartidaServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/partida/PartidaServiceTest.java#L915shouldApuestaValida) | Verifica que el jugador pueda ver todas las apuestas realizadas en la ronda actual. | Implementada | Unitaria en backend |
| HJJ6: Elegir rol de la carta Tigresa | [UTB-11:CartaRestControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/carta/CartaRestControllerTest.java#L131shouldCambioTigresa) | Verifica que el jugador pueda elegir el rol de la carta Tigresa. | Implementada | Unitaria en backend |
| HJJ7: Ver ganador de la baza   | [UTB-13:PartidaControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/partida/PartidaControllerTest.java#L270shouldGanadorPartida) | Verifica que un jugador puede ver el ganador de una baza al finalizarla.                       | Implementada  | Unitaria en backend   |
| HJJ8: Conocer reglas de juego | [UTB-12:App.test](../../frontend\src\App.test.js) | Verifica que las reglas del juego sean accesibles. | Implementada | Prueba de interfaz en frontend |
| HJJ9: Crear partidas | [UTB-13:PartidaControllerTest](../../src\test\java\es\us\dp1\lx_xy_24_25\your_game_name\partida\PartidaControllerTest.java#L136shouldCreatePartida) | Verifica que se pueda crear una partida correctamente. | Implementada | Unitaria en backend |
| HJJ10: Hacer apuestas | [UTB-5:PartidaServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/partida/PartidaServiceTest.java#L915shouldApuestaValida) | Verifica que un jugador pueda hacer apuestas sobre cuántas bazas ganará. | Implementada | Unitaria en backend |
| HJJ11: Unirse a partida | [UTB-14:JugadorRestControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/jugador/JugadorRestControllerTest.java#L168shouldCreateJugador) | Verifica que se cree un jugador al unirse a una partida. | Implementada | Unitaria en backend |
| HJJ12: Iniciar partida creada | [UTB-5:PartidaServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/partida/PartidaServiceTest.java#L339testIniciarPartida) | Verifica que el usuario creador pueda iniciar la partida. | Implementada | Unitaria en backend |
| HJJ13: Ver jugadores en la sala | [UTB-13:PartidaControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/partida/PartidaControllerTest.java#L181shouldGetJugadoresByPartidaId) | Verifica que se obtengan los jugadores de una partida. | Implementada | Unitaria en backend |
| HJJ13: Ver jugadores en la sala | [UTB-15:SalaEspera.test.js](../../frontend/src/salaEspera/SalaEspera.test.js) | Verifica que se obtengan los jugadores de una partida. | Implementada | Prueba en frontend |
| HJJ14: Volver a partida empezada | [UTB-5:PartidaServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/partida/PartidaServiceTest.java#L571shouldUsuarioJugadorEnPartidaJugando) | Verifica que el jugador pueda volver a una partida en curso. | Implementada | Prueba en frontend |
| HJJ15: Salir de sala de espera | [UTB-13:PartidaControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/jugador/JugadorRestControllerTest.java#191shouldDeleteJugadorWithWebSocket) | Verifica que un jugador pueda salir de la sala de espera antes de que la partida comience.      | Implementada  | Unitaria en backend   |
| HJJ16: Ver ganador de la partida | [UTB-13:PartidaControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/partida/PartidaControllerTest.java#L270shouldGanadorPartida) | Verifica que se muestre el ganador/es de la partida al finalizar la última ronda.               | Implementada  | Unitaria en backend   |
| HJJ17: Ver bazas ganadas por ronda | [UTB-16:BazaRestControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/baza/BazaRestControllerTest.java#L192shouldFindBazaByIdGanador) | Verifica que los jugadores puedan ver cuántas bazas ha ganado cada uno tras cada ronda.         | Implementada  | Unitaria en backend   |
| HJJ18: Saber ronda y baza actual | [UTB-16:BazaRestControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/baza/BazaRestControllerTest.java#L215shouldFindBazaActualByRondaId) | Verifica que los jugadores sepan la ronda y baza actuales durante la partida.     | Implementada  | Unitaria en backend   |
| HJJ19: Listar mis partidas | [UTB-17:UserPartidas.test.js](../../frontend/src/admin/users/UserPartidas.test.js) | Verifica que un jugador pueda ver un listado de las partidas en las que ha participado.         | Implementada  | Prueba en frontend   |
| HSJ1: Chat en partida                          | [UTB-18:ChatControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/chat/ChatControllerTest.java#L106shouldSendChatMessage)         | Verifica que los jugadores puedan enviar y recibir mensajes en el chat de la partida.             | Implementada  | Unitaria en backend   |
| HSJ2: Enviar solicitud de amistad              | [UTB-19:AmistadServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/amistad/AmistadServiceTest.java#L342shouldEnviarSolicitudDeAmistad) | Verifica que un jugador pueda enviar solicitudes de amistad.                                     | Implementada  | Unitaria en backend   |
| HSJ3: Ver, aceptar y rechazar solicitudes      | [UTB-19:AmistadServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/amistad/AmistadServiceTest.java#L393shouldAceptarORechazarSolicitudDeAmistad_AceptaSolicitud) | Verifica que se puedan aceptar o rechazar solicitudes de amistad.                               | Implementada  | Unitaria en backend   |
| HSJ3: Ver, aceptar y rechazar solicitudes      | [UTB-19:AmistadServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/amistad/AmistadServiceTest.java#L405shouldAceptarORechazarSolicitudDeAmistad_RechazaSolicitud) | Verifica que se puedan aceptar o rechazar solicitudes de amistad.                               | Implementada  | Unitaria en backend   |
| HSJ4: Ver a mis amigos conectados             | [UTB-19:AmistadServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/amistad/AmistadServiceTest.java#L261shouldGetAllMyConnectedFriends) | Verifica que un jugador pueda ver sus amigos conectados.                                         | Implementada  | Unitaria en backend   |
| HSJ5: Dejar de ser amigos                     | [UTB-19:AmistadServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/amistad/AmistadServiceTest.java#L168shouldDeleteAmistad) | Verifica que se pueda eliminar a un amigo.                                                      | Implementada  | Unitaria en backend   |
| HSJ6: Invitar a jugar o espectar mi partida   | [UTB-20:InvitacionRestControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/invitacion/InvitacionRestControllerTest.java#L139shouldEnviarInvitacion) | Verifica que se pueda enviar una invitación a jugar o espectar.                                  | Implementada  | Unitaria en backend   |
| HSJ7: Ver, aceptar y rechazar invitaciones    | [UTB-20:InvitacionRestControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/invitacion/InvitacionRestControllerTest.java#L92shouldGetTodasMisInvitaciones) | Verifica que un jugador pueda ver las invitaciones a partidas.                        | Implementada  | Unitaria en backend   |
| HSJ7: Ver, aceptar y rechazar invitaciones    | [UTB-20:InvitacionRestControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/invitacion/InvitacionRestControllerTest.java#L163shouldAceptarInvitacion) | Verifica que un jugador pueda aceptar o rechazar invitaciones a partidas.                        | Implementada  | Unitaria en backend   |
| HSJ8: Espectar partidas                      | [UTB-21:AmistadRestControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/amistad/AmistadRestControllerTest.java#L82shouldGetAmigosQuePuedenVerLaPartida) | Verifica que se permita a un jugador entrar como espectador en una partida.                     | Implementada  | Unitaria en backend   |

### 5.3 Matriz de Trazabilidad entre Pruebas e Historias de Usuario

| Prueba                               | HGJ1 | HGJ2 | HGJ3 | HGJ4 | HGJ5 | HGJ6 | HGA1 | HGA2 | HGA3 | HGA4 | HJA1 | HJA2 | HJJ1 | HJJ2 | HJJ3 | HJJ4 | HJJ5 | HJJ6 | HJJ7 | HJJ8 | HJJ9 | HJJ10 | HJJ11 | HJJ12 | HJJ13 | HJJ14 | HJJ15 | HJJ16 | HJJ17 | HJJ18 | HJJ19 | HSJ1 | HSJ2 | HSJ3 | HSJ4 | HSJ5 | HSJ6 | HSJ7 | HSJ8 |
|-------------------------------------|------|------|------|------|------|------|------|------|------|------|------|------|------|------|------|------|------|------|------|------|------|-------|-------|-------|-------|-------|-------|-------|-------|-------|------|------|------|------|------|------|------|------|------|
| UTB-1:AuthControllerTests           |  X   |  X   |  X   |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |       |       |       |       |       |       |       |       |       |      |      |      |      |      |      |      |      |
| UTB-2:UserControllerTests          |      |      |      |  X   |  X   |  X   |      |  X   |  X   |  X   |      |      |      |      |      |      |      |      |      |      |      |       |       |       |       |       |       |       |       |       |      |      |      |      |      |      |      |      |
| UTB-3:UserServiceTests             |      |      |      |      |      |      |  X   |      |      |      |      |      |      |      |      |      |      |      |      |      |      |       |       |       |       |       |       |       |       |       |      |      |      |      |      |      |      |      |
| UTB-4:UserListAdmin                |      |      |      |      |      |      |  X   |      |      |      |      |      |      |      |      |      |      |      |      |      |      |       |       |       |       |       |       |       |       |       |      |      |      |      |      |      |      |      |
| UTB-5:PartidaServiceTest           |      |      |      |      |      |      |      |      |      |      |  X   |      |      |      |      |      |  X   |      |      |      |      |  X    |      |  X    |       |  X    |       |       |       |       |       |      |      |      |      |      |      |      |
| UTB-6:PartidaListAdmin.test        |      |      |      |      |      |      |      |      |      |      |  X   |      |      |      |      |      |      |      |      |      |      |       |       |       |       |       |       |       |       |       |      |      |      |      |      |      |      |      |
| UTB-7:UserDashBoard.test           |      |      |      |      |      |      |      |      |      |      |      |  X   |      |      |      |      |      |      |      |      |      |       |       |       |       |       |       |       |       |       |      |      |      |      |      |      |      |      |
| UTB-8:ManoRestControllerTest       |      |      |      |      |      |      |      |      |      |      |      |      |  X   |      |      |      |      |      |      |      |      |       |       |       |       |       |       |       |       |       |      |      |      |      |      |      |      |      |
| UTB-9:RondaServiceTest             |      |      |      |      |      |      |      |      |      |      |      |      |      |  X   |      |      |      |      |      |      |      |       |       |       |       |       |       |       |       |       |      |      |      |      |      |      |      |      |
| UTB-10:ManoServiceTest             |      |      |      |      |      |      |      |      |      |      |      |      |      |      |  X   |  X   |      |      |      |      |      |       |       |       |       |       |       |       |       |       |      |      |      |      |      |      |      |      |
| UTB-11:CartaRestControllerTest     |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |     |  X    |      |      |      |       |       |       |       |       |       |       |       |       |      |      |      |      |      |      |      |      |
| UTB-12:App.test                    |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |  X   |      |       |       |       |       |       |       |       |       |       |      |      |      |      |      |      |      |      |
| UTB-13:PartidaControllerTest       |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |  X   |      |  X   |       |       |      |  X    |       |  X    |  X    |       |       |      |      |      |      |      |      |      |      |
UTB-14:JugadorRestControllerTest        |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |       |       |       |   X   |       |       |       |       |      |      |      |      |      |      |      |      |
| UTB-15:SalaEspera.test.js              |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |       |       |  X    |      |       |       |       |       |      |      |      |      |      |      |      |      |
| UTB-16:BazaRestControllerTest          |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |       |       |       |       |      |       |  X    |  X    |      |      |      |      |      |      |      |      |
| UTB-17:UserPartidas.test.js          |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |       |       |       |       |      |       |       |      |  X    |      |      |      |      |      |      |      |
| UTB-18:ChatControllerTest          |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |       |       |       |       |      |       |       |      |       |  X   |      |      |      |      |      |      |
| UTB-19:AmistadServiceTest          |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |       |       |       |       |      |       |       |      |       |      |  X   |  X   |  X   |  X   |      |      |
| UTB-20:InvitacionRestControllerTest          |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |       |       |       |       |      |       |       |      |       |      |      |      |      |      |  X   |  X   | 
UTB-21:AmistadRestControllerTest       |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |       |       |       |       |       |       |       |       |      |      |      |      |      |      |      |      |  X   |






## 6. Criterios de Aceptación

- Todas las pruebas unitarias deben pasar con éxito antes de la entrega final del proyecto.
- La cobertura de código debe ser al menos del 70%.
- No debe haber fallos críticos en las pruebas de integración y en la funcionalidad.

## 7. Conclusión

Este plan de pruebas establece la estructura y los criterios para asegurar la calidad del software desarrollado. Es responsabilidad del equipo de desarrollo y pruebas seguir este plan para garantizar la entrega de un producto funcional y libre de errores.
