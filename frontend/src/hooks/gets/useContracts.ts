import axios from "axios";
import { useState, useEffect } from "react";
import { ip } from "@/utils/ip";

interface Contract {
  id: string;
  ref: string;
  project: string;
  supplier: string;
  startDate: string;
  endDate: string;
  serviceName: string; // Certifique-se de mapear o campo corretamente
}

interface UseContractsProps {
  limit?: number;
  page?: number;
}

export function useContracts({ limit = 10, page = 1 }: UseContractsProps) {
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  async function fetchContracts() {
    setLoading(true);
    setError(null);

    try {
      const response = await axios.get(
        `${ip}/contract/supplier?page=${page - 1}&size=${limit}`,
      );
      console.log(response.data);
      // Mapeando os dados para garantir o mapeamento correto do service_name
      const mappedContracts = response.data.content.map((contract: any) => ({
        id: contract.id || "",
        ref: contract.ref || "",
        project: contract.project || "",
        supplier: contract.supplier || "",
        startDate: contract.startDate || "",
        endDate: contract.endDate || "",
        serviceName: contract.serviceName || "", // Certifique-se de capturar service_name
      }));

      setContracts(mappedContracts);
      setTotalPages(response.data.totalPages);
    } catch (err: any) {
      setError(err.message || "Erro ao buscar contratos.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchContracts();
  }, [page, limit]);

  return { contracts, totalPages, error, loading };
}
