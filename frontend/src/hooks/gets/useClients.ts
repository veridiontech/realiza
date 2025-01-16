import { useQuery } from "@tanstack/react-query";
import axios from "axios";

interface Client {
  id: number;
  name: string;
}

export function useClients() {
  return useQuery<Client[]>({
    queryKey: ["clients"],
    queryFn: async () => {
      const response = await axios.get("http://localhost:3001/api/clients");
      return response.data;
    },
    staleTime: 5 * 60 * 1000,
  });
}
