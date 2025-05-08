// import { useBranch } from "@/context/Branch-provider";
// import { useClient } from "@/context/Client-Provider";
import { Button } from "@/components/ui/button";
import { useUser } from "@/context/user-provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { Eye, Settings2, User } from "lucide-react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

interface TableEmployeeProps {
  idProvider: string | null;
}

export function TableEmployee({ idProvider }: TableEmployeeProps) {
  const [employees, setEmployee] = useState([]);
  const { user } = useUser();
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
    // console.log("idProvider watch:", idProvider);
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

  if (user?.role === "ROLE_SUPPLIER_RESPONSIBLE" && "ROLE_SUPPLIER_MANAGER") {
    return (
      <div className="flex flex-wrap gap-5 justify-center">
        {employees.map((employee: any) => (
          <div className="bg-white flex items-start border border-neutral-200 shadow-lg rounded-md">
            <div className="flex w-[20vw] flex-col gap-9  p-10">
              <div className="flex items-center justify-between">
                <h1 className="text-[20px]">Contratos</h1>
                <Button className="bg-realizaBlue">
                  Adicionar contratos +
                </Button>
              </div>
              <div className="flex flex-col gap-5">
                <div className="flex items-center justify-between">
                  <h2 className="text-realizaBlue font-medium">
                    Industria Ultra gás - altura
                  </h2>
                  <div>
                    <Eye />
                  </div>
                </div>
                <div className="flex items-center justify-between">
                  <h2 className="text-realizaBlue font-medium">
                    Industria Ultra gás - altura
                  </h2>
                  <div>
                    <Eye />
                  </div>
                </div>
              </div>
              <Button className="bg-realizaBlue">Ver todos os contratos</Button>
            </div>
            <div key={employee.idEmployee} className="p-4 w-[15vw]">
              <div className="flex flex-col gap-5">
              <div className="flex gap-2">
                <div className="bg-neutral-200 p-2 rounded-full"><User /></div>
                <div>
                  <h3 className="font-medium">{employee.name} {employee.surname}</h3>
                  <p className="text-sky-950 underline text-[12px]">{employee.position}</p>
                </div>
              </div>
              <div className="flex flex-col gap-5">
                <span className="text-sky-950 font-medium text-[14px]">Informações do colaborador</span>
                <div className="flex items-center gap-1 text-[14px]">
                  <p>Nome completo:</p>
                  <p>{employee.name} {employee.surname}</p>
                </div>
                <div className="flex items-center gap-1 text-[14px]">
                  <p>Status:</p>
                  <p>{employee.situation}</p>
                </div>
                <div className="flex items-center gap-1 text-[14px]">
                  <p>Tipo de contrato:</p>
                  <p>{employee.contractType}</p>
                </div>
                <div className="flex items-center gap-1 text-[14px]">
                  <p>CBO:</p>
                  <p>{employee.cbo}</p>
                </div>
              </div>
            </div>
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (user?.role === "ROLE_CLIENT_RESPONSIBLE" && "ROLE_CLIENT_MANAGER") {
    return (
      <div className="flex flex-wrap gap-5 justify-center">
        {employees.map((employee: any) => (
          <div className="bg-white flex items-start border border-neutral-200 shadow-lg rounded-md">
            <div className="flex w-[20vw] flex-col gap-9  p-10">
              <div className="flex items-center justify-between">
                <h1 className="text-[20px]">Contratos</h1>
                <Button className="bg-realizaBlue">
                  Adicionar contratos +
                </Button>
              </div>
              <div className="flex flex-col gap-5">
                <div className="flex items-center justify-between">
                  <h2 className="text-realizaBlue font-medium">
                    Industria Ultra gás - altura
                  </h2>
                  <div>
                    <Eye />
                  </div>
                </div>
                <div className="flex items-center justify-between">
                  <h2 className="text-realizaBlue font-medium">
                    Industria Ultra gás - altura
                  </h2>
                  <div>
                    <Eye />
                  </div>
                </div>
              </div>
              <Button className="bg-realizaBlue">Ver todos os contratos</Button>
            </div>
            <div key={employee.idEmployee} className="p-4 w-[15vw]">
              <div className="flex flex-col gap-5">
              <div className="flex gap-2">
                <div className="bg-neutral-200 p-2 rounded-full"><User /></div>
                <div>
                  <h3 className="font-medium">{employee.name} {employee.surname}</h3>
                  <p className="text-sky-950 underline text-[12px]">{employee.position}</p>
                </div>
              </div>
              <div className="flex flex-col gap-5">
                <span className="text-sky-950 font-medium text-[14px]">Informações do colaborador</span>
                <div className="flex items-center gap-1 text-[14px]">
                  <p>Nome completo:</p>
                  <p>{employee.name} {employee.surname}</p>
                </div>
                <div className="flex items-center gap-1 text-[14px]">
                  <p>Status:</p>
                  <p>{employee.situation}</p>
                </div>
                <div className="flex items-center gap-1 text-[14px]">
                  <p>Tipo de contrato:</p>
                  <p>{employee.contractType}</p>
                </div>
                <div className="flex items-center gap-1 text-[14px]">
                  <p>CBO:</p>
                  <p>{employee.cbo}</p>
                </div>
              </div>
            </div>
            </div>
          </div>
        ))}
      </div>
    );
  }

  return (
    <div className="flex flex-wrap gap-5 justify-center">
        {employees.map((employee: any) => (
          <div className="bg-white flex items-start border border-neutral-200 shadow-lg rounded-md">
            <div className="flex w-[20vw] flex-col gap-9  p-10">
              <div className="flex items-center justify-between">
                <h1 className="text-[20px]">Contratos</h1>
                <Button className="bg-realizaBlue">
                  Adicionar contratos +
                </Button>
              </div>
              <div className="flex flex-col gap-5">
                <div className="flex items-center justify-between">
                  <h2 className="text-realizaBlue font-medium">
                    Industria Ultra gás - altura
                  </h2>
                  <div>
                    <Eye />
                  </div>
                </div>
                <div className="flex items-center justify-between">
                  <h2 className="text-realizaBlue font-medium">
                    Industria Ultra gás - altura
                  </h2>
                  <div>
                    <Eye />
                  </div>
                </div>
              </div>
              <Button className="bg-realizaBlue">Ver todos os contratos</Button>
            </div>
            <div key={employee.idEmployee} className="p-4 w-[15vw]">
              <div className="flex flex-col gap-5">
              <div className="flex gap-2">
                <div className="bg-neutral-200 p-2 rounded-full"><User /></div>
                <div>
                  <h3 className="font-medium">{employee.name} {employee.surname}</h3>
                  <p className="text-sky-950 underline text-[12px]">{employee.position}</p>
                </div>
              </div>
              <div className="flex flex-col gap-5">
                <span className="text-sky-950 font-medium text-[14px]">Informações do colaborador</span>
                <div className="flex items-center gap-1 text-[14px]">
                  <p>Nome completo:</p>
                  <p>{employee.name} {employee.surname}</p>
                </div>
                <div className="flex items-center gap-1 text-[14px]">
                  <p>Status:</p>
                  <p>{employee.situation}</p>
                </div>
                <div className="flex items-center gap-1 text-[14px]">
                  <p>Tipo de contrato:</p>
                  <p>{employee.contractType}</p>
                </div>
                <div className="flex items-center gap-1 text-[14px]">
                  <p>CBO:</p>
                  <p>{employee.cbo}</p>
                </div>
              </div>
            </div>
            </div>
          </div>
        ))}
      </div>
  );
}
