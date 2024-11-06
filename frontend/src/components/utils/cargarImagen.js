import React from 'react';

function CartaImagen({ imagenFrontal }) {
    return (
        <img
            src={`/static/images/cartas/${imagenFrontal}`}
            alt={`Carta ${imagenFrontal}`}
            className="imagen-carta"
        />
    );
}

export default CartaImagen;