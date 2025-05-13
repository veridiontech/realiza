import React, { useState, useEffect, useRef } from 'react';
import { Upload } from 'lucide-react';
import { Button } from '@/components/ui/button';
import axios from 'axios';
import { ip } from '@/utils/ip';
import { useUser } from '@/context/user-provider';
import { useClient } from '@/context/Client-Provider';

interface Document {
  title: string;
  status: string;
  validity: string;
  id: string;
}

export const UploadDocumentModal = ({ isOpen, onClose }: { isOpen: boolean, onClose: () => void }) => {
  const [documents, setDocuments] = useState<Document[]>([]);
  const fileInputRefs = useRef<Record<string, HTMLInputElement | null>>({});
  const { user } = useUser();
  const { client } = useClient();

  const fetchDocuments = async () => {
    try {
      let idSearch = null;

      if (user?.role === 'ROLE_SUPPLIER_RESPONSIBLE' || user?.role === 'ROLE_SUPPLIER_MANAGER') {
        idSearch = user.supplier;
      } else if (user?.role === 'ROLE_CLIENT_RESPONSIBLE') {
        idSearch = client?.idClient;
      }

      if (!idSearch) {
        console.warn("ID de busca indefinido para esse tipo de usuário.");
        return;
      }

      const response = await axios.get(`${ip}/document/supplier/filtered-supplier`, {
        params: { size: 100000, idSearch },
      });

      const docs = Array.isArray(response.data.content) ? response.data.content : [];
      setDocuments(docs);
    } catch (error) {
      console.error("Erro ao carregar documentos:", error);
    }
  };

  const handleFileUpload = async (docId: string, file: File) => {
    const formData = new FormData();
    formData.append('file', file);

    try {
      await axios.post(`${ip}/document/supplier/${docId}/upload`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      alert('Upload feito com sucesso!');
    } catch (error) {
      console.error('Erro ao fazer upload:', error);
      alert('Erro ao enviar o arquivo.');
    }
  };

  const handleFileChange = (docId: string, event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      handleFileUpload(docId, file);
    }
  };

  const triggerFileInput = (docId: string) => {
    fileInputRefs.current[docId]?.click();
  };

  useEffect(() => {
    if (isOpen) {
      fetchDocuments();
    }
  }, [isOpen]);

  if (!isOpen) return null;

return (
    <div className="fixed inset-0 bg-gray-500 bg-opacity-50 flex justify-center items-center z-50">
      <div className="bg-white p-8 rounded-lg w-[900px] max-h-[700px] overflow-y-auto shadow-xl">
        <div className="overflow-y-auto max-h-[500px]">
          <table className="min-w-full table-auto">
            <thead>
              <tr>
                <th className="px-20 py-4 text-left text-base">Documento</th>
                <th className="px-6 py-4 text-left text-base">Status</th>
                <th className="px-6 py-4 text-left text-base">Validade</th>
                <th className="px-6 py-4 text-left text-base">Ações</th>
              </tr>
            </thead>
            <tbody>
              {documents.map((doc) => (
                <tr key={doc.id} className="border-t">
                  <td className="px-6 py-4 text-sm">{doc.title}</td>
                  <td className="px-6 py-4 text-sm">{doc.status}</td>
                  <td className="px-6 py-4 text-sm">{doc.validity || '-'}</td>
                  <td className="px-6 py-4">
                    <input
                      type="file"
                      accept=".pdf"
                      className="hidden"
                      ref={(el) => (fileInputRefs.current[doc.id] = el)}
                      onChange={(e) => handleFileChange(doc.id, e)}
                    />
                    <Button
                      type="button"
                      onClick={() => triggerFileInput(doc.id)}
                      className="bg-realizaBlue text-white flex items-center justify-center p-2 hover:bg-blue-700 w-[40px] h-[40px]"
                    >
                      <Upload size={20} />
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="flex justify-start mt-8">
          <Button variant="ghost" onClick={onClose} className="px-6 py-3 text-base">Fechar</Button>
        </div>
      </div>
    </div>
  );
};
