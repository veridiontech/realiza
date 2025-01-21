import { useState, useEffect } from "react";
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import {
  useFetchServiceProviders,
  ServiceProviderProps,
} from "@/hooks/gets/useServiceProviders";
import { NotebookPen } from "lucide-react";
import { ButtonBlue } from "@/components/ui/buttonBlue";
import { StepOneServiceProviders } from "./modals/stepOne";
import { StepTwoServiceProviders } from "./modals/stepTwo";

export function ServiceProvider() {
  const itemsPerPage = 5;

  const {
    serviceProviders = [],
    totalPages = 0,
    error,
    fetchServiceProviders,
  } = useFetchServiceProviders();

  const [currentPage, setCurrentPage] = useState(0);
  const [isStepOneModalOpen, setIsStepOneModalOpen] = useState(false);
  const [isStepTwoModalOpen, setIsStepTwoModalOpen] = useState(false);
  const [providerData, setProviderData] = useState<Record<string, any> | null>(
    null,
  );

  useEffect(() => {
    fetchServiceProviders(itemsPerPage, currentPage);
  }, [currentPage]);

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
  };

  const handleStepOneSubmit = (data: Record<string, any>) => {
    console.log("Dados do Primeiro Modal:", data);
    setProviderData(data);
    setIsStepOneModalOpen(false);
    setIsStepTwoModalOpen(true);
  };

  const handleStepTwoSubmit = (data: Record<string, any>) => {
    console.log("Dados do Segundo Modal:", { ...providerData, ...data });
    setIsStepTwoModalOpen(false);
  };

  const columns = [
    { key: "companyName", label: "Nome do Fornecedor" },
    { key: "cnpj", label: "CNPJ" },
    { key: "client", label: "ID do Cliente" },
    {
      key: "idProvider",
      label: "Ações",
      render: (value: string) => (
        <button
          onClick={() => console.log("Ação para o fornecedor:", value)}
          className="ml-4 text-blue-500 hover:underline"
        >
          <NotebookPen />
        </button>
      ),
    },
  ] as {
    key: keyof ServiceProviderProps;
    label: string;
    render?: (value: string) => React.ReactNode;
  }[];

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="dark:bg-primary flex h-full w-[90rem] flex-col rounded-lg bg-white">
        <h1 className="m-8">Prestadores de Serviço</h1>

        <div className="flex w-[90rem] flex-row justify-between px-10">
          <div className="relative mb-4">
            <input
              type="text"
              placeholder="🔍 Pesquisar fornecedor..."
              className="w-[34rem] rounded-lg border border-gray-300 p-2 focus:outline-blue-400"
              onChange={() => {}}
            />
          </div>
          <ButtonBlue onClick={() => setIsStepOneModalOpen(true)}>
            Adicionar Prestador
          </ButtonBlue>
        </div>

        {error ? (
          <p className="text-center text-red-600">
            Erro ao carregar os dados: {error}
          </p>
        ) : (
          <Table data={serviceProviders} columns={columns} />
        )}

        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />

        {isStepOneModalOpen && (
          <StepOneServiceProviders
            onClose={() => setIsStepOneModalOpen(false)}
            onSubmit={handleStepOneSubmit}
          />
        )}

        {isStepTwoModalOpen && (
          <StepTwoServiceProviders
            onClose={() => setIsStepTwoModalOpen(false)}
            onSubmit={handleStepTwoSubmit}
          />
        )}
      </div>
    </div>
  );
}
