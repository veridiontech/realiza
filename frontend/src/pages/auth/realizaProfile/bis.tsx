import { Helmet } from "react-helmet-async";
import {
  StatusDocumentChart,
  ChartData,
} from "@/components/BIs/BisPageComponents/statusDocumentChart";
import { ExemptionPendingChart } from "@/components/BIs/BisPageComponents/exemptionRankingChart";
import { ConformityGaugeChart } from "@/components/BIs/BisPageComponents/conformityChart";
import FornecedoresTable from "@/components/BIs/BisPageComponents/FornecedoresTable";
import { ConformityRankingTable } from "@/components/BIs/BisPageComponents/conformityRankingTable";
import { AllocatedEmployees } from "@/components/BIs/BisPageComponents/AllocatedEmployees";
import { Employees } from "@/components/BIs/BisPageComponents/employees";
import { ActiveContracts } from "@/components/BIs/BisPageComponents/activeContracts";
import { Suppliers } from "@/components/BIs/BisPageComponents/suppliersCard";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";
import { useEffect, useMemo, useRef, useState } from "react";


import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

type Option = { value: string; label: string };

type FiltersState = {
  branchIds: string[];
  providerIds: string[];
  documentTypes: string[];
  responsibleIds: string[];
  activeContract: string[];
  statuses: string[];
  documentTitles: string[];
};

type RawDocumentStatus = {
  name: string;
  status: Array<{ status: string; quantity: number }>;
};
type RawExemption = { name: string; quantity: number };
type RawRanking = {
  corporateName: string;
  cnpj: string;
  adherence: number;
  conformity: number;
  nonConformingDocumentQuantity: number;
  conformityLevel: string;
};

function useClickOutside<T extends HTMLElement>(onOutside: () => void) {
  const ref = useRef<T | null>(null);
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (!ref.current) return;
      if (!ref.current.contains(e.target as Node)) onOutside();
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, [onOutside]);
  return ref;
}

function MultiSelectDropdown(props: {
  label: string;
  options: Option[];
  values: string[];
  onChange: (values: string[]) => void;
  placeholder?: string;
  className?: string;
}) {
  const {
    label,
    options,
    values,
    onChange,
    placeholder = "Selecionar",
    className,
  } = props;
  const [open, setOpen] = useState(false);
  const ref = useClickOutside<HTMLDivElement>(() => setOpen(false));

  function toggle(v: string) {
    const set = new Set(values);
    set.has(v) ? set.delete(v) : set.add(v);
    onChange(Array.from(set));
  }

  const title = useMemo(() => {
    if (!values.length) return placeholder;
    if (values.length === 1) {
      const o = options.find((x) => x.value === values[0]);
      return o?.label ?? values[0];
    }
    return `${values.length} selecionados`;
  }, [values, options, placeholder]);

  return (
    <div ref={ref} className={`relative ${className ?? ""}`}>
      <button
        type="button"
        onClick={() => setOpen((o) => !o)}
        className="w-full border border-gray-300 rounded-md px-3 py-2 text-left flex items-center justify-between"
      >
        <span className="truncate">
          {label}: <span className="font-medium">{title}</span>
        </span>
        <svg viewBox="0 0 20 20" className="w-4 h-4">
          <path
            d="M5.5 7.5l4.5 4 4.5-4"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
          />
        </svg>
      </button>
      {open && (
        <div className="absolute z-20 mt-1 w-full bg-white shadow-lg border border-gray-200 rounded-md max-h-64 overflow-auto">
          <ul className="py-1">
            {options.length === 0 && (
              <li className="px-3 py-2 text-sm text-gray-500">Sem opções</li>
            )}
            {options.map((opt) => (
              <li key={opt.value}>
                <label className="flex items-center gap-2 px-3 py-2 cursor-pointer hover:bg-gray-50">
                  <input
                    type="checkbox"
                    className="h-4 w-4"
                    checked={values.includes(opt.value)}
                    onChange={() => toggle(opt.value)}
                  />
                  <span className="text-sm">{opt.label}</span>
                </label>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

function buildIdNameMap(arr: Array<{ id: string | number; name?: string }>) {
  const map = new Map<string, string>();
  arr.forEach((x) => map.set(String(x.id), x.name ?? String(x.id)));
  return map;
}

function normalize(s: unknown) {
  return String(s ?? "").toLowerCase();
}

function matchesAny(text: string, needles: string[]) {
  if (!needles.length) return true;
  const ntext = normalize(text);
  return needles.some((n) => ntext.includes(normalize(n)));
}

function filterDocumentStatus(
  raw: RawDocumentStatus[],
  filters: FiltersState,
  names: {
    branchNames: string[];
    providerNames: string[];
    responsibleNames: string[];
  }
) {
  const hasStatuses = filters.statuses.length > 0;
  const nameNeedles = [
    ...names.branchNames,
    ...names.providerNames,
    ...names.responsibleNames,
    ...filters.documentTypes,
    ...filters.documentTitles,
    ...filters.activeContract,
  ].filter(Boolean);

  let filtered = raw.filter((r) => matchesAny(r.name, nameNeedles));

  filtered = filtered
    .map((r) => ({
      ...r,
      status: hasStatuses
        ? r.status.filter((s) => filters.statuses.includes(String(s.status)))
        : r.status,
    }))
    .filter((r) => r.status.length > 0);

  const chartRows: ChartData[] = filtered.map((cat) => {
    const row: any = { name: cat.name };
    cat.status.forEach((s) => {
      row[s.status] = s.quantity;
    });
    return row;
  });
  return chartRows;
}

function filterExemption(
  raw: RawExemption[],
  filters: FiltersState,
  names: {
    branchNames: string[];
    providerNames: string[];
    responsibleNames: string[];
  }
) {
  const nameNeedles = [
    ...names.branchNames,
    ...names.providerNames,
    ...names.responsibleNames,
    ...filters.documentTypes,
    ...filters.documentTitles,
    ...filters.activeContract,
  ].filter(Boolean);
  return raw.filter((r) => matchesAny(r.name, nameNeedles));
}

function filterRanking(
  raw: RawRanking[],
  filters: FiltersState,
  names: {
    branchNames: string[];
    providerNames: string[];
    responsibleNames: string[];
  }
) {
  const providerNeedles = names.providerNames;
  const otherNeedles = [
    ...names.branchNames,
    ...names.responsibleNames,
    ...filters.documentTitles,
    ...filters.documentTypes,
  ].filter(Boolean);

  return raw.filter((r) => {
    const byProvider = providerNeedles.length
      ? matchesAny(r.corporateName, providerNeedles)
      : true;
    const byOthers = otherNeedles.length
      ? matchesAny(r.corporateName, otherNeedles)
      : true;
    return byProvider && byOthers;
  });
}

export const MonittoringBis = () => {
  const [activeTab, setActiveTab] = useState("visao-geral");
  const { client } = useClient();
  const clientId = client?.idClient;
  const token = localStorage.getItem("tokenClient");

  const [branchOpts, setBranchOpts] = useState<Option[]>([]);
  const [providerOpts, setProviderOpts] = useState<Option[]>([]);
  const [docTypeOpts, setDocTypeOpts] = useState<Option[]>([]);
  const [respOpts, setRespOpts] = useState<Option[]>([]);
  const [contractStatusOpts, setContractStatusOpts] = useState<Option[]>([]);
  const [statusOpts, setStatusOpts] = useState<Option[]>([]);
  const [docTitleOpts, setDocTitleOpts] = useState<Option[]>([]);

  const [branchIdName, setBranchIdName] = useState<Map<string, string>>(
    new Map()
  );
  const [providerIdName, setProviderIdName] = useState<Map<string, string>>(
    new Map()
  );
  const [respIdName, setRespIdName] = useState<Map<string, string>>(new Map());

  const [draft, setDraft] = useState<FiltersState>({
    branchIds: [],
    providerIds: [],
    documentTypes: [],
    responsibleIds: [],
    activeContract: [],
    statuses: [],
    documentTitles: [],
  });
  const [applied, setApplied] = useState<FiltersState>({ ...draft });

  const [rawDocStatus, setRawDocStatus] = useState<RawDocumentStatus[]>([]);
  const [rawExemption, setRawExemption] = useState<RawExemption[]>([]);
  const [rawRanking, setRawRanking] = useState<RawRanking[]>([]);
  const [rawCounts, setRawCounts] = useState({
    contractQuantity: 0,
    supplierQuantity: 0,
    allocatedEmployeeQuantity: 0,
    employeeQuantity: 0,
  });

  const [chartData, setChartData] = useState<ChartData[]>([]);
  const [documentExemptionData, setDocumentExemptionData] = useState<
    RawExemption[]
  >([]);
  const [tableData, setTableData] = useState<any[]>([]);
  const [stats, setStats] = useState({
    contractQuantity: 0,
    supplierQuantity: 0,
    allocatedEmployeeQuantity: 0,
    employeeQuantity: 0,
  });

  useEffect(() => {
    if (!clientId) {
      console.warn("Filters: clientId não disponível. Não será possível carregar as opções de filtro.");
      return;
    }
    (async () => {
      try {
        const url = `${ip}/dashboard/${clientId}/filters`;
        console.log(`Filters: Solicitando opções de filtro da URL: ${url}`);
        const { data } = await axios.get(url, {
          headers: {
            Authorization: `Bearer ${token}`,
            Accept: "application/json",
          },
        });
        console.log("Filters: Dados recebidos com sucesso.");
        const {
          branches = [],
          providers = [],
          documentTypes = [],
          responsibles = [],
          contractStatus = [],
          statuses = [],
          documentTitles = [],
        } = data ?? {};
        
        const mapIdNameBranches = buildIdNameMap(branches);
        const mapIdNameProviders = buildIdNameMap(providers);
        const mapIdNameResps = buildIdNameMap(responsibles);
        
        const toOptions = (arr: any[]) =>
        arr.map((v) => ({ value: String(v), label: String(v) }));
        const toOptionsIdName = (
          arr: Array<{ id: string | number; name?: string }>
        ) =>
        arr.map((v) => ({
          value: String(v.id),
          label: v.name ?? String(v.id),
        }));
        
        setBranchOpts(toOptionsIdName(branches));
        setProviderOpts(toOptionsIdName(providers));
        setRespOpts(toOptionsIdName(responsibles));
        setDocTypeOpts(toOptions(documentTypes));
        setContractStatusOpts(toOptions(contractStatus));
        setStatusOpts(toOptions(statuses));
        setDocTitleOpts(toOptions(documentTitles));
        
        setBranchIdName(mapIdNameBranches);
        setProviderIdName(mapIdNameProviders);
        setRespIdName(mapIdNameResps);
      } catch (e) {
        console.error("Filters: Erro ao carregar /filters", e);
      }
    })();
  }, [clientId, token]);

  useEffect(() => {
    if (!clientId) {
      console.warn("General: clientId não disponível. Limpando estados de dados.");
      setRawDocStatus([]);
      setRawExemption([]);
      setRawRanking([]);
      setRawCounts({ contractQuantity: 0, supplierQuantity: 0, allocatedEmployeeQuantity: 0, employeeQuantity: 0 });
      setChartData([]);
      setDocumentExemptionData([]);
      setTableData([]);
      setStats({ contractQuantity: 0, supplierQuantity: 0, allocatedEmployeeQuantity: 0, employeeQuantity: 0 });
      return;
    }
    (async () => {
      try {
        const url = `${ip}/dashboard/${clientId}/general`;
        const requestBody = { clientId: clientId };
        console.log(`General: Solicitando dados da URL: ${url}`);
        console.log(`General: Corpo da requisição:`, requestBody);
        const { data } = await axios.post(
          url,
          requestBody,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
              Accept: "application/json",
            },
          }
        );
        console.log("General: Dados recebidos com sucesso:", data);

        const {
          documentExemption = [],
          documentStatus = [],
          pendingRanking = [],
          contractQuantity = 0,
          supplierQuantity = 0,
          allocatedEmployeeQuantity = 0,
          employeeQuantity = 0,
        } = data ?? {};

        setRawDocStatus(documentStatus);
        setRawExemption(documentExemption);
        setRawRanking(pendingRanking);
        setRawCounts({
          contractQuantity,
          supplierQuantity,
          allocatedEmployeeQuantity,
          employeeQuantity,
        });

        setChartData(
          (documentStatus ?? []).map((cat: any) => {
            const row: any = { name: cat?.name ?? "" };
            (cat?.status ?? []).forEach((s: any) => {
              if (s?.status) row[s.status] = s.quantity ?? 0;
            });
            return row;
          })
        );
        setDocumentExemptionData(documentExemption ?? []);
        setTableData(
          (pendingRanking ?? []).map((r: any) => ({
            name: r.corporateName,
            cnpj: r.cnpj,
            adherence: r.adherence,
            conformity: r.conformity,
            nonConformDocs: r.nonConformingDocumentQuantity,
            conformityLevel: r.conformityLevel,
          }))
        );
        setStats({
          contractQuantity,
          supplierQuantity,
          allocatedEmployeeQuantity,
          employeeQuantity,
        });
      } catch (e) {
        console.error("General: Erro ao carregar /general", e);
        setRawDocStatus([]);
        setRawExemption([]);
        setRawRanking([]);
        setRawCounts({ contractQuantity: 0, supplierQuantity: 0, allocatedEmployeeQuantity: 0, employeeQuantity: 0 });
        setChartData([]);
        setDocumentExemptionData([]);
        setTableData([]);
        setStats({ contractQuantity: 0, supplierQuantity: 0, allocatedEmployeeQuantity: 0, employeeQuantity: 0 });
      }
    })();
  }, [clientId, token]);

  const conformity = tableData.length > 0 ? tableData[0]?.conformity : 0;


  const adherence = tableData.length > 0 ? tableData[0]?.adherence : 0;

  function applyFilters() {
    console.log("Aplicando filtros:", draft);
    setApplied({ ...draft });
  }

  function clearFilters() {
    console.log("Limpando filtros.");
    const empty: FiltersState = {
      branchIds: [],
      providerIds: [],
      documentTypes: [],
      responsibleIds: [],
      activeContract: [],
      statuses: [],
      documentTitles: [],
    };
    setDraft(empty);
    setApplied(empty);
  }

  useEffect(() => {
    console.log("Dados aplicados (applied) mudaram, aplicando filtros locais.");
    const branchNames = applied.branchIds.map(
      (id) => branchIdName.get(id) ?? id
    );
    const providerNames = applied.providerIds.map(
      (id) => providerIdName.get(id) ?? id
    );
    const responsibleNames = applied.responsibleIds.map(
      (id) => respIdName.get(id) ?? id
    );

    // documentStatus

    const docStatusChart = filterDocumentStatus(rawDocStatus, applied, {
      branchNames,
      providerNames,
      responsibleNames,
    });
    console.log("Resultados do filtro de Status de Documentos:", docStatusChart);
    setChartData(docStatusChart);

    const docExFiltered = filterExemption(rawExemption, applied, {
      branchNames,
      providerNames,
      responsibleNames,
    });
    console.log("Resultados do filtro de Exenção:", docExFiltered);
    setDocumentExemptionData(docExFiltered);

    const rankingFiltered = filterRanking(rawRanking, applied, {
      branchNames,
      providerNames,
      responsibleNames,
    });
    console.log("Resultados do filtro de Ranking:", rankingFiltered);
    setTableData(
      rankingFiltered.map((r) => ({
        name: r.corporateName,
        cnpj: r.cnpj,
        adherence: r.adherence,
        conformity: r.conformity,
        nonConformDocs: r.nonConformingDocumentQuantity,
        conformityLevel: r.conformityLevel,
      }))
    );

    const supplierCount = new Set(rankingFiltered.map((r) => r.corporateName))
      .size;
    setStats({
      contractQuantity: rawCounts.contractQuantity,
      supplierQuantity: supplierCount,
      allocatedEmployeeQuantity: rawCounts.allocatedEmployeeQuantity,
      employeeQuantity: rawCounts.employeeQuantity,
    });
    console.log("Estatísticas atualizadas:", {
      contractQuantity: rawCounts.contractQuantity,
      supplierQuantity: supplierCount,
      allocatedEmployeeQuantity: rawCounts.allocatedEmployeeQuantity,
      employeeQuantity: rawCounts.employeeQuantity,
    });
  }, [
    applied,
    rawDocStatus,
    rawExemption,
    rawRanking,
    branchIdName,
    providerIdName,
    respIdName,
    rawCounts,
  ]);

  const canApply = useMemo(
    () => JSON.stringify(draft) !== JSON.stringify(applied),
    [draft, applied]
  );

  const generatePDF = () => {
    try {
      const doc = new jsPDF();

      const clientName = client?.corporateName || "Cliente não informado";
      const selectedBranchNames =
        draft.branchIds.length > 0
          ? draft.branchIds.map((id) => branchIdName.get(id) || id).join(", ")
          : "Todas as filiais";

      // Cabeçalho Principal do PDF
      doc.setFontSize(18);
      doc.text("Relatório Geral de Monitoramento", 14, 22);
      doc.setFontSize(11);
      doc.text(`Cliente: ${clientName}`, 14, 30);
      doc.text(`Filial(is): ${selectedBranchNames}`, 14, 36);

      const titleStyle = {
        fontStyle: "bold",
        fontSize: 14,
        cellPadding: { top: 10, bottom: 5 },
        halign: "left",
      };
      const headerStyleGreen = {
        fillColor: [22, 160, 133],
        textColor: 255,
        fontStyle: "bold",
      };
      const headerStyleBlue = {
        fillColor: [41, 128, 185],
        textColor: 255,
        fontStyle: "bold",
      };
      const headerStyleOrange = {
        fillColor: [243, 156, 18],
        textColor: 255,
        fontStyle: "bold",
      };
      const headerStyleRed = {
        fillColor: [192, 57, 43],
        textColor: 255,
        fontStyle: "bold",
      };

      const allRows: any[] = [];

      // --- Seção 1: Resumo Geral ---
      if (rawCounts) {
        allRows.push([
          { content: "Resumo Geral", colSpan: 6, styles: titleStyle },
        ]);
        allRows.push([
          { content: "Métrica", styles: headerStyleGreen },
          { content: "Quantidade", styles: headerStyleGreen },
        ]);
        allRows.push(["Fornecedores", rawCounts.supplierQuantity]);
        allRows.push(["Contratos", rawCounts.contractQuantity]);
        allRows.push([
          "Colaboradores",
          rawCounts.employeeQuantity,
        ]);
        allRows.push([
          "Funcionários Alocados",
          rawCounts.allocatedEmployeeQuantity,
        ]);
      }

      // --- Seção 2: Status dos Documentos ---
      // A MUDANÇA ESTÁ AQUI: Adicionamos o tipo 'any[]' para a variável statusRows.
      const statusRows: any[] = [];
      rawDocStatus.forEach((doc) => {
        doc.status.forEach((s) => {
          if (s.quantity > 0) {
            statusRows.push([doc.name, s.status, s.quantity]);
          }
        });
      });

      if (statusRows.length > 0) {
        allRows.push([
          { content: "Status dos Documentos", colSpan: 6, styles: titleStyle },
        ]);
        allRows.push([
          { content: "Tipo de Documento", styles: headerStyleBlue },
          { content: "Status", styles: headerStyleBlue },
          { content: "Quantidade", styles: headerStyleBlue },
        ]);
        allRows.push(...statusRows);
      }

      // --- Seção 3: Isenção de Documentos ---
      if (rawExemption.length > 0) {
        allRows.push([
          { content: "Isenção de Documentos", colSpan: 6, styles: titleStyle },
        ]);
        allRows.push([
          { content: "Nome", styles: headerStyleOrange },
          { content: "Quantidade", styles: headerStyleOrange },
        ]);
        rawExemption.forEach((item) => {
          allRows.push([item.name, item.quantity]);
        });
      }

      // --- Seção 4: Ranking de Pendências ---
      if (rawRanking.length > 0) {
        allRows.push([
          { content: "Ranking de Pendências", colSpan: 6, styles: titleStyle },
        ]);
        allRows.push([
          { content: "Razão Social", styles: headerStyleRed },
          { content: "CNPJ", styles: headerStyleRed },
          { content: "Aderência", styles: headerStyleRed },
          { content: "Conformidade", styles: headerStyleRed },
          { content: "Docs Não Conformes", styles: headerStyleRed },
          { content: "Nível", styles: headerStyleRed },
        ]);
        rawRanking.forEach((item) => {
          allRows.push([
            item.corporateName,
            item.cnpj,
            `${item.adherence.toFixed(2)}%`,
            `${item.conformity.toFixed(2)}%`,
            item.nonConformingDocumentQuantity,
            item.conformityLevel,
          ]);
        });
      }

      // --- A ÚNICA CHAMADA AUTOTABLE ---
      autoTable(doc, {
        startY: 45,
        body: allRows,
        theme: "grid",
        columnStyles: {
          0: { cellWidth: 45 },
          1: { cellWidth: 40 },
          2: { cellWidth: "auto" },
          3: { cellWidth: "auto" },
          4: { cellWidth: 25 },
          5: { cellWidth: "auto" },
        },
      });

      doc.save("relatorio_geral.pdf");
    } catch (error) {
      console.error("ERRO CRÍTICO ao gerar PDF:", error);
      alert(
        "Ocorreu um erro inesperado ao gerar o relatório. Verifique o console para mais detalhes."
      );
    }
  };

  return (
    <>
      <Helmet title="monitoring table" />
      <section
        className="mx-5 md:mx-20 flex flex-col gap-6 pb-20"
        id="contentToCapture"
      >
        {/* Tabs */}
        <div className="border-b border-gray-200">
          <nav className="-mb-px flex space-x-8" aria-label="Tabs">
            <button
              onClick={() => setActiveTab("visao-geral")}
              className={`whitespace-nowrap border-b-2 px-1 py-4 text-sm font-medium ${
                activeTab === "visao-geral"
                  ? "border-blue-600 text-blue-600"
                  : "border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700"
              }`}
            >
              Visão Geral
            </button>
            <button
              onClick={() => setActiveTab("fornecedores")}
              className={`whitespace-nowrap border-b-2 px-1 py-4 text-sm font-medium ${
                activeTab === "fornecedores"
                  ? "border-blue-600 text-blue-600"
                  : "border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700"
              }`}
            >
              Fornecedores
            </button>
          </nav>
        </div>

        {activeTab === "visao-geral" && (
          <div>
            <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
              <h2 className="text-lg font-semibold text-gray-800">Filtros</h2>

              <div className="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-4">
                <MultiSelectDropdown
                  label="Unidades"
                  options={branchOpts}
                  values={draft.branchIds}
                  onChange={(v) => setDraft((s) => ({ ...s, branchIds: v }))}
                />
                <MultiSelectDropdown
                  label="Fornecedores"
                  options={providerOpts}
                  values={draft.providerIds}
                  onChange={(v) => setDraft((s) => ({ ...s, providerIds: v }))}
                />
                <MultiSelectDropdown
                  label="Tipo de Documento"
                  options={docTypeOpts}
                  values={draft.documentTypes}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, documentTypes: v }))
                  }
                />
                <MultiSelectDropdown
                  label="Responsáveis"
                  options={respOpts}
                  values={draft.responsibleIds}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, responsibleIds: v }))
                  }
                />
                <MultiSelectDropdown
                  label="Status do Contrato"
                  options={contractStatusOpts}
                  values={draft.activeContract}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, activeContract: v }))
                  }
                />
                <MultiSelectDropdown
                  label="Status"
                  options={statusOpts}
                  values={draft.statuses}
                  onChange={(v) => setDraft((s) => ({ ...s, statuses: v }))}
                />
                <MultiSelectDropdown
                  label="Títulos de Documento"
                  options={docTitleOpts}
                  values={draft.documentTitles}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, documentTitles: v }))
                  }
                />
              </div>

              <div className="mt-6 flex justify-end gap-3 border-t border-gray-200 pt-4">
                <button
                  type="button"
                  onClick={clearFilters}
                  className="rounded-md bg-white px-4 py-2 text-sm font-medium text-gray-800 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
                >
                  Limpar
                </button>
                <button
                  type="button"
                  onClick={applyFilters}
                  disabled={!canApply}
                  className={`rounded-md px-4 py-2 text-sm font-medium text-white shadow-sm ${
                    canApply
                      ? "bg-blue-600 hover:bg-blue-700"
                      : "cursor-not-allowed bg-blue-300"
                  }`}
                >
                  Aplicar Filtros
                </button>
              </div>
            </div>


            <div className="grid grid-cols-1 py-5 gap-6 md:grid-cols-2">
              <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm h-[30vh]">
                <ConformityGaugeChart percentage={conformity} />
              </div>

              <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm h-[30vh]">
                <ConformityGaugeChart
                  title="Aderência"
                  percentage={adherence}
                />
              </div>
            </div>

            <div className="grid grid-cols-1 gap-6 sm:grid-cols-4">
              <div className="rounded-lg py-6 flex justify-center">
                <ActiveContracts count={stats.contractQuantity ?? 0} />
              </div>

              <div className="rounded-lg py-6 flex justify-center">
                <Suppliers count={stats.supplierQuantity ?? 0} />
              </div>

              <div className="rounded-lg py-6 flex justify-center">
                <Employees
                  count={stats.employeeQuantity ?? 0}
                />
              </div>
              <div className="rounded-lg py-6 flex justify-center">
                <AllocatedEmployees
                  count={stats.allocatedEmployeeQuantity ?? 0}
                />
              </div>
            </div>

            <div className="overflow-x-auto mt-10 pb-10">
              <StatusDocumentChart data={chartData} />
            </div>
            <div className="overflow-x-auto mt-10">
              <div className="flex flex-col md:flex-row gap-6 md:gap-8">
                <div className="w-full md:w-2/5">
                  <ExemptionPendingChart data={documentExemptionData} />
                </div>

                <div className="w-full md:w-3/5">
                  <ConformityRankingTable data={tableData} />
                </div>
              </div>
            </div>

            <button
              onClick={generatePDF}
              className="mt-4 px-4 py-2 bg-blue-500 text-white rounded"
            >
              Gerar Relatório em PDF
            </button>
          </div>
        )}

        {activeTab === "fornecedores" && (
          <div>

            <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
              <h2 className="text-lg font-semibold text-gray-800">Filtros</h2>

              <div className="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-4">
                <MultiSelectDropdown
                  label="Unidades"
                  options={branchOpts}
                  values={draft.branchIds}
                  onChange={(v) => setDraft((s) => ({ ...s, branchIds: v }))}
                />
                <MultiSelectDropdown
                  label="Fornecedores"
                  options={providerOpts}
                  values={draft.providerIds}
                  onChange={(v) => setDraft((s) => ({ ...s, providerIds: v }))}
                />
                <MultiSelectDropdown
                  label="Tipo de Documento"
                  options={docTypeOpts}
                  values={draft.documentTypes}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, documentTypes: v }))
                  }
                />
                <MultiSelectDropdown
                  label="Responsáveis"
                  options={respOpts}
                  values={draft.responsibleIds}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, responsibleIds: v }))
                  }
                />
                <MultiSelectDropdown
                  label="Status do Contrato"
                  options={contractStatusOpts}
                  values={draft.activeContract}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, activeContract: v }))
                  }
                />
                <MultiSelectDropdown
                  label="Status"
                  options={statusOpts}
                  values={draft.statuses}
                  onChange={(v) => setDraft((s) => ({ ...s, statuses: v }))}
                />
                <MultiSelectDropdown
                  label="Títulos de Documento"
                  options={docTitleOpts}
                  values={draft.documentTitles}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, documentTitles: v }))
                  }
                />
              </div>

              <div className="mt-6 flex justify-end gap-3 border-t border-gray-200 pt-4">
                <button
                  type="button"
                  onClick={clearFilters}
                  className="rounded-md bg-white px-4 py-2 text-sm font-medium text-gray-800 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"

                >
                  Limpar
                </button>
                <button
                  type="button"
                  onClick={applyFilters}
                  disabled={!canApply}
                  className={`rounded-md px-4 py-2 text-sm font-medium text-white shadow-sm ${
                    canApply
                      ? "bg-blue-600 hover:bg-blue-700"
                      : "cursor-not-allowed bg-blue-300"
                  }`}
                >
                  Aplicar Filtros
                </button>
              </div>
            </div>

            <div className="overflow-x-auto mt-6">
              {clientId ? (
                <FornecedoresTable clientId={clientId} filters={applied} />
              ) : (
                <div>Erro: O clientId não está disponível.</div>
              )}
            </div>
          </div>
        )}
      </section>
    </>
  );
};