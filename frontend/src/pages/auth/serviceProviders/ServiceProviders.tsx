import { useState, useEffect } from "react";
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import {
  useFetchServiceProviders,
  ServiceProviderProps,
} from "@/hooks/gets/useServiceProviders";
import { NotebookPen } from "lucide-react";
import { ButtonBlue } from "@/components/ui/buttonBlue";

export function ServiceProvider() {
  const itemsPerPage = 5;

  const {
    serviceProviders,
    totalPages,
    loading,
    error,
    fetchServiceProviders,
  } = useFetchServiceProviders();

  const [currentPage, setCurrentPage] = useState(0);

  useEffect(() => {
    fetchServiceProviders(itemsPerPage, currentPage); // Chama o fetch ao carregar a página ou mudar a página
  }, [currentPage]);

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
  };

  const columns = [
    { key: "idProvider", label: "ID do Fornecedor" },
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

  if (loading) {
    return <p className="mt-10 text-center">Carregando...</p>;
  }

  if (error) {
    return <p className="mt-10 text-center text-red-500">Erro: {error}</p>;
  }

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
          <ButtonBlue onClick={() => console.log("Adicionar Fornecedor")}>
            Adicionar Prestador
          </ButtonBlue>
        </div>

        {serviceProviders.length > 0 ? (
          <Table data={serviceProviders} columns={columns} />
        ) : (
          <p className="text-center text-gray-500">Nenhum dado disponível.</p>
        )}

        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </div>
    </div>
  );
}
