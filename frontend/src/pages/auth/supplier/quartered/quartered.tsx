// Arquivo: /components/Quartered.tsx (ou onde estiver seu componente)
import { useState, useEffect } from "react";
import { Table } from "@/components/ui/tableVanila";
import { Pagination } from "@/components/ui/pagination";
import {
  useSupplierQuartereds,
  QuarteredProps,
} from "@/hooks/gets/supplier/supplierGetQuartereds";
import { NotebookPen } from "lucide-react";
import SupplierAddQuartered from "@/components/supplier-add-quartered";
import { useUser } from "@/context/user-provider";

export function Quartered() {
  const { user } = useUser();
  console.log(user);

  const itemsPerPage = 5;

  // Use o hook para obter os dados
  const { quartereds, totalPages, error, fetchQuartereds } =
    useSupplierQuartereds();

  const [currentPage, setCurrentPage] = useState(0);

  useEffect(() => {
    // Filtra os subcontratados pelo ID do supplier (obtido do contexto do usuário)
    fetchQuartereds(itemsPerPage, currentPage, user?.idUser);
  }, [currentPage, user?.idUser]);

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
  };

  const columns = [
    { key: "companyName", label: "Nome do Subcontratado" },
    { key: "cnpj", label: "CNPJ" },
    { key: "branches", label: "Filiais que atua" },
    {
      key: "idProvider",
      label: "Ações",
      render: (_value: string | undefined, row: QuarteredProps) => (
        <div>
          <button
            onClick={() =>
              console.log("Ação para o subcontratado:", row.idProvider)
            }
            className="text-realizaBlue hover:underline"
          >
            <NotebookPen />
          </button>
        </div>
      ),
    },
  ] as {
    key: keyof QuarteredProps;
    label: string;
    className?: string;
    render?: (
      value: string | undefined,
      row: QuarteredProps,
    ) => React.ReactNode;
  }[];

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="dark:bg-primary flex h-full w-[90rem] flex-col rounded-lg bg-white">
        <h1 className="m-8 text-2xl">SubContratados</h1>

        <div className="flex w-[90rem] flex-row justify-between px-10">
          <div className="relative mb-4">
            <input
              type="text"
              placeholder="🔍 Pesquisar subcontratado..."
              className="focus:outline-realizaBlue w-[34rem] rounded-lg border border-gray-300 p-2"
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
          <Table<QuarteredProps> data={quartereds} columns={columns} />
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
