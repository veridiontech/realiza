import { Area, AreaChart, CartesianGrid, XAxis } from 'recharts'

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

export const description = 'A simple area chart'

const chartData = [
  { month: 'January', desktop: 4006 },
  { month: 'February', desktop: 12305 },
  { month: 'March', desktop: 6000 },
  { month: 'April', desktop: 1073 },
  { month: 'May', desktop: 2209 },
  { month: 'June', desktop: 10214 },
]

const chartConfig = {
  desktop: {
    label: 'Desktop',
    color: 'green',
  },
} satisfies ChartConfig

export function ProfitChart() {
  return (
    <Card className="h-[320px] w-[1048px]">
      <CardHeader>
        <CardTitle className="text-green-500">Lucro</CardTitle>
        <CardDescription>Resultado obtido em 2024</CardDescription>
      </CardHeader>
      <CardContent>
        <ChartContainer config={chartConfig} className="h-[220px] w-[1000px]">
          <AreaChart accessibilityLayer data={chartData}>
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
              content={<ChartTooltipContent indicator="line" />}
            />
            <Area
              dataKey="desktop"
              type="natural"
              fill="var(--color-desktop)"
              fillOpacity={0.2}
              stroke="var(--color-desktop)"
            />
          </AreaChart>
        </ChartContainer>
      </CardContent>
    </Card>
  )
}
