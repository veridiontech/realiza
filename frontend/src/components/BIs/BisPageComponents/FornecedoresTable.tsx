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

type ApiItem = {
  corporateName: string;
  cnpj: string;
  totalDocumentQuantity: number;
  adherenceQuantity: number;
  nonAdherenceQuantity: number;
  conformityQuantity: number;
  nonConformityQuantity: number;
  adherence: number;
  conformity: number;
  conformityRange: string;
  employeeQuantity: number;
};


type Row = {
  id: string;
  fornecedor: string;
  cnpj: string;
  totalDocumentos: number;
  aderencia: string;
  conformidade: string;
  risco: string;
  totalFuncionarios: number;
};

function translateRisk(risk: string): string {
  if (!risk) return "Não definido";
  const lowerCaseRisk = risk.toLowerCase();
  switch (lowerCaseRisk) {
    case "risky":
      return "Crítico";
    case "high":
      return "Alto";
    case "medium":
      return "Médio";
    case "low":
      return "Baixo";
    default:
      return risk; 
  }
}

function mapItemToRow(it: ApiItem): Row {
  return {
    id: it.cnpj,
    fornecedor: it.corporateName ?? "—",
    cnpj: it.cnpj ?? "—",
    totalDocumentos: it.totalDocumentQuantity ?? 0,
    aderencia: `${(it.adherence ?? 0).toFixed(1)}%`,
    conformidade: `${(it.conformity ?? 0).toFixed(1)}%`,
    risco: translateRisk(it.conformityRange),
    totalFuncionarios: it.employeeQuantity ?? 0,
  };
}

async function fetchProviders(clientId: string, filters: Filters) {
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
    `${ip}/dashboard/${clientId}/provider`,
    body,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  );
  const content = Array.isArray(data?.content)
    ? data.content
    : Array.isArray(data)
      ? data
      : [];
  return content as ApiItem[];
}


const generatePDF = (data: Row[]) => {
  const doc = new jsPDF({ orientation: "landscape" });

  doc.setFontSize(16);
  doc.text("Relatório - Dashboard de Fornecedores", 14, 22);

  const tableHeaders = [
    "Fornecedor",
    "CNPJ",
    "Total Docs",
    "Aderência",
    "Conformidade",
    "Risco",
    "Funcionários",
  ];

  const tableBody = data.map((row) => [
    row.fornecedor,
    row.cnpj,
    row.totalDocumentos,
    row.aderencia,
    row.conformidade,
    row.risco,
    row.totalFuncionarios,
  ]);

  autoTable(doc, {
    head: [tableHeaders],
    body: tableBody,
    startY: 30,
    theme: "grid",
    headStyles: {
      fillColor: [22, 160, 133],
      textColor: 255,
      fontStyle: "bold",
    },
    styles: {
      fontSize: 8,
    },
    columnStyles: {
      0: { cellWidth: "auto" },
      1: { cellWidth: 35 },
    },
  });

  doc.save("relatorio_fornecedores.pdf");
};

type Props = {
  clientId: string;
  filters?: Filters;
};

const getRiskBadgeClasses = (risk: string): string => {
  const r = risk.toLowerCase();
  if (r === "crítico") {
    return "bg-red-100 text-red-800 border-red-200";
  }
  if (r === "alto") {
    return "bg-orange-100 text-orange-800 border-orange-200";
  }
  if (r === "médio") {
    return "bg-yellow-100 text-yellow-800 border-yellow-200";
  }
  if (r === "baixo") {
    return "bg-green-100 text-green-800 border-green-200";
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


export default function DashboardFornecedoresTable({
  clientId,
  filters = {},
}: Props) {
  const USE_MOCK_DATA = true;
  const mockApiData: ApiItem[] = [
    {
      corporateName: "Tech Solutions Ltda",
      cnpj: "12.345.678/0001-99",
      totalDocumentQuantity: 50,
      adherenceQuantity: 45,
      nonAdherenceQuantity: 5,
      conformityQuantity: 48,
      nonConformityQuantity: 2,
      adherence: 90.0,
      conformity: 96.0,
      conformityRange: "LOW", 
      employeeQuantity: 120,
    },
    {
      corporateName: "Inovação e Cia",
      cnpj: "98.765.432/0001-11",
      totalDocumentQuantity: 30,
      adherenceQuantity: 20,
      nonAdherenceQuantity: 10,
      conformityQuantity: 15,
      nonConformityQuantity: 15,
      adherence: 66.7,
      conformity: 50.0,
      conformityRange: "RISKY", 
      employeeQuantity: 45,
    },
    {
      corporateName: "Serviços Gerais Express",
      cnpj: "55.555.555/0001-55",
      totalDocumentQuantity: 80,
      adherenceQuantity: 75,
      nonAdherenceQuantity: 5,
      conformityQuantity: 68,
      nonConformityQuantity: 12,
      adherence: 93.8,
      conformity: 85.0,
      conformityRange: "Medium", 
      employeeQuantity: 250,
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
          const items = await fetchProviders(clientId, filters);
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
      const va = a[sortKey];
      const vb = b[sortKey];
      if (typeof va === "number" && typeof vb === "number") {
        return sortDir === "asc" ? va - vb : vb - va;
      }
      const sa = (va ?? "").toString().toLowerCase();
      const sb = (vb ?? "").toString().toLowerCase();
      if (sa < sb) return sortDir === "asc" ? -1 : 1;
      if (sa > sb) return sortDir === "asc" ? 1 : -1;
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
          Dashboard de Fornecedores
        </h2>
        <button
          onClick={() => generatePDF(sorted)}
          className="bg-blue-600 text-white px-4 py-2 text-sm font-medium rounded-md shadow-sm hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
          disabled={loading || sorted.length === 0}
          title={
            sorted.length === 0
              ? "Nenhum dado para exportar"
              : "Exportar para PDF"
          }
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
                ["cnpj", "CNPJ"],
                ["totalDocumentos", "Total Docs"],
                ["aderencia", "Aderência"],
                ["conformidade", "Conformidade"],
                ["risco", "Risco"],
                ["totalFuncionarios", "Funcionários"],
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
                  colSpan={7}
                  className="px-6 py-12 text-center text-gray-500"
                >
                  Nenhum fornecedor encontrado para os filtros selecionados.
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
                    {r.cnpj}
                  </td>
                  <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                    {r.totalDocumentos}
                  </td>
                  <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                    {r.aderencia}
                  </td>
                  <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                    {r.conformidade}
                  </td>
                  <td className="px-6 py-4 text-sm">
                    <span
                      className={`inline-block rounded-full border px-3 py-1 text-xs font-semibold ${getRiskBadgeClasses(
                        r.risco,
                      )}`}
                    >
                      {r.risco}
                    </span>
                  </td>
                  <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                    {r.totalFuncionarios}
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