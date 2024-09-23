import { Bar, BarChart, CartesianGrid, XAxis, YAxis } from 'recharts'

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from '@/components/ui/chart'

export const description = 'A bar chart'

const chartData = [
  { month: 'January', desktop: 18600 },
  { month: 'February', desktop: 30005 },
  { month: 'March', desktop: 23700 },
  { month: 'April', desktop: 73000 },
  { month: 'May', desktop: 20009 },
  { month: 'June', desktop: 21400 },
  { month: 'July', desktop: 88000 },
  { month: 'August', desktop: 58000 },
  { month: 'September', desktop: 40000 },
  { month: 'October', desktop: 60000 },
  { month: 'November', desktop: 70000 },
  { month: 'December', desktop: 20000 },
]

const chartConfig = {
  desktop: {
    label: 'R$',
    color: 'hsl(var(--chart-1))',
  },
} satisfies ChartConfig

export function BarChartMonitoring() {
  return (
    <Card className="flex h-[500px] w-full flex-col items-center">
      <CardHeader className="flex flex-row gap-[1000px]">
        <CardTitle>Receita total representação gráfica</CardTitle>
        <CardDescription>atualizado em 2024</CardDescription>
      </CardHeader>
      <CardContent>
        <ChartContainer config={chartConfig} className="h-[420px] w-[1500px]">
          <BarChart
            data={chartData}
            width={10} // Definindo a largura
            height={40} // Definindo a altura
          >
            <CartesianGrid vertical={false} />
            {/* Definindo o eixo Y com metas */}
            <YAxis
              type="number"
              domain={[10000, 100000]} // Limite de 10mil a 100mil
              tickCount={10} // Definindo 10 marcas (ou personalizar)
              tickFormatter={(value) => `${value / 1000}K`} // Exibir valores em milhar
            />
            <XAxis
              dataKey="month"
              tickLine={false}
              tickMargin={10}
              axisLine={false}
              tickFormatter={(value) => value.slice(0, 3)}
            />
            <ChartTooltip
              cursor={false}
              content={<ChartTooltipContent hideLabel />}
            />
            <Bar dataKey="desktop" fill="#2563EB" radius={8} />
          </BarChart>
        </ChartContainer>
      </CardContent>
    </Card>
  )
}
