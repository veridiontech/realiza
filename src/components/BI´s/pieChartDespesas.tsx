import * as React from 'react'
import { Label, Pie, PieChart, Tooltip } from 'recharts'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

export const description = 'A donut chart with text'

const monthlyData = [
  { expense: 'Manutenção e Reparos', value: 500, fill: '#FF6384' },
  { expense: 'Publicidade e Promoção', value: 300, fill: '#36A2EB' },
  { expense: 'Salários e Benefícios', value: 1200, fill: '#FFCE56' },
  { expense: 'Aluguel e Utilidades', value: 800, fill: '#4BC0C0' },
  { expense: 'Impostos e Taxas', value: 400, fill: '#9966FF' },
  { expense: 'Custos do Gás', value: 150, fill: '#FF9F40' },
  { expense: 'Seguros', value: 250, fill: '#FF6384' },
  { expense: 'Transporte', value: 300, fill: '#36A2EB' },
]

const yearlyData = [
  { expense: 'Manutenção e Reparos', value: 6000, fill: '#FF6384' },
  { expense: 'Publicidade e Promoção', value: 3600, fill: '#36A2EB' },
  { expense: 'Salários e Benefícios', value: 14400, fill: '#FFCE56' },
  { expense: 'Aluguel e Utilidades', value: 9600, fill: '#4BC0C0' },
  { expense: 'Impostos e Taxas', value: 4800, fill: '#9966FF' },
  { expense: 'Custos do Gás', value: 1800, fill: '#FF9F40' },
  { expense: 'Seguros', value: 3000, fill: '#FF6384' },
  { expense: 'Transporte', value: 3600, fill: '#36A2EB' },
]

export function PieChartDespesas() {
  const [isYearly, setIsYearly] = React.useState(false)

  const chartData = isYearly ? yearlyData : monthlyData

  const totalValue = React.useMemo(() => {
    return chartData.reduce((acc, curr) => acc + curr.value, 0)
  }, [chartData])

  return (
    <Card className="flex flex-col shadow-custom-blue">
      <CardHeader className="items-start pb-0">
        <CardTitle>Distribuição de despesas</CardTitle>
      </CardHeader>
      <CardContent className="flex-1 pb-0">
        <div className="flex border border-none">
          <div className="flex flex-col items-center">
            <PieChart width={450} height={550}>
              <Tooltip />
              <Pie
                data={chartData}
                dataKey="value"
                nameKey="expense"
                innerRadius={150}
                outerRadius={200}
                strokeWidth={5}
              >
                <Label
                  content={({ viewBox }) => {
                    if (viewBox && 'cx' in viewBox && 'cy' in viewBox) {
                      return (
                        <text
                          x={viewBox.cx}
                          y={viewBox.cy}
                          textAnchor="middle"
                          dominantBaseline="middle"
                        >
                          <tspan
                            x={viewBox.cx}
                            y={viewBox.cy}
                            className="fill-foreground text-3xl font-bold"
                          >
                            {totalValue.toLocaleString()}
                          </tspan>
                          <tspan
                            x={viewBox.cx}
                            y={(viewBox.cy || 0) + 24}
                            className="fill-muted-foreground"
                          >
                            {isYearly ? 'Anual' : 'Mensal'}
                          </tspan>
                        </text>
                      )
                    }
                  }}
                />
              </Pie>
            </PieChart>
            <div className="mb-4 flex items-center justify-between">
              <button
                onClick={() => setIsYearly((prev) => !prev)}
                className="w-[9vw] rounded border"
              >
                Alternar para {isYearly ? 'Mensal' : 'Anual'}
              </button>
            </div>
          </div>
          <ul className="ml-8 flex flex-col justify-center space-y-2">
            {chartData.map((item, index) => (
              <li key={index} className="flex items-center space-x-2">
                <span
                  className="h-3 w-3 rounded-full"
                  style={{ backgroundColor: item.fill }}
                ></span>
                <span>{item.expense}</span>
              </li>
            ))}
          </ul>
        </div>
      </CardContent>
    </Card>
  )
}
