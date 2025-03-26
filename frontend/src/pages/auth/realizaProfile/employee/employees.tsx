// import { Table } from "@/components/ui/tableVanila";
// import { Pagination } from "@/components/ui/pagination";
// import { useEmployees } from "@/hooks/gets/realiza/useEmployees";
// import { StepOneEmployee } from "./modals/stepOne";
// import { Settings2 } from "lucide-react";
// import { Link } from "react-router-dom";
// import { Employee } from "@/types/employee";
// import { useClient } from "@/context/Client-Provider";
import { useState, useEffect} from "react";
import { TableEmployee } from "./tableEmployee";
import { NewModalCreateEmployee } from "./modals/newModalCreateEmployee";
import { Button } from "@/components/ui/button";
import { Pagination } from "@/components/ui/pagination";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";


export const EmployeesTable = (): JSX.Element => {
  const [selectedTab, setSelectedTab] = useState("fornecedor");
  const [, setEmployees] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { client } = useClient();
  const [selectedSupplier, setSelectedSupplier] = useState<string | null>(null);
  const [suppliersList, setSuppliersList] = useState<{ id: string; name: string }[]>([]);

  const fetchEmployees = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(`${ip}/employee/filtered-client?idSearch=${client?.idClient}&page=${currentPage}`);
      const { content, totalPages: total } = response.data;
      setEmployees(content);
      setTotalPages(total);
    } catch (err) {
      if (axios.isAxiosError(err) && err.response) {
        console.error("Erro ao buscar employees", err.response.data);
      }
      console.error("Erro ao buscar colaboradores:", err);
      setError("Erro ao buscar colaboradores. Tente novamente.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (client?.idClient) {
      fetchEmployees();
    }
  }, [client?.idClient, currentPage]);

  const handlePageChange = (page: number) => {
    if (page >= 0 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  const suppliers = async () => {
    try {
      const res = await axios.get(`${ip}/suppliers`);
      setSuppliersList(res.data);
    } catch (error) {
      console.error("Erro ao encontrar fornecedor:", error);
    }
  };

  useEffect(() => {
    suppliers();
  }, []);

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
              selectedTab === "fornecedor" ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg" : "text-realizaBlue bg-white"
            }`}
            onClick={() => setSelectedTab("fornecedor")}
          >
            Fornecedor
          </Button>
          <Button
            variant="ghost"
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "subcontratado" ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg" : "text-realizaBlue bg-white"
            }`}
            onClick={() => setSelectedTab("subcontratado")}
          >
            Subcontratado
          </Button>
        </div>
        <div>
          {loading ? (
            <div className="text-center text-gray-600">Carregando colaboradores...</div>
          ) : error ? (
            <div className="text-center text-red-500">{error}</div>
          ) : selectedTab === "fornecedor" ? (
            <div>
              <h2 className="text-xl mb-4">Selecione um Fornecedor</h2>
              <div>
                <span className="text-realizaBlue text-[14px]">Fornecedor: </span>
                <select
                  value={selectedSupplier || ""}
                  onChange={(e) => setSelectedSupplier(e.target.value)}
                  className="text-[12px] p-2 border rounded w-full"
                >
                  <option value="">Selecione um fornecedor</option>
                  {suppliersList.map((supplier) => (
                    <option key={supplier.id} value={supplier.id}>
                      {supplier.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          ) : (
            <div className="text-center text-gray-600">
              Lista de colaboradores será exibida aqui.
            </div>
          )}
        </div>
        <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={handlePageChange} />
      </div>
    </div>
  );
};