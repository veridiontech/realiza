// import { Table } from "@/components/ui/tableVanila";
// import { Pagination } from "@/components/ui/pagination";
// import { useEmployees } from "@/hooks/gets/realiza/useEmployees";
// import { StepOneEmployee } from "./modals/stepOne";
// import { Settings2 } from "lucide-react";
// import { Link } from "react-router-dom";
// import { Employee } from "@/types/employee";
// import { useClient } from "@/context/Client-Provider";
import { useState} from "react";
import { TableEmployee } from "./tableEmployee";
import { NewModalCreateEmployee } from "./modals/newModalCreateEmployee";
import { Button } from "@/components/ui/button";


export const EmployeesTable = (): JSX.Element => {
  const [selectedTab, setSelectedTab] = useState("colaboradores")

  // const [currentPage, setCurrentPage] = useState(1);
  // const [, setIsModalOpen] = useState(false);
  // const itemsPerPage = 10;

  // const { client } = useClient()



  return (
    <div className="m-4 flex justify-center">
      <div className="dark:bg-primary flex w-[90rem] flex-col rounded-lg bg-white p-10 shadow-md">
        <div className="mb-6 flex items-center justify-between">
          <h1 className="text-2xl">Colaboradores</h1>
          <NewModalCreateEmployee />
        </div>
        <div className="mb-4 flex border-b">
          <Button
            variant="ghost"
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "fornecedor"
                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                : "text-realizaBlue bg-white"
            }`}
            onClick={() => setSelectedTab("fornecedor")}
          >
            Fornecedor
          </Button>
          <Button
            variant="ghost"
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "subcontratado"
                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                : "text-realizaBlue bg-white"
            }`}
            onClick={() => setSelectedTab("subcontratado")}
          >
            Subcontratado
          </Button>
        </div>
        <div>
          {selectedTab === "fornecedor" && <TableEmployee />}
          {selectedTab === "subcontratado" && (
            <div className="text-center text-gray-600">
              Lista de colaboradores será exibida aqui.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
