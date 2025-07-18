import { Helmet } from "react-helmet-async";
import {
  StatusDocumentChart,
  ChartData,
} from "@/components/BIs/BisPageComponents/statusDocumentChart";
import { ExemptionPendingChart } from "@/components/BIs/BisPageComponents/exemptionRankingChart";
import { ConformityRankingTable } from "@/components/BIs/BisPageComponents/conformityRankingTable";
import { AllocatedEmployees } from "@/components/BIs/BisPageComponents/AllocatedEmployees";
import { ActiveContracts } from "@/components/BIs/BisPageComponents/activeContracts";
import { Suppliers } from "@/components/BIs/BisPageComponents/suppliersCard";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";
import { useEffect, useState } from "react";
import { jsPDF } from "jspdf";
import html2canvas from "html2canvas";

export const MonittoringBis = () => {
  const { client } = useClient();
  const clientId = client?.idClient;
  const [chartData, setChartData] = useState<ChartData[]>([]);
  const [tableData, setTableData] = useState<any[]>([]);
  const [documentExemptionData, setDocumentExemptionData] = useState<any[]>([]);
  const token = localStorage.getItem("tokenClient");

  const [stats, setStats] = useState({
    contractQuantity: 0,
    supplierQuantity: 0,
    allocatedEmployeeQuantity: 0,
  });

  useEffect(() => {
    if (!clientId) return;

    const getData = async () => {
      try {
        const url = `${ip}/dashboard/${clientId}/general`;
        const { data } = await axios.get(url, {
          headers: { Authorization: `Bearer ${token}` },
        });

        const {
          documentExemption = [],
          documentStatus = [],
          pendingRanking = [],
          contractQuantity = 0,
          supplierQuantity = 0,
          allocatedEmployeeQuantity = 0,
        } = data;

        setDocumentExemptionData(documentExemption);

        const formattedChart: ChartData[] = documentStatus.map((cat: any) => {
          const row: any = { name: cat.name };
          cat.status.forEach((s: any) => {
            row[s.status] = s.quantity;
          });
          return row;
        });
        setChartData(formattedChart);

        const formattedTable = pendingRanking.map((r: any) => ({
          name: r.corporateName,
          cnpj: r.cnpj,
          adherence: r.adherence,
          conformity: r.conformity,
          nonConformDocs: r.nonConformingDocumentQuantity,
          conformityLevel: r.conformityLevel,
        }));
        setTableData(formattedTable);
        setStats({
          contractQuantity,
          supplierQuantity,
          allocatedEmployeeQuantity,
        });
      } catch (err) {
        console.error(err);
      }
    };

    getData();
  }, [clientId, token]);

  const generatePDF = () => {
    const content = document.getElementById("contentToCapture");
    if (!content) return;

    html2canvas(content).then((canvas) => {
      const imgData = canvas.toDataURL("image/png");
      const doc = new jsPDF();
      doc.addImage(imgData, "PNG", 10, 10, 180, 160);
      doc.save("graficos_completos.pdf");
    });
  };

  return (
    <>
      <Helmet title="monitoring table" />
      <section
        className="mx-5 md:mx-20 flex flex-col gap-12 pb-20"
        id="contentToCapture"
      >
        <div className="overflow-x-auto mt-10 pb-10">
          <StatusDocumentChart data={chartData} />
        </div>
        <div className="overflow-x-auto mt-10">
          <div className="flex flex-col md:flex-row gap-6 md:gap-8 min-w-[320px] md:min-w-full">
            <div className="flex-shrink-0 w-full md:w-[400px]">
              <ExemptionPendingChart data={documentExemptionData} />
            </div>
            <div className="flex-grow min-w-[320px] overflow-x-auto">
              <ConformityRankingTable data={tableData} />

              <div className="mt-6 flex flex-grow min-w-[800px]">
                <ActiveContracts count={stats.contractQuantity ?? 0} />
                <Suppliers count={stats.supplierQuantity ?? 0} />
                <AllocatedEmployees
                  count={stats.allocatedEmployeeQuantity ?? 0}
                />
              </div>
            </div>
          </div>
        </div>
        <button
          onClick={generatePDF}
          className="mt-4 px-4 py-2 bg-blue-500 text-white rounded"
        >
          Gerar PDF com Gráficos
        </button>
      </section>
    </>
  );
};
