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
  const [searchTerm, setSearchTerm] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [detailsContract, setDetailsContract] = useState<any>(null);

  const page = 0;
  const limit = 10;

      // const tokenFromStorage = localStorage.getItem("tokenClient");

const getEmployee = async () => {
  setIsLoading(true);
  try {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    const res = await axios.get(
      `${ip}/employee?idSearch=${idProvider}&enterprise=SUPPLIER`,
      {
        params: { page, limit },
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      }
    );
    setEmployee(res.data?.content ?? []);
  } catch (error) {
    console.error("Erro ao buscar colaboradores:", error);
  } finally {
    setIsLoading(false);
  }
};

  const filteredEmployees = employees.filter((employee: any) =>
    `${employee.name} ${employee.surname}`
      .toLowerCase()
      .includes(searchTerm.toLowerCase())
  );

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  console.log("colaboradores da branch:", employees);

useEffect(() => {
  if (idProvider) {
    getEmployee();
  } else {
    setEmployee([]);
  }
}, [idProvider]);

const getMoreDetailsDocument = async (id: string) => {
  const tokenFromStorage = localStorage.getItem("tokenClient");
  try {
    const res = await axios.get(`${ip}/contract/supplier/${id}`, {
      headers: {
        Authorization: `Bearer ${tokenFromStorage}`,
      },
    });
    console.log("detalhes:", res.data);
    
    const contractDetails = res.data;
    setDetailsContract(contractDetails);
  } catch (err: any) {
    console.error("Erro ao buscar detalhes do contrato:", err);
  }
};

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
                    <Link
                      to={`/fornecedor/detailsEmployees/${employee.idEmployee}`}
                      className="flex gap-2 hover:bg-neutral-300 p-2 rounded-md"
                    >
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
                        <p>{employee.cboId}</p>
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
                        <p>{employee.cboId}</p>
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
    <div className="flex flex-col items-center justify-center gap-5  relative xl:top-[6vw] md:top-[10vw]">
      {isLoading ? (
        <div className="flex  w-[95vw] flex-wrap justify-center gap-5 rounded-md p-10 shadow-lg  bg-white">
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
        <div className="flex  flex-col w-[95vw]  gap-5 rounded-md p-10 shadow-lg  bg-white">
          <div>
            <div className="flex flex-col gap-2">
              <span>Filtrar colaborador</span>
              <div className="flex w-[50vw] items-center gap-1 rounded-md border border-neutral-500  p-2">
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
          </div>
          <div className="flex justify-center gap-5 items-center">
            {filteredEmployees.map((employee: any) => (
              <div className="flex items-start rounded-md border border-neutral-200 bg-white shadow-xl ">
                <div className="flex w-[25vw] h-[50vh] flex-col gap-9 p-10">
                  <div className="flex items-center justify-between">
                    <h1 className="text-[20px]">Contratos</h1>
                    <Button className="bg-realizaBlue">
                      Adicionar contratos +
                    </Button>
                  </div>
                  <div className="flex flex-col gap-5">
                    {employee.contracts.map((contract: any) => (
                      <div
                        key={contract.idContract}
                        className="flex items-center justify-between"
                      >
                        <h2 className="text-realizaBlue font-medium">
                          {contract.serviceName}
                        </h2>
                        <Dialog>
                          <DialogTrigger
                            className=""
                            onClick={() =>
                              getMoreDetailsDocument(contract.idContract)
                            }
                          >
                            {" "}
                            <div>
                              <Eye />
                            </div>
                          </DialogTrigger>
                          <DialogContent>
                            <DialogHeader>
                              <DialogTitle>Detalhes do contrato</DialogTitle>
                            </DialogHeader>
                            <div>
                              {detailsContract ? (
                                <div>
                                  <div key={detailsContract.idContract} className="flex flex-col gap-2">
                                    <h2><strong>Nome do serviço: </strong> {detailsContract.serviceName}</h2>
                                    <h3>
                                      <strong>Referência do contrato:{" "}</strong>
                                      
                                      {detailsContract.contractReference}
                                    </h3>
                                    <span className="">
                                      <strong>Tipo de despesa:</strong>{" "}
                                      {detailsContract.expenseType}
                                    </span>
                                    <span><strong>Data de início: </strong>{detailsContract.dateStart}</span>
                                  </div>
                                  <div>
                                    <h2>Responsável do serviço: {detailsContract.responsible|| "Não informado"}</h2>
                                    
                                  </div>
                                </div>
                              ) : (
                                <div></div>
                              )}
                            </div>
                          </DialogContent>
                        </Dialog>
                      </div>
                    ))}
                  </div>
                  <Button className="bg-realizaBlue">
                    Ver todos os contratos
                  </Button>
                </div>
                <Link
                  to={`/sistema/detailsEmployees/${employee.idEmployee}`}
                  key={employee.idEmployee}
                  className="w-[15vw] p-4"
                >
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
                        <p>{employee.cboId}</p>
                      </div>
                    </div>
                  </div>
                </Link>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
