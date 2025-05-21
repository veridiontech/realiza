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

const mockData = [
  { category: 'Geral', value: 8 },
  { category: 'Segurança', value: 7 },
  { category: 'Saúde', value: 1 },
  { category: 'Trabalhista', value: 1 },
]

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

export function ExemptionPendingChart() {
  return (
    <Card className="w-[320px] ml-auto shadow-lg rounded-md border border-gray-100">
      <CardContent className="pt-6 pb-4 px-6">
        <h2 className="text-gray-900 text-lg font-semibold mb-6 select-none">
          Isenções Aguardando Aprovações
        </h2>
        <ResponsiveContainer width="100%" height={400}>
          <BarChart
            data={mockData}
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
              domain={[0, 'dataMax + 2']}
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
