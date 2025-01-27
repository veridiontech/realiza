import { useState, useEffect } from "react";
import axios from "axios";
import { Table } from "@/components/ui/table";
import { ButtonBlue } from "@/components/ui/buttonBlue";
import { AddDocument } from "./modals/addDocument";
import { useParams } from "react-router-dom";
import { Eye, Edit } from "lucide-react";
import { ip } from "@/utils/ip";
import { DocumentViewer } from "./modals/viewDoc";

interface Employee {
  id: string;
  name: string;
  status: string;
}

interface Document {
  idDocumentation: string;
  title: string;
  creationDate: string;
  status: string;
}

export function DetailsEmployee() {
  const { id } = useParams<{ id: string }>();
  const [employee, setEmployee] = useState<Employee | null>(null);
  const [documents, setDocuments] = useState<Document[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isViewerOpen, setIsViewerOpen] = useState(false);
  const [selectedDocumentId, setSelectedDocumentId] = useState<string | null>(
    null,
  );

  const fetchEmployee = async () => {
    try {
      const response = await axios.get(`${ip}/employee/brazilian/${id}`);
      const data = response.data;
      setEmployee({
        id: data.idEmployee,
        name: data.name,
        status: data.isActive ? "Ativo" : "Desligado",
      });
    } catch (err: any) {
      setError(
        err.response?.data?.message || "Erro ao carregar o Colaborador.",
      );
    }
  };

  const fetchDocuments = async () => {
    try {
      const response = await axios.get(
        `${ip}/document/employee/filtered-employee`,
        {
          params: {
            idSearch: id,
            page: 0,
            size: 10,
            sort: "creationDate",
            direction: "DESC",
          },
        },
      );
      setDocuments(response.data.content || []);
    } catch (err: any) {
      setError(
        err.response?.data?.message || "Erro ao carregar os documentos.",
      );
    }
  };

  useEffect(() => {
    setIsLoading(true);
    Promise.all([fetchEmployee(), fetchDocuments()]).finally(() =>
      setIsLoading(false),
    );
  }, [id]);

  if (isLoading) {
    return <p>Carregando...</p>;
  }

  if (error) {
    return <p className="text-red-500">Erro: {error}</p>;
  }

  if (!employee) {
    return <p>Colaborador não encontrado.</p>;
  }

  const columns = [
    {
      key: "title" as keyof Document,
      label: "Documento",
    },
    {
      key: "creationDate" as keyof Document,
      label: "Data de Envio",
      render: (creationDate: string) => {
        const formattedDate = new Date(creationDate).toLocaleDateString(
          "pt-BR",
          {
            day: "2-digit",
            month: "2-digit",
            year: "2-digit",
          },
        );
        return formattedDate;
      },
    },
    {
      key: "dueDate" as keyof Document,
      label: "Data de Vencimento",
      render: (_: string, __: Document) => "-",
    },
    {
      key: "status" as keyof Document,
      label: "Status",
      render: (status: string) => (
        <span
          className={`text-sm font-medium ${
            status === "ativo" ? "text-green-600" : "text-red-600"
          }`}
        >
          {status}
        </span>
      ),
    },
    {
      key: "actions" as keyof Document,
      label: "Ações",
      render: (_: string, row: Document) => (
        <div className="flex space-x-2">
          <button
            className="text-blue-500 hover:underline"
            onClick={() => {
              setSelectedDocumentId(row.idDocumentation);
              setIsViewerOpen(true);
            }}
          >
            <Eye size={16} />
          </button>
          <button
            className="text-yellow-500 hover:underline"
            onClick={() => console.log(`Editar: ${row.idDocumentation}`)}
          >
            <Edit size={16} />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="flex h-full w-full flex-col overflow-auto bg-gray-100 p-10">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-xl font-bold">Detalhes do Colaborador</h1>
      </div>
      <div className="flex flex-col space-y-6">
        <div className="rounded-lg bg-white p-6 shadow">
          <div className="flex flex-col items-center">
            <div className="mb-4 h-20 w-20 rounded-full bg-blue-100"></div>
            <h3 className="text-lg font-medium">{employee.name}</h3>
            <p className="text-sm text-gray-500">Status: {employee.status}</p>
          </div>
        </div>
        <div className="ml-10 flex justify-start">
          <ButtonBlue onClick={() => setIsModalOpen(true)}>
            Adicionar Documento
          </ButtonBlue>
        </div>
        <div className="flex flex-row items-start space-x-4">
          <div className="flex-[2]">
            <Table<Document> data={documents} columns={columns} />
          </div>
          <div className="flex flex-[1] items-center justify-center bg-black p-4">
            <span className="text-white">teste</span>
          </div>
        </div>
      </div>
      {isModalOpen && (
        <AddDocument
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          employeeId={employee.id}
        />
      )}
      {isViewerOpen && selectedDocumentId && (
        <DocumentViewer
          documentId={selectedDocumentId}
          onClose={() => setIsViewerOpen(false)}
        />
      )}
    </div>
  );
}
