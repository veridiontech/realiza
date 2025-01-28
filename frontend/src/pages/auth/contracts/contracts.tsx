import { useState, useEffect } from "react";
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import { useContracts } from "@/hooks/gets/useContracts";
import { useClient } from "@/context/Client-Provider";
import { Contract } from "@/types/contracts";
import { NotebookPen } from "lucide-react";

export default function ContractsTable() {
  const { client } = useClient(); // Recebe o cliente do contexto
  const itemsPerPage = 10;

  const {
    contracts = [],
    totalPages = 0,
    loading,
    error,
    fetchContracts,
  } = useContracts();

  const [currentPage, setCurrentPage] = useState(0);

  useEffect(() => {
    if (client?.idClient) {
      // Chama a função fetchContracts sempre que o cliente ou a página muda
      fetchContracts(itemsPerPage, currentPage, client.idClient);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, client?.idClient]);

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
  };

  const columns: {
    key: keyof Contract;
    label: string;
    className?: string;
    render?: (value: string | number | any[], row: Contract) => React.ReactNode;
  }[] = [
    { key: "serviceName", label: "Serviço" },
    { key: "startDate", label: "Data de Início" },
    { key: "endDate", label: "Data de Fim" },
    {
      key: "id",
      label: "Ações",
      render: (_, row) => (
        <div className="flex items-center space-x-2">
          <button
            onClick={() => console.log("Ação para o contrato:", row)}
            className="text-blue-500 hover:underline"
          >
            <NotebookPen />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="dark:bg-primary flex h-full w-[90rem] flex-col rounded-lg bg-white">
        <h1 className="m-8 text-xl font-semibold">Tabela de Contratos</h1>

        <div className="flex w-[90rem] flex-row justify-between px-10">
          <div className="relative mb-4">
            <input
              type="text"
              placeholder="🔍 Pesquisar contrato..."
              className="w-[34rem] rounded-lg border border-gray-300 p-2 focus:outline-blue-400"
              onChange={() => {}}
            />
          </div>
        </div>

        {error ? (
          <p className="text-center text-red-600">
            Erro ao carregar os dados: {error}
          </p>
        ) : loading ? (
          <p className="text-center">Carregando contratos...</p>
        ) : contracts.length > 0 ? (
          <Table<Contract> data={contracts} columns={columns} />
        ) : (
          <p className="text-center text-gray-500">
            Nenhum contrato disponível.
          </p>
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
