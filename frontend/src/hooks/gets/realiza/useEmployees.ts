import axios from "axios";
import { useState } from "react";
import { Employee } from "@/types/employee";
import { ip } from "@/utils/ip";

const API_URL = `${ip}/employee`;

export function useEmployees() {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function fetchEmployees(
    limit = 10,
    page = 0,
    idEnterprise = "CLIENT",
    idSearch = "",
  ) {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(API_URL, {
        params: { size: limit, page, idEnterprise, idSearch },
      });

      const { content, totalPages } = response.data;
      setEmployees(
        content.map((emp: any) => ({
          id: emp.idEmployee,
          name: emp.name,
          status: emp.isActive ? "Ativo" : "Desligado",
        })),
      );
      setTotalPages(totalPages);
    } catch (err: any) {
      setError(err.message || "Erro ao carregar Colaboradores.");
    } finally {
      setLoading(false);
    }
  }

  return {
    employees,
    totalPages,
    loading,
    error,
    fetchEmployees,
  };
}
