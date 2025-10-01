import { Helmet } from "react-helmet-async";
import {
  StatusDocumentChart,
  ChartData,
} from "@/components/BIs/BisPageComponents/statusDocumentChart";
import { ExemptionPendingChart } from "@/components/BIs/BisPageComponents/exemptionRankingChart";
import { ConformityGaugeChart } from "@/components/BIs/BisPageComponents/conformityChart";
import { HistoryChart } from "@/components/BIs/BisPageComponents/HistoryChart";
import FornecedoresTable from "@/components/BIs/BisPageComponents/FornecedoresTable";
import { GeneralDocumentsTable } from "@/components/BIs/BisPageComponents/GeneralDocumentsTable";
import { ConformityRankingTable } from "@/components/BIs/BisPageComponents/conformityRankingTable";
import { AllocatedEmployees } from "@/components/BIs/BisPageComponents/AllocatedEmployees";
import { Employees } from "@/components/BIs/BisPageComponents/employees";
import { ActiveContracts } from "@/components/BIs/BisPageComponents/activeContracts";
import { Suppliers } from "@/components/BIs/BisPageComponents/suppliersCard";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";
import { useEffect, useMemo, useRef, useState } from "react";
import { TotalDocumentsCard } from "@/components/BIs/BisPageComponents/TotalDocumentsCard";

import * as XLSX from "xlsx";
import { saveAs } from "file-saver";

import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

const mockHistoryData = [
  { mes: "Mai/25", Aderência: 83.04, Conformidade: 79.34 },
  { mes: "Jun/25", Aderência: 86.52, Conformidade: 82.0 },
  { mes: "Jul/25", Aderência: 91.27, Conformidade: 86.1 },
  { mes: "Ago/25", Aderência: 91.01, Conformidade: 88.3 },
  { mes: "Set/25", Aderência: 92.41, Conformidade: 88.01 },
];

const mockDocStatusData: RawDocumentStatus[] = [
  {
    name: "Segurança do Trabalho",
    status: [
      { status: "APROVADO", quantity: 120 },
      { status: "PENDENTE", quantity: 35 },
      { status: "REPROVADO", quantity: 15 },
      { status: "VENCIDO", quantity: 8 },
    ],
  },
  {
    name: "Cadastro e Certidões",
    status: [
      { status: "APROVADO", quantity: 250 },
      { status: "PENDENTE", quantity: 40 },
      { status: "REPROVADO", quantity: 5 },
      { status: "ISENTO", quantity: 50 },
    ],
  },
  {
    name: "Saúde Ocupacional",
    status: [
      { status: "APROVADO", quantity: 180 },
      { status: "PENDENTE", quantity: 60 },
      { status: "VENCIDO", quantity: 22 },
    ],
  },
  {
    name: "Outras Exigências",
    status: [
      { status: "APROVADO", quantity: 95 },
      { status: "PENDENTE", quantity: 10 },
    ],
  },
];

const monthMap: { [key: string]: string } = {
  JANUARY: "Jan",
  FEBRUARY: "Fev",
  MARCH: "Mar",
  APRIL: "Abr",
  MAY: "Mai",
  JUNE: "Jun",
  JULY: "Jul",
  AUGUST: "Ago",
  SEPTEMBER: "Set",
  OCTOBER: "Out",
  NOVEMBER: "Nov",
  DECEMBER: "Dez",
};

const contractStatusMap: { [key: string]: string } = {
  PENDING: "Pendente",
  DENIED: "Negado",
  ACTIVE: "Ativo",
  FINISHED: "Finalizado",
  FINISH_REQUESTED: "Solicitação de Finalização",
  SUSPENDED: "Suspenso",
  SUSPEND_REQUESTED: "Solicitação de Suspensão",
  REACTIVATION_REQUESTED: "Solicitação de Reativação",
};

const docValidityMap: { [key: string]: string } = {
  INDEFINITE: "Indefinido",
  WEEKLY: "Semanal",
  MONTHLY: "Mensal",
  ANNUAL: "Anual",
};

const docTypeMap: { [key: string]: string } = {
  thirdCompany: "Empresa Terceirizada",
  thirdCollaborators: "Colaboradores Terceirizados",
  otherRequirements: "Outras Exigências",
};

const employeeSituationMap: { [key: string]: string } = {
  ALOCADO: "Alocado",
  DESALOCADO: "Desalocado",
  DEMITIDO: "Demitido",
  AFASTADO: "Afastado",
  LICENCA_MATERNIDADE: "Licença Maternidade",
  LICENCA_MEDICA: "Licença Médica",
  LICENCA_MILITAR: "Licença Militar",
};

const documentStatusMap: { [key: string]: string } = {
  PENDENTE: "Pendente",
  EM_ANALISE: "Em Análise",
  REPROVADO: "Reprovado",
  APROVADO: "Aprovado",
  REPROVADO_IA: "Reprovado (IA)",
  APROVADO_IA: "Aprovado (IA)",
  VENCIDO: "Vencido",
};

type Option = { value: string; label: string };

type FiltersState = {
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
};

type RawDocumentStatus = {
  name: string;
  status: Array<{ status: string; quantity: number }>;

  doesBlock?: boolean;
  validity?: string;
};
type RawExemption = { name: string; quantity: number };
type RawRanking = {
  corporateName: string;
  cnpj: string;
  adherence: number;
  conformity: number;
  nonConformingDocumentQuantity: number;
  conformityLevel: string;

  contractId?: string;
  employeeId?: string;
  employeeCpf?: string;
  employeeSituation?: string;
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
    placeholder = "",
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

  let filtered = raw.filter((r) => {
    const byName = matchesAny(r.name, nameNeedles);

    const byBlock = filters.documentDoesBlock.length
      ? filters.documentDoesBlock.includes(r.doesBlock ?? false)
      : true;
    const byValidity = filters.documentValidity.length
      ? filters.documentValidity.includes(r.validity ?? "")
      : true;

    return byName && byBlock && byValidity;
  });

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
    contractNames: string[];
    employeeNames: string[];
  }
) {
  const providerNeedles = names.providerNames;
  const otherNeedles = [
    ...names.branchNames,
    ...names.responsibleNames,
    ...filters.documentTitles,
    ...filters.documentTypes,

    ...names.contractNames,
    ...names.employeeNames,
  ].filter(Boolean);

  return raw.filter((r) => {
    const byProvider = providerNeedles.length
      ? matchesAny(r.corporateName, providerNeedles)
      : true;

    const byOthers = otherNeedles.length
      ? matchesAny(r.corporateName, otherNeedles)
      : true;

    const byProviderCnpj = filters.providerCnpjs.length
      ? filters.providerCnpjs.includes(r.cnpj)
      : true;

    const byEmployeeCpf = filters.employeeCpfs.length
      ? filters.employeeCpfs.includes(r.employeeCpf ?? "")
      : true;

    const byEmployeeSituation = filters.employeeSituations.length
      ? filters.employeeSituations.includes(r.employeeSituation ?? "")
      : true;

    return (
      byProvider &&
      byOthers &&
      byProviderCnpj &&
      byEmployeeCpf &&
      byEmployeeSituation
    );
  });
}

export const MonittoringBis = () => {
  const [activeTab, setActiveTab] = useState("visao-geral");
  const { client } = useClient();
  const clientId = client?.idClient;
  const token = localStorage.getItem("tokenClient");

  const USE_MOCK_DATA = true;

  const [branchOpts, setBranchOpts] = useState<Option[]>([]);
  const [providerOpts, setProviderOpts] = useState<Option[]>([]);
  const [docTypeOpts, setDocTypeOpts] = useState<Option[]>([]);
  const [respOpts, setRespOpts] = useState<Option[]>([]);
  const [contractStatusOpts, setContractStatusOpts] = useState<Option[]>([]);
  const [statusOpts, setStatusOpts] = useState<Option[]>([]);
  const [docTitleOpts, setDocTitleOpts] = useState<Option[]>([]);

  const [providerCnpjOpts, setProviderCnpjOpts] = useState<Option[]>([]);
  const [contractOpts, setContractOpts] = useState<Option[]>([]);
  const [employeeOpts, setEmployeeOpts] = useState<Option[]>([]);
  const [employeeCpfOpts, setEmployeeCpfOpts] = useState<Option[]>([]);
  const [employeeSituationOpts, setEmployeeSituationOpts] = useState<Option[]>(
    []
  );
  const [docBlockOpts, setDocBlockOpts] = useState<Option[]>([]);
  const [docValidityOpts, setDocValidityOpts] = useState<Option[]>([]);

  const [branchIdName, setBranchIdName] = useState<Map<string, string>>(
    new Map()
  );
  const [providerIdName, setProviderIdName] = useState<Map<string, string>>(
    new Map()
  );
  const [respIdName, setRespIdName] = useState<Map<string, string>>(new Map());

  const [contractIdName, setContractIdName] = useState<Map<string, string>>(
    new Map()
  );
  const [employeeIdName, setEmployeeIdName] = useState<Map<string, string>>(
    new Map()
  );

  const [historyData, setHistoryData] = useState<any[]>([]);
  const [isLoadingHistory, setIsLoadingHistory] = useState(false);

  const [historyPeriod] = useState({
    startMonth: "JANEIRO",
    startYear: 2025,
    endMonth: "DEZEMBRO",
    endYear: 2025,
  });

  const [draft, setDraft] = useState<FiltersState>({
    branchIds: [],
    providerIds: [],
    documentTypes: [],
    responsibleIds: [],
    activeContract: [],
    statuses: [],
    documentTitles: [],

    providerCnpjs: [],
    contractIds: [],
    employeeIds: [],
    employeeCpfs: [],
    employeeSituations: [],
    documentDoesBlock: [],
    documentValidity: [],
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

  const [totalDocuments, setTotalDocuments] = useState(0);

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
      console.warn(
        "Filters: clientId não disponível. Não será possível carregar as opções de filtro."
      );
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

          providerCnpjs = [],
          contracts = [],
          employees = [],
          employeeCpfs = [],
          employeeSituations = [],
          documentDoesBlock = [],
          documentValidity = [],
        } = data ?? {};
        
        console.log("Filters Log: CPFs recebidos da API:", employeeCpfs); 

        const toOptions = (arr: any[]) => {
          const uniqueValues = Array.from(new Set(arr));
          return uniqueValues.map((v) => ({
            value: String(v),
            label: String(v),
          }));
        };

        const toOptionsIdName = (
          arr: Array<{ id: string | number; name?: string }>
        ) => {
          const uniqueMap = new Map<string, Option>();
          arr.forEach((v) => {
            const value = String(v.id);
            const label = v.name ?? String(v.id);
            uniqueMap.set(value, { value, label });
          });
          return Array.from(uniqueMap.values());
        };

        setBranchIdName(buildIdNameMap(branches));
        setProviderIdName(buildIdNameMap(providers));
        setRespIdName(buildIdNameMap(responsibles));
        setContractIdName(buildIdNameMap(contracts));
        setEmployeeIdName(buildIdNameMap(employees));

        setBranchOpts(toOptionsIdName(branches));
        setProviderOpts(toOptionsIdName(providers));
        setRespOpts(toOptionsIdName(responsibles));
        setDocTypeOpts(
          documentTypes.map((dt: string) => ({
            value: dt,
            label: docTypeMap[dt] || dt,
          }))
        );
        setContractStatusOpts(
          contractStatus.map((s: string) => ({
            value: s,
            label: contractStatusMap[s] || s,
          }))
        );
        
        setStatusOpts(
          statuses.map((s: string) => ({
            value: s,
            label: documentStatusMap[s] || s.replace(/_/g, ' ').replace(/\w\S*/g, (txt) => {
              return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
            }) || s,
          }))
        );
        
        setDocTitleOpts(toOptions(documentTitles));
        setProviderCnpjOpts(toOptions(providerCnpjs));
        setContractOpts(toOptionsIdName(contracts));
        setEmployeeOpts(toOptionsIdName(employees));
        setEmployeeCpfOpts(toOptions(employeeCpfs));
        
        setEmployeeSituationOpts(
          employeeSituations.map((s: string) => ({
            value: s,
            label: employeeSituationMap[s] || s.replace(/_/g, ' ').replace(/\w\S*/g, (txt) => {
              return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
            }) || s,
          }))
        );
        
        setDocValidityOpts(
          documentValidity.map((dv: string) => ({
            value: dv,
            label: docValidityMap[dv] || dv,
          }))
        );
        setDocBlockOpts(
          documentDoesBlock.map((v: boolean) => ({
            value: String(v),
            label: v ? "Sim" : "Não",
          }))
        );
      } catch (e) {
        console.error("Filters: Erro ao carregar /filters", e);
      }
    })();
  }, [clientId, token]);

  useEffect(() => {
    if (!clientId) {
      setRawDocStatus([]);
      setRawExemption([]);
      setRawRanking([]);
      setRawCounts({
        contractQuantity: 0,
        supplierQuantity: 0,
        allocatedEmployeeQuantity: 0,
        employeeQuantity: 0,
      });
      setChartData([]);
      setDocumentExemptionData([]);
      setTableData([]);
      setStats({
        contractQuantity: 0,
        supplierQuantity: 0,
        allocatedEmployeeQuantity: 0,
        employeeQuantity: 0,
      });
      return;
    }

    if (USE_MOCK_DATA) {
      setTimeout(() => {
        setRawDocStatus(mockDocStatusData);

        setRawExemption([
          { name: "Segurança do Trabalho", quantity: 12 },
          { name: "Saúde Ocupacional", quantity: 8 },
          { name: "Cadastro e Certidões", quantity: 5 },
        ]);
        setRawRanking([
          {
            corporateName: "Fornecedor Mock A",
            cnpj: "11.222.333/0001-44",
            adherence: 95.5,
            conformity: 98.0,
            nonConformingDocumentQuantity: 2,
            conformityLevel: "Alto",
          },
          {
            corporateName: "Fornecedor Mock B",
            cnpj: "44.555.666/0001-77",
            adherence: 80.0,
            conformity: 75.0,
            nonConformingDocumentQuantity: 15,
            conformityLevel: "Médio",
          },
        ]);
        setRawCounts({
          contractQuantity: 25,
          supplierQuantity: 15,
          allocatedEmployeeQuantity: 150,
          employeeQuantity: 200,
        });
      }, 500);
      return;
    }
    (async () => {
      try {
        const url = `${ip}/dashboard/${clientId}/general`;
        const requestBody = { clientId: clientId };
        const { data } = await axios.post(url, requestBody, {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
            Accept: "application/json",
          },
        });

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
        setRawCounts({
          contractQuantity: 0,
          supplierQuantity: 0,
          allocatedEmployeeQuantity: 0,
          employeeQuantity: 0,
        });
        setChartData([]);
        setDocumentExemptionData([]);
        setTableData([]);
        setStats({
          contractQuantity: 0,
          supplierQuantity: 0,
          allocatedEmployeeQuantity: 0,
          employeeQuantity: 0,
        });
      }
    })();
  }, [clientId, token, USE_MOCK_DATA]);

  useEffect(() => {
    const calculateTotal = () => {
      if (!rawDocStatus || rawDocStatus.length === 0) {
        return 0;
      }

      const total = rawDocStatus.reduce((acc, documentType) => {
        const quantityInType = documentType.status.reduce(
          (subAcc, s) => subAcc + s.quantity,
          0
        );
        return acc + quantityInType;
      }, 0);

      return total;
    };

    setTotalDocuments(calculateTotal());
  }, [rawDocStatus]);

  useEffect(() => {
    if (activeTab === "historico" && clientId) {
      if (USE_MOCK_DATA) {
        setIsLoadingHistory(true);
        setTimeout(() => {
          setHistoryData(mockHistoryData);
          setIsLoadingHistory(false);
        }, 500);
        return;
      }

      const fetchHistoryData = async () => {
        setIsLoadingHistory(true);
        try {
          const url = `${ip}/dashboard/${clientId}/history`;
          const requestBody = {
            ...historyPeriod,
            branchIds: applied.branchIds,
            providerIds: applied.providerIds,
            providerCnpjs: applied.providerCnpjs,
          };

          const { data } = await axios.post(url, requestBody, {
            headers: { Authorization: `Bearer ${token}` },
          });

          const formattedData = Object.entries(data).map(
            ([monthYear, values]: [string, any]) => {
              const [year, monthName] = monthYear.split("-");
              const adherence =
                values.total > 0 ? (values.adherent / values.total) * 100 : 0;
              const conformity =
                values.total > 0 ? (values.conformity / values.total) * 100 : 0;

              return {
                mes: `${monthMap[monthName]}/${year.slice(2)}`,
                Aderência: adherence,
                Conformidade: conformity,
              };
            }
          );

          setHistoryData(formattedData);
        } catch (error) {
          console.error("Erro ao buscar dados do histórico:", error);
          setHistoryData([]);
        } finally {
          setIsLoadingHistory(false);
        }
      };

      fetchHistoryData();
    }
  }, [activeTab, clientId, token, applied, historyPeriod]);

  const conformity = tableData.length > 0 ? tableData[0]?.conformity : 0;

  const adherence = tableData.length > 0 ? tableData[0]?.adherence : 0;

  function applyFilters() {
    setApplied({ ...draft });
  }

  function clearFilters() {
    const empty: FiltersState = {
      branchIds: [],
      providerIds: [],
      documentTypes: [],
      responsibleIds: [],
      activeContract: [],
      statuses: [],
      documentTitles: [],

      providerCnpjs: [],
      contractIds: [],
      employeeIds: [],
      employeeCpfs: [],
      employeeSituations: [],
      documentDoesBlock: [],
      documentValidity: [],
    };
    setDraft(empty);
    setApplied(empty);
  }

  useEffect(() => {
    const branchNames = applied.branchIds.map(
      (id) => branchIdName.get(id) ?? id
    );
    const providerNames = applied.providerIds.map(
      (id) => providerIdName.get(id) ?? id
    );
    const responsibleNames = applied.responsibleIds.map(
      (id) => respIdName.get(id) ?? id
    );
    const contractNames = applied.contractIds.map(
      (id) => contractIdName.get(id) ?? id
    );
    const employeeNames = applied.employeeIds.map(
      (id) => employeeIdName.get(id) ?? id
    );

    const allNames = {
      branchNames,
      providerNames,
      responsibleNames,
      contractNames,
      employeeNames,
    };

    const docStatusChart = filterDocumentStatus(
      rawDocStatus,
      applied,
      allNames
    );
    setChartData(docStatusChart);

    const docExFiltered = filterExemption(rawExemption, applied, allNames);
    setDocumentExemptionData(docExFiltered);

    const rankingFiltered = filterRanking(rawRanking, applied, allNames);
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
  }, [
    applied,
    rawDocStatus,
    rawExemption,
    rawRanking,
    branchIdName,
    providerIdName,
    respIdName,
    rawCounts,
    contractIdName,
    employeeIdName,
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
        allRows.push(["Colaboradores", rawCounts.employeeQuantity]);
        allRows.push([
          "Funcionários Alocados",
          rawCounts.allocatedEmployeeQuantity,
        ]);
      }
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

  const handleExport = () => {
    if (historyData.length === 0) {
      alert("Não há dados no histórico para exportar.");
      return;
    }

    const selectedBranchNames = applied.branchIds
      .map((id) => branchIdName.get(id) || id)
      .join(", ");
    const branchText = selectedBranchNames || "Todas as Filiais";
    const safeBranchName = branchText
      .replace(/[^a-z0-9]/gi, "_")
      .toLowerCase();
    const fileName = `historico_${safeBranchName}`;

    const fileType =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
    const fileExtension = ".xlsx";

    const headers = [
      ["Filial(is):", branchText],
      [],
      ["Mês", "Aderência", "Conformidade"],
    ];

    const ws = XLSX.utils.aoa_to_sheet(headers);

    XLSX.utils.sheet_add_json(ws, historyData, {
      origin: "A4",
      skipHeader: true,
    });

    const wb = { Sheets: { Histórico: ws }, SheetNames: ["Histórico"] };
    const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
    const data = new Blob([excelBuffer], { type: fileType });
    saveAs(data, fileName + fileExtension);
  };

  return (
    <>
      <Helmet title="monitoring table" />
      <section
        className="mx-5 md:mx-20 flex flex-col gap-6 pb-20"
        id="contentToCapture"
      >
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
            <button
              onClick={() => setActiveTab("historico")}
              className={`whitespace-nowrap border-b-2 px-1 py-4 text-sm font-medium ${
                activeTab === "historico"
                  ? "border-blue-600 text-blue-600"
                  : "border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700"
              }`}
            >
              Histórico
            </button>
          </nav>
        </div>

        {activeTab === "visao-geral" && (
          <div>
            <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
              <h2 className="text-lg font-semibold text-gray-800">Filtros</h2>

              <div className="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-6">
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
                  label="CNPJ do Fornecedor"
                  options={providerCnpjOpts}
                  values={draft.providerCnpjs}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, providerCnpjs: v }))
                  }
                />
                <MultiSelectDropdown
                  label="Contratos"
                  options={contractOpts}
                  values={draft.contractIds}
                  onChange={(v) => setDraft((s) => ({ ...s, contractIds: v }))}
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
                  label="Responsáveis"
                  options={respOpts}
                  values={draft.responsibleIds}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, responsibleIds: v }))
                  }
                />
                <MultiSelectDropdown
                  label="Funcionários"
                  options={employeeOpts}
                  values={draft.employeeIds}
                  onChange={(v) => setDraft((s) => ({ ...s, employeeIds: v }))}
                />

                <MultiSelectDropdown
                  label="CPF do Funcionário"
                  options={employeeCpfOpts}
                  values={draft.employeeCpfs}
                  onChange={(v) => setDraft((s) => ({ ...s, employeeCpfs: v }))}
                />
                <MultiSelectDropdown
                  label="Situação do Funcionário"
                  options={employeeSituationOpts}
                  values={draft.employeeSituations}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, employeeSituations: v }))
                  }
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
                  label="Status de Documento"
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
                <MultiSelectDropdown
                  label="Documento Bloqueia ?"
                  options={docBlockOpts}
                  values={draft.documentDoesBlock.map(String)}
                  onChange={(v) =>
                    setDraft((s) => ({
                      ...s,
                      documentDoesBlock: v.map((b) => b === "true"),
                    }))
                  }
                />
                <MultiSelectDropdown
                  label="Validade do Documento"
                  options={docValidityOpts}
                  values={draft.documentValidity}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, documentValidity: v }))
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

            <div className="grid grid-cols-1 gap-6 sm:grid-cols-5">
              <div className="rounded-lg py-6 flex justify-center">
                <ActiveContracts count={stats.contractQuantity ?? 0} />
              </div>

              <div className="rounded-lg py-6 flex justify-center">
                <Suppliers count={stats.supplierQuantity ?? 0} />
              </div>
              <div className="rounded-lg py-6 flex justify-center">
                <TotalDocumentsCard count={totalDocuments} />
              </div>

              <div className="rounded-lg py-6 flex justify-center">
                <Employees count={stats.employeeQuantity ?? 0} />
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

            <div className="overflow-x-auto mt-10 pb-10">
              {clientId && (
                <GeneralDocumentsTable clientId={clientId} filters={applied} />
              )}
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

              <div className="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-6">
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
                  label="CNPJ do Fornecedor"
                  options={providerCnpjOpts}
                  values={draft.providerCnpjs}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, providerCnpjs: v }))
                  }
                />
                <MultiSelectDropdown
                  label="Contratos"
                  options={contractOpts}
                  values={draft.contractIds}
                  onChange={(v) => setDraft((s) => ({ ...s, contractIds: v }))}
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
                  label="Responsáveis"
                  options={respOpts}
                  values={draft.responsibleIds}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, responsibleIds: v }))
                  }
                />
                <MultiSelectDropdown
                  label="Funcionários"
                  options={employeeOpts}
                  values={draft.employeeIds}
                  onChange={(v) => setDraft((s) => ({ ...s, employeeIds: v }))}
                />

                <MultiSelectDropdown
                  label="CPF do Funcionário"
                  options={employeeCpfOpts}
                  values={draft.employeeCpfs}
                  onChange={(v) => setDraft((s) => ({ ...s, employeeCpfs: v }))}
                />
                <MultiSelectDropdown
                  label="Situação do Funcionário"
                  options={employeeSituationOpts}
                  values={draft.employeeSituations}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, employeeSituations: v }))
                  }
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
                  label="Status de Documento"
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
                <MultiSelectDropdown
                  label="Documento Bloqueia ?"
                  options={docBlockOpts}
                  values={draft.documentDoesBlock.map(String)}
                  onChange={(v) =>
                    setDraft((s) => ({
                      ...s,
                      documentDoesBlock: v.map((b) => b === "true"),
                    }))
                  }
                />
                <MultiSelectDropdown
                  label="Validade do Documento"
                  options={docValidityOpts}
                  values={draft.documentValidity}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, documentValidity: v }))
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
        {activeTab === "historico" && (
          <div>
            <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
              <h2 className="text-lg font-semibold text-gray-800">Filtros</h2>

              <div className="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-6">
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
                  label="CNPJ do Fornecedor"
                  options={providerCnpjOpts}
                  values={draft.providerCnpjs}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, providerCnpjs: v }))
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
                  label="Tipo de Documento"
                  options={docTypeOpts}
                  values={draft.documentTypes}
                  onChange={(v) =>
                    setDraft((s) => ({ ...s, documentTypes: v }))
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

            <HistoryChart data={historyData} isLoading={isLoadingHistory} />

            <div className="mt-6 flex justify-end">
              <button
                onClick={handleExport}
                className="rounded-md bg-green-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2"
                disabled={isLoadingHistory || historyData.length === 0}
              >
                Exportar para Excel
              </button>
            </div>
          </div>
        )}
      </section>
    </>
  );
};