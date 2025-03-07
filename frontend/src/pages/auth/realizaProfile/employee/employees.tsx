import { useState} from "react";
// import { Table } from "@/components/ui/tableVanila";
// import { Pagination } from "@/components/ui/pagination";
// import { useEmployees } from "@/hooks/gets/realiza/useEmployees";
import { StepOneEmployee } from "./modals/stepOne";
// import { Settings2 } from "lucide-react";
// import { Link } from "react-router-dom";
// import { Employee } from "@/types/employee";
// import { useClient } from "@/context/Client-Provider";
import { TableEmployee } from "./tableEmployee";

export const EmployeesTable = (): JSX.Element => {
  // const [currentPage, setCurrentPage] = useState(1);
  const [, setIsModalOpen] = useState(false);
  // const itemsPerPage = 10;

  // const { client } = useClient();

 

 



  const handleModalSubmit = (formData: Record<string, any>) => {
    console.log("Dados do novo colaborador:", formData);
    setIsModalOpen(false);
  };



  return (
    <div className="m-4 flex justify-center">
      <div className="dark:bg-primary flex w-[90rem] flex-col rounded-lg bg-white p-10 shadow-md">
        <div className="mb-6 flex items-center justify-between">
          <h1 className="text-2xl">Colaboradores</h1>

          <StepOneEmployee
            onClose={() => setIsModalOpen(false)}
            onSubmit={handleModalSubmit}
          />
        </div>

          <div>
            <TableEmployee />

          </div>

      </div>
    </div>
  );
};
