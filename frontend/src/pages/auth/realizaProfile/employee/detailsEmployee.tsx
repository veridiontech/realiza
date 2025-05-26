import { useState, useEffect } from "react";
import axios from "axios";
import { Table } from "@/components/ui/tableVanila";
// import { ButtonBlue } from "@/components/ui/buttonBlue";
import { AddDocument } from "./modals/addDocument";
import { useParams } from "react-router-dom";
import { Eye, Edit, User } from "lucide-react";
import { ip } from "@/utils/ip";
import { DocumentViewer } from "./modals/viewDoc";
import { Blocks } from "react-loader-spinner";
import { toast } from "sonner";

interface Document {
  idDocumentation: string;
  title: string;
  creationDate: string;
  status: string;
}

export function DetailsEmployee() {
  const { id } = useParams<{ id: string }>();
  const [employee, setEmployee] = useState<any | null>(null);
  const [documents, setDocuments] = useState<Document[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isViewerOpen, setIsViewerOpen] = useState(false);
  const [selectedDocumentId, setSelectedDocumentId] = useState<string | null>(null);

  const fetchEmployee = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.get(`${ip}/employee/brazilian/${id}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setEmployee(response.data);
    } catch (err: any) {
      setError(err.response?.data?.message || "Erro ao carregar o Colaborador.");
    }
  };

  const fetchDocuments = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.get(`${ip}/document/employee/filtered-employee`, {
        params: {
          idSearch: id,
          page: 0,
          size: 10,
          sort: "creationDate",
          direction: "DESC",
        },
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setDocuments(response.data.content || []);
    } catch (err: any) {
      setError(err.response?.data?.message || "Erro ao carregar os documentos.");
    }
  };

  const updateSituation = async (newSituation: string) => {
    const tokenFromStorage = localStorage.getItem("tokenClient");

    const payload = {
      situation: newSituation,
    };

    const formData = new FormData();
    formData.append(
      "employeeBrazilianRequestDto",
      new Blob([JSON.stringify(payload)], { type: "application/json" })
    );

    try {
      await axios.put(`${ip}/employee/brazilian/${id}`, formData, {
        headers: {
          Authorization: `Bearer ${tokenFromStorage}`,
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

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Blocks
          height="80"
          width="80"
          color="#34495E"
          ariaLabel="blocks-loading"
          wrapperStyle={{}}
          wrapperClass="blocks-wrapper"
          visible={true}
        />
      </div>
    );
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
        const formattedDate = new Date(creationDate).toLocaleDateString("pt-BR", {
          day: "2-digit",
          month: "2-digit",
          year: "2-digit",
        });
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
        <span className={`text-sm font-medium ${status === "ativo" ? "text-green-600" : "text-red-600"}`}>
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
            className="text-realizaBlue hover:underline"
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
          <div className="flex flex-col items-start">
            <div className="flex gap-2">
              <div className="bg-realizaBlue mb-4 flex h-20 w-20 items-center justify-center rounded-full">
                <User className="text-white" />
              </div>
              <div className="flex flex-col gap-1">
                <h3 className="text-lg font-medium">
                  {employee.name} {employee.surname}
                </h3>
                <div className="flex items-center gap-2">
                  <label htmlFor="situation" className="text-[14px] text-gray-500">
                    Situação:
                  </label>
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
        </div>

        <div className="flex flex-row items-start space-x-4">
          <div className="flex-[2]">
            <Table<Document> data={documents} columns={columns} />
          </div>
          <div className="flex flex-1 flex-col">
            <h2 className="text-[20px] font-medium">Histórico</h2>
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
                    <td className="p-2 text-stone-600">NR33 - Atividade teste</td>
                    <td className="p-2 text-stone-600">Upload</td>
                    <td className="p-2 text-stone-600">Monica</td>
                    <td className="p-2 text-stone-600">10/04/2025</td>
                  </tr>
                  <tr>
                    <td className="p-2 text-stone-600">NR34 - Atividade teste</td>
                    <td className="p-2 text-stone-600">Upload</td>
                    <td className="p-2 text-stone-600">Monica</td>
                    <td className="p-2 text-stone-600">05/04/2025</td>
                  </tr>
                  <tr>
                    <td className="p-2 text-stone-600">MDF³ - Atividade teste</td>
                    <td className="p-2 text-stone-600">Modificação</td>
                    <td className="p-2 text-stone-600">Monica</td>
                    <td className="p-2 text-stone-600">05/04/2025</td>
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
