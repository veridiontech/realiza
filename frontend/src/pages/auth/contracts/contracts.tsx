import { useState } from "react";
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import { ScrollText } from "lucide-react";
import { useContracts } from "@/hooks/gets/useContracts";

const ContractsTable = () => {
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const { contracts, totalPages, loading, error } = useContracts({
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
      render: (id: string) => (
        <button className="ml-4 text-blue-500 hover:underline">
          <ScrollText />
        </button>
      ),
    },
  ];

  if (loading) {
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

        {contracts.length > 0 ? (
          <Table data={contracts} columns={columns} />
        ) : (
          <p className="text-center text-gray-500">
            Nenhum contrato disponível.
          </p>
        )}

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
