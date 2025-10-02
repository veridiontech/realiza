import { useEffect, useState } from "react";
import axios from "axios";
import { Button } from "@/components/ui/button";
import { ip } from "@/utils/ip";

interface DocumentosDaAtividade {
  idassociation: string;
  idDocument: string;
  idActivity: string;
  documentTitle: string;
  selected: boolean;
}

interface DocumentItem {
  id: string;
  title: string;
  expirationQuantity: number;
  expirationUnit: string;
  type: string;
}

interface DocumentGroup {
  typeName: string;
  documents: DocumentItem[];
}

interface ActivityItem {
  id: string;
  title: string;
}

interface ActivityGroup {
  risk: string;
  activities: ActivityItem[];
}

interface ServiceItem {
  id: string;
  title: string;
  risk: string;
}

interface ServiceGroup {
  risk: string;
  services: ServiceItem[];
}

interface ParametrizationResponse {
  documents: DocumentGroup[];
  activities: ActivityGroup[];
  services: ServiceGroup[];
}

interface HistorySectionProps {
  idBranch: string;
}

interface AuditLogItem {
  id: string;
  action: string;
  date: string;
  userName: string;
  userResponsibleFullName: string;
}

type ParamType = "documents" | "activities" | "services";

const formatarValidade = (quantidade: number, unidade: string): string => {
  const unidadeLower = unidade.toLowerCase();

  const mapaUnidades: Record<string, string> = {
    days: "dia",
    day: "dia",
    dias: "dia",
    weeks: "semana",
    week: "semana",
    semanas: "semana",
    months: "mês",
    month: "mês",
    meses: "mês",
    years: "ano",
    year: "ano",
    anos: "ano",
  };

  const singularOuPlural = (quantidade: number): string => {
    const singular = mapaUnidades[unidadeLower] || unidadeLower;

    if (quantidade === 1) return singular;

    switch (singular) {
      case "mês":
        return "meses";
      case "ano":
        return "anos";
      case "dia":
        return "dias";
      case "semana":
        return "semanas";
      default:
        return singular + "s";
    }
  };

  const unidadeFormatada = singularOuPlural(quantidade);
  return `${quantidade} ${unidadeFormatada}`;
};

const formatarRisco = (valor: string): string => {
  const mapa: Record<string, string> = {
    LOW: "Baixo",
    MEDIUM: "Médio",
    HIGH: "Alto",
    VERY_HIGH: "Muito alto",
    CRITICAL: "Crítico",
  };

  return mapa[valor] || valor;
};

const formatarDataHora = (isoDateString: string | undefined): string => {
  if (!isoDateString) return "—";
  try {
    const date = new Date(isoDateString);
    if (isNaN(date.getTime())) return "—";

    const options: Intl.DateTimeFormatOptions = {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
      hour12: false,
    };

    return date.toLocaleString("pt-BR", options);
  } catch (e) {
    console.error("Erro ao formatar data:", e);
    return "—";
  }
};

const formatarAcao = (action: string): string => {
  const mapaAcoes: Record<string, string> = {
    CREATE: "Criação",
    UPDATE: "Atualização",
    DELETE: "Exclusão",
    UPLOAD: "Upload",
    FINISH: "Finalização",
    APPROVE: "Aprovação",
    REJECT: "Rejeição",
    EXEMPT: "Isenção",
    ALLOCATE: "Alocação",
    DEALLOCATE: "Desalocação",
    STATUS_CHANGE: "Mudança de Status",
    ACTIVATE: "Ativação",
    LOGIN: "Login",
    LOGOUT: "Logout",
    SUSPEND: "Suspensão",
    ALL: "Todas as Ações",
  };

  return mapaAcoes[action.toUpperCase()] || action;
};

export function HistorySection({ idBranch }: HistorySectionProps) {
  const [filterDate, setFilterDate] = useState("");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("desc");
  const [parametrizationData, setParametrizationData] =
    useState<ParametrizationResponse | null>(null);
  const [historyData, setHistoryData] = useState<AuditLogItem[]>([]);

  const [isLoading, setIsLoading] = useState(false);
  const [filterUser, setFilterUser] = useState("");
  const [filterAction] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const [selectedParamType, setSelectedParamType] =
    useState<ParamType>("activities");

  const [selectedActivityId, setSelectedActivityId] = useState<string | null>(
    null
  );
  const [activityDocuments, setActivityDocuments] = useState<
    DocumentosDaAtividade[]
  >([]);
  const [isActivityDocsLoading, setIsActivityDocsLoading] = useState(false);

  useEffect(() => {
    if (!idBranch) return;
    fetchParametrization();
  }, [idBranch]);

  useEffect(() => {
    fetchAuditLog();
  }, [sortOrder]);

  const fetchParametrization = async () => {
    const token = localStorage.getItem("tokenClient");
    if (!token || !idBranch) return;

    setIsLoading(true);
    try {
      const res = await axios.get(`${ip}/branch/control-panel/${idBranch}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setParametrizationData(res.data);
      console.log("res", res.data);
    } catch (err) {
      console.error("Erro ao buscar parametrização:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchAuditLog = async () => {
    const token = localStorage.getItem("tokenClient");
    if (!token || !idBranch) return;

    console.log("Iniciando requisição de logs de auditoria...");
    console.log("Filtros atuais:", {
      auditLogTypeEnum: "BRANCH",
      id: idBranch,
      direction: sortOrder.toUpperCase(),
      page: 0,
      size: 10,
      action: filterAction || undefined,
      userName: filterUser || undefined,
      startDate: startDate || undefined,
      endDate: endDate || undefined,
    });

    try {
      const res = await axios.get(`${ip}/audit-log`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        params: {
          auditLogTypeEnum: "BRANCH",
          id: idBranch,
          direction: sortOrder.toUpperCase(),
          page: 0,
          size: 10,
          action: filterAction || undefined,
          userName: filterUser || undefined,
          startDate: startDate || undefined,
          endDate: endDate || undefined,
        },
      });

      console.log("Dados de logs de auditoria recebidos com sucesso:", res.data);

      setHistoryData(res.data.content || []);
    } catch (err) {
      console.error("Erro ao buscar histórico de auditoria:", err);
    }
  };

  const toggleSortOrder = () => {
    setSortOrder((prev) => (prev === "desc" ? "asc" : "desc"));
  };

  // Removemos a lógica de filtragem local. A filtragem deve ocorrer na API (fetchAuditLog)
  const sortedHistoryData = historyData;

  const fetchActivityDocuments = async (idActivity: string) => {
    if (selectedActivityId === idActivity) {
      setSelectedActivityId(null);
      setActivityDocuments([]);
      return;
    }

    setSelectedActivityId(idActivity);
    setActivityDocuments([]);
    setIsActivityDocsLoading(true);
    const token = localStorage.getItem("tokenClient");
    if (!token) {
      setIsActivityDocsLoading(false);
      return;
    }

    try {
      const res = await axios.get(
        `${ip}/contract/activity/find-document-by-activity/${idActivity}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setActivityDocuments(res.data || []);
      console.log(`Documentos da Atividade ${idActivity}:`, res.data);
    } catch (err) {
      console.error(
        `Erro ao buscar documentos da atividade ${idActivity}:`,
        err
      );
      setActivityDocuments([]);
    } finally {
      setIsActivityDocsLoading(false);
    }
  };

  const renderActivityDocuments = () => {
    if (isActivityDocsLoading) {
      return (
        <p className="text-blue-500 italic p-2 text-center">
          Carregando documentos...
        </p>
      );
    }

    const associatedDocuments = activityDocuments.filter(doc => doc.selected);

    if (associatedDocuments.length === 0) {
      return (
        <p className="text-gray-600 italic p-2 text-center">
          Nenhum documento **associado** encontrado para esta atividade.
        </p>
      );
    }

    return (
      <div className="mt-2 ml-4 p-3 bg-white border border-blue-200 rounded-lg shadow-inner">
        <h4 className="text-md font-bold mb-2 text-blue-800">
          Documentos Associados:
        </h4>
        <ul className="list-disc list-inside space-y-1 text-sm text-gray-700">
          {associatedDocuments.map((doc, idx) => (
            <li key={idx} className="flex items-center">
              <span
                className="w-2 h-2 mr-2 rounded-full bg-green-500"
                title="Documento Selecionado"
              ></span>
              <span className="font-medium">
                {doc.documentTitle}
              </span>
            </li>
          ))}
        </ul>
      </div>
    );
  };

  const renderDocuments = () => {
    if (
      !parametrizationData?.documents ||
      parametrizationData.documents.length === 0
    ) {
      return (
        <p className="text-gray-600 italic text-center p-4">
          Nenhum documento encontrado.
        </p>
      );
    }
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {parametrizationData.documents.map((docGroup, i) => (
          <div
            key={i}
            className="bg-gray-50 p-4 rounded-md border border-gray-100"
          >
            <p className="font-semibold text-realizaBlue text-lg mb-2">
              {docGroup.typeName}
            </p>
            <ul className="list-none space-y-2 text-sm text-gray-700">
              {docGroup.documents.map((doc, idx) => (
                <li key={idx} className="flex items-start">
                  <span className="text-green-500 mr-2">●</span>
                  <div>
                    <strong className="block">{doc.title}</strong>
                    {doc.type && (
                      <span className="text-gray-500 block">
                        Tipo: {doc.type}
                      </span>
                    )}
                    {doc.expirationQuantity > 0 && doc.expirationUnit && (
                      <span className="text-red-500 font-medium block">
                        Validade de:{" "}
                        {formatarValidade(
                          doc.expirationQuantity,
                          doc.expirationUnit
                        )}
                      </span>
                    )}
                  </div>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>
    );
  };

  const renderActivities = () => {
    if (
      !parametrizationData?.activities ||
      parametrizationData.activities.length === 0
    ) {
      return (
        <p className="text-gray-600 italic text-center p-4">
          Nenhuma atividade encontrada.
        </p>
      );
    }
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {parametrizationData.activities.map((group, i) => (
          <div
            key={i}
            className="bg-gray-50 p-4 rounded-md border border-gray-100"
          >
            <p className="font-semibold text-gray-800 text-lg mb-2">
              Risco:{" "}
              <span
                className={`font-bold ${
                  group.risk === "LOW"
                    ? "text-green-600"
                    : group.risk === "MEDIUM"
                    ? "text-yellow-600"
                    : "text-red-600"
                }`}
              >
                {formatarRisco(group.risk)}
              </span>
            </p>
            <ul className="list-none space-y-2 text-sm text-gray-700">
              {group.activities.map((act, idx) => (
                <li
                  key={idx}
                  className={`items-start p-2 rounded-md transition duration-150 ease-in-out cursor-pointer hover:bg-gray-200 ${
                    selectedActivityId === act.id ? "bg-blue-100 border border-blue-300" : ""
                  }`}
                  onClick={() => fetchActivityDocuments(act.id)}
                >
                  <div className="flex items-start">
                    <span className="text-blue-500 mr-2">
                      {selectedActivityId === act.id ? "▼" : "▶"}
                    </span>{" "}
                    <strong>{act.title}</strong>
                  </div>
                  {selectedActivityId === act.id && renderActivityDocuments()}
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>
    );
  };

  const renderServices = () => {
    if (
      !parametrizationData?.services ||
      parametrizationData.services.length === 0
    ) {
      return (
        <p className="text-gray-600 italic text-center p-4">
          Nenhum serviço encontrado.
        </p>
      );
    }
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {parametrizationData.services.map((group, i) => (
          <div
            key={i}
            className="bg-gray-50 p-4 rounded-md border border-gray-100"
          >
            <p className="font-semibold text-gray-800 text-lg mb-2">
              Risco:{" "}
              <span
                className={`font-bold ${
                  group.risk === "LOW"
                    ? "text-green-600"
                    : group.risk === "MEDIUM"
                    ? "text-yellow-600"
                    : "text-red-600"
                }`}
              >
                {formatarRisco(group.risk)}
              </span>
            </p>
            <ul className="list-none space-y-2 text-sm text-gray-700">
              {group.services.map((svc, idx) => (
                <li key={idx} className="flex items-start">
                  <span className="text-teal-500 mr-2">●</span>{" "}
                  <strong>{svc.title}</strong>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>
    );
  };

  const renderSelectedParametrization = () => {
    if (isLoading)
      return (
        <p className="text-gray-600 italic">Carregando parametrizações...</p>
      );
    if (!parametrizationData)
      return <p className="text-red-600">⚠️ Nenhuma parametrização carregada.</p>;

    switch (selectedParamType) {
      case "documents":
        return renderDocuments();
      case "activities":
        return renderActivities();
      case "services":
        return renderServices();
      default:
        return null;
    }
  };

  return (
    <div className="relative bottom-[8vw]">
      <div className="bg-white shadow-lg rounded-xl p-8 border border-gray-200">
        <h2 className="text-3xl font-extrabold mb-8 text-gray-900 flex items-center">
          <span className="mr-3 text-blue-700">📘</span> Detalhes do Histórico e
          Parametrização
        </h2>

        <div className="flex flex-col md:flex-row md:flex-wrap gap-4 mb-6 p-4 bg-gray-50 rounded-md border border-gray-100">
          <div className="flex flex-col">
            <label className="text-gray-700 font-medium mb-1">
              Data exata
            </label>
            <input
              type="date"
              value={filterDate}
              onChange={(e) => {
                const dateValue = e.target.value;
                setFilterDate(dateValue);
                // Preenche Início e Fim para a mesma data para simular a "data exata" na API
                setStartDate(dateValue);
                setEndDate(dateValue);
              }}
              className="border border-gray-300 rounded-lg px-4 py-2 text-sm"
            />
          </div>

          <div className="flex flex-col">
            <label className="text-gray-700 font-medium mb-1">Início</label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => {
                setStartDate(e.target.value);
                setFilterDate(""); // Limpa Data Exata ao usar intervalo
              }}
              className="border border-gray-300 rounded-lg px-4 py-2 text-sm"
            />
          </div>

          <div className="flex flex-col">
            <label className="text-gray-700 font-medium mb-1">Fim</label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => {
                setEndDate(e.target.value);
                setFilterDate(""); // Limpa Data Exata ao usar intervalo
              }}
              className="border border-gray-300 rounded-lg px-4 py-2 text-sm"
            />
          </div>

          <div className="flex flex-col">
            <label className="text-gray-700 font-medium mb-1">Usuário</label>
            <input
              type="text"
              value={filterUser}
              onChange={(e) => setFilterUser(e.target.value)}
              placeholder="Nome do usuário"
              className="border border-gray-300 rounded-lg px-4 py-2 text-sm"
            />
          </div>

          <div className="flex flex-col justify-end gap-2 md:ml-auto">
            <Button
              className="bg-realizaBlue hover:bg-realizaBlue-dark text-white font-semibold px-4 py-2 rounded-lg"
              onClick={fetchAuditLog}
            >
              🔍 Filtrar
            </Button>
            <Button
              className="bg-gray-200 text-gray-700 hover:bg-gray-300 font-semibold px-4 py-2 rounded-lg"
              onClick={toggleSortOrder}
            >
              Ordenar por -{" "}
              {sortOrder === "desc" ? "Mais recente" : "Mais antigo"}
            </Button>
          </div>
        </div>

        <div className="overflow-x-auto mb-8">
          <table className="min-w-full table-auto border border-gray-300 rounded-lg overflow-hidden">
            <thead className="bg-gradient-to-r from-blue-600 to-blue-800 text-white text-sm uppercase tracking-wider">
              <tr>
                <th className="px-6 py-3 text-left font-semibold">Data/Hora</th>
                <th className="px-6 py-3 text-left font-semibold">Ação</th>
                <th className="px-6 py-3 text-left font-semibold">Usuário</th>
              </tr>
            </thead>
            <tbody>
              {sortedHistoryData.length === 0 ? (
                <tr>
                  <td
                    colSpan={3}
                    className="text-center px-4 py-6 text-gray-500 italic bg-gray-50"
                  >
                    Nenhum registro encontrado para os filtros aplicados.
                  </td>
                </tr>
              ) : (
                sortedHistoryData.map((item) => (
                  <tr
                    key={item.id}
                    className="text-sm border-b border-gray-200 last:border-b-0 hover:bg-gray-50 transition duration-150 ease-in-out"
                  >
                    <td className="px-6 py-3 font-medium text-gray-700">
                      {formatarDataHora(item.date)}
                    </td>
                    <td className="px-6 py-3 text-blue-600 font-semibold">
                      {formatarAcao(item.action)}
                    </td>
                    <td className="px-6 py-3 text-gray-700">
                      {item.userResponsibleFullName || "Usuário Desconhecido"}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <hr className="my-10 border-gray-300" />
        <h2 className="text-3xl font-extrabold mb-6 text-gray-900 flex items-center">
          <span className="mr-3 text-purple-700">⚙️</span> Parametrização do
          Sistema
        </h2>

        <div className="flex space-x-4 mb-8 justify-center">
          <Button
            onClick={() => setSelectedParamType("documents")}
            className={`px-6 py-3 rounded-lg font-semibold transition duration-200 ease-in-out ${
              selectedParamType === "documents"
                ? "bg-indigo-600 text-white shadow-md"
                : "bg-gray-200 text-gray-700 hover:bg-gray-300"
            }`}
          >
            📄 Documentos
          </Button>
          <Button
            onClick={() => setSelectedParamType("activities")}
            className={`px-6 py-3 rounded-lg font-semibold transition duration-200 ease-in-out ${
              selectedParamType === "activities"
                ? "bg-orange-600 text-white shadow-md"
                : "bg-gray-200 text-gray-700 hover:bg-gray-300"
            }`}
          >
            🛠️ Atividades
          </Button>
          <Button
            onClick={() => setSelectedParamType("services")}
            className={`px-6 py-3 rounded-lg font-semibold transition duration-200 ease-in-out ${
              selectedParamType === "services"
                ? "bg-purple-600 text-white shadow-md"
                : "bg-gray-200 text-gray-700 hover:bg-gray-300"
            }`}
          >
            🧰 Serviços
          </Button>
        </div>

        {renderSelectedParametrization()}
      </div>
    </div>
  );
}