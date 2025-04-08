// import { Table } from "@/components/ui/tableVanila";
// import { Pagination } from "@/components/ui/pagination";
// import { useEmployees } from "@/hooks/gets/realiza/useEmployees";
// import { StepOneEmployee } from "./modals/stepOne";
// import { Settings2 } from "lucide-react";
// import { Link } from "react-router-dom";
// import { Employee } from "@/types/employee";
// import { useClient } from "@/context/Client-Provider";
import { useState, useEffect } from "react";
// import { TableEmployee } from "./tableEmployee";
// import { NewModalCreateEmployee } from "./modals/newModalCreateEmployee";
import { Button } from "@/components/ui/button";
// import { Pagination } from "@/components/ui/pagination";
import axios from "axios";
import { ip } from "@/utils/ip";
// import { useClient } from "@/context/Client-Provider";
import { propsSupplier } from "@/types/interfaces";
import { TableEmployee } from "./tableEmployee";
import { useBranch } from "@/context/Branch-provider";
import { useSupplier } from "@/context/Supplier-context";
import { useUser } from "@/context/user-provider";
import { NewModalCreateEmployee } from "./modals/newModalCreateEmployee";

export const EmployeesTable = (): JSX.Element => {
  const [selectedTab, setSelectedTab] = useState("fornecedor");
  // const [employee, setEmployees] = useState([]);
  // const [totalPages, setTotalPages] = useState(1);
  // const [currentPage, setCurrentPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error] = useState<string | null>(null);
  // const { client } = useClient();
  const { selectedBranch } = useBranch();
  const [selectedSupplier, setSelectedSupplier] = useState<string | null>(null);
  const [suppliersList, setSuppliersList] = useState<propsSupplier[]>([]);
  const [getUniqueSupplier, setGetUniqueSupplier] =
    useState<propsSupplier | null>(null);
  const [getSubcontractorList, setGetSubcontractorList] = useState([])
  const {user} = useUser()
  const {supplier} = useSupplier()
  // const [employees, setEmployees] = useState([])

  // const handlePageChange = (page: number) => {
  //   if (page >= 0 && page <= totalPages) {
  //     setCurrentPage(page);
  //   }
  // };

  const suppliers = async () => {
    try {
      const res = await axios.get(
        `${ip}/supplier/filtered-client?idSearch=${selectedBranch?.idBranch}`,
      );
      setSuppliersList(res.data.content);
    } catch (error) {
      console.error("Erro ao encontrar fornecedor:", error);
    }
  };

  const uniqueSupplier = async () => {
    try {
      const res = await axios.get(`${ip}/supplier/${selectedSupplier}`);
      setGetUniqueSupplier(res.data);
    } catch (err) {
      console.log("erro ao buscar unico supplier:", err);
    }
  };

  const getSubcontractor = async() => {
    setLoading(true)
    try{
      const res = await axios.get(`${ip}/subcontractor/filtered-supplier?idSearch=${selectedSupplier}`)
      setGetSubcontractorList(res.data.content)
    }catch(err) {
      console.log("erro ao buscar subcontratados:", err);
    }finally{
      setLoading(false)
    }
  }

  // const getEmployees = async() => {
  //   try{
  //     const res = await axios.get(`${ip}/employee?idSearch=${supplier?.idProvider}&enterprise=SUPPLIER`)
  //     setEmployees(res.data.content)
  //   }catch(err) {
  //     console.log(err);
      
  //   }
  // }

  // useEffect(() => {
  //   if(supplier?.idProvider) {
  //     getEmployees()
  //   }
  // }, [supplier?.idProvider])

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      suppliers();
    }
    if (selectedSupplier) {
      uniqueSupplier();
      getSubcontractor()
    }
  }, [selectedBranch?.idBranch, selectedSupplier]);

  if(user?.role === "ROLE_SUPPLIER_RESPONSIBLE" && "ROLE_SUPPLIER_RESPONSIBLE") {
    return (
      <div className="m-4 flex justify-center">
        <div className="dark:bg-primary flex w-[90rem] flex-col rounded-lg bg-white p-10 shadow-md">
          <div className="mb-6 flex items-center justify-between">
            <h1 className="text-2xl">Colaboradores</h1>
            <NewModalCreateEmployee />
          </div>
          <div className="mb-4 flex">
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
            {loading ? (
              <div className="text-center text-gray-600">
                Carregando colaboradores...
              </div>
            ) : error ? (
              <div className="text-center text-red-500">{error}</div>
            ) : selectedTab === "fornecedor" ? (
              <div>
                <h2 className="mb-4 text-xl">{supplier?.corporateName}</h2>
                <div>
                  
                  <TableEmployee idProvider={supplier?.idProvider ?? null} />
                </div>
              </div>
            ) : (
              <div>
                <div className="flex flex-col gap-5">
                  <div className="flex items-center gap-2">
                    {" "}
                    <strong>Fornecedor: </strong>
                    <h1>{supplier?.corporateName}</h1>
                  </div>
                  <div>
                    {getSubcontractorList.map((subcontractor: any) => (
                      <div key={subcontractor.idProvider}>
                        <span>{subcontractor.corporateName}teste</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )}
          </div>
          {/* <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          /> */}
        </div>
      </div>
    );
  }

  return (
    <div className="m-4 flex justify-center">
      <div className="dark:bg-primary flex w-[90rem] flex-col rounded-lg bg-white p-10 shadow-md">
        <div className="mb-6 flex items-center justify-between">
          <h1 className="text-2xl">Colaboradores</h1>
          {/* <NewModalCreateEmployee /> */}
        </div>
        <div className="mb-4 flex">
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
          {loading ? (
            <div className="text-center text-gray-600">
              Carregando colaboradores...
            </div>
          ) : error ? (
            <div className="text-center text-red-500">{error}</div>
          ) : selectedTab === "fornecedor" ? (
            <div>
              <h2 className="mb-4 text-xl">Selecione um Fornecedor</h2>
              <div>
                <span className="text-realizaBlue text-[14px]">
                  Fornecedor:{" "}
                </span>
                <select
                  value={selectedSupplier || ""}
                  onChange={(e) => setSelectedSupplier(e.target.value)}
                  className="rounded-lg border p-2 text-[12px]"
                >
                  <option value="">Selecione um fornecedor</option>
                  {suppliersList.map((supplier: propsSupplier) => (
                    <option
                      key={supplier.idProvider}
                      value={supplier.idProvider}
                      onClick={() => selectedSupplier}
                    >
                      {supplier.corporateName}
                    </option>
                  ))}
                </select>
                <TableEmployee idProvider={selectedSupplier} />
              </div>
            </div>
          ) : (
            <div>
              <div className="flex flex-col gap-5">
                <div className="flex items-center gap-2">
                  {" "}
                  <strong>Fornecedor: </strong>
                  {getUniqueSupplier ? (
                    <div className="flex items-center gap-3">
                      <p>{getUniqueSupplier?.corporateName}</p> -
                      <p>{getUniqueSupplier?.cnpj}</p>
                    </div>
                  ) : (
                    <p>Nenhum fornecedor selecionado</p>
                  )}
                </div>
                <div>
                  {getSubcontractorList.map((subcontractor: any) => (
                    <div key={subcontractor.idProvider}>
                      <span>{subcontractor.corporateName}teste</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>
        {/* <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        /> */}
      </div>
    </div>
  );
};
