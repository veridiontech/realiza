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

export const ClientCollaborators = (): JSX.Element => {
  const [selectedTab, setSelectedTab] = useState<"fornecedor" | "subcontratado">(
    "fornecedor"
  );
  const [loading, setLoading] = useState(false);
  const [error] = useState<string | null>(null);
  const { selectedBranch } = useBranch();
  const [selectedSupplier, setSelectedSupplier] = useState<string | null>(null);
  const [suppliersList, setSuppliersList] = useState<propsSupplier[]>([]);
  const [getUniqueSupplier, setGetUniqueSupplier] =
    useState<propsSupplier | null>(null);
  const [, setGetSubcontractorList] = useState<any[]>([]); 
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

  console.log("Id supplier: ", supplier);

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
    if (!selectedSupplier) {
      setGetSubcontractorList([]);
      return;
    }
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
  }, [selectedBranch?.idBranch]);

  useEffect(() => {
    if (selectedSupplier) {
      uniqueSupplier();
      if (selectedTab === "subcontratado") {
        getSubcontractor();
      }
    } else {
      setGetUniqueSupplier(null);
      setGetSubcontractorList([]);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedSupplier, selectedTab]);

  // Lógica de Renderização para ROLE_CLIENT_RESPONSIBLE
  if (user?.role === "ROLE_CLIENT_RESPONSIBLE") {
    return (
      <div className="m-4 flex flex-col items-center">
        <div className="dark:bg-primary flex w-[90rem] flex-col rounded-lg bg-white p-10 shadow-md relative bottom-[5vw]">
          <div className="mb-6 flex items-center justify-between bg-realizaBlue p-5 rounded-md">
            <h1 className="text-2xl text-white font-medium flex items-center gap-1">
              <Users2Icon size={30} className="text-[#FFCE50]" /> Colaboradores
            </h1>
            <div className="flex gap-2">
              <NewModalCreateEmployee
                onEmployeeCreated={() => {
                  if (selectedTab === "fornecedor") {
                    // Re-fetch logic based on actual scenario
                  } else if (selectedTab === "subcontratado") {
                    getSubcontractor(); 
                  }
                }}
                targetType={selectedTab === "fornecedor" ? "supplier" : "subcontractor"} 
                supplierId={supplier?.idProvider ?? null} 
                subcontractId={null} 
              />
              <ManageEmployeesModal
                idProvider={supplier?.idProvider ?? null}
              />
            </div>
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
          <div className="flex gap-1">
            <Cog />
            <h2 className="mb-4 text-xl">{supplier?.corporateName}</h2>
          </div>
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
              <div className="">
                <TableEmployee
                  idTarget={supplier?.idProvider ?? null}
                  targetType="supplier"
                />
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
                {/* Se a intenção é mostrar colaboradores de subcontratados do fornecedor selecionado: */}
                <TableEmployee
                  idTarget={supplier?.idProvider ?? null} // ID do fornecedor, para listar subcontratados dele
                  targetType="subcontractor"
                />
              </div>
            </div>
          )}
        </div>
      </div>
    );
  }

  // Lógica de Renderização para outros papéis (e seleção inicial)
  return (
    <div className="m-4 flex justify-center">
      <div className="flex flex-col">
        <div className="dark:bg-primary flex w-[95vw] h-[30vh] flex-col rounded-lg bg-white p-10 shadow-md relative bottom-[8vw]">
          <div className="mb-6 flex items-center justify-between bg-realizaBlue p-5">
            <div className="flex items-center gap-1">
              <Users2Icon className="text-[#FFCE50]" />
              <h1 className="text-2xl text-white font-medium">Colaboradores</h1>
            </div>
            <div className="mb-4 flex gap-5">
              <Button
                variant="ghost"
                className={`px-4 py-2 transition-all duration-300 ${
                  selectedTab === "fornecedor"
                    ? "bg-white text-realizaBlue scale-110 font-bold  shadow-lg"
                    : "text-white border border-white"
                }`}
                onClick={() => {
                  setSelectedTab("fornecedor");
                  setGetUniqueSupplier(null); 
                }}
              >
                <Cog /> Fornecedor
              </Button>
              <Button
                variant="ghost"
                className={`px-4 py-2 transition-all duration-300 ${
                  selectedTab === "subcontratado"
                    ? "bg-white text-realizaBlue scale-110 font-bold  shadow-lg"
                    : "text-white border border-white"
                }`}
                onClick={() => {
                  setSelectedTab("subcontratado");
                }}
              >
                <BriefcaseBusiness /> Subcontratado
              </Button>
            </div>
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
                    onChange={(e) => {
                      const selectedId = e.target.value;
                      setSelectedSupplier(selectedId);
                      const supplierData = suppliersList.find(
                        (sup) => sup.idProvider === selectedId
                      );
                      if (supplierData) {
                        setSupplier(supplierData);
                      } else {
                        setSupplier(null);
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
                <TableEmployee
                  idTarget={selectedSupplier}
                  targetType="supplier"
                />
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
                  {/* Assumindo que o selectedSupplier é o alvo para a busca de subcontratados */}
                  <TableEmployee
                    idTarget={selectedSupplier}
                    targetType="subcontractor"
                  />
                  
                </div>
              </div>
            )}
          </div>
        </div>
        <div className="flex gap-2 justify-end  relative  2xl:bottom-[14vh] xl:bottom-[12vh] lg:bottom-[8vh] md:bottom-[7vh] bottom-[15vh]">
          <NewModalCreateEmployee
            onEmployeeCreated={
              selectedTab === "fornecedor" ? suppliers : getSubcontractor
            } 
            targetType={selectedTab === "fornecedor" ? "supplier" : "subcontractor"}
            supplierId={selectedSupplier}
            subcontractId={selectedSupplier} // Passa o selectedSupplier como subcontratId (logica pode precisar de ajuste dependendo do seu backend)
            targetName={
              selectedTab === "fornecedor"
                ? suppliersList.find((s) => s.idProvider === selectedSupplier)
                    ?.corporateName
                : getUniqueSupplier?.corporateName
            }
          />
          <ManageEmployeesModal idProvider={selectedSupplier} />
        </div>
      </div>
    </div>
  );
};