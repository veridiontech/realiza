import { useState } from "react";
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import { ScrollText, FilePlus2, SquareUser } from "lucide-react";
import { StepTwoServiceProviders } from "../serviceProviders/modals/stepTwo";
// import { useContracts } from "@/hooks/gets/useContracts";

const ContractsTable = () => {
  const [currentPage, setCurrentPage] = useState(1);
  const [isStepTwoModalOpen, setIsStepTwoModalOpen] = useState(false);
  const [providerData, setProviderData] = useState<Record<string, any> | null>(
    null,
  );
  const itemsPerPage = 10;

  // Mock de contratos
  const mockContracts = Array.from({ length: 20 }, (_, index) => ({
    id: `${index + 1}`,
    serviceName: `Serviço ${index + 1}`,
    startDate: new Date(2023, 0, index + 1).toLocaleDateString("pt-BR"),
    endDate: new Date(2023, 0, index + 10).toLocaleDateString("pt-BR"),
  }));

  const totalPages = Math.ceil(mockContracts.length / itemsPerPage);
  const contracts = mockContracts.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage,
  );

  const columns: {
    key: keyof (typeof contracts)[0];
    label: string;
    render?: (value: any) => JSX.Element;
  }[] = [
    { key: "serviceName", label: "Serviço" },
    { key: "startDate", label: "Data de Início" },
    { key: "endDate", label: "Data de Fim" },
    {
      key: "id",
      label: "Ações",
      render: (id: string) => (
        <div>
          <button
            className="text-blue-500 hover:underline"
            onClick={() => {
              setProviderData({ id });
              setIsStepTwoModalOpen(true);
            }}
          >
            <FilePlus2 />
          </button>
          <button className="ml-4 text-blue-500 hover:underline">
            <ScrollText />
          </button>
          <button className="ml-4 text-blue-500 hover:underline">
            <SquareUser />
          </button>
        </div>
      ),
    },
  ];

  const handleStepTwoSubmit = (data: Record<string, any>) => {
    console.log("Dados do Segundo Modal:", { ...providerData, ...data });
    setIsStepTwoModalOpen(false);
  };

  const handlePageChange = (newPage: number) => {
    if (newPage > 0 && newPage <= totalPages) {
      setCurrentPage(newPage);
    }
  };

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
          totalPages={Math.max(totalPages, 1)}
          onPageChange={handlePageChange}
        />

        {isStepTwoModalOpen && (
          <StepTwoServiceProviders
            onClose={() => setIsStepTwoModalOpen(false)}
            onSubmit={handleStepTwoSubmit}
          />
        )}
      </div>,-
    </div>
  );
};

export default ContractsTable;
