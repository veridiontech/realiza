import { useState } from 'react';
import { Book } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { UploadDocumentModal } from '@/components/ui/upload-document-modal';

interface UploadDocumentButtonProps {
  text?: string;
}

export const UploadDocumentButton = ({ text = "Visualizar documentos" }: UploadDocumentButtonProps) => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  return (
    <div className="flex flex-col items-center justify-center">
      <Button
        type="button"
        onClick={handleOpenModal}
        variant="outline"
        className="border border-gray-300 bg-white text-[#2A3F57] hover:bg-gray-100 px-4 py-2 flex items-center gap-2 text-sm font-medium"
      >
        <Book size={18} className="text-[#2A3F57]" />
        {text}
      </Button>

      <UploadDocumentModal isOpen={isModalOpen} onClose={handleCloseModal} />
    </div>
  );
};
