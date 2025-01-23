import { useState, useEffect } from "react";
import axios from "axios";
import { ButtonBlue } from "@/components/ui/buttonBlue";
import { ip } from "@/utils/ip";
import { AddDocument } from "./modals/addDocument";
import { useParams } from "react-router-dom";

interface Employee {
  id: string;
  name: string;
  status: string;
}

export function DetailsEmployee() {
  const { id } = useParams<{ id: string }>(); // Obtém o ID da URL
  const [employee, setEmployee] = useState<Employee | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const fetchEmployee = async () => {
    setIsLoading(true);
    try {
      const response = await axios.get(`${ip}/employee/brazilian/${id}`);
      const data = response.data;
      setEmployee({
        id: data.idEmployee,
        name: data.name,
        status: data.isActive ? "Ativo" : "Desligado",
      });
    } catch (err: any) {
      setError(
        err.response?.data?.message || "Erro ao carregar o Colaborador.",
      );
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchEmployee();
  }, [id]);

  if (isLoading) {
    return <p>Carregando...</p>;
  }

  if (error) {
    return <p className="text-red-500">Erro: {error}</p>;
  }

  if (!employee) {
    return <p>Colaborador não encontrado.</p>;
  }

  return (
    <div className="flex h-full w-full flex-col bg-gray-100 p-10">
      <div className="mb-6 flex items-center">
        <h1 className="text-xl font-bold">Detalhes do Colaborador</h1>
      </div>
      <div className="flex gap-6">
        <div className="w-full">
          <div className="rounded-lg bg-white p-6 shadow">
            <div className="mb-4 flex flex-col items-center">
              <div className="mb-4 h-20 w-20 rounded-full bg-blue-100"></div>
              <h3 className="text-lg font-medium">{employee.name}</h3>
              <p className="text-sm text-gray-500">Status: {employee.status}</p>
            </div>
            <ButtonBlue onClick={() => setIsModalOpen(true)}>
              Adicionar Documento
            </ButtonBlue>
          </div>
        </div>
      </div>

      {isModalOpen && employee && (
        <AddDocument
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          employeeId={employee.id}
        />
      )}
    </div>
  );
}
