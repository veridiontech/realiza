import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState } from "react";
import { User } from "lucide-react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { ip } from "@/utils/ip";

export function TableProviders() {
  const [suppliers, setSuppliers] = useState<any[]>([]);
  const [filteredSuppliers, setFilteredSuppliers] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const { selectedBranch } = useBranch();
  const navigate = useNavigate();

  const [serviceTypes, setServiceTypes] = useState<string[]>([]);
  const [selectedService, setSelectedService] = useState<string>("");

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

        const suppliersList = response.data.content || [];
        setSuppliers(suppliersList);

        // Extrair e setar tipos de serviço únicos
        const types = new Set<string>();
        suppliersList.forEach((s: any) => {
          s.serviceTypes?.forEach((t: any) => types.add(t?.name));
        });
        setServiceTypes(Array.from(types));

      } catch (error) {
        console.error("Erro ao buscar fornecedores:", error);
        setSuppliers([]);
        setServiceTypes([]);
      } finally {
        setLoading(false);
      }
    };

    if (selectedBranch?.idBranch) {
      fetchSuppliers();
    }
  }, [selectedBranch]);

  useEffect(() => {
    if (!selectedService) {
      setFilteredSuppliers(suppliers);
    } else {
      setFilteredSuppliers(
        suppliers.filter((s: any) =>
          s.serviceTypes?.some((t: any) => t?.name === selectedService)
        )
      );
    }
  }, [selectedService, suppliers]);

  return (
    <div className="p-4 pt-0 md:p-4 md:pt-0">
      <h2 className="mb-4 flex items-center gap-2 text-xl font-semibold text-[#34495E] dark:text-white">
        <User className="text-[#C0B15B] w-5 h-5" />
        Todos os Fornecedores
      </h2>

      {/* Filtro por tipo de serviço */}
      <div className="mb-4 flex flex-wrap gap-2">
        <select
          value={selectedService}
          onChange={(e) => setSelectedService(e.target.value)}
          className="rounded border px-3 py-2 text-sm"
        >
          <option value="">Todos os tipos de serviço</option>
          {serviceTypes.map((type) => (
            <option key={type} value={type}>
              {type}
            </option>
          ))}
        </select>
      </div>

      {/* Tabela */}
      <div className="hidden md:block min-h-[60vh] rounded-lg bg-white p-6 shadow-lg">
        <table className="w-full border-collapse text-left text-sm text-gray-700">
          <thead className="bg-[#DDE3DC] text-gray-700">
            <tr>
              <th className="px-4 py-3 font-semibold">Nome do Fornecedor</th>
              <th className="px-4 py-3 font-semibold">CNPJ | NIF</th>
              <th className="px-4 py-3 font-semibold">Tipo(s) de Serviço</th>
              <th className="px-4 py-3 font-semibold text-center">Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="px-4 py-4 text-center text-gray-500">
                  Carregando...
                </td>
              </tr>
            ) : filteredSuppliers.length > 0 ? (
              filteredSuppliers.map((supplier: any, index: number) => (
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
                  <td className="px-4 py-3">
                    {supplier.serviceTypes?.map((s: any, i: number) => (
                      <span key={i} className="mr-1 inline-block rounded bg-[#E5E7EB] px-2 py-0.5 text-xs text-gray-700">
                        {s?.name}
                      </span>
                    ))}
                  </td>
                  <td className="px-4 py-3 text-center">
                    <button
                      onClick={() => navigate(`/sistema/fornecedor/${supplier.idProvider}`)}
                      className="flex items-center gap-2 rounded-md bg-[#34495E] px-4 py-1.5 text-xs font-medium text-white hover:bg-[#1c1c6b] transition"
                    >
                      Visualizar perfil
                      <span className="text-xs">›</span>
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={4} className="px-4 py-4 text-center text-gray-500">
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
