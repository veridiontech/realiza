import { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

const token = localStorage.getItem("tokenClient");

type Filters = {
  branchIds?: string[];
  providerIds?: string[];
  documentTypes?: string[];
  responsibleIds?: string[];
  activeContract?: string[];
  statuses?: string[];
  documentTitles?: string[];
};
type ApiItem = Record<string, any>;

type Row = {
  id: string;
  filial: string;
  cnpjFilial: string;
  fornecedor: string;
  cnpjFornecedor: string;
  documento: string;
  tipoDocumento: string;
  status: string;
  responsavel?: string;
  emailResponsavel?: string;
  conformidade?: string;
  criadoEm?: string;
  ultimaChecagem?: string;
  validade?: string;
};

function formatDate(v?: string) {
  if (!v) return "—";
  const d = new Date(v);
  if (isNaN(+d)) return v;
  return d.toLocaleDateString('pt-BR');
}

function mapItemToRow(it: ApiItem, idx: number): Row {
  return {
    id: String(it.id ?? idx),
    filial: it.branchName ?? "—",
    cnpjFilial: it.branchCnpj ?? "—",
    fornecedor: it.supplierName ?? "—",
    cnpjFornecedor: it.supplierCnpj ?? "—",
    documento: it.documentTitle ?? "—",
    tipoDocumento: it.documentType ?? it.contractType ?? "—",
    status: it.status ?? it.documentStatus ?? it.contractStatus ?? "—",
    responsavel: it.responsibleFullName ?? it.employeeFullName ?? "—",
    emailResponsavel: it.responsibleEmail ?? it.employeeEmail ?? "—",
    conformidade:
      typeof it.conforming === "boolean"
        ? it.conforming
          ? "Conforme"
          : "Não conforme"
        : "—",
    criadoEm: formatDate(it.createdAt ?? it.contractAt ?? it.serviceStartAt),
    ultimaChecagem: formatDate(it.lastCheck ?? it.checkedAt),
    validade: formatDate(it.expirationDate ?? it.validUntil),
  };
}

async function fetchDocumentDetails(clientId: string, filters: Filters) {
  const body = {
    branchIds: filters.branchIds ?? [],
    providerIds: filters.providerIds ?? [],
    documentTypes: filters.documentTypes ?? [],
    responsibleIds: filters.responsibleIds ?? [],
    activeContract: filters.activeContract,
    statuses: filters.statuses ?? [],
    documentTitles: filters.documentTitles ?? [],
  };

  const { data } = await axios.post(
    `${ip}/dashboard/${clientId}/document/details`,
    body,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
  const content = Array.isArray(data?.content)
    ? data.content
    : Array.isArray(data)
      ? data
      : [];
  return content as ApiItem[];
}

const generatePDF = (data: Row[]) => {
  const doc = new jsPDF({ orientation: 'landscape' });
  
  doc.setFontSize(16);
  doc.text("Relatório de Documentos de Fornecedores", 14, 22);

  const tableHeaders = [
    "Fornecedor", "CNPJ", "Filial", "Documento", "Status", 
    "Conformidade", "Validade", "Responsável", "Última Checagem"
  ];

  const tableBody = data.map(row => [
    row.fornecedor ?? '—',
    row.cnpjFornecedor ?? '—',
    row.filial ?? '—',
    row.documento ?? '—',
    row.status ?? '—',
    row.conformidade ?? '—',
    row.validade ?? '—',
    row.responsavel ?? '—',
    row.ultimaChecagem ?? '—'
  ]);

  autoTable(doc, {
    head: [tableHeaders],
    body: tableBody,
    startY: 30,
    theme: 'grid',
    headStyles: {
      fillColor: [22, 160, 133],
      textColor: 255,
      fontStyle: 'bold'
    },
    styles: {
      fontSize: 8,
    },
    columnStyles: {
      0: { cellWidth: 'auto' },
      1: { cellWidth: 35 },
      2: { cellWidth: 'auto' },
      3: { cellWidth: 'auto' },
    }
  });

  doc.save('relatorio_documentos.pdf');
};

type Props = {
  clientId: string;
  filters?: Filters;
};

const getStatusBadgeClasses = (status: string): string => {
  const s = status.toLowerCase();
  if (s.includes("pend")) {
    return "bg-orange-100 text-orange-800 border-orange-200";
  }
  if (s.includes("aprov")) {
    return "bg-green-100 text-green-800 border-green-200";
  }
  if (s.includes("reprov")) {
    return "bg-red-100 text-red-800 border-red-200";
  }
  return "bg-gray-100 text-gray-800 border-gray-200";
};

const SortIcon = ({ direction }: { direction: "asc" | "desc" | null }) => {
  if (!direction) return null;
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      className="ml-1 inline-block h-4 w-4"
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
    >
      {direction === "asc" ? (
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d="M5 15l7-7 7 7"
        />
      ) : (
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d="M19 9l-7 7-7-7"
        />
      )}
    </svg>
  );
};

export default function FornecedoresTable({ clientId, filters = {} }: Props) {
  const USE_MOCK_DATA = true;
  
  const mockApiData: ApiItem[] = [
    {
      id: 1,
      branchName: 'Matriz - São Paulo',
      branchCnpj: '01.234.567/0001-88',
      supplierName: 'Tech Solutions Ltda',
      supplierCnpj: '12.345.678/0001-99',
      documentTitle: 'Contrato Social',
      documentType: 'Cadastro',
      status: 'APROVADO',
      responsibleFullName: 'Juliana Martins',
      conforming: true,
      lastCheck: '2025-09-15T11:00:00Z',
      expirationDate: '2030-01-01T00:00:00Z',
    },
    {
      id: 2,
      branchName: 'Filial - Rio de Janeiro',
      branchCnpj: '01.234.567/0002-69',
      supplierName: 'Inovação e Cia',
      supplierCnpj: '98.765.432/0001-11',
      documentTitle: 'Licença de Operação',
      documentType: 'Certidão',
      status: 'PENDENTE',
      responsibleFullName: 'Ricardo Alves',
      conforming: false,
      lastCheck: '2025-09-20T15:00:00Z',
      expirationDate: '2025-11-25T00:00:00Z',
    },
    {
      id: 3,
      branchName: 'Matriz - São Paulo',
      branchCnpj: '01.234.567/0001-88',
      supplierName: 'Serviços Gerais Express',
      supplierCnpj: '55.555.555/0001-55',
      documentTitle: 'CIPA - Relatório Anual',
      documentType: 'Segurança do Trabalho',
      status: 'REPROVADO',
      responsibleFullName: 'Fernanda Lima',
      conforming: false,
      lastCheck: '2025-09-28T09:30:00Z',
      expirationDate: '2025-10-10T00:00:00Z',
    },
  ];

  const [rows, setRows] = useState<Row[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [sortKey, setSortKey] = useState<keyof Row>("fornecedor");
  const [sortDir, setSortDir] = useState<"asc" | "desc">("asc");

  useEffect(() => {
    let alive = true;
    const loadData = async () => {
      setLoading(true);
      setError(null);

      if (USE_MOCK_DATA) {
        setTimeout(() => {
          if (alive) {
            setRows(mockApiData.map(mapItemToRow));
            setLoading(false);
          }
        }, 500);
      } else {
        try {
          const items = await fetchDocumentDetails(clientId, filters);
          if (alive) {
            setRows(items.map(mapItemToRow));
          }
        } catch (e: any) {
          if (alive) {
            setError(e?.message ?? "Erro ao carregar dados");
          }
        } finally {
          if (alive) {
            setLoading(false);
          }
        }
      }
    };

    loadData();
    return () => {
      alive = false;
    };
  }, [clientId, JSON.stringify(filters), USE_MOCK_DATA]);

  const sorted = useMemo(() => {
    const copy = [...rows];
    copy.sort((a, b) => {
      const va = (a[sortKey] ?? "").toString().toLowerCase();
      const vb = (b[sortKey] ?? "").toString().toLowerCase();
      if (va < vb) return sortDir === "asc" ? -1 : 1;
      if (va > vb) return sortDir === "asc" ? 1 : -1;
      return 0;
    });
    return copy;
  }, [rows, sortKey, sortDir]);

  function setSort(k: keyof Row) {
    if (k === sortKey) setSortDir((d) => (d === "asc" ? "desc" : "asc"));
    else {
      setSortKey(k);
      setSortDir("asc");
    }
  }

  return (
    <div className="w-full overflow-x-auto rounded-lg border border-gray-200 bg-white shadow-sm">
      <div className="flex justify-between items-center p-6 border-b border-gray-200">
        <h2 className="text-lg font-semibold text-gray-800">
          Documentos de Fornecedores
        </h2>
        <button
            onClick={() => generatePDF(sorted)}
            className="bg-blue-600 text-white px-4 py-2 text-sm font-medium rounded-md shadow-sm hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
            disabled={loading || sorted.length === 0}
            title={sorted.length === 0 ? "Nenhum dado para exportar" : "Exportar para PDF"}
        >
          Baixar PDF
        </button>
      </div>

      {loading && (
        <div className="p-12 text-center text-gray-500">
          Carregando dados...
        </div>
      )}
      {error && <div className="p-12 text-center text-red-600">{error}</div>}

      {!loading && !error && (
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              {[
                ["fornecedor", "Fornecedor"],
                ["cnpjFornecedor", "CNPJ"],
                ["filial", "Filial"],
                ["documento", "Documento"],
                ["status", "Status"],
                ["conformidade", "Conformidade"],
                ["validade", "Validade"],
                ["responsavel", "Responsável"],
                ["ultimaChecagem", "Última checagem"],
              ].map(([key, label]) => (
                <th
                  key={key}
                  onClick={() => setSort(key as keyof Row)}
                  className="cursor-pointer whitespace-nowrap px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500 hover:bg-gray-100"
                  title="Clique para ordenar"
                >
                  <div className="flex items-center">
                    <span>{label}</span>
                    <SortIcon direction={sortKey === key ? sortDir : null} />
                  </div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200 bg-white">
            {sorted.length === 0 ? (
              <tr>
                <td
                  colSpan={9}
                  className="px-6 py-12 text-center text-gray-500"
                >
                  Nenhum documento encontrado para os filtros selecionados.
                </td>
              </tr>
            ) : (
              sorted.map((r) => (
                <tr
                  key={r.id}
                  className="transition-colors duration-200 hover:bg-gray-50"
                >
                  <td className="whitespace-nowrap px-6 py-4 text-sm font-medium text-gray-900">
                    {r.fornecedor}
                  </td>
                  <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                    {r.cnpjFornecedor}
                  </td>
                  <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                    {r.filial}
                  </td>
                  <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                    {r.documento}
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <span
                      className={`inline-block rounded-full border px-3 py-1 text-xs font-semibold ${getStatusBadgeClasses(
                        r.status
                      )}`}
                    >
                      {r.status}
                    </span>
                  </td>
                  <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                    {r.conformidade}
                  </td>
                  <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                    {r.validade}
                  </td>
                  <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                    {r.responsavel}
                  </td>
                  <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                    {r.ultimaChecagem}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      )}
    </div>
  );
}