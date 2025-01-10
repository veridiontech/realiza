import { useQuery } from "@tanstack/react-query";

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

export function useContracts({ limit = 1000, page = 1 }: UseContractsProps) {
  return useQuery<Contract[]>({
    queryKey: ["Contracts", limit, page],
    queryFn: async () => {
      const response = await fetch(
        `http://localhost:3001/Contracts?_limit=${limit}&_page=${page}`,
      );
      if (!response.ok) {
        throw new Error("Erro ao carregar os dados de contratos");
      }
      return response.json();
    },
    staleTime: 5 * 60 * 1000,
    placeholderData: (previousData) => previousData || [],
  });
}
