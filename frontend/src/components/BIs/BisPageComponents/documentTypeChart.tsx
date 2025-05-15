import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
  LabelList
} from 'recharts'

import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
} from '@/components/ui/card'

const COLORS: Record<string, string> = {
  PENDENTE: '#facc15',
  INVALIDO: '#ef4444',
  VENCIDO: '#f97316',
  ISENTO: '#6366f1',
  OK: '#22c55e',
  VALIDACAO: '#06b6d4',
}

const MOCK_DATA = [
  {
    tipo: 'segurança',
    pendentes: 147,
    invalidos: 579,
    vencidos: 0,
    isencao: 7,
    ok: 2300,
    validacao: 3,
    total: 3036,
  },
  {
    tipo: 'saude',
    pendentes: 10,
    invalidos: 40,
    vencidos: 0,
    isencao: 0,
    ok: 399,
    validacao: 0,
    total: 449,
  },
  {
    tipo: 'geral',
    pendentes: 5,
    invalidos: 15,
    vencidos: 2,
    isencao: 1,
    ok: 460,
    validacao: 0,
    total: 483,
  },
  {
    tipo: 'trabalhista',
    pendentes: 50,
    invalidos: 10,
    vencidos: 2,
    isencao: 3,
    ok: 994,
    validacao: 0,
    total: 1059,
  },
]

const CustomTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload || !payload.length) return null

  const data: Record<string, number> = payload.reduce((acc: Record<string, number>, curr: any) => {
    acc[curr.dataKey] = curr.value
    return acc
  }, {})

  const total: number = Object.values(data).reduce((acc, val) => acc + val, 0)

  return (
    <div className="bg-zinc-800 text-white p-4 rounded shadow-md text-sm">
      <div className="font-semibold uppercase mb-1">{label}</div>
      {Object.entries(data).map(([key, value]) => (
        <div key={key} className="flex justify-between">
          <span className="capitalize">
            <span
              className="inline-block w-3 h-3 rounded-full mr-2"
              style={{ backgroundColor: COLORS[key.toUpperCase()] }}
            ></span>
            {key.charAt(0).toUpperCase() + key.slice(1)}
          </span>
          <span>
            {value} ({((value / total) * 100).toFixed(2)}%)
          </span>
        </div>
      ))}
      <div className="border-t border-zinc-700 mt-2 pt-2 flex justify-between font-semibold">
        <span>Total</span>
        <span>{total} (100%)</span>
      </div>
    </div>
  )
}

export function DocumentTypeChart() {
  return (
    <Card className="w-full h-[400px]">
      <CardHeader>
        <CardTitle className="text-base">Tipo Documentos</CardTitle>
      </CardHeader>
      <CardContent className="h-[320px]">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart
            layout="vertical"
            data={MOCK_DATA}
            margin={{ top: 10, right: 30, left: 10, bottom: 0 }}
          >
            <XAxis type="number" />
            <YAxis dataKey="tipo" type="category" />
            <Tooltip content={<CustomTooltip />} />
            <Legend verticalAlign="top" align="left" height={36} />
            <Bar dataKey="pendentes" stackId="a" fill={COLORS.PENDENTE} name="Pendentes" />
            <Bar dataKey="invalidos" stackId="a" fill={COLORS.INVALIDO} name="Inválidos" />
            <Bar dataKey="vencidos" stackId="a" fill={COLORS.VENCIDO} name="Vencidos" />
            <Bar dataKey="isencao" stackId="a" fill={COLORS.ISENTO} name="Isenção Solicitada" />
            <Bar dataKey="ok" stackId="a" fill={COLORS.OK} name="Ok">
              <LabelList dataKey="total" position="right" />
            </Bar>
            <Bar dataKey="validacao" stackId="a" fill={COLORS.VALIDACAO} name="Em Validação" />
          </BarChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  )
}