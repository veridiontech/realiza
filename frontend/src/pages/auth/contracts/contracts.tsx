import { useState } from "react";
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import { ScrollText } from "lucide-react";
import { useContracts } from "@/hooks/gets/useContracts";

const ContractsTable = () => {
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const {
    contracts = [], // Fallback para array vazio
    totalPages = 1, // Fallback para 1 página
    loading,
    error,
  } = useContracts({
    limit: itemsPerPage,
    page: currentPage,
  });

  const columns: {
    key: keyof (typeof contracts)[0];
    label: string;
    render?: (value: any) => JSX.Element;
  }[] = [
    { key: "ref", label: "Referência" },
    { key: "project", label: "Projeto" },
    { key: "clientFinal", label: "Cliente Final" },
    { key: "client", label: "Cliente" },
    { key: "startDate", label: "Data de Início" },
    { key: "endDate", label: "Data de Fim" },
    {
      key: "id",
      label: "Ações",
      render: () => (
        <button className="ml-4 text-blue-500 hover:underline">
          <ScrollText />
        </button>
      ),
    },
  ];

  const handlePageChange = (newPage: number) => {
    // Garante que a página não vá além dos limites
    if (newPage > 0 && newPage <= totalPages) {
      setCurrentPage(newPage);
    }
  };

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="flex h-full w-[90rem] flex-col rounded-lg bg-white">
        <h1 className="m-4 text-xl">Tabela de Contratos</h1>

        {loading ? (
          <p className="mt-10 text-center">Carregando...</p>
        ) : error ? (
          <p className="text-center text-red-600">
            Erro ao carregar os dados:{" "}
            {typeof error === "string"
              ? error
              : "Algo deu errado. Tente novamente mais tarde."}
          </p>
        ) : contracts.length > 0 ? (
          <Table data={contracts} columns={columns} />
        ) : (
          <p className="text-center text-gray-500">
            Nenhum contrato disponível.
          </p>
        )}

        <Pagination
          currentPage={currentPage}
          totalPages={Math.max(totalPages, 1)}
          onPageChange={handlePageChange}
        />
      </div>
    </div>
  );
};

export default ContractsTable;
