import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { FilePlus } from "lucide-react";
import { Button } from "./ui/button";
import { Label } from "./ui/label";
import { Input } from "./ui/input";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useEffect, useState } from "react";
import { Oval } from "react-loader-spinner";
import { toast } from "sonner";
import { ScrollArea } from "./ui/scroll-area";
import { useBranch } from "@/context/Branch-provider";
import { Search, UserPlus } from "lucide-react";
import { fetchCompanyByCNPJ } from "@/hooks/gets/realiza/useCnpjApi";
import { useUser } from "@/context/user-provider";
import { useDataSendEmailContext } from "@/context/dataSendEmail-Provider";

const cnpjRegex = /^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/;
const phoneRegex = /^\(?\d{2}\)?\s?\d{4,5}-?\d{4}$/;

export const modalSendEmailFormSchema = z.object({
  email: z.string().email("Insira um email válido"),
  phone: z
    .string()
    .optional()
    .refine((val) => !val || phoneRegex.test(val), {
      message: "Telefone inválido, use o formato (XX) XXXXX-XXXX",
    }),
  cnpj: z
    .string()
    .nonempty("Insira o CNPJ")
    .regex(cnpjRegex, "CNPJ inválido, use o formato 00.000.000/0000-00"),
  corporateName: z.string().nonempty("Insira o nome da empresa"),
});

export const modalSendEmailFormSchemaSubContractor = z.object({
  email: z.string().email("Insira um email válido"),
  phone: z
    .string()
    .optional()
    .refine((val) => !val || phoneRegex.test(val), {
      message: "Telefone inválido, use o formato (XX) XXXXX-XXXX",
    }),
  cnpj: z
    .string()
    .nonempty("Insira o CNPJ")
    .regex(cnpjRegex, "CNPJ inválido, use o formato 00.000.000/0000-00"),
  corporateName: z.string().nonempty("Insira o nome da empresa"),
  providerSubcontractor: z
    .string()
    .nonempty("Selecionar um fornecedor é obrigatório"),
});

export const contractFormSchema = z.object({
  cnpj: z.string(),
  serviceName: z.string().nonempty("Nome do serviço é obrigatório"),
  idServiceType: z.string().nonempty("Tipo de serviço é obrigatório"),
  description: z.string().optional(),
  expenseType: z.string().nonempty("Tipo do serviço é obrigatório"),
  labor: z.boolean(),
  hse: z.boolean(),
  dateStart: z.string().nonempty("Início efetivo é obrigatório"),
  idResponsible: z.string().nonempty("Selecione um gestor"),
  contractReference: z
    .string()
    .nonempty("Referência do contrato é obrigatório"),
  subcontractPermission: z.enum(["true", "false"], {}).optional(),
});

type ModalSendEmailFormSchema = z.infer<typeof modalSendEmailFormSchema>;

type ModalSendEmailFormSchemaSubContractor = z.infer<
  typeof modalSendEmailFormSchemaSubContractor
>;
type ContractFormSchema = z.infer<typeof contractFormSchema>;

export function ModalCadastroSubcontratado() {
  const [managers, setManagers] = useState<any>([]);
  const [activities, setActivities] = useState<any>([]);
  const [pushCnpj, setPushCnpj] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [nextModal, setNextModal] = useState(false);
  const [providerDatas, setProviderDatas] = useState<any>({});
  const { user } = useUser();
  const { selectedBranch } = useBranch();
  const [isSubcontractor, setIsSubContractor] = useState<string | null>(null);
  const [suppliers, setSuppliers] = useState<any>([]);
  const [getIdManager, setGetIdManager] = useState<string | null>(null);
  const { setDatasSender } = useDataSendEmailContext();
  const [isSsma, setIsSsma] = useState(false);
  const [selectedActivities, setSelectedActivities] = useState<string[]>([]);
  const [servicesType, setServicesType] = useState([]);
  const [cnpjValue, setCnpjValue] = useState("");
  const [phoneValue, setPhoneValue] = useState("");
  const [searchService, setSearchService] = useState("");
  const [searchActivity, setSearchActivity] = useState("");
  const [usedEmails, setUsedEmails] = useState<string[]>([]);
  const [isButtonDisabled, setIsButtonDisabled] = useState(false);
  const [isMainModalOpen, setIsMainModalOpen] = useState(false);
  const [targetContractId, setTargetContractId] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    getValues,
    reset,
  } = useForm<ModalSendEmailFormSchema>({
    resolver: zodResolver(modalSendEmailFormSchema),
  });

  const {
    register: registerContract,
    handleSubmit: handleSubmitContract,
    formState: { errors: errorsContract },
    reset: resetContract,
  } = useForm<ContractFormSchema>({
    resolver: zodResolver(contractFormSchema),
  });

  const {
    register: registerSubContract,
    handleSubmit: handleSubmitSubContract,
    formState: { errors: errorsSubContract },
    setValue: setValueSubContract,
    getValues: getValuesSubContract,
    reset: resetSubContract,
  } = useForm<ModalSendEmailFormSchemaSubContractor>({
    resolver: zodResolver(modalSendEmailFormSchemaSubContractor),
  });

  const resetFormState = () => {
    reset();
    resetContract();
    resetSubContract();

    setIsSubContractor(null);
    setCnpjValue("");
    setPhoneValue("");
    setNextModal(false);
    setProviderDatas({});
    setPushCnpj(null);
    setIsSsma(false);
    setSelectedActivities([]);
    setSearchService("");
    setSearchActivity("");
    setUsedEmails([]);
    setIsLoading(false);
    setIsButtonDisabled(false);
  };

  const handleModalOpenChange = (isOpen: boolean) => {
    setIsMainModalOpen(isOpen);
    if (isOpen) {
      setIsSubContractor("subcontratado");
    } else {
      resetFormState();
    }
  };

  const handleCheckboxChange = (activityId: string, isChecked: boolean) => {
    if (isChecked) {
      setSelectedActivities((prev) => [...prev, activityId]);
    } else {
      setSelectedActivities((prev) => prev.filter((id) => id !== activityId));
    }
  };

  type RiscoNivel = "LOW" | "MEDIUM" | "HIGH" | "VERY_HIGH";

  const formatarRisco = (risco: RiscoNivel): string => {
    switch (risco) {
      case "LOW":
        return "Risco: Baixo";
      case "MEDIUM":
        return "Risco: Médio";
      case "HIGH":
        return "Risco: Alto";
      case "VERY_HIGH":
        return "Risco: Muito Alto";
      default:
        return risco;
    }
  };

  const formatCNPJ = (value: string) => {
    return value
      .replace(/\D/g, "")
      .replace(/^(\d{2})(\d)/, "$1.$2")
      .replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3")
      .replace(/\.(\d{3})(\d)/, ".$1/$2")
      .replace(/(\d{4})(\d)/, "$1-$2")
      .slice(0, 18);
  };

  const formatPhone = (value: string) => {
    const digits = value.replace(/\D/g, "");

    if (digits.length <= 2) {
      return digits;
    } else if (digits.length <= 6) {
      return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
    } else if (digits.length <= 10) {
      return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
    } else {
      return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(
        7,
        11
      )}`;
    }
  };

  useEffect(() => {
    if (isSubcontractor === "contratado") {
      setCnpjValue(getValues("cnpj") || "");
      setPhoneValue(getValues("phone") || "");
    } else if (isSubcontractor === "subcontratado") {
      setCnpjValue(getValuesSubContract("cnpj") || "");
      setPhoneValue(getValuesSubContract("phone") || "");
    }
  }, [getValues, getValuesSubContract, isSubcontractor]);

  const handleCNPJSearch = async () => {
    const cnpjValueToSearch =
      isSubcontractor === "contratado"
        ? getValues("cnpj")
        : getValuesSubContract("cnpj");
    setIsLoading(true);
    try {
      const companyData = await fetchCompanyByCNPJ(cnpjValueToSearch);
      if (isSubcontractor === "contratado") {
        setValue("corporateName", companyData.razaoSocial || "");
      } else {
        setValueSubContract("corporateName", companyData.razaoSocial || "");
      }
    } catch (error) {
      toast.error("Erro ao buscar dados do CNPJ");
    } finally {
      setIsLoading(false);
    }
  };

  const getSupplier = async () => {
    if (!selectedBranch?.idBranch) return;
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/contract/supplier/subcontract-permission`,
        {
          params: {
            idBranch: selectedBranch.idBranch,
          },
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setSuppliers(res.data);
    } catch (err) {
      console.log("Erro ao buscar prestadores de serviço", err);
    }
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getSupplier();
      setSuppliers([]);
    }
  }, [selectedBranch]);

  const getActivities = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const activitieData = await axios.get(
        `${ip}/contract/activity/find-by-branch/${selectedBranch?.idBranch}`,
        {
          params: {
            size: 1000,
          },
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setActivities(activitieData.data);
    } catch (err) {
      console.log(err);
    }
  };

  const createClient = async (
    data: ModalSendEmailFormSchema | ModalSendEmailFormSchemaSubContractor
  ) => {
    setIsLoading(true);

    const emailAtual = data.email.toLowerCase();
    if (usedEmails.includes(emailAtual)) {
      toast.error("E-mail já utilizado nesta sessão");
      setIsLoading(false);
      return;
    }

    try {
      let payload;
      if (isSubcontractor === "contratado") {
        payload = { ...data };
      } else {
        payload = {
          ...data,
          idContractSupplier: (data as ModalSendEmailFormSchemaSubContractor)
            .providerSubcontractor,
        };
      }

      setProviderDatas(payload);
      setPushCnpj(data.cnpj);
      setUsedEmails((prev) => [...prev, emailAtual]);
      toast.success("Prestador preenchido com sucesso");
      setNextModal(true);
    } catch (err) {
      console.log("Erro ao criar prestador", err);
      toast.error("Erro ao criar prestador. Tente novamente");
    } finally {
      setIsLoading(false);
    }
  };

  const getManager = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/user/client/filtered-client?idSearch=${selectedBranch?.idBranch}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setManagers(res.data.content);
    } catch (err) {
      console.log(
        `erro ao buscar gestores da filia: ${selectedBranch?.name}`,
        err
      );
    }
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getManager();
    }
  }, [selectedBranch?.idBranch]);

  const createContract = async (data: ContractFormSchema) => {
    if (!providerDatas) {
      toast.error("Dados do prestador não encontrados. Reinicie o processo.");
      return;
    }
    setIsLoading(true);
    setIsButtonDisabled(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      let payload: any = {
        ...data,
        subcontractPermission: data.subcontractPermission === "true",
        idRequester: user?.idUser,
        idBranch: selectedBranch?.idBranch,
        idActivities: selectedActivities,
      };

      let apiUrl = "";

      if (isSubcontractor === "contratado") {
        apiUrl = `${ip}/contract/supplier`;
        payload = {
          ...payload,
          providerDatas,
        };
      } else if (isSubcontractor === "subcontratado") {
        apiUrl = `${ip}/contract/subcontractor`;
        payload = {
          ...payload,
          serviceName: data.serviceName,
          contractReference: data.contractReference,
          description: data.description,
          expenseType: data.expenseType,
          labor: data.labor,
          hse: data.hse,
          dateStart: data.dateStart,
          idRequester: user?.idUser,
          idActivities: selectedActivities,
          idContractSupplier: providerDatas.idContractSupplier,
          providerDatas: {
            corporateName: providerDatas?.corporateName || "",
            email: providerDatas?.email || "",
            cnpj: providerDatas?.cnpj || "",
            telephone: providerDatas?.phone || "",
          },
        };
      } else {
        toast.error("Tipo de contratação não selecionado.");
        setIsLoading(false);
        setIsButtonDisabled(false);
        return;
      }

      setDatasSender(payload);

      await axios.post(apiUrl, payload, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      toast.success("Contrato criado com sucesso!");
      setNextModal(false);
      setIsMainModalOpen(false);
    } catch (err: any) {
      if (err.response) {
        console.error("Erro no servidor:", err.response.data);
        toast.error(
          `Erro ao criar contrato: ${
            err.response.data.message || "Erro desconhecido."
          }`
        );
      } else if (err.request) {
        console.error("Erro na requisição:", err.request);
        toast.error("Erro na requisição ao servidor.");
      } else {
        console.error("Erro ao configurar requisição:", err.message);
        toast.error("Erro ao criar contrato. Tente novamente.");
      }
    } finally {
      setIsLoading(false);
      setIsButtonDisabled(false);
    }
  };

  const getServicesType = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/contract/service-type`, {
        params: {
          owner: "BRANCH",
          idOwner: selectedBranch?.idBranch,
        },
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setServicesType(res.data);
    } catch (err) {
      console.log("Erro ao buscar serviços", err);
    }
  };

  return (
    <Dialog open={isMainModalOpen} onOpenChange={handleModalOpenChange}>
      <DialogTrigger asChild>
        <Button className="w-full text-left px-4 py-2 text-sm text-gray-700 font-normal bg-white hover:bg-gray-100 flex items-center gap-2">
          <FilePlus className="w-4 h-4" /> Cadastrar subcontratado
        </Button>
      </DialogTrigger>
      <DialogTrigger asChild>
        <Button className="h-[8vw] w-[8vw] bg-sky-700 md:hidden">+</Button>
      </DialogTrigger>
      <DialogContent className="max-w-[90vw] md:max-w-[45vw]">
        <div className="flex items-center justify-between bg-[#2E3C4D] px-5 py-4 h-[60px] min-w-full">
          <h2 className="text-white text-base font-semibold flex items-center gap-2">
            <UserPlus className="w-5 h-5 text-[#C0B15B]" />
            Cadastrar novo subcontratado
          </h2>
        </div>

        <div className="bg-[#F2F3F5] text-sm text-gray-800 p-2 px-4 rounded shadow mb-4">
          <strong>Filial:</strong>{" "}
          {selectedBranch?.name ?? "Nenhuma filial selecionada"}
        </div>

        <div>
          {isSubcontractor === "subcontratado" && (
            <form
              onSubmit={handleSubmitSubContract(createClient)}
              className="flex flex-col gap-4"
            >
              <div className="relative">
                <Label className="text-black">CNPJ</Label>
                <div className="flex items-center gap-3">
                  <Input
                    type="text"
                    placeholder="00.000.000/0000-00"
                    value={cnpjValue}
                    onChange={(e) => {
                      const formatted = formatCNPJ(e.target.value);
                      setCnpjValue(formatted);
                      setValueSubContract("cnpj", formatted, {
                        shouldValidate: true,
                      });
                    }}
                    className="w-full rounded-md border border-gray-300 px-3 py-2 bg-[#F2F3F5] text-gray-700"
                  />
                  {isLoading ? (
                    <div
                      onClick={handleCNPJSearch}
                      className="bg-realizaBlue cursor-pointer rounded-lg p-2 text-black transition-all hover:bg-neutral-500"
                    >
                      <Oval
                        visible={true}
                        height="30"
                        width="40"
                        color="#34495E"
                        ariaLabel="oval-loading"
                        wrapperStyle={{}}
                        wrapperClass=""
                      />
                    </div>
                  ) : (
                    <div
                      onClick={handleCNPJSearch}
                      className="bg-realizaBlue cursor-pointer rounded-lg p-2 text-black transition-all hover:bg-neutral-500"
                    >
                      <Search />
                    </div>
                  )}
                </div>
                {errorsSubContract.cnpj && (
                  <span className="text-red-600">
                    {errorsSubContract.cnpj.message}
                  </span>
                )}
              </div>

              <div className="mb-1">
                <Label className="text-black">Razão Social</Label>
                <Input
                  type="text"
                  placeholder="Digite a razão social do novo prestador"
                  {...registerSubContract("corporateName")}
                  className="w-full rounded-md border border-gray-300 px-3 py-2 bg-[#F2F3F5] text-gray-700"
                />
                {errorsSubContract.corporateName && (
                  <span className="text-red-600">
                    {errorsSubContract.corporateName.message}
                  </span>
                )}
              </div>
              <div className="mb-1">
                <Label className="text-black">Email</Label>
                <Input
                  type="email"
                  placeholder="Digite o email do novo prestador"
                  {...registerSubContract("email")}
                  className="w-full rounded-md border border-gray-300 px-3 py-2 bg-[#F2F3F5] text-gray-700"
                />
                {errorsSubContract.email && (
                  <span className="text-red-600">
                    {errorsSubContract.email.message}
                  </span>
                )}
              </div>
              <div className="flex flex-col gap-2">
                <Label className="text-black">Telefone</Label>
                <Input
                  type="text"
                  value={phoneValue}
                  onChange={(e) => {
                    const formattedPhone = formatPhone(e.target.value);
                    setPhoneValue(formattedPhone);
                    setValueSubContract("phone", formattedPhone, {
                      shouldValidate: true,
                    });
                  }}
                  placeholder="(00) 00000-0000"
                  maxLength={15}
                  className="w-full rounded-md border border-gray-300 px-3 py-2 bg-[#F2F3F5] text-gray-700"
                />
                {errorsSubContract.phone && (
                  <span className="text-red-600">
                    {errorsSubContract.phone.message}
                  </span>
                )}
              </div>
              <div className="flex flex-col gap-1">
                <Label className="text-black">Selecione um contrato</Label>
                <select
                  defaultValue={targetContractId || ""}
                  className="rounded-lg p-2"
                  {...registerSubContract("providerSubcontractor")}
                >
                  <option value="" disabled>
                    Selecione uma opção
                  </option>
                  {Array.isArray(suppliers) &&
                    suppliers.map((supplier: any) => (
                      <option
                        value={supplier.idContract}
                        key={supplier.idContract}
                      >
                        {supplier.contractReference}
                      </option>
                    ))}
                </select>
                {errorsSubContract.providerSubcontractor && (
                  <span className="text-red-600">
                    {errorsSubContract.providerSubcontractor.message}
                  </span>
                )}
              </div>
              <div className="flex justify-end">
                {isLoading ? (
                  <Button>
                    <Oval
                      visible={true}
                      height="30"
                      width="30"
                      color="#fff"
                      ariaLabel="radio-loading"
                      wrapperStyle={{}}
                      wrapperClass=""
                    />
                  </Button>
                ) : (
                  <Button
                    className="bg-realizaBlue"
                    type="submit"
                    onClick={() => {
                      getActivities(), getServicesType();
                    }}
                  >
                    Próximo
                  </Button>
                )}
              </div>
            </form>
          )}
          <Dialog open={nextModal} onOpenChange={setNextModal}>
            <DialogContent className="max-w-[95vw] border-none md:max-w-[45vw]">
              <DialogHeader className="bg-[#1E2A38] px-6 py-4 rounded-t-md">
                <DialogTitle className="text-white flex items-center gap-2 text-base font-semibold">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    className="w-5 h-5 stroke-yellow-400"
                    viewBox="0 0 24 24"
                  >
                    <path
                      stroke="currentColor"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth="2"
                      d="M4 6h16M4 12h16M4 18h7"
                    />
                  </svg>
                  Faça o contrato
                </DialogTitle>
              </DialogHeader>
              <div className="bg-[#F3F4F6] px-6 py-2">
                <p className="text-sm text-gray-700 font-medium">
                  Filial: {selectedBranch?.name}
                </p>
              </div>

              <ScrollArea className="h-[60vh] w-full px-2">
                <div className="w-full flex flex-col gap-4">
                  <form
                    className="flex flex-col gap-2"
                    onSubmit={handleSubmitContract(createContract, (errors) => {
                      console.error(
                        "Validation errors for contract form:",
                        errors
                      );
                      toast.error(
                        "Por favor, preencha todos os campos obrigatórios do contrato."
                      );
                    })}
                  >
                    <div>
                      <Label className="text-black">
                        CNPJ do novo prestador
                      </Label>
                      <div>
                        <Input
                          type="text"
                          {...registerContract("cnpj")}
                          value={pushCnpj || "erro ao puxar cnpj"}
                          readOnly
                        />
                        {errorsContract.cnpj && (
                          <span className="text-red-600">
                            {errorsContract.cnpj.message}
                          </span>
                        )}
                      </div>
                    </div>

                    <div className="flex flex-col gap-2">
                      <Label className="text-black">Gestor do serviço</Label>
                      <select
                        key={getIdManager}
                        className="rounded-md border p-2"
                        {...registerContract("idResponsible")}
                      >
                        <option value="" disabled>
                          Selecione um gestor
                        </option>
                        {Array.isArray(managers) &&
                          managers.map((manager: any) => (
                            <option
                              value={manager.idUser}
                              onClick={() => setGetIdManager(manager.idUser)}
                              key={manager.idUser}
                            >
                              {manager.firstName} {manager.surname}{" "}
                            </option>
                          ))}
                      </select>
                      {errorsContract.idResponsible && (
                        <span className="text-red-600">
                          {errorsContract.idResponsible.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-black">Nome do Serviço</Label>
                      <Input {...registerContract("serviceName")} />
                      {errorsContract.serviceName && (
                        <span className="text-red-500">
                          {errorsContract.serviceName.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-black">
                        Data de início efetivo
                      </Label>
                      <Input type="date" {...registerContract("dateStart")} />
                      {errorsContract.dateStart && (
                        <span className="text-red-600">
                          {errorsContract.dateStart.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-black">
                        Referência do contrato
                      </Label>
                      <Input {...registerContract("contractReference")} />
                      {errorsContract.contractReference && (
                        <span className="text-red-500">
                          {errorsContract.contractReference.message}
                        </span>
                      )}
                    </div>
                    <div className="flex flex-col gap-1">
                      <Label className="text-black">Tipo de despesa</Label>
                      <select
                        {...registerContract("expenseType")}
                        className="rounded-md border p-2"
                        defaultValue=""
                      >
                        <option value="" disabled>
                          Selecione uma opção
                        </option>
                        <option value="CAPEX">CAPEX</option>
                        <option value="OPEX">OPEX</option>
                        <option value="NENHUM">Nenhuma</option>
                      </select>
                      {errorsContract.expenseType && (
                        <span className="text-red-500">
                          {errorsContract.expenseType.message}
                        </span>
                      )}
                    </div>

                    <div className="flex flex-col gap-3">
                      <Label className="text-black">Tipo de Gestão</Label>
                      <div className="flex flex-col items-start gap-3">
                        <div className="flex flex-row-reverse gap-2">
                          <Label className="text-[14px] text-black">SSMA</Label>
                          <input
                            type="checkbox"
                            checked={isSsma}
                            {...registerContract("hse", {
                              onChange: (e) => {
                                setIsSsma(e.target.checked);
                              },
                            })}
                          />
                        </div>
                        <div className="flex flex-row-reverse gap-2">
                          <Label className="text-[14px] text-black">
                            TRABALHISTA
                          </Label>
                          <input
                            type="checkbox"
                            {...registerContract("labor")}
                          />
                        </div>
                      </div>

                      {errorsContract.expenseType && (
                        <span className="text-red-500">
                          {errorsContract.expenseType.message}
                        </span>
                      )}
                    </div>
                    <div className="flex flex-col gap-1">
                      <Label className="text-black">Tipo do Serviço</Label>

                      <div className="border border-neutral-400 flex items-center gap-2 rounded-md px-2 py-1 bg-white shadow-sm">
                        <Search className="text-neutral-500 w-5 h-5" />
                        <input
                          type="text"
                          placeholder="Pesquisar serviço..."
                          value={searchService}
                          onChange={(e) => setSearchService(e.target.value)}
                          className="border-none w-full outline-none text-sm placeholder:text-neutral-400"
                        />
                      </div>

                      <select
                        {...registerContract("idServiceType")}
                        className="rounded-md border p-2 w-full mt-1"
                        defaultValue=""
                      >
                        <option value="" disabled>
                          Selecione uma opção
                        </option>
                        {servicesType
                          .filter((s: any) =>
                            s.title
                              .toLowerCase()
                              .includes(searchService.toLowerCase())
                          )
                          .map((idServiceType: any) => (
                            <option
                              key={idServiceType.idServiceType}
                              value={idServiceType.idServiceType}
                            >
                              {idServiceType.title} -{" "}
                              {formatarRisco(idServiceType.risk as RiscoNivel)}
                            </option>
                          ))}
                      </select>

                      {errorsContract.idServiceType && (
                        <span className="text-red-500">
                          {errorsContract.idServiceType.message}
                        </span>
                      )}
                    </div>

                    {isSsma === true && (
                      <div className="flex flex-col gap-2">
                        <Label className="text-black">Tipo de atividade</Label>

                        <div className="border border-neutral-400 flex items-center gap-2 rounded-md px-2 py-1 bg-white shadow-sm">
                          <Search className="text-neutral-500 w-5 h-5" />
                          <input
                            type="text"
                            placeholder="Pesquisar atividade..."
                            value={searchActivity}
                            onChange={(e) => setSearchActivity(e.target.value)}
                            className="border-none w-full outline-none text-sm placeholder:text-neutral-400"
                          />
                        </div>

                        <ScrollArea className="h-[20vh] p-2 rounded-lg bg-white shadow-inner">
                          <div className="flex flex-col gap-2">
                            {Array.isArray(activities) &&
                              activities
                                .filter((activity: any) =>
                                  activity.title
                                    .toLowerCase()
                                    .includes(searchActivity.toLowerCase())
                                )
                                .map((activity: any) => (
                                  <label
                                    key={activity.idActivity}
                                    className="flex items-center gap-2 text-black"
                                  >
                                    <input
                                      type="checkbox"
                                      checked={selectedActivities.includes(
                                        activity.idActivity
                                      )}
                                      onChange={(e) =>
                                        handleCheckboxChange(
                                          activity.idActivity,
                                          e.target.checked
                                        )
                                      }
                                    />
                                    {activity.title}
                                  </label>
                                ))}
                          </div>
                        </ScrollArea>
                      </div>
                    )}

                    {isSsma === true && selectedActivities.length > 0 && (
                      <div className="flex flex-col gap-1 mt-2">
                        <Label className="text-black">
                          Atividades selecionadas
                        </Label>
                        <div className="bg-white rounded-md border p-2 h-auto min-h-[3rem] max-h-[10rem] overflow-y-auto">
                          <ul className="list-disc ml-4 text-sm text-black">
                            {selectedActivities.map((idAtividade) => {
                              const atividade = activities.find(
                                (a: any) => a.idActivity === idAtividade
                              );
                              return (
                                <li key={idAtividade}>
                                  {atividade
                                    ? atividade.title
                                    : "Atividade não encontrada"}
                                </li>
                              );
                            })}
                          </ul>
                        </div>
                      </div>
                    )}

                    <div className="flex flex-col gap-1">
                      <Label className="text-black">
                        Descrição detalhada do serviço
                      </Label>
                      <textarea
                        {...registerContract("description")}
                        className="rounded-md border p-2"
                      />
                      {errorsContract.description && (
                        <span className="text-red-500">
                          {errorsContract.description.message}
                        </span>
                      )}
                    </div>

                    {isLoading ? (
                      <Button
                        className="bg-realizaBlue"
                        type="submit"
                        disabled={isButtonDisabled}
                      >
                        <Oval
                          visible={true}
                          height="30"
                          width="30"
                          color="#fff"
                          ariaLabel="oval-loading"
                          wrapperStyle={{}}
                          wrapperClass=""
                        />
                      </Button>
                    ) : (
                      <Button
                        className="bg-realizaBlue"
                        type="submit"
                        disabled={isButtonDisabled}
                      >
                        Enviar contrato
                      </Button>
                    )}
                  </form>
                </div>
              </ScrollArea>
            </DialogContent>
          </Dialog>
        </div>
      </DialogContent>
    </Dialog>
  );
}
