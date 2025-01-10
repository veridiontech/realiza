import { useState } from "react";
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import { useServiceProviders } from "@/hooks/useServiceProviders";
import { QuickActions } from "@/components/quickActions/quickAction";
import { StepOneServiceProviders } from "./modals/stepOne";

export function ServiceProvider() {
  const fetchLimit = 1000;
  const itemsPerPage = 10;
  const { data, isLoading, error } = useServiceProviders({
    limit: fetchLimit,
    page: 1,
  });

  if (isLoading) {
    return <p className="mt-10 text-center">Carregando...</p>;
  }

  if (error) {
    return (
      <p className="mt-10 text-center text-red-500">
        Erro ao carregar os dados
      </p>
    );
  }

  const [currentPage, setCurrentPage] = useState(1);
  const [search, setSearch] = useState("");
  const [isAddProviderModalOpen, setIsAddProviderModalOpen] = useState(false);

  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentData = data?.slice(startIndex, startIndex + itemsPerPage) || [];
  const totalPages = Math.ceil((data?.length || 0) / itemsPerPage);

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="flex h-full w-[90rem] flex-col rounded-lg bg-white">
        <h1 className="m-8">Prestadores de Serviço</h1>
        <div className="flex w-[90rem] flex-row justify-between px-10">
          <div className="relative mb-4">
            <input
              type="text"
              placeholder="🔍 Pesquisar units, ações etc..."
              className="w-[34rem] rounded-lg border border-gray-300 p-2 focus:outline-blue-400"
              value={""}
            />
          </div>
          <button
            className="h-[3rem] rounded-md border-2 border-blue-300 px-6 text-black hover:border-blue-600 hover:bg-blue-300 hover:text-white"
            onClick={() => setIsAddProviderModalOpen(true)}
          >
            Adicionar Prestador
          </button>
        </div>

        <Table data={currentData || []} />

        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={setCurrentPage}
        />

        <div className="mx-10 my-6">
          <QuickActions />
        </div>
      </div>
      {isAddProviderModalOpen && (
        <StepOneServiceProviders
          onClose={() => setIsAddProviderModalOpen(false)} // Fecha o modal
        />
      )}
    </div>
  );
}
