import { useState } from "react";
import { Table } from "@/components/ui/table";
import { useEmployees } from "@/hooks/useEmployees";
import { Pagination } from "@/components/ui/pagination";

// Defina o tipo Employee
type Employee = {
  name: string;
  avatarUrl: string;
};

// Defina as colunas com o tipo correto
const columns: {
  key: keyof Employee;
  label: string;
  render?: (value: any) => JSX.Element;
}[] = [
  {
    key: "avatarUrl",
    label: "",
    render: (avatarUrl) => (
      <img
        src={avatarUrl}
        alt="User Avatar"
        className="h-10 w-10 rounded-full object-cover"
      />
    ),
  },
  {
    key: "name",
    label: "Name",
  },
];

export const EmployeesTable = () => {
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  const {
    data: { data: employees = [], total = 0 } = {},
    isLoading,
    error,
  } = useEmployees({
    limit: itemsPerPage,
    page: currentPage,
  });

  const totalPages = Math.ceil(total / itemsPerPage);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  if (isLoading) {
    return <div className="text-center">Loading...</div>;
  }

  if (error) {
    return (
      <div className="text-center text-red-500">Error loading employees</div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-6">
      <h1 className="mb-4 text-center text-xl font-bold">Employee Table</h1>
      <div className="overflow-x-auto">
        <Table data={employees} columns={columns} />
      </div>
      <div className="mt-4 flex justify-center">
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </div>
    </div>
  );
};
