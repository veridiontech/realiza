import { useQuery } from "@tanstack/react-query";

interface Client {
  id: number;
  name: string;
}

export function useClients() {
  return useQuery<Client[]>({
    queryKey: ["clients"],
    queryFn: async () => {
      const response = await fetch("http://localhost:3001/api/clients");
      if (!response.ok) {
        throw new Error("Erro ao carregar os dados");
      }
      return response.json();
    },
    staleTime: 5 * 60 * 1000,
  });
}
