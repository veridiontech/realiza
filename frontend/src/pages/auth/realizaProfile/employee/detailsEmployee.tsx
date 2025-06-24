import { useState, useEffect } from "react";
import axios from "axios";
import { Table } from "@/components/ui/tableVanila";
import { AddDocument } from "./modals/addDocument";
import { useParams } from "react-router-dom";
import { Eye, Edit, User } from "lucide-react";
import { ip } from "@/utils/ip";
import { DocumentViewer } from "./modals/viewDoc";
import { Blocks } from "react-loader-spinner";
import { toast } from "sonner";

interface Document {
  idDocument: string;
  title: string;
  creationDate: string;
  status: string;
}

export function DetailsEmployee() {
  const { id } = useParams<{ id: string }>();
  const [employee, setEmployee] = useState<any | null>(null);
  const [documents, setDocuments] = useState<Document[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isViewerOpen, setIsViewerOpen] = useState(false);
  const [selectedDocumentId, setSelectedDocumentId] = useState<string | null>(null);
  const [selectedDocumentTitle, setSelectedDocumentTitle] = useState<string | null>(null);

  const handleStatusChange = (id: string, newStatus: string) => {
    setDocuments(prevDocuments =>
      prevDocuments.map(doc =>
        doc.idDocument === id ? { ...doc, status: newStatus } : doc
      )
    );
  };

  // 🔄 Carrega dados do colaborador
  const fetchEmployee = async () => {
    try {
      const token = localStorage.getItem("tokenClient");
      const response = await axios.get(`${ip}/employee/brazilian/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setEmployee(response.data);
    } catch (err: any) {
      setError(err.response?.data?.message || "Erro ao carregar o Colaborador.");
    }
  };

  // 🔄 Carrega documentos
  const fetchDocuments = async () => {
    try {
      const token = localStorage.getItem("tokenClient");
      const response = await axios.get(`${ip}/document/employee/filtered-employee`, {
        params: {
          idSearch: id,
          page: 0,
          size: 10,
          sort: "creationDate",
          direction: "DESC",
        },
        headers: { Authorization: `Bearer ${token}` },
      });
      setDocuments(response.data.content || []);
    } catch (err: any) {
      setError(err.response?.data?.message || "Erro ao carregar os documentos.");
    }
  };

  const updateSituation = async (newSituation: string) => {
    const token = localStorage.getItem("tokenClient");

    const payload = { situation: newSituation };
    const formData = new FormData();
    formData.append(
      "employeeBrazilianRequestDto",
      new Blob([JSON.stringify(payload)], { type: "application/json" })
    );

    try {
      await axios.put(`${ip}/employee/brazilian/${id}`, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data",
        },
      });
      toast.success("Situação atualizada com sucesso!");
      fetchEmployee();
    } catch (error: any) {
      console.error("Erro ao atualizar situação:", error.response?.data || error.message);
      toast.error("Erro ao atualizar situação.");
    }
  };

  useEffect(() => {
    setIsLoading(true);
    Promise.all([fetchEmployee(), fetchDocuments()]).finally(() => setIsLoading(false));
  }, [id]);

  const columns: {
    key: keyof Document;
    label: string;
    render?: (value: string, row: Document) => React.ReactNode;
  }[] = [
      {
        key: "title",
        label: "Documento",
      },
      {
        key: "creationDate",
        label: "Data de Envio",
        render: (value: string) =>
          new Date(value).toLocaleDateString("pt-BR", {
            day: "2-digit",
            month: "2-digit",
            year: "2-digit",
          }),
      },
      {
        key: "status",
        label: "Status",
        render: (value: string) => (
          <span className={`text-sm font-medium ${value === "ativo" ? "text-green-600" : "text-red-600"}`}>
            {value}
          </span>
        ),
      },
      {
        key: "idDocument",
        label: "Ações",
        render: (_: string, row: Document) => (
          <div className="flex space-x-2">
            <button
              className="text-realizaBlue hover:underline"
              onClick={() => {
                setSelectedDocumentId(row.idDocument);
                setIsViewerOpen(true);
              }}
            >
              <Eye size={16} />
            </button>
            <button
              className="text-yellow-500 hover:underline"
              onClick={() => {
                setSelectedDocumentId(row.idDocument);
                setSelectedDocumentTitle(row.title);
                setTimeout(() => setIsModalOpen(true), 0);
              }}
            >
              <Edit size={16} />
            </button>
          </div>
        ),
      },
    ];

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Blocks height="80" width="80" color="#34495E" visible />
      </div>
    );
  }

  if (error) return <p className="text-red-500">Erro: {error}</p>;
  if (!employee) return <p>Colaborador não encontrado.</p>;

  return (
    <div className="flex h-full w-full flex-col overflow-auto bg-gray-100 p-10">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-xl font-bold">Detalhes do Colaborador</h1>
      </div>

      <div className="flex flex-col space-y-6">
        <div className="rounded-lg bg-white p-6 shadow">
          <div className="flex gap-2 items-start">
            <div className="bg-realizaBlue flex h-20 w-20 items-center justify-center rounded-full">
              <User className="text-white" />
            </div>
            <div className="flex flex-col gap-1">
              <h3 className="text-lg font-medium">{employee.name} {employee.surname}</h3>
              <div className="flex items-center gap-2">
                <label htmlFor="situation" className="text-sm text-gray-500">Situação:</label>
                <select
                  id="situation"
                  value={employee.situation}
                  onChange={(e) => updateSituation(e.target.value)}
                  className="rounded border p-1 text-sm"
                >
                  <option value="ALOCADO">Alocado</option>
                  <option value="DESALOCADO">Desalocado</option>
                  <option value="DEMITIDO">Demitido</option>
                  <option value="AFASTADO">Afastado</option>
                  <option value="LICENCA_MATERNIDADE">Licença Maternidade</option>
                  <option value="LICENCA_MEDICA">Licença Médica</option>
                  <option value="LICENCA_MILITAR">Licença Militar</option>
                  <option value="FERIAS">Férias</option>
                  <option value="ALISTAMENTO_MILITAR">Alistamento Militar</option>
                  <option value="APOSENTADORIA_POR_INVALIDEZ">Aposentadoria por Invalidez</option>
                </select>
              </div>
            </div>
          </div>
        </div>

        <div className="flex flex-row space-x-4">
          <div className="flex-[2]">
            <Table<Document> data={documents} columns={columns} />
          </div>
          <div className="flex flex-1 flex-col">
            <h2 className="text-lg font-medium">Histórico</h2>
            <div className="h-[40vh] overflow-auto rounded-md bg-white p-4 shadow-md">
              <table className="w-full">
                <thead>
                  <tr>
                    <th className="p-2 text-left text-sm text-stone-600">Atividade</th>
                    <th className="p-2 text-left text-sm text-stone-600">Tipo de Atividade</th>
                    <th className="p-2 text-left text-sm text-stone-600">Feito por</th>
                    <th className="p-2 text-left text-sm text-stone-600">Data</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    {/* <td className="p-2 text-stone-600">...</td> */}
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      {isModalOpen && (
        <AddDocument
          isOpen={isModalOpen}
          onClose={() => {
            setIsModalOpen(false);
            setSelectedDocumentId(null);
            setSelectedDocumentTitle(null);
          }}
          documentId={selectedDocumentId}
          preSelectedTitle={selectedDocumentTitle}
        />
      )}

      {isViewerOpen && selectedDocumentId && (
        <DocumentViewer
          documentId={selectedDocumentId}
          onClose={() => setIsViewerOpen(false)}
          onStatusChange={handleStatusChange}
        />
      )}
    </div>
  );
}
