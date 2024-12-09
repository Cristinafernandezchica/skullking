# Informe de retrospectiva de los Sprints

Documento sobre la retrospectiva del trabajo durante la Sprint 1 del equipo L7 - 05
Los miembros del equipo son:

Aguilar Morcillo, Marta
Amo Sánchez, Ángel
Chávez Malavé, Luis Emmanuel
Fernández Chica, Cristina
Gutiérrez González, Candela Jazmín

Project Manager: Fernández Chica, Cristina

## Sprint 1
### ¿Qué ha funcionado bien?
Durante este sprint, hemos llegado al alcance que nos habíamos propuesto al principio. Las actividades se distribuyeron en parejas, lo que permitió que todos trabajáramos en la mayoría de los temas abordados durante el Sprint, logrando así cumplir con el objetivo dentro del plazo previsto.

### ¿Qué cosas hay que mejorar para el siguiente sprint?
En este primer sprint al tener que dedicarle más tiempo a asentar las bases del proyecto, hemos llevado la cuenta de las horas invertidas a mano, pero ante la falta de informe de horas del entregable, para el próximo sprint hemos decidido que usaremos Clockify para poder medir exactamente el tiempo que le destina cada miembro. Además, para el siguiente sprint también debemos mejorar la cohesión de las distintas partes en las que dividimos la entrega para ser aún más eficientes, pues hay varias partes que hemos terminado colaborando todo el equipo.
Y compenetrarnos para poder fusionar nuestras ramas sin que surjan conflictos, aunque no hayamos tenido muchos problemas con ello.

### ¿Problemas que hayamos tenido para poder progresar correctamente en el último Sprint?
Aparte de lo comentado anteriormente, no hemos tenido muchos más problemas, a expeción de que como hemos tenido una gran carga de trabajo externa de otras asignaturas, nos ha limitado muchas veces el realizar reuniones.

### Medición del tiempo trabajado
En cuanto a la medición del tiempo trabajado la hemos realizado manualmente y hemos trabajado las siguientes horas:
Todo el grupo - 2 horas - 01/10/2024 - (17:40-19:40)
Todo el grupo - 4 horas - 02/10/2024 - (10:30-14:30)
Todo el grupo - 2,5 horas - 04/10/2024 - (11:30-14:00)
Todo el grupo - 2,5 horas - 07/10/2024 - (10:00-12:30)

## Sprint 2
### ¿Qué ha funcionado bien?

 Durante este sprint se ha alcanzado lo solicitado de cada entregable. Para ello, hemos decidido comenzar con la asignación de una clase a cada integrante. Además, algunas entidades han sido implementadas por algunas parejas, así como las distintas dependencias entre las entidades, lo que se debe a las relaciones en cadena de las clases. Se ha procedido con la implementación de algunas historias de usuario, y, finalmente, se ha completado la documentación y se han realizado las pruebas unitarias correspondientes. En paralelo, se han ido incorporando los cambios realizados al diagrama de clases.

### Asignación de tareas
#### Diagrama de clases
Aguilar Morcillo, Marta;  Amo Sánchez, Ángel

#### Diagrama de capas

Gutiérrez González, Candela Jazmín

#### Patrones de diseño y arquitectónicos aplicados

Fernández Chica, Cristina;  Aguilar Morcillo, Marta

#### Decisiones de diseño

Amo Sánchez, Ángel

#### Plan de pruebas y matriz de trazabilidad

Fernández Chica, Cristina

#### Clases
Partida: Fernández Chica, Cristina

Ronda: Gutiérrez González, Candela Jazmín

Baza: Amo Sánchez, Ángel

Mano: Fernández Chica, Cristina;  Gutiérrez González, Candela Jazmín;  Chávez Malavé, Luis Emmanuel;  Amo Sánchez, Ángel

Truco: Aguilar Morcillo, Marta

Carta: Fernández Chica, Cristina;  Chávez Malavé, Luis Emmanuel

Jugador: Chávez Malavé, Luis Emmanuel

#### Frontend

Fernández Chica, Cristina; Chávez Malavé, Luis Emmanuel; Gutiérrez González; Candela;  Amo Sánchez, Ángel

#### Las asignaciones son orientativas,  ya que han habido aportaciones de distintas personas en cada apartado por la complejidad del proyecto.


 ### ¿Problemas que hayamos tenido para poder progresar correctamente en el último Sprint?

 Al haber seguido una organización de asignación de clases, han surgido numerosos problemas, pues como se ha explicado, el proyecto sigue una organizacion en cadena, lo cual hace imposible una implementacion limpia. 

 ### ¿Qué cosas hay que mejorar para el siguiente sprint?

La comunicación. Debido a la decisión tomada, la comunicación era clave para la viabilidad del proyecto, y creemos que se ha visto reflejado en los problemas que han surgido a lo largo del sprint.

### Medición del tiempo trabajado

En cuanto a la medición del tiempo trabajado, hemos solucionado el problema del sprint anterior, y todos hemos hecho uso de la herramienta Clockify:

#### Sprint Rueda (todos)

![Sprint2 Rueda](/docs/deliverables/reportesClockify/Sprint2/Sprint2_TODOS.jpg)

#### Sprint Gráfica (todos)

![Sprint2 Gráfica](/docs/deliverables/reportesClockify/Sprint2/Sprint2_GRÁfICA.jpg)

#### Sprint Gráfica Aguilar Morcillo, Marta

![Sprint2 Marta](/docs/deliverables/reportesClockify/Sprint2/Sprint2_MARTA.jpg)

#### Sprint Gráfica  Amo Sánchez, Ángel

![Sprint2 Angel](/docs/deliverables/reportesClockify/Sprint2/Sprint2_ANGEL.jpg)

#### Sprint Gráfica Fernández Chica, Cristina

![Sprint2 Cristina](/docs/deliverables/reportesClockify/Sprint2/Sprint2_CRISTINA.jpg)

#### Sprint Gráfica Gutiérrez González, Candela Jazmín

![Sprint2 Candela](/docs/deliverables/reportesClockify/Sprint2/Sprint2_CANDELA.jpg)

#### Sprint Gráfica Chávez Malavé, Luis Emmanuel

![Sprint2 Emmanuel](/docs/deliverables/reportesClockify/Sprint2/Sprint2_EMMANUEL.jpg)
 


## Sprint 3
### ¿Qué ha funcionado bien?


En este sprint se han alcanzado todos los objetivos propuestos. Para lograrlo, hemos decidido centrar nuestros esfuerzos en dividir el trabajo en dos grandes bloques: la refactorización e implementación de decisiones de diseño y el desarrollo de la parte funcional de la aplicación.

Gracias a esta estrategia, hemos podido desarrollar el ranking de puntuaciones, dividiendo toda la tabla mediante la paginación. Además, se mejoró el lobby de partida, se logró un gran avance en el desarrollo de la partida en sí y, finalmente, alcanzamos nuestro último gran hito: la mejora en la legibilidad del código. Esto se consiguió gracias al uso de patrones de diseño como *State*, la eliminación de funciones que ya no eran utilizadas y la implementación de DTOs.

Cabe recalcar que, de forma paralela, se implementaron aún más tests para verificar el correcto funcionamiento del código.

### Asignación de tareas

#### Diagrama de capas

Gutiérrez González, Candela Jazmín

#### Diagrama de dominio
Amo Sánchez, Ángel

#### Patrones de diseño y arquitectónicos aplicados
Aguilar Morcillo, Marta

#### Decisiones de diseño
Aguilar Morcillo, Marta

#### Plan de pruebas y matriz de trazabilidad

Fernández Chica, Cristina

#### Tests
Amo Sánchez, Ángel ; Gutiérrez González, Candela Jazmín; Fernández Chica, Cristina

#### Backend

Fernández Chica, Cristina; Gutiérrez González, Candela; Aguilar Morcillo, Marta; Chávez Malavé, Luis Emmanuel; Amo Sánchez, Ángel

#### Frontend

Fernández Chica, Cristina; Chávez Malavé, Luis Emmanuel; Gutiérrez González, Candela;  Amo Sánchez, Ángel

#### Las asignaciones son orientativas,  ya que han habido aportaciones de distintas personas en cada apartado por la complejidad del proyecto.


 ### ¿Problemas que hayamos tenido para poder progresar correctamente en el último Sprint?


La alta complejidad del código. Puesto que cada pareja era responsable de desarrollar un componente de la aplicación, al momento de fusionarlos para hacer funcionar la aplicación, nos vimos con el problema de que no se terminaban de acoplar correctamente, generando fallos. De igual manera, como se ha ido añadiendo constantemente funcionalidad a nuestra pestaña de juego, terminó siendo un bloque de código muy grande.

 ### ¿Qué cosas hay que mejorar para el siguiente sprint?

Estandarizar nuestro código. Aunque hayamos hecho un gran avance en este sprint, todavía tenemos que continuar con el trabajo de refactorización del código e intentar unificar todas las llamadas a funciones de nuestra pantalla principal para mejorar la legibilidad del código.

### Medición del tiempo trabajado

En cuanto a la medición del tiempo trabajado, hemos vuelto a usar la herramienta Clockify:

#### Sprint Rueda (todos)

![Sprint3 Rueda](/docs/deliverables/reportesClockify/Sprint3/rueda_todos_sprint3.png)

#### Sprint Gráfica (todos)

![Sprint3 Gráfica](/docs/deliverables/reportesClockify/Sprint3/barras_todos_sprint3.png)

#### Sprint Gráfica Aguilar Morcillo, Marta

![Sprint3 Marta](/docs/deliverables/reportesClockify/Sprint3/Marta_sprint3.png)

#### Sprint Gráfica  Amo Sánchez, Ángel

![Sprint3 Angel](/docs/deliverables/reportesClockify/Sprint3/Angel_sprint3.png)

#### Sprint Gráfica Fernández Chica, Cristina

![Sprint3 Cristina](/docs/deliverables/reportesClockify/Sprint3/Cristina_rueda_sprint3.png)

#### Sprint Gráfica Gutiérrez González, Candela Jazmín

![Sprint3 Candela](/docs/deliverables/reportesClockify/Sprint3/Candela_sprint3.png)

#### Sprint Gráfica Chávez Malavé, Luis Emmanuel

![Sprint3 Emmanuel](/docs/deliverables/reportesClockify/Sprint3/Emmanuel_sprint3.png)