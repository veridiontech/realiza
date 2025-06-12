import { Helmet } from "react-helmet-async";

import { StatusDocumentChart } from "@/components/BIs/BisPageComponents/statusDocumentChart";
import { ExemptionPendingChart } from "@/components/BIs/BisPageComponents/exemptionRankingChart";
import { ConformityRankingTable } from "@/components/BIs/BisPageComponents/conformityRankingTable";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState } from "react";

export const MonittoringBis = () => {
  const {selectedBranch} = useBranch()
  const [datasChart, setDatasChart] = useState([])

  const token = localStorage.getItem("tokenClient")

const getDataChart = async () => {
  try {
    const res = await axios.get(`${ip}/dashboard/${selectedBranch?.idBranch}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    const formattedData = res.data.documentStatus.map((category: any) => {
      // Começa com o nome da categoria
      const chartRow: { [key: string]: string | number } = {
        name: category.name,
      };

      // Adiciona cada status como uma nova chave no objeto
      // Ex: { name: 'meio ambiente', PENDENTE: 8, APROVADO: 4 }
      category.status.forEach((statusItem: any) => {
        chartRow[statusItem.type] = statusItem.quantity;
      });

      return chartRow;
    });

    console.log('Dados formatados para o novo gráfico:', formattedData);
    setDatasChart(formattedData);
  } catch (err: any) {
    console.log(err);
  }
};

  useEffect(() => {
    if(selectedBranch?.idBranch) {
      getDataChart()
    }
  }, [])

  return (
    <>
      <Helmet title="monitoring table" />
      <section className="mx-5 md:mx-20 flex flex-col gap-12 pb-20">
        <div className="overflow-x-auto mt-10 pb-10">
          <StatusDocumentChart data={datasChart}/>
        </div>
        <div className="overflow-x-auto mt-10">
          <div className="flex flex-col md:flex-row gap-6 md:gap-8 min-w-[320px] md:min-w-full">
            <div className="flex-shrink-0 w-full md:w-[300px]">
              <ExemptionPendingChart />
            </div>
            <div className="flex-grow min-w-[320px]">
              <ConformityRankingTable />
            </div>
          </div>
        </div>
      </section>
    </>
  );
};
