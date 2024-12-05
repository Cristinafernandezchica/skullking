import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import PartidaListAdmin from "./PartidaListAdmin";
import * as tokenService from "../../services/token.service";
import "@testing-library/jest-dom/extend-expect";

// Mock de servicios y fetch
jest.mock("../../services/token.service", () => ({
  getLocalAccessToken: jest.fn(),
}));

// Mock global de fetch
beforeEach(() => {
  global.fetch = jest.fn(); // Mock explícito
});

describe("PartidaListAdmin Component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("debe renderizar correctamente el componente", () => {
    tokenService.getLocalAccessToken.mockReturnValue("fake-jwt-token");
    render(<PartidaListAdmin />);

    // Comprobar que los elementos clave están en el DOM
    expect(screen.getByRole("heading", { name: /Partidas/i })).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Buscar por nombre")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Partidas por página")).toBeInTheDocument();
    expect(screen.getByText(/Página anterior/i)).toBeInTheDocument();
    expect(screen.getByText(/Página siguiente/i)).toBeInTheDocument();
  });

  test("debe mostrar un mensaje de error si la API falla", async () => {
    tokenService.getLocalAccessToken.mockReturnValue("fake-jwt-token");

    // Mock de fetch para simular error
    fetch.mockRejectedValueOnce(new Error("Error al obtener las partidas"));

    render(<PartidaListAdmin />);

    // Esperar que aparezca el mensaje de error
    await waitFor(() => {
      expect(screen.getByText(/Error al obtener las partidas/i)).toBeInTheDocument();
    });
  });

  test("debe mostrar la lista de partidas correctamente", async () => {
    tokenService.getLocalAccessToken.mockReturnValue("fake-jwt-token");

    const partidasMock = [
      {
        id: 1,
        nombre: "Partida 1",
        estado: "ESPERANDO",
        ownerPartida: 1,
        inicio: "2023-11-26T10:30:00Z",
      },
      {
        id: 2,
        nombre: "Partida 2",
        estado: "JUGANDO",
        ownerPartida: 2,
        inicio: "2023-11-25T10:30:00Z",
      },
    ];

    // Mock de fetch para simular una respuesta exitosa
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => partidasMock,
    });

    render(<PartidaListAdmin />);

    // Esperar que se cargue la lista
    await waitFor(() => {
      expect(screen.getByText("Partida 1")).toBeInTheDocument();
      expect(screen.getByText("Partida 2")).toBeInTheDocument();
    });
  });

  test("debe filtrar partidas por nombre correctamente", async () => {
    // Mock del token y datos de la API
    tokenService.getLocalAccessToken.mockReturnValue("fake-jwt-token");

    const partidasMock = [
      {
        id: 1,
        nombre: "Partida A",
        estado: "ESPERANDO",
        ownerPartida: 1,
        inicio: "2023-11-26T10:30:00Z",
      },
      {
        id: 2,
        nombre: "Partida B",
        estado: "JUGANDO",
        ownerPartida: 2,
        inicio: "2023-11-25T10:30:00Z",
      },
    ];

    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => partidasMock,
    });

    render(<PartidaListAdmin />);

    // Esperar que se cargue la lista
    await waitFor(() => {
      expect(screen.getByText("Partida A")).toBeInTheDocument();
      expect(screen.getByText("Partida B")).toBeInTheDocument();
    });

    // Filtrar por nombre
    const searchInput = screen.getByPlaceholderText("Buscar por nombre");
    fireEvent.change(searchInput, { target: { value: "A" } });

    // Verificar el resultado filtrado
    await waitFor(() => {
      expect(screen.getByText("Partida A")).toBeInTheDocument();
      expect(screen.queryByText("Partida B")).not.toBeInTheDocument();
    });
  });

  test("debe cambiar el número de partidas por página correctamente", async () => {
    // Mock del token y datos de la API
    tokenService.getLocalAccessToken.mockReturnValue("fake-jwt-token");

    const partidasMock = [
      { id: 1, nombre: "Partida 1", estado: "ESPERANDO", ownerPartida: 1, inicio: "2023-11-26T10:30:00Z" },
      { id: 2, nombre: "Partida 2", estado: "JUGANDO", ownerPartida: 2, inicio: "2023-11-25T10:30:00Z" },
      { id: 3, nombre: "Partida 3", estado: "TERMINADA", ownerPartida: 3, inicio: "2023-11-24T10:30:00Z" },
    ];

    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => partidasMock,
    });

    render(<PartidaListAdmin />);

    // Esperar que se cargue la lista completa
    await waitFor(() => {
      expect(screen.getByText("Partida 1")).toBeInTheDocument();
      expect(screen.getByText("Partida 2")).toBeInTheDocument();
      expect(screen.getByText("Partida 3")).toBeInTheDocument();
    });

    // Cambiar partidas por página
    const partidasPorPaginaInput = screen.getByPlaceholderText("Partidas por página");
    fireEvent.change(partidasPorPaginaInput, { target: { value: "1" } });

    // Verificar que se muestra solo una partida por página
    await waitFor(() => {
      expect(screen.getByText("Partida 1")).toBeInTheDocument();
      expect(screen.queryByText("Partida 2")).not.toBeInTheDocument();
    });
  });

  test("debe paginar correctamente las partidas", async () => {
    // Mock del token y datos de la API
    tokenService.getLocalAccessToken.mockReturnValue("fake-jwt-token");

    const partidasMock = [
      { id: 1, nombre: "Partida 1", estado: "ESPERANDO", ownerPartida: 1, inicio: "2023-11-26T10:30:00Z" },
      { id: 2, nombre: "Partida 2", estado: "JUGANDO", ownerPartida: 2, inicio: "2023-11-25T10:30:00Z" },
    ];

    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => partidasMock,
    });

    render(<PartidaListAdmin />);

    // Cambiar el número de partidas por página
    const partidasPorPaginaInput = screen.getByPlaceholderText("Partidas por página");
    fireEvent.change(partidasPorPaginaInput, { target: { value: "1" } });

    // Navegar a la siguiente página
    const nextPageButton = screen.getByText(/Página siguiente/i);
    fireEvent.click(nextPageButton);

    // Verificar que muestra la segunda partida
    await waitFor(() => {
      expect(screen.getByText("Partida 2")).toBeInTheDocument();
      expect(screen.queryByText("Partida 1")).not.toBeInTheDocument();
    });
  });
});
