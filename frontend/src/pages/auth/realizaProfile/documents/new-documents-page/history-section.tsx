import { Button } from "@/components/ui/button";
import { useState } from "react";
import { ChevronLeft, ChevronRight } from "lucide-react";

export function HistorySection() {
  const [selectedTab, setSelectedTab] = useState("historyDetails");  // Começar diretamente na aba de detalhes
  const [filterDate, setFilterDate] = useState("");  // Filtro de data
  const [sortOrder, setSortOrder] = useState("desc");  // Estado para controle da ordenação (desc ou asc)

  // Dados fixos de histórico
  const historyData = [
    { date: "2025-06-20", action: "Alteração na configuração de serviços", user: "Admin" },
    { date: "2025-06-18", action: "Adição de novo perfil de permissão", user: "João" },
    { date: "2025-06-15", action: "Atualização do cadastro de documentos", user: "Maria" },
    // Adicione mais registros de histórico conforme necessário
  ];

  const tabsOrder = ["historyDetails"]; // A única aba agora é os detalhes

  const handlePrev = () => {
    const currentIndex = tabsOrder.indexOf(selectedTab);
    if (currentIndex > 0) {
      setSelectedTab(tabsOrder[currentIndex - 1]);
    }
  };

  const handleNext = () => {
    const currentIndex = tabsOrder.indexOf(selectedTab);
    if (currentIndex < tabsOrder.length - 1) {
      setSelectedTab(tabsOrder[currentIndex + 1]);
    }
  };

  const renderTabName = () => {
    if (selectedTab === "historyDetails") return "Detalhes do Histórico";
    return "";
  };

  // Função para filtrar os dados com base na data selecionada
  const filteredHistoryData = filterDate
    ? historyData.filter(item => item.date.includes(filterDate))
    : historyData;

  // Função para ordenar os dados de acordo com o estado de sortOrder (crescente ou decrescente)
  const sortedHistoryData = filteredHistoryData.sort((a, b) => {
    if (sortOrder === "desc") {
      return new Date(b.date).getTime() - new Date(a.date).getTime();  // Mais recente para mais antigo
    } else {
      return new Date(a.date).getTime() - new Date(b.date).getTime();  // Mais antigo para mais recente
    }
  });

  // Função para alternar a ordem de classificação
  const toggleSortOrder = () => {
    setSortOrder(sortOrder === "desc" ? "asc" : "desc");
  };

  return (
    <div className="relative bottom-[8vw]">
      <div className="absolute left-0 right-0 top-0 z-10 flex items-center justify-between gap-4 rounded-lg bg-white p-5 shadow-md md:hidden">
        <Button
          variant={"ghost"}
          onClick={handlePrev}
          disabled={selectedTab === "historyDetails"}
          className={`text-realizaBlue ${selectedTab === "historyDetails" ? "cursor-not-allowed opacity-50" : ""}`}
        >
          <ChevronLeft className="h-6 w-6" />
        </Button>
        <div className="flex flex-1 justify-center">
          <Button
            variant={"ghost"}
            className="bg-realizaBlue pointer-events-none px-6 py-3 font-bold text-white shadow-lg"
          >
            {renderTabName()}
          </Button>
        </div>
        <Button
          variant={"ghost"}
          onClick={handleNext}
          disabled={selectedTab === "historyDetails"}
          className={`text-realizaBlue ${selectedTab === "historyDetails" ? "cursor-not-allowed opacity-50" : ""}`}
        >
          <ChevronRight className="h-6 w-6" />
        </Button>
      </div>
      <div className="bg-white pt-24 shadow-md">
        {/* Exibir os Detalhes do Histórico */}
        {selectedTab === "historyDetails" && (
          <div className="p-6">
            <h2 className="text-2xl mb-4">Detalhes do Histórico de Parametrização</h2>
            <div className="mb-6">
              {/* Filtro de data */}
              <input
                type="date"
                value={filterDate}
                onChange={(e) => setFilterDate(e.target.value)} // Atualiza o filtro de data
                className="border p-2 rounded"
              />
            </div>

            {/* Botão para alternar a ordenação */}
            <div className="mb-4">
              <Button
                className="bg-realizaBlue text-white"
                onClick={toggleSortOrder} // Alterna entre crescente e decrescente
              >
                Ordenar por - {sortOrder === "desc" ? "Mais recente" : "Mais antigo"}
              </Button>
            </div>

            <table className="w-full table-auto border-collapse">
              <thead>
                <tr>
                  <th className="border p-4 text-left">Data</th>
                  <th className="border p-4 text-left">Ação</th>
                  <th className="border p-4 text-left">Usuário</th>
                </tr>
              </thead>
              <tbody>
                {sortedHistoryData.length === 0 ? (
                  <tr>
                    <td colSpan={3} className="border p-4 text-center">
                      Nenhum registro encontrado para a data selecionada.
                    </td>
                  </tr>
                ) : (
                  sortedHistoryData.map((item, index) => (
                    <tr key={index}>
                      <td className="border p-4">{item.date}</td>
                      <td className="border p-4">{item.action}</td>
                      <td className="border p-4">{item.user}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
