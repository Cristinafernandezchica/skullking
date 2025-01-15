import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import PartidaListAdmin from './PartidaListAdmin';
import { server } from '../../mocks/server'; // Aquí se importa el server

jest.mock('../../services/token.service', () => ({
  getLocalAccessToken: jest.fn(() => 'mocked-token'),
  getUser: jest.fn(() => ({
    id: 1,
    username: 'admin',
    authority: { id: 1, authority: 'ADMIN' },
  })),
}));

// Iniciamos el server antes de cada test y lo cerramos después
beforeAll(() => server.listen()); // Inicia el servidor que simula las peticiones
afterEach(() => server.resetHandlers()); // Resetea los handlers después de cada test
afterAll(() => server.close()); // Cierra el servidor después de todas las pruebas

describe('PartidaListAdmin', () => {

  test('debería filtrar partidas por nombre correctamente', async () => {
    render(<PartidaListAdmin />);

    // Espera que las partidas se carguen en el DOM
    const partida1 = await screen.findByText('Partida Ejemplo1');
    const partida2 = await screen.findByText('Partida Ejemplo2');

    // Verifica que ambas partidas están inicialmente en el DOM
    expect(partida1).toBeInTheDocument();
    expect(partida2).toBeInTheDocument();

    // Simula que el usuario escribe "Partida Ejemplo1" en la barra de búsqueda
    const searchInput = screen.getByPlaceholderText('Buscar por nombre');
    fireEvent.change(searchInput, { target: { value: 'Partida Ejemplo1' } });

    // Espera que "Partida Ejemplo1" aparezca en el DOM
    await waitFor(() => {
      const filteredPartida = screen.queryByText('Partida Ejemplo1');
      expect(filteredPartida).toBeInTheDocument();
    });

    // Espera que "Partida Ejemplo2" NO esté en el DOM
    await waitFor(() => {
      const filteredPartida2 = screen.queryByText('Partida Ejemplo2');
      expect(filteredPartida2).not.toBeInTheDocument();
    });
  });

  test('debería filtrar partidas por estado (jugando, esperando, terminada)', async () => {
    render(<PartidaListAdmin />);

    // Espera que las partidas se carguen en el DOM
    const partida1 = await screen.findByText('Partida Ejemplo1');
    const partida2 = await screen.findByText('Partida Ejemplo2');

    // Verifica que ambas partidas están inicialmente en el DOM
    expect(partida1).toBeInTheDocument();
    expect(partida2).toBeInTheDocument();

    // Selecciona el botón de filtro por estado "jugando"
    const jugandoFilter = screen.getByRole('button', { name: /jugando-filter/i });
    fireEvent.click(jugandoFilter);

    // Espera a que las partidas filtradas por "jugando" aparezcan
    await waitFor(() => {
      const partidaJugando = screen.queryByText('Partida Ejemplo2');
      expect(partidaJugando).toBeInTheDocument();
    });

    // Verifica que las partidas con otros estados no estén en el DOM
    const partidaEsperando = screen.queryByText('Partida Ejemplo1');
    expect(partidaEsperando).not.toBeInTheDocument();
  });

  test('debería mostrar mensaje de error cuando no se encuentra ninguna partida con el nombre dado', async () => {
    render(<PartidaListAdmin />);

    // Espera que las partidas se carguen en el DOM
    const partida1 = await screen.findByText('Partida Ejemplo1');
    const partida2 = await screen.findByText('Partida Ejemplo2');

    // Verifica que ambas partidas están inicialmente en el DOM
    expect(partida1).toBeInTheDocument();
    expect(partida2).toBeInTheDocument();

    // Simula que el usuario escribe "Inexistente" en la barra de búsqueda
    const searchInput = screen.getByPlaceholderText('Buscar por nombre');
    fireEvent.change(searchInput, { target: { value: 'Inexistente' } });

    // Espera que el mensaje de error aparezca en el DOM
    await waitFor(() => {
      const errorMessage = screen.getByText('No hay consultas con esos filtros y parámetros de búsqueda.');
      expect(errorMessage).toBeInTheDocument();
    });

    // Verifica que las partidas no están presentes
    const filteredPartida1 = screen.queryByText('Partida Ejemplo1');
    const filteredPartida2 = screen.queryByText('Partida Ejemplo2');
    expect(filteredPartida1).not.toBeInTheDocument();
    expect(filteredPartida2).not.toBeInTheDocument();
  });

  test('debería mostrar correctamente los creadores de las partidas', async () => {
    render(<PartidaListAdmin />);
  
    // Busca todas las celdas de la columna "Creador"
    const creatorCells = await screen.findAllByRole('cell', { name: /Cargando creador...|user1|user2/i });
  
    // Filtra las celdas específicas para validar a cada creador
    const owner1Cell = creatorCells.find((cell) => cell.textContent === 'user1');
    const owner2Cell = creatorCells.find((cell) => cell.textContent === 'user2');
  
    // Valida que ambas celdas existen
    expect(owner1Cell).toBeInTheDocument();
    expect(owner2Cell).toBeInTheDocument();
  });

  test('debería permitir borrar filtros con el botón "Borrar todo"', async () => {
    render(<PartidaListAdmin />);

    // Selecciona el botón "Borrar todo"
    const clearAllButton = screen.getByRole('button', { name: /clear-all/i });

    // Haz clic en el botón "Borrar todo"
    fireEvent.click(clearAllButton);

    // Verifica que el campo de búsqueda esté vacío
    const searchInput = screen.getByPlaceholderText('Buscar por nombre');
    expect(searchInput.value).toBe('');
  });

  test('debería manejar correctamente el número de partidas por página', async () => {
    render(<PartidaListAdmin />);

    // Selecciona el input para partidas por página
    const partidasPerPageInput = screen.getByRole('spinbutton', {
      name: /partidas-por-pagina/i,
    });

    // Cambia el valor del input
    fireEvent.change(partidasPerPageInput, { target: { value: '10' } });

    // Verifica que el valor sea el esperado
    expect(partidasPerPageInput.value).toBe('10');
  });

  test('debería deshabilitar los botones de navegación cuando no hay más páginas', async () => {
    render(<PartidaListAdmin />);

    // Verifica que el botón "Página anterior" esté deshabilitado
    const prevPageButton = screen.getByRole('button', { name: /página anterior/i });
    expect(prevPageButton).toBeDisabled();

    // Verifica que el botón "Página siguiente" esté deshabilitado
    const nextPageButton = screen.getByRole('button', { name: /página siguiente/i });
    expect(nextPageButton).toBeDisabled();
  });
});
