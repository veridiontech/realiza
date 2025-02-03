import { useState, useEffect } from "react";
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import { useContracts } from "@/hooks/gets/useContracts";
import { useClient } from "@/context/Client-Provider";
import { Contract } from "@/types/contracts";
import { NotebookPen, Users} from "lucide-react";
import { useNavigate } from "react-router-dom";
import { ModalAddContract } from "@/components/modal-add-contract";

export default function ContractsTable() {
  const { client } = useClient();
  const itemsPerPage = 10;
  const navigate = useNavigate();

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
      fetchContracts(itemsPerPage, currentPage, client.idClient);
    }
  }, [currentPage, client?.idClient]);

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
  };

  const columns = [
    { key: "serviceName" as keyof Contract, label: "Serviço" },
    { key: "startDate" as keyof Contract, label: "Data de Início" },
    { key: "endDate" as keyof Contract, label: "Data de Fim" },
    {
      key: "id" as keyof Contract,
      label: "Ações",
      render: (_: any, row: Contract) => (
        <div className="flex items-center space-x-2">
          <button
            onClick={() => console.log("Contrato Selecionado:", row)}
            className="text-blue-500 hover:underline"
          >
            <NotebookPen />
          </button>
          <button
            onClick={() => {
              console.log("ID do contrato antes da navegação:", row.id);
              if (row.id) {
                navigate(`/sistema/employee-to-contract/${row.id}`);
              } else {
                console.error("ID do contrato não encontrado!", row);
              }
            }}
            className="text-green-500 hover:underline"
          >
            <Users />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="dark:bg-primary flex h-full w-[90rem] flex-col rounded-lg bg-white">
        {/* Cabeçalho com o título e botão para adicionar contrato */}
        <div className="m-8 flex items-center justify-between">
          <h1 className="text-xl font-semibold">Tabela de Contratos</h1>
          <ModalAddContract /> {/* Botão Novo Contrato */}
        </div>

        {/* Mensagens de erro e carregamento */}
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

        {/* Paginação */}
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </div>
    </div>
  );
}
