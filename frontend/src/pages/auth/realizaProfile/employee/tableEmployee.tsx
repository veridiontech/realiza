import { useClient } from "@/context/Client-Provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { Settings2 } from "lucide-react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

export function TableEmployee() {
  const [employees, setEmployee] = useState([]);
  const [branches, setBranches] = useState([]);
  const [selectedBranch, setSelectedBranch] = useState("");
  const { client } = useClient();

  const page = 0; // Número da página
  const limit = 10; // Quantidade de itens por página

  const getBranchClient = async () => {
    if (!client?.idClient) return;
    try {
      const res = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client.idClient}`
      );
      setBranches(res.data.content);
      console.log("Filiais:", res.data.content);
    } catch (err) {
      console.log("Erro ao buscar filial do cliente", err);
    }
  };

  const getEmployee = async (idBranch: string) => {

    console.log("idBranch: ", idBranch);
    try {
      const res = await axios.get(
        `${ip}/employee?idSearch=${idBranch}&enterprise=CLIENT`, {
          params: {
            page: page,
            limit: limit,
          }
        }
      );
      setEmployee(res.data.content);
    } catch (error) {
      console.log("Erro ao buscar colaboradores:", error);
    }
  };

  console.log("colaboradores da branch:", employees,);
  

  useEffect(() => {
    if (client?.idClient) {
      getBranchClient();
      setSelectedBranch("")
      setEmployee([])
    }
  }, [client?.idClient]);

  const handleBranchChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const branchId = e.target.value;
    setSelectedBranch(branchId);
    getEmployee(branchId);
  };

  return (
    <div>
      <table className="min-w-full border-collapse border border-gray-300 mt-4">
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
              <tr key={employee.id}>
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
                  <Link to={`/sistema/detailsEmployees/${employee.idEmployee}`}>
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
  );
}
