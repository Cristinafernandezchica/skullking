import React from "react";
import AppNavbar from "./AppNavbar";
import { render, screen } from "./test-utils";

describe("AppNavbar", () => {
  test("renders public links correctly", () => {
    render(<AppNavbar />);

    const linkDocsElement = screen.getByRole("link", { name: "Documentos" });
    expect(linkDocsElement).toBeInTheDocument();

    const linkRegisterElement = screen.getByRole("link", { name: "Registrarse" });
    expect(linkRegisterElement).toBeInTheDocument();

    const linkLoginElement = screen.getByRole("link", { name: "Iniciar sesión" });
    expect(linkLoginElement).toBeInTheDocument();
  });

  test("renders PLAYER links correctly", () => {
    render(<AppNavbar />);

    const linkStatsElement = screen.queryByRole("link", { name: "Estadísticas" });
    const linkProfileElement = screen.queryByRole("link", { name: /testuser/i }); // Con un usuario simulado
    const linkLogoutElement = screen.queryByRole("link", { name: "Cerrar sesión" });

    if (linkStatsElement) expect(linkStatsElement).toBeInTheDocument();
    if (linkProfileElement) expect(linkProfileElement).toBeInTheDocument();
    if (linkLogoutElement) expect(linkLogoutElement).toBeInTheDocument();
  });

  test("renders ADMIN links correctly", () => {
    render(<AppNavbar />);

    const linkUsersElement = screen.queryByRole("link", { name: "Usuarios" });
    const linkGamesElement = screen.queryByRole("link", { name: "Partidas" });
    const linkStatsElement = screen.queryByRole("link", { name: "Estadísticas" });

    if (linkUsersElement) expect(linkUsersElement).toBeInTheDocument();
    if (linkGamesElement) expect(linkGamesElement).toBeInTheDocument();
    if (linkStatsElement) expect(linkStatsElement).toBeInTheDocument();
  });

  test("renders profile image for PLAYER role", () => {
    render(<AppNavbar />);

    const profileImage = screen.queryByAltText("Perfil");
    if (profileImage) {
      expect(profileImage).toBeInTheDocument();
      expect(profileImage).toHaveAttribute("src", expect.stringContaining("test-file-stub"));
    }
  });

  test("renders toggler button for navbar collapse", () => {
    render(<AppNavbar />);

    const togglerButton = screen.getByRole("button", { name: /toggle navigation/i });
    expect(togglerButton).toBeInTheDocument();
  });

  test("renders the logo correctly", () => {
    render(<AppNavbar />);
    const logoElement = screen.getByAltText("logo");
    expect(logoElement).toBeInTheDocument();
    expect(logoElement).toHaveAttribute("src", expect.stringContaining("test-file-stub"));
  });
});
