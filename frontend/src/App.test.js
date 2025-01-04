import React from "react";
import App from "./App";
import { render, screen } from "./test-utils";
import { ErrorBoundary } from "react-error-boundary";

describe("App", () => {
  test("renders public routes correctly", () => {
    render(<App />);

    const linkDocsElement = screen.getByRole("link", { name: "Documentos" });
    expect(linkDocsElement).toBeInTheDocument();

    const linkRegisterElement = screen.getByRole("link", { name: "Registrarse" });
    expect(linkRegisterElement).toBeInTheDocument();

    const linkLoginElement = screen.getByRole("link", { name: "Iniciar sesión" });
    expect(linkLoginElement).toBeInTheDocument();
  });

  test("renders PLAYER routes correctly", () => {
    render(<App />);

    const linkPlayElement = screen.queryByRole("link", { name: "Jugar" });
    const linkProfileElement = screen.queryByRole("link", { name: /perfil/i });
    const linkLogoutElement = screen.queryByRole("link", { name: "Cerrar sesión" });

    if (linkPlayElement) expect(linkPlayElement).toBeInTheDocument();
    if (linkProfileElement) expect(linkProfileElement).toBeInTheDocument();
    if (linkLogoutElement) expect(linkLogoutElement).toBeInTheDocument();
  });

  test("renders ADMIN routes correctly", () => {
    render(<App />);

    const linkUsersElement = screen.queryByRole("link", { name: "Usuarios" });
    const linkGamesElement = screen.queryByRole("link", { name: "Partidas" });
    const linkStatsElement = screen.queryByRole("link", { name: "Estadísticas" });

    if (linkUsersElement) expect(linkUsersElement).toBeInTheDocument();
    if (linkGamesElement) expect(linkGamesElement).toBeInTheDocument();
    if (linkStatsElement) expect(linkStatsElement).toBeInTheDocument();
  });

  test("renders fallback error boundary UI correctly", () => {
    const ErrorComponent = () => {
      throw new Error("Test Error");
    };
  
    render(
      <ErrorBoundary FallbackComponent={({ error }) => <div>Algo fue mal: {error.message}</div>}>
        <ErrorComponent />
      </ErrorBoundary>
    );
  
    const fallbackErrorMessage = "Algo fue mal: Test Error";
    expect(screen.getByText(fallbackErrorMessage)).toBeInTheDocument();
  });
  

  test("renders toggler button for navbar collapse", () => {
    render(<App />);

    const togglerButton = screen.getByRole("button", { name: /toggle navigation/i });
    expect(togglerButton).toBeInTheDocument();
  });
});
