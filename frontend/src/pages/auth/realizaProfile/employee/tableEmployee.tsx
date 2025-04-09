// import { useBranch } from "@/context/Branch-provider";
// import { useClient } from "@/context/Client-Provider";
import { useUser } from "@/context/user-provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { Settings2 } from "lucide-react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

interface TableEmployeeProps {
  idProvider: string | null;
}

export function TableEmployee({ idProvider }: TableEmployeeProps) {
  const [employees, setEmployee] = useState([]);
  const  {user} = useUser()
  // const [branches, setBranches] = useState([]);
  // const [selectedBranch, setSelectedBranch] = useState("");
  // const { client } = useClient();
  // const { selectedBranch } = useBranch();

  const page = 0; // Número da página
  const limit = 10; // Quantidade de itens por página

  // const getBranchClient = async () => {
  //   if (!client?.idClient) return;
  //   try {
  //     const res = await axios.get(
  //       `${ip}/branch/filtered-client?idSearch=${client.idClient}`
  //     );
  //     setBranches(res.data.content);
  //     console.log("Filiais:", res.data.content);
  //   } catch (err) {
  //     console.log("Erro ao buscar filial do cliente", err);
  //   }
  // };

  const getEmployee = async () => {
    console.log("idProvider watch:", idProvider);
    try {
      const res = await axios.get(
        `${ip}/employee?idSearch=${idProvider}&enterprise=SUPPLIER`,
        {
          params: {
            page: page,
            limit: limit,
          },
        },
      );
      console.log("employees:", res.data.content);

      setEmployee(res.data.content);
    } catch (error) {
      console.log("Erro ao buscar colaboradores:", error);
    }
  };

  console.log("colaboradores da branch:", employees);

  useEffect(() => {
    if (idProvider) {
      getEmployee();
    }
    setEmployee([]);
  }, [idProvider]);

  // const handleBranchChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
  //   const branchId = e.target.value;
  //   // setSelectedBranch(branchId);
  //   getEmployee(branchId);
  // };

  if(user?.role === "ROLE_SUPPLIER_RESPONSIBLE" && "ROLE_SUPPLIER_MANAGER") {
    return (
      <div>
        <table className="mt-4 min-w-full border-collapse border border-gray-300">
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
                    <Link to={`/fornecedor/detailsEmployees/${employee.idEmployee}`}>
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

  if(user?.role === "ROLE_CLIENT_RESPONSIBLE" && "ROLE_CLIENT_MANAGER") {
    return (
      <div>
        <table className="mt-4 min-w-full border-collapse border border-gray-300">
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
                    <Link to={`/fornecedor/detailsEmployees/${employee.idEmployee}`}>
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


  return (
    <div>
      <table className="mt-4 min-w-full border-collapse border border-gray-300">
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
