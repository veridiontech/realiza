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
  const token = localStorage.getItem("tokenClient");

  const [chartData, setChartData] = useState<ChartData[]>([]);
  const [tableData, setTableData] = useState<any[]>([]);
  const [documentExemptionData, setDocumentExemptionData] = useState<any[]>([]);
  const [stats, setStats] = useState({
    contractQuantity: 0,
    supplierQuantity: 0,
    allocatedEmployeeQuantity: 0,
  });

  const [filters, setFilters] = useState({
    branchIds: [],
    providerIds: [],
    documentTypes: [],
    responsibleIds: [],
    activeContract: [],
    statuses: [],
    documentTitles: [],
  });

  const [selectedFilters, setSelectedFilters] = useState({
    branchId: "",
    providerId: "",
    documentType: "",
    responsibleId: "",
    activeContract: "",
    status: "",
    documentTitle: "",
  });

  useEffect(() => {
    if (!clientId) return;

    const fetchFilterData = async () => {
      try {
        const url = `${ip}/dashboard/${clientId}/general`;
        const { data } = await axios.get(url, {
          headers: { Authorization: `Bearer ${token}` },
        });

        console.log("Filtros recebidos:", data);
        setFilters({
          branchIds: data.branchIds || [],
          providerIds: data.providerIds || [],
          documentTypes: data.documentTypes || [],
          responsibleIds: data.responsibleIds || [],
          activeContract: data.activeContract || [],
          statuses: data.statuses || [],
          documentTitles: data.documentTitles || [],
        });
      } catch (err) {
        console.error("Error fetching filter data:", err);
      }
    };

    fetchFilterData();
  }, [clientId, token]);

  const handleFilterChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const { name, value } = e.target;
    setSelectedFilters((prevFilters) => ({
      ...prevFilters,
      [name]: value,
    }));
  };

  useEffect(() => {
    if (!clientId) return;

    const fetchFilteredData = async () => {
      try {
        const url = `${ip}/dashboard/${clientId}/general`;
        const { data } = await axios.get(url, {
          headers: { Authorization: `Bearer ${token}` },
          params: {
            branchIds: selectedFilters.branchId
              ? [selectedFilters.branchId]
              : [],
            providerIds: selectedFilters.providerId
              ? [selectedFilters.providerId]
              : [],
            documentTypes: selectedFilters.documentType
              ? [selectedFilters.documentType]
              : [],
            responsibleIds: selectedFilters.responsibleId
              ? [selectedFilters.responsibleId]
              : [],
            activeContract: selectedFilters.activeContract
              ? [selectedFilters.activeContract]
              : [],
            statuses: selectedFilters.status ? [selectedFilters.status] : [],
            documentTitles: selectedFilters.documentTitle
              ? [selectedFilters.documentTitle]
              : [],
          },
        });

        const {
          documentExemption = [],
          documentStatus = [],
          pendingRanking = [],
          contractQuantity = 0,
          supplierQuantity = 0,
          allocatedEmployeeQuantity = 0,
        } = data;

        // Atualizando os estados com os novos dados
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
        console.error("Error fetching filtered data:", err);
      }
    };

    fetchFilteredData();
  }, [clientId, token, selectedFilters]);

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
        <div className="flex flex-wrap gap-2">
          <div className="w-full md:w-1/2 lg:w-1/4">
            <select
              name="branchId"
              value={selectedFilters.branchId}
              onChange={handleFilterChange}
              className="w-full border border-gray-300 rounded-md p-2"
            >
              <option value="">Selecione Unidade</option>
              {filters.branchIds.map((branch) => (
                <option key={branch} value={branch}>
                  {branch}
                </option>
              ))}
            </select>
          </div>

          <div className="w-full md:w-1/2 lg:w-1/4">
            <select
              name="providerId"
              value={selectedFilters.providerId}
              onChange={handleFilterChange}
              className="w-full border border-gray-300 rounded-md p-2"
            >
              <option value="">Selecione Fornecedor</option>
              {filters.providerIds.map((provider) => (
                <option key={provider} value={provider}>
                  {provider}
                </option>
              ))}
            </select>
          </div>

          <div className="w-full md:w-1/2 lg:w-1/4">
            <select
              name="documentType"
              value={selectedFilters.documentType}
              onChange={handleFilterChange}
              className="w-full border border-gray-300 rounded-md p-2"
            >
              <option value="">Selecione Tipo de Documento</option>
              {filters.documentTypes.map((documentType) => (
                <option key={documentType} value={documentType}>
                  {documentType}
                </option>
              ))}
            </select>
          </div>

          <div className="w-full md:w-1/2 lg:w-1/4">
            <select
              name="responsibleId"
              value={selectedFilters.responsibleId}
              onChange={handleFilterChange}
              className="w-full border border-gray-300 rounded-md p-2"
            >
              <option value="">Selecione Responsável</option>
              {filters.responsibleIds.map((responsible) => (
                <option key={responsible} value={responsible}>
                  {responsible}
                </option>
              ))}
            </select>
          </div>

          <div className="w-full md:w-1/2 lg:w-1/4">
            <select
              name="activeContract"
              value={selectedFilters.activeContract}
              onChange={handleFilterChange}
              className="w-full border border-gray-300 rounded-md p-2"
            >
              <option value="">Selecione Contrato Ativo</option>
              {filters.activeContract.map((contract) => (
                <option key={contract} value={contract}>
                  {contract}
                </option>
              ))}
            </select>
          </div>

          <div className="w-full md:w-1/2 lg:w-1/4">
            <select
              name="status"
              value={selectedFilters.status}
              onChange={handleFilterChange}
              className="w-full border border-gray-300 rounded-md p-2"
            >
              <option value="">Selecione Status</option>
              {filters.statuses.map((status) => (
                <option key={status} value={status}>
                  {status}
                </option>
              ))}
            </select>
          </div>

          <div className="w-full md:w-1/2 lg:w-1/4">
            <select
              name="documentTitle"
              value={selectedFilters.documentTitle}
              onChange={handleFilterChange}
              className="w-full border border-gray-300 rounded-md p-2"
            >
              <option value="">Selecione Título de Documento</option>
              {filters.documentTitles.map((title) => (
                <option key={title} value={title}>
                  {title}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Gráficos e Tabelas */}
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
