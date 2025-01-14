import { useState } from "react";
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import { useServiceProviders } from "@/hooks/useServiceProviders";
import { QuickActions } from "@/components/quickActions/quickAction";
import { StepOneServiceProviders } from "./modals/stepOne";
import { StepTwoServiceProviders } from "./modals/stepTwo";
import { NotebookPen } from "lucide-react";
import { ButtonBlue } from "@/components/ui/buttonBlue";

type ServiceProviders = {
  id: number;
  category: string;
  corporateReason: string;
  enterprise: string;
  cnpj: string;
  units: string;
};

export function ServiceProvider() {
  const fetchLimit = 1000;
  const itemsPerPage = 10;
  const { data, isLoading, error } = useServiceProviders({
    limit: fetchLimit,
    page: 1,
  });

  const [currentPage, setCurrentPage] = useState(1);
  const [isStepOneModalOpen, setIsStepOneModalOpen] = useState(false);
  const [isStepTwoModalOpen, setIsStepTwoModalOpen] = useState(false);

  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentData = data?.slice(startIndex, startIndex + itemsPerPage) || [];
  const totalPages = Math.ceil((data?.length || 0) / itemsPerPage);

  const handleStepOneSubmit = (data: Record<string, any>) => {
    console.log("Dados do primeiro modal enviados:", data);
    setIsStepOneModalOpen(false);
    setIsStepTwoModalOpen(true);
  };

  const handleStepTwoSubmit = (data: Record<string, any>) => {
    console.log("Dados do segundo modal enviados:", data);
    setIsStepTwoModalOpen(false);
  };

  const columns: {
    key: keyof ServiceProviders;
    label: string;
    render?: (value: any) => JSX.Element;
  }[] = [
    { key: "category", label: "Categoria" },
    { key: "corporateReason", label: "Razão Social" },
    { key: "enterprise", label: "Empresa" },
    { key: "cnpj", label: "CNPJ" },
    { key: "units", label: "Unidades" },
    {
      key: "id",
      label: "Ações",
      render: () => (
        <button className="ml-4 text-blue-500 hover:underline">
          <NotebookPen />
        </button>
      ),
    },
  ];

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
          <ButtonBlue onClick={() => setIsStepOneModalOpen(true)}>
            Adicionar Prestador
          </ButtonBlue>
        </div>

        <Table data={currentData || []} columns={columns} />

        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={setCurrentPage}
        />

        <div className="mx-10 my-6">
          <QuickActions />
        </div>
      </div>

      {isStepOneModalOpen && (
        <StepOneServiceProviders
          onClose={() => setIsStepOneModalOpen(false)}
          onSubmit={handleStepOneSubmit} // Submete o primeiro modal
        />
      )}

      {isStepTwoModalOpen && (
        <StepTwoServiceProviders
          onClose={() => setIsStepTwoModalOpen(false)}
          onSubmit={handleStepTwoSubmit} // Submete o segundo modal
        />
      )}
    </div>
  );
}
