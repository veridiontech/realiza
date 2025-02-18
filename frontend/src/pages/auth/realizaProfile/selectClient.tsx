import { useState, useEffect } from "react";
import axios from "axios";
import { toast } from "sonner";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Modal } from "@/components/modal";
import selectClientImage from "@/assets/selectClientImage.png";
import { Puff } from "react-loader-spinner";
import { ip } from "@/utils/ip";
import { useUser } from "@/context/user-provider";
import { fetchCompanyByCNPJ } from "@/hooks/gets/realiza/useCnpjApi";

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

  const [userForm, setUserForm] = useState({
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
      setStep(3);
      onClose();
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
  const [loading, setIsLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [getClients, setGetClients] = useState<any[]>([]);
  const [showAddWorkflow, setShowAddWorkflow] = useState(false);
  const navigate = useNavigate();
  const { user } = useUser();

  const getClient = async () => {
    setIsLoading(true);
    try {
      const res = await axios.get(`${ip}/client`);
      setGetClients(res.data.content);
    } catch (err) {
      console.log("Erro ao buscar clientes", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    getClient();
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
  }, [user, navigate]);

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="dark:bg-primary border-realizaBlue flex h-[30rem] w-[80rem] justify-between rounded-lg border bg-white shadow-md dark:border-white">
        <div className="ml-10 mt-4">
          <h1 className="text-2xl">Escolha seu ambiente</h1>
          <div className="dark:bg-primary-foreground my-10 h-[23rem] w-[40rem] rounded-lg p-6 outline outline-1 outline-offset-2 outline-slate-300">
            <div className="flex items-start justify-between">
              <h2 className="text-xl">Selecione um Cliente</h2>
              <div className="mb-4">
                <button
                  onClick={() => setShowAddWorkflow(true)}
                  className="bg-realizaBlue hover:bg-realizaBlue rounded px-4 py-2 text-white"
                >
                  Adicionar Novo Cliente
                </button>
              </div>
            </div>
            <div className="relative mb-4">
              <input
                type="text"
                placeholder="🔍 Procure por clientes cadastrados aqui..."
                className="focus:outline-realizaBlue w-full rounded-lg border border-gray-300 p-2"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            {loading ? (
              <div className="flex w-[20vw] items-center justify-start gap-4 rounded-md border p-2 dark:bg-white">
                <Puff
                  visible={true}
                  height="30"
                  width="30"
                  color="#34495D"
                  ariaLabel="puff-loading"
                />
                <span className="text-black">Carregando...</span>
              </div>
            ) : getClients.length === 0 ? (
              <p className="text-gray-500">Nenhum cliente encontrado.</p>
            ) : (
              <select className="h-[5vh] w-[20vw] rounded-md border p-1 text-black">
                {getClients.map((client: any) => (
                  <option key={client.idClient} value={client.idClient}>
                    {client.tradeName}
                  </option>
                ))}
              </select>
            )}
          </div>
        </div>
        <div className="mx-8 my-4 h-[28rem] w-[30rem] rounded-lg bg-blue-50">
          <img
            src={selectClientImage}
            alt="Imagem de seleção de cliente"
            className="h-full w-full rounded-lg object-cover"
          />
        </div>
      </div>
      {showAddWorkflow && (
        <AddClientWorkflow onClose={() => setShowAddWorkflow(false)} />
      )}
    </div>
  );
}
