import React, { useState } from "react";
import axios from "axios";
import { Modal } from "@/components/modal";

interface AddDocumentProps {
  isOpen: boolean;
  onClose: () => void;
  employeeId: string;
}

export const AddDocument: React.FC<AddDocumentProps> = ({
  isOpen,
  onClose,
  employeeId,
}) => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [status, setStatus] = useState("");

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      const file = event.target.files[0];

      // Valida se o arquivo é do tipo PDF
      if (file.type !== "application/pdf") {
        alert("Por favor, selecione um arquivo no formato PDF.");
        setSelectedFile(null); // Reseta o arquivo selecionado
        return;
      }

      setSelectedFile(file); // Define o arquivo no estado
    }
  };

  const handleSubmit = async (formData: Record<string, any>) => {
    // Validação para garantir que o arquivo foi selecionado
    if (!selectedFile) {
      alert("Por favor, selecione um arquivo antes de enviar.");
      return;
    }

    const data = new FormData();

    const jsonBlob = new Blob(
      [
        JSON.stringify({
          title: formData.title,
          status: "ativo",
          employee: employeeId,
        }),
      ],
      { type: "application/json" },
    );

    data.append("documentEmployeeRequestDto", jsonBlob);
    data.append("file", selectedFile); // Adiciona o arquivo selecionado

    try {
      await axios.post("https://realiza.onrender.com/document/employee", data);
      setStatus("Arquivo enviado com sucesso!");
      setSelectedFile(null); // Reseta o arquivo após o envio
    } catch (error) {
      console.error(error);
      setStatus("Erro ao enviar o arquivo. Tente novamente.");
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
          label: "Tipo de Documento",
          type: "select",
          options: [
            "Contrato de Trabalho",
            "Declaração",
            "Comprovante de Residência",
            "Certificado",
          ],
          required: true,
        },
        {
          name: "file",
          label: "Arquivo PDF",
          type: "custom",
          render: () => (
            <div className="flex flex-col">
              <label
                htmlFor="file-upload"
                className="mb-2 text-sm font-medium text-yellow-400"
              >
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
                className="bg-realizaBlue hover:bg-realizaBlue cursor-pointer rounded px-4 py-2 text-white"
              >
                {selectedFile ? selectedFile.name : "Escolher Arquivo"}
              </label>
            </div>
          ),
        },
      ]}
    >
      {status && <p className="text-yellow-400">{status}</p>}
    </Modal>
  ) : null;
};
