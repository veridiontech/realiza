import { Pie, PieChart } from "recharts";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { ChartConfig, ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart";
import { useClient } from "@/context/Client-Provider";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useEffect, useState } from "react";

export const description = "A donut chart";

export function GraphicHomeRight() {
  const { client } = useClient();
  const [branches, setBranches] = useState<any[]>([]);

  const fetchBranches = async () => {
    try {
      const response = await axios.get(`${ip}/branch/filtered-client?idSearch=${client?.idClient}`);
      const { content } = response.data;
      console.log("dados: ",content);
      
      setBranches(content); // Armazena os dados recebidos
    } catch (err) {
      console.error("Erro ao buscar filiais:", err);
    }
  };

  useEffect(() => {
    if (client?.idClient) {
      fetchBranches();
    }
  }, [client?.idClient]);

  // Transformar os dados da API em um formato adequado para o gráfico
  const chartData = [
    { browser: "Rio de janeiro", visitors: branches.filter(b => b.state === "RJ").length, fill: "var(--color-chrome)" },
    { browser: "São Paulo", visitors: branches.filter(b => b.state === "SP").length, fill: "var(--color-safari)" },
    { browser: "Bahia", visitors: branches.filter(b => b.state === "Bahia").length, fill: "var(--color-firefox)" },
    { browser: "Pernambuco", visitors: branches.filter(b => b.state === "Pernambuco").length, fill: "var(--color-edge)" },
    { browser: "Acre", visitors: branches.filter(b => b.state === "Acre").length, fill: "var(--color-other)" },
  ];

  const chartConfig = {
    visitors: {
      label: "Visitors",
    },
    chrome: {
      label: "Chrome",
      color: "hsl(var(--chart-1))",
    },
    safari: {
      label: "Safari",
      color: "hsl(var(--chart-2))",
    },
    firefox: {
      label: "Firefox",
      color: "hsl(var(--chart-3))",
    },
    edge: {
      label: "Edge",
      color: "hsl(var(--chart-4))",
    },
    other: {
      label: "Other",
      color: "hsl(var(--chart-5))",
    },
  } satisfies ChartConfig;

  return (
    <Card className="flex flex-col">
      <CardHeader className="pb-0">
        <CardTitle>Unidades por estado</CardTitle>
      </CardHeader>
      <CardContent className="flex-1 pb-0">
        <ChartContainer config={chartConfig} className="mx-auto aspect-square max-h-[320px]">
          <PieChart>
            <ChartTooltip cursor={false} content={<ChartTooltipContent hideLabel />} />
            <Pie data={chartData} dataKey="visitors" nameKey="browser" innerRadius={60} />
          </PieChart>
        </ChartContainer>
      </CardContent>
      <CardFooter className="flex-col gap-2 text-sm">
        <div className="text-muted-foreground leading-none">
          Para ver mais, acesse o painel geral
        </div>
      </CardFooter>
    </Card>
  );
}
