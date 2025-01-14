import { useQuery } from "@tanstack/react-query";

interface Employee {
  name: string;
  avatarUrl: string;
}

interface UseEmployeesProps {
  limit?: number;
  page?: number;
}

export function useEmployees({ limit = 1000, page = 1 }: UseEmployeesProps) {
  return useQuery({
    queryKey: ["Employees", limit, page],
    queryFn: async () => {
      const response = await fetch(
        `http://localhost:3001/employees?_limit=${limit}&_page=${page}`,
      );
      if (!response.ok) {
        throw new Error("Erro ao carregar os dados de funcionários");
      }
      const total = response.headers.get("X-Total-Count");
      const data = await response.json();
      return { data, total: total ? parseInt(total, 10) : 0 };
    },
    staleTime: 5 * 60 * 1000,
    placeholderData: () => ({ data: [], total: 0 }),
  });
}
