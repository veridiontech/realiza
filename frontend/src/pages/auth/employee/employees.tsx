import { useState } from "react";
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import { useEmployees } from "@/hooks/gets/useEmployees";
import { ButtonBlue } from "@/components/ui/buttonBlue";
import { StepOneEmployee } from "./modals/stepOne";
import { Settings2 } from "lucide-react";
import { Link } from "react-router-dom";
import { Employee } from "@/types/employee";

const columns: {
  key: keyof Employee;
  label: string;
  render?: (value: any, row: Employee) => JSX.Element;
  className?: string;
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
  {
    key: "id",
    label: "Ações",
    render: (row) => (
      <Link to={`/detailsEmployees/${row.id}`}>
        <button className="ml-4 text-blue-500 hover:underline">
          <Settings2 />
        </button>
      </Link>
    ),
  },
];

export const EmployeesTable = () => {
  const [currentPage, setCurrentPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const itemsPerPage = 10;

  const { data, isLoading, error } = useEmployees({
    limit: 1000,
    page: 1,
  });

  const employees = data?.data || [];
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentData = employees.slice(startIndex, startIndex + itemsPerPage);
  const totalPages = Math.ceil((employees.length || 0) / itemsPerPage);

  const handlePageChange = (page: number) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  const handleModalSubmit = (formData: Record<string, any>) => {
    console.log("Dados do novo funcionário:", formData);
    setIsModalOpen(false);
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
        <Table<Employee> data={currentData} columns={columns} />
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </div>

      {isModalOpen && (
        <StepOneEmployee
          onClose={() => setIsModalOpen(false)}
          onSubmit={handleModalSubmit}
        />
      )}
    </div>
  );
};
