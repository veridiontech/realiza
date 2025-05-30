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
import axios from 'axios'
import { useEffect, useState } from 'react'
import { ip } from '@/utils/ip'
import { useBranch } from '@/context/Branch-provider'

export function ConformityGaugeChart() {
  const { selectedBranch } = useBranch()
  const [percentage, setPercentage] = useState<number | null>(null)
  const [loading, setLoading] = useState(true)

  const token = localStorage.getItem("tokenClient")

  useEffect(() => {
    const fetchConformity = async () => {
      try {
        const res = await axios.get(
          `${ip}/conformity?branchId=${selectedBranch?.idBranch}&clientId=${selectedBranch?.client}`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
        )
        const conformityValue = res.data?.percentage ?? 0
        console.log(conformityValue)
        setPercentage(conformityValue)
      } catch (err) {
        console.error('Erro ao buscar conformidade:', err)
        setPercentage(0)
      } finally {
        setLoading(false)
      }
    }

    if (selectedBranch?.idBranch && selectedBranch.client) {
      fetchConformity()
    }
  }, [selectedBranch?.idBranch, selectedBranch?.client])

  const chartData = [
    {
      name: 'Conformidade',
      value: percentage ?? 0,
      fill:
        percentage! >= 75
          ? '#22c55e'
          : percentage! >= 50
            ? '#eab308'
            : '#ef4444',
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
    <Card className="w-full h-96"> 
      <CardHeader className="pb-0">
        <CardTitle className="text-muted-foreground text-base">Conformidade</CardTitle>
        <CardDescription className="text-xl font-bold">
          {percentage?.toFixed(2)}%
        </CardDescription>
      </CardHeader>
      <CardContent className="h-full flex items-center justify-center relative">
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
