import { useState, useEffect } from "react";
import axios from "axios";
import { toast } from "sonner";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Modal } from "@/components/modal";
import { ip } from "@/utils/ip";
import { useUser } from "@/context/user-provider";
import { fetchCompanyByCNPJ } from "@/hooks/gets/realiza/useCnpjApi";
import { Skeleton } from "@/components/ui/skeleton";
import { useClient } from "@/context/Client-Provider";
import { EditModalEnterprise } from "./profileEnterprise/edit-modal-enterprise";
import { useBranch } from "@/context/Branch-provider";
import { Settings2 } from "lucide-react";

interface CompanyData {
  razaoSocial: string;
  nomeFantasia: string;
  cep: string;
  state: string;
  city: string;
  address: string;
  number: string;
  telefone?: string;
}

const sanitizeNumber = (value: string) => value.replace(/\D/g, "");

export function AddClientWorkflow({ onClose }: { onClose: () => void }) {
  const [step, setStep] = useState(1);
  const [clientForm, setClientForm] = useState({
    cnpj: "",
    tradeName: "",
    corporateName: "",
    email: "",
    telephone: "",
    cep: "",
    state: "",
    city: "",
    address: "",
    number: "",
  });

  const [, setUserForm] = useState({
    cpf: "",
    firstName: "",
    surname: "",
    email: "",
    telephone: "",
    cellphone: "",
    password: "",
    branch: "",
  });

  const handleStep1Submit = async (formData: Record<string, any>) => {
    try {
      const { cnpj } = formData;
      const res: CompanyData = await fetchCompanyByCNPJ(cnpj);
      setClientForm({
        cnpj: sanitizeNumber(cnpj),
        tradeName: res.nomeFantasia,
        corporateName: res.razaoSocial,
        email: "",
        telephone: res.telefone ? sanitizeNumber(res.telefone) : "",
        cep: sanitizeNumber(res.cep),
        state: res.state,
        city: res.city,
        address: res.address,
        number: res.number,
      });
      setStep(2);
    } catch (error: any) {
      toast.error(error.message);
    }
  };

  const handleStep2Submit = async (formData: Record<string, any>) => {
    const requiredFields = [
      "cnpj",
      "tradeName",
      "corporateName",
      "email",
      "telephone",
      "cep",
      "state",
      "city",
      "address",
      "number",
    ];

    if (requiredFields.some((field) => !formData[field])) {
      toast.error("Preencha todos os campos obrigatórios.");
      return;
    }

    try {
      const sanitizedData = {
        ...formData,
        cnpj: sanitizeNumber(formData.cnpj),
        telephone: sanitizeNumber(formData.telephone),
        cep: sanitizeNumber(formData.cep),
      };

      console.log("Dados enviados para /client:", sanitizedData);

      const response = await axios.post(`${ip}/client`, sanitizedData, {
        headers: { "Content-Type": "application/json" },
      });
      toast.success("Cliente cadastrado com sucesso!");
      setUserForm((prev) => ({ ...prev, branch: response.data.idClient }));
      // Em vez de avançar para step 3, feche o modal após um pequeno delay
      setTimeout(() => {
        onClose();
      }, 1000);
    } catch (error) {
      console.error("Erro ao cadastrar cliente:", error);
      toast.error("Erro ao cadastrar cliente!");
    }
  };

  const handleStep3Submit = async (formData: Record<string, any>) => {
    const requiredFields = ["cpf", "firstName", "surname", "email", "password"];
    if (requiredFields.some((field) => !formData[field])) {
      toast.error("Preencha todos os campos obrigatórios.");
      return;
    }

    try {
      console.log("Dados enviados para /user/client:", formData);
      await axios.post(`${ip}/user/client`, formData, {
        headers: { "Content-Type": "application/json" },
      });
      toast.success("Usuário cadastrado com sucesso!");
      onClose();
    } catch (error) {
      console.error("Erro ao cadastrar usuário:", error);
      toast.error("Erro ao cadastrar usuário!");
    }
  };

  if (step === 1) {
    return (
      <Modal
        key="step1"
        title="Buscar CNPJ"
        fields={[
          {
            name: "cnpj",
            label: "CNPJ",
            type: "text",
            placeholder: "Digite o CNPJ",
            required: true,
          },
        ]}
        onSubmit={handleStep1Submit}
        onClose={onClose}
      />
    );
  }

  if (step === 2) {
    return (
      <Modal
        key={`step2-${clientForm.cnpj}`}
        title="Cadastrar Cliente"
        fields={[
          {
            name: "cnpj",
            label: "CNPJ",
            type: "text",
            defaultValue: clientForm.cnpj,
            required: true,
          },
          {
            name: "tradeName",
            label: "Nome Fantasia",
            type: "text",
            defaultValue: clientForm.tradeName,
            required: true,
          },
          {
            name: "corporateName",
            label: "Razão Social",
            type: "text",
            defaultValue: clientForm.corporateName,
            required: true,
          },
          {
            name: "email",
            label: "E-mail",
            type: "email",
            defaultValue: clientForm.email,
            required: true,
          },
          {
            name: "telephone",
            label: "Telefone",
            type: "text",
            defaultValue: clientForm.telephone,
            required: true,
          },
          {
            name: "cep",
            label: "CEP",
            type: "text",
            defaultValue: clientForm.cep,
            required: true,
          },
          {
            name: "state",
            label: "Estado",
            type: "text",
            defaultValue: clientForm.state,
            required: true,
          },
          {
            name: "city",
            label: "Cidade",
            type: "text",
            defaultValue: clientForm.city,
            required: true,
          },
          {
            name: "address",
            label: "Endereço",
            type: "text",
            defaultValue: clientForm.address,
            required: true,
          },
          {
            name: "number",
            label: "Número",
            type: "text",
            defaultValue: clientForm.number,
            required: true,
          },
        ]}
        onSubmit={handleStep2Submit}
        onClose={onClose}
      />
    );
  }

  return (
    <Modal
      key="step3"
      title="Cadastrar Usuário da Empresa"
      fields={[
        { name: "cpf", label: "CPF", type: "text", required: true },
        { name: "firstName", label: "Nome", type: "text", required: true },
        { name: "surname", label: "Sobrenome", type: "text", required: true },
        { name: "email", label: "E-mail", type: "email", required: true },
        { name: "telephone", label: "Telefone", type: "text", required: true },
        { name: "cellphone", label: "Celular", type: "text" },
        { name: "password", label: "Senha", type: "password", required: true },
      ]}
      onSubmit={handleStep3Submit}
      onClose={onClose}
    />
  );
}

export function SelectClient() {
  const [branches, setBranches] = useState([]);
  const [showAddWorkflow, setShowAddWorkflow] = useState(false);
  const navigate = useNavigate();
  const { user } = useUser();
  const { client } = useClient();
  const [selectedTab, setSelectedTab] = useState("filiais");
  const [employees, setEmployees] = useState([]);
  const { selectedBranch } = useBranch();

  const fetchBranches = async () => {
    try {
      const response = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client?.idClient}`,
      );
      const { content } = response.data;
      setBranches(content);
    } catch (err) {
      console.error("Erro ao buscar filiais:", err);
    }
  };
  const getEmployee = async (idBranch: string) => {
    if (!idBranch) return
    setEmployees([]); 
    console.log("idBranch: ", idBranch);
    try {
      const res = await axios.get(
        `${ip}/employee?idSearch=${idBranch}&enterprise=CLIENT`
      );
      setEmployees(res.data.content);
    } catch (error) {
      console.log("Erro ao buscar colaboradores:", error);
    }
  };

  useEffect(() => {
    console.log("id da branch:", selectedBranch);

    if (selectedBranch) {
      getEmployee(selectedBranch);
    }
  }, [selectedBranch]);

  useEffect(() => {
    if (client?.idClient) {
      fetchBranches();
    }
    const timer = setTimeout(() => {
      if (user?.idUser) {
        toast("Você está na versão 1.0.2 do sistema realiza", {
          action: (
            <Button
              className="bg-realizaBlue dark:border-realizaBlue border dark:bg-white dark:hover:bg-gray-400"
              onClick={() => navigate(`/sistema/new-features/${user.idUser}`)}
            >
              Visualizar novas funções
            </Button>
          ),
        });
      }
    }, 3000);
    return () => clearTimeout(timer);
  }, [user, navigate, client?.idClient]);

  const firstLetter = client?.tradeName?.charAt(0) || "";
  const lastLetter = client?.tradeName?.slice(-1) || "";

  console.log("branch selecionada:", selectedBranch);

  return (
    <div className="mt-10 flex justify-center gap-10">
      <div className="flex items-start justify-center gap-10">
        <div>
          {client ? (
            <div className="flex flex-col gap-10">
              <div className="flex gap-10">
                <div className="flex w-[50vw] items-start justify-between rounded-lg border bg-white p-10 shadow-lg">
                  <div className="flex gap-3">
                    <div className="bg-realizaBlue flex h-[16vh] w-[8vw] items-center justify-center rounded-full p-7">
                      <div className="text-[40px] text-white">
                        {firstLetter}
                        {lastLetter}
                      </div>
                    </div>
                    <div className="flex flex-col gap-10">
                      <div className="flex flex-col items-start">
                        <h2 className="text-realizaBlue text-[30px] font-medium">
                          {client.tradeName}
                        </h2>
                        <h3 className="ml-1 text-sky-900">
                          {client.corporateName}
                        </h3>
                      </div>
                      <div className="text-[13px] text-sky-900">
                        <p>{client.email}</p>
                        <p>{client.cnpj}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div className="rounded-lg border bg-white p-8 shadow-lg">
                <div className="flex flex-col items-start gap-4">
                  <div>
                    <nav className="flex items-center">
                      <Button
                        variant={"ghost"}
                        className={`bg-realizaBlue px-4 py-2 transition-all duration-300 ${
                          selectedTab === "filiais"
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
                          selectedTab === "usuarios"
                            ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                            : "text-realizaBlue bg-white"
                        }`}
                        onClick={() => setSelectedTab("usuarios")}
                      >
                        Usuários
                      </Button>
                    </nav>
                  </div>
                  {selectedTab === "filiais" && (
                    <div>
                      <table className="mt-4 w-[40vw] border-collapse border border-gray-300">
                        <thead>
                          <tr>
                            <th className="border border-gray-300 px-4 py-2 text-start">
                              Filiais
                            </th>
                            <th className="border">Cnpj</th>
                          </tr>
                        </thead>
                        <tbody>
                          {branches && branches.length > 0 ? (
                            branches.map((branch: any) => (
                              <tr key={branch.idBranch}>
                                <td className="border border-gray-300 px-4 py-2">
                                  <li className="text-realizaBlue">
                                    {branch.name}
                                  </li>
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
                  )}
                  {selectedTab === "usuarios" && (
                    <div>
                      <table className="mt-4 w-[40vw] border-collapse border border-gray-300">
                        <thead>
                          <tr>
                            <th className="border border-gray-300 px-4 py-2">
                              Nome
                            </th>
                            <th className="border border-gray-300 px-4 py-2">
                              Status
                            </th>
                            <th className="border border-gray-300 px-4 py-2">
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
          ) : (
            <div className="flex flex-col gap-10">
              <div className="flex gap-10">
                <div>
                  <div>
                    <div className="flex w-[50vw] items-start justify-between rounded-lg border bg-white p-10 shadow-lg">
                      <div className="flex">
                        <Skeleton className="h-[16vh] w-[8vw] rounded-full bg-gray-600" />
                        <div className="flex flex-col gap-10">
                          <div className="flex flex-col gap-5">
                            <Skeleton className="h-[1.5vh] w-[15vw] rounded-full bg-gray-600" />
                            <Skeleton className="ml-1 h-[1.5vh] w-[8vw] rounded-full bg-gray-600" />
                          </div>
                          <div className="ml-2 flex flex-col gap-5">
                            <Skeleton className="h-[0.5vh] w-[6vw] rounded-full bg-gray-600" />
                            <Skeleton className="h-[0.3vh] w-[4vw] rounded-full bg-gray-600" />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div className="rounded-lg border bg-white p-8 shadow-lg">
                <div className="flex flex-col items-start gap-4">
                  <div>
                    <nav className="flex items-center">
                      <Button
                        variant={"ghost"}
                        className={`bg-realizaBlue px-4 py-2 transition-all duration-300 ${
                          selectedTab === "filiais"
                            ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                            : "text-realizaBlue bg-white"
                        }`}
                        onClick={() => setSelectedTab("filiais")}
                      >
                        Filiais
                      </Button>
                      <Button
                        variant={"ghost"}
                        className={`px-4 py-2 transition-all duration-300 text-white${
                          selectedTab === "usuarios"
                            ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                            : "text-realizaBlue bg-white"
                        }`}
                        onClick={() => setSelectedTab("usuarios")}
                      >
                        Usuários
                      </Button>
                    </nav>
                  </div>
                  {selectedTab === "filiais" && (
                    <div>
                      <table className="mt-4 w-[40vw] border-collapse border border-gray-300">
                        <thead>
                          <tr>
                            <th className="border border-gray-300 px-4 py-2 text-start">
                              Filiais
                            </th>
                            <th>Cnpj</th>
                          </tr>
                        </thead>
                        <tbody>
                          {branches && branches.length > 0 ? (
                            branches.map((branch: any) => (
                              <tr key={branch.id}>
                                <td className="border border-gray-300 px-4 py-2">
                                  <li className="text-realizaBlue">
                                    {branch.name}
                                  </li>
                                </td>
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
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
        <div className="flex flex-col gap-4">
          <button
            onClick={() => setShowAddWorkflow(true)}
            className="bg-realizaBlue hover:bg-realizaBlue rounded px-4 py-2 text-white"
          >
            +
          </button>
          <EditModalEnterprise />
        </div>
      </div>

      {showAddWorkflow && (
        <AddClientWorkflow onClose={() => setShowAddWorkflow(false)} />
      )}
    </div>
  );
}
