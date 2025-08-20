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

  async function fetchEmployees(
    limit = 10,
    page = 0,
    idSearch: string | null = null,
    enterprise: "CLIENT" | "PROVIDER" | "SUBCONTRACTOR"
  ) {
    setLoading(true);
    setError(null);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const branchId = user?.branch || "";

      const params: any = {
        size: limit,
        page,
      };

      if (enterprise === "PROVIDER" || enterprise === "SUBCONTRACTOR") {
        params.enterprise = enterprise;
        params.idSearch = idSearch;
      } else {
        params.idSearch = branchId;
      }

      const response = await axios.get(API_URL, {
        params,
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
      setError(err.response?.data?.message || "Erro ao carregar Colaboradores.");
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