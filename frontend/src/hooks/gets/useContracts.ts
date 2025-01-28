import axios from "axios";
import { useState } from "react";
import { ip } from "@/utils/ip";
import { Contract } from "@/types/contracts";

export function useContracts() {
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Função para buscar o `provider_supplier_id_provider` baseado no `clientId`
  async function fetchProviderSupplierId(clientId: string): Promise<string> {
    try {
      const response = await axios.get(`${ip}/supplier/filtered-client`, {
        params: {
          idSearch: clientId,
        },
      });

      const provider = response.data.content?.[0];
      if (!provider) {
        throw new Error("Nenhum fornecedor encontrado para o cliente.");
      }

      return provider.idProvider; // Retorna o `idProvider`
    } catch (err: any) {
      console.log("Client ID enviado para busca:", clientId);
      console.error("Erro ao buscar o fornecedor do cliente:", err);
      throw new Error("Erro ao buscar o fornecedor do cliente.");
    }
  }

  // Função para buscar os contratos usando o `provider_supplier_id_provider`
  async function fetchContracts(limit = 10, page = 0, clientId = "") {
    if (loading) return; // Evita múltiplas chamadas simultâneas
    setLoading(true);
    setError(null);

    try {
      const providerSupplierId = await fetchProviderSupplierId(clientId);

      const response = await axios.get(
        `${ip}/contract/supplier/filtered-supplier`,
        {
          params: {
            _limit: limit,
            _page: page,
            idSearch: providerSupplierId,
          },
        },
      );

      console.log("ID do fornecedor enviado para busca:", providerSupplierId);

      const { content, totalPages } = response.data;

      setContracts(
        content.map((contract: any) => ({
          id: contract.id || "",
          ref: contract.ref || "",
          project: contract.project || "",
          clientFinal: contract.clientFinal || "",
          client: contract.client || "",
          providerSupplierName: contract.providerSupplierName || "",
          serviceType: contract.serviceType || "",
          serviceDuration: contract.serviceDuration || "",
          serviceName: contract.serviceName || "",
          contractReference: contract.contractReference || "",
          description: contract.description || "",
          allocatedLimit: contract.allocatedLimit || 0,
          responsible: contract.responsible || "",
          expenseType: contract.expenseType || "",
          startDate: contract.startDate || "",
          endDate: contract.endDate || "",
          activities: contract.activities || [],
          requirements: contract.requirements || [],
        })),
      );
      setTotalPages(totalPages || 1);
    } catch (err: any) {
      console.error("Erro ao buscar contratos:", err);
      setError(err.response?.data?.message || "Erro ao buscar contratos.");
    } finally {
      setLoading(false);
    }
  }

  function resetContracts() {
    setContracts([]);
    setTotalPages(1);
    setError(null);
  }

  return {
    contracts,
    totalPages,
    loading,
    error,
    fetchContracts,
    resetContracts,
  };
}
