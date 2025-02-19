// src/components/SupplierEmployee.tsx
import { useState, useEffect } from "react";
import { Table } from "@/components/ui/tableVanila";
import { Pagination } from "@/components/ui/pagination";
import { SupplierGetEmployee } from "@/hooks/gets/supplier/supplierGetEmployee";
import { ButtonBlue } from "@/components/ui/buttonBlue";
import { SupplierAddEmployee } from "./modal/supplierAddEmployee";
import { Settings2 } from "lucide-react";
import { Link } from "react-router-dom";
import { Employee } from "@/types/employee";
import { useClient } from "@/context/Client-Provider";

export const SupplierEmployee = (): JSX.Element => {
  const [currentPage, setCurrentPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const itemsPerPage = 10;

  const { client } = useClient();

  const { employees, totalPages, loading, error, fetchEmployees } =
    SupplierGetEmployee();

  useEffect(() => {
    // Se existir client.idClient, usa enterprise "CLIENT", senão usa "SUPPLIER"
    if (client?.idClient) {
      fetchEmployees(itemsPerPage, currentPage - 1, "CLIENT");
    } else {
      fetchEmployees(itemsPerPage, currentPage - 1, "SUPPLIER");
    }
  }, [currentPage, client?.idClient]);

  const handlePageChange = (page: number) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  const handleModalSubmit = (formData: Record<string, any>) => {
    console.log("Dados do novo colaborador:", formData);
    setIsModalOpen(false);
  };

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
        <Link to={`/sistema/detailsEmployees/${row.id}`}>
          <button className="text-realizaBlue ml-4 hover:underline">
            <Settings2 />
          </button>
        </Link>
      ),
    },
  ];

  if (error) {
    return (
      <p className="text-center text-red-500">
        Erro ao carregar os dados de Colaborador: {error}
      </p>
    );
  }

  return (
    <div className="m-4 flex justify-center">
      <div className="dark:bg-primary flex w-[90rem] flex-col rounded-lg bg-white p-10 shadow-md">
        <div className="mb-6 flex items-center justify-between">
          <h1 className="mb-6 text-xl font-semibold">Colaboradores</h1>
          <ButtonBlue onClick={() => setIsModalOpen(true)}>
            Adicionar Colaborador
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
        <SupplierAddEmployee
          onClose={() => setIsModalOpen(false)}
          onSubmit={handleModalSubmit}
        />
      )}
    </div>
  );
};
