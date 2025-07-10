import { Card, CardContent } from "@/components/ui/card";

interface ChartData {
  name: string;
  [key: string]: any;
}

interface ConformityRankingTableProps {
  data: ChartData[];
}

const getConformityColor = (level: "RISKY" | "ATTENTION" | "NORMAL" | "OK") => {
  switch (level) {
    case "RISKY":
      return "bg-red-100 text-red-800 font-semibold";
    case "ATTENTION":
      return "bg-yellow-100 text-yellow-800 font-semibold";
    case "NORMAL":
      return "bg-blue-100 text-blue-800 font-semibold";
    case "OK":
      return "bg-green-100 text-green-800 font-semibold";
    default:
      return "";
  }
};

export function ConformityRankingTable({ data }: ConformityRankingTableProps) {
  // Verifica se 'data' é um array válido
  if (!Array.isArray(data) || data.length === 0) {
    return (
      <Card className="w-full max-w-full rounded-lg shadow-md border border-gray-200">
        <CardContent className="pt-6 pb-4 px-6">
          <h2 className="text-gray-800 text-xl font-bold mb-6 select-none">
            Ranking Pendências
          </h2>
          <div className="overflow-x-auto w-full">
            <p className="text-center">Carregando dados...</p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="w-full max-w-full rounded-lg shadow-md border border-gray-200">
      <CardContent className="pt-6 pb-4 px-6">
        <h2 className="text-gray-800 text-xl font-bold mb-6 select-none">
          Ranking Pendências
        </h2>
        <div className="overflow-x-auto w-full">
          <table className="min-w-[800px] w-full text-sm text-left">
            <thead className="bg-gray-50 border-b border-gray-300">
              <tr>
                <th className="py-3 px-4 font-semibold text-gray-600 whitespace-nowrap">
                  Razão Social
                </th>
                <th className="py-3 px-4 font-semibold text-gray-600 whitespace-nowrap">
                  CNPJ
                </th>
                <th className="py-3 px-4 font-semibold text-gray-600 whitespace-nowrap text-center">
                  Aderência %
                </th>
                <th className="py-3 px-4 font-semibold text-gray-600 whitespace-nowrap text-center">
                  Conformidade %
                </th>
                <th className="py-3 px-4 font-semibold text-gray-600 whitespace-nowrap text-center">
                  Docs Não Conformes
                </th>
                <th className="py-3 px-4 font-semibold text-gray-600 whitespace-nowrap text-center">
                  Faixa de Conformidade
                </th>
              </tr>
            </thead>
            <tbody>
              {data.map((c) => (
                <tr
                  key={c.cnpj}
                  className="border-b border-gray-200 last:border-none hover:bg-gray-50 transition-colors"
                >
                  <td className="py-3 px-4 text-blue-600 hover:underline cursor-pointer whitespace-nowrap font-medium">
                    {c.name}
                  </td>
                  <td className="py-3 px-4 whitespace-nowrap">{c.cnpj}</td>
                  <td className="py-3 px-4 whitespace-nowrap text-center">
                    {c.adherence}
                  </td>
                  <td
                    className={`py-3 px-4 whitespace-nowrap text-center rounded-md ${getConformityColor(
                      c.conformityLevel
                    )}`}
                  >
                    {c.conformity}
                  </td>
                  <td className="py-3 px-4 whitespace-nowrap text-center">
                    {c.nonConformDocs}
                  </td>
                  <td
                    className={`py-3 px-4 whitespace-nowrap text-center ${getConformityColor(
                      c.conformityLevel
                    )}`}
                  >
                    {c.conformityLevel}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  );
}
