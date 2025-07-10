import { Helmet } from "react-helmet-async";
import {
  StatusDocumentChart,
  ChartData,
} from "@/components/BIs/BisPageComponents/statusDocumentChart";
import { ExemptionPendingChart } from "@/components/BIs/BisPageComponents/exemptionRankingChart";
import { ConformityRankingTable } from "@/components/BIs/BisPageComponents/conformityRankingTable";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState } from "react";

export const MonittoringBis = () => {
  const { selectedBranch } = useBranch();
  const [chartData, setChartData] = useState<ChartData[]>([]);
  const [tableData, setTableData] = useState<any[]>([]);
  const [documentExemptionData, setDocumentExemptionData] = useState<any[]>([]);
  const token = localStorage.getItem("tokenClient");

  useEffect(() => {
    if (!selectedBranch?.idBranch) return;

    const getData = async () => {
      try {
        const { data } = await axios.get(
          `${ip}/dashboard/${selectedBranch.idBranch}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        console.log(data);

        setDocumentExemptionData(data.documentExemption);

        const formattedChart: ChartData[] = data.documentStatus.map(
          (cat: any) => {
            const row: any = { name: cat.name };
            cat.status.forEach((s: any) => {
              row[s.type] = s.quantity;
            });
            return row;
          }
        );
        setChartData(formattedChart);

        const formattedTable = data.pendingRanking.map((r: any) => ({
          name: r.corporateName, 
          cnpj: r.cnpj, 
          adherence: r.adherence, 
          conformity: r.conformity, 
          nonConformDocs: r.nonConformingDocumentQuantity, 
          conformityLevel: r.conformityLevel, 
        }));
        setTableData(formattedTable);
      } catch (err) {
        console.error(err);
      }
    };

    getData();
  }, [selectedBranch?.idBranch, token]);

  return (
    <>
      <Helmet title="monitoring table" />
      <section className="mx-5 md:mx-20 flex flex-col gap-12 pb-20">
        <div className="overflow-x-auto mt-10 pb-10">
          <StatusDocumentChart data={chartData} />
        </div>
        <div className="overflow-x-auto mt-10">
          <div className="flex flex-col md:flex-row gap-6 md:gap-8 min-w-[320px] md:min-w-full">
            <div className="flex-shrink-0 w-full md:w-[400px]">
              <ExemptionPendingChart data={documentExemptionData} />
            </div>
            <div className="flex-grow min-w-[320px]">
              <ConformityRankingTable data={tableData} />
            </div>
          </div>
        </div>
      </section>
    </>
  );
};
