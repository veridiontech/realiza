import { useState, useEffect } from "react";
import axios from "axios";
import { Table } from "@/components/ui/tableVanila";
import { AddDocument } from "./modals/addDocument";
import { useParams } from "react-router-dom";
import { Eye, Upload, User, MoreVertical, FileX2 } from "lucide-react";
import { ip } from "@/utils/ip";
import { DocumentViewer } from "./modals/viewDoc";
import { Blocks } from "react-loader-spinner";
import { toast } from "sonner";
import { useUser } from "@/context/user-provider";

interface Document {
  idDocument: string;
  title: string;
  assignDate?: string;
  status: string;
  expirationDate?: string;
  creationDate: string;
}

interface Contract {
  idContract: string;
  contractReference: string;
  description?: string;
  dateStart?: string;
  serviceName?: string;
}

export function DetailsEmployee() {
  const { id } = useParams<{ id: string }>();
  const { user } = useUser();
  const isSupplier =
    user?.role === "ROLE_SUPPLIER_MANAGER" ||
    user?.role === "ROLE_SUPPLIER_RESPONSIBLE";

  const [employee, setEmployee] = useState<any | null>(null);
  const [documents, setDocuments] = useState<Document[]>([]);
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isViewerOpen, setIsViewerOpen] = useState(false);
  const [selectedDocumentId, setSelectedDocumentId] = useState<string | null>(null);
  const [selectedDocumentTitle, setSelectedDocumentTitle] = useState<string | null>(null);

  const formatSituation = (situation: string) => {
    const map: Record<string, string> = {
      ALOCADO: "Alocado",
      DESALOCADO: "Desalocado",
      DEMITIDO: "Demitido",
      AFASTADO: "Afastado",
      LICENCA_MATERNIDADE: "Licença Maternidade",
      LICENCA_MEDICA: "Licença Médica",
      LICENCA_MILITAR: "Licença Militar",
      FERIAS: "Férias",
      ALISTAMENTO_MILITAR: "Alistamento Militar",
      APOSENTADORIA_POR_INVALIDEZ: "Aposentadoria por Invalidez",
    };
    return map[situation] || situation;
  };

  const formatDateBR = (value?: string) => {
    if (!value) return "-";
    const d = new Date(value);
    if (isNaN(d.getTime())) return "-";
    return d.toLocaleDateString("pt-BR", { day: "2-digit", month: "2-digit", year: "2-digit" });
    // se preferir ano com 4 dígitos, troque "2-digit" por "numeric" em year
  };

  const handleStatusChange = (idDoc: string, newStatus: string) => {
    setDocuments((prev) =>
      prev.map((d) => (d.idDocument === idDoc ? { ...d, status: newStatus } : d))
    );
  };

  const exemptDocument = async (documentId: string, documentTitle: string) => {
    try {
      const token = localStorage.getItem("tokenClient");
      const contractId =
        employee?.contracts?.[0]?.idContract || contracts?.[0]?.idContract;

      if (!contractId) {
        toast.error("Contrato não encontrado para isentar o documento.");
        return;
      }

      await axios.post(
        `${ip}/document/${documentId}/exempt`,
        {},
        {
          params: { contractId },
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      toast.success(`Documento "${documentTitle}" isento com sucesso!`);
      setDocuments((prev) =>
        prev.map((d) => (d.idDocument === documentId ? { ...d, status: "ISENTO" } : d))
      );
    } catch (err: any) {
      console.error("Erro ao isentar o documento:", err?.response?.data || err?.message);
      toast.error("Erro ao isentar o documento.");
    }
  };

  // =================== FETCHERS ===================

  const fetchEmployee = async () => {
    const token = localStorage.getItem("tokenClient");
    try {
      const res = await axios.get(`${ip}/employee/brazilian/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
        params: isSupplier ? { enterprise: "SUPPLIER" } : undefined,
      });
      setEmployee(res.data);
      setError(null);
    } catch (err: any) {
      try {
        const res2 = await axios.get(`${ip}/employee/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
          params: isSupplier ? { enterprise: "SUPPLIER" } : undefined,
        });
        setEmployee(res2.data);
        setError(null);
      } catch (err2: any) {
        console.error("Erro ao carregar o Colaborador:", err2?.response?.data || err2?.message);
        const backendMsg =
          err2?.response?.data?.message ||
          err?.response?.data?.message ||
          "Erro ao carregar o Colaborador.";
        setError(backendMsg);
      }
    }
  };

  const fetchDocuments = async () => {
    const token = localStorage.getItem("tokenClient");
    try {
      const res = await axios.get(`${ip}/document/employee/filtered-employee`, {
        params: {
          idSearch: id,
          page: 0,
          size: 10,
          sort: "creationDate",
          direction: "DESC",
          ...(isSupplier ? { enterprise: "SUPPLIER" } : {}),
        },
        headers: { Authorization: `Bearer ${token}` },
      });
      setDocuments(res.data?.content || []);
    } catch (err: any) {
      console.error("Erro ao carregar os documentos:", err?.response?.data || err?.message);
      setError(err?.response?.data?.message || "Erro ao carregar os documentos.");
    }
  };

  const fetchContracts = async () => {
    const token = localStorage.getItem("tokenClient");
    try {
      const res = await axios.get(`${ip}/contract/find-by-employee/${id}`, {
        params: {
          page: 0,
          size: 20,
          sort: "contractReference",
          direction: "ASC",
        },
        headers: { Authorization: `Bearer ${token}` },
      });
      const items = Array.isArray(res.data?.content) ? res.data.content : res.data || [];
      setContracts(items);
    } catch (err: any) {
      console.error("Erro ao carregar os contratos:", err?.response?.data || err?.message);
      setContracts([]);
    }
  };

  useEffect(() => {
    setIsLoading(true);
    Promise.all([fetchEmployee(), fetchDocuments(), fetchContracts()]).finally(() =>
      setIsLoading(false)
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  // =================== TABELA DE DOCUMENTOS (já existente) ===================

  const columns: {
    key: keyof Document;
    label: string;
    className?: string;
    render?: (value: string | undefined, row: Document) => React.ReactNode;
  }[] = [
    { key: "title", label: "Documento" },
    {
      key: "creationDate",
      label: "Data de Envio",
      render: (value) => formatDateBR(value),
    },
    {
      key: "assignDate",
      label: "Data de Atribuição",
      render: (value) => formatDateBR(value),
    },
    {
      key: "expirationDate",
      label: "Data de Validade",
      render: (value) => formatDateBR(value),
    },
    {
      key: "status",
      label: "Status",
      render: (value) => {
        let cls = "";
        if (value === "PENDENTE") cls = "text-yellow-500";
        else if (value === "EM_ANALISE") cls = "text-blue-600";
        else if (value === "APROVADO" || value === "APROVADO_IA") cls = "text-green-600";
        else if (value === "REPROVADO" || value === "REPROVADO_IA") cls = "text-red-600";
        return <span className={`text-sm font-medium ${cls}`}>{value}</span>;
      },
    },
    {
      key: "idDocument",
      label: "Ações",
      render: (_value, row) => (
        <div className="relative inline-block text-left">
          <button
            className="text-realizaBlue hover:underline"
            onClick={() =>
              setSelectedDocumentId(selectedDocumentId === row.idDocument ? null : row.idDocument)
            }
          >
            <MoreVertical size={16} />
          </button>

          {selectedDocumentId === row.idDocument && (
            <div className="absolute z-10 w-32 top-0 right-10 origin-top-right rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5">
              <div className="py-1 text-sm text-gray-700">
                <button
                  onClick={() => {
                    setIsViewerOpen(true);
                    setSelectedDocumentId(row.idDocument);
                  }}
                  className="block w-full px-4 py-2 text-left hover:bg-gray-100"
                >
                  <Eye size={14} className="inline mr-2" />
                  Visualizar
                </button>
                <button
                  onClick={() => {
                    setSelectedDocumentId(row.idDocument);
                    setSelectedDocumentTitle(row.title);
                    setTimeout(() => setIsModalOpen(true), 0);
                  }}
                  className="block w-full px-4 py-2 text-left hover:bg-gray-100"
                >
                  <Upload size={14} className="inline mr-2" />
                  Enviar
                </button>
                <button
                  onClick={() => exemptDocument(row.idDocument, row.title)}
                  className="block w-full px-4 py-2 text-left text-red-600 hover:bg-gray-100"
                >
                  <FileX2 size={14} className="inline mr-2" />
                  Isentar
                </button>
              </div>
            </div>
          )}
        </div>
      ),
    },
  ];

  // =================== RENDER ===================

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
              <h3 className="text-lg font-medium">
                {employee.name} {employee.surname}
              </h3>
              <div className="flex items-center gap-2">
                <p className="text-sm text-gray-500">Situação:</p>
                <span className="rounded bg-gray-200 px-2 py-1 text-sm font-medium text-gray-800">
                  {formatSituation(employee.situation)}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div className="flex flex-row space-x-4">
          <div className="flex-[2]">
            <Table<Document> data={documents} columns={columns} />
          </div>

          {/* ======= Lado direito: TABELA de Contratos ======= */}
          <div className="flex flex-1 flex-col">
            <h2 className="text-lg font-medium">Contratos</h2>
            <div className="h-[40vh] overflow-auto rounded-md bg-white p-4 shadow-md">
              {contracts.length === 0 ? (
                <p className="text-sm text-stone-600">Nenhum contrato encontrado.</p>
              ) : (
                <table className="w-full border-collapse">
                  <thead>
                    <tr className="border-b">
                      <th className="p-2 text-left text-sm text-stone-600">Referência</th>
                      <th className="p-2 text-left text-sm text-stone-600">Serviço</th>
                      <th className="p-2 text-left text-sm text-stone-600">Início</th>
                      <th className="p-2 text-left text-sm text-stone-600">Descrição</th>
                    </tr>
                  </thead>
                  <tbody>
                    {contracts.map((c) => (
                      <tr key={c.idContract} className="border-b hover:bg-stone-50">
                        <td className="p-2 text-sm font-semibold">
                          {c.contractReference || "-"}
                        </td>
                        <td className="p-2 text-sm">
                          {c.serviceName || "-"}
                        </td>
                        <td className="p-2 text-sm">
                          {formatDateBR(c.dateStart)}
                        </td>
                        <td className="p-2 text-sm text-stone-700">
                          {c.description || "-"}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </div>
          {/* ================================================ */}
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
          onStatusChange={handleStatusChange}
        />
      )}

      {isViewerOpen && selectedDocumentId && (
        <DocumentViewer
          documentId={selectedDocumentId}
          isOpen={isViewerOpen}
          onClose={() => setIsViewerOpen(false)}
          onStatusChange={handleStatusChange}
        />
      )}
    </div>
  );
}
