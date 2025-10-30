import { useBranch } from "@/context/Branch-provider";
import { useClient } from "@/context/Client-Provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { Search } from "lucide-react";
import { useEffect, useState } from "react";

export function BranchesTable() {
  const [branches, setBranches] = useState([]);
  const { client } = useClient();
  const { branch } = useBranch();
  const [filteredBranches, setFilteredBranches] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

  const fetchBranches = async () => {
    setLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client?.idClient}&size=9999`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        },
      );
      const allBranches = response.data.content;
      setBranches(allBranches);
      setFilteredBranches(allBranches);
    } catch (err) {
      console.error("Erro ao buscar filiais:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    const term = event.target.value.toLowerCase();
    setSearchTerm(term);
    const filtered = branches.filter(
      (branch: any) =>
        branch.name.toLowerCase().includes(term) || branch.cnpj.includes(term),
    );
    setFilteredBranches(filtered);
  };

  useEffect(() => {
    if (client?.idClient) {
      fetchBranches();
    }
  }, [client?.idClient, branch]);

  return (
    <div>
      <div className="flex flex-col gap-5">
        {/* Bloco para visualização em telas pequenas (mobile) */}
        <div className="block h-[300px] space-y-4 overflow-y-auto md:hidden">
          {loading ? (
            <p className="text-center text-gray-600">Carregando...</p>
          ) : filteredBranches && filteredBranches.length > 0 ? (
            filteredBranches.map((branch: any) => (
              <div
                key={branch.idBranch}
                className="rounded-lg border border-gray-300 bg-white p-4 shadow-sm"
              >
                <p className="bg-[#345D5C33] text-sm font-semibold text-gray-700">
                  Filial:
                </p>
                <p className="text-realizaBlue mb-2">{branch.name}</p>
                <p className="bg-[#345D5C33] text-sm font-semibold text-gray-700">
                  CNPJ:
                </p>
                <p className="text-gray-800">{branch.cnpj}</p>
              </div>
            ))
          ) : (
            <p className="text-center text-gray-600">
              Nenhuma filial encontrada
            </p>
          )}
        </div>

        {/* Bloco para visualização em telas maiores (desktop) */}
        {/* Adicionado rounded-lg/shadow/border aqui, assumindo que BranchesTable é o conteúdo principal do Card */}
        <div className="hidden bg-white md:block rounded-lg shadow-md border"> 
          
          <div className="flex w-full items-center gap-4 border-b border-gray-200 p-4">
            <div className="flex w-64 items-center gap-4 rounded-md border p-2">
              <Search className="h-4 w-4 text-gray-500" />
              <input
                type="text"
                value={searchTerm}
                onChange={handleSearch}
                placeholder="Pesquisar filiais"
                className="outline-none"
              />
            </div>
          </div>

          {/* AJUSTE CRÍTICO: Usando h-[300px] (altura fixa) e overflow-x-hidden no contêiner de rolagem */}
          <div className="h-[300px] overflow-y-auto overflow-x-hidden relative z-0">
            <table className="w-full border-collapse">
              <thead className="relative z-20"> 
                {/* Mantendo sticky top-0 e z-20 para garantir que o cabeçalho não seja vazado */}
                <tr className="sticky top-0 bg-[#37474F] text-white z-20"> 
                  <th className="px-4 py-2 text-start">Filiais</th>
                  <th className="px-4 py-2 text-start">CNPJ</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td
                      colSpan={2}
                      className="px-4 py-2 text-center text-gray-600"
                    >
                      Carregando...
                    </td>
                  </tr>
                ) : filteredBranches && filteredBranches.length > 0 ? (
                  filteredBranches.map((branch: any) => (
                    <tr 
                        key={branch.idBranch} 
                        className="border-b border-gray-200 last:border-b-0 hover:bg-gray-50 cursor-pointer"
                    >
                      <td className="px-4 py-2">
                        {/* Emulação da bolinha com flexbox e span */}
                        <div className="flex items-center space-x-2">
                          <span className="h-2 w-2 rounded-full bg-realizaBlue flex-shrink-0"></span>
                          <span className="text-realizaBlue">{branch.name}</span>
                        </div>
                      </td>
                      <td className="text-start px-4 py-2">{branch.cnpj}</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td
                      colSpan={2}
                      className="px-4 py-2 text-center border-t border-gray-300"
                    >
                      Nenhuma filial encontrada
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}