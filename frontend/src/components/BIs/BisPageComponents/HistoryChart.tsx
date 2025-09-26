
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';

type ChartData = {
  month: string;
  Aderência: number;
  Conformidade: number;
};

type HistoryChartProps = {
  data: ChartData[];
  isLoading: boolean;
};

export function HistoryChart({ data, isLoading }: HistoryChartProps) {
  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-full min-h-[400px] text-gray-500">
        Carregando dados do histórico...
      </div>
    );
  }

  if (!data || data.length === 0) {
    return (
      <div className="flex justify-center items-center h-full min-h-[400px] text-gray-500">
        Nenhum dado de histórico encontrado para o período ou filtros selecionados.
      </div>
    );
  }
  
  return (
    <div className="w-full h-[400px] mt-8 rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
      <h3 className="text-lg font-semibold text-gray-800 mb-4">Histórico de Aderência e Conformidade</h3>
      <ResponsiveContainer width="100%" height="100%" >
        <LineChart
          data={data}
          margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="month" />
          <YAxis
            tickFormatter={(tick) => `${tick}%`}
            domain={[0, 100]}
          />
          <Tooltip formatter={(value: number) => `${value.toFixed(2)}%`} />
          <Legend />
          <Line
            type="monotone"
            dataKey="Aderência"
            stroke="#3b82f6" // Azul
            strokeWidth={2}
            activeDot={{ r: 8 }}
          />
          <Line
            type="monotone"
            dataKey="Conformidade"
            stroke="#10b981" // Verde
            strokeWidth={2}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}