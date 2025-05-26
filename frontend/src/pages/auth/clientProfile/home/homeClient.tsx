import { useClient } from "@/context/Client-Provider";
import { EditModalEnterprise } from "../../realizaProfile/profileEnterprise/edit-modal-enterprise";
import { Skeleton } from "@/components/ui/skeleton";
import { Link } from "react-router-dom";
import { Settings2 } from "lucide-react";
import { useEffect, useState } from "react";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useBranch } from "@/context/Branch-provider";
import { Button } from "@/components/ui/button";
import { useUser } from "@/context/user-provider";
// import { EnterpriseResume } from "@/components/home/enterpriseResume";
import { BranchResume } from "./branch-resume";

export function HomeClient() {
  const [employees, setEmployees] = useState([]);
  const { user } = useUser();
  const { selectedBranch } = useBranch();
  const { client } = useClient();
  const [selectTab, setSelectedTab] = useState("filiais");
  const [branches, setBranches] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredBranches, setFilteredBranches] = useState([]);

  const firstLetter = client?.tradeName?.charAt(0) || "";
  const lastLetter = client?.tradeName?.slice(-1) || "";

  const firstLetterBranch = selectedBranch?.name?.charAt(0) || "";
  const lastLetterBranch = selectedBranch?.name?.slice(-1) || "";

  const fetchBranches = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client?.idClient}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
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
        branch.name.toLowerCase().includes(searchTerm) ||
        branch.cnpj.includes(term)
    );
    setFilteredBranches(filtered);
  };

  const getEmployee = async () => {
    setEmployees([]);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/employee?idSearch=${selectedBranch?.idBranch}&enterprise=CLIENT`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      console.log(res.data.content);

      setEmployees(res.data.content);
    } catch (error) {
      console.log("Erro ao buscar colaboradores:", error);
    }
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getEmployee();
    }
  }, [selectedBranch?.idBranch]);

  useEffect(() => {
    if (client?.idClient) {
      fetchBranches();
    }
  }, [selectedBranch?.idBranch, client?.idClient]);

  if (user?.role === "ROLE_CLIENT_MANAGER") {
    return (
      <BranchResume
        firstLetter={firstLetterBranch}
        lastLetter={lastLetterBranch}
        isLoading={!!selectedBranch}
        cnpj={selectedBranch?.cnpj}
        email={selectedBranch?.email}
        name={selectedBranch?.name}
      />
    );
  }

  return (
    <div className="flex flex-col items-center justify-center gap-5 p-10">
      <div className="flex gap-4">
        <div className="flex w-[50vw] items-start justify-between rounded-lg border bg-white p-10 shadow-lg">
          <div className="flex gap-3">
            <div className="bg-realizaBlue flex h-[16vh] w-[8vw] items-center justify-center rounded-full p-7">
              <div className="text-[40px] text-white">
                {firstLetter}
                {lastLetter}
              </div>
            </div>
            <div className="flex flex-col gap-10">
              <div className="flex flex-col items-start gap-3">
                <div className="text-realizaBlue text-[30px] font-medium">
                  {client ? (
                    <h2>{client?.tradeName}</h2>
                  ) : (
                    <Skeleton className="h-[1.5vh] w-[15vw] rounded-full bg-gray-600" />
                  )}
                </div>
                <div className="ml-1 text-sky-900">
                  {client ? (
                    <h3>{client?.corporateName}</h3>
                  ) : (
                    <Skeleton className="h-[1.5vh] w-[8vw] rounded-full bg-gray-600" />
                  )}
                </div>
              </div>
              <div className="flex flex-col gap-1 text-[13px] text-sky-900">
                <div>
                  {client ? (
                    <p>{client?.email}</p>
                  ) : (
                    <Skeleton className="h-[0.8vh] w-[7vw] rounded-full bg-gray-600" />
                  )}
                </div>
                <div>
                  {client ? (
                    <p>{client?.cnpj}</p>
                  ) : (
                    <Skeleton className="h-[0.6vh] w-[5vw] rounded-full bg-gray-600" />
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
        <EditModalEnterprise />
      </div>
      <div className="mr-10 rounded-lg border bg-white p-8 shadow-lg">
        <div className="flex flex-col items-start gap-4">
          <div className="">
            <nav className="flex items-center">
              <Button
                variant={"ghost"}
                className={`bg-realizaBlue px-4 py-2 transition-all duration-300 ${
                  selectTab === "filiais"
                    ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                    : "text-realizaBlue bg-white"
                }`}
                onClick={() => setSelectedTab("filiais")}
              >
                Filiais
              </Button>
              <Button
                variant={"ghost"}
                className={`bg-realizaBlue px-4 py-2 transition-all duration-300${
                  selectTab === "usuarios"
                    ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                    : "text-realizaBlue bg-white"
                }`}
                onClick={() => setSelectedTab("usuarios")}
              >
                Usuários
              </Button>
            </nav>
          </div>
          {selectTab === "filiais" && (
            <div>
              <div className="flex items-center gap-5">
                <div className="text-sm font-semibold text-sky-900">
                  Buscar Filial:
                </div>
                <input
                  type="text"
                  value={searchTerm}
                  onChange={handleSearch}
                  placeholder="Pesquisar filiais"
                  className="w-64 rounded-md border p-2"
                />
              </div>
              <div className="mt-4">
                <table className="mt-4 w-[40vw] border-collapse border border-gray-300">
                  <thead>
                    <tr>
                      <th className="border border-gray-300 px-4 py-2 text-start">
                        Filiais
                      </th>
                      <th className="border">CNPJ</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredBranches && filteredBranches.length > 0 ? (
                      filteredBranches.map((branch: any) => (
                        <tr key={branch.idBranch}>
                          <td className="border border-gray-300 px-4 py-2">
                            <li className="text-realizaBlue">{branch.name}</li>
                          </td>
                          <td className="text-center">{branch.cnpj}</td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td
                          colSpan={3}
                          className="border border-gray-300 px-4 py-2 text-center"
                        >
                          Nenhuma filial encontrada
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          )}
          {selectTab === "usuarios" && (
            <div>
              <table className="mt-4 w-[40vw] border-collapse border border-gray-300">
                <thead>
                  <tr>
                    <th className="border border-gray-300 px-4 py-2">Nome</th>
                    <th className="border border-gray-300 px-4 py-2">Status</th>
                    <th className="border border-gray-300 px-4 py-2">Ações</th>
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
                                ? "text-green-500"
                                : "text-red-500"
                            }
                          >
                            {employee.situation}
                          </span>
                        </td>
                        <td className="border border-gray-300 px-4 py-2">
                          <Link
                            to={`/sistema/detailsEmployees/${employee.idEmployee}`}
                          >
                            <button className="text-realizaBlue ml-4 hover:underline">
                              <Settings2 />
                            </button>
                          </Link>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td
                        colSpan={3}
                        className="border border-gray-300 px-4 py-2 text-center"
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
    </div>
  );
}
