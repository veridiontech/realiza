import { useBranch } from "@/context/Branch-provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";

export function TableServiceProvider() {
  const [suppliers, setSuppliers] = useState<any>([]);
  const [loading, setLoading] = useState<boolean>(false);  
  const { selectedBranch } = useBranch();

  const getSupplier = async () => {
    if (!selectedBranch?.idBranch) return;
    setLoading(true); 
    try {
      const res = await axios.get(
        `${ip}/supplier/filtered-client?idSearch=${selectedBranch.idBranch}`,
      );
      console.log("Dados do supplier:", res.data.content);
      setSuppliers(res.data.content);
    } catch (err) {
      console.log("Erro ao buscar prestadores de serviço", err);
    } finally {
      setLoading(false);  
    }
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getSupplier();
      setSuppliers([]); 
    }
  }, [selectedBranch]);

  return (
    <div className="p-10">
      <div className="mb-4"></div>
      <table className="w-full border-collapse border border-gray-300">
        <thead>
          <tr className="bg-gray-200">
            <th className="border border-gray-300 p-2 text-left">
              Nome do Fornecedor
            </th>
            <th className="border border-gray-300 p-2 text-left">CNPJ</th>
            <th className="border border-gray-300 p-2 text-left">
              Filiais que Atua
            </th>
          </tr>
        </thead>
        <tbody>
          {loading ? (  // Verifica o estado de carregamento
            <tr className="border border-gray-300">
              <td
                className="border border-gray-300 p-2 text-center"
                colSpan={3}
              >
                Carregando...
              </td>
            </tr>
          ) : suppliers.length > 0 ? (
            suppliers.map((supplier: any) => (
              <tr key={supplier.idProvider} className="border border-gray-300">
                <td className="border border-gray-300 p-2">
                  {supplier.tradeName}
                </td>
                <td className="border border-gray-300 p-2">{supplier.cnpj}</td>
                <td className="border border-gray-300 p-2">
                  {supplier.branches && supplier.branches.length > 0
                    ? supplier.branches
                        .map((branch: any) => branch.nameBranch)
                        .join(", ")
                    : "Nenhuma filial associada"}
                </td>
              </tr>
            ))
          ) : (
            <tr className="border border-gray-300">
              <td
                className="border border-gray-300 p-2 text-center"
                colSpan={3}
              >
                Nenhum fornecedor encontrado.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
