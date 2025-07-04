import { useRef, useState } from "react";
import html2canvas from "html2canvas";
import jsPDF from "jspdf";

import { StatusDocumentChart } from "./BisPageComponents/statusDocumentChart";
import { ExemptionPendingChart } from "./BisPageComponents/exemptionRankingChart";
import { ConformityRankingTable } from "./BisPageComponents/conformityRankingTable";

// Dados mock para o StatusDocumentChart
const mockStatusData = [
  { name: "meio ambiente", PENDENTE: 10, APROVADO: 5 },
  { name: "segurança do trabalho", PENDENTE: 20, REPROVADO: 5 },
  { name: "saúde", PENDENTE: 12, VENCIDO: 3 },
  { name: "ações", PENDENTE: 8, APROVADO: 10 },
];

export function BIPage() {
  const biRef = useRef<HTMLDivElement>(null);
  const [loading, setLoading] = useState(false);

  const handleGeneratePDF = async () => {
    if (!biRef.current) return;

    setLoading(true);

    try {
      const canvas = await html2canvas(biRef.current, { scale: 2 });
      const imgData = canvas.toDataURL("image/png");

      const pdf = new jsPDF("p", "mm", "a4");
      const pageWidth = pdf.internal.pageSize.getWidth();
      const pageHeight = pdf.internal.pageSize.getHeight();

      const imgProps = pdf.getImageProperties(imgData);
      const pdfWidth = pageWidth;
      const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width;

      const now = new Date();
      const formattedDate = now.toLocaleDateString("pt-BR");

      pdf.setFontSize(14);
      pdf.text("Relatório - Painel de Indicadores", 14, 15);
      pdf.setFontSize(10);
      pdf.text(`Gerado em: ${formattedDate}`, 14, 22);

      const topOffset = 30;

      if (pdfHeight + topOffset <= pageHeight) {
        pdf.addImage(imgData, "PNG", 0, topOffset, pdfWidth, pdfHeight);
      } else {
        let heightLeft = pdfHeight;
        let y = topOffset;

        while (heightLeft > 0) {
          pdf.addImage(imgData, "PNG", 0, y, pdfWidth, pdfHeight);
          heightLeft -= pageHeight - topOffset;
          if (heightLeft > 0) {
            pdf.addPage();
            y = 0;
          }
        }
      }

      pdf.save("relatorio-bi.pdf");
    } catch (error) {
      console.error("Erro ao gerar PDF:", error);
    }

    setLoading(false);
  };

  return (
    <div className="p-6">
      {/* Cabeçalho com botão */}
      <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4 mb-6">
        <h1 className="text-2xl font-bold text-slate-800">
          Painel de Indicadores
        </h1>

        <button
  onClick={handleGeneratePDF}
  disabled={loading}
  className="fixed bottom-6 right-6 z-50 bg-green-600 hover:bg-green-700 text-white px-6 py-3 rounded-full shadow-lg font-semibold transition"
>
  {loading ? "Gerando PDF..." : "Gerar PDF"}
</button>
      </div>

      {/* Conteúdo exportado para PDF */}
      <div
        ref={biRef}
        className="flex flex-col gap-10 bg-white p-6 rounded shadow-md"
      >
        <StatusDocumentChart data={mockStatusData} />
        <div className="flex gap-10 flex-col lg:flex-row">
          <ExemptionPendingChart />
          <ConformityRankingTable />
        </div>
      </div>
    </div>
  );
}
