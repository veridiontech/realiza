import { useState } from "react";
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import { useEmployees } from "@/hooks/useEmployees";
import { ButtonBlue } from "@/components/ui/buttonBlue";
import { StepOneEmployee } from "./modals/stepOne"; // Certifique-se de ajustar o caminho para onde o modal está localizado

type Employee = {
  name: string;
  status: "Ativo" | "Desligado";
};

const columns: {
  key: keyof Employee;
  label: string;
  render?: (value: any, row: Employee) => JSX.Element;
}[] = [
  { key: "name", label: "Nome" },
  {
    key: "status",
    label: "Status",
    render: (status) => (
      <span className={status === "Ativo" ? "text-green-500" : "text-red-500"}>
        {status}
      </span>
    ),
  },
];

export const EmployeesTable = () => {
  const [currentPage, setCurrentPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false); // Estado para controlar o modal
  const itemsPerPage = 10;

  const { data, isLoading, error } = useEmployees({
    limit: itemsPerPage,
    page: currentPage,
  });

  
  const employees = data?.data || [];
  const total = data?.total || 0;
  const totalPages = Math.ceil(total / itemsPerPage);

  const handlePageChange = (page: number) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  const handleModalSubmit = (formData: Record<string, any>) => {
    console.log("Dados do novo funcionário:", formData);
    setIsModalOpen(false); // Fecha o modal após o envio
  };

  if (isLoading) {
    return <p className="text-center">Carregando...</p>;
  }

  if (error) {
    return (
      <p className="text-center text-red-500">
        Erro ao carregar os dados de funcionários.
      </p>
    );
  }

  return (
    <div className="m-4 flex justify-center">
      <div className="flex w-[90rem] flex-col rounded-lg bg-white p-4 shadow-md">
        <div className="mb-6 flex items-center justify-between">
          <h1 className="mb-6 text-xl font-semibold">Funcionários</h1>
          <ButtonBlue onClick={() => setIsModalOpen(true)}>
            Adicionar Funcionário
          </ButtonBlue>
        </div>
        <Table<Employee> data={employees} columns={columns} />
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </div>

      {/* Modal de Cadastro de Funcionário */}
      {isModalOpen && (
        <StepOneEmployee
          onClose={() => setIsModalOpen(false)} // Fecha o modal
          onSubmit={handleModalSubmit} // Lida com o envio do formulário
        />
      )}
    </div>
  );
};
