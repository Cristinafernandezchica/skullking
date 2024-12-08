import { render, screen } from "../../test-utils";
import userEvent from "@testing-library/user-event";
import PartidaListAdmin from "./PartidaListAdmin";

beforeAll(() => {
    global.fetch = jest.fn((url) => {
        if (url.includes('/partidas')) {
            return Promise.resolve({
                ok: true,
                json: () => Promise.resolve([
                    {
                        id: 2,
                        nombre: 'Partida Ejemplo2',
                        inicio: '2024-11-07T18:00:00',
                        estado: 'TERMINADA',
                        ownerPartida: 14
                    },
                    {
                        id: 3,
                        nombre: 'Partida Ejemplo',
                        inicio: '2024-11-05T13:00:00',
                        estado: 'ESPERANDO',
                        ownerPartida: 6
                    },
                ]),
            });
        } else if (url.includes('/users')) {
            const userId = url.split('/').pop();
            const users = {
                14: { id: 14, username: 'YTR7670' },
                6: { id: 6, username: 'player3' },
            };
            return Promise.resolve({
                ok: true,
                json: () => Promise.resolve(users[userId]),
            });
        }
        return Promise.reject(new Error('Unknown API endpoint'));
    });
});


afterAll(() => {
    jest.clearAllMocks();
});

describe('PartidaListAdmin', () => {
    test('renders partidas correctly', async () => {
        render(<PartidaListAdmin />);

        // Verificar que las partidas aparecen en la tabla
        const partida1 = await screen.findByText(/Partida Ejemplo2/);
        const partida2 = await screen.findByText(/Partida Ejemplo/);
        expect(partida1).toBeInTheDocument();
        expect(partida2).toBeInTheDocument();

        // Verificar el estado de las partidas
        const estado1 = await screen.findByText(/TERMINADA/);
        const estado2 = await screen.findByText(/ESPERANDO/);
        expect(estado1).toBeInTheDocument();
        expect(estado2).toBeInTheDocument();
    });

    test('search partidas correctly', async () => {
        const user = userEvent.setup();
        render(<PartidaListAdmin />);

        // Buscar por nombre
        const searchInput = await screen.findByRole('searchbox', { name: /search/ });
        await user.type(searchInput, 'Partida Ejemplo');

        // Verificar que se filtra correctamente
        const partida1 = await screen.findByText(/Partida Ejemplo/);
        expect(partida1).toBeInTheDocument();
        const partida2 = screen.queryByText(/Partida Ejemplo2/);
        expect(partida2).not.toBeInTheDocument();
    });

    test('clear filters and search', async () => {
        const user = userEvent.setup();
        render(<PartidaListAdmin />);

        // Aplicar filtro y búsqueda
        const searchInput = await screen.findByRole('searchbox', { name: /search/ });
        await user.type(searchInput, 'Partida Ejemplo');
        const clearButton = await screen.findByRole('button', { name: /clear-all/ });
        await user.click(clearButton);

        // Verificar que todas las partidas aparecen
        const allPartidas = await screen.findAllByRole('row');
        expect(allPartidas).toHaveLength(1); // Incluye el header
    });
    
    test('handles pagination correctly', async () => {
        render(<PartidaListAdmin />);

        // Simulación con solo dos partidas: sin paginación activa
        const pageInfo = await screen.findByText(/Página 1 de 1/);
        expect(pageInfo).toBeInTheDocument();
        const nextPageButton = screen.getByRole('button', { name: /Página siguiente/ });
        expect(nextPageButton).toBeDisabled();
    });
});
