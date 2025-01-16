import { useQuery } from "@tanstack/react-query";

interface ServiceProviders {
  id: number;
  category: string;
  corporateReason: string;
  enterprise: string;
  cnpj: string;
  units: string;
  options: JSX.Element;
}

interface UseServiceProvidersProps {
  limit?: number;
  page?: number;
}

export function useServiceProviders({
  limit = 1000,
  page = 1,
}: UseServiceProvidersProps) {
  return useQuery<ServiceProviders[]>({
    queryKey: ["ServiceProviders", limit, page],
    queryFn: async () => {
      const response = await fetch(
        "http://localhost:3001/ServiceProviders?_limit=${limit}&_page=${page}",
      );
      if (!response.ok) {
        throw new Error("Erro ao carregar os dados");
      }
      return response.json();
    },
    staleTime: 5 * 60 * 1000,
    placeholderData: (previousData) => previousData || [],
  });
}
