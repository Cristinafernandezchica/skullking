import React from 'react';
import { render, screen, waitFor } from '../test-utils';
import userEvent from "@testing-library/user-event";
import SalaEspera from "./index"; // Cambiada la ruta a la misma carpeta con el nombre 'index'

describe('SalaEspera', () => {
    const mockData = [{ id: 1, usuario: { username: 'Jugador1' }, turno: 1 }, { id: 2, usuario: { username: 'Jugador2' }, turno: 2 }];
    const jwt = 'dummy-jwt-token';
    const id = 'dummy-game-id';

    beforeEach(() => {
        global.fetch = jest.fn()
            .mockResolvedValueOnce({
                ok: true,
                json: async () => ({ id, estado: 'ESPERANDO', ownerPartida: 'user1' }),
            })
            .mockResolvedValueOnce({
                ok: true,
                json: async () => mockData,
            });
    });

    test('renders correctly', async () => {
        render(<SalaEspera />);

        const heading = await screen.findByRole('heading', { name: /lobby/i });
        expect(heading).toBeInTheDocument();
        const button = screen.queryByRole('button', { name: /iniciar partida/i });
        if (mockData[0].usuario.username === 'user1') {
            expect(button).toBeInTheDocument();
        }
    });

    test('renders jugadores correctly', async () => {
        render(<SalaEspera />);
        const jugador1 = await screen.findByRole('cell', { 'name': 'Jugador1' });
        expect(jugador1).toBeInTheDocument();

        const jugador2 = await screen.findByRole('cell', { 'name': 'Jugador2' });
        expect(jugador2).toBeInTheDocument();

        const rows = await screen.findAllByRole('row');
        expect(rows).toHaveLength(mockData.length + 1); // +1 for the header row
    });

    // TO DO: Da fallitos

    test('handles fetch error correctly', async () => {
        global.fetch.mockRejectedValueOnce(new Error('Network response was not ok'));

        render(<SalaEspera />);
        const alert = await waitFor(() => screen.findByRole('alert'));
        expect(alert).toBeInTheDocument();
        expect(alert).toHaveTextContent('Error encontrando jugadores');
    });

});
