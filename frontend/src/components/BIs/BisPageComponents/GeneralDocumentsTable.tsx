import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";
import { ChevronLeft, ChevronRight } from "lucide-react";

interface Filters {
  branchIds: string[];
  providerIds: string[];
  documentTypes: string[];
  responsibleIds: string[];
  activeContract: string[];
  statuses: string[];
  documentTitles: string[];
  providerCnpjs: string[];
  contractIds: string[];
  employeeIds: string[];
  employeeCpfs: string[];
  employeeSituations: string[];
  documentDoesBlock: boolean[];
  documentValidity: string[];
}
interface Documento {
  documentTitle: string;
  documentGroupName: string;
  status: string;
  versionDate: string;
  expirationDate: string;
  doesBlock: boolean;
  supplierName: string;
  branchName: string;
}
interface GeneralDocumentsTableProps {
  clientId: string;
  filters: Filters;
}

export function GeneralDocumentsTable({
  clientId,
  filters,
}: GeneralDocumentsTableProps) {
  const USE_MOCK_DATA = true;
  const mockDocumentosData: Documento[] = [
    {
      documentTitle: "NR-35 - Trabalho em Altura",
      documentGroupName: "Segurança do Trabalho",
      status: "APROVADO",
      versionDate: "2025-08-10T10:00:00Z",
      expirationDate: "2026-08-15T00:00:00Z",
      doesBlock: true,
      supplierName: "Fornecedor Exemplo A",
      branchName: "Filial São Paulo",
    },
    {
      documentTitle: "ASO - Atestado de Saúde Ocupacional",
      documentGroupName: "Saúde Ocupacional",
      status: "PENDENTE",
      versionDate: "2025-09-01T14:30:00Z",
      expirationDate: "2025-12-20T00:00:00Z",
      doesBlock: true,
      supplierName: "Fornecedor Exemplo B",
      branchName: "Filial Rio de Janeiro",
    },
    {
      documentTitle: "Certidão Negativa de Débitos",
      documentGroupName: "Cadastro e Certidões",
      status: "REPROVADO",
      versionDate: "2025-09-29T09:00:00Z",
      expirationDate: "2025-10-05T00:00:00Z",
      doesBlock: false,
      supplierName: "Fornecedor Exemplo A",
      branchName: "Filial São Paulo",
    },
    {
      documentTitle: "NR-10 - Segurança em Instalações Elétricas",
      documentGroupName: "Segurança do Trabalho",
      status: "APROVADO",
      versionDate: "2025-07-20T11:00:00Z",
      expirationDate: "2027-01-30T00:00:00Z",
      doesBlock: true,
      supplierName: "Fornecedor Exemplo C",
      branchName: "Filial Curitiba",
    },
  ];

  const [documentos, setDocumentos] = useState<Documento[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    if (USE_MOCK_DATA) {
      setIsLoading(true);
      setTimeout(() => {
        console.log(
          "--- USANDO DADOS MOCKADOS PARA A TABELA DE DOCUMENTOS GERAIS ---"
        );
        setDocumentos(mockDocumentosData);
        setTotalPages(1);
        setIsLoading(false);
      }, 500);
      return;
    }

    const fetchDocuments = async () => {
      setIsLoading(true);
      setError(null);
      const token = localStorage.getItem("tokenClient");

      try {
        const url = `${ip}/dashboard/${clientId}/document/details?page=${page}&size=10`;
        const requestBody = { ...filters };

        const { data } = await axios.post(url, requestBody, {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });

        setDocumentos(data.content || []);
        setTotalPages(data.totalPages || 0);
      } catch (err) {
        console.error("Erro ao buscar detalhes dos documentos:", err);
        setError(
          "Não foi possível carregar os documentos. Tente novamente mais tarde."
        );
      } finally {
        setIsLoading(false);
      }
    };

    if (clientId) {
      fetchDocuments();
    }
  }, [clientId, filters, page, USE_MOCK_DATA]);

  const handlePreviousPage = () => {
    setPage((prevPage) => Math.max(prevPage - 1, 0));
  };

  const handleNextPage = () => {
    setPage((prevPage) => Math.min(prevPage + 1, totalPages - 1));
  };

  return (
    <div className="mt-6 rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-800">
          Documentos Gerais
        </h2>
        <button className="rounded-md bg-gray-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-gray-700">
          Baixar PDF
        </button>
      </div>

      {isLoading ? (
        <div className="text-center py-10">Carregando documentos...</div>
      ) : error ? (
        <div className="text-center py-10 text-red-500">{error}</div>
      ) : documentos.length === 0 ? (
        <div className="text-center py-10 text-gray-500">
          Nenhum documento encontrado para os filtros selecionados.
        </div>
      ) : (
        <>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Documento
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Grupo
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Data de Envio
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Validade
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Bloqueia?
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Fornecedor
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {documentos.map((doc, index) => (
                  <tr key={index}>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-semibold text-gray-900">
                      {doc.documentTitle}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {doc.documentGroupName}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {doc.status}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {new Date(doc.versionDate).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {new Date(doc.expirationDate).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {doc.doesBlock ? "Sim" : "Não"}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {doc.supplierName}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div className="flex items-center justify-end mt-4">
            <span className="text-sm text-gray-700">
              Página {page + 1} de {totalPages}
            </span>
            <div className="ml-4">
              <button
                onClick={handlePreviousPage}
                disabled={page === 0}
                className="p-2 border rounded-md disabled:opacity-50"
              >
                <ChevronLeft size={16} />
              </button>
              <button
                onClick={handleNextPage}
                disabled={page >= totalPages - 1}
                className="p-2 border rounded-md ml-2 disabled:opacity-50"
              >
                <ChevronRight size={16} />
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}