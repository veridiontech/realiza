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
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Link } from "react-router-dom";

interface TableEmployeeProps {
  /** id do alvo (fornecedor OU subcontratado) */
  idTarget: string | null;
  /** controla qual empresa o backend deve filtrar */
  targetType: "supplier" | "subcontractor";
}

export function TableEmployee({ idTarget, targetType }: TableEmployeeProps) {
  const [employees, setEmployee] = useState<any[]>([]);
  const { user } = useUser();
  const [searchTerm, setSearchTerm] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [detailsContract, setDetailsContract] = useState<any>(null);

  const page = 0;
  const limit = 10;

  const getEmployee = async () => {
    if (!idTarget) {
      setEmployee([]);
      return;
    }

    setIsLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const enterprise = targetType === "supplier" ? "SUPPLIER" : "SUBCONTRACTOR";
      const res = await axios.get(
        `${ip}/employee?idSearch=${idTarget}&enterprise=${enterprise}`,
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

  useEffect(() => {
    if (idTarget) {
      getEmployee();
    } else {
      setEmployee([]);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [idTarget, targetType]);

  const getMoreDetailsDocument = async (id: string) => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      // Se houver endpoint específico para subcontratado, troque aqui.
      const res = await axios.get(`${ip}/contract/supplier/${id}`, {
        headers: {
          Authorization: `Bearer ${tokenFromStorage}`,
        },
      });
      setDetailsContract(res.data);
    } catch (err: any) {
      console.error("Erro ao buscar detalhes do contrato:", err);
    }
  };

  const getStatusClass = (value: string) => {
    if (value === "DESALOCADO") {
      return "text-red-600";
    } else if (value === "ALOCADO") {
      return "text-green-600";
    } else {
      return "text-gray-600";
    }
  };

  const getTriangleColor = (status: string) => {
    if (status === "ALOCADO") {
      return "border-b-green-600";
    } else if (status === "DESALOCADO") {
      return "border-b-red-600";
    } else {
      return "border-b-gray-400";
    }
  };

  // === BLOCO 1 (fornecedor) ===
  if (user?.role === "ROLE_SUPPLIER_RESPONSIBLE" && "ROLE_SUPPLIER_MANAGER") {
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
            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
              {Array.isArray(filteredEmployees) &&
                filteredEmployees.map((employee: any) => (
                  <div key={employee.idEmployee} className="bg-white rounded-xl shadow-md border border-gray-200 overflow-hidden flex flex-col md:flex-row">
                    <div className="p-6 w-full md:w-1/2 border-r border-gray-100">
                      <div className="flex items-center justify-between">
                        <h1 className="text-[20px]">Contratos</h1>
                        <Button className="bg-realizaBlue">
                          Adicionar contratos +
                        </Button>
                      </div>
                      <div className="flex flex-col gap-5">
                        {Array.isArray(employee.contracts) &&
                          employee.contracts.map((contract: any) => (
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
                                  <div>
                                    <Eye />
                                  </div>
                                </DialogTrigger>
                                <DialogContent>
                                  <DialogHeader>
                                    <DialogTitle>
                                      Detalhes do contrato
                                    </DialogTitle>
                                  </DialogHeader>
                                  <div>
                                    {detailsContract ? (
                                      <div>
                                        <div
                                          key={detailsContract.idContract}
                                          className="flex flex-col gap-2"
                                        >
                                          <h2>
                                            <strong>Nome do serviço: </strong>{" "}
                                            {detailsContract.serviceName}
                                          </h2>
                                          <h3>
                                            <strong>
                                              Referência do contrato:{" "}
                                            </strong>
                                            {detailsContract.contractReference}
                                          </h3>
                                          <span className="">
                                            <strong>Tipo de despesa:</strong>{" "}
                                            {detailsContract.expenseType}
                                          </span>
                                          <span>
                                            <strong>Data de início: </strong>
                                            {detailsContract.dateStart}
                                          </span>
                                        </div>
                                        <div>
                                          <h2>
                                            Responsável do serviço:{" "}
                                            {detailsContract.responsible ||
                                              "Não informado"}
                                          </h2>
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
                      className="w-[12vw] p-3"
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
                            <p>{employee.cboTitle}</p>
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

  // === BLOCO 2 (cliente) ===
  if (user?.role === "ROLE_CLIENT_RESPONSIBLE" && "ROLE_CLIENT_MANAGER") {
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
            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
              {Array.isArray(filteredEmployees) &&
                filteredEmployees.map((employee: any) => (
                  <div key={employee.idEmployee} className="bg-white rounded-xl shadow-md border border-gray-200 overflow-hidden flex flex-col md:flex-row">
                    <div className="p-6 w-full md:w-1/2 border-r border-gray-100">
                      <div className="flex items-center justify-between">
                        <h1 className="text-[20px]">Contratos</h1>
                        <Button className="bg-realizaBlue">
                          Adicionar contratos +
                        </Button>
                      </div>
                      <div className="flex flex-col gap-5">
                        {Array.isArray(employee.contracts) &&
                          employee.contracts.map((contract: any) => (
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
                                  <div>
                                    <Eye />
                                  </div>
                                </DialogTrigger>
                                <DialogContent>
                                  <DialogHeader>
                                    <DialogTitle>
                                      Detalhes do contrato
                                    </DialogTitle>
                                  </DialogHeader>
                                  <div>
                                    {detailsContract ? (
                                      <div>
                                        <div
                                          key={detailsContract.idContract}
                                          className="flex flex-col gap-2"
                                        >
                                          <h2>
                                            <strong>Nome do serviço: </strong>{" "}
                                            {detailsContract.serviceName}
                                          </h2>
                                          <h3>
                                            <strong>
                                              Referência do contrato:{" "}
                                            </strong>
                                            {detailsContract.contractReference}
                                          </h3>
                                          <span className="">
                                            <strong>Tipo de despesa:</strong>{" "}
                                            {detailsContract.expenseType}
                                          </span>
                                          <span>
                                            <strong>Data de início: </strong>
                                            {detailsContract.dateStart}
                                          </span>
                                        </div>
                                        <div>
                                          <h2>
                                            Responsável do serviço:{" "}
                                            {detailsContract.responsible ||
                                              "Não informado"}
                                          </h2>
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
                      className="w-[12vw] p-3"
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
                            <p>{employee.cboTitle}</p>
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

  // === fallback ===
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
        <div className="flex  flex-col w-[95vw]  gap-5 rounded-md p-10 shadow-lg bg-white">
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
          <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5">
            {Array.isArray(filteredEmployees) &&
              filteredEmployees.map((employee: any) => (
                <div key={employee.idEmployee} className="bg-slate-50 rounded-xl shadow-md border border-gray-200 overflow-hidden flex flex-col md:flex-row ">
                  <div className="p-6 w-full md:w-1/2 border-r border-gray-100 flex flex-col justify-between">
                    <div className="">
                      <div className="flex items-center justify-between">
                        <h1 className="text-[20px]">Contratos</h1>
                      </div>
                      <hr className="mt-2 mb-5" />
                      <div className="flex flex-col gap-5">
                        {Array.isArray(employee.contracts) &&
                          employee.contracts
                            .slice(0, 2)
                            .map((contract: any) => (
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
                                      getMoreDetailsDocument(
                                        contract.idContract
                                      )
                                    }
                                  >
                                    <div>
                                      <Eye height={20} width={20} />
                                    </div>
                                  </DialogTrigger>
                                  <DialogContent>
                                    <DialogHeader>
                                      <DialogTitle>
                                        Detalhes do contrato
                                      </DialogTitle>
                                    </DialogHeader>
                                    <div>
                                      {detailsContract ? (
                                        <div>
                                          <div
                                            key={detailsContract.idContract}
                                            className="flex flex-col gap-2"
                                          >
                                            <h2>
                                              <strong>Nome do serviço: </strong>{" "}
                                              {detailsContract.serviceName}
                                            </h2>
                                            <h3>
                                              <strong>
                                                Referência do contrato:{" "}
                                              </strong>
                                              {
                                                detailsContract.contractReference
                                              }
                                            </h3>
                                            <span className="">
                                              <strong>Tipo de despesa:</strong>{" "}
                                              {detailsContract.expenseType}
                                            </span>
                                            <span>
                                              <strong>Data de início: </strong>
                                              {detailsContract.dateStart}
                                            </span>
                                          </div>
                                          <div>
                                            <h2>
                                              Responsável do serviço:{" "}
                                              {detailsContract.responsible ||
                                                "Não informado"}
                                            </h2>
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
                    </div>
                    <Button className="bg-realizaBlue">
                      Ver todos os contratos
                    </Button>
                  </div>
                  <Link
                    to={`/sistema/detailsEmployees/${employee.idEmployee}`}
                    className="w-full md:w-1/2 p-3 bg-white"
                  >
                    <div className="flex flex-col gap-5">
                      <div className="flex gap-2">
                        <div className="rounded-md bg-neutral-200 p-2">
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
                          <p>Tipo de contrato:</p>
                          <p>{employee.contractType}</p>
                        </div>
                        <div className="flex items-center gap-1 text-[14px]">
                          <p>CBO:</p>
                          <p>{employee.cboTitle}</p>
                        </div>
                        <div className="relative flex py-2 items-center overflow-hidden gap-1 text-[14px]">
                          <p>Status:</p>
                          <p
                            className={`font-semibold ${getStatusClass(employee.situation)}`}
                          >
                            {employee.situation}
                          </p>
                          <div
                            className={`absolute bottom-0 right-0 w-0 h-0 border-b-[40px] border-l-[40px] border-l-transparent ${getTriangleColor(employee.situation)}`}
                          ></div>
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
