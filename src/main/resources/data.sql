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

INSERT INTO Jugador(id,puntos,partida_id,user_id,turno) VALUES (3,30,3,4,1);
INSERT INTO Jugador(id,puntos,partida_id,user_id,turno) VALUES (1,15,3,5,1);
INSERT INTO Jugador(id,puntos,partida_id,user_id,turno) VALUES (2,15,3,5,1);
INSERT INTO Jugador(id,puntos,partida_id,user_id,turno) VALUES (4,30,3,4,1);



-- moradas
INSERT INTO Carta(id, numero, tipo_carta) VALUES (1, 1, 'morada');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (2, 2, 'morada');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (3, 3, 'morada');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (4, 4, 'morada');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (5, 5, 'morada');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (6, 6, 'morada');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (7, 7, 'morada');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (8, 8, 'morada');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (9, 9, 'morada');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (10, 10, 'morada');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (11, 11, 'morada');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (12, 12, 'morada');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (13, 13, 'morada');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (14, 14, 'morada');

--amarillas 

INSERT INTO Carta(id, numero, tipo_carta) VALUES (15, 1, 'amarillo');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (16, 2, 'amarillo');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (17, 3, 'amarillo');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (18, 4, 'amarillo');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (19, 5, 'amarillo');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (20, 6, 'amarillo');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (21, 7, 'amarillo');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (22, 8, 'amarillo');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (23, 9, 'amarillo');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (24, 10, 'amarillo');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (25, 11, 'amarillo');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (26, 12, 'amarillo');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (27, 13, 'amarillo');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (28, 14, 'amarillo');

-- verde
INSERT INTO Carta(id, numero, tipo_carta) VALUES (29, 1, 'verde');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (30, 2, 'verde');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (31, 3, 'verde');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (32, 4, 'verde');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (33, 5, 'verde');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (34, 6, 'verde');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (35, 7, 'verde');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (36, 8, 'verde');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (37, 9, 'verde');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (38, 10, 'verde');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (39, 11, 'verde');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (40, 12, 'verde');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (41, 13, 'verde');
INSERT INTO Carta(id, numero, tipo_carta) VALUES (42, 14, 'verde');

-- especiales 
INSERT INTO Carta(id,  numero, tipo_carta) VALUES (43,  1, 'pirata');
INSERT INTO Carta(id,  numero, tipo_carta) VALUES (44,  2, 'sirena');
INSERT INTO Carta(id,  numero, tipo_carta) VALUES (45,  3, 'skullking');
INSERT INTO Carta(id,  numero, tipo_carta) VALUES (46,  4, 'tigresa');
INSERT INTO Carta(id,  numero, tipo_carta) VALUES (47, 5, 'banderaBlanca');
INSERT INTO Carta(id,  numero, tipo_carta) VALUES (48,  6, 'triunfo');


-- testeo

INSERT INTO Mano(id, jugador_id, apuesta, resultado) VALUES (1, 4, 5, 3);
INSERT INTO Mano(id, jugador_id, apuesta, resultado) VALUES (2, 4, 7, 6);
INSERT INTO Mano(id, jugador_id, apuesta, resultado) VALUES (3, 4, 4, 2);

-- Asignar cartas a la mano 1
INSERT INTO carta_mano(mano_id, carta_id) VALUES (1, 1);  -- Mano 1 contiene Carta 1
INSERT INTO carta_mano(mano_id, carta_id) VALUES (1, 2);  -- Mano 1 contiene Carta 2

-- Asignar cartas a la mano 2
INSERT INTO carta_mano(mano_id, carta_id) VALUES (2, 2);  -- Mano 2 contiene Carta 2
INSERT INTO carta_mano(mano_id, carta_id) VALUES (2, 3);  -- Mano 2 contiene Carta 3
INSERT INTO carta_mano(mano_id, carta_id) VALUES (2, 4);  -- Mano 2 contiene Carta 4

-- Asignar cartas a la mano 3
INSERT INTO carta_mano(mano_id, carta_id) VALUES (3, 1);  -- Mano 3 contiene Carta 1
INSERT INTO carta_mano(mano_id, carta_id) VALUES (3, 4);  -- Mano 3 contiene Carta 4