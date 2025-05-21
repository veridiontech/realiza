import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
  LabelList,
  CartesianGrid,
} from 'recharts'

import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
} from '@/components/ui/card'

const COLORS: Record<string, string> = {
  PENDENTES: '#fde68a',   // amarelo pastel
  INVALIDOS: '#fca5a5',   // vermelho pastel
  VENCIDOS: '#fbbf24',    // laranja pastel
  ISENCAO: '#a5b4fc',     // azul pastel
  OK: '#86efac',          // verde pastel
  VALIDACAO: '#7dd3fc',   // verde-água pastel
}

const MOCK_DATA = [
  {
    tipo: 'segurança',
    pendentes: 650,
    invalidos: 120,
    vencidos: 340,
    isencao: 80,
    ok: 170,
    validacao: 780,
    total: 2140,
  },
  {
    tipo: 'saude',
    pendentes: 1000,
    invalidos: 230,
    vencidos: 540,
    isencao: 860,
    ok: 36,
    validacao: 96,
    total: 2762,
  },
  {
    tipo: 'geral',
    pendentes: 25,
    invalidos: 54,
    vencidos: 178,
    isencao: 872,
    ok: 632,
    validacao: 1000,
    total: 2761,
  },
  {
    tipo: 'trabalhista',
    pendentes: 142,
    invalidos: 94,
    vencidos: 367,
    isencao: 239,
    ok: 992,
    validacao: 42,
    total: 1876,
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
        <div key={key} className="flex justify-between items-center">
          <span className="capitalize flex items-center gap-2">
            <span
              className="inline-block w-3 h-3 rounded-full"
              style={{ backgroundColor: COLORS[key.toUpperCase()] || '#ccc', marginRight: 6 }}
            ></span>
            {key.charAt(0).toUpperCase() + key.slice(1)}
          </span>
          <span>
            ({((value / total) * 100).toFixed(1)}%)
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

export function StatusDocumentChart() {
  return (
    <Card className="w-full h-[400px]">
      <CardHeader>
        <CardTitle className="text-base">Status de Documentos</CardTitle>
      </CardHeader>
      <CardContent className="h-[320px]">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart
            data={MOCK_DATA}
            margin={{ top: 20, right: 30, left: 20, bottom: 40 }}
          >
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis dataKey="tipo" tick={{ fontSize: 14 }} />
            <YAxis />
            <Tooltip content={<CustomTooltip />} />
            <Legend
              verticalAlign="bottom"
              align="center"
              height={40}
              iconSize={20}
              iconType="circle"
              wrapperStyle={{ marginTop: 50 }}
            />
            <Bar dataKey="pendentes" fill={COLORS.PENDENTES} name="Pendentes" >
              <LabelList dataKey="pendentes" position="top" />
            </Bar>
            <Bar dataKey="invalidos" fill={COLORS.INVALIDOS} name="Inválidos" >
              <LabelList dataKey="invalidos" position="top" />
            </Bar>
            <Bar dataKey="vencidos" fill={COLORS.VENCIDOS} name="Vencidos" >
              <LabelList dataKey="vencidos" position="top" />
            </Bar>
            <Bar dataKey="isencao" fill={COLORS.ISENCAO} name="Isenção Solicitada" >
              <LabelList dataKey="isencao" position="top" />
            </Bar>
            <Bar dataKey="ok" fill={COLORS.OK} name="Ok" >
              <LabelList dataKey="ok" position="top" />
            </Bar>
            <Bar dataKey="validacao" fill={COLORS.VALIDACAO} name="Em Validação" >
              <LabelList dataKey="validacao" position="top" />
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  )
}
