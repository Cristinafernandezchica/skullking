import React from 'react';
import '../formGenerator/css/modal.css'; 

const Modal = ({ show, handleClose, children }) => {
  return (
    <div className={`modal ${show ? 'show' : ''}`}>
      <div className="modal-content">
        <button className="close" onClick={handleClose}>
          &times;
        </button>
        {children}
      </div>
    </div>
  );
};

export default Modal;