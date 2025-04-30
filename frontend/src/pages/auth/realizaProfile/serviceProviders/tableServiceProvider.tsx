import { useBranch } from "@/context/Branch-provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";
import { Eye, Pencil, UserPlus, Ban } from "lucide-react";
import bgModalRealiza from "@/assets/modalBG.jpeg";

export function TableServiceProvider() {
  const [suppliers, setSuppliers] = useState<any>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const { selectedBranch } = useBranch();
  const [isOpen, setIsOpen] = useState(false);
  const [action, setAction] = useState<string>("");

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

  const getContractDetails = async (supplierId: string) => {
    setLoading(true);
    try {
      const res = await axios.get(`${ip}/contract/supplier/filtered-client`,
        {
          params: {
            supplierId: { supplierId }
          }
        }
      );
      getContractDetails(res.data);
    } catch (err) {
      console.log("Erro ao buscar contrato", err);
    } finally {
      setLoading(false);
    }
  };

  const openModal = (actionType: string) => {
    setAction(actionType);
    setIsOpen(true);
  };

  const closeModal = () => {
    setIsOpen(false);
    setAction("");
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getSupplier();
      setSuppliers([]);
    }
  }, [selectedBranch]);

  return (
    <div className="p-5 md:p-10">
      <div className="mb-4"></div>
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
              <p className="mb-2 text-realizaBlue">{supplier.tradeName}</p>
              <p className="text-sm font-semibold text-gray-700">CNPJ:</p>
              <p className="mb-2 text-gray-800">{supplier.cnpj}</p>
              <p className="text-sm font-semibold text-gray-700">Filiais:</p>
              <p className="text-gray-800">
                {supplier.branches && supplier.branches.length > 0
                  ? supplier.branches.map((b: any) => b.nameBranch).join(", ")
                  : "Nenhuma filial associada"}
              </p>
              <p className="text-sm font-semibold text-gray-700">Ações:</p>
              <div className="flex gap-2">
                <button title="Visualizar contrato" onClick={() => openModal("Visualizar")}>
                  <Eye className="w-5 h-5" />
                </button>
                <button title="Editar" onClick={() => openModal("Editar")}>
                  <Pencil className="w-5 h-5" />
                </button>
                <button title="Alocar funcionário" onClick={() => openModal("Alocar")}>
                  <UserPlus className="w-5 h-5" />
                </button>
                <button title="Finalizar" onClick={() => openModal("Finalizar")}>
                  <Ban className="w-5 h-5" />
                </button>
              </div>
            </div>
          ))
        ) : (
          <p className="text-center text-gray-600">Nenhum fornecedor encontrado.</p>
        )}
      </div>
      <div className="hidden md:block overflow-x-auto rounded-lg border bg-white p-4 shadow-lg">
        <table className="w-full border-collapse border border-gray-300">
          <thead className="bg-gray-200">
            <tr>
              <th className="border border-gray-300 p-2 text-left">Nome do Fornecedor</th>
              <th className="border border-gray-300 p-2 text-left">CNPJ</th>
              <th className="border border-gray-300 p-2 text-left">Filiais que Atua</th>
              <th className="border border-gray-300 p-2 text-left">Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={3} className="border border-gray-300 p-2 text-center">
                  Carregando...
                </td>
              </tr>
            ) : suppliers.length > 0 ? (
              suppliers.map((supplier: any) => (
                <tr key={supplier.idProvider}>
                  <td className="border border-gray-300 p-2">{supplier.tradeName}</td>
                  <td className="border border-gray-300 p-2">{supplier.cnpj}</td>
                  <td className="border border-gray-300 p-2">
                    {supplier.branches && supplier.branches.length > 0
                      ? supplier.branches.map((b: any) => b.nameBranch).join(", ")
                      : "Nenhuma filial associada"}
                  </td>
                  <td className="border border-gray-300 p-2 space-x-2">
                    <button title="Visualizar contrato" onClick={() => openModal("Visualizar")}>
                      <Eye className="w-5 h-5" />
                    </button>
                    <button title="Editar" onClick={() => openModal("Editar")}>
                      <Pencil className="w-5 h-5" />
                    </button>
                    <button title="Alocar funcionário" onClick={() => openModal("Alocar")}>
                      <UserPlus className="w-5 h-5" />
                    </button>
                    <button title="Finalizar" onClick={() => openModal("Finalizar")}>
                      <Ban className="w-5 h-5" />
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={3} className="border border-gray-300 p-2 text-center">
                  Nenhum fornecedor encontrado.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
      {isOpen && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
          <div
            className="bg-white p-6 rounded-lg shadow-lg w-96 relative"
            style={{ backgroundImage: `url(${bgModalRealiza})`, backgroundSize: 'cover' }}
          >
            <h2 className="text-lg font-semibold text-white">{action}</h2>
            <div className="flex justify-end">
              <button
                className="bg-realizaBlue text-white px-4 py-2 rounded"
                onClick={closeModal}
              >
                Fechar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
