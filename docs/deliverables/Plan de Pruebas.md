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
| HGJ3: Cerrar sesión         | [UTB-1:AuthControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/auth/AuthControllerTests.java#L122shouldNotValidateToken) | Verifica que un usuario puede cerrar sesión correctamente observando que su token ya no es válido tras cerrar sesión.                                      | Implementada  | De integración backend|
| HGJ4: Editar perfil de usuario        | [UTB-2:UserControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java#L187shouldUpdateUser) | Verifica que un usuario puede editar su perfil.                                                | Implementada  | De integración backend|
| HGA1: Listado usuarios registrados     | [UTB-3:UserServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserServiceTests.java#L55shouldFindAllUsers) | Verifica que se obtengan todos los usuarios registrados.                                        | Implementada  | Unitaria en backend   |
| HGA1: Listado usuarios registrados     | [UTB-11:UserListAdmin](../../frontend\src\admin\users\UserListAdmin.test.js) | Verifica que se obtengan y visualicen todos los usuarios registrados.                                        | Implementada  | En frontend   |
| HGA2: Borrar un usuario     | [UTB-2:UserControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java#L214shouldDeleteOtherUser) | Verifica que se pueda borrar un usuario.                                                       | Implementada  | Unitaria en backend   |
| HGA3: Editar un usuario     | [UTB-2:UserControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java#L187shouldUpdateUser) | Verifica que se pueda editar un usuario por un administrador.                                  | Implementada  | De integración backend|
| HGA4: Crear un usuario      | [UTB-2:UserControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java#L175shouldCreateUser) | Verifica que un administrador pueda crear un usuario.                                           | Implementada  | De integración backend|
| HJA1: Ver partidas filtradas| [UTB-4:PartidaServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/partida/PartidaServiceTest.java#L101testGetAllPartidasFiltradasPorNombreYEstado) | Verifica que se pueden obtener las partidas pudiendo filtrar por estado y/o nombre.            | Implementada  | Unitaria en backend   |
| HJA1: Ver partidas filtradas| [UTB-12:PartidaListAdmin.test](../../frontend\src\admin\partidas\PartidaListAdmin.test.js) | Verifica que se pueden obtener las partidas pudiendo filtrar por estado y/o nombre.                                          |  Implementada  | En frontend   |
| HJA2: Ver estadísticas      | [UTB-13:UserDashBoard.test](../../frontend\src\admin\users\UserDashboard.test.js) | Verifica que un usuario cualquiera puede ver las estadísticas de los usuarios.                      | Por implementar  | En frontend|
| HJJ1: Jugar una carta durante mi turno | [UTB-5:BazaRestControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/baza/BazaRestControllerTest.java#L225shouldCrearTrucosDeBaza) | Verifica que un jugador pueda jugar una carta en su turno. | Implementada | Unitaria en backend |
| HJJ2: Ver puntuación actualizada | [UTB-6:RondaServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/ronda/RondaServiceTest.java#L412shouldGetPuntaje) | Verifica que la puntuación se actualice correctamente tras cada ronda. | Implementada | Unitaria en backend |
| HJJ3: Usar cartas especiales | [UTB-7:ManoServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/mano/ManoServiceTest.java#L555shouldCartasDisabledConCartaEspecial) | Verifica que el jugador pueda usar cartas especiales. | Implementada | Unitaria en backend |
| HJJ4: Usar carta bandera blanca/de escape | [UTB-7:ManoServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/mano/ManoServiceTest.java#L555shouldCartasDisabledConCartaEspecial) | Verifica que el jugador pueda usar una carta bandera blanca/de escape. | Implementada | Unitaria en backend |
| HJJ5: Ver apuestas de la ronda | [UTB-7:ManoServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/mano/ManoServiceTest.java#L460shouldApuestaExito) | Verifica que el jugador pueda ver todas las apuestas realizadas en la ronda actual. | Implementada | Unitaria en backend |
| HJJ6: Elegir rol de la carta Tigresa | [UTB-7:ManoServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/mano/ManoServiceTest.java#L520shouldCartasDisabledManoNoEncontrada) | Verifica que el jugador pueda elegir el rol de la carta Tigresa. | Implementada | Unitaria en backend |
| HJJ7: Salir de la partida | [UTB-8:JugadorControllerTests](No implementada) | Verifica que el jugador pueda salir de una partida en mitad del juego. | Por implementar | Unitaria en backend |
| HJJ8: Conocer reglas de juego | [UTB-14:App.test](../../frontend\src\App.test.js) | Verifica que las reglas del juego sean accesibles. | Implementada | Prueba de interfaz en frontend |
| HJJ9: Crear partidas | [UTB-4:PartidaControllerTest](../../src\test\java\es\us\dp1\lx_xy_24_25\your_game_name\partida\PartidaControllerTest.java#L130shouldCreatePartida) | Verifica que se pueda crear una partida correctamente. | Implementada | Unitaria en backend |
| HJJ10: Hacer apuestas | [UTB-7:ManoServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/mano/ManoServiceTest.java#L460shouldApuestaExito) | Verifica que un jugador pueda hacer apuestas sobre cuántas bazas ganará. | Implementada | Unitaria en backend |
| HJJ11: Unirse a partida | [UTB-3:JugadorRestControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/jugador/JugadorRestControllerTest.java#shouldCreateJugador) | Verifica que se cree un jugador al unirse a una partida. | Implementada | Unitaria en backend |
| HJJ12: Iniciar partida creada | [UTB-4:PartidaServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/partida/PartidaServiceTest.java#L233testIniciarPartida) | Verifica que el usuario creador pueda iniciar la partida. | Implementada | Unitaria en backend |
| HJJ13: Ver jugadores en la sala | [UTB-4:PartidaControllerTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/partida/PartidaControllerTest.java#L181shouldGetJugadoresByPartidaId) | Verifica que se obtengan los jugadores de una partida. | Implementada | Unitaria en backend |
| HJJ14: Volver a partida empezada | [UTB-4:PartidaServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/partida/PartidaServiceTest.java#L412shouldUsuarioJugadorEnPartidaJugando) | Verifica que el jugador pueda volver a una partida en curso. | Implementada | Unitaria en backend |

### 5.3 Matriz de Trazabilidad entre Pruebas e Historias de Usuario

| Prueba                    |  HGJ1  |  HGJ2  |  HGJ3  |  HGJ4  |  HGA1  |  HGA2  |  HGA3  |  HGA4  |  HJA1  |  HJA2  |  HJJ8  |  HJJ9  |  HJJ10 |  HJJ11 |  HJJ12 |  HJJ13 |  HJJ14 |
|---------------------------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|
| UTB-1:AuthControllerTests |   X    |    X   |   X    |        |        |        |        |        |        |        |        |        |        |        |        |        |        |
| UTB-2:UserControllerTests |        |        |        |    X   |        |   X    |   X    |   X    |        |        |        |        |        |        |        |        |        |
| UTB-3:UserServiceTests    |        |        |        |        |   X    |        |        |        |        |        |        |        |        |        |        |        |        |
| UTB-4:PartidaServiceTest  |        |        |        |        |        |        |        |        |   X    |        |        |   X    |        |        |    X   |        |        |
| UTB-5:JugadorRestControllerTest |        |        |        |        |        |        |        |        |        |        |        |        |        |    X   |        |    X   |        |
| UTB-6:RondaServiceTest    |        |        |        |        |        |        |        |        |        |        |        |        |        |        |        |        |   X    |
| UTB-7:ManoServiceTest     |        |        |        |        |        |        |        |        |        |        |        |        |    X   |        |        |        |        |
| UTB-8:BazaControllerTest  |        |        |        |        |        |        |        |        |        |        |        |        |        |        |        |        |   X    |
| UTB-9:PartidaListAdmin    |        |        |        |        |        |        |        |        |   X    |        |        |        |        |        |        |        |        |
| UTB-10:UserDashBoard      |        |        |        |        |        |        |        |        |        |   X    |        |        |        |        |        |        |        |
| UTB-11:UserListAdmin      |        |        |        |        |   X    |        |        |        |        |        |        |        |        |        |        |        |        |
| UTB-12:App.test           |        |        |        |        |        |        |        |        |        |        |    X   |        |        |        |        |        |        |


## 6. Criterios de Aceptación

- Todas las pruebas unitarias deben pasar con éxito antes de la entrega final del proyecto.
- La cobertura de código debe ser al menos del 70%.
- No debe haber fallos críticos en las pruebas de integración y en la funcionalidad.

## 7. Conclusión

Este plan de pruebas establece la estructura y los criterios para asegurar la calidad del software desarrollado. Es responsabilidad del equipo de desarrollo y pruebas seguir este plan para garantizar la entrega de un producto funcional y libre de errores.
