import { useClient } from "@/context/Client-Provider";
import { EditModalEnterprise } from "../../realizaProfile/profileEnterprise/edit-modal-enterprise";
import { Link } from "react-router-dom";
import { Settings2 } from "lucide-react";
import { useEffect, useState } from "react";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useBranch } from "@/context/Branch-provider";
import { Button } from "@/components/ui/button";
import { useUser } from "@/context/user-provider";
import { BranchResume } from "./branch-resume";
import { ConformityGaugeChart } from "@/components/BIs/BisPageComponents/conformityChart";

// Cards
import { ActiveContracts } from "@/components/BIs/BisPageComponents/activeContracts";
import { Employees } from "@/components/BIs/BisPageComponents/employees";
import { Suppliers } from "@/components/BIs/BisPageComponents/suppliersCard";
import { AllocatedEmployees } from "@/components/BIs/BisPageComponents/AllocatedEmployees";

export function HomeClient() {
  const [employees, setEmployees] = useState<any[]>([]);
  const { user } = useUser();
  const { selectedBranch } = useBranch();
  const { client } = useClient();

  const [selectTab, setSelectedTab] = useState<"filiais" | "usuarios">("filiais");
  const [branches, setBranches] = useState<any[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredBranches, setFilteredBranches] = useState<any[]>([]);

  const [homeData, setHomeData] = useState<{
    activeContractQuantity: number;
    activeEmployeeQuantity: number;
    activeSupplierQuantity: number;
    allocatedEmployeeQuantity: number;
    conformity: number;
  }>({
    activeContractQuantity: 0,
    activeEmployeeQuantity: 0,
    activeSupplierQuantity: 0,
    allocatedEmployeeQuantity: 0,
    conformity: 0,
  });

  const [loadingCards, setLoadingCards] = useState(false);

  const firstLetter = client?.tradeName?.charAt(0) || "";
  const lastLetter = client?.tradeName?.slice(-1) || "";
  const firstLetterBranch = selectedBranch?.name?.charAt(0) || "";
  const lastLetterBranch = selectedBranch?.name?.slice(-1) || "";

  const fetchBranches = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client?.idClient}`,
        { headers: { Authorization: `Bearer ${tokenFromStorage}` } }
      );
      const { content } = response.data;
      setBranches(content);
      setFilteredBranches(content);
    } catch (err) {
      console.error("Erro ao buscar filiais:", err);
    }
  };

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    const term = event.target.value.toLowerCase();
    setSearchTerm(term);
    const filtered = branches.filter(
      (branch: any) =>
        branch.name.toLowerCase().includes(term) || branch.cnpj.includes(term)
    );
    setFilteredBranches(filtered);
  };

  const getEmployee = async () => {
    setEmployees([]);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/employee?idSearch=${selectedBranch?.idBranch}&enterprise=CLIENT`,
        { headers: { Authorization: `Bearer ${tokenFromStorage}` } }
      );
      setEmployees(res.data.content);
    } catch (error) {
      console.log("Erro ao buscar colaboradores:", error);
    }
  };

  useEffect(() => {
    const fetchHomeNumbers = async () => {
      if (!selectedBranch?.idBranch) {
        setHomeData({
          activeContractQuantity: 0,
          activeEmployeeQuantity: 0,
          activeSupplierQuantity: 0,
          allocatedEmployeeQuantity: 0,
          conformity: 0,
        });
        return;
      }
      try {
        setLoadingCards(true);
        const token = localStorage.getItem("tokenClient");
        const { data } = await axios.get(
          `${ip}/dashboard/home/${selectedBranch.idBranch}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        const raw = Number(data?.conformity ?? 0);
        const conformity = isFinite(raw) ? (raw <= 1 ? raw * 100 : raw) : 0;

        setHomeData({
          activeContractQuantity: Number(data?.activeContractQuantity) || 0,
          activeEmployeeQuantity: Number(data?.activeEmployeeQuantity) || 0,
          activeSupplierQuantity: Number(data?.activeSupplierQuantity) || 0,
          allocatedEmployeeQuantity:
            Number(data?.allocatedEmployeeQuantity) || 0,
          conformity,
        });
      } catch (e) {
        console.error("Erro ao buscar dados do dashboard/home:", e);
        setHomeData({
          activeContractQuantity: 0,
          activeEmployeeQuantity: 0,
          activeSupplierQuantity: 0,
          allocatedEmployeeQuantity: 0,
          conformity: 0,
        });
      } finally {
        setLoadingCards(false);
      }
    };

    fetchHomeNumbers();
  }, [selectedBranch?.idBranch]);

  useEffect(() => {
    if (selectedBranch?.idBranch) getEmployee();
  }, [selectedBranch?.idBranch]);

  useEffect(() => {
    if (client?.idClient) fetchBranches();
  }, [selectedBranch?.idBranch, client?.idClient]);

  // ====== VISÃO DE GESTOR DE FILIAL ======
  if (user?.role === "ROLE_CLIENT_MANAGER") {
    return (
      <div>
        <BranchResume
          firstLetter={firstLetterBranch}
          lastLetter={lastLetterBranch}
          isLoading={!!selectedBranch}
          cnpj={selectedBranch?.cnpj}
          email={selectedBranch?.email}
          name={selectedBranch?.name}
        />

        <div className="flex items-center justify-center px-[20vw] gap-5">
          <ActiveContracts count={homeData.activeContractQuantity} />
          <Employees count={homeData.activeEmployeeQuantity} />
          <Suppliers count={homeData.activeSupplierQuantity} />
          <AllocatedEmployees count={homeData.allocatedEmployeeQuantity} />
          <ConformityGaugeChart
            percentage={homeData.conformity}
            loading={loadingCards}
          />
        </div>

        <div className="mt-5 w-full text-right px-[10vw]">
          <Link to={`/cliente/dashboard-details/${user?.idUser}`}>
            <Button className="hover:bg-realizaBlue dark:bg-primary bg-realizaBlue dark:text-white dark:hover:bg-blue-950">
              Ver mais
            </Button>
          </Link>
        </div>
      </div>
    );
  }

  // ====== VISÃO INICIAL DO CLIENTE ======
  return (
    <div className="flex flex-col items-center p-10">
      {/* Header */}
      <div className="flex w-full justify-between items-center mb-8 px-10">
        <div className="flex items-center gap-4">
          <div className="bg-realizaBlue flex h-16 w-16 items-center justify-center rounded-full text-white text-3xl font-bold">
            {firstLetter}
            {lastLetter}
          </div>
          <div className="flex flex-col">
            <h2 className="text-realizaBlue text-2xl font-medium">
              {client?.tradeName}
            </h2>
            <h3 className="text-sky-900 text-sm">{client?.corporateName}</h3>
          </div>
        </div>
        <EditModalEnterprise />
      </div>

      {/* ====== (1) TABELA AGORA VEM PRIMEIRO ====== */}
      <div className="w-full max-w-6xl rounded-lg border bg-white p-8 shadow-lg">
        <div className="flex items-center justify-between gap-4 flex-wrap">
          <div className="flex gap-2">
            <Button
              variant={"ghost"}
              className={`px-4 ${selectTab === "filiais"
                  ? "bg-realizaBlue text-white"
                  : "text-realizaBlue"
                }`}
              onClick={() => setSelectedTab("filiais")}
            >
              Fornecedores
            </Button>
            <Button
              variant={"ghost"}
              className={`px-4 ${selectTab === "usuarios"
                  ? "bg-realizaBlue text-white"
                  : "text-realizaBlue"
                }`}
              onClick={() => setSelectedTab("usuarios")}
            >
              Usuários
            </Button>
          </div>

          {selectTab === "filiais" && (
            <input
              type="text"
              value={searchTerm}
              onChange={handleSearch}
              placeholder="Pesquisar filiais"
              className="w-64 rounded-md border px-3 py-2 text-sm"
            />
          )}
        </div>

        <div className="mt-6">
          {selectTab === "filiais" ? (
            <div className="overflow-x-auto">
              <table className="w-full border-collapse border border-gray-300">
                <thead>
                  <tr>
                    <th className="border border-gray-300 px-4 py-2 text-start">
                      Filiais
                    </th>
                    <th className="border border-gray-300 px-4 py-2 text-start">
                      CNPJ
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {filteredBranches && filteredBranches.length > 0 ? (
                    filteredBranches.map((branch: any) => (
                      <tr key={branch.idBranch}>
                        <td className="border border-gray-300 px-4 py-2 text-realizaBlue">
                          {branch.name}
                        </td>
                        <td className="border border-gray-300 px-4 py-2">
                          {branch.cnpj}
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td
                        colSpan={2}
                        className="border border-gray-300 px-4 py-6 text-center text-gray-500"
                      >
                        Nenhuma filial encontrada
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full border-collapse border border-gray-300">
                <thead>
                  <tr>
                    <th className="border border-gray-300 px-4 py-2 text-start">
                      Nome
                    </th>
                    <th className="border border-gray-300 px-4 py-2 text-start">
                      Status
                    </th>
                    <th className="border border-gray-300 px-4 py-2 text-start">
                      Ações
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {employees && employees.length > 0 ? (
                    employees.map((employee: any) => (
                      <tr key={employee.idEmployee}>
                        <td className="border border-gray-300 px-4 py-2">
                          {employee.name}
                        </td>
                        <td className="border border-gray-300 px-4 py-2">
                          <span
                            className={
                              employee.situation === "Ativo"
                                ? "text-green-600"
                                : "text-red-600"
                            }
                          >
                            {employee.situation}
                          </span>
                        </td>
                        <td className="border border-gray-300 px-4 py-2">
                          <Link
                            to={`/sistema/detailsEmployees/${employee.idEmployee}`}
                            className="text-realizaBlue hover:underline inline-flex items-center gap-1"
                          >
                            <Settings2 size={16} />
                            Abrir
                          </Link>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td
                        colSpan={3}
                        className="border border-gray-300 px-4 py-6 text-center text-gray-500"
                      >
                        Nenhum colaborador encontrado
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* ===== NOVO CONTAINER DOS CARDS E GRAFICO ===== */}
      <div className="mt-8 flex w-full max-w-6xl flex-col items-center justify-between gap-8 md:flex-row">
        <div className="flex w-full flex-wrap justify-end gap-6 md:w-3/4">
          <ActiveContracts count={homeData.activeContractQuantity} />
          <Employees count={homeData.activeEmployeeQuantity} />
          <Suppliers count={homeData.activeSupplierQuantity} />
          <AllocatedEmployees count={homeData.allocatedEmployeeQuantity} />
        </div>
        <div className="flex w-full items-center justify-center rounded-lg border bg-white p-5 shadow-sm md:w-1/4">
          <ConformityGaugeChart
            percentage={homeData.conformity}
            loading={loadingCards}
          />
        </div>
      </div>

      {/* Botão "Ver mais" */}
      <div className="mt-5 w-full max-w-6xl text-right">
        <Link to={`/sistema/dashboard-details/${user?.idUser}`}>
          <Button className="hover:bg-realizaBlue dark:bg-primary bg-realizaBlue dark:text-white dark:hover:bg-blue-950">
            Ver mais
          </Button>
        </Link>
      </div>
    </div>
  );
}