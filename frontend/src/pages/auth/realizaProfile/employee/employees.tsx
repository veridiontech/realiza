import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import axios from "axios";
import { ip } from "@/utils/ip";
import { propsSupplier } from "@/types/interfaces";
import { TableEmployee } from "./tableEmployee";
import { useBranch } from "@/context/Branch-provider";
import { useSupplier } from "@/context/Supplier-context";
import { useUser } from "@/context/user-provider";
import { NewModalCreateEmployee } from "./modals/newModalCreateEmployee";
import { ManageEmployeesModal } from "./modals/ManageEmployeesModal";
import { BriefcaseBusiness, Cog, Users2Icon } from "lucide-react";

export const EmployeesTable = (): JSX.Element => {
  const [selectedTab, setSelectedTab] = useState("fornecedor");
  const [loading, setLoading] = useState(false);
  const [error] = useState<string | null>(null);
  const { selectedBranch } = useBranch();
  const [selectedSupplier, setSelectedSupplier] = useState<string | null>(null);
  const [suppliersList, setSuppliersList] = useState<propsSupplier[]>([]);
  const [getUniqueSupplier, setGetUniqueSupplier] =
    useState<propsSupplier | null>(null);
  const [getSubcontractorList, setGetSubcontractorList] = useState([]);
  const { user } = useUser();
  const { supplier, setSupplier } = useSupplier();

  const suppliers = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/supplier/filtered-client?idSearch=${selectedBranch?.idBranch}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setSuppliersList(res.data.content);
    } catch (error) {
      console.error("Erro ao encontrar fornecedor:", error);
    }
  };

  const uniqueSupplier = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/supplier/${selectedSupplier}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setGetUniqueSupplier(res.data);
    } catch (err) {
      console.log("erro ao buscar unico supplier:", err);
    }
  };

  const getSubcontractor = async () => {
    setLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/subcontractor/filtered-supplier?idSearch=${selectedSupplier}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setGetSubcontractorList(res.data.content);
    } catch (err) {
      console.log("erro ao buscar subcontratados:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      suppliers();
    }
    if (selectedSupplier) {
      uniqueSupplier();
      getSubcontractor();
    }
  }, [selectedBranch?.idBranch, selectedSupplier]);

  const isSupplierResponsible = user?.role === "ROLE_SUPPLIER_RESPONSIBLE";

  return (
    <div className="m-4 flex justify-center">
      <div className="flex flex-col">
        <div className="dark:bg-primary relative bottom-[8vw] flex h-[30vh] w-[95vw] flex-col rounded-lg bg-white p-10 shadow-md">
          <div className="mb-6 flex items-center justify-between rounded-md bg-realizaBlue p-5">
            <div className="flex items-center gap-1">
              <Users2Icon className="text-[#FFCE50]" />
              <h1 className="text-2xl font-medium text-white">Colaboradores</h1>
            </div>
            <div className="flex gap-2">
              <Button
                variant="ghost"
                className={`px-4 py-2 transition-all duration-300 ${
                  selectedTab === "fornecedor"
                    ? "scale-110 font-bold shadow-lg bg-white text-realizaBlue"
                    : "text-white border border-white"
                }`}
                onClick={() => setSelectedTab("fornecedor")}
              >
                <Cog className="mr-2 h-4 w-4" /> Fornecedor
              </Button>
              <Button
                variant="ghost"
                className={`px-4 py-2 transition-all duration-300 ${
                  selectedTab === "subcontratado"
                    ? "scale-110 font-bold shadow-lg bg-white text-realizaBlue"
                    : "text-white border border-white"
                }`}
                onClick={() => setSelectedTab("subcontratado")}
              >
                <BriefcaseBusiness className="mr-2 h-4 w-4" /> Subcontratado
              </Button>
            </div>
          </div>

          <div className="p-4">
            {loading ? (
              <div className="text-center text-gray-600">
                Carregando colaboradores...
              </div>
            ) : error ? (
              <div className="text-center text-red-500">{error}</div>
            ) : selectedTab === "fornecedor" ? (
              <div>
                {isSupplierResponsible ? (
                  <div>
                    <h2 className="mb-4 text-xl">{supplier?.corporateName}</h2>
                    <TableEmployee idProvider={supplier?.idProvider ?? null} />
                  </div>
                ) : (
                  <div>
                    <h2 className="mb-4 text-xl">Selecione um Fornecedor</h2>
                    <div>
                      <span className="text-realizaBlue text-[14px]">
                        Fornecedor:{" "}
                      </span>
                      <select
                        value={selectedSupplier || ""}
                        onChange={(e) => {
                          const selectedId = e.target.value;
                          setSelectedSupplier(selectedId);
                          const supplierData = suppliersList.find(
                            (sup) => sup.idProvider === selectedId
                          );
                          if (supplierData) {
                            setSupplier(supplierData);
                          }
                        }}
                        className="rounded-lg border p-2 text-[12px]"
                      >
                        <option value="">Selecione um fornecedor</option>
                        {suppliersList.map((supplier) => (
                          <option
                            key={supplier.idProvider}
                            value={supplier.idProvider}
                          >
                            {supplier.corporateName}
                          </option>
                        ))}
                      </select>
                    </div>
                    <TableEmployee idProvider={selectedSupplier} />
                  </div>
                )}
              </div>
            ) : (
              <div>
                <div className="flex flex-col gap-5">
                  <div className="flex items-center gap-2">
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
                        <span>{subcontractor.corporateName}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
        <div className="relative bottom-[18vh] flex justify-end gap-2 2xl:bottom-[16vh] xl:bottom-[14vh] lg:bottom-[10vh] md:bottom-[9vh]">
          <NewModalCreateEmployee onEmployeeCreated={getSubcontractor} />
          <ManageEmployeesModal idProvider={selectedSupplier} />
        </div>
      </div>
    </div>
  );
};