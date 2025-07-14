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
import { jsPDF } from "jspdf";
import html2canvas from "html2canvas";

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
        // console.log(data);

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

  const generatePDF = () => {
    // Seleciona a div que contém os gráficos e a tabela
    const content = document.getElementById("contentToCapture");

    if (content) {
      // Usando html2canvas para capturar a imagem da tela

            console.log('Capturando o conteúdo...', content)

      html2canvas(content).then((canvas) => {
        const imgData = canvas.toDataURL("image/png");
        
        console.log('Canvas gerado:', canvas)

        // Criando o PDF a partir da captura da tela
        const doc = new jsPDF();
        doc.addImage(imgData, "PNG", 10, 10, 180, 160); // Adiciona a imagem capturada ao PDF
        doc.save("graficos_completos.pdf"); // Salva o PDF
                console.log('PDF gerado!')

      });
    } else {
      console.log('Nenhum conteúdo encontrado para capturar.')
    }

  };

  return (
    <>
      <Helmet title="monitoring table" />
      <section className="mx-5 md:mx-20 flex flex-col gap-12 pb-20" id="contentToCapture">
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
