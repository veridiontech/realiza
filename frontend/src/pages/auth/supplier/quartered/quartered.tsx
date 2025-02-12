import { useState, useEffect } from "react";
import { Table } from "@/components/ui/tableVanila";
import { Pagination } from "@/components/ui/pagination";
import {
  useFetchServiceProviders,
  ServiceProviderProps,
} from "@/hooks/gets/realiza/useServiceProviders";
import { NotebookPen } from "lucide-react";
import SupplierAddQuartered from "@/components/supplier-add-quartered";
import { useClient } from "@/context/Client-Provider";

export function Quartered() {
  const { client } = useClient();
  console.log(client);

  const itemsPerPage = 5;

  const {
    serviceProviders = [],
    totalPages = 0,
    error,
    fetchServiceProviders,
  } = useFetchServiceProviders();

  const [currentPage, setCurrentPage] = useState(0);

  useEffect(() => {
    // Filtra os fornecedores pelo id do cliente, se existir.
    fetchServiceProviders(itemsPerPage, currentPage, client?.idClient);
  }, [currentPage, client?.idClient]);

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
  };

  const columns = [
    { key: "companyName", label: "Nome do Fornecedor" },
    { key: "cnpj", label: "CNPJ" },
    { key: "branches", label: "Filiais que atua" },
    {
      key: "idProvider",
      label: "Ações",
      render: (value: string) => (
        <div>
          <button
            onClick={() => console.log("Ação para o fornecedor:", value)}
            className="text-blue-500 hover:underline"
          >
            <NotebookPen />
          </button>
        </div>
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
        <h1 className="m-8 text-2xl">Quarteirizados</h1>

        <div className="flex w-[90rem] flex-row justify-between px-10">
          <div className="relative mb-4">
            <input
              type="text"
              placeholder="🔍 Pesquisar fornecedor..."
              className="w-[34rem] rounded-lg border border-gray-300 p-2 focus:outline-blue-400"
              onChange={() => {}}
            />
          </div>
          <SupplierAddQuartered />
        </div>

        {error ? (
          <p className="text-center text-red-600">
            Erro ao carregar os dados: {error}
          </p>
        ) : (
          <Table<ServiceProviderProps>
            data={serviceProviders}
            columns={columns}
          />
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
