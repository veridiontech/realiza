import { useContracts } from "@/hooks/useContracts";
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import { useState } from "react";
import { ScrollText } from "lucide-react";
import { Contract } from "@/types/contracts";

const ContractsTable = () => {
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const {
    data: contracts = [],
    isLoading,
    error,
  } = useContracts({
    limit: itemsPerPage,
    page: currentPage,
  });

  const totalPages = Math.ceil((contracts.length || 0) / itemsPerPage);

  const columns: {
    key: keyof Contract;
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
      render: (id: string) => (
        <button className="ml-4 text-blue-500 hover:underline">
          <ScrollText />
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
        Erro ao carregar contratos.
      </p>
    );
  }

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="flex h-full w-[90rem] flex-col rounded-lg bg-white">
        <h1 className="m-4 text-xl">Tabela de Contratos</h1>

        <Table data={contracts} columns={columns} />

        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={setCurrentPage}
        />
      </div>
    </div>
  );
};

export default ContractsTable;
