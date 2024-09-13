import { CartesianGrid, Line, LineChart, XAxis } from 'recharts'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from '@/components/ui/chart'
export const description = 'A line chart with dots'
const chartData = [
  { month: 'Jan', desktop: 186, mobile: 80 },
  { month: 'Fev', desktop: 305, mobile: 200 },
  { month: 'Mar', desktop: 237, mobile: 120 },
  { month: 'Abr', desktop: 73, mobile: 190 },
  { month: 'Mai', desktop: 209, mobile: 130 },
  { month: 'Jun', desktop: 214, mobile: 140 },
  { month: 'Jul', desktop: 204, mobile: 120 },
  { month: 'Ago', desktop: 184, mobile: 110 },
  { month: 'Set', desktop: 74, mobile: 150 },
  { month: 'Out', desktop: 124, mobile: 140 },
]
const chartConfig = {
  desktop: {
    label: 'Desktop',
    color: 'hsl(var(--chart-1))',
  },
  mobile: {
    label: 'Mobile',
    color: 'hsl(var(--chart-2))',
  },
} satisfies ChartConfig
export function GraphicHomeLeft() {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Fornecedores ativos</CardTitle>
      </CardHeader>
      <CardContent>
        <ChartContainer config={chartConfig}>
          <LineChart
            accessibilityLayer
            data={chartData}
            margin={{
              left: 12,
              right: 12,
            }}
          >
            <CartesianGrid vertical={false} />
            <XAxis
              dataKey="month"
              tickLine={false}
              axisLine={false}
              tickMargin={8}
              tickFormatter={(value) => value.slice(0, 3)}
            />
            <ChartTooltip
              cursor={false}
              content={<ChartTooltipContent hideLabel />}
            />
            <Line
              dataKey="desktop"
              type="natural"
              stroke="var(--color-desktop)"
              strokeWidth={2}
              dot={{
                fill: 'var(--color-desktop)',
              }}
              activeDot={{
                r: 6,
              }}
            />
          </LineChart>
        </ChartContainer>
      </CardContent>
    </Card>
  )
}
