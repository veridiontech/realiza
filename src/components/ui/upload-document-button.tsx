import React, { useState } from 'react';
import { Book } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { UploadDocumentModal } from '@/components/ui/upload-document-modal'; 

export const UploadDocumentButton = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);  // Estado para controle do modal

  const handleOpenModal = () => {
    setIsModalOpen(true);  // Abre o modal
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);  // Fecha o modal
  };

  return (
    <div className="flex flex-col items-center justify-center">
      {/* Botão de upload que abre o modal */}
      <Button
        type="button"
        onClick={handleOpenModal}
        className="bg-realizaBlue text-white p-2 hover:bg-blue-700 w-[40px] h-[40px] flex items-center justify-center"
      >
        <Book size={20} />
      </Button>

      {/* Modal de upload de documento */}
      <UploadDocumentModal isOpen={isModalOpen} onClose={handleCloseModal} />
    </div>
  );
};
