import { useClient } from "@/context/Client-Provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";

export function TableServiceProvider() {
  const [suppliers, setSuppliers] = useState<any>([]);
  const { client } = useClient();
  const [branches, setBranches] = useState<any>([]);
  const [selectedBranch, setSelectedBranch] = useState("");

  console.log("client:", client);
  const getBranchClient = async () => {
    if (!client?.idClient) return;
    try {
      const res = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client.idClient}`,
      );
      setBranches(res.data.content);
      console.log("Filiais:", res.data.content);
    } catch (err) {
      console.log("Erro ao buscar filial do cliente", err);
    }
  };

  // Busca os fornecedores da filial selecionada
  const getSupplier = async (idBranch: string) => {
    if (!idBranch) return;
    try {
      const res = await axios.get(
        `${ip}/supplier/filtered-client?idSearch=${idBranch}`,
      );
      console.log("Dados do supplier:", res.data.content);
      setSuppliers(res.data.content);
    } catch (err) {
      console.log("Erro ao buscar prestadores de serviço", err);
    }
  };

  useEffect(() => {
    if (client?.idClient) {
      getBranchClient();
    }
  }, [client?.idClient]);

  return (
    <div className="p-10">
      <div className="mb-4">
        <select
          value={selectedBranch}
          className="w-[20vw] rounded-md border p-2"
          onChange={(e) => {
            const id = e.target.value;
            setSelectedBranch(id);
            getSupplier(id);
          }}
        >
          <option value="" disabled>
            Selecione uma filial
          </option>
          {branches.map((branch: any) => (
            <option key={branch.idBranch} value={branch.idBranch}>
              {branch.name}
            </option>
          ))}
        </select>
      </div>
      {suppliers.length > 0 ? (
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
            {suppliers.map((supplier: any) => (
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
            ))}
          </tbody>
        </table>
      ) : (
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
            <tr className="border border-gray-300">
              <td
                className="border border-gray-300 p-2 text-center"
                colSpan={3}
              >
                Nenhum fornecedor encontrado.
              </td>
            </tr>
          </tbody>
        </table>
      )}
    </div>
  );
}
