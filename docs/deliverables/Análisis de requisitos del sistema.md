# Documento de análisis de requisitos del sistema

## Introducción

Este proyecto pretende ofrecer la posibilidad de jugar de forma online al juego de mesa Skull King, juego de cartas con temática pirata en el cual deberás apostar el número exacto de bazas que crees que ganarás en cada ronda. El programa permite a la persona interesada crear un usuario con el que será capaz de contactar con otros jugadores e iniciar una partida. 

Esta adaptación tiene como límite 10 jugadores en partida. Además, no se podrá comenzar una partida a menos que haya 3 usuarios en esta.

### EXPLICACIÓN DEL JUEGO:

En el comienzo de la partida se realiza el primer reparto de cartas. El número de cartas vendrá determinado por la ronda en la que se encuentre la partida, por ejemplo: ronda 3, se reparten 3 cartas, una por cada baza. Existe una excepción en las partidas de más de 8 jugadores, pues en alguna ronda no se podrá aplicar la regla anterior ya que no hay suficientes cartas. Por ello, se repartirá el máximo de cartas posibles a cada jugador, siendo estas la misma cantidad.

Cada jugador jugará su carta, y siguiendo la jerarquía se determinará el ganador de cada baza. Una vez jugadas las bazas que forman la ronda, se realizará el cálculo de puntos, incluyendo los puntos de bonificación.

Finalmente, el ganador se definirá a partir de la suma de las puntuaciones de las 10 rondas que conforman la partida, la cual tiene una duración media de 30 minutos. 

### JERARQUÍA DE CARTAS:

En la cúspide de la jerarquía se encuentran las cartas de personaje: pirata, sirena y Skull King. Estos mantienen una relación parecida al juego “piedra, papel y tijera”; el pirata gana a la sirena, el Skull King al pirata y la sirena al Skull King. Además, debemos tener en cuenta la carta tigresa, pues puede ejercer dos papeles, pirata o bandera blanca. 

Siguiendo la jerarquía, hallamos el palo de triunfos con cartas numeradas del 1 al 14. Por debajo, se sitúan el resto de palos, morado, amarillo y verde, numerados de la misma forma. Finalmente, se encuentran las banderas blancas, las cuales pierden la baza independientemente del resto de cartas jugadas.

### CARTAS:

#### Bandera Blanca
![Bandera Blanca](/src/main/resources/static/resources/images/cartas/bandera_blanca.png)

 Las 5 cartas de bandera blanca pueden ser jugadas para ‘no ganar’ al considerarse de valor 0. Pierden con el resto de cartas. Son muy útiles para aseguraros no ganar más bazas de las que se han pujado.

#### Pirata
![Pirata](/src/main/resources/static/resources/images/cartas/pirata_1.png)

Las cartas de Pirata superan a todas las cartas numeradas. Todas son de igual valor, por lo que si se juegan más de una en la misma baza, quien haya jugado el primer pirata ganará la baza.

#### Tigresa
![Tigresa](/src/main/resources/static/resources/images/cartas/tigresa.png)

Al jugar la Tigresa, debéis declarar si contará como Pirata o como Huida. Asume todas la características de un pirata o de una huida.

#### Skull King
![Skull King](/src/main/resources/static/resources/images/cartas/skull_king.png)

El Skull King vence a todas las cartas numeradas y todos los Piratas (incluida la Tigresa al ser jugada como Pirata). Las únicas que puede derrotarle son las Sirenas.

#### Sirena
![Sirena](/src/main/resources/static/resources/images/cartas/sirena_1.png)

Las Sirenas vencen a todos los palos numerados pero pierden con todos los Piratas,con la excepción del Skull King. Si se juegan ambas Sirenas en la misma baza, la primera que fue jugada ganará la baza.

#### Palos normales (amarillo, verde y morado)
![Palo amarillo](/src/main/resources/static/resources/images/cartas/amarilla_14.png)

![Palo verde](/src/main/resources/static/resources/images/cartas/morada_9.png)

![Palo morado](/src/main/resources/static/resources/images/cartas/verde_2.png)

Hay tres palos normales: amarillo (Cofre del Tesoro), verde (Loro) y morado (Mapa del Tesoro).

#### Palo Triunfo
![Palo Triunfo](/src/main/resources/static/resources/images/cartas/triunfo_4.png)

Las cartas triunfo son superiores a la de las otras tres palos.

### PUNTUACIÓN:

A la hora de apostar se decide cómo se ganarán puntos. Por un lado, se puede apostar todo a perder, el jugador deberá perder todas las bazas. En el caso de acertar su apuesta se multiplicará por 10 el número de cartas de la ronda, y si se equivoca, se multiplicará por -10 el número anterior. Por otro lado, se puede realizar una apuesta normal en la cual las bazas ganadas se multiplicarán por 20 si se acierta la apuesta, en caso de que esta sea errónea, se multiplicará por -10 aquellas bazas no acertadas.

Una vez realizado dicho cálculo de puntos se sumarán los puntos de bonificación si y sólo si se ha acertado la apuesta. Para ello, se deberá analizar las bazas ganadas de cada jugador. 

Si se halla alguna carta 14 de los palos normales, se deberá sumar 10 puntos por cada una, y en el caso de encontrar el 14 del palo de triunfos, se sumarán 20 puntos. Para calcular los puntos que se obtienen con las cartas de personaje se deberá tener en cuenta las relaciones: 20 puntos por cada sirena ganada por el pirata, 30 puntos por cada pirata ganado por el Skull King, y 40 puntos si la sirena ha ganado al Skull King.

[Enlace al vídeo de explicación de las reglas del juego / partida jugada por el grupo](https://youtu.be/CV1VBWZAIJM)

## Tipos de Usuarios / Roles

Jugador: Usuario interesado en jugar al juego de mesa SkullKing desde su dispositivo

Administrador: Persona autorizada a intervenir entre los usuarios y a acceder a la información registrada en la aplicación de las partidas en curso y las finalizadas y de los usuarios registrados.

## Historias de Usuario

A continuación se definen todas las historias de usuario a implementar:

### HGJ1-(ISSUE#49): Registrar usuarios ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/49#issue-2565870591]())
| "Como jugador quiero poder registrarme en el juego para poder jugar al mismo."| 
|-----|
|![Registro](../mockups/Registro.png)|
|![RegistroJugador](../mockups/Datos_registro.png)|
|Puedes presionar el boton "player" para registrate como jugador, esto te enviará a un formulario en el cual simplemente debes introducir los datos indicados y presionar "save" para guardar tu perfil.|
|Caso negativo: Si el usuario ya existe, se mostrará un error en el que se indicará que el usuario está ocupado.|

 ### HGJ2-(ISSUE#50): Hacer log-in ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/50#issue-2565890369]())
| "Como jugador quiero poder loguearme en el juego para jugar desde mi usuario."| 
|-----|
|![Login](../mockups/Login.png)|
|Tienes que rellenar los datos solicitados para verificar que efectivamente es tu usuario, finalmente puedes presionar "Log-in", en el cual si los datos son correctos entrarás en tu perfil, en caso contrario deberás introducirlos nuevamente.|
|Caso negativo: Si el usuario o la contraseña son incorrectos, se mostrará un error en el que se indicará que los datos de inicio de sesión son incorrectos.|

 ### HGJ3-(ISSUE#51): Hacer log-out ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/51#issue-2565892501]())
| "Como jugador quiero poder hacer log-out para salirme de mi usuario y evitar que otras personas lo usen"| 
|-----|
|![Logout](../mockups/Logout.png)|
|Siempre y cuando estés logado, aparecerá en el navbar el botón "log-out", una vez presionado ya no estarás logado en tu perfil.|

 ### HGJ4-(ISSUE#52): Editar perfil de usuario  ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/52#issue-2565901340]())
| "Como jugador quiero poder editar mi perfil para poner mi información correctamente."| 
|-----|
|![Editar perfil](../mockups/Editar_perfil.png)|
|  |

 ### HGA1-(ISSUE#30): Listado usuarios registrados ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/30#issue-2561421045]())
| "Como administrador quiero ver el listado de usuarios registrados para llevar un control sobre la cantidad de usuarios en el sistema."| 
|-----|
|![Listado de usuarios](../mockups/Listado_usuarios.png)|
|Se despliega una lista de todos los usuarios registrados en el sistema.|

 ### HGA2-(ISSUE#53): Borrar un usuario ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/53#issue-2565933479]())
| "Como administrador quiero poder borrar usuarios para llevar un control adecuado de los usuarios del sistema."|
|-----|
|![Borrar usuario](../mockups/Listado_usuarios.png)|
|En la lista de usuarios tienes las opciones para editar los datos de los usuarios usando el boton "edit", crear uno con el boton "add user" y eliminarlo usando "delete", ademas haciendo click en el nombre del usuario puedes ver su perfil.|

 ### HGA3-(ISSUE#54): Editar un usuario ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/54#issue-2565956464]())
| "Como administrador quiero poder editar el perfil de un usuario para ayudar a los jugadores que hayan olvidado sus credenciales."|
|-----|
|![Edición jugador](../mockups/Cambio_de_credenciales.png)|
|En el formulario que aparece puedes modificar los datos del usuario especificado.|

 ### HGA4-(ISSUE#53): Crear un usuario ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/53#issue-2565933479]())
| "Como administrador quiero poder crear un usuario para llevar una administración del sistema"|
|-----|
|![Creación jugador](../mockups/Listado_usuarios.png)|
|Pulsando el botón "Add User", el administrador deberá rellenar los datos correspondientes y confirmar la creación.|

### HJJ8-(ISSUE#47): Conocer reglas de juego ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/47#issue-2565864686]())
| "Como jugador quiero conocer las reglas del juego para poder jugar correctamente."| 
|-----|
|![Ver instrucciones](../mockups/Ver_Instrucciones.png)|
|Puedes acceder al pdf con todas las reglas del juego e incluso descargar dicho pdf para tenerlo en tu computadora.|

 ### HJJ13-(ISSUE#109): Ver jugadores en la sala ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/109]())
| "Como jugador quiero poder ver los jugadores que se unen a la partida para poder saber con quienes voy a jugar."| 
|-----|
|![Lobby](../mockups/iniciar_partida.png)|
|Una vez el jugador se encuentre en la sala de espera de la partida, el jugador podrá ver los jugadores que hay en la partida. Estos se van actualizando conforme se van uniendo.|

 ### HJJ9-(ISSUE#48): Crear partidas ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/48#issue-2565866251]())
| "Como jugador quiero poder crear partidas para poder jugar."| 
|-----|
|![Lobby](../mockups/Lobby_partidas.png)|
|Puedes presionar en el botón "Crear partida" para crear una sala en la que las personas puedan entrar.|
|Caso negativo: Si ya estás en una partida en estado "JUGANDO" o "ESPERANDO", se lanzará una excepción y no podrás crear otra partida.|

 ### HJJ11-(ISSUE#106): Unirse a partida ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/106]())
| "Como jugador quiero poder unirme a una partida para poder jugar."| 
|-----|
|![Lobby](../mockups/Lobby_partidas.png)|
|Puedes presionar en el botón "Unirse a partida" para unirte a una partida que esté en estado "ESPERANDO".|
|Caso negativo: Si ya estás en una partida en estado "JUGANDO" o "ESPERANDO", se lanzará una excepción y no podrás unirte a otra partida.|

 ### HJJ12-(ISSUE#108): Iniciar a partida creada ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/108]())
| "Como jugador quiero poder iniciar una partida que he creado para poder jugar."| 
|-----|
|![Lobby](../mockups/iniciar_partida.png)|
|Una vez el jugador se encuentre en la sala de espera de la partida, si ha creado la partida, puede presionar el botón "Iniciar Prtida".|
|Caso negativo: Si en la partida hay menos de 3 jugadores, no se podrá iniciar, aparecerá una excepción que lo indique.|

### HJJ12-(ISSUE#108): Volver a partida empezada ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/108]())
| "Como jugador quiero poder volver a la partida empezada anteriormente."| 
|-----|
|![Lobby](../mockups/volverAPartida.png)|
|Una vez el jugador se encuentre en una partida en espera o empezada, puede presionar el botón "Volver a Partida".|

 ### HJJ10-(ISSUE#43): Hacer apuestas ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/43]())
| "Como jugador quiero poder hacer apuestas sobre cuántas bazas ganaré para poder planificar mi estrategia."| 
|-----|
|![Hacer apuestas](../mockups/Hacer_apuesta_sobre_cuantas_bazas_ganare.png)|
|Al inicio de cada ronda aparecerá una ventana emergente con botones del 0 al número de cartas repartidas en la ronda actual, el jugador deberá pulsar uno de los números en función de la predicción de bazas que cree que va a ganar en la ronda.|

 ### HJA1-(ISSUE#44): Ver listado de partidas filtradas ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/44]())
| "Como administrador quiero poder ver el listado de partidas, incluyendo nombre de la partida, creador, estado y jugadores participantes para llevar un control sobre las partidas."| 
|-----|
|![Listado de partidas en curso](../mockups/listado_partidas.png)|
|El administrador, en la parte superior de la pantalla, tendrá un botón 'Partidas'. Dentro de esta pantalla encontrará una barra de búsqueda para filtrar las partidas por nombre. Además, encontrará diversos botones para filtrar en función del estado de las partidas.|

### HJA2-(ISSUE#177): Ver estadísticas usuarios ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/177]())
| "Como administrador quiero poder ver las estadísticas de los usuarios para saber cuáles son los datos de partidas de los usuarios."| 
|-----|
|![Ver estadísiticas usuarios](../mockups/ver_estadisticas_usuarios.png)|
|El administrador podrá ver las estadísitcas en la pestaña con este mismo nombre.|

### HJJ5-(ISSUE#40): Ver apuestas de la ronda ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/40]())
| "Como jugador quiero poder ver todas las apuestas realizadas en la ronda actual para poder perjudicar las apuestas de otros jugadores."| 
|-----|
|![Ver apuestas de la ronda](../mockups/Ver_apuestas_para_hacer_estrategia.png)|
|El jugador podrá observar las apuestas de todos los jugadores actualizada tras cada ronda en el margen izquierdo de la pantalla. Esta se mantendrá visible durante toda la ronda hasta que se actualice en la siguiente ronda.|

 ### HJJ1-(ISSUE#36): Jugar una carta durante mi turno ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/36]())
| "Como jugador quiero poder jugar una carta durante mi turno para intentar ganar una baza."| 
|-----|
|![Jugar una carta durante mi turno](../mockups/Poder_jugar_una_carta.png)|
|El jugador selecciona una de sus cartas disponibles siempre y cuando el juego le permita echarla en base a las reglas del juego, si no es posible, el jugador deberá escoger otra de sus cartas.|

 ### HJJ3-(ISSUE#38): Usar cartas especiales ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/38]())
| "Como jugador quiero poder usar cartas especiales (pirata, sirena o Skull King) en lugar del palo predominante para intentar ganar una baza."| 
|-----|
|![Usar cartas especiales](../mockups/Usar_cartas_especiales.png)|
|El jugador selecciona una carta especial de su mano durante su turno, podrá echarla independientemente del palo que predomine durante la baza.|

 ### HJJ4-(ISSUE#39): Usar carta blanca/de escape ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/39]())
| "Como jugador quiero poder usar una carta blanca/de escape para perder una baza y acertar mi apuesta."| 
|-----|
|![Usar carta blanca/de escape](../mockups/Jugar_carta_escape.png)|
|El jugador selecciona una carta blanca/de escape de su mano durante su turno, podrá echarla independientemente del palo que predomine durante la baza.|

 ### HJJ6-(ISSUE#41): Elegir rol de la carta Tigresa ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/41]())
| "Como jugador quiero poder escoger el rol de la carta Tigresa (bandera blanca o pirata) para poder cumplir con mi apuesta."| 
|-----|
|![Elegir rol de la carta Tigresa](../mockups/Elegir_la_carta_Tigresa.png)|
|El jugador selecciona la carta Tigresa, aparece una ventana emergente donde le aparecerán dos botones con cada una de las opciones, deberá pulsar una de los dos para elegir el rol. Una vez pulsado el rol quedará seleccionado.|

### HJJ2-(ISSUE#37): Ver puntuación actualizada ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/37]())
| "Como jugador quiero poder ver mi puntuación actualizada tras cada ronda para saber si voy ganando o no."| 
|-----|
|![Ver puntuación actualizada](../mockups/Ver_puntuacion.png)|
|El jugador podrá observar su puntuación y la de sus compañeros actualizada tras cada ronda en el margen izquierdo de la pantalla. Esta se mantendrá visible durante toda la ronda siguiente hasta que se actualice al final de la ronda.|

### HJJ14-(ISSUE#175): Ver ganador baza ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/175]())
| "Como jugador quiero poder ver el ganador de la baza tras su finalización para saber si quién ha sido el ganador."| 
|-----|
|![Ver ganador baza](../mockups/ver_ganador_baza.png)|
|El jugador podrá observar el ganador de cada baza tras su finalización (una vez todos los jugadores hayan jugador su correspondiente carta) a trvés de un modal que aparecerá automaticamente en pantalla. Se cerrará al pasar a la siguiente baza o ronda.|

 ### HJJ7-(ISSUE#42): Salir de la partida ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/42]())
| "Como jugador quiero poder rendirme y salir en mitad de la partida para evitar seguir jugando en caso de que me surja un imprevisto."| 
|-----|
|![Salir de la partida](../mockups/Poder_salir_en_mitad_de_la_partida.png)|
|El jugador debe pulsar el botón 'Exit', situado en la esquina inferior derecha.|


## Diagrama conceptual del sistema
En esta sección proporcionamos un diagrama UML de clases que describe el modelo de datos a implementar en la aplicación. Este diagrama esta anotado con las restricciones simples de los datos a gestionar por la aplicación.

![Diagrama UML conceptual](/docs/diagrams/Modelo%20conceptual.png)


## Reglas de Negocio
### R0 - Cartas en la baraja 
Las cartas que conforman las manos de una ronda concreta no se pueden repetir.

### R1 - Bazas de vuelta a la baraja  
Al finalizar cada baza, esta permanecerá en la mesa del jugador ganador. Una vez finalizada la ronda, todas las bazas vuelven a la baraja.

### R2 - Jugadores en partida  
Previo al comienzo de la partida se deberá tener como mínimo 3 jugadores. Al cumplir con esta condición se podrá dar inicio al juego. A su vez, será necesario comprobar que no se supere la cantidad de 8 jugadores.

### R3 - Número de cartas en cada ronda normal  
El número de cartas a repartir estará determinado por el número de ronda en la que se encuentre la partida, siendo la última la ronda 10.  
Existe un caso especial en las partidas de más de 6 jugadores. En este caso, la partida alcanzará un punto en el que no se podrán repartir el mismo número de cartas que de ronda. Por ello, se repartirán el máximo de cartas posibles a cada jugador, siendo estas la misma cantidad.

### R4 - Realizar apuesta  
Al comenzar cada ronda, posterior al reparto de cartas, en función de tu mano se deberá hacer una apuesta.

### R5 - Apuesta válida  
La apuesta de cada jugador deberá ser menor o igual al número de cartas en mano y mayor o igual a 0.

### R6 - Obtención de puntos  
La única forma de obtener puntos en el juego es acertar la apuesta realizada a inicios de la ronda.

### R7 - Obtención de puntos de bonificación  
Para obtener puntos de bonificación se deberá acertar la apuesta realizada a inicios de ronda.

### R8 - Palo de la baza  
La definición del palo de la ronda vendrá determinada por la carta tirada en primer lugar.

### R9 - Palo de la baza: bandera blanca  
La definición del palo de la ronda vendrá determinada por la carta tirada tras la bandera blanca. En caso de que se emplee esta carta nuevamente, el palo será definido por el siguiente jugador.

### R10 - Palo de la baza: carta personaje  
En caso de que el primer jugador juegue una carta de personaje, la baza no tendrá un palo principal.

### R11 - Palo principal  
El jugador deberá jugar una carta del palo principal, en caso de no tener, se tirará cualquier carta de su elección.

### R12 - Cartas especiales  
Si el jugador posee alguna carta especial, este podrá jugarla independientemente del palo principal.

### R13 - Carta de huida  
Al jugar una carta de huida se perderá la baza en la que se juegue, a menos que todas las cartas jugadas en la misma baza sean de huida o Tigresa como un huida, pues la primera carta jugada ganará la baza.

### R14 - Carta de personajes: Tigresa  
Si se quiere jugar la carta Tigresa, una vez puesta en mesa, se deberá elegir el tipo de papel que interpretará, bandera blanca o pirata.

### R15 - Definición del primer jugador  
Al iniciar la partida se elegirá de forma aleatoria al jugador que comenzará la baza de la primera ronda. En cada baza nueva, el jugador que comenzará será el siguiente al que comenzó en la baza anterior, siguiendo el sentido horario.

### R16 - Turnos  
El turno de cada jugador vendrá determinado por el sentido horario.

### R17 - Ganador de baza  
El ganador de la baza será aquel cuyo truco sea el más alto, respetando la jerarquía.

### R18 - Ganador de la partida  
El ganador de la partida será aquel cuya puntaje sea el mayor en comparación con el resto de jugadores.

### R19 - Finalización de ronda  
La ronda finaliza una vez se jueguen las bazas de dicha ronda.

### R20 - Finalización de partida  
La partida finaliza una vez se haya calculado la puntuación final.

### R21 - Cálculo de puntos de ronda  
El cálculo de los puntos de la ronda se realizará al finalizar esta.

### R22 - Usuario un jugador por partida
Cada usuario tendrá asociado un solo jugador en una partida.

### R23 - Usuario jugador distinto por partida
El jugador asociado al usuario será distinto en cada partida.

### R24 - Usuario con varios jugadores  
El usuario en la aplicación, a medida que juegue partidas podrá tener distintos jugadores. Sin embargo, nunca podrán haber dos jugadores del mismo usuario jugando al mismo tiempo, es decir, un usuario no podrá tener varios jugadores que se encuentren en partidas con estado "ESPERANDO" o "JUGANDO".

### R25 - 3 Cartas de personajes distintas  
En el caso de que en mesa se hayan jugado las 3 cartas de personaje (Skull King, Pirata y Sirena), la primera sirena jugada ganará la ronda.

### R26 - Todas las cartas banderas blancas  
En el caso de que en mesa todos los jugadores hayan jugado bandera blanca (también puede ser la tigresa jugada como bandera blanca), ganará el truco tirada en primer lugar.

### R27 - Relación Skull King - Pirata  
En el caso de que en mesa se haya jugado un Skull King y uno o varios piratas, ganará el Skull King.

### R28 - Relación Pirata - Sirena  
En el caso de que en mesa se hayan jugado uno o varios piratas y una o varias sirenas, ganará el primer pirata jugado.

### R29 - Cartas triunfo  
En el caso de que no se haya jugado ninguna carta de personaje y haya en mesa uno o más triunfos, ganará el de mayor número.

### R30 - Cartas del palo principal  
En el caso de que no se haya jugado ninguna carta de personaje ni triunfo, ganará el truco de mayor número del palo principal.

### R31 - Apostar a ganar: acierto de apuesta  
Si el jugador establece que ganará al menos 1 baza, y acierta la apuesta, el número de bazas ganadas se multiplicará por 20 puntos.

### R32 - Apostar a ganar: apuesta no acertada  
Si el jugador establece que ganará al menos 1 baza, y no acierta la apuesta, el número de bazas apostadas a ganar y no se han ganado se multiplicará por -10 puntos.

### R33 - Apostar a perder: acierto de apuesta  
Si el jugador establece que perderá todas las bazas de la ronda, y acierta la apuesta, el número de bazas de la ronda se multiplicará por 10 puntos.

### R34 - Apostar a perder: apuesta no acertada  
Si el jugador establece que perderá todas las bazas de la ronda, y no acierta la apuesta, el número de bazas de la ronda se multiplicará por -10 puntos.

### R35 - Puntos de bonificación: carta 14 de palo  
Una vez se compruebe que se ha acertado la apuesta, si el jugador ha ganado alguna baza que incluya alguna carta 14 de los palos normales, se sumarán 10 puntos por cada una a su puntuación de la ronda.

### R36 - Puntos de bonificación: carta 14 de triunfo  
Una vez se compruebe que se ha acertado la apuesta, si el jugador ha ganado alguna baza que incluya alguna carta 14 de triunfo, se sumarán 20 puntos a su puntuación de la ronda.

### R37 - Puntos de bonificación: sirena capturada  
Una vez se compruebe que se ha acertado la apuesta, si el jugador ha ganado alguna baza que incluya alguna carta de sirena, se sumarán 20 puntos por cada una a su puntuación de la ronda.

### R38 - Puntos de bonificación: pirata capturado  
Una vez se compruebe que se ha acertado la apuesta, si el jugador ha ganado alguna baza que incluya alguna carta de pirata, se sumarán 20 puntos por cada uno a su puntuación de la ronda.

### R39 - Puntos de bonificación: Skull King capturado  
Una vez se compruebe que se ha acertado la apuesta, si el jugador ha ganado alguna baza que incluya la carta de Skull King, se sumarán 40 puntos a su puntuación de la ronda.

### R40 - Puntos de bonificación: 3 cartas de personaje distintos  
Una vez se compruebe que se ha acertado la apuesta, si el jugador ha ganado alguna baza que incluya los 3 tipos de personaje, se aplicará la regla de puntos de bonificación de Skull King capturado.

### R41 - Nombre partida
No pueden existir dos partidas en curso o en espera con el mismo nombre.

### R42 - Tiempo de apuesta
Tras el reparto de cartas se deberá apostar dentro del tiempo establecido (20 segundos), en otro caso, la apuesta se establecerá a 0.
