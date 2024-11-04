import tokenService from "../services/token.service";


const jwt = tokenService.getLocalAccessToken();
async function getCurrentUser(){
    const response = await fetch("/api/v1/users/current", {
        headers: { "Authorization": `Bearer ${jwt}` },
        method: "GET"
      });
      return response.json();
}

async function crearJugadorAPartida(idPartida){
    const usuario = await getCurrentUser();
    const jugador = {
        puntos: 0, partidaId: idPartida, userId: usuario.id
    }
    const response = await fetch("/api/v1/", {
        headers: { "Authorization": `Bearer ${jwt}` },
        method: "POST"
      });
      return response.json();
}