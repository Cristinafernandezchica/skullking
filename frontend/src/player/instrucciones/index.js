import React from 'react';
import '../../App.css';
import '../../static/css/home/home.css';
import pdf from '../../static/pdf/skullKing-instrucciones.pdf'; // Ajusta el path si es necesario

export default function Instructions() {
    return (
        <div>
                <iframe 
                    src={pdf} 
                    width="100%" 
                    height="1200px" 
                    title="PDF Viewer"
                    style={{ border: 'none' }}
                />
        </div>
    );
}