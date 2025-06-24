import React, { useEffect, useState } from "react";
import axios from "axios";
import { Modal } from "@/components/modal";
import { ip } from "@/utils/ip";

interface AddDocumentSuppliersProps {
  isOpen: boolean;
  onClose: () => void;
  documentId: string | null;
  preSelectedTitle?: string | null;
}

export const AddDocument: React.FC<AddDocumentSuppliersProps> = ({
  isOpen,
  onClose,
  documentId,
  preSelectedTitle,
}) => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);
  const [statusType, setStatusType] = useState<"success" | "error" | null>(null);

  useEffect(() => {
    console.log("documentID:", documentId);
  }, [documentId]);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];

    if (!file) return;

    if (file.type !== "application/pdf") {
      setStatusMessage("Por favor, selecione um arquivo PDF.");
      setStatusType("error");
      return;
    }

    setSelectedFile(file);
    setStatusMessage(null);
    setStatusType(null);
  };

  const handleSubmit = async () => {
    console.log("📦 Submetendo upload para documentId:", documentId);

    if (!selectedFile) {
      setStatusMessage("Por favor, selecione um arquivo antes de enviar.");
      setStatusType("error");
      return;
    }

    if (!documentId) {
      setStatusMessage("ID do documento não encontrado.");
      setStatusType("error");
      console.warn("⚠️ documentId está indefinido no submit.");
      return;
    }

    const formData = new FormData();
    formData.append("file", selectedFile);

    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");

      await axios.post(`${ip}/document/supplier/${documentId}/upload`, formData, {
        headers: {
          Authorization: `Bearer ${tokenFromStorage}`,
          "Content-Type": "multipart/form-data",
        },
      });

      setStatusMessage("Arquivo enviado com sucesso!");
      setStatusType("success");
      setSelectedFile(null);
    } catch (error: any) {
      console.error("❌ Erro ao enviar:", error.response?.data || error.message);
      setStatusMessage("Erro ao enviar o arquivo. Tente novamente.");
      setStatusType("error");
    }
  };

  return isOpen ? (
    <Modal
      title="Upload de Documento"
      onClose={onClose}
      onSubmit={handleSubmit}
      fields={[
        {
          name: "title",
          label: "Nome do Documento",
          type: "custom",
          render: () => (
            <input
              type="text"
              readOnly
              value={preSelectedTitle || ""}
              className="mb-4 w-full rounded border-none bg-transparent p-2 text-sm text-white focus:outline-none"
            />
          ),
        },
        {
          name: "file",
          label: "Arquivo PDF",
          type: "custom",
          render: () => (
            <div className="flex flex-col">
              <label htmlFor="file-upload" className="mb-2 text-sm font-medium text-yellow-400">
                Selecione o Arquivo
              </label>
              <input
                id="file-upload"
                type="file"
                accept=".pdf"
                onChange={handleFileChange}
                className="hidden"
              />
              <label
                htmlFor="file-upload"
                className="cursor-pointer rounded bg-realizaBlue px-4 py-2 text-center text-sm text-white hover:bg-blue-700"
              >
                {selectedFile ? selectedFile.name : "Escolher Arquivo"}
              </label>
            </div>
          ),
        },
      ]}
    >
      {statusMessage && (
        <p className={`mt-4 text-sm ${statusType === "success" ? "text-green-500" : "text-red-500"}`}>
          {statusMessage}
        </p>
      )}
    </Modal>
  ) : null;
};
