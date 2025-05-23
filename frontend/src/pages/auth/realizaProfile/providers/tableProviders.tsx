import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState } from "react";
import { Eye } from "lucide-react";
import { useNavigate } from "react-router-dom";

export function TableProviders() {
  const [suppliers, setSuppliers] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const { selectedBranch } = useBranch();
  const navigate = useNavigate();

  const mockSuppliers = [
    {
      idProvider: "1",
      contractReference: "00012345",
      providerSupplierName: "Fornecedor Alfa LTDA",
      providerSupplierCnpj: "12.345.678/0001-90",
      serviceName: "Serviço X",
      dateStart: new Date().toISOString(),
      finished: false,
      email: "contato@fornecedoralfa.com",
      telefone: "(11) 1234-5678",
      outrasContatos: "WhatsApp: (11) 98765-4321",
    },
    {
      idProvider: "2",
      contractReference: "00054321",
      providerSupplierName: "Fornecedor Beta SA",
      providerSupplierCnpj: "98.765.432/0001-09",
      serviceName: "Serviço Y",
      dateStart: new Date().toISOString(),
      finished: true,
      email: "contato@fornecedorb.com",
      telefone: "(21) 4321-8765",
      outrasContatos: "Telegram: @fornecedorbeta",
    },
  ];

  useEffect(() => {
    setLoading(true);
    const timer = setTimeout(() => {
      setSuppliers(mockSuppliers);
      setLoading(false);
    }, 1000);

    return () => clearTimeout(timer);
  }, [selectedBranch]);

  return (
    <div className="p-5 md:p-10">
      {/* mobile */}
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
              <p className="mb-2 text-realizaBlue">{supplier.providerSupplierName}</p>
              <p className="text-sm font-semibold text-gray-700">CNPJ:</p>
              <p className="mb-2 text-gray-800">{supplier.providerSupplierCnpj}</p>
              <p className="mb-2 text-gray-800">
                {new Date(supplier.dateStart).toLocaleDateString("pt-BR")}
              </p>
              <p className="text-sm font-semibold text-gray-700">Ações:</p>
              <div className="flex gap-2">
                <button
                  title="Visualizar fornecedor"
                  onClick={() => navigate(`/sistema/fornecedor/${supplier.idProvider}`)}
                >
                  <Eye className="w-5 h-5" />
                </button>
              </div>
            </div>
          ))
        ) : (
          <p className="text-center text-gray-600">Nenhum fornecedor encontrado.</p>
        )}
      </div>

      {/* desktop */}
      <div className="hidden md:block overflow-x-auto rounded-lg border bg-white p-4 shadow-lg">
        <table className="w-full border-collapse border border-gray-300">
          <thead className="bg-gray-200">
            <tr>
              <th className="border border-gray-300 p-2 text-left">Nome do Fornecedor</th>
              <th className="border border-gray-300 p-2 text-left">CNPJ</th>
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
                  <td className="border border-gray-300 p-2">{supplier.providerSupplierName}</td>
                  <td className="border border-gray-300 p-2">{supplier.providerSupplierCnpj}</td>
                  <td className="border border-gray-300 p-2 space-x-2">
                    <button
                      title="Visualizar fornecedor"
                       onClick={() => navigate(`/sistema/fornecedor/${supplier.idProvider}`)}
                    >
                      <Eye className="w-5 h-5" />
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
    </div>
  );
}
