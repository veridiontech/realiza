import {
  Building2,
  ChevronRight,
  Files,
  MessageCircle,
  Settings2,
  University,
  Users,
  UsersRound,
} from "lucide-react";
import { Helmet } from "react-helmet-async";
import { Link, NavLink } from "react-router-dom";

import { EnterpriseResume } from "@/components/home/enterpriseResume";
import { ConformityGaugeChart } from "@/components/BIs/BisPageComponents/conformityChart";
import { Button } from "@/components/ui/button";
import { MainCard } from "@/components/quickActions/mainCard";
import { ActionButton } from "@/components/quickActions/actionButton";
import { useEffect, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { useClient } from "@/context/Client-Provider";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useBranch } from "@/context/Branch-provider";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useUser } from "@/context/user-provider";
import { toast } from "sonner";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { AddNewBranch } from "../branchs/modals/add-new-branch";
import { UltraSection } from "../ultra/ultra-branchs";
import { ScrollArea } from "@/components/ui/scroll-area";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { BranchesTable } from "./branchesTable";
import { ActiveContracts } from "@/components/BIs/BisPageComponents/activeContracts";
import { Employees } from "@/components/BIs/BisPageComponents/employees";
import { Oval } from "react-loader-spinner";
import { Suppliers } from "@/components/BIs/BisPageComponents/suppliersCard";
import { AllocatedEmployees } from "@/components/BIs/BisPageComponents/AllocatedEmployees";

const cpfRegex = /^\d{3}\.\d{3}\.\d{3}-\d{2}$|^\d{11}$/;
const phoneRegex = /^\(?\d{2}\)?\s?\d{4,5}-?\d{4}$/;

function validarCPF(cpf: string): boolean {
  cpf = cpf.replace(/[^\d]+/g, "");

  if (cpf.length !== 11) return false;

  if (/^(\d)\1{10}$/.test(cpf)) return false;

  let soma = 0;
  let resto;

  for (let i = 1; i <= 9; i++) {
    soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
  }

  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf.substring(9, 10))) return false;

  soma = 0;
  for (let i = 1; i <= 10; i++) {
    soma += parseInt(cpf.substring(i - 1, i)) * (12 - i);
  }
  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf.substring(10, 11))) return false;

  return true;
}

function validarTelefoneRepetido(telefone: string) {
  const digits = telefone.replace(/\D/g, "");
  return !/^(\d)\1+$/.test(digits);
}

const createUserClient = z.object({
  firstName: z.string().nonempty("Nome é obrigatório"),
  surname: z.string().nonempty("Sobrenome é obrigatório"),
  cellPhone: z
    .string()
    .optional()
    .refine((val) => !val || phoneRegex.test(val), {
      message: "Telefone inválido, use o formato (XX) XXXXX-XXXX",
    })
    .refine((val) => !val || validarTelefoneRepetido(val), {
      message: "Telefone inválido: não pode ter números repetidos",
    }),
  cpf: z
    .string()
    .nonempty("Cpf é obrigatório")
    .regex(cpfRegex, "CPF inválido, use o formato 000.000.000-00")
    .refine((cpf) => validarCPF(cpf), {
      message: "CPF inválido",
    }),
  email: z
    .string()
    .email("Formato de email inválido")
    .nonempty("Email é obrigatório"),
  position: z.string().nonempty("Seu cargo é obrigatório"),
  role: z.string().default("ROLE_CLIENT_MANAGER"),
  branchAccessIds: z
    .array(z.string())
    .min(1, "Selecione pelo menos uma filial para o usuário"),
  contractAccessIds: z.array(z.string()).optional(),
  profileId: z.string().nonempty("Selecione um perfil para o usuário"),
});

type CreateUserClient = z.infer<typeof createUserClient>;
export function Dashboard() {
  const [selectedTab, setSelectedTab] = useState("filiais");
  const [usersFromBranch, setUsersFromBranch] = useState([]);
  const { client } = useClient();
  const { selectedBranch } = useBranch();
  const { user } = useUser();
  const [phoneValue, setPhoneValue] = useState("");
  const [cpfValue, setCpfValue] = useState("");
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [data, setData] = useState<{
    conformity: number;
    activeContractQuantity: number;
    activeEmployeeQuantity: number;
    activeSupplierQuantity: number;
    allocatedEmployeeQuantity: number;
  } | null>(null);

  const [availableBranches, setAvailableBranches] = useState<
    { idBranch: string; name: string }[]
  >([]);
  const [selectedBranchIds, setSelectedBranchIds] = useState<string[]>([]);

  const [availableContracts, setAvailableContracts] = useState<
    { id: string; contractReference: string; branchName: string }[]
  >([]);
  const [selectedContractIds, setSelectedContractIds] = useState<string[]>([]);

  const [availableProfiles, setAvailableProfiles] = useState<
    { id: string; profileName: string }[]
  >([]);
  const [selectedProfileId, setSelectedProfileId] = useState<string>("");

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
    reset,
  } = useForm<CreateUserClient>({
    resolver: zodResolver(createUserClient),
  });

  const formatCPF = (value: string) => {
    return value
      .replace(/\D/g, "")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d{1,2})$/, "$1-$2")
      .slice(0, 14);
  };

  const formatPhone = (value: string) => {
    const digits = value.replace(/\D/g, "");

    if (digits.length <= 2) {
      return digits;
    } else if (digits.length <= 6) {
      return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
    } else if (digits.length <= 10) {
      return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(
        6
      )}`;
    } else {
      return `(${digits.slice(0, 2)}) ${digits.slice(
        2,
        7
      )}-${digits.slice(7, 11)}`;
    }
  };

  const onSubmitUserClient = async (data: CreateUserClient) => {
    const cpfSemPontuacao = data.cpf.replace(/\D/g, "");
    const cpfJaExiste = usersFromBranch.some(
      (user: any) => user.cpf.replace(/\D/g, "") === cpfSemPontuacao
    );

    if (cpfJaExiste) {
      toast.error("CPF já cadastrado para esta filial");
      return;
    }

    const emailJaExiste = usersFromBranch.some(
      (user: any) => user.email.toLowerCase() === data.email.toLowerCase()
    );

    if (emailJaExiste) {
      toast.error("E-mail já cadastrado para esta filial");
      return;
    }

    const payload = {
      ...data,
      idEnterprise: selectedBranch?.idBranch,
      enterprise: "CLIENT",
      idUser: user?.idUser,
      branchAccessIds: selectedBranchIds,
      contractAccessIds: selectedContractIds,
      profileId: selectedProfileId,
    };
    console.log("Enviando dados do novo usuário:", payload);
    setIsLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.post(`${ip}/user/manager/new-user`, payload, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      toast.success("Sucesso ao criar usuário");
      setIsOpen(false);
      await getUsersFromBranch();
      reset();
      setCpfValue("");
      setPhoneValue("");
      setSelectedBranchIds([]);
      setSelectedContractIds([]);
      setSelectedProfileId("");
    } catch (err: any) {
      if (err.response && err.response.data) {
        const mensagemBackend =
          err.response.data.message ||
          err.response.data.error ||
          "Erro inesperado no servidor";
        console.log(mensagemBackend);
        toast.error(mensagemBackend);
      } else {
        toast.error("Erro ao criar usuário. Verifique os dados e tente novamente.");
      }
      setIsOpen(false);
      console.log(err);
    } finally {
      setIsLoading(false);
    }
  };

  const getUsersFromBranch = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/user/client/filtered-client?idSearch=${selectedBranch?.idBranch}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      const { content } = res.data;
      console.log("usuários da branch:", content);
      setUsersFromBranch(content);
    } catch (err) {
      console.error("erro ao buscar usuários:", err);
    }
  };

  const fetchAvailableBranches = async () => {
    if (!client?.idClient) {
      console.warn("ID do cliente não disponível para buscar filiais.");
      return;
    }
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client.idClient}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setAvailableBranches(response.data.content);
    } catch (error) {
      console.error("Erro ao buscar filiais disponíveis:", error);
      toast.error("Erro ao carregar as filiais. Tente novamente.");
    }
  };

  const fetchContractsByBranchIds = async (branchIds: string[]) => {
    if (branchIds.length === 0) {
      setAvailableContracts([]);
      setSelectedContractIds([]);
      setValue("contractAccessIds", [], { shouldValidate: true });
      return;
    }
    setIsLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const queryParams = branchIds.map((id) => `branchIds=${id}`).join("&");
      const response = await axios.get(
        `${ip}/contract/find-by-branchIds?${queryParams}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setAvailableContracts(response.data);
    } catch (error) {
      console.error("Erro ao buscar contratos por filiais:", error);
      toast.error("Erro ao carregar os contratos. Tente novamente.");
      setAvailableContracts([]);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchAvailableProfiles = async (clientId: string) => {
    if (!clientId) {
      console.warn("ID do cliente não disponível para buscar perfis.");
      setAvailableProfiles([]);
      return;
    }
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.get(
        `${ip}/profile/by-name/${clientId}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setAvailableProfiles(response.data);
    } catch (error) {
      console.error("Erro ao buscar perfis disponíveis:", error);
      toast.error("Erro ao carregar os perfis. Tente novamente.");
      setAvailableProfiles([]);
    }
  };

  const handleBranchSelection = (branchId: string, isChecked: boolean) => {
    setSelectedBranchIds((prevSelected) => {
      const newSelected = isChecked
        ? [...prevSelected, branchId]
        : prevSelected.filter((id) => id !== branchId);
      setValue("branchAccessIds", newSelected, { shouldValidate: true });
      return newSelected;
    });
  };

  const handleContractSelection = (contractId: string, isChecked: boolean) => {
    setSelectedContractIds((prevSelected) => {
      const newSelected = isChecked
        ? [...prevSelected, contractId]
        : prevSelected.filter((id) => id !== contractId);
      setValue("contractAccessIds", newSelected, { shouldValidate: true });
      return newSelected;
    });
  };

  const handleProfileSelection = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const profileId = e.target.value;
    setSelectedProfileId(profileId);
    setValue("profileId", profileId, { shouldValidate: true });
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getUsersFromBranch();
    }
  }, [selectedBranch?.idBranch]);

  useEffect(() => {
    if (!isOpen) {
      reset();
      setCpfValue("");
      setPhoneValue("");
      setSelectedBranchIds([]);
      setSelectedContractIds([]);
      setSelectedProfileId("");
    }
  }, [isOpen, reset]);

  useEffect(() => {
    if (isOpen && client?.idClient) {
      fetchAvailableBranches();
      fetchAvailableProfiles(client.idClient);
    }
  }, [isOpen, client?.idClient]);

  useEffect(() => {
    fetchContractsByBranchIds(selectedBranchIds);
  }, [selectedBranchIds]);

  useEffect(() => {
    const fetchConformity = async () => {
      try {
        const token = localStorage.getItem("tokenClient");
        const res = await axios.get(
          `${ip}/dashboard/home/${selectedBranch?.idBranch}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setData(res.data);
      } catch (err) {
        console.error("Erro ao buscar conformidade:", err);
        setData({
          conformity: 0,
          activeContractQuantity: 0,
          activeEmployeeQuantity: 0,
          activeSupplierQuantity: 0,
          allocatedEmployeeQuantity: 0,
        });
      } finally {
        setIsLoading(false);
      }
    };

    if (selectedBranch?.idBranch && selectedBranch.client) {
      fetchConformity();
    }
  }, [selectedBranch?.idBranch, selectedBranch?.client]);

  if (client?.isUltragaz) {
    return (
      <>
        <Helmet title="Dashboard" />
        <section className="relative bottom-[5vw] pb-10 pt-14">
          <div className="mx-auto max-w-[80vw]">
            <div className="flex flex-col gap-10">
              <EnterpriseResume />
              {client?.isUltragaz && <UltraSection />}
            </div>
            <div className="mt-8 grid grid-cols-1 gap-8">
              <div className="w-full flex justify-center">
                <div className="w-full bg-white rounded-xl shadow-lg p-6 flex flex-col items-center justify-center border border-gray-300 h-[900px]">
                  <ConformityGaugeChart percentage={data?.conformity} />
                </div>
              </div>
            </div>

            <div className="mt-5 w-full text-right">
              <NavLink to={`sistema/dashboard-details/${user?.idUser}`}>
                <Button className="hover:bg-realizaBlue dark:bg-primary bg-realizaBlue dark:text-white dark:hover:bg-blue-950">
                  Ver mais <ChevronRight />
                </Button>
              </NavLink>
            </div>

            <div className="pt-20">
              <h2 className="pb-6 text-xl font-medium">Ações rápidas</h2>

              <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-4">
                <MainCard
                  title="Fornecedores"
                  value={324}
                  icon={<UsersRound size={28} />}
                />
                <MainCard
                  title="Mensagens"
                  value={12}
                  icon={<MessageCircle size={28} />}
                />
                <MainCard
                  title="Unidades"
                  value={4}
                  icon={<University size={28} />}
                />
                <MainCard
                  title="Contratos"
                  value={72}
                  icon={<Files size={28} />}
                />
              </div>

              <div className="mt-8 grid grid-cols-1 gap-x-8 gap-y-4 sm:grid-cols-2 lg:grid-cols-4">
                <ActionButton
                  label="Adicionar fornecedor"
                  icon={<ChevronRight />}
                />
                <ActionButton
                  label="Enviar documento"
                  icon={<ChevronRight />}
                />
                <ActionButton label="Criar contato" icon={<ChevronRight />} />
                <ActionButton label="Gerar relatório" icon={<ChevronRight />} />
                <ActionButton
                  label="Atualizar documentos"
                  icon={<ChevronRight />}
                />
                <ActionButton
                  label="Consultar contratos"
                  icon={<ChevronRight />}
                />
                <ActionButton
                  label="Aprovar solicitações"
                  icon={<ChevronRight />}
                />
                <ActionButton
                  label="Editar colaboradores"
                  icon={<ChevronRight />}
                />
              </div>
            </div>
          </div>
        </section>
      </>
    );
  }

  return (
    <>
      <Helmet title="Dashboard" />
      <section className="dark:bg-primary-foreground pb-10 pt-14">
        <div className="container relative bottom-[6vw] mx-auto max-w-7xl">
          <div className="flex flex-col gap-10">
            <EnterpriseResume />
            <div className="flex items-center gap-10">
              <div className="h-[60vh] w-[70vw] rounded-lg border bg-white p-8 shadow-sm">
                <div className="flex flex-col gap-4">
                  <div>
                    <nav className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <Button
                          variant={"ghost"}
                          className={`bg-realizaBlue px-4 py-2 transition-all duration-300 ${
                            selectedTab === "filiais"
                              ? "bg-realizaBlue scale-110 font-bold text-white shadow-sm"
                              : "text-realizaBlue border-realizaBlue border bg-white"
                          }`}
                          onClick={() => setSelectedTab("filiais")}
                        >
                          <Building2 /> Filiais
                        </Button>
                        <Button
                          variant={"ghost"}
                          className={`bg-realizaBlue px-4 py-2 transition-all duration-300${
                            selectedTab === "usuarios"
                              ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                              : "text-realizaBlue border-realizaBlue border bg-white"
                          }`}
                          onClick={() => setSelectedTab("usuarios")}
                        >
                          <Users /> Usuários
                        </Button>
                      </div>
                      {selectedTab === "filiais" && <AddNewBranch />}
                      {selectedTab === "usuarios" && (
                        <Dialog open={isOpen} onOpenChange={setIsOpen}>
                          <DialogTrigger asChild>
                            <Button className="bg-realizaBlue hidden md:block">
                              Criar Usuário
                            </Button>
                          </DialogTrigger>
                          <DialogTrigger asChild>
                            <Button className="bg-realizaBlue md:hidden">
                              +
                            </Button>
                          </DialogTrigger>
                          <DialogContent
                            style={{
                              backgroundImage: `url(${bgModalRealiza})`,
                            }}
                          >
                            <DialogHeader>
                              <DialogTitle className="flex flex-col gap-2 text-white md:flex-row md:items-center">
                                Criar usuário para o cliente{" "}
                                {client ? (
                                  <span className="font-semibold text-white">
                                    {client.corporateName}
                                  </span>
                                ) : (
                                  <span className="text-red-600">
                                    Nenhum cliente selecionado
                                  </span>
                                )}
                              </DialogTitle>
                            </DialogHeader>

                            <ScrollArea className="h-[40vh] p-3">
                              <form
                                onSubmit={handleSubmit(onSubmitUserClient)}
                                className="m-2 flex flex-col gap-5"
                              >
                                <div>
                                  <Label className="text-white">Nome</Label>
                                  <Input
                                    type="text"
                                    {...register("firstName")}
                                    placeholder="Digite seu nome"
                                  />
                                  {errors.firstName && (
                                    <span className="text-sm text-red-600">
                                      {errors.firstName.message}
                                    </span>
                                  )}
                                </div>

                                <div>
                                  <Label className="text-white">
                                    Sobrenome
                                  </Label>
                                  <Input
                                    type="text"
                                    {...register("surname")}
                                    placeholder="Digite seu sobrenome"
                                  />
                                  {errors.surname && (
                                    <span className="text-sm text-red-600">
                                      {errors.surname.message}
                                    </span>
                                  )}
                                </div>

                                <div>
                                  <Label className="text-white">Email</Label>
                                  <Input
                                    type="email"
                                    {...register("email")}
                                    placeholder="Digite seu e-mail"
                                  />
                                  {errors.email && (
                                    <span className="text-sm text-red-600">
                                      {errors.email.message}
                                    </span>
                                  )}
                                </div>

                                <div>
                                  <Label className="text-white">CPF</Label>
                                  <Input
                                    type="text"
                                    value={cpfValue}
                                    onChange={(e) => {
                                      const formattedCpf = formatCPF(
                                        e.target.value
                                      );
                                      setCpfValue(formattedCpf);
                                      setValue("cpf", formattedCpf, {
                                        shouldValidate: true,
                                      });
                                    }}
                                    placeholder="000.000.000-00"
                                    maxLength={14}
                                  />
                                  {errors.cpf && (
                                    <span className="text-sm text-red-600">
                                      {errors.cpf.message}
                                    </span>
                                  )}
                                </div>

                                <div className="flex flex-col gap-2">
                                  <Label className="text-white">Telefone</Label>
                                  <Input
                                    type="text"
                                    value={phoneValue}
                                    {...register("cellPhone")}
                                    onChange={(e) => {
                                      const formattedPhone = formatPhone(
                                        e.target.value
                                      );
                                      setPhoneValue(formattedPhone);
                                      setValue("cellPhone", formattedPhone, {
                                        shouldValidate: true,
                                      });
                                    }}
                                    placeholder="(00) 00000-0000"
                                    maxLength={15}
                                  />
                                  {errors.cellPhone && (
                                    <span className="text-sm text-red-600">
                                      {errors.cellPhone.message}
                                    </span>
                                  )}
                                </div>

                                <div>
                                  <Label className="text-white">Cargo</Label>
                                  <Input
                                    type="text"
                                    {...register("position")}
                                    placeholder="Digite seu cargo"
                                  />
                                  {errors.position && (
                                    <span className="text-sm text-red-600">
                                      {errors.position.message}
                                    </span>
                                  )}
                                </div>

                                <div className="flex flex-col gap-2">
                                  <Label htmlFor="profile" className="text-white">
                                    Perfil do Usuário
                                  </Label>
                                  {isLoading ? (
                                    <p className="text-white">
                                      Carregando perfis...
                                    </p>
                                  ) : (
                                    <select
                                      id="profile"
                                      {...register("profileId")}
                                      value={selectedProfileId}
                                      onChange={handleProfileSelection}
                                      className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 text-black"
                                    >
                                      <option value="">Selecione um perfil</option>
                                      {availableProfiles.map((profile) => (
                                        <option key={profile.id} value={profile.id}>
                                          {profile.profileName}
                                        </option>
                                      ))}
                                    </select>
                                  )}
                                  {errors.profileId && (
                                    <span className="text-sm text-red-600">
                                      {errors.profileId.message}
                                    </span>
                                  )}
                                </div>

                                <div className="flex flex-col gap-2">
                                  <Label className="text-white">
                                    Filiais de Acesso
                                  </Label>
                                  {isLoading ? (
                                    <p className="text-white">
                                      Carregando filiais...
                                    </p>
                                  ) : availableBranches.length > 0 ? (
                                    <div className="grid grid-cols-1 gap-2 md:grid-cols-2">
                                      {availableBranches.map((branch) => (
                                        <div
                                          key={branch.idBranch}
                                          className="flex items-center space-x-2"
                                        >
                                          <input
                                            type="checkbox"
                                            id={`branch-${branch.idBranch}`}
                                            checked={selectedBranchIds.includes(
                                              branch.idBranch
                                            )}
                                            onChange={(e) =>
                                              handleBranchSelection(
                                                branch.idBranch,
                                                e.target.checked
                                              )
                                            }
                                            className="form-checkbox h-4 w-4 text-realizaBlue border-gray-300 rounded focus:ring-realizaBlue"
                                          />
                                          <Label
                                            htmlFor={`branch-${branch.idBranch}`}
                                            className="text-white"
                                          >
                                            {branch.name}
                                          </Label>
                                        </div>
                                      ))}
                                    </div>
                                  ) : (
                                    <p className="text-white">
                                      Nenhuma filial disponível.
                                    </p>
                                  )}
                                  {errors.branchAccessIds && (
                                    <span className="text-sm text-red-600">
                                      {errors.branchAccessIds.message}
                                    </span>
                                  )}
                                </div>

                                {selectedBranchIds.length > 0 && (
                                  <div className="flex flex-col gap-2">
                                    <Label className="text-white">
                                      Contratos de Acesso
                                    </Label>
                                    {isLoading ? (
                                      <p className="text-white">
                                        Carregando contratos...
                                      </p>
                                    ) : availableContracts.length > 0 ? (
                                      <div className="grid grid-cols-1 gap-2 md:grid-cols-2">
                                        {availableContracts.map((contract) => (
                                          <div
                                            key={contract.id}
                                            className="flex items-center space-x-2"
                                          >
                                            <input
                                              type="checkbox"
                                              id={`contract-${contract.id}`}
                                              checked={selectedContractIds.includes(
                                                contract.id
                                              )}
                                              onChange={(e) =>
                                                handleContractSelection(
                                                  contract.id,
                                                  e.target.checked
                                                )
                                              }
                                              className="form-checkbox h-4 w-4 text-realizaBlue border-gray-300 rounded focus:ring-realizaBlue"
                                            />
                                            <Label
                                              htmlFor={`contract-${contract.id}`}
                                              className="text-white"
                                            >
                                              {contract.contractReference} ({contract.branchName})
                                            </Label>
                                          </div>
                                        ))}
                                      </div>
                                    ) : (
                                      <p className="text-white">
                                        Nenhum contrato encontrado para as filiais selecionadas.
                                      </p>
                                    )}
                                    {errors.contractAccessIds && (
                                      <span className="text-sm text-red-600">
                                        {errors.contractAccessIds.message}
                                      </span>
                                    )}
                                  </div>
                                )}

                                <div className="flex justify-end">
                                  <div>
                                    {isLoading ? (
                                      <Button className="bg-realizaBlue w-full">
                                        <Oval
                                          visible={true}
                                          height="80"
                                          width="80"
                                          color="#4fa94d"
                                          ariaLabel="oval-loading"
                                        />
                                      </Button>
                                    ) : (
                                      <Button
                                        className="bg-realizaBlue w-full"
                                        type="submit"
                                      >
                                        Criar
                                      </Button>
                                    )}
                                  </div>
                                </div>
                              </form>
                            </ScrollArea>
                          </DialogContent>
                        </Dialog>
                      )}
                    </nav>
                  </div>
                  {selectedTab === "filiais" && (
                    <div>
                      <BranchesTable />
                    </div>
                  )}
                  {selectedTab === "usuarios" && (
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

                      <div className="block space-y-4 md:hidden">
                        {usersFromBranch && usersFromBranch.length > 0 ? (
                          usersFromBranch.map((users: any) => (
                            <div
                              key={users.idUser}
                              className="rounded-lg border border-gray-300 bg-white p-4 shadow-sm"
                            >
                              <p className="text-sm font-semibold text-gray-700">
                                Nome:
                              </p>
                              <p className="text-realizaBlue mb-2">
                                {users.firstName} {users.surname}
                              </p>

                              <p className="text-sm font-semibold text-gray-700">
                                CPF:
                              </p>
                              <p className="mb-2 text-gray-800">{users.cpf}</p>

                              <Link
                                to={`/sistema/detailsUsers/${users.idUser}`}
                              >
                                <button className="text-realizaBlue mt-2 flex items-center gap-1 hover:underline">
                                  <Settings2 size={18} /> Acessar
                                </button>
                              </Link>
                            </div>
                          ))
                        ) : (
                          <p className="text-center text-gray-600">
                            Nenhum colaborador encontrado
                          </p>
                        )}
                      </div>
                      <div className="hidden overflow-x-auto rounded-lg border bg-white p-4 shadow-lg md:block">
                        <table className="w-full border-collapse border border-gray-300">
                          <thead>
                            <tr>
                              <th className="border border-gray-300 px-4 py-2 text-start">
                                Nome
                              </th>
                              <th className="border border-gray-300 px-4 py-2 text-start">
                                CPF
                              </th>
                              <th className="border border-gray-300 px-4 py-2 text-start">
                                Ações
                              </th>
                            </tr>
                          </thead>
                          <tbody>
                            {usersFromBranch && usersFromBranch.length > 0 ? (
                              usersFromBranch.map((users: any) => (
                                <tr key={users.idUser}>
                                  <td className="border border-gray-300 px-4 py-2">
                                    {users.firstName} {users.surname}
                                  </td>
                                  <td className="border border-gray-300 px-4 py-2">
                                    {users.cpf}
                                  </td>
                                  <td className="border border-gray-300 px-4 py-2">
                                    <Link
                                      to={`/sistema/detailsUsers/${users.idUser}`}
                                      className="text-realizaBlue hover:underline flex items-center gap-1"
                                    >
                                      <Settings2 size={16} />
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
                    </div>
                  )}
                </div>
              </div>
              <div className="w-[30vw] flex justify-center">
                <div className="h-[60vh] w-[30vw] rounded-lg border bg-white p-5 shadow-sm">
                  <ConformityGaugeChart
                    percentage={data?.conformity}
                    loading={isLoading}
                  />
                </div>
              </div>
            </div>
          </div>
          <div className="mt-9 flex w-full">
            <ActiveContracts count={data?.activeContractQuantity ?? 0} />
            <Employees count={data?.activeEmployeeQuantity ?? 0} />
            <Suppliers count={data?.activeSupplierQuantity ?? 0} />
            <AllocatedEmployees count={data?.allocatedEmployeeQuantity ?? 0} />
          </div>
          <div className="mt-5 w-full text-right">
            <Link to={`/sistema/dashboard-details/${user?.idUser}`}>
              <Button className="hover:bg-realizaBlue dark:bg-primary bg-realizaBlue dark:text-white dark:hover:bg-blue-950">
                Ver mais <ChevronRight />
              </Button>
            </Link>
          </div>
        </div>
      </section>
    </>
  );
}