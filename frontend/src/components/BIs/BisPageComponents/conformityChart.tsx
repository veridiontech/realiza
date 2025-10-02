import {
  RadialBarChart,
  RadialBar,
  ResponsiveContainer,
  PolarAngleAxis,
  Tooltip,
} from 'recharts'
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from '@/components/ui/card'
import { Blocks } from 'react-loader-spinner'

// ALTERAÇÃO: Adicionadas as props count e total
interface ConformityGaugeChartProps {
  percentage?: number
  loading?: boolean
  title?: string
  count?: number
  total?: number
}

export function ConformityGaugeChart({
  percentage,
  loading = false,
  title = 'Conformidade',
  count, // ALTERAÇÃO: Nova prop
  total, // ALTERAÇÃO: Nova prop
}: ConformityGaugeChartProps) {
  const chartData = [
    {
      name: title,
      value: percentage ?? 0,
      fill:
        (percentage ?? 0) >= 75
          ? '#22c55e' // Verde
          : (percentage ?? 0) >= 50
            ? '#eab308' // Amarelo
            : '#ef4444', // Vermelho
    },
  ]

  if (loading) {
    return (
      <Card className="w-full h-80 flex justify-center items-center">
        <Blocks height="60" width="60" color="#3B82F6" visible />
      </Card>
    )
  }

  return (
    <Card className="w-full h-full">
      {/* ALTERAÇÃO: O CardHeader agora exibe a contagem em CardDescription */}
      <CardHeader className="pb-0">
        <CardTitle className="text-muted-foreground text-base">
          {title}
        </CardTitle>
        {/* Renderiza a contagem apenas se os valores forem fornecidos */}
        {count !== undefined && total !== undefined && (
          <CardDescription className="text-sm">
            {count} de {total} documentos
          </CardDescription>
        )}
      </CardHeader>

      <CardContent className="h-full flex items-center justify-center relative -mt-4">
        <ResponsiveContainer width="100%" height="100%">
          <RadialBarChart
            innerRadius="80%"
            outerRadius="100%"
            barSize={16}
            data={chartData}
            startAngle={180}
            endAngle={0}
          >
            <PolarAngleAxis
              type="number"
              domain={[0, 100]}
              angleAxisId={0}
              tick={false}
            />
            <RadialBar background dataKey="value" cornerRadius={8} />
            <Tooltip
              cursor={false}
              content={({ payload }) => {
                const val = payload?.[0]?.value
                return val !== undefined ? (
                  <div className="bg-white p-2 shadow rounded text-sm">
                    {Number(val).toFixed(2)}%
                  </div>
                ) : null
              }}
            />
          </RadialBarChart>
        </ResponsiveContainer>
        <div className="absolute text-2xl font-bold text-muted-foreground">
          {percentage?.toFixed(2)}%
        </div>
      </CardContent>
    </Card>
  )
}