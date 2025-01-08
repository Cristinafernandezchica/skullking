import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { rest } from 'msw';
import { setupServer } from 'msw/node';
import UserStatisticsDashboard from './UserDashboard'; // Asegúrate de importar el componente correcto

// Mock para los endpoints de la API
const server = setupServer(
  rest.get('/api/v1/users/sorted-by-points', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json([
        { id: 1, username: 'user1', numPuntosGanados: 100, numPartidasJugadas: 10, numPartidasGanadas: 5 },
        { id: 2, username: 'user2', numPuntosGanados: 80, numPartidasJugadas: 20, numPartidasGanadas: 10 },
      ])
    );
  }),
  rest.get('/api/v1/users/sorted-by-win-percentage', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json([
        { id: 2, username: 'user2', numPuntosGanados: 80, numPartidasJugadas: 20, numPartidasGanadas: 10, winPercentage: 50 },
        { id: 1, username: 'user1', numPuntosGanados: 100, numPartidasJugadas: 10, numPartidasGanadas: 5, winPercentage: 50 },
      ])
    );
  })
);

// Configuración del servidor de pruebas
beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('UserStatisticsDashboard Component Tests', () => {
  test('renders correctly and displays top user by points (positive test)', async () => {
    render(<UserStatisticsDashboard />);

    // Verificar que se está mostrando la vista por puntos (por defecto)
    const topUserHeading = await waitFor(() =>
      screen.findByText(/usuario con más puntos/i)
    );
    expect(topUserHeading).toBeInTheDocument();

    const topUserValue = await waitFor(() =>
      screen.findByText(/user1 - 100 puntos ganados/i)
    );
    expect(topUserValue).toBeInTheDocument();

    // Verificar que la tabla contiene los datos correctos
    const rows = await screen.findAllByRole('row');
    expect(rows).toHaveLength(3); // 1 header + 2 users
  });

  test('handles API failure gracefully (negative test)', async () => {
    // Simular un error en el endpoint de la API
    server.use(
      rest.get('/api/v1/users/sorted-by-points', (req, res, ctx) => {
        return res(ctx.status(500)); // Error interno del servidor
      })
    );

    render(<UserStatisticsDashboard />);

    // Verificar que se muestra un mensaje de error
    const errorMessage = await waitFor(() =>
      screen.findByText(/network response was not ok/i)
    );
    expect(errorMessage).toBeInTheDocument();

    // Verificar que la tabla no se renderizó correctamente
    const rows = screen.queryAllByRole('row');
    expect(rows).toHaveLength(0); // No se deben renderizar filas
  });

  test('displays sorted users by win percentage when button is clicked', async () => {
    render(<UserStatisticsDashboard />);

    // Esperar que la vista inicial por puntos esté visible
    await waitFor(() => screen.findByText(/usuario con más puntos/i));

    // Simular el cambio de orden por porcentaje de victorias
    const sortButton = screen.getByText(/ordenar por porcentaje de victorias/i);
    sortButton.click();

    // Esperar que los usuarios se muestren por porcentaje de victorias
    const topUserValue = await waitFor(() =>
      screen.findByText(/user2 - 50% de victorias/i)
    );
    expect(topUserValue).toBeInTheDocument();

    const rows = await screen.findAllByRole('row');
    expect(rows).toHaveLength(3); // 1 header + 2 users
  });
});