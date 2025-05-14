import { Pie, PieChart } from "recharts";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart";
import { useClient } from "@/context/Client-Provider";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useEffect, useState } from "react";
import { Blocks } from "react-loader-spinner";

export const description = "A donut chart";

export function GraphicHomeRight() {
  const { client } = useClient();
  const [branches, setBranches] = useState<any[]>([]);

  const fetchBranches = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client?.idClient}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      }
      );
      const { content } = response.data;
      setBranches(content);
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
    {
      browser: "Acre",
      visitors: branches.filter((b) => b.state === "AC").length,
      fill: "var(--color-chrome)",
    },
    {
      browser: "Alagoas",
      visitors: branches.filter((b) => b.state === "AL").length,
      fill: "var(--color-safari)",
    },
    {
      browser: "Amapá",
      visitors: branches.filter((b) => b.state === "AP").length,
      fill: "var(--color-firefox)",
    },
    {
      browser: "Amazonas",
      visitors: branches.filter((b) => b.state === "AM").length,
      fill: "var(--color-edge)",
    },
    {
      browser: "Bahia",
      visitors: branches.filter((b) => b.state === "BA").length,
      fill: "var(--color-other)",
    },
    {
      browser: "Ceará",
      visitors: branches.filter((b) => b.state === "CE").length,
      fill: "var(--color-chrome)",
    },
    {
      browser: "Distrito Federal",
      visitors: branches.filter((b) => b.state === "DF").length,
      fill: "var(--color-safari)",
    },
    {
      browser: "Espírito Santo",
      visitors: branches.filter((b) => b.state === "ES").length,
      fill: "var(--color-firefox)",
    },
    {
      browser: "Goiás",
      visitors: branches.filter((b) => b.state === "GO").length,
      fill: "var(--color-edge)",
    },
    {
      browser: "Maranhão",
      visitors: branches.filter((b) => b.state === "MA").length,
      fill: "var(--color-other)",
    },
    {
      browser: "Mato Grosso",
      visitors: branches.filter((b) => b.state === "MT").length,
      fill: "var(--color-chrome)",
    },
    {
      browser: "Mato Grosso do Sul",
      visitors: branches.filter((b) => b.state === "MS").length,
      fill: "var(--color-safari)",
    },
    {
      browser: "Minas Gerais",
      visitors: branches.filter((b) => b.state === "MG").length,
      fill: "var(--color-firefox)",
    },
    {
      browser: "Pará",
      visitors: branches.filter((b) => b.state === "PA").length,
      fill: "var(--color-edge)",
    },
    {
      browser: "Paraíba",
      visitors: branches.filter((b) => b.state === "PB").length,
      fill: "var(--color-other)",
    },
    {
      browser: "Paraná",
      visitors: branches.filter((b) => b.state === "PR").length,
      fill: "var(--color-chrome)",
    },
    {
      browser: "Pernambuco",
      visitors: branches.filter((b) => b.state === "PE").length,
      fill: "var(--color-safari)",
    },
    {
      browser: "Piauí",
      visitors: branches.filter((b) => b.state === "PI").length,
      fill: "var(--color-firefox)",
    },
    {
      browser: "Rio de Janeiro",
      visitors: branches.filter((b) => b.state === "RJ").length,
      fill: "var(--color-edge)",
    },
    {
      browser: "Rio Grande do Norte",
      visitors: branches.filter((b) => b.state === "RN").length,
      fill: "var(--color-other)",
    },
    {
      browser: "Rio Grande do Sul",
      visitors: branches.filter((b) => b.state === "RS").length,
      fill: "var(--color-chrome)",
    },
    {
      browser: "Rondônia",
      visitors: branches.filter((b) => b.state === "RO").length,
      fill: "var(--color-safari)",
    },
    {
      browser: "Roraima",
      visitors: branches.filter((b) => b.state === "RR").length,
      fill: "var(--color-firefox)",
    },
    {
      browser: "Santa Catarina",
      visitors: branches.filter((b) => b.state === "SC").length,
      fill: "var(--color-edge)",
    },
    {
      browser: "São Paulo",
      visitors: branches.filter((b) => b.state === "SP").length,
      fill: "var(--color-other)",
    },
    {
      browser: "Sergipe",
      visitors: branches.filter((b) => b.state === "SE").length,
      fill: "var(--color-chrome)",
    },
    {
      browser: "Tocantins",
      visitors: branches.filter((b) => b.state === "TO").length,
      fill: "var(--color-safari)",
    },
  ];

  if (!client?.idClient || branches.length === 0) {
    return (
      <Card className="flex flex-col gap-10">
        <CardHeader className="pb-0">
          <CardTitle>Unidades por estado</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-col items-center gap-20">
          <div className="text-muted-foreground text-center">
            Nenhum cliente selecionado ou sem dados disponíveis.
          </div>
          <Blocks
            height="80"
            width="80"
            color="#34495E"
            ariaLabel="blocks-loading"
            wrapperStyle={{}}
            wrapperClass="blocks-wrapper"
            visible={true}
          />
        </CardContent>
      </Card>
    );
  }

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
  };

  return (
    <Card className="flex flex-col">
      <CardHeader className="pb-0">
        <CardTitle>Unidades por estado</CardTitle>
      </CardHeader>
      <CardContent className="flex-1 pb-0">
        <ChartContainer
          config={chartConfig}
          className="mx-auto aspect-square max-h-[320px]"
        >
          <PieChart>
            <ChartTooltip
              cursor={false}
              content={<ChartTooltipContent hideLabel />}
            />
            <Pie
              data={chartData}
              dataKey="visitors"
              nameKey="browser"
              innerRadius={60}
            />
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
