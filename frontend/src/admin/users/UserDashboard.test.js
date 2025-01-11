import { render, screen, waitFor, within } from '@testing-library/react';
import { server } from '../../mocks/server';
import UserStatisticsDashboard from './UserDashboard';
import { rest } from 'msw';

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('UserStatisticsDashboard Component', () => {
    test('Muestra una lista con todos los usuarios', async () => {
        render(<UserStatisticsDashboard />);

        // Esperar a que el primer usuario se renderice
        await waitFor(() => {
            expect(screen.getByText('user1')).toBeInTheDocument();
        });

        // Esperar a que el segundo usuario se renderice
        await waitFor(() => {
            expect(screen.getByText('user2')).toBeInTheDocument();
        });
    });

    test('Ordena los usuarios por puntos correctamente', async () => {
        render(<UserStatisticsDashboard />);
    
        // Simular clic en el botón para ordenar por puntos
        const sortByPointsButton = screen.getByRole('button', { name: /ordenar por puntos/i });
        sortByPointsButton.click();
    
        // Esperar a que los usuarios estén ordenados correctamente en la tabla
        await waitFor(() => {
            const rows = screen.getAllByRole('row').slice(1); // Excluir la fila del encabezado
            const actualData = rows.map(row => {
                const cells = within(row).getAllByRole('cell');
                return {
                    username: cells[0].textContent,
                    points: parseInt(cells[1].textContent, 10),
                };
            });
    
            // Validar que los usuarios están ordenados de mayor a menor por puntos
            expect(actualData).toEqual([
                { username: 'user2', points: 120 },
                { username: 'user1', points: 100 },
            ]);
        });
    });

    test('Ordena los usuarios por porcentaje de victorias correctamente', async () => {
        render(<UserStatisticsDashboard />);
    
        // Simular clic en el botón para ordenar por porcentaje de victorias
        const sortByWinPercentageButton = screen.getByRole('button', { name: /ordenar por porcentaje de victorias/i });
        sortByWinPercentageButton.click();
    
        // Esperar a que los usuarios estén ordenados correctamente en la tabla
        await waitFor(() => {
            const rows = screen.getAllByRole('row').slice(1); // Excluir la fila del encabezado
            const actualData = rows.map(row => {
                const cells = within(row).getAllByRole('cell');
                return {
                    username: cells[0].textContent.trim(),
                    winPercentage: parseFloat(parseFloat(cells[1].textContent.trim()).toFixed(2)), // Asegurar 2 decimales
                };
            });
    
            // Validar que los usuarios están ordenados de mayor a menor por porcentaje de victorias
            expect(actualData).toEqual([
                { username: 'user2', winPercentage: 75.00 },
                { username: 'user1', winPercentage: 50.00 },
            ]);
        });
    });    
    
    test('El usuario con más puntos se muestra en la parte superior', async () => {
        render(<UserStatisticsDashboard />);

        const sortByPointsButton = screen.getByRole('button', { name: /ordenar por puntos/i });
        sortByPointsButton.click();

        // Verificar que se está mostrando la vista por puntos (por defecto)
        const topUserHeading = await waitFor(() =>
            screen.findByText(/usuario con más puntos/i)
        );
        expect(topUserHeading).toBeInTheDocument();

        const topUserValue = await waitFor(() =>
            screen.findByText(/user2 - 120 puntos ganados/i)
        );
        expect(topUserValue).toBeInTheDocument();

        // Verificar que la tabla contiene los datos correctos
        const rows = await screen.findAllByRole('row');
        expect(rows).toHaveLength(3); // 1 header + 2 users
    });

    test('El usuario con mayor porcentaje de victorias se muestra en la parte superior', async () => {
        render(<UserStatisticsDashboard />);

        const sortByWinPercentageButton = screen.getByRole('button', { name: /ordenar por porcentaje de victorias/i });
        sortByWinPercentageButton.click();
        
        // Verificar que se está mostrando la vista por puntos (por defecto)
        const topUserHeading = await waitFor(() =>
            screen.findByText(/usuario con mayor porcentaje de victorias/i)
        );
        expect(topUserHeading).toBeInTheDocument();

        const topUserValue = await waitFor(() =>
            screen.findByText(/user2 - 75.00 porcentaje de victorias/i)
        );
        expect(topUserValue).toBeInTheDocument();

        // Verificar que la tabla contiene los datos correctos
        const rows = await screen.findAllByRole('row');
        expect(rows).toHaveLength(3); // 1 header + 2 users
    });

});
