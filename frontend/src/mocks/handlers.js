import { rest } from 'msw';

// Simulación de Authorities
const authorityPlayer = {
    id: 2,
    authority: 'PLAYER'
};

const authorityAdmin = {
    id: 1,
    authority: 'ADMIN'
};

// Simulación de Usuarios
const user1 = {
    id: 1,
    username: "user1",
    password: "$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e",
    descripcionPerfil: "A Jugar!",
    imagenPerfil: "https://blog.tienda-medieval.com/wp-content/uploads/2019/02/Parche-pirata-ojo-derecho.jpg",
    conectado: false,
    numPartidasJugadas: 0,
    numPartidasGanadas: 0,
    numPuntosGanados: 0,
    authority: authorityPlayer,
};

const user2 = {
    id: 2,
    username: "user2",
    password: "$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e",
    descripcionPerfil: "A Jugar!",
    imagenPerfil: "https://blog.tienda-medieval.com/wp-content/uploads/2019/02/Parche-pirata-ojo-derecho.jpg",
    conectado: false,
    numPartidasJugadas: 0,
    numPartidasGanadas: 0,
    numPuntosGanados: 0,
    authority: authorityPlayer,
};

// Simulación de Partidas
const partida1 = {
    id: 1,
    nombre: "Partida Ejemplo1",
    inicio: "2024-11-05 13:00:00",
    fin: "2024-11-05 15:00:00",
    estado: "ESPERANDO",
    ownerPartida: user1,
    turnoActual: 2,
};

const partida2 = {
    id: 2,
    nombre: "Partida Ejemplo2",
    inicio: "2024-11-07 18:00:00",
    fin: "2024-11-07 21:00:00",
    estado: "JUGANDO",
    ownerPartida: user2,
    turnoActual: null,
};

// Simulación de Jugadores
const jugador1 = {
    id: 1,
    puntos: 15,
    partida: partida1,
    usuario: user1,
    apuestaActual: 0,
};

const jugador2 = {
    id: 2,
    puntos: 20,
    partida: partida1,
    usuario: user2,
    apuestaActual: 0,
};

const jugador3 = {
    id: 3,
    puntos: 30,
    partida: partida2,
    usuario: user1,
    apuesta_actual: 0,
};

export const handlers = [
    // Endpoint para obtener todos los usuarios (con posible filtro por authority)
    rest.get('/api/v1/users', (req, res, ctx) => {
        const users = [user1, user2];
        return res(ctx.status(200), ctx.json(users));
    }),

    // Endpoint para obtener todas las partidas (con posible filtro por nombre y estado)
    rest.get('/api/v1/partidas', (req, res, ctx) => {
        const partidas = [partida1, partida2];
        return res(ctx.status(200), ctx.json(partidas));
    }),

    // Endpoint para obtener los jugadores de una partida específica
    rest.get('/api/v1/partidas/:partidaId/jugadores', (req, res, ctx) => {
        const { partidaId } = req.params;
        let jugadores = [jugador1, jugador2, jugador3].filter(jugador => jugador.partida.id === parseInt(partidaId));

        return res(ctx.status(200), ctx.json(jugadores));
    })
];
