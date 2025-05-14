import axios from "axios";
import { useState } from "react";
import { Employee } from "@/types/employee";
import { ip } from "@/utils/ip";
import { useUser } from "@/context/user-provider";

const API_URL = `${ip}/employee`;

export function clientGetEmployee() {
  const { user } = useUser();
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function fetchEmployees(limit = 10, page = 0, enterprise = "CLIENT") {
    setLoading(true);
    setError(null);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const branchId = user?.branch || "";
      const response = await axios.get(API_URL, {
        params: { size: limit, page, enterprise, idSearch: branchId },
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
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
