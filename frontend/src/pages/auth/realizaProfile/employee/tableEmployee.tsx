// import { useBranch } from "@/context/Branch-provider";
// import { useClient } from "@/context/Client-Provider";
import { Button } from "@/components/ui/button";
import { useUser } from "@/context/user-provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { Eye, Search, User } from "lucide-react";
import { useEffect, useState } from "react";
import { Blocks } from "react-loader-spinner";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Link } from "react-router-dom";

interface TableEmployeeProps {
  idProvider: string | null;
}

export function TableEmployee({ idProvider }: TableEmployeeProps) {
  const [employees, setEmployee] = useState([]);
  const { user } = useUser();
  const [searchTerm, setSearchTerm] = useState(""); // Estado para o termo de busca
  const [isLoading, setIsLoading] = useState(false);
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
    setIsLoading(true);
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
    } finally {
      setIsLoading(false);
    }
  };

  const filteredEmployees = employees.filter((employee: any) =>
    `${employee.name} ${employee.surname}`
      .toLowerCase()
      .includes(searchTerm.toLowerCase()),
  );

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
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
      <div className="relative bottom-[4vw] flex flex-col items-center justify-center gap-5">
        <div className="flex items-center gap-2">
          <span>Filtrar colaborador</span>
          <div className="flex w-[50vw] items-center gap-1 rounded-md border border-neutral-500 bg-white p-2">
            <Search />
            <input
              type="text"
              placeholder="Pesquisar por nome"
              value={searchTerm}
              onChange={handleSearchChange}
              className="bg-transparent outline-none"
            />
          </div>
        </div>
        {isLoading ? (
          <div className="flex w-[80vw] flex-wrap justify-center gap-5 rounded-md bg-white p-10 shadow-lg">
            <Blocks
              height="80"
              width="80"
              color="#4fa94d"
              ariaLabel="blocks-loading"
              wrapperStyle={{}}
              wrapperClass="blocks-wrapper"
              visible={true}
            />
          </div>
        ) : (
          <div className="flex w-[80vw] flex-wrap justify-center gap-5 rounded-md bg-white p-10 shadow-lg">
            {filteredEmployees.map((employee: any) => (
              <div className="flex items-start rounded-md border border-neutral-200 bg-white shadow-xl">
                <div className="flex w-[20vw] flex-col gap-9 p-10">
                  <div className="flex items-center justify-between">
                    <h1 className="text-[20px]">Contratos</h1>
                    <Dialog>
                      <DialogTrigger>
                        {" "}
                        <Button className="bg-realizaBlue">
                          Adicionar contratos +
                        </Button>
                      </DialogTrigger>
                      <DialogContent>
                        <DialogHeader>
                          <DialogTitle>
                            Alocar colaborador a um novo contrato
                          </DialogTitle>
                          <DialogDescription>
                            This action cannot be undone. This will permanently
                            delete your account and remove your data from our
                            servers.
                          </DialogDescription>
                        </DialogHeader>
                      </DialogContent>
                    </Dialog>
                  </div>
                  <div className="flex flex-col gap-5">
                    <div className="flex items-center justify-between">
                      <h2 className="text-realizaBlue font-medium">
                        Industria Ultra gás - altura
                      </h2>
                      <Dialog>
                        <DialogTrigger>
                          <Eye />
                        </DialogTrigger>
                        <DialogContent>
                          <DialogHeader>
                            <DialogTitle>Are you absolutely sure?</DialogTitle>
                            <DialogDescription>
                              This action cannot be undone. This will
                              permanently delete your account and remove your
                              data from our servers.
                            </DialogDescription>
                          </DialogHeader>
                        </DialogContent>
                      </Dialog>
                    </div>
                    <div className="flex items-center justify-between">
                      <h2 className="text-realizaBlue font-medium">
                        Industria Ultra gás - altura
                      </h2>
                      <Dialog>
                        <DialogTrigger>
                          <Eye />
                        </DialogTrigger>
                        <DialogContent>
                          <DialogHeader>
                            <DialogTitle>Are you absolutely sure?</DialogTitle>
                            <DialogDescription>
                              This action cannot be undone. This will
                              permanently delete your account and remove your
                              data from our servers.
                            </DialogDescription>
                          </DialogHeader>
                        </DialogContent>
                      </Dialog>
                    </div>
                  </div>
                  <Dialog>
                    <DialogTrigger className="">
                      {" "}
                      <Button className="bg-realizaBlue w-full">
                        Ver todos os contratos
                      </Button>
                    </DialogTrigger>
                    <DialogContent>
                      <DialogHeader>
                        <DialogTitle>Todos os contratos</DialogTitle>
                      </DialogHeader>
                      <div className="flex flex-col gap-2">
                        <div className="flex items-center justify-between">
                          <h2 className="text-realizaBlue font-medium">
                            Industria Ultra gás - altura
                          </h2>
                          <Dialog>
                            <DialogTrigger>
                              <Eye />
                            </DialogTrigger>
                            <DialogContent>
                              <DialogHeader>
                                <DialogTitle>
                                  Are you absolutely sure?
                                </DialogTitle>
                                <DialogDescription>
                                  This action cannot be undone. This will
                                  permanently delete your account and remove
                                  your data from our servers.
                                </DialogDescription>
                              </DialogHeader>
                            </DialogContent>
                          </Dialog>
                        </div>
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
                    </DialogContent>
                  </Dialog>
                </div>
                <div key={employee.idEmployee} className="w-[15vw] p-4">
                  <div className="flex flex-col gap-5">
                    <Link to={`/fornecedor/detailsEmployees/${employee.idEmployee}`} className="flex gap-2 hover:bg-neutral-300 p-2 rounded-md">
                      <div className="rounded-full bg-neutral-200 p-2">
                        <User />
                      </div>
                      <div>
                        <h3 className="font-medium">
                          {employee.name} {employee.surname}
                        </h3>
                        <p className="text-[12px] text-sky-950 underline">
                          {employee.position}
                        </p>
                      </div>
                    </Link>
                    <div className="flex flex-col gap-5">
                      <span className="text-[14px] font-medium text-sky-950">
                        Informações do colaborador
                      </span>
                      <div className="flex items-center gap-1 text-[14px]">
                        <p>Nome completo:</p>
                        <p>
                          {employee.name} {employee.surname}
                        </p>
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
        )}
      </div>
    );
  }

  if (user?.role === "ROLE_CLIENT_RESPONSIBLE" && "ROLE_CLIENT_MANAGER") {
    return (
      <div className="flex flex-col items-center justify-center gap-5">
        <div className="flex items-center gap-2">
          <span>Filtrar colaborador</span>
          <div className="flex w-[50vw] items-center gap-1 rounded-md border border-neutral-500 bg-white p-2">
            <Search />
            <input
              type="text"
              placeholder="Pesquisar por nome"
              value={searchTerm}
              onChange={handleSearchChange}
              className="bg-transparent outline-none"
            />
          </div>
        </div>
        {isLoading ? (
          <div className="flex w-[80vw] flex-wrap justify-center gap-5 rounded-md bg-white p-10 shadow-lg">
            <Blocks
              height="80"
              width="80"
              color="#4fa94d"
              ariaLabel="blocks-loading"
              wrapperStyle={{}}
              wrapperClass="blocks-wrapper"
              visible={true}
            />
          </div>
        ) : (
          <div className="flex w-[80vw] flex-wrap justify-center gap-5 rounded-md bg-white p-10 shadow-lg">
            {filteredEmployees.map((employee: any) => (
              <div className="flex items-start rounded-md border border-neutral-200 bg-white shadow-xl">
                <div className="flex w-[20vw] flex-col gap-9 p-10">
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
                  <Button className="bg-realizaBlue">
                    Ver todos os contratos
                  </Button>
                </div>
                <div key={employee.idEmployee} className="w-[15vw] p-4">
                  <div className="flex flex-col gap-5">
                    <div className="flex gap-2">
                      <div className="rounded-full bg-neutral-200 p-2">
                        <User />
                      </div>
                      <div>
                        <h3 className="font-medium">
                          {employee.name} {employee.surname}
                        </h3>
                        <p className="text-[12px] text-sky-950 underline">
                          {employee.position}
                        </p>
                      </div>
                    </div>
                    <div className="flex flex-col gap-5">
                      <span className="text-[14px] font-medium text-sky-950">
                        Informações do colaborador
                      </span>
                      <div className="flex items-center gap-1 text-[14px]">
                        <p>Nome completo:</p>
                        <p>
                          {employee.name} {employee.surname}
                        </p>
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
        )}
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center justify-center gap-5">
      <div className="flex items-center gap-2">
        <span>Filtrar colaborador</span>
        <div className="flex w-[50vw] items-center gap-1 rounded-md border border-neutral-500 bg-white p-2">
          <Search />
          <input
            type="text"
            placeholder="Pesquisar por nome"
            value={searchTerm}
            onChange={handleSearchChange}
            className="bg-transparent outline-none"
          />
        </div>
      </div>
      {isLoading ? (
        <div className="flex w-[80vw] flex-wrap justify-center gap-5 rounded-md bg-white p-10 shadow-lg">
          <Blocks
            height="80"
            width="80"
            color="#4fa94d"
            ariaLabel="blocks-loading"
            wrapperStyle={{}}
            wrapperClass="blocks-wrapper"
            visible={true}
          />
        </div>
      ) : (
        <div className="flex w-[80vw] flex-wrap justify-center gap-5 rounded-md bg-white p-10 shadow-lg">
          {filteredEmployees.map((employee: any) => (
            <div className="flex items-start rounded-md border border-neutral-200 bg-white shadow-xl">
              <div className="flex w-[20vw] flex-col gap-9 p-10">
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
                    <Dialog>
                      <DialogTrigger>
                        {" "}
                        <div>
                          <Eye />
                        </div>
                      </DialogTrigger>
                      <DialogContent>
                        <DialogHeader>
                          <DialogTitle>Are you absolutely sure?</DialogTitle>
                          <DialogDescription>
                            This action cannot be undone. This will permanently
                            delete your account and remove your data from our
                            servers.
                          </DialogDescription>
                        </DialogHeader>
                      </DialogContent>
                    </Dialog>
                  </div>
                </div>
                <Button className="bg-realizaBlue">
                  Ver todos os contratos
                </Button>
              </div>
              <div key={employee.idEmployee} className="w-[15vw] p-4">
                <div className="flex flex-col gap-5">
                  <div className="flex gap-2">
                    <div className="rounded-full bg-neutral-200 p-2">
                      <User />
                    </div>
                    <div>
                      <h3 className="font-medium">
                        {employee.name} {employee.surname}
                      </h3>
                      <p className="text-[12px] text-sky-950 underline">
                        {employee.position}
                      </p>
                    </div>
                  </div>
                  <div className="flex flex-col gap-5">
                    <span className="text-[14px] font-medium text-sky-950">
                      Informações do colaborador
                    </span>
                    <div className="flex items-center gap-1 text-[14px]">
                      <p>Nome completo:</p>
                      <p>
                        {employee.name} {employee.surname}
                      </p>
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
      )}
    </div>
  );
}
