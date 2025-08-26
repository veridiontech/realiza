import type { FC } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { FilePlus, Search, UserPlus } from "lucide-react";
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
import { fetchCompanyByCNPJ } from "@/hooks/gets/realiza/useCnpjApi";
import { useUser } from "@/context/user-provider";
import { useDataSendEmailContext } from "@/context/dataSendEmail-Provider";

const cnpjRegex = /^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/;
const phoneRegex = /^\(?\d{2}\)?\s?\d{4,5}-?\d{4}$/;

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
  idContractSupplier: z.string().nonempty("O contrato principal é obrigatório"),
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

type ModalSendEmailFormSchemaSubContractor = z.infer<
  typeof modalSendEmailFormSchemaSubContractor
>;
type ContractFormSchema = z.infer<typeof contractFormSchema>;

interface ModalCadastroSubcontratadoProps {
  idContract: string;
}

export const ModalCadastroSubcontratado: FC<ModalCadastroSubcontratadoProps> = ({ idContract }) => {
   console.log("ID DO CONTRATO RECEBIDO COMO PROP:", idContract);
  const [managers, setManagers] = useState<any[]>([]);
  const [activities, setActivities] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [nextModal, setNextModal] = useState(false);
  const [providerDatas, setProviderDatas] = useState<any>({});
  const { user } = useUser();
  const { selectedBranch } = useBranch();
  const { setDatasSender } = useDataSendEmailContext();
  const [isSsma, setIsSsma] = useState(false);
  const [selectedActivities, setSelectedActivities] = useState<string[]>([]);
  const [servicesType, setServicesType] = useState<any[]>([]);
  const [cnpjValue, setCnpjValue] = useState("");
  const [phoneValue, setPhoneValue] = useState("");
  const [searchService, setSearchService] = useState("");
  const [searchActivity, setSearchActivity] = useState("");
  const [usedEmails, setUsedEmails] = useState<string[]>([]);
  const [isButtonDisabled, setIsButtonDisabled] = useState(false);
  const [isMainModalOpen, setIsMainModalOpen] = useState(false);
  const [parentContractInfo, setParentContractInfo] = useState<{ reference: string } | null>(null);

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

  useEffect(() => {
    const fetchParentContractDetails = async () => {
      if (!idContract) return;
      try {
        const tokenFromStorage = localStorage.getItem("tokenClient");
        const res = await axios.get(`${ip}/contract/supplier/${idContract}`, {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        });
        setParentContractInfo({ reference: res.data.contractReference });
      } catch (error) {
        console.error("Erro ao buscar detalhes do contrato principal", error);
        toast.error("Não foi possível carregar os dados do contrato principal.");
      }
    };

    fetchParentContractDetails();
  }, [idContract]);

  const resetFormState = () => {
    resetContract();
    resetSubContract();
    setCnpjValue("");
    setPhoneValue("");
    setNextModal(false);
    setProviderDatas({});
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
      setValueSubContract("idContractSupplier", idContract);
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
      return `(${digits}`;
    } else if (digits.length <= 6) {
      return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
    } else if (digits.length <= 10) {
      return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
    } else {
      return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7, 11)}`;
    }
  };

  const handleCNPJSearch = async () => {
    const cnpjValueToSearch = getValuesSubContract("cnpj");
    setIsLoading(true);
    try {
      const companyData = await fetchCompanyByCNPJ(cnpjValueToSearch);
      setValueSubContract("corporateName", companyData.razaoSocial || "");
    } catch (error) {
      toast.error("Erro ao buscar dados do CNPJ");
    } finally {
      setIsLoading(false);
    }
  };

  const getActivities = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const activitieData = await axios.get(
        `${ip}/contract/activity/find-by-branch/${selectedBranch?.idBranch}`,
        {
          params: { size: 1000 },
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setActivities(activitieData.data);
    } catch (err) {
      console.log(err);
    }
  };

  const createClient = async (data: ModalSendEmailFormSchemaSubContractor) => {
    setIsLoading(true);

    const emailAtual = data.email.toLowerCase();
    if (usedEmails.includes(emailAtual)) {
      toast.error("E-mail já utilizado nesta sessão");
      setIsLoading(false);
      return;
    }

    try {
      setProviderDatas(data);
      setUsedEmails((prev) => [...prev, emailAtual]);
      toast.success("Dados do subcontratado validados com sucesso");
      setNextModal(true);
    } catch (err) {
      console.log("Erro ao validar dados", err);
      toast.error("Erro ao validar dados. Tente novamente");
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
      const payload: any = {
        ...data,
        subcontractPermission: data.subcontractPermission === "true",
        idRequester: user?.idUser,
        idBranch: selectedBranch?.idBranch,
        idActivities: selectedActivities,
        serviceName: data.serviceName,
        contractReference: data.contractReference,
        description: data.description,
        expenseType: data.expenseType,
        labor: data.labor,
        hse: data.hse,
        dateStart: data.dateStart,
        idContractSupplier: providerDatas.idContractSupplier,
        providerDatas: {
          corporateName: providerDatas?.corporateName || "",
          email: providerDatas?.email || "",
          cnpj: providerDatas?.cnpj || "",
          telephone: providerDatas?.phone || "",
        },
      };

      setDatasSender(payload);

      await axios.post(`${ip}/contract/subcontractor`, payload, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      toast.success("Subcontratado criado com sucesso!");
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
      } else {
        console.error("Erro:", err.message);
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
      <DialogContent className="max-w-[90vw] md:max-w-[45vw]">
        <div className="flex items-center justify-between bg-[#2E3C4D] px-5 py-4 h-[60px] min-w-full">
          <h2 className="text-white text-base font-semibold flex items-center gap-2">
            <UserPlus className="w-5 h-5 text-[#C0B15B]" />
            Cadastrar Novo Subcontratado
          </h2>
        </div>

        <div className="bg-[#F2F3F5] text-sm text-gray-800 p-2 px-4 rounded shadow mb-4">
          <strong>Filial:</strong>{" "}
          {selectedBranch?.name ?? "Nenhuma filial selecionada"}
        </div>

        <div>
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
                  <div className="bg-realizaBlue cursor-pointer rounded-lg p-2 text-black transition-all hover:bg-neutral-500">
                    <Oval height="24" width="24" color="#FFFFFF" ariaLabel="oval-loading" />
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
                <span className="text-red-600 text-sm">
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
                <span className="text-red-600 text-sm">
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
                <span className="text-red-600 text-sm">
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
                <span className="text-red-600 text-sm">
                  {errorsSubContract.phone.message}
                </span>
              )}
            </div>
            
            <div className="flex flex-col gap-1">
              <Label className="text-black">Referência do Contrato</Label>
              <Input
                type="text"
                value={parentContractInfo ? parentContractInfo.reference : "Carregando..."}
                disabled
                className="rounded-lg p-2 bg-gray-200 cursor-not-allowed border-gray-300"
              />
              <input type="hidden" {...registerSubContract("idContractSupplier")} />
              
              {errorsSubContract.idContractSupplier && (
                <span className="text-red-600 text-sm">
                  {errorsSubContract.idContractSupplier.message}
                </span>
              )}
            </div>

            <div className="flex justify-end">
              {isLoading ? (
                <Button disabled>
                  <Oval height="20" width="20" color="#fff" />
                </Button>
              ) : (
                <Button
                  className="bg-realizaBlue"
                  type="submit"
                  onClick={() => {
                    getActivities();
                    getServicesType();
                  }}
                >
                  Próximo
                </Button>
              )}
            </div>
          </form>

          <Dialog open={nextModal} onOpenChange={setNextModal}>
            <DialogContent className="max-w-[95vw] border-none md:max-w-[45vw]">
                <DialogHeader className="bg-[#1E2A38] px-6 py-4 rounded-t-md">
                   <DialogTitle className="text-white flex items-center gap-2 text-base font-semibold">
                      <svg xmlns="http://www.w3.org/2000/svg" fill="none" className="w-5 h-5 stroke-yellow-400" viewBox="0 0 24 24">
                         <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 6h16M4 12h16M4 18h7" />
                      </svg>
                      Faça o contrato
                   </DialogTitle>
                </DialogHeader>
                <div className="bg-[#F3F4F6] px-6 py-2">
                   <p className="text-sm text-gray-700 font-medium">
                      Filial: {selectedBranch?.name}
                   </p>
                </div>
                <ScrollArea className="h-[60vh] w-full px-6 py-4">
                   <div className="w-full flex flex-col gap-4">
                      <form
                         className="flex flex-col gap-4"
                         onSubmit={handleSubmitContract(createContract, (errors) => {
                            console.error("Validation errors for contract form:", errors);
                            toast.error("Por favor, preencha todos os campos obrigatórios do contrato.");
                         })}
                      >
                         <div>
                            <Label className="text-black">CNPJ do novo prestador</Label>
                            <Input
                               type="text"
                               {...registerContract("cnpj")}
                               defaultValue={providerDatas.cnpj}
                               readOnly
                               className="bg-gray-200 cursor-not-allowed"
                            />
                         </div>

                         <div className="flex flex-col gap-2">
                           <Label className="text-black">Gestor do serviço</Label>
                           <select
                              className="rounded-md border p-2"
                              {...registerContract("idResponsible")}
                              defaultValue=""
                           >
                              <option value="" disabled>Selecione um gestor</option>
                              {Array.isArray(managers) && managers.map((manager: any) => (
                                 <option value={manager.idUser} key={manager.idUser}>
                                    {manager.firstName} {manager.surname}
                                 </option>
                              ))}
                           </select>
                           {errorsContract.idResponsible && <span className="text-red-600 text-sm">{errorsContract.idResponsible.message}</span>}
                        </div>
                        
                         <div>
                            <Label className="text-black">Nome do Serviço</Label>
                            <Input {...registerContract("serviceName")} />
                            {errorsContract.serviceName && <span className="text-red-500 text-sm">{errorsContract.serviceName.message}</span>}
                         </div>

                         <div>
                            <Label className="text-black">Data de início efetivo</Label>
                            <Input type="date" {...registerContract("dateStart")} />
                            {errorsContract.dateStart && <span className="text-red-600 text-sm">{errorsContract.dateStart.message}</span>}
                         </div>

                         <div>
                            <Label className="text-black">Referência do contrato</Label>
                            <Input {...registerContract("contractReference")} />
                            {errorsContract.contractReference && <span className="text-red-500 text-sm">{errorsContract.contractReference.message}</span>}
                         </div>

                         <div className="flex flex-col gap-1">
                            <Label className="text-black">Tipo de despesa</Label>
                            <select {...registerContract("expenseType")} className="rounded-md border p-2" defaultValue="">
                               <option value="" disabled>Selecione uma opção</option>
                               <option value="CAPEX">CAPEX</option>
                               <option value="OPEX">OPEX</option>
                               <option value="NENHUM">Nenhuma</option>
                            </select>
                            {errorsContract.expenseType && <span className="text-red-500 text-sm">{errorsContract.expenseType.message}</span>}
                         </div>

                         <div className="flex flex-col gap-3">
                            <Label className="text-black">Tipo de Gestão</Label>
                            <div className="flex flex-col items-start gap-3">
                               <div className="flex items-center gap-2">
                                  <input type="checkbox" id="hse-checkbox" {...registerContract("hse", { onChange: (e) => setIsSsma(e.target.checked) })} />
                                  <Label htmlFor="hse-checkbox" className="text-[14px] text-black">SSMA</Label>
                               </div>
                               <div className="flex items-center gap-2">
                                  <input type="checkbox" id="labor-checkbox" {...registerContract("labor")} />
                                  <Label htmlFor="labor-checkbox" className="text-[14px] text-black">TRABALHISTA</Label>
                               </div>
                            </div>
                         </div>
                        
                         <div className="flex flex-col gap-1">
                            <Label className="text-black">Tipo do Serviço</Label>
                            <div className="border border-neutral-400 flex items-center gap-2 rounded-md px-2 py-1 bg-white shadow-sm">
                               <Search className="text-neutral-500 w-5 h-5" />
                               <input type="text" placeholder="Pesquisar serviço..." value={searchService} onChange={(e) => setSearchService(e.target.value)} className="border-none w-full outline-none text-sm placeholder:text-neutral-400" />
                            </div>
                            <select {...registerContract("idServiceType")} className="rounded-md border p-2 w-full mt-1" defaultValue="">
                               <option value="" disabled>Selecione uma opção</option>
                               {servicesType.filter((s: any) => s.title.toLowerCase().includes(searchService.toLowerCase())).map((service: any) => (
                                  <option key={service.idServiceType} value={service.idServiceType}>
                                     {service.title} - {formatarRisco(service.risk as RiscoNivel)}
                                  </option>
                               ))}
                            </select>
                            {errorsContract.idServiceType && <span className="text-red-500 text-sm">{errorsContract.idServiceType.message}</span>}
                         </div>

                         {isSsma && (
                            <div className="flex flex-col gap-2">
                               <Label className="text-black">Tipo de atividade</Label>
                               <div className="border border-neutral-400 flex items-center gap-2 rounded-md px-2 py-1 bg-white shadow-sm">
                                  <Search className="text-neutral-500 w-5 h-5" />
                                  <input type="text" placeholder="Pesquisar atividade..." value={searchActivity} onChange={(e) => setSearchActivity(e.target.value)} className="border-none w-full outline-none text-sm placeholder:text-neutral-400" />
                               </div>
                               <ScrollArea className="h-[20vh] p-2 rounded-lg bg-white shadow-inner">
                                  <div className="flex flex-col gap-2">
                                     {Array.isArray(activities) && activities.filter((activity: any) => activity.title.toLowerCase().includes(searchActivity.toLowerCase())).map((activity: any) => (
                                        <label key={activity.idActivity} className="flex items-center gap-2 text-black">
                                           <input type="checkbox" checked={selectedActivities.includes(activity.idActivity)} onChange={(e) => handleCheckboxChange(activity.idActivity, e.target.checked)} />
                                           {activity.title}
                                        </label>
                                     ))}
                                  </div>
                               </ScrollArea>
                            </div>
                         )}

                         <div className="flex flex-col gap-1">
                            <Label className="text-black">Descrição detalhada do serviço</Label>
                            <textarea {...registerContract("description")} className="rounded-md border p-2" />
                            {errorsContract.description && <span className="text-red-500 text-sm">{errorsContract.description.message}</span>}
                         </div>

                         <Button className="bg-realizaBlue mt-4" type="submit" disabled={isButtonDisabled}>
                           {isLoading ? <Oval height="20" width="20" color="#fff" /> : "Enviar contrato"}
                         </Button>
                      </form>
                   </div>
                </ScrollArea>
            </DialogContent>
          </Dialog>
        </div>
      </DialogContent>
    </Dialog>
  );
};