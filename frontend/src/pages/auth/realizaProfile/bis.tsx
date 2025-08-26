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
import { ActiveContracts } from "@/components/BIs/BisPageComponents/activeContracts";
import { Suppliers } from "@/components/BIs/BisPageComponents/suppliersCard";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";
import { useEffect, useMemo, useRef, useState } from "react";
import { jsPDF } from "jspdf";
import html2canvas from "html2canvas";

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
      setRawCounts({ contractQuantity: 0, supplierQuantity: 0, allocatedEmployeeQuantity: 0 });
      setChartData([]);
      setDocumentExemptionData([]);
      setTableData([]);
      setStats({ contractQuantity: 0, supplierQuantity: 0, allocatedEmployeeQuantity: 0 });
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
        } = data ?? {};

        setRawDocStatus(documentStatus);
        setRawExemption(documentExemption);
        setRawRanking(pendingRanking);
        setRawCounts({
          contractQuantity,
          supplierQuantity,
          allocatedEmployeeQuantity,
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
        });
      } catch (e) {
        console.error("General: Erro ao carregar /general", e);
        setRawDocStatus([]);
        setRawExemption([]);
        setRawRanking([]);
        setRawCounts({ contractQuantity: 0, supplierQuantity: 0, allocatedEmployeeQuantity: 0 });
        setChartData([]);
        setDocumentExemptionData([]);
        setTableData([]);
        setStats({ contractQuantity: 0, supplierQuantity: 0, allocatedEmployeeQuantity: 0 });
      }
    })();
  }, [clientId, token]);

  const conformity = tableData.length > 0 ? tableData[0]?.conformity : 0;

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
    });
    console.log("Estatísticas atualizadas:", {
      contractQuantity: rawCounts.contractQuantity,
      supplierQuantity: supplierCount,
      allocatedEmployeeQuantity: rawCounts.allocatedEmployeeQuantity,
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
    console.log("Iniciando a geração de PDF.");
    const content = document.getElementById("contentToCapture");
    if (!content) {
      console.error("Erro: Elemento de conteúdo para PDF não encontrado.");
      return;
    }
    html2canvas(content).then((canvas) => {
      const imgData = canvas.toDataURL("image/png");
      const doc = new jsPDF();
      doc.addImage(imgData, "PNG", 10, 10, 180, 160);
      doc.save("graficos_completos.pdf");
      console.log("PDF gerado e salvo com sucesso.");
    });
  };

  return (
    <>
      <Helmet title="monitoring table" />
      <section
        className="mx-5 md:mx-20 flex flex-col gap-6 pb-20"
        id="contentToCapture"
      >
        <div className="flex gap-4 mb-6">
          <button
            onClick={() => setActiveTab("visao-geral")}
            className={`px-4 py-2 rounded-t-md ${
              activeTab === "visao-geral"
                ? "bg-blue-600 text-white"
                : "bg-gray-200"
            }`}
          >
            Visão Geral
          </button>
          <button
            onClick={() => setActiveTab("fornecedores")}
            className={`px-4 py-2 rounded-t-md ${
              activeTab === "fornecedores"
                ? "bg-blue-600 text-white"
                : "bg-gray-200"
            }`}
          >
            Fornecedores
          </button>
        </div>

        {activeTab === "visao-geral" && (
          <div>
            <div className="flex flex-wrap gap-3">
              <MultiSelectDropdown
                label="Unidades"
                options={branchOpts}
                values={draft.branchIds}
                onChange={(v) => setDraft((s) => ({ ...s, branchIds: v }))}
                className="w-full md:w-1/2 lg:w-1/4"
              />
              <MultiSelectDropdown
                label="Fornecedores"
                options={providerOpts}
                values={draft.providerIds}
                onChange={(v) => setDraft((s) => ({ ...s, providerIds: v }))}
                className="w-full md:w-1/2 lg:w-1/4"
              />
              <MultiSelectDropdown
                label="Tipo de Documento"
                options={docTypeOpts}
                values={draft.documentTypes}
                onChange={(v) => setDraft((s) => ({ ...s, documentTypes: v }))}
                className="w-full md:w-1/2 lg:w-1/4"
              />
              <MultiSelectDropdown
                label="Responsáveis"
                options={respOpts}
                values={draft.responsibleIds}
                onChange={(v) => setDraft((s) => ({ ...s, responsibleIds: v }))}
                className="w-full md:w-1/2 lg:w-1/4"
              />
              <MultiSelectDropdown
                label="Status do Contrato"
                options={contractStatusOpts}
                values={draft.activeContract}
                onChange={(v) => setDraft((s) => ({ ...s, activeContract: v }))}
                className="w-full md:w-1/2 lg:w-1/4"
              />
              <MultiSelectDropdown
                label="Status"
                options={statusOpts}
                values={draft.statuses}
                onChange={(v) => setDraft((s) => ({ ...s, statuses: v }))}
                className="w-full md:w-1/2 lg:w-1/4"
              />
              <MultiSelectDropdown
                label="Títulos de Documento"
                options={docTitleOpts}
                values={draft.documentTitles}
                onChange={(v) => setDraft((s) => ({ ...s, documentTitles: v }))}
                className="w-full md:w-1/2 lg:w-1/4"
              />

              <div className="flex items-center gap-2 w-full md:w-auto">
                <button
                  type="button"
                  onClick={() => setApplied({ ...draft })}
                  disabled={!canApply}
                  className={`px-4 py-2 rounded-md text-white ${
                    canApply
                      ? "bg-blue-600"
                      : "bg-blue-300 cursor-not-allowed"
                  }`}
                >
                  Aplicar
                </button>
                <button
                  type="button"
                  onClick={clearFilters}
                  className="px-4 py-2 bg-gray-100 border rounded-md"
                >
                  Limpar
                </button>
              </div>
            </div>

            <div className="mt-2 flex min-w-[800px]">
              <ActiveContracts count={stats.contractQuantity ?? 0} />
              <Suppliers count={stats.supplierQuantity ?? 0} />
              <AllocatedEmployees
                count={stats.allocatedEmployeeQuantity ?? 0}
              />
              <div className="h-[30vh] w-[30vw] rounded-lg border bg-white p-5 shadow-sm">
                <ConformityGaugeChart percentage={conformity} />
              </div>
            </div>

            <div className="overflow-x-auto mt-10 pb-10">
              <StatusDocumentChart data={chartData} />
            </div>
            <div className="overflow-x-auto mt-10">
              <div className="flex flex-col md:flex-row gap-6 md:gap-8 min-w-[320px] md:min-w-full">
                <div className="flex-shrink-0 w-full md:w-[400px]">
                  <ExemptionPendingChart data={documentExemptionData} />
                </div>
                <div className="flex-grow min-w-[320px] overflow-x-auto">
                  <ConformityRankingTable data={tableData} />
                </div>
              </div>
            </div>

            <button
              onClick={generatePDF}
              className="mt-4 px-4 py-2 bg-blue-500 text-white rounded"
            >
              Gerar PDF com Gráficos
            </button>
          </div>
        )}

        {activeTab === "fornecedores" && (
          <div>
            <div className="mt-2 flex flex-wrap gap-3">
              <MultiSelectDropdown
                label="Unidades"
                options={branchOpts}
                values={draft.branchIds}
                onChange={(v) => setDraft((s) => ({ ...s, branchIds: v }))}
                className="w-full md:w-1/2 lg:w-1/4"
              />
              <MultiSelectDropdown
                label="Fornecedores"
                options={providerOpts}
                values={draft.providerIds}
                onChange={(v) => setDraft((s) => ({ ...s, providerIds: v }))}
                className="w-full md:w-1/2 lg:w-1/4"
              />
              <MultiSelectDropdown
                label="Tipo de Documento"
                options={docTypeOpts}
                values={draft.documentTypes}
                onChange={(v) => setDraft((s) => ({ ...s, documentTypes: v }))}
                className="w-full md:w-1/2 lg:w-1/4"
              />
              <MultiSelectDropdown
                label="Responsáveis"
                options={respOpts}
                values={draft.responsibleIds}
                onChange={(v) => setDraft((s) => ({ ...s, responsibleIds: v }))}
                className="w-full md:w-1/2 lg:w-1/4"
              />
              <MultiSelectDropdown
                label="Status"
                options={statusOpts}
                values={draft.statuses}
                onChange={(v) => setDraft((s) => ({ ...s, statuses: v }))}
                className="w-full md:w-1/2 lg:w-1/4"
              />
              <MultiSelectDropdown
                label="Títulos de Documento"
                options={docTitleOpts}
                values={draft.documentTitles}
                onChange={(v) => setDraft((s) => ({ ...s, documentTitles: v }))}
                className="w-full md:w-1/2 lg:w-1/4"
              />

              <div className="flex items-center gap-2 w-full md:w-auto">
                <button
                  type="button"
                  onClick={applyFilters}
                  disabled={!canApply}
                  className={`px-4 py-2 rounded-md text-white ${
                    canApply
                      ? "bg-blue-600"
                      : "bg-blue-300 cursor-not-allowed"
                  }`}
                >
                  Aplicar
                </button>
                <button
                  type="button"
                  onClick={clearFilters}
                  className="px-4 py-2 bg-gray-100 border rounded-md"
                >
                  Limpar
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