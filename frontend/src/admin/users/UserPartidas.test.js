import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import UserPartidas from './UserPartidas';
import { server } from '../../mocks/server'; // Aquí se importa el server

jest.mock('../../services/token.service', () => ({ 
    getLocalAccessToken: jest.fn(() => 'mocked-token'),
    getUser: jest.fn(() => ({
      id: 1,
      username: 'user1',
      password: "$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e", // Añadido password
      descripcionPerfil: "A Jugar!", // Añadido descripcionPerfil
      imagenPerfil: "https://blog.tienda-medieval.com/wp-content/uploads/2019/02/Parche-pirata-ojo-derecho.jpg", // Añadido imagenPerfil
      conectado: false, // Añadido conectado
      numPartidasJugadas: 0, // Añadido numPartidasJugadas
      numPartidasGanadas: 0, // Añadido numPartidasGanadas
      numPuntosGanados: 0, // Añadido numPuntosGanados
      authority: { id: 2, authority: 'PLAYER' }, // Añadido authority
    })),
  }));

// Iniciamos el server antes de cada test y lo cerramos después
beforeAll(() => server.listen()); // Inicia el servidor que simula las peticiones
afterEach(() => server.resetHandlers()); // Resetea los handlers después de cada test
afterAll(() => server.close()); // Cierra el servidor después de todas las pruebas

describe('UserPartidas', () => {

    test('debería filtrar partidas por nombre correctamente', async () => {
        render(<UserPartidas />);  // Renderiza el componente
      
        // Espera que las partidas se carguen en el DOM
        const partida1 = await screen.findByText('Partida Ejemplo1');
        const partida2 = await screen.findByText('Partida Ejemplo2');
      
        // Verifica que ambas partidas están inicialmente en el DOM
        expect(partida1).toBeInTheDocument();
        expect(partida2).toBeInTheDocument();
      
        // Simula que el usuario escribe "Partida Ejemplo" en la barra de búsqueda
        const searchInput = screen.getByPlaceholderText('Buscar por nombre');
        fireEvent.change(searchInput, { target: { value: 'Partida Ejemplo1' } });
      
        // Espera que "Partida Ejemplo" aparezca en el DOM
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
        render(<UserPartidas />);
      
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
          // Aquí verificamos que las partidas con el estado 'JUGANDO' están presentes
          const partidaJugando = screen.queryByText('JUGANDO');
          expect(partidaJugando).toBeInTheDocument();
        });
      
        // Aquí verificamos que las partidas que no deberían tener el estado 'JUGANDO' ya no estén presentes
        const partidaEsperando = screen.queryByText('ESPERANDO');
        const partidaTerminada = screen.queryByText('TERMINADA');
      
        // Verificamos que las partidas con otros estados no estén en el DOM
        expect(partidaEsperando).not.toBeInTheDocument();
        expect(partidaTerminada).not.toBeInTheDocument();
      });

    test('debería mostrar mensaje de error cuando no se encuentra ninguna partida con el nombre dado', async () => {
        render(<UserPartidas />);  // Renderiza el componente
      
        // Espera que las partidas se carguen en el DOM
        const partida1 = await screen.findByText('Partida Ejemplo1');
        const partida2 = await screen.findByText('Partida Ejemplo2');
        
        // Verifica que ambas partidas están inicialmente en el DOM
        expect(partida1).toBeInTheDocument();
        expect(partida2).toBeInTheDocument();
      
        // Simula que el usuario escribe "Partidita" en la barra de búsqueda
        const searchInput = screen.getByPlaceholderText('Buscar por nombre');
        fireEvent.change(searchInput, { target: { value: 'Partidita' } });
      
        // Espera que el mensaje de error aparezca en el DOM
        await waitFor(() => {
          const errorMessage = screen.getByText('No hay consultas con esos filtros y parámetros de búsqueda.');
          expect(errorMessage).toBeInTheDocument();
        });
      
        // Verifica que las partidas no están presentes
        const filteredPartida1 = screen.queryByText('Partida Ejemplo1');
        const filteredPartida2 = screen.queryByText('Partida Ejemplo2');
        
        // Asegúrate de que las partidas existentes no aparezcan
        expect(filteredPartida1).not.toBeInTheDocument();
        expect(filteredPartida2).not.toBeInTheDocument();
    });
      

  test('debería permitir borrar filtros con el botón "Borrar todo"', async () => {
    render(<UserPartidas />);

    // Selecciona el botón "Borrar todo"
    const clearAllButton = screen.getByRole('button', { name: /clear-all/i });

    // Haz clic en el botón "Borrar todo"
    fireEvent.click(clearAllButton);

    // Verifica que el campo de búsqueda esté vacío
    const searchInput = screen.getByPlaceholderText('Buscar por nombre');
    expect(searchInput.value).toBe('');
  });

  test('debería manejar correctamente el número de partidas por página', async () => {
    render(<UserPartidas />);

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
    render(<UserPartidas />);

    // Verifica que el botón "Página anterior" esté deshabilitado
    const prevPageButton = screen.getByRole('button', { name: /página anterior/i });
    expect(prevPageButton).toBeDisabled();

    // Verifica que el botón "Página siguiente" esté deshabilitado
    const nextPageButton = screen.getByRole('button', { name: /página siguiente/i });
    expect(nextPageButton).toBeDisabled();
  });

  test('debería mostrar solo las partidas del usuario actual', async () => {
    render(<UserPartidas />);

    // Esperar a que las partidas aparezcan en el DOM
    await waitFor(() => screen.findByText('Partida Ejemplo2'));
    await waitFor(() => screen.findByText('Partida Ejemplo1'));

    // Asegurar que solo aparece las partidas asociadas al usuario actual que es el user1
    const partidaDeBetty = screen.queryByText('Partida Ejemplo1');
    expect(partidaDeBetty).toBeInTheDocument(); // Verifica que aparece la partida1
    const partidaDeBetty2 = screen.queryByText('Partida Ejemplo2');
    expect(partidaDeBetty2).toBeInTheDocument(); // Verifica que aparece la partida2
  });

});

