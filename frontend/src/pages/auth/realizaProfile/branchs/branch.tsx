import { useEffect, useState } from "react";
import { Table } from "@/components/ui/tableVanila";
import axios from "axios";
import { Puff } from "react-loader-spinner";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";
import { Eye } from "lucide-react";
import { Link } from "react-router-dom";
import { useUser } from "@/context/user-provider";
import { useBranch } from "@/context/Branch-provider";
import { propsBranch } from "@/types/interfaces";
import { AddNewBranch } from "./modals/add-new-branch";

export function Branch() {
  const [branches, setBranches] = useState<propsBranch[]>([]);
  const [, setTotalPages] = useState(1);
  const [, setCurrentPage] = useState(1);
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
    { key: "name", label: "Unidade ou Filial" },
    { key: "cnpj", label: "Cnpj | NIF" },
    { key: "address", label: "Endereço" },
    { key: "email", label: "E-mail" },
    //{ key: "phone", label: "Telefone" },
    {
      key: "actions",
      label: "Ações",
      render: (_value, row: propsBranch) => (
        <div className="flex justify-center">
          {user?.role === "ROLE_CLIENT_RESPONSIBLE" ? (
            <Link to={`/cliente/profileBranch/${row.idBranch}`}>
              <Eye className="text-gray-600 hover:text-black" />
            </Link>
          ) : (
            <Link
              to={`/sistema/profileBranch/${row.idBranch}`}
              onClick={() => setSelectedBranch(row)}
            >
              <Eye className="text-gray-600 hover:text-black" />
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
        `${ip}/branch/filtered-client?idSearch=${client?.idClient}&size=9999`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
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


  return (
    <div className="px-4 pt-0 md:px-10 md:pt-4">
      {/* Título fora da caixa */}
      <h2 className="mb-4 flex items-center gap-2 text-base font-semibold text-[#34495E]">
        <svg width="18" height="18" fill="currentColor" viewBox="0 0 24 24">
          <path d="M12 12c2.7 0 4.9-2.2 4.9-4.9S14.7 2.2 12 2.2 7.1 4.4 7.1 7.1s2.2 4.9 4.9 4.9zm0 2.2c-3.2 0-9.7 1.6-9.7 4.9v2.2h19.4v-2.2c0-3.3-6.5-4.9-9.7-4.9z" />
        </svg>
        Todas as Filiais
      </h2>

      <div className="">
        <div className="mb-4 flex items-center justify-between">
          <div className="flex w-full max-w-md items-center gap-2 rounded-md border border-gray-300 bg-[#F0F2F1] px-4 py-2">
            <svg className="h-5 w-5 text-gray-400" fill="currentColor" viewBox="0 0 24 24">
              <path d="M10 2a8 8 0 105.3 14.3l4.4 4.4 1.4-1.4-4.4-4.4A8 8 0 0010 2zm0 2a6 6 0 110 12A6 6 0 0110 4z" />
            </svg>
            <input
              type="text"
              placeholder="Pesquisar unidades, opções etc..."
              className="w-full bg-transparent text-sm text-gray-700 placeholder-gray-500 outline-none"
            />
          </div>
          <AddNewBranch />
        </div>

        {loading ? (
          <div className="flex items-center gap-2 rounded-md border p-2 dark:bg-white">
            <Puff visible={true} height="30" width="30" color="#34495D" ariaLabel="puff-loading" />
            <span className="text-black">Carregando...</span>
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
                    className="mb-4 rounded-lg border bg-white p-4 shadow-sm"
                  >
                    <p className="text-sm font-semibold text-gray-700">Filial:</p>
                    <p className="mb-2 text-realizaBlue">{branch.name}</p>
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

      </div>
    </div>
  );
}
