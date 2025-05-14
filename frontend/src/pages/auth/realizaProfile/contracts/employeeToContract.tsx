import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useClient } from "@/context/Client-Provider";
import { useEmployees } from "@/hooks/gets/realiza/useEmployees";
import axios from "axios";
import { ip } from "@/utils/ip";
import { Employee } from "@/types/employee";

export default function EmployeeToContract() {
  const { contractId } = useParams<{ contractId?: string }>();
  const { client } = useClient();
  const { employees, fetchEmployees } = useEmployees();
  const [updating, setUpdating] = useState(false);

  // Verificar se o contractId foi passado corretamente
  useEffect(() => {
    if (!contractId) {
      console.error("Nenhum ID de contrato foi passado na URL.");
      return;
    }
    if (client?.idClient) {
      fetchEmployees(10, 0, "CLIENT", client.idClient);
    }
  }, [contractId, client?.idClient]);

  // Função para associar o funcionário ao contrato
  const handleAssignEmployee = async (employee: Employee) => {
    if (!contractId) {
      alert("Erro: Nenhum contrato selecionado.");
      return;
    }
    setUpdating(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      console.log(
        `Associando funcionário ${employee.id} ao contrato ${contractId}`,
      );
      const updatedEmployee = {
        ...employee,
        idContracts: [...(employee.idContracts ?? []), contractId],
      };

      await axios.put(
        `${ip}/employee/brazilian/${employee.id}`,
        updatedEmployee, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      }

      );
      alert("Funcionário associado ao contrato com sucesso!");
    } catch (error) {
      console.error("Erro ao associar funcionário:", error);
      alert("Erro ao associar funcionário ao contrato.");
    } finally {
      setUpdating(false);
    }
  };

  return (
    <div className="relative flex h-screen w-full items-center justify-center bg-gray-100">
      <h1 className="absolute left-16 top-20 text-xl font-bold">
        Alocar Funcionários
      </h1>

      <div className="flex w-4/5 overflow-hidden rounded-lg bg-white shadow-lg">
        {/* Lista de funcionários */}
        <div className="flex w-1/2 flex-col bg-cyan-300 p-6">
          <h2 className="mb-4 text-lg font-semibold">
            Funcionários Disponíveis
          </h2>

          {employees.length > 0 ? (
            employees.map((emp) => (
              <div
                key={emp.id}
                className="mb-2 flex items-center rounded-lg bg-white p-4 shadow-md"
              >
                <div className="ml-4">
                  <p className="font-medium text-gray-800">{emp.name}</p>
                </div>
                <button
                  onClick={() => handleAssignEmployee(emp)}
                  className="ml-auto text-cyan-600 hover:text-cyan-800"
                  disabled={updating}
                >
                  {updating ? "Atribuindo..." : "➡️"}
                </button>
              </div>
            ))
          ) : (
            <p className="text-gray-500">Nenhum funcionário disponível.</p>
          )}
        </div>

        {/* Detalhes do Contrato */}
        <div className="flex-1 bg-gray-50 p-6">
          <h2 className="mb-4 text-lg font-semibold">Detalhes do Contrato</h2>
          <p>
            <strong>ID do Contrato:</strong>{" "}
            {contractId ?? "Nenhum contrato selecionado"}
          </p>
          <p>
            <strong>Cliente:</strong>{" "}
            {client?.companyName ?? "Nenhum cliente selecionado"}
          </p>
        </div>
      </div>
    </div>
  );
}
