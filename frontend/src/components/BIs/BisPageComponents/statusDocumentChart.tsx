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
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';

// Interface genérica para os dados, já que as chaves são dinâmicas
interface ChartData {
  name: string;
  [key: string]: any; // Permite chaves como PENDENTE, APROVADO, etc.
}

interface StatusDocumentChartProps {
  data: ChartData[];
}

// Mapeamento de cores para os status conhecidos.
// O gráfico usará uma cor padrão se um status novo aparecer.
const STATUS_COLORS: Record<string, string> = {
  PENDENTE: '#fde68a',     // Amarelo
  REPROVADO: '#fca5a5', // Vermelho
  VENCIDO: '#fbbf24',      // Laranja
  ISENCAO: '#a5b4fc',      // Azul
  APROVADO: '#86efac',      // Verde
  APROVADO_IA: '#7dd3fc',  // Verde-água
};

export function StatusDocumentChart({ data }: StatusDocumentChartProps) {
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

  // Calcula todas as chaves de status únicas dos dados (ex: PENDENTE, APROVADO, etc.)
  // para criar as barras dinamicamente.
  const statusKeys = data.reduce((acc, current) => {
    Object.keys(current).forEach((key) => {
      if (key !== 'name') {
        acc.add(key);
      }
    });
    return acc;
  }, new Set<string>());

  return (
    <Card className="w-full h-[400px]">
      <CardHeader>
        <CardTitle className="text-base">Status de Documentos</CardTitle>
      </CardHeader>
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

            {/* Gera uma <Bar> para cada tipo de status dinamicamente */}
            {Array.from(statusKeys).map((key) => (
              <Bar
                key={key}
                dataKey={key}
                // A propriedade 'stackId' é o que faz as barras serem empilhadas
                stackId="a"
                name={key.replace('_', ' ').toLowerCase()} // Formata o nome para a legenda
                fill={STATUS_COLORS[key] || '#cccccc'} // Usa a cor mapeada ou uma cor padrão
              />
            ))}
          </BarChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  );
}