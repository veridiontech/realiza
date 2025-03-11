import axios from "axios";
import { useState } from "react";
import { ip } from "@/utils/ip";
import { Contract } from "@/types/contracts";
import { useBranch } from "@/context/Branch-provider";

export function useContracts() {
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const {selectedBranch} = useBranch()

  // Função para buscar o `provider_supplier_id_provider` baseado no `clientId`
  async function fetchProviderSupplierId(clientId: string): Promise<string | null> {
    try {
      if (!clientId) {
        throw new Error("ID do cliente não fornecido.");
      }
      if (!selectedBranch?.idBranch) {
        console.error("Erro: Nenhuma filial selecionada.");
        return null; // Retorna null para evitar erro na requisição
      }
  
      console.log("Buscando fornecedor para clientId:", clientId, "Filial:", selectedBranch?.idBranch);
  
      const response = await axios.get(`${ip}/contract/supplier/filtered-client`, {
        params: { idSearch: selectedBranch.idBranch },
      });
  
      console.log("Resposta da API fornecedores:", response.data);
  
      const provider = response.data.content?.[0];
      if (!provider || !provider.idProvider) {
        console.warn("Nenhum fornecedor encontrado para o cliente.");
        return null;
      }
  
      console.log("contratos", contracts);

      return provider.idProvider;
    } catch (err: any) {
      console.error("Erro ao buscar o fornecedor do cliente:", err);
      return null;
    }
  }
  

  // Função para buscar os contratos usando o `provider_supplier_id_provider`
  async function fetchContracts(limit = 10, page = 0, clientId = "") {
    if (loading) {
      console.warn("fetchContracts chamado enquanto ainda está carregando.");
      return;
    }
  
    if (!clientId) {
      console.error("Erro: clientId não pode ser vazio.");
      setError("ID do cliente inválido.");
      return;
    }
  
    setLoading(true);
    setError(null);
  
    try {
      const providerSupplierId = await fetchProviderSupplierId(clientId);
  
      if (!providerSupplierId) {
        console.warn("Nenhum fornecedor encontrado. A busca de contratos foi interrompida.");
        setContracts([]); // Garante que a tabela fique vazia caso não tenha fornecedor
        return;
      }
  
      console.log("ID do fornecedor enviado para busca:", providerSupplierId);
  
      const response = await axios.get(`${ip}/contract/supplier/filtered-supplier`, {
        params: {
          _limit: limit,
          _page: page,
          idSearch: providerSupplierId,
        },
      });
  
      console.log("Resposta da API contratos:", response.data);
  
      // ✅ Verifica se `response.data.content` é uma lista antes de mapear
      if (!response.data.content || !Array.isArray(response.data.content)) {
        console.warn("Nenhum contrato encontrado ou formato inesperado.");
        setContracts([]);
        setTotalPages(1);
        return;
      }
  
      const { content, totalPages } = response.data;
  
      setContracts(
        content.map((contract: any) => ({
          id: contract.id ?? contract.idContract ?? "",
          contractReference: contract.contractReference || "",
          serviceType: contract.serviceType || "",
          serviceDuration: contract.serviceDuration || "",
          serviceName: contract.serviceName || "",
          description: contract.description || "",
          allocatedLimit: contract.allocatedLimit || 0,
          expenseType: contract.expenseType || "",
          startDate: contract.startDate || "",
          endDate: contract.endDate || "",
          activities: contract.activities || [],
        }))
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
