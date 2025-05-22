import React, { useState, useEffect, useRef } from 'react';
import { Upload, Eye } from 'lucide-react';
import { Button } from '@/components/ui/button';
import axios from 'axios';
import { ip } from '@/utils/ip';
import { useUser } from '@/context/user-provider';
import { useClient } from '@/context/Client-Provider';
import { toast } from 'sonner';
import { Oval } from 'react-loader-spinner';

interface Document {
  title: string;
  status: string;
  validity: string;
  idDocument: string;
  fileName?: string;
}

export const UploadDocumentModal = ({ isOpen, onClose }: { isOpen: boolean, onClose: () => void }) => {
  const [documents, setDocuments] = useState<Document[]>([]);
  const fileInputRefs = useRef<Record<string, HTMLInputElement | null>>({});
  const { user } = useUser();
  const { client } = useClient();
  const [loadingDocs, setLoadingDocs] = useState<Record<string, boolean>>({});
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  const handlePreviewDocument = async (docId: string) => {
    try {
      const token = localStorage.getItem('tokenClient');
      if (!token) {
        toast.error("Token de autenticação não encontrado.");
        return;
      }

      const response = await axios.get(`${ip}/document/supplier/${docId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      const { fileData, fileContentType } = response.data;

      if (!fileData || !fileContentType) {
        toast.error("Arquivo não encontrado.");
        return;
      }

      const fileUrl = `data:${fileContentType};base64,${fileData}`;
      setPreviewUrl(fileUrl);
    } catch (error) {
      console.error("Erro ao visualizar documento:", error);
      toast.error("Erro ao visualizar documento.");
    }
  };

  const closePreviewModal = () => {
    setPreviewUrl(null);
  };

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

      const token = localStorage.getItem('tokenClient');
      console.log("aaa", idSearch)

      const response = await axios.get(`${ip}/document/supplier/filtered-supplier`, {
        params: { size: 100000, idSearch },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      const docs = Array.isArray(response.data.content) ? response.data.content : [];
      setDocuments(docs);
    } catch (error) {
      console.error("Erro ao carregar documentos:", error);
    }
  };

  const handleFileUpload = async (docId: string, file: File) => {
    if (!file) {
      alert('Nenhum arquivo selecionado');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    setLoadingDocs(prev => ({ ...prev, [docId]: true }));

    try {
      const response = await axios.post(`${ip}/document/supplier/${docId}/upload`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      if (response.status === 200) {
        toast.success("Arquivo enviado com Sucesso!");
      } else {
        alert('Erro ao enviar o arquivo. Tente novamente.');
      }
    } catch (error) {
      console.error('Erro ao fazer upload:', error);
      alert('Erro ao enviar o arquivo.');
    } finally {
      setLoadingDocs(prev => ({ ...prev, [docId]: false }));
      fetchDocuments();
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
      <div className="bg-white p-8 rounded-lg w-[1500px] max-h-[700px] overflow-y-auto shadow-xl">
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
              {documents.map((doc) => {
                const getStatus = () => {
                  if (user?.role === 'ROLE_REALIZA_PLUS' || user?.role === 'ROLE_REALIZA_BASIC') {
                    return doc.status;
                  } else {
                    if (doc.status === 'APROVADO_IA' || doc.status === 'REPROVADO_IA') {
                      return 'EM_ANALISE';
                    }
                    return doc.status;
                  }
                };

                return (
                  <tr key={doc.idDocument} className="border-t">
                    <td className="px-6 py-4 text-sm">{doc.title}</td>
                    <td className="px-6 py-4 text-sm">{getStatus()}</td>
                    <td className="px-6 py-4 text-sm">{doc.validity || '-'}</td>
                    <td className="px-6 py-4">
                      <div className="flex items-center space-x-2">
                        {doc.status !== 'PENDENTE' && doc.fileName && (
                          <button
                            type="button"
                            onClick={() => handlePreviewDocument(doc.idDocument)}
                            className="text-black hover:text-gray-600"
                            title="Visualizar documento"
                          >
                            <Eye size={18} />
                          </button>
                        )}

                        <input
                          type="file"
                          accept=".pdf"
                          className="hidden"
                          ref={(el) => (fileInputRefs.current[doc.idDocument] = el)}
                          onChange={(e) => handleFileChange(doc.idDocument, e)}
                        />

                        <Button
                          type="button"
                          onClick={() => triggerFileInput(doc.idDocument)}
                          className="bg-realizaBlue text-white flex items-center justify-center p-2 hover:bg-blue-700 w-[40px] h-[40px]"
                          disabled={loadingDocs[doc.idDocument] === true}
                        >
                          {loadingDocs[doc.idDocument] ? (
                            <Oval
                              visible={true}
                              height={24}
                              width={24}
                              color="#fff"
                              ariaLabel="loading"
                            />
                          ) : (
                            <Upload size={20} />
                          )}
                        </Button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>

        {previewUrl && (
          <div
            className="fixed inset-0 bg-black bg-opacity-75 flex justify-center items-center z-50"
            onClick={closePreviewModal}
          >
            <div
              className="bg-white p-4 rounded-lg max-w-[90vw] max-h-[90vh]"
              onClick={(e) => e.stopPropagation()}
            >
              <iframe src={previewUrl} width="800" height="600" />
              <button
                onClick={closePreviewModal}
                className="mt-2 px-4 py-2 bg-gray-300 rounded"
              >
                Fechar
              </button>
            </div>
          </div>
        )}

        <div className="flex justify-start mt-8">
          <Button variant="ghost" onClick={onClose} className="px-6 py-3 text-base">
            Fechar
          </Button>
        </div>
      </div>
    </div>
  );
};
