import { useState, useEffect } from "react";
import axios from "axios";
import { toast } from "sonner";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Modal } from "@/components/modal";
import { MagnifyingGlass } from "react-loader-spinner";
import { ip } from "@/utils/ip";
import { useUser } from "@/context/user-provider";
import { fetchCompanyByCNPJ } from "@/hooks/gets/realiza/useCnpjApi";
import { Skeleton } from "@/components/ui/skeleton";
import { useClient } from "@/context/Client-Provider";
import { TableServiceProvider } from "./serviceProviders/tableServiceProvider";

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

  return (
    <div className="m-10 flex justify-center gap-10">
      <div className="">
        <div>
          {client ? (
            <div className="flex flex-col gap-10">
              <div className="flex gap-10">
                <div className="flex w-[40vw] items-start justify-between rounded-lg border bg-white p-10 shadow-lg">
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
                  <button
                    onClick={() => setShowAddWorkflow(true)}
                    className="bg-realizaBlue hover:bg-realizaBlue rounded px-4 py-2 text-white"
                  >
                    + Cliente
                  </button>
                </div>
                <Link to={`/sistema/branch/${user?.idUser}`} className="w-[20vw] rounded-lg border bg-white p-10 shadow-lg hover:bg-gray-200">
                    <div className="flex flex-col items-start">
                      <h2 className="text-realizaBlue text-[23px]">
                        Filiais do cliente
                      </h2>
                      <div>
                        {branches.map((branch: any) => (
                          <div>
                            <div key={branch.idBranch}>
                              <li className="text-sky-900">{branch.name}</li>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                </Link>
              </div>
              <div className="rounded-lg border bg-white p-8 shadow-lg">
                <h2>Prestadores de serviço do cliente</h2>
                <div>
                  <TableServiceProvider />
                </div>
              </div>
            </div>
          ) : (
            <div className="flex flex-col gap-10">
              <div className="flex gap-10">
                <div>
                  <div>
                    <div className="flex w-[40vw] items-start justify-between rounded-lg border bg-white p-10 shadow-lg">
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
                      <button
                        onClick={() => setShowAddWorkflow(true)}
                        className="bg-realizaBlue hover:bg-realizaBlue rounded px-4 py-2 text-white"
                      >
                        + Cliente
                      </button>
                    </div>
                  </div>
                </div>
                <div className="rounded-lg border bg-white p-10 shadow-lg">
                  <div className="flex flex-col">
                    <h2 className="text-realizaBlue text-[23px]">
                      Filiais do cliente
                    </h2>
                    <p>
                      <MagnifyingGlass
                        visible={true}
                        height="60"
                        width="55"
                        ariaLabel="magnifying-glass-loading"
                        wrapperStyle={{}}
                        wrapperClass="magnifying-glass-wrapper"
                        glassColor="#c0efff"
                        color="#34495D"
                      />
                    </p>
                  </div>
                </div>
              </div>
              <div>
                <div className="rounded-lg border bg-white p-8 shadow-lg">
                  <h2>Prestadores de serviço do cliente</h2>
                  <div>
                    <TableServiceProvider />
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {showAddWorkflow && (
        <AddClientWorkflow onClose={() => setShowAddWorkflow(false)} />
      )}
    </div>
  );
}
