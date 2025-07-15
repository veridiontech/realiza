import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  LabelList,
  ResponsiveContainer,
} from 'recharts'
import { Card, CardContent } from '@/components/ui/card'

const COLORS = {
  barFill: '#60a5fa', // azul pastel moderno
  labelFill: '#1e293b', // cinza escuro para texto
  axisTick: '#475569', // cinza médio para ticks
  gridStroke: '#e2e8f0', // grid suave
}

const CustomTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload || !payload.length) return null

  return (
    <div className="bg-white shadow-md rounded p-3 text-gray-800 text-sm">
      <p className="font-semibold mb-1">{label}</p>
      <p>{`Pendências: ${payload[0].value}`}</p>
    </div>
  )
}

export function ExemptionPendingChart({ data }: any) {
  // Formatar os dados para o gráfico
  const chartData = data.map((item: any) => ({
    category: item.name,  // Usando 'name' para categoria no eixo X
    value: item.quantity  // Usando 'quantity' como valor no gráfico
  }));

  return (
    <Card className="w-[400px] ml-auto shadow-lg rounded-md border border-gray-100">
      <CardContent className="pt-6 pb-4 px-2">
        <h2 className="text-gray-900 text-lg font-semibold mb-6 select-none">
          Isenções Aguardando Aprovações
        </h2>
        <ResponsiveContainer width="100%" height={400}>
          <BarChart
            data={chartData} // Passando os dados para o gráfico
            margin={{ top: 10, right: 20, left: 10, bottom: 40 }}
            barSize={36}
            barGap={24}
          >
            <CartesianGrid strokeDasharray="4 4" stroke={COLORS.gridStroke} vertical={false} />
            <XAxis
              dataKey="category"  // Usando 'category' para o eixo X
              tick={{ fill: COLORS.axisTick, fontWeight: '600', fontSize: 14 }}
              dy={12}
              tickLine={false}
              axisLine={false}
            />
            <YAxis
              domain={[0, 'dataMax + 10']}  // Define o intervalo do eixo Y
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
  )
}

