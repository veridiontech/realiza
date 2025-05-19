import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { ScrollArea } from "@/components/ui/scroll-area";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";

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

export function ManageEmployeesModal({ idProvider }: ManageEmployeesModalProps) {
  const [activeTab, setActiveTab] = useState<"alocar" | "desalocar">("alocar");
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedEmployees, setSelectedEmployees] = useState<string[]>([]);
  const [mainModalOpen, setMainModalOpen] = useState(false);
  const [confirmModalOpen, setConfirmModalOpen] = useState(false);
  const [selectContractsModalOpen, setSelectContractsModalOpen] = useState(false);
  const [finalConfirmOpen, setFinalConfirmOpen] = useState(false);
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [selectedContracts, setSelectedContracts] = useState<string[]>([]);

  useEffect(() => {
    if (mainModalOpen) {
      const fetchAndSortEmployees = async () => {
        setLoading(true);
        try {
          const tokenFromStorage = localStorage.getItem("tokenClient");
          const res = await axios.get(`${ip}/employee?idSearch=${idProvider}&enterprise=SUPPLIER`, {
            headers: { Authorization: `Bearer ${tokenFromStorage}` }
          });
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
    }
  }, [mainModalOpen]);

  const toggleSelectAll = () => {
    if (selectedEmployees.length === employees.length) {
      setSelectedEmployees([]);
    } else {
      setSelectedEmployees(employees.map((emp) => emp.idEmployee));
    }
  };

  const toggleSelect = (id: string) => {
    setSelectedEmployees((prev) =>
      prev.includes(id) ? prev.filter((e) => e !== id) : [...prev, id]
    );
  };

  const handleConfirm = () => {
    if (selectedEmployees.length === 0) return;
    setMainModalOpen(false);
    setSelectContractsModalOpen(true);
  };

  const getContracts = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/contract/supplier/filtered-supplier?idSearch=${idProvider}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      });
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

  const toggleSelectContract = (id: string) => {
    setSelectedContracts((prev) =>
      prev.includes(id) ? prev.filter((cid) => cid !== id) : [...prev, id]
    );
  };

  const handleAllocate = async () => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    console.log("Lista de colaboradores" , selectedEmployees);
    try {
      await Promise.all(
        selectedContracts.map(async (contractId) => {
          console.log("ID do Contrato", contractId);
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
      setFinalConfirmOpen(false);
      setSelectedContracts([]);
      setSelectedEmployees([]);
    } catch (error) {
      console.error("Erro ao alocar colaboradores:", error);
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
        <DialogContent className="bg-[#1F2A40] border border-[#2E3C57] text-white max-w-md">
          <DialogHeader>
            <DialogTitle className="text-white text-center text-lg">
              O que você deseja fazer?
            </DialogTitle>
          </DialogHeader>
          <div className="flex flex-col items-center gap-4 mt-6">
            <Button
              onClick={() => {
                setActiveTab("alocar");
                setConfirmModalOpen(false);
                setMainModalOpen(true);
              }}
              className="w-full bg-green-600 hover:bg-green-700 text-white font-semibold py-2 rounded-md shadow"
            >
              Alocar Colaborador
            </Button>
            <Button
              onClick={() => {
                setActiveTab("desalocar");
                setConfirmModalOpen(false);
                setMainModalOpen(true);
              }}
              className="w-full bg-red-600 hover:bg-red-700 text-white font-semibold py-2 rounded-md shadow"
            >
              Desalocar Colaborador
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      <Dialog open={mainModalOpen} onOpenChange={setMainModalOpen}>
        <DialogContent
          style={{ backgroundImage: `url(${bgModalRealiza})` }}
          className="max-w-[85vw] sm:max-w-[40vw] md:max-w-[35vw] text-white"
        >
          <DialogHeader>
            <DialogTitle className="text-white">
              {activeTab === "alocar" ? "Alocar Colaboradores" : "Desalocar Colaboradores"}
            </DialogTitle>
          </DialogHeader>

          <Button
            onClick={toggleSelectAll}
            className="mb-3 bg-[#3A4C70] hover:bg-[#506A93] text-white font-medium px-4 py-2 rounded-md"
          >
            {selectedEmployees.length === employees.length
              ? "Limpar Seleção"
              : "Selecionar Todos"}
          </Button>

          <ScrollArea className="h-[60vh] mt-2 space-y-2">
            {loading ? (
              <p>Carregando colaboradores...</p>
            ) : employees.length > 0 ? (
              employees.map((emp) => {
                const isSelected = selectedEmployees.includes(emp.idEmployee);
                return (
                  <button
                    key={emp.idEmployee}
                    onClick={() => toggleSelect(emp.idEmployee)}
                    className={`w-full text-left p-3 rounded-md border transition-all duration-200 ${
                      isSelected
                        ? "bg-green-600 border-green-400"
                        : "bg-[#2E3C57] hover:bg-[#3A4C70] border-[#3A4C70]"
                    }`}
                  >
                    {emp.name} {emp.surname}
                  </button>
                );
              })
            ) : (
              <p>Nenhum colaborador encontrado.</p>
            )}
          </ScrollArea>

          {selectedEmployees.length > 0 && (
            <div className="mt-4 flex justify-end">
              <Button
                onClick={handleConfirm}
                className={`${
                  activeTab === "alocar" ? "bg-green-600" : "bg-red-600"
                } hover:brightness-110 text-white font-semibold px-6 py-2 rounded-md`}
              >
                Confirmar {activeTab === "alocar" ? "Alocação" : "Desalocação"}
              </Button>
            </div>
          )}
        </DialogContent>
      </Dialog>

      <Dialog open={selectContractsModalOpen} onOpenChange={setSelectContractsModalOpen}>
        <DialogContent className="bg-[#1F2A40] border border-[#2E3C57] text-white max-w-md">
          <DialogHeader>
            <DialogTitle className="text-white text-center text-lg">
              Selecione os contratos
              <ScrollArea className="h-[50vh] mt-4 space-y-2">
                {contracts.length > 0 ? (
                  contracts.map((contract) => {
                    const isSelected = selectedContracts.includes(contract.idContract);
                    return (
                      <button
                        key={contract.idContract}
                        onClick={() => toggleSelectContract(contract.idContract)}
                        className={`w-full text-left p-3 rounded-md border transition-all duration-200 ${
                          isSelected
                            ? "bg-green-600 border-green-400"
                            : "bg-[#2E3C57] hover:bg-[#3A4C70] border-[#3A4C70]"
                        }`}
                      >
                        <div className="flex flex-col">
                          <span className="font-semibold">{contract.serviceName}</span>
                          <span className="text-sm text-gray-300">{contract.contractReference}</span>
                          <span className="text-sm text-gray-300">
                            {new Date(contract.dateStart).toLocaleDateString()}
                          </span>
                          <span className="text-sm text-gray-400">{contract.description}</span>
                        </div>
                      </button>
                    );
                  })
                ) : (
                  <p>Nenhum contrato disponível.</p>
                )}
              </ScrollArea>
            </DialogTitle>
          </DialogHeader>
          <div className="mt-6 flex justify-end">
            <Button
              onClick={() => {
                setFinalConfirmOpen(true);
                setSelectContractsModalOpen(false);
              }}
              className="bg-green-600 hover:bg-green-700 text-white font-semibold px-6 py-2 rounded-md"
            >
              Confirmar Alocação
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      <Dialog open={finalConfirmOpen} onOpenChange={setFinalConfirmOpen}>
        <DialogContent className="bg-[#1F2A40] border border-[#2E3C57] text-white max-w-lg">
          <DialogHeader>
            <DialogTitle className="text-white text-center text-lg">
              Confirme a alocação
            </DialogTitle>
          </DialogHeader>

          <div className="mt-4 space-y-4">
            <div>
              <h3 className="font-semibold mb-2">Colaboradores Selecionados:</h3>
              <ul className="list-disc list-inside text-sm text-gray-300">
                {employees
                  .filter((emp) => selectedEmployees.includes(emp.idEmployee))
                  .map((emp) => (
                    <li key={emp.idEmployee}>{emp.name} {emp.surname}</li>
                  ))}
              </ul>
            </div>

            <div>
              <h3 className="font-semibold mb-2">Contratos Selecionados:</h3>
              <ul className="list-disc list-inside text-sm text-gray-300">
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

          <div className="mt-6 flex justify-between">
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
              onClick={handleAllocate}
              className="bg-green-600 hover:bg-green-700 text-white font-semibold px-6 py-2 rounded-md"
            >
              Confirmar
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
}
