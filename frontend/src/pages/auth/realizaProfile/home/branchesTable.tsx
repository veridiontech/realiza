import { useClient } from "@/context/Client-Provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { ArrowLeft, ArrowRight, Search } from "lucide-react";
import { useEffect, useState } from "react";

export function BranchesTable() {
  const [branches, setBranches] = useState([]);
  const [currentPage, setCurrentPage] = useState(1); // Página atual
  const [totalPages, setTotalPages] = useState(0); // Total de páginas
  const { client } = useClient();
  const [filteredBranches, setFilteredBranches] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

  const fetchBranches = async (page = 1) => {
    setLoading(true); // Inicia o carregamento
    try {
      const response = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client?.idClient}`,
        {
          params: {
            page: page - 1, // Páginas geralmente começam de 0
          },
        },
      );
      const { content, totalPages: total } = response.data;
        setBranches(content);
      setFilteredBranches(content);
      setTotalPages(total); // Atualiza total de páginas
      // setSearchBranches(content)
    } catch (err) {
      console.error("Erro ao buscar filiais:", err);
    } finally {
      setLoading(false); // Finaliza o carregamento
    }
  };

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    const term = event.target.value.toLowerCase(); // Converter para minúsculas para garantir que a busca seja case-insensitive
    setSearchTerm(term); // Atualizar o termo de pesquisa

    // Filtrar as filiais com base no nome ou CNPJ
    const filtered = branches.filter(
      (branch: any) =>
        branch.name.toLowerCase().includes(term) || branch.cnpj.includes(term),
    );

    setFilteredBranches(filtered); // Atualizar o estado de filiais filtradas
  };

  useEffect(() => {
    if (client?.idClient) {
      fetchBranches(currentPage);
    }
  }, [client?.idClient, currentPage]);

  return (
    <div>
      <div className="flex flex-col gap-5">
        <div className="block space-y-4 md:hidden">
          {filteredBranches && filteredBranches.length > 0 ? (
            filteredBranches.map((branch: any) => (
              <div
                key={branch.idBranch}
                className="rounded-lg border border-gray-300 bg-white p-4 shadow-sm"
              >
                <p className="text-sm font-semibold text-gray-700">Filial:</p>
                <p className="text-realizaBlue mb-2">{branch.name}</p>
                <p className="text-sm font-semibold text-gray-700">CNPJ:</p>
                <p className="text-gray-800">{branch.cnpj}</p>
              </div>
            ))
          ) : (
            <p className="text-center text-gray-600">
              Nenhuma filial encontrada
            </p>
          )}
        </div>

        <div className="hidden overflow-x-auto rounded-lg border bg-white p-4 shadow-lg md:block">
          <div className="flex w-64 items-center gap-4 rounded-md border p-2">
            <Search />
            <input
              type="text"
              value={searchTerm}
              onChange={handleSearch}
              placeholder="Pesquisar filiais"
              className="outline-none"
            />
          </div>
          <table className="mt-4 w-full border-collapse border border-gray-300">
            <thead>
              <tr>
                <th className="border border-gray-300 px-4 py-2 text-start">
                  Filiais
                </th>
                <th className="border">CNPJ</th>
              </tr>
            </thead>
            <tbody>
              {filteredBranches && filteredBranches.length > 0 ? (
                filteredBranches.map((branch: any) => (
                  <tr key={branch.idBranch}>
                    <td className="border border-gray-300 px-4 py-2">
                      <li className="text-realizaBlue">{branch.name}</li>
                    </td>
                    <td className="border border-gray-300 text-center">
                      {branch.cnpj}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td
                    colSpan={2}
                    className="border border-gray-300 px-4 py-2 text-center"
                  >
                    Nenhuma filial encontrada
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
        <div className="mt-4 flex flex-col items-end justify-center">
          <div className="mt-4 flex gap-2">
            <button
              className={`${
                currentPage === 1 || loading
                  ? "cursor-not-allowed bg-neutral-300"
                  : "bg-realizaBlue"
              } rounded-md px-4 py-2 text-white`}
              onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
              disabled={currentPage === 1 || loading}
            >
              <ArrowLeft />
            </button>

            {/* Botão Próximo */}
            <button
              className={`${
                currentPage === totalPages || loading
                  ? "cursor-not-allowed bg-neutral-300"
                  : "bg-realizaBlue"
              } rounded-md px-4 py-2 text-white`}
              onClick={() =>
                setCurrentPage((prev) => Math.min(prev + 1, totalPages))
              }
              disabled={currentPage === totalPages || loading}
            >
              <ArrowRight />
            </button>
          </div>
          {loading ? (
            <div className="text-center text-gray-600">Carregando...</div>
          ) : (
            <div className="text-center">
              <span>
                Página {currentPage} de {totalPages}
              </span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
