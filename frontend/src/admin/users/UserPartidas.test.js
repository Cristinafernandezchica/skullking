/* 
import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import UserPartidas from './UserPartidas'; 

jest.mock('../../services/token.service', () => ({
    getLocalAccessToken: jest.fn(() => 'fake-jwt-token'),
    getUser: jest.fn(() => ({ id: 1, username: 'testuser' })),
}));

jest.mock('../../util/getErrorModal', () => jest.fn(() => null));

describe('UserPartidas Component', () => {
    beforeEach(() => {
        global.fetch = jest.fn();
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    it('should render the component and display initial elements', () => {
        render(<UserPartidas />);

        expect(screen.getByText('Partidas')).toBeInTheDocument();
        expect(screen.getByPlaceholderText('Buscar por nombre')).toBeInTheDocument();
        expect(screen.getByPlaceholderText('Partidas por página')).toBeInTheDocument();
        expect(screen.getByLabelText('jugando-filter')).toBeInTheDocument();
        expect(screen.getByLabelText('esperando-filter')).toBeInTheDocument();
        expect(screen.getByLabelText('terminada-filter')).toBeInTheDocument();
        expect(screen.getByLabelText('all-filter')).toBeInTheDocument();
    });

    it('should fetch and display partidas', async () => {
        const mockPartidas = [
            {
                id: 1,
                nombre: 'Partida 1',
                estado: 'JUGANDO',
                inicio: '2023-01-01T10:00:00Z',
                ownerPartida: 1,
            },
        ];

        const mockJugadores = [
            { id: 1, usuario: { id: 1, username: 'testuser' } },
        ];

        global.fetch
            .mockResolvedValueOnce({
                ok: true,
                json: async () => mockPartidas,
            })
            .mockResolvedValueOnce({
                ok: true,
                json: async () => mockJugadores,
            });

        render(<UserPartidas />);

        await waitFor(() => {
            expect(screen.getByText('Partida 1')).toBeInTheDocument();
            expect(screen.getByText('JUGANDO')).toBeInTheDocument();
        });
    });

    it('should filter partidas by estado', async () => {
        const mockPartidas = [
            {
                id: 1,
                nombre: 'Partida 1',
                estado: 'JUGANDO',
                inicio: '2023-01-01T10:00:00Z',
                ownerPartida: 1,
            },
            {
                id: 2,
                nombre: 'Partida 2',
                estado: 'TERMINADA',
                inicio: '2023-01-02T10:00:00Z',
                ownerPartida: 2,
            },
        ];

        const mockJugadores = [
            { id: 1, usuario: { id: 1, username: 'testuser' } },
        ];

        global.fetch
            .mockResolvedValueOnce({
                ok: true,
                json: async () => mockPartidas,
            })
            .mockResolvedValueOnce({
                ok: true,
                json: async () => mockJugadores,
            });

        render(<UserPartidas />);

        await waitFor(() => {
            expect(screen.getByText('Partida 1')).toBeInTheDocument();
            expect(screen.getByText('Partida 2')).toBeInTheDocument();
        });

        fireEvent.click(screen.getByLabelText('jugando-filter'));

        await waitFor(() => {
            expect(screen.getByText('Partida 1')).toBeInTheDocument();
            expect(screen.queryByText('Partida 2')).not.toBeInTheDocument();
        });
    });

    it('should handle search functionality', async () => {
        const mockPartidas = [
            {
                id: 1,
                nombre: 'Partida 1',
                estado: 'JUGANDO',
                inicio: '2023-01-01T10:00:00Z',
                ownerPartida: 1,
            },
            {
                id: 2,
                nombre: 'Partida 2',
                estado: 'TERMINADA',
                inicio: '2023-01-02T10:00:00Z',
                ownerPartida: 2,
            },
        ];

        global.fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockPartidas,
        });

        render(<UserPartidas />);

        await waitFor(() => {
            expect(screen.getByText('Partida 1')).toBeInTheDocument();
            expect(screen.getByText('Partida 2')).toBeInTheDocument();
        });

        fireEvent.change(screen.getByPlaceholderText('Buscar por nombre'), {
            target: { value: 'Partida 1' },
        });

        await waitFor(() => {
            expect(screen.getByText('Partida 1')).toBeInTheDocument();
            expect(screen.queryByText('Partida 2')).not.toBeInTheDocument();
        });
    });
});
*/
