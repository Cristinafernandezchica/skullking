import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import UserListAdmin from './UserListAdmin';
import UserCreateAdmin from './UserCreateAdmin';
import UserEditAdmin from './UserEditAdmin';
import { server } from '../../mocks/server'; // Simula peticiones al backend

// Mock de tokenService para simular autenticación
jest.mock('../../services/token.service', () => ({
  getLocalAccessToken: jest.fn(() => 'mocked-token'),
  getUser: jest.fn(() => ({
    id: 3,
    username: 'admin',
    password: "$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e",
    descripcionPerfil: "Administrador",
    imagenPerfil: "https://blog.tienda-medieval.com/wp-content/uploads/2019/02/Parche-pirata-ojo-derecho.jpg",
    conectado: false,
    numPartidasJugadas: 0,
    numPartidasGanadas: 0,
    numPuntosGanados: 0,
    authority: { id: 1, authority: 'ADMIN' },
  })),
}));

// Mock del componente PrivateRoute
jest.mock('../../privateRoute', () => ({ children }) => <>{children}</>);

// Configuración del mock server para interceptar peticiones al backend
beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('UserListAdmin Component', () => {

  test('debería mostrar todos los usuarios en la tabla', async () => {
    render(
      <MemoryRouter>
        <UserListAdmin />
      </MemoryRouter>
    );

    // Esperar que los usuarios se carguen
    const user1 = await screen.findByText('user1');
    const user2 = await screen.findByText('user2');

    // Verificar que los usuarios estén en el DOM
    expect(user1).toBeInTheDocument();
    expect(user2).toBeInTheDocument();
  });

  test('debería funcionar el botón "Añadir usuario"', async () => {
    render(
      <MemoryRouter initialEntries={["/users"]}>
        <Routes>
          <Route path="/users" element={<UserListAdmin />} />
          <Route path="/users/new" element={<UserCreateAdmin />} />
        </Routes>
      </MemoryRouter>
    );

    const addButton = screen.getByRole('link', { name: /añadir usuario/i });
    fireEvent.click(addButton);

    await waitFor(() => {
      expect(screen.getByText('Crear nuevo usuario')).toBeInTheDocument();
    });
  });

  
  test('debería funcionar el botón "Editar"', async () => {
    render(
      <MemoryRouter initialEntries={["/users"]}>
        <Routes>
          <Route path="/users" element={<UserListAdmin />} />
          <Route path="/users/:id" element={<UserEditAdmin />} />
        </Routes>
      </MemoryRouter>
    );

    const editButton = await screen.findByRole('button', { name: /edit-1/i });
    fireEvent.click(editButton);

    await waitFor(() => {
      expect(screen.getByText('No se puede editar al usuario user1 porque está en una partida "ESPERANDO".')).toBeInTheDocument();
    });
  });

  test('debería funcionar el botón "Eliminar"', async () => {
    render(
      <MemoryRouter>
        <UserListAdmin />
      </MemoryRouter>
    );

    const deleteButton = await screen.findByRole('button', { name: /delete-1/i });
    fireEvent.click(deleteButton);

    // Simulamos un mensaje de confirmación
    const alertMessage = await screen.findByText(/No se puede eliminar al usuario user1 porque está en una partida en espera./i);
    expect(alertMessage).toBeInTheDocument();
  });

});
