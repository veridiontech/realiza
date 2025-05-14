import { useEffect, useState } from "react";
import { Pagination } from "@/components/ui/pagination";
import { Table } from "@/components/ui/tableVanila";
import axios from "axios";
import { Puff } from "react-loader-spinner";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";
import { Eye } from "lucide-react"; // Importando apenas o ícone Eye
import { Link } from "react-router-dom";
import { useUser } from "@/context/user-provider";
import { useBranch } from "@/context/Branch-provider";
import { propsBranch } from "@/types/interfaces";
import { AddNewBranch } from "./modals/add-new-branch";

export function Branch() {
  const [branches, setBranches] = useState<propsBranch[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { client } = useClient();
  const { user } = useUser();
  const { setSelectedBranch } = useBranch();

  const columns: {
    key: keyof propsBranch;
    label: string;
    render?: (value: any, row: propsBranch) => JSX.Element;
  }[] = [
      { key: "name", label: "Nome da Filial" },
      { key: "cnpj", label: "CNPJ" },
      { key: "address", label: "Endereço" },
      {
        key: "actions",
        label: "Ações",
        render: (_value, row: propsBranch) => (
          <div>
            {user?.role === "ROLE_CLIENT_RESPONSIBLE" ? (
              <Link to={`/cliente/profileBranch/${row.idBranch}`}>
                <Eye />
              </Link>
            ) : (
              <Link
                to={`/sistema/profileBranch/${row.idBranch}`}
                onClick={() => setSelectedBranch(row)}
              >
                <Eye />
              </Link>
            )}
          </div>
        ),
      },
    ];

  const fetchBranches = async () => {
    setLoading(true);
    setError(null);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client?.idClient}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
      );
      const { content, totalPages: total } = response.data;
      setBranches(content);
      setTotalPages(total);
    } catch (err) {
      console.error("Erro ao buscar filiais:", err);
      setError("Erro ao buscar filiais. Tente novamente.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (client?.idClient) {
      setBranches([]);
      setCurrentPage(1);
      fetchBranches();
    }
  }, [client?.idClient]);

  const handlePageChange = (page: number) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  return (
    <div className="m-4 flex justify-center">
      <div className="flex w-full max-w-7xl flex-col rounded-lg bg-white p-4 shadow-md">
        <div className="mb-6 flex items-center justify-between">
          <h1 className="text-2xl">Filiais</h1>
          <AddNewBranch />
        </div>
        {loading ? (
          <div className="flex w-[20vw] items-center justify-start rounded-md border p-2 dark:bg-white">
            <Puff
              visible={true}
              height="30"
              width="30"
              color="#34495D"
              ariaLabel="puff-loading"
            />
            <span className="ml-2 text-black">Carregando...</span>
          </div>
        ) : error ? (
          <div className="text-center text-red-500">{error}</div>
        ) : (
          <>
            {/* Tela Mobile */}
            <div className="block md:hidden">
              {branches.length > 0 ? (
                branches.map((branch) => (
                  <div
                    key={branch.idBranch}
                    className="rounded-lg border bg-white p-4 shadow-sm mb-4"
                  >
                    <p className="text-sm font-semibold text-gray-700">Filial:</p>
                    <p className="text-realizaBlue mb-2">{branch.name}</p>

                    <p className="text-sm font-semibold text-gray-700">CNPJ:</p>
                    <p className="text-gray-800">{branch.cnpj}</p>

                    <p className="text-sm font-semibold text-gray-700">Endereço:</p>
                    <p className="text-gray-800">{branch.address}</p>

                    <div className="mt-2">
                      {user?.role === "ROLE_CLIENT_RESPONSIBLE" ? (
                        <Link to={`/cliente/profileBranch/${branch.idBranch}`}>
                          <Eye />
                        </Link>
                      ) : (
                        <Link
                          to={`/sistema/profileBranch/${branch.idBranch}`}
                          onClick={() => setSelectedBranch(branch)}
                        >
                          <Eye />
                        </Link>
                      )}
                    </div>
                  </div>
                ))
              ) : (
                <p className="text-center text-gray-600">Nenhuma filial encontrada</p>
              )}
            </div>

            {/* Tela Desktop */}
            <div className="hidden md:block overflow-x-auto rounded-lg border bg-white p-4 shadow-lg">
              <Table<propsBranch> data={branches} columns={columns} />
            </div>
          </>
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
