// src/hooks/gets/supplier/supplierGetEmployee.ts
import axios from "axios";
import { useState } from "react";
import { ip } from "@/utils/ip";
import { useUser } from "@/context/user-provider";
import { useClient } from "@/context/Client-Provider";
import { Employee } from "@/types/employee"; // Interface padrão

const API_URL = `${ip}/employee`;

export function SupplierGetEmployee() {
  const { user } = useUser();
  const { client } = useClient();
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  async function fetchEmployees(limit = 10, page = 0, enterprise = "SUPPLIER") {
    setLoading(true);
    setError(null);
    let idSearch = "";
    if (enterprise === "CLIENT") {
      idSearch = client?.idClient || "";
    } else {
      idSearch = user?.supplier || user?.idUser || "";
    }
    console.log("fetchEmployees params:", {
      limit,
      page,
      enterprise,
      idSearch,
    });
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.get(API_URL, {
        params: {
          size: limit,
          page,
          enterprise,
          idSearch,
        },
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      });
      console.log("fetchEmployees response:", response.data);
      const { content, totalPages } = response.data;
      const mappedEmployees: Employee[] = content.map((emp: any) => ({
        id: Number(emp.idEmployee),
        name: emp.name,
        status: emp.situation || "Ativo",
      }));
      setEmployees(mappedEmployees);
      setTotalPages(totalPages);
    } catch (err: any) {
      console.error("Error fetching employees:", err);
      setError(err.message || "Erro ao carregar colaboradores.");
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
