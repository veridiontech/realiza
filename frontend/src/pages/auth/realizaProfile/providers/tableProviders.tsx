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

        const response = await axios.get( `${ip}/supplier/filtered-client`,
          {
            params: {
              idSearch: selectedBranch?.idBranch
            },
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
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
    <div className="p-5 md:p-10">
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
      <div className="hidden md:block overflow-x-auto rounded-lg border bg-white p-4 shadow-lg">
        <table className="w-full border-collapse border border-gray-300">
          <thead className="bg-gray-200">
            <tr>
              <th className="border border-gray-300 p-2 text-left">
                Nome do Fornecedor
              </th>
              <th className="border border-gray-300 p-2 text-left">CNPJ</th>
              <th className="border border-gray-300 p-2 text-left">Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td
                  colSpan={3}
                  className="border border-gray-300 p-2 text-center"
                >
                  Carregando...
                </td>
              </tr>
            ) : suppliers.length > 0 ? (
              suppliers.map((supplier: any) => (
                <tr key={supplier.idProvider}>
                  <td className="border border-gray-300 p-2">
                    {supplier.corporateName}
                  </td>
                  <td className="border border-gray-300 p-2">
                    {supplier.cnpj}
                  </td>
                  <td className="border border-gray-300 p-2 space-x-2">
                    <button
                      title="Visualizar fornecedor"
                      onClick={() =>
                        navigate(`/sistema/fornecedor/${supplier.idProvider}`)
                      }
                    >
                      <Eye className="w-5 h-5" />
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td
                  colSpan={3}
                  className="border border-gray-300 p-2 text-center"
                >
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
