import { useState, useEffect } from "react";
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import { useEmployees } from "@/hooks/gets/useEmployees";
import { ButtonBlue } from "@/components/ui/buttonBlue";
import { StepOneEmployee } from "./modals/stepOne";
import { Settings2 } from "lucide-react";
import { Link } from "react-router-dom";
import { Employee } from "@/types/employee";

export const EmployeesTable = () => {
  const [currentPage, setCurrentPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const itemsPerPage = 10;

  const { employees, totalPages, loading, error, fetchEmployees } =
    useEmployees();

  useEffect(() => {
    fetchEmployees(itemsPerPage, currentPage - 1);
  }, [currentPage]);

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
        <span
          className={status === "Ativo" ? "text-green-500" : "text-red-500"}
        >
          {status}
        </span>
      ),
    },
    {
      key: "id",
      label: "Ações",
      render: (_, row) => (
        <Link to={`/detailsEmployees/${row.id}`}>
          <button className="ml-4 text-blue-500 hover:underline">
            <Settings2 />
          </button>
        </Link>
      ),
    },
  ];

  const handlePageChange = (page: number) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  const handleModalSubmit = (formData: Record<string, any>) => {
    console.log("Dados do novo funcionário:", formData);
    setIsModalOpen(false);
  };

  if (error) {
    return (
      <p className="text-center text-red-500">
        Erro ao carregar os dados de funcionários: {error}
      </p>
    );
  }

  return (
    <div className="m-4 flex justify-center">
      <div className="flex w-[90rem] flex-col rounded-lg bg-white dark:bg-primary shadow-md p-10">
        <div className="mb-6 flex items-center justify-between">
          <h1 className="mb-6 text-xl font-semibold">Colaboradores</h1>
          <ButtonBlue onClick={() => setIsModalOpen(true)}>
            Adicionar Funcionário
          </ButtonBlue>
        </div>
        {loading ? (
          <div className="flex items-center justify-center">
            <span>Carregando...</span>
          </div>
        ) : (
          <div>
            <Table<Employee> data={employees} columns={columns} />
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />
          </div>
        )}
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
