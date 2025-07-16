import * as XLSX from "xlsx";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts';
import { FileDown } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';

// Interface genérica para os dados, já que as chaves são dinâmicas
export interface ChartData {
  name: string;
  [key: string]: any; // Permite chaves como PENDENTE, APROVADO, etc.
}

interface StatusDocumentChartProps {
  data: ChartData[];
}

// Mapeamento de cores para os status conhecidos.
const STATUS_COLORS: Record<string, string> = {
  PENDENTE: '#fde68a',
  REPROVADO_IA: '#fca5a5',
  VENCIDO: '#fbbf24',
  ISENCAO: '#a5b4fc',
  APROVADO: '#86efac',
  APROVADO_IA: '#7dd3fc',
};

export function StatusDocumentChart({ data }: StatusDocumentChartProps) {
  // Se não tiver dados, mostra o loading
  if (!data || data.length === 0) {
    return (
      <Card className="w-full h-[400px]">
        <CardHeader>
          <CardTitle className="text-base">Status de Documentos</CardTitle>
        </CardHeader>
        <CardContent className="flex items-center justify-center h-full">
          <p>Carregando dados...</p>
        </CardContent>
      </Card>
    );
  }

  // Função para exportar os dados do gráfico em Excel
  const exportToExcel = () => {
    const wb = XLSX.utils.book_new();
    const ws = XLSX.utils.json_to_sheet(data);
    XLSX.utils.book_append_sheet(wb, ws, "StatusDocument");
    XLSX.writeFile(wb, "status_document.xlsx");
  };

  // Descobre dinamicamente todas as chaves de status
  const statusKeys = data.reduce((acc, cur) => {
    Object.keys(cur).forEach((k) => {
      if (k !== "name") acc.add(k);
    });
    return acc;
  }, new Set<string>());

  return (
    <Card className="relative w-full h-[400px]">
      <CardHeader>
        <CardTitle className="text-base">Status de Documentos</CardTitle>
      </CardHeader>

      <button
        onClick={exportToExcel}
        className="absolute top-2 right-2 p-1 text-green-500 text-xs rounded-full"
      >
        <FileDown width={20}/>
      </button>

      <CardContent className="h-[320px]">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart
            data={data}
            margin={{ top: 20, right: 30, left: 20, bottom: 40 }}
          >
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis dataKey="name" tick={{ fontSize: 14 }} />
            <YAxis />
            <Tooltip
              contentStyle={{
                backgroundColor: 'rgba(30, 41, 59, 0.9)',
                borderColor: '#334155',
                color: '#ffffff',
                borderRadius: '0.5rem',
              }}
              cursor={{ fill: 'rgba(100, 116, 139, 0.1)' }}
            />
            <Legend
              verticalAlign="bottom"
              align="center"
              height={40}
              iconSize={14}
              iconType="circle"
              wrapperStyle={{ paddingTop: 30 }}
            />

            {Array.from(statusKeys).map((key) => (
              <Bar
                key={key}
                dataKey={key}
                stackId="a"
                name={key.replace('_', ' ').toLowerCase()}
                fill={STATUS_COLORS[key] || '#cccccc'}
              />
            ))}
          </BarChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  );
}
