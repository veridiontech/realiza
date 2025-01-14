import { useQuery } from "@tanstack/react-query";

interface Employee {
  name: string;
  status: "Ativo" | "Desligado";
}

interface UseEmployeesProps {
  limit?: number;
  page?: number;
}

export function useEmployees({ limit = 10, page = 1 }: UseEmployeesProps) {
  return useQuery<{
    data: Employee[];
    total: number;
  }>({
    queryKey: ["Employees", limit, page],
    queryFn: async () => {
      const response = await fetch(
        `http://localhost:3001/Employees?_limit=${limit}&_page=${page}`,
      );
      if (!response.ok) {
        throw new Error("Erro ao carregar os dados de funcionários");
      }

      const totalResponse = await fetch("http://localhost:3001/Employees");
      if (!totalResponse.ok) {
        throw new Error("Erro ao carregar o total de funcionários");
      }

      const total = (await totalResponse.json()).length;
      const data = await response.json();

      return {
        data: data.map((employee: any) => ({
          name: employee.name,
          status: employee.status || "Ativo",
        })),
        total,
      };
    },
    staleTime: 5 * 60 * 1000,
    placeholderData: { data: [], total: 0 },
  });
}
