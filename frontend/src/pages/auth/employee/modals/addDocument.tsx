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
  const [fileName, setFileName] = useState("");
  const [status, setStatus] = useState("");

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      setSelectedFile(event.target.files[0]);
      setFileName(event.target.files[0].name);
    }
  };

  const handleSubmit = async (formData: Record<string, any>) => {
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
    data.append("file", selectedFile);

    try {
      const response = await axios.post(
        "https://realiza.onrender.com/document/employee",
        data,
      );
      setStatus("Arquivo enviado com sucesso!");
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
          render: ({ onChange }) => (
            <div className="flex flex-col items-start">
              <label className="w-full">
                <input
                  type="file"
                  accept=".pdf"
                  onChange={(e) => {
                    handleFileChange(e);
                    onChange(e.target.files?.[0]);
                  }}
                  className="hidden"
                  id="file-upload"
                />
                <div className="flex items-center gap-2">
                  <button
                    className="cursor-pointer rounded bg-blue-500 px-4 py-2 font-semibold text-white hover:bg-blue-600"
                    onClick={() =>
                      document.getElementById("file-upload")?.click()
                    }
                  >
                    Escolher Arquivo
                  </button>
                  {fileName && (
                    <span className="text-sm text-yellow-400">
                      {fileName.length > 30
                        ? `${fileName.substring(0, 30)}...`
                        : fileName}
                    </span>
                  )}
                </div>
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
