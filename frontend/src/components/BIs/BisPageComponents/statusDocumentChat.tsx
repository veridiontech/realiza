import {
    BarChart,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Bar,
    ResponsiveContainer,
  } from 'recharts'
  import {
    Card,
    CardHeader,
    CardTitle,
    CardContent,
  } from '@/components/ui/card'
  import { useEffect, useState } from 'react'
  import axios from 'axios'
  import { ip } from '@/utils/ip'
  import { Blocks } from 'react-loader-spinner'
  import { useClient } from '@/context/Client-Provider'
  
  const CustomTooltip = ({ active, payload }: any) => {
    if (active && payload && payload.length) {
      const { status, quantidade, percentual } = payload[0].payload
      return (
        <div className="rounded bg-gray-800 p-2 text-white shadow-md text-sm">
          <p><strong>status:</strong> {status}</p>
          <p>Quantidade: <strong>{quantidade.toLocaleString()}</strong></p>
          <p>Porcentagem: <strong>{percentual.toFixed(2)}%</strong></p>
        </div>
      )
    }
    return null
  }
  
  export function StatusDocumentChart() {
    const { client } = useClient()
    const [data, setData] = useState<any[]>([])
    const [loading, setLoading] = useState(true)
  
    useEffect(() => {
      const fetchData = async () => {
        try {
          const res = await axios.get(`${ip}/documents/status?clientId=${client?.idClient}`)
          if (res.data && Array.isArray(res.data) && res.data.length > 0) {
            setData(res.data)
          } else {
            // fallback mock
            setData([
              { status: 'Pendentes', quantidade: 450, percentual: 10.04 },
              { status: 'Inválidos', quantidade: 823, percentual: 18.38 },
              { status: 'Vencidos', quantidade: 11, percentual: 0.22 },
              { status: 'Isenção Solicitada', quantidade: 0, percentual: 0.0 },
              { status: 'Em Validação', quantidade: 3, percentual: 0.06 },
              { status: 'Ok', quantidade: 3494, percentual: 71.29 },
              { status: 'Total', quantidade: 4901, percentual: 100.0 },
            ])
          }
        } catch (err) {
          console.error('Erro ao buscar status de documentos:', err)
          // fallback mock
          setData([
            { status: 'Pendentes', quantidade: 450, percentual: 10.04 },
            { status: 'Inválidos', quantidade: 823, percentual: 18.38 },
            { status: 'Vencidos', quantidade: 11, percentual: 0.22 },
            { status: 'Isenção Solicitada', quantidade: 0, percentual: 0.0 },
            { status: 'Em Validação', quantidade: 3, percentual: 0.06 },
            { status: 'Ok', quantidade: 3494, percentual: 71.29 },
            { status: 'Total', quantidade: 4901, percentual: 100.0 },
          ])
        } finally {
          setLoading(false)
        }
      }
  
      if (client?.idClient) {
        fetchData()
      }
    }, [client?.idClient])
  
    if (loading) {
      return (
        <Card className="w-full h-[300px] flex items-center justify-center">
          <Blocks height={60} width={60} color="#2563eb" visible />
        </Card>
      )
    }
  
    return (
      <Card className="w-full">
        <CardHeader>
          <CardTitle>Status de Documentos</CardTitle>
          <div className="text-sm text-muted-foreground flex gap-4">
            <div className="flex items-center gap-1">
              <span className="w-3 h-3 bg-blue-500 rounded-full inline-block"></span>
              Quantidade
            </div>
          </div>
        </CardHeader>
        <CardContent className="h-[300px]">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={data} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="status" tick={{ fontSize: 12 }} />
              <YAxis yAxisId="left" orientation="left" />
              <Tooltip content={<CustomTooltip />} />
              <Bar yAxisId="left" dataKey="quantidade" fill="#2563eb" barSize={30} radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>
    )
  }
  