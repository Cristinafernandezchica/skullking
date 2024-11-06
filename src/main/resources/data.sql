-- One admin user, named admin1 with passwor 4dm1n and authority admin
INSERT INTO authorities(id,authority) VALUES (1,'ADMIN');
INSERT INTO appusers(id,username,password,authority) VALUES (1,'admin1','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);


-- Ten player users, named player1 with passwor 0wn3r
INSERT INTO authorities(id,authority) VALUES (2,'PLAYER');
INSERT INTO appusers(id,username,password,authority) VALUES (4,'player1','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (5,'player2','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (6,'player3','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (7,'player4','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (8,'player5','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (9,'player6','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (10,'player7','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (11,'player8','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (12,'player9','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (13,'player10','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (14,'YTR7670','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (15,'RFM6490','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (16,'NMY0786','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (17,'PXT3852','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (18,'DGL2523','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);





-- moradas


INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (1, 1, 'morada', 'morada_1.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (2, 2, 'morada', 'morada_2.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (3, 3, 'morada', 'morada_3.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (4, 4, 'morada', './images/cartas/morada_4.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (5, 5, 'morada', './images/cartas/morada_5.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (6, 6, 'morada', './images/cartas/morada_6.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (7, 7, 'morada', './images/cartas/morada_7.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (8, 8, 'morada', './images/cartas/morada_8.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (9, 9, 'morada', './images/cartas/morada_9.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (10, 10, 'morada', './images/cartas/morada_10.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (11, 11, 'morada', './images/cartas/morada_11.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (12, 12, 'morada', './images/cartas/morada_12.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (13, 13, 'morada', './images/cartas/morada_13.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (14, 14, 'morada', './images/cartas/morada_14.png', './images/cartas/parte_trasera.png');


--amarillas


INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (15, 1, 'amarillo', './images/cartas/amarilla_1.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (16, 2, 'amarillo', './images/cartas/amarilla_2.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (17, 3, 'amarillo', './images/cartas/amarilla_3.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (18, 4, 'amarillo', './images/cartas/amarilla_4.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (19, 5, 'amarillo', './images/cartas/amarilla_5.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (20, 6, 'amarillo', './images/cartas/amarilla_6.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (21, 7, 'amarillo', './images/cartas/amarilla_7.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (22, 8, 'amarillo', './images/cartas/amarilla_8.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (23, 9, 'amarillo', './images/cartas/amarilla_9.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (24, 10, 'amarillo', './images/cartas/amarilla_10.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (25, 11, 'amarillo', './images/cartas/amarilla_11.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (26, 12, 'amarillo', './images/cartas/amarilla_12.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (27, 13, 'amarillo', './images/cartas/amarilla_13.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (28, 14, 'amarillo', './images/cartas/amarilla_14.png', './images/cartas/parte_trasera.png');


-- verde


INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (29, 1, 'verde', './images/cartas/verde_1.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (30, 2, 'verde', './images/cartas/verde_2.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (31, 3, 'verde', './images/cartas/verde_3.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (32, 4, 'verde', './images/cartas/verde_4.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (33, 5, 'verde', './images/cartas/verde_5.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (34, 6, 'verde', './images/cartas/verde_6.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (35, 7, 'verde', './images/cartas/verde_7.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (36, 8, 'verde', './images/cartas/verde_8.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (37, 9, 'verde', './images/cartas/verde_9.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (38, 10, 'verde', './images/cartas/verde_10.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (39, 11, 'verde', './images/cartas/verde_11.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (40, 12, 'verde', './images/cartas/verde_12.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (41, 13, 'verde', './images/cartas/verde_13.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (42, 14, 'verde', './images/cartas/verde_14.png', './images/cartas/parte_trasera.png');


-- triunfo


INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (43, 1, 'triunfo', './images/cartas/triunfo_1.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (44, 2, 'triunfo', './images/cartas/triunfo_2.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (45, 3, 'triunfo', './images/cartas/triunfo_3.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (46, 4, 'triunfo', './images/cartas/triunfo_4.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (47, 5, 'triunfo', './images/cartas/triunfo_5.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (48, 6, 'triunfo', './images/cartas/triunfo_6.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (49, 7, 'triunfo', './images/cartas/triunfo_7.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (50, 8, 'triunfo', './images/cartas/triunfo_8.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (51, 9, 'triunfo', './images/cartas/triunfo_9.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (52, 10, 'triunfo', './images/cartas/triunfo_10.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (53, 11, 'triunfo', './images/cartas/triunfo_11.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (54, 12, 'triunfo', './images/cartas/triunfo_12.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (55, 13, 'triunfo', './images/cartas/triunfo_13.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id, numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (56, 14, 'triunfo', './images/cartas/triunfo_14.png', './images/cartas/parte_trasera.png');


-- especiales


INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (57,  null, 'pirata', './images/cartas/pirata_1.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (58,  null, 'pirata', './images/cartas/pirata_2.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (59,  null, 'pirata', './images/cartas/pirata_3.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (60,  null, 'pirata', './images/cartas/pirata_4.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (61,  null, 'pirata', './images/cartas/pirata_5.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (62,  null, 'sirena', './images/cartas/sirena_1.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (63,  null, 'sirena', './images/cartas/sirena_2.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (64,  null, 'skullking', './images/cartas/skull_king.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (65,  null, 'tigresa', './images/cartas/sirena_2.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (66, null, 'banderaBlanca', './images/cartas/bandera_blanca.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (67, null, 'banderaBlanca', './images/cartas/bandera_blanca.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (68, null, 'banderaBlanca', './images/cartas/bandera_blanca.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (69, null, 'banderaBlanca', './images/cartas/bandera_blanca.png', './images/cartas/parte_trasera.png');
INSERT INTO Carta(id,  numero, tipo_carta, imagen_frontal, imagen_trasera) VALUES (70, null, 'banderaBlanca', './images/cartas/bandera_blanca.png', './images/cartas/parte_trasera.png');


-- testeo


INSERT INTO Partida(id,nombre,inicio,fin,estado) VALUES (3,'carmelito',null,null,null);

INSERT INTO Jugador(id,puntos,partida_id,user_id,turno) VALUES (3,30,3,4,0);
INSERT INTO Jugador(id,puntos,partida_id,user_id,turno) VALUES (1,15,3,5,1);
INSERT INTO Jugador(id,puntos,partida_id,user_id,turno) VALUES (2,15,3,5,1);
INSERT INTO Jugador(id,puntos,partida_id,user_id,turno) VALUES (4,30,3,4,1);



INSERT INTO Mano(id, jugador_id, apuesta, resultado) VALUES (1, 4, 5, 3);
INSERT INTO Mano(id, jugador_id, apuesta, resultado) VALUES (2, 2, 1, 0);
INSERT INTO Mano(id, jugador_id, apuesta, resultado) VALUES (3, 3, 0, 0);
-- Asignar cartas a la mano 1
INSERT INTO carta_mano(mano_id, carta_id) VALUES (1, 1);  -- Mano 1 contiene Carta 1
INSERT INTO carta_mano(mano_id, carta_id) VALUES (1, 2);  -- Mano 1 contiene Carta 2
INSERT INTO carta_mano(mano_id, carta_id) VALUES (1, 3);  -- Mano 1 contiene Carta 2


-- Pruebas para Baza
INSERT INTO Baza(id, tipo_carta, jugador_id, carta_id, ronda_id) VALUES (1,'triunfo',1,1,1);
INSERT INTO Baza(id, tipo_carta, jugador_id, carta_id, ronda_id) VALUES (2,'pirata',3,8,2);
