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

const admin = {
    id: 3,
    username: "admin",
    password: "$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e",
    descripcionPerfil: "Administrador",
    imagenPerfil: "https://blog.tienda-medieval.com/wp-content/uploads/2019/02/Parche-pirata-ojo-derecho.jpg",
    conectado: false,
    numPartidasJugadas: 0,
    numPartidasGanadas: 0,
    numPuntosGanados: 0,
    authority: authorityAdmin,
};

// Simulación de Usuarios
const user1 = {
    id: 1,
    username: "user1",
    password: "$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e",
    descripcionPerfil: "A Jugar!",
    imagenPerfil: "https://blog.tienda-medieval.com/wp-content/uploads/2019/02/Parche-pirata-ojo-derecho.jpg",
    conectado: false,
    numPartidasJugadas: 10,
    numPartidasGanadas: 5,
    numPuntosGanados: 100,
    authority: authorityPlayer,
};

const user2 = {
    id: 2,
    username: "user2",
    password: "$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e",
    descripcionPerfil: "A Jugar!",
    imagenPerfil: "https://blog.tienda-medieval.com/wp-content/uploads/2019/02/Parche-pirata-ojo-derecho.jpg",
    conectado: false,
    numPartidasJugadas: 8,
    numPartidasGanadas: 6,
    numPuntosGanados: 120,
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
    apuestaActual: 0,
};

// Usuarios ordenados por puntos (simulando la respuesta del backend)
const usersSortedByPoints = [user2, user1]; // user2 tiene más puntos que user1

// Usuarios ordenados por porcentaje de victorias (simulando la respuesta del backend)
const usersSortedByWinPercentage = [user2, user1]; // user2 tiene mejor porcentaje de victorias que user1

export const handlers = [
    // Endpoint para obtener todos los usuarios
    rest.get('/api/v1/users', (req, res, ctx) => {
        const users = [user1, user2];
        return res(ctx.status(200), ctx.json(users));
    }),

    // Endpoint para obtener usuarios ordenados por puntos totales
    rest.get('/api/v1/users/sorted-by-points', (req, res, ctx) => {
        return res(ctx.status(200), ctx.json(usersSortedByPoints));
    }),

    // Endpoint para obtener usuarios ordenados por porcentaje de victorias
    rest.get('/api/v1/users/sorted-by-win-percentage', (req, res, ctx) => {
        return res(ctx.status(200), ctx.json(usersSortedByWinPercentage));
    }),

    // Endpoint para obtener todas las partidas
    rest.get('/api/v1/partidas', (req, res, ctx) => {
        const partidas = [partida1, partida2];
        return res(ctx.status(200), ctx.json(partidas));
    }),

    // Endpoint para obtener los jugadores de una partida específica
    rest.get('/api/v1/partidas/:partidaId/jugadores', (req, res, ctx) => {
        const { partidaId } = req.params;
        let jugadores = [jugador1, jugador2, jugador3].filter(jugador => jugador.partida.id === parseInt(partidaId));

        return res(ctx.status(200), ctx.json(jugadores));
    }),

    // Endpoint para obtener jugadores por usuario ID
    rest.get('/api/v1/jugadores/:usuarioId/usuarios', (req, res, ctx) => {
        const { usuarioId } = req.params;
        const jugadores = [jugador1, jugador2, jugador3].filter(jugador => jugador.usuario.id === parseInt(usuarioId));

        if (jugadores.length === 0) {
            return res(ctx.status(404), ctx.json({ message: "No se encontraron jugadores para este usuario" }));
        }

        return res(ctx.status(200), ctx.json(jugadores));
    }),

    // Endpoint para eliminar un jugador
    rest.delete('/api/v1/jugadores/:id', (req, res, ctx) => {
        const { id } = req.params;
        const jugador = [jugador1, jugador2, jugador3].find(jugador => jugador.id === parseInt(id));

        if (!jugador) {
            return res(ctx.status(404), ctx.json({ message: "Jugador no encontrado" }));
        }

        return res(ctx.status(200), ctx.json({ message: "Jugador eliminado!" }));
    }),

    // Endpoint para obtener partidas por owner ID
    rest.get('/api/v1/partidas', (req, res, ctx) => {
        const { ownerId } = req.url.searchParams;
        const partidas = [partida1, partida2].filter(partida => partida.ownerPartida.id === parseInt(ownerId));

        if (partidas.length === 0) {
            return res(ctx.status(404), ctx.json({ message: "No se encontraron partidas para este owner" }));
        }

        return res(ctx.status(200), ctx.json(partidas));
    }),

    // Endpoint para eliminar un usuario
    rest.delete('/api/v1/users/:userId', (req, res, ctx) => {
        const { userId } = req.params;
        const user = [user1, user2].find(user => user.id === parseInt(userId));

        if (!user) {
            return res(ctx.status(404), ctx.json({ message: "Usuario no encontrado" }));
        }

        return res(ctx.status(204), ctx.json({ message: "Usuario eliminado" }));
    }),

    // Endpoint para eliminar una partida
    rest.delete('/api/v1/partidas/:id', (req, res, ctx) => {
        const { id } = req.params;
        const partida = [partida1, partida2].find(partida => partida.id === parseInt(id));

        if (!partida) {
            return res(ctx.status(404), ctx.json({ message: "Partida no encontrada" }));
        }

        return res(ctx.status(204), ctx.json({ message: "Partida eliminada" }));
    }),

    // Endpoint para obtener todas las autoridades
    rest.get('/api/v1/users/authorities', (req, res, ctx) => {
        const authorities = [authorityAdmin, authorityPlayer];
        return res(ctx.status(200), ctx.json(authorities));
    }),
    
    // Endpoint para obtener un usuario por ID
    rest.get('/api/v1/users/:id', (req, res, ctx) => {
        const { id } = req.params;
        const user = [user1, user2].find(user => user.id === parseInt(id));
    
        if (!user) {
            return res(ctx.status(404), ctx.json({ message: "Usuario no encontrado" }));
        }
    
        return res(ctx.status(200), ctx.json(user));
    })
];
