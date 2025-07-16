import * as XLSX from 'xlsx';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  LabelList,
  ResponsiveContainer,
} from 'recharts';
import { FileDown } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';

const COLORS = {
  barFill: '#60a5fa',
  labelFill: '#1e293b',
  axisTick: '#475569',
  gridStroke: '#e2e8f0',
};

const COLORS = {
  gridStroke: '#e0e0e0',  // Cor da grade
  axisTick: '#9e9e9e',    // Cor dos ticks do eixo
  barFill: '#4f97ff',     // Cor da barra do gráfico
  labelFill: '#000000'    // Cor do texto das labels
}

const CustomTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload || !payload.length) return null;
  return (
    <div className="bg-white shadow-md rounded p-3 text-gray-800 text-sm">
      <p className="font-semibold mb-1">{label}</p>
      <p>{`Pendências: ${payload[0].value}`}</p>
    </div>
  );
};

export function ExemptionPendingChart({ data }: any) {
  // Formatar os dados para o gráfico
  const chartData = data.map((item: any) => ({
    category: item.name,
    value: item.quantity,
  }));

  // Função para exportar os dados do gráfico em Excel
  const exportToExcel = () => {
    // Se quiser renomear colunas na planilha:
    const sheetData = chartData.map(d => ({
      Categoria: d.category,
      Pendências: d.value,
    }));

    const wb = XLSX.utils.book_new();
    const ws = XLSX.utils.json_to_sheet(sheetData);
    XLSX.utils.book_append_sheet(wb, ws, 'ExemptionPending');
    XLSX.writeFile(wb, 'exemption_pending.xlsx');
  };

  return (
    <Card className="relative w-[400px] ml-auto shadow-lg rounded-md border border-gray-100">
      {/* Botão no canto superior direito */}
      <button
        onClick={exportToExcel}
        className="absolute top-2 right-2 p-1 text-green-500 text-xs rounded-full"
      >
        <FileDown width={20}/>
      </button>

      <CardContent className="pt-6 pb-4 px-2">
        <h2 className="text-gray-900 text-lg font-semibold mb-6 select-none">
          Isenções Aguardando Aprovações
        </h2>
        <ResponsiveContainer width="100%" height={400}>
          <BarChart
            data={chartData}
            margin={{ top: 10, right: 20, left: 10, bottom: 40 }}
            barSize={36}
            barGap={24}
          >
            <CartesianGrid strokeDasharray="4 4" stroke={COLORS.gridStroke} vertical={false} />
            <XAxis
              dataKey="category"
              tick={{ fill: COLORS.axisTick, fontWeight: '600', fontSize: 14 }}
              dy={12}
              tickLine={false}
              axisLine={false}
            />
            <YAxis
              domain={[0, 'dataMax + 10']}
              tick={{ fill: COLORS.axisTick, fontWeight: '600' }}
              tickCount={6}
              tickLine={false}
              axisLine={false}
            />
            <Tooltip content={<CustomTooltip />} cursor={{ fill: 'rgba(96, 165, 250, 0.15)' }} />
            <Bar dataKey="value" fill={COLORS.barFill} radius={[8, 8, 0, 0]}>
              <LabelList dataKey="value" position="top" fill={COLORS.labelFill} fontWeight="600" fontSize={16} />
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  );
}
