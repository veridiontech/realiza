import axios from "axios";
import { useState, useEffect } from "react";
import { ip } from "@/utils/ip";

interface Contract {
  id: string;
  ref: string;
  project: string;
  clientFinal: string;
  client: string;
  startDate: string;
  endDate: string;
}

interface UseContractsProps {
  limit?: number;
  page?: number;
}

export function useContracts({ limit = 10, page = 1 }: UseContractsProps) {
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [totalPages, setTotalPages] = useState(0); // Total de páginas para paginação
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  async function fetchContracts() {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(
        `${ip}/contract/supplier?page=${page - 1}&size=${limit}`,
      );
      setContracts(response.data.content); // Dados retornados no campo `content`
      setTotalPages(response.data.totalPages); // Total de páginas retornado pela API
    } catch (err: any) {
      setError(err.message || "Erro ao buscar contratos.");
    } finally {
      setLoading(false);
    }
  }

  // Chama o fetch ao carregar ou mudar a página
  useEffect(() => {
    fetchContracts();
  }, [page, limit]); // Dependências garantem que o fetch será chamado ao mudar `page` ou `limit`

  return { contracts, totalPages, error, loading };
}
