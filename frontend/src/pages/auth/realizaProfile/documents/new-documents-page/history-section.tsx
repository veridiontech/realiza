import { useEffect, useState } from "react";
import axios from "axios";
import { Button } from "@/components/ui/button";
import { ip } from "@/utils/ip";

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
      case "mês": return "meses";
      case "ano": return "anos";
      case "dia": return "dias";
      case "semana": return "semanas";
      default: return singular + "s";
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

export function HistorySection({ idBranch }: HistorySectionProps) {
  const [filterDate, setFilterDate] = useState("");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("desc");
  const [parametrizationData, setParametrizationData] =
    useState<ParametrizationResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [selectedParamType, setSelectedParamType] =
    useState<ParamType>("documents");

  const historyData = [
    { date: "2025-06-20", action: "Alteração na configuração de serviços", user: "Admin" },
    { date: "2025-06-18", action: "Adição de novo perfil de permissão", user: "João" },
    { date: "2025-06-15", action: "Atualização do cadastro de documentos", user: "Maria" },
  ];

  useEffect(() => {
    if (!idBranch) return;
    fetchParametrization();
  }, [idBranch]);

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
    } catch (err) {
      console.error("Erro ao buscar parametrização:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const toggleSortOrder = () => {
    setSortOrder((prev) => (prev === "desc" ? "asc" : "desc"));
  };

  const filteredHistoryData = filterDate
    ? historyData.filter((item) => item.date.includes(filterDate))
    : historyData;

  const sortedHistoryData = [...filteredHistoryData].sort((a, b) =>
    sortOrder === "desc"
      ? new Date(b.date).getTime() - new Date(a.date).getTime()
      : new Date(a.date).getTime() - new Date(b.date).getTime()
  );

  const renderDocuments = () => {
    if (!parametrizationData?.documents || parametrizationData.documents.length === 0) {
      return (
        <p className="text-gray-600 italic text-center p-4">
          Nenhum documento encontrado.
        </p>
      );
    }
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {parametrizationData.documents.map((docGroup, i) => (
          <div key={i} className="bg-gray-50 p-4 rounded-md border border-gray-100">
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
                      <span className="text-gray-500 block">Tipo: {doc.type}</span>
                    )}
                    {doc.expirationQuantity && doc.expirationUnit && (
                      <span className="text-red-500 font-medium block">
                        Validade de: {formatarValidade(doc.expirationQuantity, doc.expirationUnit)}
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
    if (!parametrizationData?.activities || parametrizationData.activities.length === 0) {
      return (
        <p className="text-gray-600 italic text-center p-4">
          Nenhuma atividade encontrada.
        </p>
      );
    }
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {parametrizationData.activities.map((group, i) => (
          <div key={i} className="bg-gray-50 p-4 rounded-md border border-gray-100">
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
                <li key={idx} className="flex items-start">
                  <span className="text-blue-500 mr-2">●</span>{" "}
                  <strong>{act.title}</strong>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>
    );
  };

  const renderServices = () => {
    if (!parametrizationData?.services || parametrizationData.services.length === 0) {
      return (
        <p className="text-gray-600 italic text-center p-4">
          Nenhum serviço encontrado.
        </p>
      );
    }
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {parametrizationData.services.map((group, i) => (
          <div key={i} className="bg-gray-50 p-4 rounded-md border border-gray-100">
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
      return <p className="text-gray-600 italic">Carregando parametrizações...</p>;
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
          <span className="mr-3 text-blue-700">📘</span> Detalhes do Histórico e Parametrização
        </h2>

        <div className="flex flex-col md:flex-row md:items-center md:gap-4 mb-6 p-4 bg-gray-50 rounded-md border border-gray-100">
          <label htmlFor="filter-date" className="text-gray-700 font-medium mr-2">
            Filtrar por Data:
          </label>
          <input
            id="filter-date"
            type="date"
            value={filterDate}
            onChange={(e) => setFilterDate(e.target.value)}
            className="border border-gray-300 rounded-lg px-4 py-2 text-sm focus:ring-2 focus:ring-realizaBlue focus:border-transparent transition duration-200 ease-in-out w-full md:w-auto"
          />
          <Button
            className="bg-realizaBlue hover:bg-realizaBlue-dark text-white font-semibold py-2 px-4 rounded-lg shadow-sm transition duration-200 ease-in-out mt-3 md:mt-0"
            onClick={toggleSortOrder}
          >
            Ordenar por - {sortOrder === "desc" ? "Mais recente" : "Mais antigo"}
          </Button>
        </div>

        <div className="overflow-x-auto mb-8">
          <table className="min-w-full table-auto border border-gray-300 rounded-lg overflow-hidden">
            <thead className="bg-gradient-to-r from-blue-600 to-blue-800 text-white text-sm uppercase tracking-wider">
              <tr>
                <th className="px-6 py-3 text-left font-semibold">Data</th>
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
                    Nenhum registro encontrado para a data selecionada.
                  </td>
                </tr>
              ) : (
                sortedHistoryData.map((item, index) => (
                  <tr
                    key={index}
                    className="text-sm border-b border-gray-200 last:border-b-0 hover:bg-gray-50 transition duration-150 ease-in-out"
                  >
                    <td className="px-6 py-3">{item.date}</td>
                    <td className="px-6 py-3">{item.action}</td>
                    <td className="px-6 py-3">{item.user}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <hr className="my-10 border-gray-300" />
        <h2 className="text-3xl font-extrabold mb-6 text-gray-900 flex items-center">
          <span className="mr-3 text-purple-700">⚙️</span> Parametrização do Sistema
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
