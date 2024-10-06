# Documento de análisis de requisitos del sistema

_Esta es una plantilla que sirve como guía para realizar este entregable. Por favor, mantén las mismas secciones y los contenidos que se indican para poder hacer su revisión más ágil._ 

## Introducción

_En esta sección debes describir de manera general cual es la funcionalidad del proyecto a rasgos generales. ¿Qué valor puede aportar? ¿Qué objetivos pretendemos alcanzar con su implementación? ¿Cuántos jugadores pueden intervenir en una partida como máximo y como mínimo? ¿Cómo se desarrolla normalmente una partida?¿Cuánto suelen durar?¿Cuando termina la partida?¿Cuantos puntos gana cada jugador o cual es el criterio para elegir al vencedor?_

[Enlace al vídeo de explicación de las reglas del juego / partida jugada por el grupo](http://youtube.com)

## Tipos de Usuarios / Roles

< Nombre Rol >: < Breve descripción del rol >

_Ej1: Propietario: Dueño de una o varias mascota que viene a la clínica para mantenerla sana y cuidar de su salud._

_Ej2: Veterinario: Profesional de la salud animal con titulación universitaria homologada, está registrado en la clínica y se encarga de realizar diagnósticos y recetar tratamientos. Además mantiene actualizado el vademécum._

_Ej3: Administrador: Dueño de la clínica que se encarga de dar de alta veterinarios y gestionar la información registrada en la aplicación de la clínica._



## Historias de Usuario

A continuación se definen todas las historias de usuario a implementar:

 ### HJJ1-(ISSUE#36): Jugar una carta durante mi turno ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/36]())
| "Como jugador quiero poder jugar una carta durante mi turno para intentar ganar una baza."| 
|-----|
|![Jugar una carta durante mi turno](../mockups/Poder_jugar_una_carta.png)|
|El jugador selecciona una de sus cartas disponibles siempre y cuando el juego le permita echarla en base a las reglas del juego, si no es posible, el jugador deberá escoger otra de sus cartas.|

 ### HJJ2-(ISSUE#37): Ver puntuación actualizada ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/37]())
| "Como jugador quiero poder ver mi puntuación actualizada tras cada ronda para saber si voy ganando o no."| 
|-----|
|![Ver puntuación actualizado](../mockups/Ver_puntuacion.png)|
|El jugador podrá observar su puntuación y la de sus compañeros actualizada tras cada ronda en el margen izquierdo de la pantalla. Esta se mantendrá visible durante toda la ronda siguiente hasta que se actualice al final de la ronda.|

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

 ### HJJ5-(ISSUE#40): Ver apuestas de la ronda ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/40]())
| "Como jugador quiero poder ver todas las apuestas realizadas en la ronda actual para poder perjudicar las apuestas de otros jugadores."| 
|-----|
|![Ver apuestas de la ronda](../mockups/Ver_apuestas_para_hacer_estrategia.png)|
|El jugador podrá observar las apuestas de todos los jugadores actualizada tras cada ronda en el margen izquierdo de la pantalla. Esta se mantendrá visible durante toda la ronda hasta que se actualice en la siguiente ronda.|

 ### HJJ6-(ISSUE#41): Elegir rol de la carta Tigresa ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/41]())
| "Como jugador quiero poder escoger el rol de la carta Tigresa (bandera blanca o pirata) para poder cumplir con mi apuesta."| 
|-----|
|![Elegir rol de la carta Tigresa](../mockups/Elegir_la_carta_Tigresa.png)|
|El jugador selecciona la carta Tigresa, aparece una ventana emergente donde le aparecerán dos botones con cada una de las opciones, deberá pulsar una de los dos para elegir el rol. Una vez pulsado el rol quedará seleccionado.|

 ### HJJ7-(ISSUE#42): Salir de la partida ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/42]())
| "Como jugador quiero poder rendirme y salir en mitad de la partida para evitar seguir jugando en caso de que me surja un imprevisto."| 
|-----|
|![Salir de la partida](../mockups/Poder_salir_en_mitad_de_la_partida.png)|
|El jugador debe pulsar el botón 'Exit', situado en la esquina inferior derecha.|

### HJJ8-(ISSUE#47): Conocer reglas de juego ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/47#issue-2565864686]())
| "Como Jugador quiero conocer las reglas del juego para poder jugar correctamente."| 
|-----|
|![Ver instrucciones](../mockups/Ver_Instrucciones.png)|
|Puedes acceder al pdf con todas las reglas del juego e incluso descargar dicho pdf para tenerlo en tu computadora.|

 ### HJJ9-(ISSUE#48): Crear partidas ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/48#issue-2565866251]())
| "Como jugador quiero poder crear partidas para poder jugar."| 
|-----|
|![Lobby](../mockups/Lobby_partidas.png)|
|Puedes presionar en el botón "crear partida", para crear una sala en la que las personas puedan entrar.|

 ### HJJ10-(ISSUE#43): Hacer apuestas ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/43]())
| "Como jugador quiero poder hacer apuestas sobre cuántas bazas ganaré para poder planificar mi estrategia."| 
|-----|
|![Hacer apuestas](../mockups/Hacer_apuesta_sobre_cuantas_bazas_ganare.png)|
|Al inicio de cada ronda aparecerá una ventana emergente con botones del 0 al número de cartas repartidas en la ronda actual, el jugador deberá pulsar uno de los números en función de la predicción de bazas que cree que va a ganar en la ronda.|

 ### HJA1-(ISSUE#44): Ver listado de partidas en curso ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/44]())
| "Como administrador quiero poder ver el listado de partidas en curso, incluyendo creador y usuarios participantes para llevar un control sobre las partidas en curso."| 
|-----|
|![Listado de partidas en curso](../mockups/Listado_partidas_en_curso.png)|
|El administrador, en la parte superior de la pantalla, tendrá un desplegable 'Partidas' del que salen las categorías 'En Curso' y 'Terminadas'. Debe pulsar en 'En Curso', esto le llevará a una pantalla con el listado de partidas en curso.|

### HJA2-(ISSUE#45): Ver listado de partidas finalizadas ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/45]())
| "Como administrador quiero poder ver el listado de partidas finalizadas, incluyendo creador y usuarios participantes para llevar un control sobre las partidas ya finalizadas."| 
|-----|
|![Listado de partidas finalizadas](../mockups/Listado_partidas_terminadas.png)|
|El administrador, en la parte superior de la pantalla, tendrá un desplegable 'Partidas' del que salen las categorías 'En Curso' y 'Terminadas'. Debe pulsar en 'Terminadas', esto le llevará a una pantalla con el listado de partidas ya finalizadas.|

 ### HGJ1-(ISSUE#49): Registrar usuarios ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/49#issue-2565870591]())
| "Como jugador quiero poder registrarme en el juego para poder jugar al mismo."| 
|-----|
|![Registro](../mockups/Registro.png)|
|![RegistroJugador](../mockups/Datos_registro.png)|
|Puedes presionar el boton "player" para registrate como jugador, esto te enviará a un formulario en el cual simplemente debes introducir los datos indicados y presionar "save" para guardar tu perfil.|

 ### HGJ2-(ISSUE#50): Hacer log-in ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/50#issue-2565890369]())
| "Como jugador quiero poder loguearme en el juego para jugar desde mi usuario."| 
|-----|
|![Login](../mockups/Login.png)|
|Tienes que rellenar los datos solicitados para verificar que efectivamente es tu usuario, finalmente puedes presionar "Log-in", en el cual si los datos son correctos entrarás en tu perfil, en caso contrario deberás introducirlos nuevamente.|

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

 ### HGA1-(ISSUE#30): Listado usuaios registrados ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/30#issue-2561421045]())
| "Como admin quiero ver el listado de usuarios registrados para llevar un control sobre la cantidad de usuarios en el sistema."| 
|-----|
|![Listado de usuarios](../mockups/Listado_usuarios.png)|
|Se despliega una lista de todos los usuarios registrados en el sistema.|

 ### HGA4-(ISSUE#53): CRUD de usuarios ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/53#issue-2565933479]())
| "Como administrador quiero poder crear, ver, actualizar y borrar usuarios para llevar un control adecuado de los usuarios del sistema."|
|-----|
|![Listado de usuarios](../mockups/Listado_usuarios.png)|
|En la lista de usuarios tienes las opciones para editar los datos de los usuarios usando el boton "edit", crear uno con el boton "add user" y eliminarlo usando "delete", ademas haciendo click en el nombre del usuario puedes ver su perfil.|

 ### HGA5-(ISSUE#54): Restablecer ([https://github.com/gii-is-DP1/DP1-2024-2025--l7-5/issues/54#issue-2565956464]())
| "Como administrador quiero poder restablecer la contraseña de un usuario para ayudar a los jugadores que hayan olvidado sus credenciales."|
|-----|
|![cambio de credenciales](../mockups/Cambio_de_credenciales.png)|
|En el formulario que aparece puedes modificar los datos en el apartado de contraseña para cambiar las credenciales del usuario especificado.|

## Diagrama conceptual del sistema
_En esta sección proporcionamos un diagrama UML de clases que describe el modelo de datos a implementar en la aplicación. Este diagrama esta anotado con las restricciones simples de los datos a gestionar por la aplicación.

![Diagrama UML conceptual](/docs/diagrams/Diagrama%20UML%20Spring%201.png)


## Reglas de Negocio
### R-< X > < Nombre Regla de negocio >
_< Descripción de la restricción a imponer >_

_Ej:_ 
### R1 – Diagnósticos imposibles
El diagnóstico debe estar asociado a una enfermedad que es compatible con el tipo de mascota de su visita relacionada. Por ejemplo, no podemos establecer como enfermedad diagnosticada una otitis cuando la visita está asociada a una mascota que es un pez, porque éstos no tienen orejas ni oídos (y por tanto no será uno de los tipos de mascota asociados a la enfermedad otitis en el vademecum).

…

_Muchas de las reglas del juego se transformarán en nuestro caso en reglas de negocio, por ejemplo, “la carta X solo podrá jugarse en la ronda Y si en la ronda anterior se jugó la carta Z”, o “en caso de que un jugador quede eliminado el turno cambia de sentido”_


