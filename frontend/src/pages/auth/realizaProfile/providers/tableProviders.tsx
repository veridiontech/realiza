import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState } from "react";
import { Eye } from "lucide-react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { ip } from "@/utils/ip";

export function TableProviders() {
  const [suppliers, setSuppliers] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const { selectedBranch } = useBranch();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchSuppliers = async () => {
      setLoading(true);
      try {
        const token = localStorage.getItem("tokenClient");
        if (!token) {
          console.error("Token não encontrado.");
          setLoading(false);
          return;
        }

        const response = await axios.get(`${ip}/supplier/filtered-client`, {
          params: {
            idSearch: selectedBranch?.idBranch,
          },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setSuppliers(response.data.content || []);
      } catch (error) {
        console.error("Erro ao buscar fornecedores:", error);
        setSuppliers([]);
      } finally {
        setLoading(false);
      }
    };

    if (selectedBranch?.idBranch) {
      fetchSuppliers();
    }
  }, [selectedBranch]);

  return (
    <div className="p-4 pt-0 md:p-4 md:pt-0">
      <h2 className="mb-2 flex items-center gap-2 text-base font-semibold text-[#34495E]">
        <svg width="18" height="18" fill="currentColor" viewBox="0 0 24 24">
          <path d="M12 12c2.7 0 4.9-2.2 4.9-4.9S14.7 2.2 12 2.2 7.1 4.4 7.1 7.1s2.2 4.9 4.9 4.9zm0 2.2c-3.2 0-9.7 1.6-9.7 4.9v2.2h19.4v-2.2c0-3.3-6.5-4.9-9.7-4.9z" />
        </svg>
        Todos os fornecedores
      </h2>

      {/* Versão Mobile */}
      <div className="block md:hidden space-y-4">
        {loading ? (
          <p className="text-center text-gray-600">Carregando...</p>
        ) : suppliers.length > 0 ? (
          suppliers.map((supplier: any) => (
            <div
              key={supplier.idProvider}
              className="rounded-lg border border-gray-300 bg-white p-4 shadow-sm"
            >
              <p className="text-sm font-semibold text-gray-700">Nome:</p>
              <p className="mb-2 text-realizaBlue">
                {supplier.providerSupplierName}
              </p>
              <p className="text-sm font-semibold text-gray-700">CNPJ:</p>
              <p className="mb-2 text-gray-800">
                {supplier.providerSupplierCnpj}
              </p>
              <p className="mb-2 text-gray-800">
                {new Date(supplier.dateStart).toLocaleDateString("pt-BR")}
              </p>
              <p className="text-sm font-semibold text-gray-700">Ações:</p>
              <div className="flex gap-2">
                <button
                  title="Visualizar fornecedor"
                  onClick={() =>
                    navigate(`/sistema/fornecedor/${supplier.idProvider}`)
                  }
                >
                  <Eye className="w-5 h-5" />
                </button>
              </div>
            </div>
          ))
        ) : (
          <p className="text-center text-gray-600">
            Nenhum fornecedor encontrado.
          </p>
        )}
      </div>

      {/* Versão Desktop */}
      <div className="hidden md:block min-h-[60vh] rounded-lg bg-white p-6 shadow-lg">
        <div className="mb-4 flex items-center gap-2 rounded-md border border-gray-300 bg-[#F0F2F1] px-4 py-2">
          <svg className="h-5 w-5 text-gray-400" fill="currentColor" viewBox="0 0 24 24">
            <path d="M10 2a8 8 0 105.3 14.3l4.4 4.4 1.4-1.4-4.4-4.4A8 8 0 0010 2zm0 2a6 6 0 110 12A6 6 0 0110 4z" />
          </svg>
          <input
            type="text"
            placeholder="Pesquisar unidades, opções etc..."
            className="w-full bg-transparent text-sm text-gray-700 placeholder-gray-500 outline-none"
          />
        </div>

        <table className="w-full border-collapse text-left text-sm text-gray-700">
          <thead className="bg-[#DDE3DC] text-gray-700">
            <tr>
              <th className="px-4 py-3 font-semibold">Nome do Fornecedor</th>
              <th className="px-4 py-3 font-semibold">Cnpj | NIF</th>
              <th className="px-4 py-3 font-semibold">Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={3} className="px-4 py-4 text-center text-gray-500">
                  Carregando...
                </td>
              </tr>
            ) : suppliers.length > 0 ? (
              suppliers.map((supplier: any, index: number) => (
                <tr
                  key={supplier.idProvider}
                  className={index % 2 === 0 ? "bg-white" : "bg-[#F7F9F8]"}
                >
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-3">
                      <div className="flex h-8 w-8 items-center justify-center rounded-full bg-[#E2E8F0] text-xs font-semibold text-gray-600">
                        {supplier.corporateName?.split(" ").filter(Boolean).map((w: string) => w[0]).slice(0, 2).join("")}
                      </div>
                      {supplier.corporateName}
                    </div>
                  </td>
                  <td className="px-4 py-3">{supplier.cnpj}</td>
                  <td className="px-4 py-3 text-center">
                    <button
                      onClick={() => navigate(`/sistema/fornecedor/${supplier.idProvider}`)}
                      className="flex items-center gap-2 rounded-md bg-[#34495E] px-4 py-1.5 text-xs font-medium text-white hover:bg-[#1c1c6b] transition"
                    >
                      Visualizar perfil de fornecedor
                      <span className="text-xs">›</span>
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={3} className="px-4 py-4 text-center text-gray-500">
                  Nenhum fornecedor encontrado.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
