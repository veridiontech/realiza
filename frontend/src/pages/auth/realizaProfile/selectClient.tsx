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
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Input } from "@/components/ui/input";
import { Pagination } from "@/components/ui/pagination";
import { useBranchUltra } from "@/context/context-ultra/BranchUltra-provider";
import { useMarket } from "@/context/context-ultra/Market-provider";
import { useBoard } from "@/context/context-ultra/Board-provider";
import { useCenter } from "@/context/context-ultra/Center-provider";
import { propsBoard, propsBranchUltra, propsMarket } from "@/types/interfaces";

interface CompanyData {
  razaoSocial: string;
  nomeFantasia: string;
  cep: string;
  email: string;
  state: string;
  city: string;
  address: string;
  number: string;
  telefone?: string;
}

const sanitizeNumber = (value: string) => value.replace(/\D/g, "");

const createUserClient = z.object({
  firstName: z.string().nonempty("Nome é obrigatório"),
  surname: z.string().nonempty("Sobrenome é obrigatório"),
  cellPhone: z.string().nonempty("Celular é obrigatório"),
  cpf: z.string().nonempty("Cpf é obrigatório"),
  email: z
    .string()
    .email("Formato de email inválido")
    .nonempty("Email é obrigatório"),
  position: z.string().nonempty("Seu cargo é obrigatório"),
  password: z.string().min(6, "A senha deve ter pelo menos 6 caracteres"),
  role: z.string().default("ROLE_CLIENT_MANAGER"),
});

const createNewBoard = z.object({
  name: z.string().nonempty("Nome da diretoria é obrigatório")
})

const createNewMarket = z.object({
  name: z.string().nonempty("Nome da diretoria é obrigatório")
})

const createBranchUltra = z.object({
  cnpj: z.string(),
  name: z.string().min(1, "O nome da filial é obrigatório"),
  email: z.string().email("Insira um email válido"),
  cep: z.string().min(8, "O CEP deve ter pelo menos 8 caracteres."),
  country: z.string().min(1, "O país é obrigatório."),
  state: z.string().min(1, "O estado é obrigatório."),
  city: z.string().min(1, "A cidade é obrigatória."),
  address: z.string().min(1, "O endereço é obrigatório."),
  number: z.string().nonempty("Número é obrigatório"),
  telephone: z.string().nonempty("Insira um telefone"),
})

type CreateBranchUltra = z.infer<typeof createBranchUltra>
type CreateNewBoard = z.infer<typeof createNewBoard>
type CreateNewMarket = z.infer<typeof createNewMarket>
type CreateUserClient = z.infer<typeof createUserClient>;
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
        email: res.email,
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
        { name: "email", label: "E-mail", type: "email", required: false },
        { name: "telephone", label: "Telefone", type: "text", required: true },
        { name: "cellphone", label: "Celular", type: "text" },
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
  const [selectedTabUltra, setSelectedTabUltra] = useState("diretoria");
  const [usersFromBranch, setUsersFromBranch] = useState([]);
  const { selectedBranch } = useBranch();
  const { markets, setSelectedMarket, selectedMarket } = useMarket();
  const { boards, setSelectedBoard, selectedBoard } = useBoard();
  const { center, selectedCenter, setSelectedCenter } = useCenter();
  const { branchUltra} = useBranchUltra()

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateUserClient>({
    resolver: zodResolver(createUserClient),
  });

  const {
    register: registerNewBoard,
    handleSubmit: handleSubmitBoard,
    formState: {errors: errorBoard},
  } = useForm<CreateNewBoard>({
    resolver: zodResolver(createNewBoard)
  })

  const {
    register: registerNewMarket,
    handleSubmit: handleSubmitMarket,
    formState: {errors: errorMarket},
  } = useForm<CreateNewMarket>({
    resolver: zodResolver(createNewMarket)
  })

  const {
    register: registerBranchUltra,
    handleSubmit: handleSubmitBranchUltra,
    formState: { errors: errorsBranchUltra },
  } = useForm<CreateBranchUltra>({
    resolver: zodResolver(createBranchUltra),
  });

  const createNewBoardSubmit = async(data: CreateNewBoard) => {
    const payload = {
      ...data,
      idClient: client?.idClient
    }
    try{
      console.log("Enviando dados da nova diretoria:", payload);
      await axios.post(`${ip}/ultragaz/board`, payload)
      toast.success("Sucesso ao criar novo ")
    }catch(err) {
      toast.error("erro ao criar nova diretoria")
      console.log("erro ao criar nova diretoria",err);
      
    }
  } 

  const createNewMarketSubmit = async(data: CreateNewMarket) => {
    const payload = {
      ...data,
      idBoard: selectedBoard?.idBoard
    }
    try{
      console.log("Enviando dados da nova diretoria:", payload);
      await axios.post(`${ip}/ultragaz/market`, payload)
      toast.success("Sucesso ao criar novo ")
    }catch(err) {
      toast.error("erro ao criar nova diretoria")
      console.log("erro ao criar nova diretoria",err);
    }
  } 
  
  const createNewCenterSubmit = async(data: CreateNewMarket) => {
    const payload = {
      ...data,
      idMarket: selectedMarket?.idMarket
    }
    try{
      console.log("Enviando dados da nova diretoria:", payload);
      await axios.post(`${ip}/ultragaz/center`, payload)
      toast.success("Sucesso ao criar novo ")
    }catch(err) {
      toast.error("erro ao criar nova diretoria")
      console.log("erro ao criar nova diretoria",err);
    }
  } 

  const createNewBranchUltraSubmit = async(data: CreateBranchUltra) => {
    const payload = {
      ...data,
      center: selectedCenter?.idCenter
    }
    try{
      console.log("Enviando dados da nova diretoria:", payload);
      await axios.post(`${ip}/branch`, payload)
      toast.success("Sucesso ao criar novo ")
    }catch(err: any) {
      if (err.response && err.response.data) {
        const mensagemBackend =
          err.response.data.message ||
          err.response.data.error ||
          "Erro inesperado no servidor";
        console.log(mensagemBackend);

        toast.error(mensagemBackend);
      } else if (err.request) {
        toast.error("Não foi possível se conectar ao servidor.");
      } else {
        toast.error("Erro desconhecido ao processar requisição.");
      }

      console.error("Erro ao criar filial:", err);
    }
  } 

  const onSubmitUserClient = async (data: CreateUserClient) => {
    const payload = {
      ...data,
      branch: selectedBranch?.idBranch,
    };
    console.log("Enviando dados do novo usuário:", payload);
    try {
      await axios.post(`${ip}/user/client`, payload);
      toast.success("Sucesso ao criar usuário");
    } catch (err: any) {
      if (err.response && err.response.data) {
        const mensagemBackend =
          err.response.data.message ||
          err.response.data.error ||
          "Erro inesperado no servidor";
        console.log(mensagemBackend);
      }
      toast.error("Erro ao criar novo usuário");
      console.log(err);
    }
  };

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

  const getUsersFromBranch = async () => {
    try {
      const res = await axios.get(
        `${ip}/user/client/filtered-client?idSearch=${selectedBranch?.idBranch}`,
      );
      console.log("usuários da branch:", res.data.content);

      setUsersFromBranch(res.data.content);
    } catch (err) {
      console.log("erro ao buscar usuários:", err);
    }
  };

  useEffect(() => {
    console.log("id da branch:", selectedBranch);

    if (selectedBranch?.idBranch) {
      getUsersFromBranch();
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

  if (client?.isUltragaz === true) {
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
                  <div className="flex flex-col gap-4">
                    <div>
                      <nav className="flex items-center justify-between">
                        <div>
                          <Button
                            variant={"ghost"}
                            className={`bg-realizaBlue px-4 py-2 transition-all duration-300 ${
                              selectedTabUltra === "diretoria"
                                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                                : "text-realizaBlue bg-white"
                            }`}
                            onClick={() => setSelectedTabUltra("diretoria")}
                          >
                            Diretoria
                          </Button>
                          <Button
                            variant={"ghost"}
                            className={`bg-realizaBlue px-4 py-2 transition-all duration-300${
                              selectedTabUltra === "mercado"
                                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                                : "text-realizaBlue bg-white"
                            }`}
                            onClick={() => setSelectedTabUltra("mercado")}
                          >
                            Mercado
                          </Button>
                          <Button
                            variant={"ghost"}
                            className={`bg-realizaBlue px-4 py-2 transition-all duration-300${
                              selectedTabUltra === "nucleo"
                                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                                : "text-realizaBlue bg-white"
                            }`}
                            onClick={() => setSelectedTabUltra("nucleo")}
                          >
                            Núcleo
                          </Button>
                          <Button
                            variant={"ghost"}
                            className={`bg-realizaBlue px-4 py-2 transition-all duration-300${
                              selectedTabUltra === "filial"
                                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                                : "text-realizaBlue bg-white"
                            }`}
                            onClick={() => setSelectedTabUltra("filial")}
                          >
                            Unidade
                          </Button>
                        </div>
                        {selectedTabUltra === "diretoria" && (
                          <div>
                            <Dialog>
                              <DialogTrigger asChild>
                                <Button className="bg-realizaBlue">+</Button>
                              </DialogTrigger>
                              <DialogContent className="max-w-[30vw]">
                                <DialogHeader>
                                  <DialogTitle className="flex items-center gap-2">
                                    Criar uma nova diretoria para{" "}
                                    {client ? (
                                      <p>{client.corporateName}</p>
                                    ) : (
                                      <p>Nenhum cliente selecionado</p>
                                    )}
                                  </DialogTitle>
                                </DialogHeader>
                                <form
                                  onSubmit={handleSubmitBoard(createNewBoardSubmit)}
                                >
                                  <div className="flex flex-col gap-2">
                                    <div>
                                      <Label>Nome</Label>
                                      <Input
                                        type="text"
                                        {...registerNewBoard("name")}
                                      />
                                      {errorBoard.name && (
                                        <span className="text-red-600">
                                          {errorBoard.name.message}
                                        </span>
                                      )}
                                    </div>
                                    
                                    <Button
                                      className="bg-realizaBlue"
                                      type="submit"
                                    >
                                      Criar
                                    </Button>
                                  </div>
                                </form>
                              </DialogContent>
                            </Dialog>
                          </div>
                        )}
                        {selectedTabUltra === "mercado" && (
                          <div>
                            <Dialog>
                              <DialogTrigger asChild>
                                <Button className="bg-realizaBlue">+</Button>
                              </DialogTrigger>
                              <DialogContent className="max-w-[35vw]">
                                <DialogHeader>
                                  <DialogTitle className="flex items-center gap-2">
                                    Criar um novo mercado para{" "}
                                    {selectedBoard ? (
                                      <p>{selectedBoard.name}</p>
                                    ) : (
                                      <p className="font-normal">Nenhuma diretoria selecionada</p>
                                    )}
                                  </DialogTitle>
                                </DialogHeader>
                                <form
                                  onSubmit={handleSubmitMarket(createNewMarketSubmit)}
                                >
                                  <div className="flex flex-col gap-2">
                                    <div>
                                      <Label>Nome</Label>
                                      <Input
                                        type="text"
                                        {...registerNewMarket("name")}
                                      />
                                      {errorMarket.name && (
                                        <span className="text-red-600">
                                          {errorMarket.name.message}
                                        </span>
                                      )}
                                    </div>
                                    
                                    <Button
                                      className="bg-realizaBlue"
                                      type="submit"
                                    >
                                      Criar
                                    </Button>
                                  </div>
                                </form>
                              </DialogContent>
                            </Dialog>
                          </div>
                        )}
                        {selectedTabUltra === "nucleo" && (
                          <div>
                            <Dialog>
                              <DialogTrigger asChild>
                                <Button className="bg-realizaBlue">+</Button>
                              </DialogTrigger>
                              <DialogContent className="max-w-[35vw]">
                                <DialogHeader>
                                  <DialogTitle className="flex items-center gap-2">
                                    Criar um novo núcleo para{" "}
                                    {selectedMarket ? (
                                      <p>{selectedMarket.name}</p>
                                    ) : (
                                      <p className="font-normal">Nenhum tipo de mercado selecionado</p>
                                    )}
                                  </DialogTitle>
                                </DialogHeader>
                                <form
                                  onSubmit={handleSubmitMarket(createNewCenterSubmit)}
                                >
                                  <div className="flex flex-col gap-2">
                                    <div>
                                      <Label>Nome</Label>
                                      <Input
                                        type="text"
                                        {...registerNewMarket("name")}
                                      />
                                      {errorMarket.name && (
                                        <span className="text-red-600">
                                          {errorMarket.name.message}
                                        </span>
                                      )}
                                    </div>
                                    
                                    <Button
                                      className="bg-realizaBlue"
                                      type="submit"
                                    >
                                      Criar
                                    </Button>
                                  </div>
                                </form>
                              </DialogContent>
                            </Dialog>
                          </div>
                        )}
                        {selectedTabUltra === "filial" && (
                          <div>
                            <Dialog>
                              <DialogTrigger asChild>
                                <Button className="bg-realizaBlue">+</Button>
                              </DialogTrigger>
                              <DialogContent className="max-w-[35vw]">
                                <DialogHeader>
                                  <DialogTitle className="flex items-center gap-2">
                                    Criar uma nova filial para{" "}
                                    {selectedCenter ? (
                                      <p>{selectedCenter.name}</p>
                                    ) : (
                                      <p className="font-normal">Nenhum núcleo selecionado</p>
                                    )}
                                  </DialogTitle>
                                </DialogHeader>
                                <form
                                  onSubmit={handleSubmitBranchUltra(createNewBranchUltraSubmit)}
                                >
                                  <div className="flex flex-col gap-2">
                                    <div>
                                      <Label>Nome</Label>
                                      <Input
                                        type="text"
                                        {...registerBranchUltra("name")}
                                      />
                                      {errorsBranchUltra.name && (
                                        <span className="text-red-600">
                                          {errorsBranchUltra.name.message}
                                        </span>
                                      )}
                                    </div>
                                    <div>
                                      <Label>Email</Label>
                                      <Input
                                        type="text"
                                        {...registerBranchUltra("email")}
                                      />
                                      {errorsBranchUltra.email && (
                                        <span className="text-red-600">
                                          {errorsBranchUltra.email.message}
                                        </span>
                                      )}
                                    </div>
                                    <div>
                                      <Label>CNPJ</Label>
                                      <Input
                                        type="text"
                                        {...registerBranchUltra("cnpj")}
                                      />
                                      {errorsBranchUltra.cnpj && (
                                        <span className="text-red-600">
                                          {errorsBranchUltra.cnpj.message}
                                        </span>
                                      )}
                                    </div>
                                    <div>
                                      <Label>Cidade</Label>
                                      <Input
                                        type="text"
                                        {...registerBranchUltra("city")}
                                      />
                                      {errorsBranchUltra.city && (
                                        <span className="text-red-600">
                                          {errorsBranchUltra.city.message}
                                        </span>
                                      )}
                                    </div>
                                    <div>
                                      <Label>CEP</Label>
                                      <Input
                                        type="text"
                                        {...registerBranchUltra("cep")}
                                      />
                                      {errorsBranchUltra.cep && (
                                        <span className="text-red-600">
                                          {errorsBranchUltra.cep.message}
                                        </span>
                                      )}
                                    </div>
                                    <div>
                                      <Label>Endereço</Label>
                                      <Input
                                        type="text"
                                        {...registerBranchUltra("address")}
                                      />
                                      {errorsBranchUltra.address && (
                                        <span className="text-red-600">
                                          {errorsBranchUltra.address.message}
                                        </span>
                                      )}
                                    </div>
                                    <div>
                                      <Label>Número</Label>
                                      <Input
                                        type="text"
                                        {...registerBranchUltra("number")}
                                      />
                                      {errorsBranchUltra.number && (
                                        <span className="text-red-600">
                                          {errorsBranchUltra.number.message}
                                        </span>
                                      )}
                                    </div>
                                    <div>
                                      <Label>País</Label>
                                      <Input
                                        type="text"
                                        {...registerBranchUltra("country")}
                                      />
                                      {errorsBranchUltra.country && (
                                        <span className="text-red-600">
                                          {errorsBranchUltra.country.message}
                                        </span>
                                      )}
                                    </div>
                                    <div>
                                      <Label>Estado</Label>
                                      <Input
                                        type="text"
                                        {...registerBranchUltra("state")}
                                      />
                                      {errorsBranchUltra.state && (
                                        <span className="text-red-600">
                                          {errorsBranchUltra.state.message}
                                        </span>
                                      )}
                                    </div>
                                    <div>
                                      <Label>Telefone</Label>
                                      <Input
                                        type="text"
                                        {...registerBranchUltra("telephone")}
                                      />
                                      {errorsBranchUltra.telephone && (
                                        <span className="text-red-600">
                                          {errorsBranchUltra.telephone.message}
                                        </span>
                                      )}
                                    </div>
                                    <Button
                                      className="bg-realizaBlue"
                                      type="submit"
                                    >
                                      Criar
                                    </Button>
                                  </div>
                                </form>
                              </DialogContent>
                            </Dialog>
                          </div>
                        )}
                      </nav>
                    </div>
                    {selectedTabUltra === "diretoria" && (
                      <div>
                        <table className="mt-4 w-[40vw] border-collapse border border-gray-300">
                          <thead>
                            <tr>
                              <th className="border border-gray-300 px-4 py-2 text-start">
                                Diretorias
                              </th>
                            </tr>
                          </thead>
                          <tbody>
                            {boards && boards.length > 0 ? (
                              boards.map((board: propsBoard) => (
                                <tr key={board.idBoard}>
                                  <td className="border border-gray-300 px-4 py-2">
                                    <li className="text-realizaBlue">
                                      {board.name}
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
                    {selectedTabUltra === "mercado" && (
                      <div>
                        <div className="flex flex-col items-start gap-2">
                          <Label>Diretoria</Label>
                          <select
                            onChange={(e) => {
                              const selected = boards.find(
                                (b) => b.idBoard === e.target.value,
                              );
                              setSelectedBoard(selected || null);
                            }}
                            className="rounded-md border p-2"
                            defaultValue=""
                          >
                            <option value="" disabled>
                              Selecione uma diretoria
                            </option>
                            {boards.map((board) => (
                              <option value={board.idBoard} key={board.idBoard}>
                                {board.name}
                              </option>
                            ))}
                          </select>
                        </div>
                        <table className="mt-4 w-[40vw] border-collapse border border-gray-300">
                          <thead>
                            <tr>
                              <th className="border border-gray-300 px-4 py-2">
                                Mercados
                              </th>
                              <th className="border border-gray-300 px-4 py-2">
                                Diretoria
                              </th>
                            </tr>
                          </thead>
                          <tbody>
                            {markets && markets.length > 0 ? (
                              markets.map((market: propsMarket) => (
                                <tr
                                  key={market.idMarket}
                                  className="overflow-auto text-start"
                                >
                                  <td className="border border-gray-300 px-4 py-2">
                                    {market.name}
                                  </td>
                                  <td
                                    key={selectedBoard?.idBoard}
                                    className="text-center"
                                  >
                                    {selectedBoard?.name}
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
                    {selectedTabUltra === "nucleo" && (
                      <div>
                        <div className="flex flex-col items-start gap-4">
                          <div>
                            <div>
                              <span className="flex items-center gap-2 font-medium">
                                Diretoria selecionada:{" "}
                                {selectedBoard ? (
                                  <p className="font-normal">
                                    {selectedBoard.name}
                                  </p>
                                ) : (
                                  <p className="font-normal">
                                    Nenhuma diretoria selecionada
                                  </p>
                                )}
                              </span>
                            </div>
                          </div>
                          <div className="flex flex-col gap-2">
                            <Label>Mercado</Label>
                            <select
                              onChange={(e) => {
                                const selected = markets.find(
                                  (b) => b.idBoard === e.target.value,
                                );
                                setSelectedMarket(selected || null);
                              }}
                              className="rounded-md border p-2"
                            >
                              <option value="">
                                Selecione um tipo de mercado
                              </option>
                              {markets.map((market) => (
                                <option
                                  value={market.idBoard}
                                  key={market.idBoard}
                                >
                                  {market.name}
                                </option>
                              ))}
                            </select>
                          </div>
                        </div>
                        <table className="mt-4 w-[40vw] border-collapse border border-gray-300">
                          <thead>
                            <tr>
                              <th className="border border-gray-300 px-4 py-2">
                                Núcleo
                              </th>
                              <th className="border border-gray-300 px-4 py-2">
                                Mercado
                              </th>
                              <th className="border border-gray-300 px-4 py-2">
                                Diretoria
                              </th>
                            </tr>
                          </thead>
                          <tbody>
                            {markets && markets.length > 0 ? (
                              markets.map((market: propsMarket) => (
                                <tr
                                  key={market.idMarket}
                                  className="overflow-auto text-start"
                                >
                                  {center.map((center) => (
                                    <td key={center.idCenter}>{center.name}</td>
                                  ))}
                                  <td className="border border-gray-300 px-4 py-2">
                                    {market.name}
                                  </td>
                                  <td
                                    key={selectedBoard?.idBoard}
                                    className="text-center"
                                  >
                                    {selectedBoard?.name}
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
                    {selectedTabUltra === "filial" && (
                      <div>
                        <div className="flex flex-col items-start gap-4">
                          <div>
                            <div>
                              <span className="flex items-center gap-2 font-medium">
                                Diretoria selecionada:{" "}
                                {selectedBoard ? (
                                  <p className="font-normal">
                                    {selectedBoard.name}
                                  </p>
                                ) : (
                                  <p className="font-normal">
                                    Nenhuma diretoria selecionada
                                  </p>
                                )}
                              </span>
                            </div>
                          </div>
                          <div className="flex flex-col gap-2">
                          <span className="flex items-center gap-2 font-medium">
                                Mercado selecionado:{" "}
                                {selectedMarket ? (
                                  <p className="font-normal">
                                    {selectedMarket.name}
                                  </p>
                                ) : (
                                  <p className="font-normal">
                                    Nenhum tipo de mercado selecionado
                                  </p>
                                )}
                              </span>
                          </div>
                          <div>
                            <select onChange={(e) => {
                                const selected = center.find(
                                  (b) => b.idCenter === e.target.value,
                                );
                                setSelectedCenter(selected || null);
                              }} defaultValue="" className="border p-2 rounded-md">
                              <option value="" disabled>Selecione um núcleo</option>
                              {center.map((center) => (
                                <option value={center.idCenter} key={center.idCenter}>{center.name}</option>
                              ))}
                            </select>
                          </div>
                        </div>
                        <table className="mt-4 w-[40vw] border-collapse border border-gray-300">
                          <thead>
                            <tr>
                              <th className="border border-gray-300 px-4 py-2">
                                Unidade
                              </th>
                              <th className="border border-gray-300 px-4 py-2">
                                Núcleo
                              </th>
                            </tr>
                          </thead>
                          <tbody>
                            {branchUltra && branchUltra.length > 0 ? (
                              branchUltra.map((branchUltra: propsBranchUltra) => (
                                <tr
                                  className="overflow-auto text-start"
                                >
                                  <td className="border border-gray-300 px-4 py-2" key={branchUltra.idBranch}>{branchUltra.name}</td>
                                  <td className="border border-gray-300 px-4 py-2" key={selectedCenter?.idCenter}>{selectedCenter?.name}</td>
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
                          onClick={() => setSelectedTabUltra("filiais")}
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
                          onClick={() => setSelectedTabUltra("usuarios")}
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

  function handlePageChange(_page: number): void {
    throw new Error("Function not implemented.");
  }

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
                <div className="flex flex-col gap-4">
                  <div>
                    <nav className="flex items-center justify-between">
                      <div>
                        <Button
                          variant={"ghost"}
                          className={`bg-realizaBlue px-4 py-2 transition-all duration-300 ${selectedTab === "filiais"
                              ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                              : "text-realizaBlue bg-white"
                            }`}
                          onClick={() => setSelectedTab("filiais")}
                        >
                          Filiais
                        </Button>
                        <Button
                          variant={"ghost"}
                          className={`bg-realizaBlue px-4 py-2 transition-all duration-300${selectedTab === "usuarios"
                              ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                              : "text-realizaBlue bg-white"
                            }`}
                          onClick={() => setSelectedTab("usuarios")}
                        >
                          Usuários
                        </Button>
                      </div>
                      {selectedTab === "usuarios" && (
                        <div>
                          <Dialog>
                            <DialogTrigger asChild>
                              <Button className="bg-realizaBlue">+</Button>
                            </DialogTrigger>
                            <DialogContent className="max-w-[30vw]">
                              <DialogHeader>
                                <DialogTitle className="flex items-center gap-2">
                                  Criar usuário para o cliente{" "}
                                  {client ? (
                                    <p>{client.corporateName}</p>
                                  ) : (
                                    <p>Nenhum cliente selecionado</p>
                                  )}
                                </DialogTitle>
                              </DialogHeader>
                              <form onSubmit={handleSubmit(onSubmitUserClient)}>
                                <div className="flex flex-col gap-2">
                                  <div>
                                    <Label>Nome</Label>
                                    <Input
                                      type="text"
                                      {...register("firstName")}
                                    />
                                    {errors.firstName && (
                                      <span className="text-red-600">
                                        {errors.firstName.message}
                                      </span>
                                    )}
                                  </div>
                                  <div>
                                    <Label>Sobrenome</Label>
                                    <Input
                                      type="text"
                                      {...register("surname")}
                                    />
                                    {errors.surname && (
                                      <span className="text-red-600">
                                        {errors.surname.message}
                                      </span>
                                    )}
                                  </div>
                                  <div>
                                    <Label>Email</Label>
                                    <Input type="text" {...register("email")} />
                                    {errors.email && (
                                      <span className="text-red-600">
                                        {errors.email.message}
                                      </span>
                                    )}
                                  </div>
                                  <div>
                                    <Label>CPF</Label>
                                    <Input type="text" {...register("cpf")} />
                                    {errors.cpf && (
                                      <span className="text-red-600">
                                        {errors.cpf.message}
                                      </span>
                                    )}
                                  </div>
                                  <div>
                                    <Label>Telefone</Label>
                                    <Input
                                      type="text"
                                      {...register("cellPhone")}
                                    />
                                    {errors.cellPhone && (
                                      <span className="text-red-600">
                                        {errors.cellPhone.message}
                                      </span>
                                    )}
                                  </div>
                                  <div>
                                    <Label>Cargo</Label>
                                    <Input
                                      type="text"
                                      {...register("position")}
                                    />
                                    {errors.position && (
                                      <span className="text-red-600">
                                        {errors.position.message}
                                      </span>
                                    )}
                                  </div>
                                  <div>
                                    <Label>Senha</Label>
                                    <Input
                                      type="text"
                                      {...register("password")}
                                    />
                                    {errors.password && (
                                      <span className="text-red-600">
                                        {errors.password.message}
                                      </span>
                                    )}
                                  </div>
                                  <Button
                                    className="bg-realizaBlue"
                                    type="submit"
                                  >
                                    Criar
                                  </Button>
                                </div>
                              </form>
                            </DialogContent>
                          </Dialog>
                        </div>
                      )}
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
                      <div>
                        <span>
                          {selectedBranch ? (
                            <div>
                              <p>
                                <strong>Filial:</strong> {selectedBranch.name}
                              </p>
                            </div>
                          ) : (
                            <div>
                              <p>
                                <strong>Filial:</strong> Filial não selecionada
                              </p>
                            </div>
                          )}
                        </span>
                      </div>
                      <table className="mt-4 w-[40vw] border-collapse border border-gray-300">
                        <thead>
                          <tr>
                            <th className="border border-gray-300 px-4 py-2">
                              Nome
                            </th>
                            <th className="border border-gray-300 px-4 py-2">
                              CPF
                            </th>
                            <th className="border border-gray-300 px-4 py-2">
                              Ações
                            </th>
                          </tr>
                        </thead>
                        <tbody>
                          {usersFromBranch && usersFromBranch.length > 0 ? (
                            usersFromBranch.map((users: any) => (
                              <tr key={users.idUser} className="overflow-auto">
                                <td className="border border-gray-300 px-4 py-2">
                                  {users.firstName}
                                </td>
                                <td className="border border-gray-300 px-4 py-2">
                                  <span>{users.cpf}</span>
                                </td>
                                <td className="border border-gray-300 px-4 py-2">
                                  <Link
                                    to={`/sistema/detailsEmployees/${users.idEmployee}`}
                                  >
                                    <button className="text-realizaBlue ml-4 flex items-center justify-center hover:underline">
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
                      {/* <Pagination
                        currentPage={currentPage}
                        totalPages={totalPages}
                        onPageChange={handlePageChange}
                      /> */}
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
                        className={`bg-realizaBlue px-4 py-2 transition-all duration-300 ${selectedTab === "filiais"
                            ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                            : "text-realizaBlue bg-white"
                          }`}
                        onClick={() => setSelectedTab("filiais")}
                      >
                        Filiais
                      </Button>
                      <Button
                        variant={"ghost"}
                        className={`px-4 py-2 transition-all duration-300 text-white${selectedTab === "usuarios"
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
