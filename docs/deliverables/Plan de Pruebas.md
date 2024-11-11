# Plan de Pruebas

## 1. Introducción

Este documento describe el plan de pruebas para el proyecto **Skull King** desarrollado en el marco de la asignatura **Diseño y Pruebas 1** por el grupo **L5-07**. El objetivo del plan de pruebas es garantizar que el software desarrollado cumple con los requisitos especificados en las historias de usuario y que se han realizado las pruebas necesarias para validar su funcionamiento.

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

| Historia de Usuario | Prueba | Descripción | Estado |Tipo |
|---------------------|--------|-------------|--------|--------|
| HGJ2: Iniciar sesión | [UTB-1:UserServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserServiceTests.java#L45shouldNotFindCorrectCurrentUser) | Verifica que un usuario puede iniciar sesión con credenciales válidas. | Implementada | Unitaria en backend |
| HGJ1: Registrar usuario | [UTB-1:UserServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserServiceTests.java#L118shouldInsertUser) | Verifica que un nuevo usuario puede registrarse en el sistema. | Implementada |Unitaria en backend |
| HGA1: Listado usuarios registrados | [UTB-1:UserServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserServiceTests.java#L55shouldFindAllUsers) | Verifica que se obtengan todos los usuarios registrados. | Implementada | Unitaria en backend |
| HGA2: Borrar un usuario | [UTB-1:UserServiceTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java#214shouldDeleteOtherUser) | Verifica que se pueda borrar un usuario. | Implementada | Unitaria en backend |
| HGA3: Editar un usuario | [UTB-2:UserControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserControllerTests.java#214shouldDeleteOtherUser) | Verifica que se pueda editar un usuario. | Implementada | Unitaria en backend |
| HGA4: Crear un usuario | [UTB-2:UserControllerTests](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/UserServiceTests.java#L118shouldInsertUser) | Verifica que el administrador pueda crear un usuario. | Implementada | Unitaria en backend |
| HJJ13: Ver jugadores en la sala | [UTB-3:JugadorServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/JugadorServiceTest.java#LshouldFindAllJugadoresByPartidaId) | Verifica que se obtengan los jugadores de una partida. | Implementada | Unitaria en backend |
| HJJ9: Crear partida | [UTB-4:PartidaServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/PartidaServiceTest.java#L151testSavePartida) | Verifica que se cree una partida correctamente. | Implementada | Unitaria en backend |
| HJJ12: Iniciar partida creada | [UTB-4:PartidaServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/PartidaServiceTest.java#L219testIniciarPartidaConTresOMasJugadores) | Verifica que el usuario creador pueda iniciar la partida. | Implementada | Unitaria en backend |
| HJA1: Ver listado partidas filtradas | [UTB-4:PartidaServiceTest](../../src/test/java/es/us/dp1/lx_xy_24_25/your_game_name/user/PartidaServiceTest.java#L113testGetAllPartidasFiltrado) | Verifica que se pueden obtener las partidas pudiendo filtrar por estado y/o nombre. | Implementada | Unitaria en backend |
| HJJ8: Conocer reglas del juego | [UTB-5:FrontendTest](Por implementar) | Verifica que se cree una partida correctamente. | Por implementar | En frontend |

### 5.3 Matriz de Trazabilidad entre Pruebas e Historias de Usuario

| Prueba                    |  HGJ2  |  HGJ1  |  HGA1  |  HGA2  |  HGA3  |  HGA4  |  HJJ13 |  HJJ9  |  HJJ12 |  HJA1  |  HJJ8  |
|---------------------------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|--------|
| UTB-1:UserServiceTests    |   X    |    X   |    X   |    X   |        |   X    |        |        |        |        |        |
| UTB-2:UserControllerTests |        |        |        |        |    X   |        |        |        |        |        |        |
| UTB-3:JugadorServiceTest  |        |        |        |        |        |        |    X   |        |        |        |        |
| UTB-4:PartidaServiceTest  |        |        |        |        |        |        |        |    X   |    X   |    X   |        |
| UTB-5:FrontendTest        |        |        |        |        |        |        |        |        |        |        |    X   |

## 6. Criterios de Aceptación

- Todas las pruebas unitarias deben pasar con éxito antes de la entrega final del proyecto.
- La cobertura de código debe ser al menos del 70%.
- No debe haber fallos críticos en las pruebas de integración y en la funcionalidad.

## 7. Conclusión

Este plan de pruebas establece la estructura y los criterios para asegurar la calidad del software desarrollado. Es responsabilidad del equipo de desarrollo y pruebas seguir este plan para garantizar la entrega de un producto funcional y libre de errores.
