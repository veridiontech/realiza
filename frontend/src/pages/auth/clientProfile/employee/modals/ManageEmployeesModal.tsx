import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { toast } from "sonner";
import { Puff } from "react-loader-spinner";
import { Pointer, CircleX, Files, FileCheck2  } from "lucide-react";

interface Employee {
  idEmployee: string;
  name: string;
  surname: string;
}

interface ManageEmployeesModalProps {
  idProvider: string | null;
}

interface Contract {
  idContract: string;
  contractReference: string;
  description: string;
  dateStart: string;
  serviceName: string;
}

export function ManageEmployeesModal({
  idProvider,
}: ManageEmployeesModalProps) {
  const [activeTab, setActiveTab] = useState<"alocar" | "desalocar">("alocar");
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedEmployees, setSelectedEmployees] = useState<string[]>([]);
  const [mainModalOpen, setMainModalOpen] = useState(false);
  const [confirmModalOpen, setConfirmModalOpen] = useState(false);
  const [selectContractsModalOpen, setSelectContractsModalOpen] =
    useState(false);
  const [finalConfirmOpen, setFinalConfirmOpen] = useState(false);
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [selectedContracts, setSelectedContracts] = useState<string[]>([]);
  const [allocatedEmployees, setAllocatedEmployees] = useState<Employee[]>([]);
  const [selectedAllocatedEmployees, setSelectedAllocatedEmployees] = useState<
    string[]
  >([]);
  const [isAllocating, setIsAllocating] = useState(false);

  useEffect(() => {
    if (mainModalOpen) {
      if (activeTab === "alocar") {
        const fetchAndSortEmployees = async () => {
          setLoading(true);
          try {
            const tokenFromStorage = localStorage.getItem("tokenClient");
            const res = await axios.get(
              `${ip}/employee?idSearch=${idProvider}&enterprise=SUPPLIER`,
              {
                headers: { Authorization: `Bearer ${tokenFromStorage}` },
              }
            );
            const data = res.data.content || res.data;
            const sorted = [...data].sort((a: Employee, b: Employee) =>
              `${a.name} ${a.surname}`.localeCompare(`${b.name} ${b.surname}`)
            );
            setEmployees(sorted);
          } catch (error) {
            console.error("Erro ao buscar colaboradores:", error);
          } finally {
            setLoading(false);
            setSelectedEmployees([]);
          }
        };
        fetchAndSortEmployees();
      } else if (activeTab === "desalocar") {
        const fetchContracts = async () => {
          setLoading(true);
          try {
            const tokenFromStorage = localStorage.getItem("tokenClient");
            const res = await axios.get(
              `${ip}/contract/supplier/filtered-supplier?idSearch=${idProvider}`,
              {
                headers: { Authorization: `Bearer ${tokenFromStorage}` },
              }
            );
            setContracts(res.data.content || []);
          } catch (error) {
            console.error("Erro ao buscar contratos:", error);
          } finally {
            setLoading(false);
            setSelectedContracts([]);
          }
        };
        fetchContracts();
      }
    }
  }, [mainModalOpen, activeTab, idProvider]);

  const fetchAllocatedEmployees = async (contractId: string) => {
    setLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/employee/filtered-by-contract`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
        params: {
          idContract: contractId,
        },
      });
      setAllocatedEmployees(res.data.content || []);
      setSelectedAllocatedEmployees([]);
    } catch (error) {
      console.error("Erro ao buscar colaboradores alocados:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleDeallocate = async () => {
    if (
      selectedContracts.length !== 1 ||
      selectedAllocatedEmployees.length === 0
    ) {
      toast.error("Selecione 1 contrato e pelo menos 1 colaborador.");
      return;
    }

    const contractId = selectedContracts[0];
    const tokenFromStorage = localStorage.getItem("tokenClient");
    setIsAllocating(true);

    try {
      await axios.post(
        `${ip}/contract/remove-employee/${contractId}`,
        { employees: selectedAllocatedEmployees },
        {
          headers: {
            Authorization: `Bearer ${tokenFromStorage}`,
            "Content-Type": "application/json",
          },
        }
      );
      toast.success("Colaboradores desalocados com sucesso!");
      setFinalConfirmOpen(false);
      setSelectedContracts([]);
      setSelectedAllocatedEmployees([]);
      setAllocatedEmployees([]);
    } catch (error) {
      console.error("Erro ao desalocar colaboradores:", error);
      toast.error("Erro ao desalocar colaboradores.");
    } finally {
      setIsAllocating(false);
    }
  };

  const toggleSelectAll = () => {
    if (activeTab === "alocar") {
      if (selectedEmployees.length === employees.length) {
        setSelectedEmployees([]);
      } else {
        setSelectedEmployees(employees.map((emp) => emp.idEmployee));
      }
    } else {
      if (selectedContracts.length === contracts.length) {
        setSelectedContracts([]);
      } else {
        setSelectedContracts(contracts.map((contract) => contract.idContract));
      }
    }
  };

  const toggleSelect = (id: string) => {
    if (activeTab === "alocar") {
      setSelectedEmployees((prev) =>
        prev.includes(id) ? prev.filter((e) => e !== id) : [...prev, id]
      );
    } else {
      setSelectedContracts((prev) =>
        prev.includes(id) ? prev.filter((cid) => cid !== id) : [...prev, id]
      );
    }
  };

  const handleConfirm = () => {
    if (activeTab === "alocar") {
      if (selectedEmployees.length === 0) return;
      setMainModalOpen(false);
      setSelectContractsModalOpen(true);
    } else if (activeTab === "desalocar") {
      if (
        selectedAllocatedEmployees.length === 0 ||
        selectedContracts.length !== 1
      ) {
        toast.error("Selecione 1 contrato e pelo menos 1 colaborador.");
        return;
      }
      setMainModalOpen(false);
      setFinalConfirmOpen(true);
    }
  };

  const getContracts = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/contract/supplier/filtered-supplier?idSearch=${idProvider}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setContracts(res.data.content);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    if (selectContractsModalOpen && idProvider) {
      getContracts();
    }
  }, [selectContractsModalOpen, idProvider]);

  const toggleSelectContract = async (id: string) => {
    if (activeTab === "desalocar") {
      setSelectedContracts((prev) => {
        const updated = prev.includes(id)
          ? prev.filter((cid) => cid !== id)
          : [id];
        return updated;
      });
      await fetchAllocatedEmployees(id);
    } else {
      setSelectedContracts((prev) =>
        prev.includes(id) ? prev.filter((cid) => cid !== id) : [...prev, id]
      );
    }
  };

  const handleAllocate = async () => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    setIsAllocating(true);
    try {
      await Promise.all(
        selectedContracts.map(async (contractId) => {
          await axios.post(
            `${ip}/contract/add-employee/${contractId}`,
            { employees: selectedEmployees },
            {
              headers: {
                Authorization: `Bearer ${tokenFromStorage}`,
                "Content-Type": "application/json",
              },
            }
          );
        })
      );
      toast.success("Sucesso ao alocar colaborador");
      setFinalConfirmOpen(false);
      setSelectedContracts([]);
      setSelectedEmployees([]);
    } catch (error) {
      console.error("Erro ao alocar colaboradores:", error);
      toast.error("Erro ao alocar colaboradores.");
    } finally {
      setIsAllocating(false);
    }
  };

  return (
    <>
      <Button
        onClick={() => setConfirmModalOpen(true)}
        className="hidden md:block bg-realizaBlue border border-white rounded-md"
      >
        Gerenciar colaboradores
      </Button>
      <Button
        onClick={() => setConfirmModalOpen(true)}
        className="md:hidden bg-realizaBlue"
      >
        ⚙️
      </Button>

      <Dialog open={confirmModalOpen} onOpenChange={setConfirmModalOpen}>
        <DialogContent className="border border-[#2E3C57] text-white max-w-xl pt-3 px-3">
          <DialogHeader className="bg-[#1F2A40] p-5 rounded-sm">
            <DialogTitle className="text-white text-start text-lg">
              O que deseja realizar?
            </DialogTitle>
          </DialogHeader>
          <div className="flex items-center gap-4 mt-2 ">
            <Button
              onClick={() => {
                setActiveTab("alocar");
                setConfirmModalOpen(false);
                setMainModalOpen(true);
              }}
              className="w-full bg-green-500 bg-opacity-10 hover:bg-green-200 text-green-500 font-semibold text-md py-6 transition-colors"
            >
              <Pointer />
              Alocar Colaborador
            </Button>
            <Button
              onClick={() => {
                setActiveTab("desalocar");
                setConfirmModalOpen(false);
                setMainModalOpen(true);
              }}
              className="w-full bg-red-500 bg-opacity-10 hover:bg-red-200 text-red-500 font-semibold text-md py-6 transition-colors"
            >
              <CircleX />
              Desalocar Colaborador
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      <Dialog open={mainModalOpen} onOpenChange={setMainModalOpen}>
        <DialogContent className=" max-w-[85vw] sm:max-w-[40vw] md:max-w-[35vw] text-white pt-2 px-2">
          <DialogHeader className="bg-[#1F2A40] p-5 rounded-sm">
            <DialogTitle className="text-white">
              {activeTab === "alocar"
                ? "Alocar Colaboradores"
                : "Desalocar Colaboradores"}
            </DialogTitle>
          </DialogHeader>

          <Button
            onClick={toggleSelectAll}
            className="mb-3 bg-[#fff] hover:bg-[#fff0f0] border justify-start text-black font-medium px-4 py-2 rounded-md"
          >
            {activeTab === "alocar"
              ? selectedEmployees.length === employees.length
                ? "Limpar Seleção"
                : "Selecionar Todos"
              : selectedContracts.length === contracts.length
                ? "Limpar Seleção"
                : "Selecionar Todos"}
          </Button>

          <ScrollArea className="h-[60vh] mt-2 space-y-2">
            {loading ? (
              <p>Carregando...</p>
            ) : activeTab === "alocar" ? (
              employees.length > 0 ? (
                employees.map((emp) => {
                  const isSelected = selectedEmployees.includes(emp.idEmployee);
                  return (
                    <button
                      key={emp.idEmployee}
                      onClick={() => toggleSelect(emp.idEmployee)}
                      className={`w-full text-left p-3 rounded-md border transition-all duration-200 ${
                        isSelected
                          ? "bg-green-100 border-green-100 text-green-600 font-semibold"
                          : "bg-[#fffafa] hover:bg-green-200 border-none text-gray-700 font-semibold"
                      }`}
                    >
                     
                      {emp.name} {emp.surname}
                    </button>
                  );
                })
              ) : (
                <p>Nenhum colaborador encontrado.</p>
              )
            ) : activeTab === "desalocar" ? (
              selectedContracts.length === 1 ? (
                allocatedEmployees.length > 0 ? (
                  allocatedEmployees.map((emp) => {
                    const isSelected = selectedAllocatedEmployees.includes(
                      emp.idEmployee
                    );
                    return (
                      <button
                        key={emp.idEmployee}
                        onClick={() => {
                          setSelectedAllocatedEmployees((prev) =>
                            prev.includes(emp.idEmployee)
                              ? prev.filter((e) => e !== emp.idEmployee)
                              : [...prev, emp.idEmployee]
                          );
                        }}
                        className={`w-full text-left p-3 rounded-md border transition-all duration-200 ${
                          isSelected
                            ? "bg-red-600 border-red-400"
                            : "bg-[#2E3C57] hover:bg-[#3A4C70] border-[#3A4C70]"
                        }`}
                      >
                        {emp.name} {emp.surname}
                      </button>
                    );
                  })
                ) : (
                  <p>Nenhum colaborador alocado neste contrato.</p>
                )
              ) : (
                contracts.map((contract) => {
                  const isSelected = selectedContracts.includes(
                    contract.idContract
                  );
                  return (
                    <button
                      key={contract.idContract}
                      onClick={() => toggleSelectContract(contract.idContract)}
                      className={`w-full text-left p-3 rounded-md border transition-all duration-200 ${
                        isSelected
                          ? "bg-green-600 border-red-400"
                          : "bg-[#2E3C57] hover:bg-[#3A4C70] border-[#3A4C70]"
                      }`}
                    >
                      <div className="flex flex-col">
                        <span className="font-semibold">
                          {contract.serviceName}
                        </span>
                        <span className="text-sm text-gray-300">
                          {contract.contractReference}
                        </span>
                        <span className="text-sm text-gray-300">
                          {new Date(contract.dateStart).toLocaleDateString()}
                        </span>
                        <span className="text-sm text-gray-400">
                          {contract.description}
                        </span>
                      </div>
                    </button>
                  );
                })
              )
            ) : (
              <p>Algo deu errado ao tentar carregar os dados.</p>
            )}
          </ScrollArea>

          {((activeTab === "alocar" && selectedEmployees.length > 0) ||
            (activeTab === "desalocar" && selectedContracts.length > 0)) && (
            <div className="mt-4 flex justify-end">
              <Button
                onClick={handleConfirm}
                className={`${
                  activeTab === "alocar" ? "bg-green-600" : "bg-red-600"
                } hover:brightness-110 text-white font-semibold px-6 py-2 rounded-md`}
              >
                Proxima etapa
              </Button>
            </div>
          )}
        </DialogContent>
      </Dialog>

      <Dialog
        open={selectContractsModalOpen}
        onOpenChange={setSelectContractsModalOpen}
      >
        <DialogContent className="max-w-[85vw] sm:max-w-[40vw] md:max-w-[35vw] text-white pt-2 px-5">
          <DialogHeader className="bg-[#fff] py-5 px-2 rounded-lg">
            <DialogTitle className="text-black text-start text-lg flex gap-3 items-center">
              <Pointer width={18} height={18}/>
              Selecione os contratos
            </DialogTitle>
          </DialogHeader>
          <ScrollArea className="h-[50vh] mt-4 space-y-2">
            {contracts.length > 0 ? (
              contracts.map((contract) => {
                const isSelected = selectedContracts.includes(
                  contract.idContract
                );
                return (
                  <button
                    key={contract.idContract}
                    onClick={() => toggleSelectContract(contract.idContract)}
                    className={`w-full text-left p-3 rounded-md border transition-all duration-200 flex gap-5 items-center ${
                      isSelected
                        ? "bg-green-100 border-green-100 text-green-600 font-semibold"
                        : "bg-[#fffafa] hover:bg-green-200 border-none text-gray-700 font-semibold"
                    }`}
                  >
                     <Files width={70} height={70}/>
                    <div className="flex flex-col">
                      <span className="font-semibold">
                        {contract.serviceName}
                      </span>
                      <span className="text-sm text-gray-300">
                        {contract.contractReference}
                      </span>
                      <span className="text-sm text-gray-300">
                        {new Date(contract.dateStart).toLocaleDateString()}
                      </span>
                      <span className="text-sm text-gray-400">
                        {contract.description}
                      </span>
                    </div>
                  </button>
                );
              })
            ) : (
              <p>Nenhum contrato disponível.</p>
            )}
          </ScrollArea>

          <div className="mt-6 flex justify-end gap-3">
            <Button
              onClick={() => {
                setSelectContractsModalOpen(false);
                setMainModalOpen(true);
              }}
              className="bg-gray-500 hover:bg-gray-600 text-white font-semibold px-6 py-2 rounded-md"
            >
              Voltar
            </Button>

            <Button
              onClick={() => {
                setFinalConfirmOpen(true);
                setSelectContractsModalOpen(false);
              }}
              className="bg-green-600 hover:bg-green-700 text-white font-semibold px-6 py-2 rounded-md"
            >
              Proxima etapa
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      <Dialog open={finalConfirmOpen} onOpenChange={setFinalConfirmOpen}>
        <DialogContent className="bg-[#fff] border border-[#2E3C57] text-black max-w-lg p-0">
          <DialogHeader className="bg-[#1F2A40] py-4 px-5">
            <DialogTitle className="text-white text-start text-lg flex gap-3 items-center">
              <FileCheck2 />
              {activeTab === "alocar"
                ? "Confirme a alocação"
                : "Confirme a desalocação"}
            </DialogTitle>
          </DialogHeader>

          <div className="mt-4 space-y-4 px-5">
            <div>
              <h3 className="font-semibold mb-2">
                Colaboradores Selecionados:
              </h3>
              <ul className="list-disc list-inside font-normal text-sm text-gray-500">
                {(activeTab === "alocar"
                  ? employees.filter((emp) =>
                      selectedEmployees.includes(emp.idEmployee)
                    )
                  : allocatedEmployees.filter((emp) =>
                      selectedAllocatedEmployees.includes(emp.idEmployee)
                    )
                ).map((emp) => (
                  <li key={emp.idEmployee}>
                    {emp.name} {emp.surname}
                  </li>
                ))}
              </ul>
            </div>

            <div>
              <h3 className="font-semibold mb-2">Contratos Selecionados:</h3>
              <ul className="list-disc list-inside font-normal text-sm text-gray-500">
                {contracts
                  .filter((ct) => selectedContracts.includes(ct.idContract))
                  .map((ct) => (
                    <li key={ct.idContract}>
                      {ct.serviceName} - {ct.contractReference}
                    </li>
                  ))}
              </ul>
            </div>
          </div>

          <div className="mt-6 flex justify-between p-5">
            <Button
              onClick={() => {
                setFinalConfirmOpen(false);
                setSelectContractsModalOpen(true);
              }}
              className="bg-gray-500 hover:bg-gray-600 text-white font-semibold px-6 py-2 rounded-md"
            >
              Voltar
            </Button>
            <Button
              onClick={
                activeTab === "alocar" ? handleAllocate : handleDeallocate
              }
              disabled={isAllocating}
              className="bg-green-600 hover:bg-green-700 text-white font-semibold px-6 py-2 rounded-md flex items-center justify-center gap-2"
            >
              {isAllocating ? (
                <Puff
                  visible={true}
                  height="20"
                  width="20"
                  color="white"
                  ariaLabel="puff-loading"
                />
              ) : (
                "Confirmar"
              )}
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
}
