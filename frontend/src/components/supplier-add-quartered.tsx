import axios from "axios";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "./ui/button";
import { Label } from "./ui/label";
import { Input } from "./ui/input";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm, Controller } from "react-hook-form";
import { ip } from "@/utils/ip";
import { useState, useEffect } from "react";
import { toast } from "sonner";
import { ScrollArea } from "./ui/scroll-area";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { useUser } from "@/context/user-provider";

const cnpjRegex = /^\d{2}\.\d{3}\.\d{3}\/\d{4}\-\d{2}$/;

// Schema do convite (primeiro modal)
const subcontractorInviteSchema = z.object({
  email: z.string().email("Insira um email válido"),
  cnpj: z.string()
    .nonempty("Insira o CNPJ")
    .regex(cnpjRegex, "CNPJ inválido, use o formato XX.XXX.XXX/XXXX-XX"),
});

// Schema do contrato (segundo modal)
// Observe que:
// - o campo "cnpj" virá do convite e é preenchido automaticamente;
// - "responsible" é exigido e deve ser um id válido (não pode ser vazio);
// - "activities" e "requirements" são arrays.
const contractFormSchema = z.object({
  cnpj: z.string()
    .nonempty("CNPJ obrigatório")
    .regex(cnpjRegex, "CNPJ inválido, use o formato XX.XXX.XXX/XXXX-XX"),
  serviceType: z.string().nonempty("O tipo de serviço é obrigatório"),
  serviceDuration: z.string().nonempty("A duração do serviço é obrigatória"),
  serviceName: z.string().nonempty("O nome do serviço é obrigatório"),
  contractReference: z
    .string()
    .nonempty("A referência do contrato é obrigatória"),
  description: z.string().nonempty("A descrição detalhada é obrigatória"),
  allocatedLimit: z
    .string()
    .regex(/^\d+$/, "O limite de alocados deve ser um número válido"),
  responsible: z.string().nonempty("Selecione um gestor"),
  expenseType: z.string().nonempty("O tipo de despesa é obrigatório"),
  startDate: z
    .string()
    .refine(
      (val) => !isNaN(Date.parse(val)),
      "A data de início deve ser válida",
    ),
  endDate: z
    .string()
    .refine(
      (val) => !isNaN(Date.parse(val)),
      "A data de término deve ser válida",
    ),
  supplierContractId: z
    .string()
    .nonempty("O ID do contrato do fornecedor é obrigatório"),
  activities: z
    .array(z.string())
    .min(1, "Pelo menos uma atividade é obrigatória"),
  requirements: z
    .array(z.string())
    .min(1, "Pelo menos um requisito é obrigatória"),
});

type SubcontractorInviteSchema = z.infer<typeof subcontractorInviteSchema>;
type ContractFormSchema = z.infer<typeof contractFormSchema>;

export default function SupplierAddQuartered() {
  const { user } = useUser();
  const [isLoading, setIsLoading] = useState(false);
  const [nextModal, setNextModal] = useState(false);
  const [inviteData, setInviteData] =
    useState<SubcontractorInviteSchema | null>(null);
  const [managers, setManagers] = useState<any[]>([]);
  const [activities, setActivities] = useState<any[]>([]);
  const [requirements, setRequirements] = useState<any[]>([]);
  const [supplierContracts, setSupplierContracts] = useState<any[]>([]);
  const [selectedRadio] = useState<string | null>(null);

  // Formulário do convite
  const {
    register: registerInvite,
    handleSubmit: handleSubmitInvite,
    formState: { errors: errorsInvite },
  } = useForm<SubcontractorInviteSchema>({
    resolver: zodResolver(subcontractorInviteSchema),
  });

  // Formulário do contrato
  const {
    register: registerContract,
    handleSubmit: handleSubmitContract,
    setValue,
    control,
    formState: { errors: errorsContract },
  } = useForm<ContractFormSchema>({
    resolver: zodResolver(contractFormSchema),
    defaultValues: {
      cnpj: "",
      responsible: "",
      activities: [],
      requirements: [],
    },
  });

  // Quando os dados do convite chegam, preenche o campo "cnpj" do contrato
  useEffect(() => {
    if (inviteData) {
      setValue("cnpj", inviteData.cnpj);
    }
  }, [inviteData, setValue]);

  // Busca atividades e requisitos
  const getActivitiesAndRequirements = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const activitieData = await axios.get(`${ip}/contract/activity`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      }
      );
      const requirementData = await axios.get(`${ip}/contract/requirement`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      }
      );
      setActivities(activitieData.data.content);
      setRequirements(requirementData.data.content);
    } catch (err) {
      console.log("Erro ao buscar atividades e requisitos", err);
    }
  };

  // Busca gestores (filtrando pelo id do fornecedor)
  const getManagers = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/user/client/filtered-client?idSearch=${user?.supplier}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      }

      );
      setManagers(res.data.content);
    } catch (err) {
      console.log("Erro ao buscar gestores", err);
    }
  };

  // Busca contratos do fornecedor
  const getSupplierContracts = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/contract/supplier/filtered-supplier?idSearch=${user?.supplier}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      }

      );
      setSupplierContracts(res.data.content);
    } catch (err) {
      console.error("Erro ao buscar contratos do fornecedor", err);
    }
  };

  useEffect(() => {
    getActivitiesAndRequirements();
  }, []);

  useEffect(() => {
    if (user?.supplier) {
      getSupplierContracts();
    }
  }, [user]);

  // Envio do convite
  const onSubmitInvite = async (data: SubcontractorInviteSchema) => {
    setIsLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.post(`${ip}/invite`, {
        email: data.email,
        idCompany: user?.supplier,
        company: "SUBCONTRACTOR",
        cnpj: data.cnpj,
      }, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      }
      );
      toast.success("Email de cadastro enviado para novo subcontratado");
      setInviteData(data);
      await getManagers();
      setNextModal(true);
    } catch (error) {
      console.error("Erro ao enviar email para subcontratado:", error);
      toast.error("Erro ao enviar email. Tente novamente");
    } finally {
      setIsLoading(false);
    }
  };

  // Envio do contrato
  const onSubmitContract = async (data: ContractFormSchema) => {
    if (!inviteData) {
      toast.error("Dados do convite não encontrados");
      return;
    }
    setIsLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const payload = {
        ...data,
        // Força o valor de cnpj a vir do convite
        cnpj: inviteData.cnpj,
        subcontractPermission: true,
        // Se não houver um subcontratado pré-selecionado, enviamos null para disparar a criação
        providerSubcontractor: null,
        providerSupplier: user?.supplier,
        branch: user?.branch,
        startDate: new Date(data.startDate).toISOString(),
        endDate: new Date(data.endDate).toISOString(),
      };
      await axios.post(`${ip}/contract/subcontractor`, payload, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      });
      toast.success("Contrato criado com sucesso");
    } catch (error) {
      console.error("Erro ao criar contrato:", error);
      toast.error("Erro ao criar contrato. Tente novamente");
    } finally {
      setIsLoading(false);
    }
  };

  const shouldShowServiceType =
    selectedRadio === null || selectedRadio === "nao";

  return (
    <>
      {/* Primeiro Modal: Envio do Convite */}
      <Dialog>
        <DialogTrigger asChild>
          <Button className="bg-sky-700">Cadastrar novo subcontratado</Button>
        </DialogTrigger>
        <DialogContent
          style={{ backgroundImage: `url(${bgModalRealiza})` }}
          className="max-w-[45vw]"
        >
          <DialogHeader>
            <DialogTitle className="text-white">
              Enviar convite para subcontratado
            </DialogTitle>
          </DialogHeader>
          <form
            onSubmit={handleSubmitInvite(onSubmitInvite)}
            className="flex flex-col gap-4 p-4"
          >
            <div>
              <Label className="text-white">Email</Label>
              <Input
                type="email"
                placeholder="Digite o email do subcontratado"
                {...registerInvite("email")}
                className="w-full"
              />
              {errorsInvite.email && (
                <span className="text-red-600">
                  {errorsInvite.email.message}
                </span>
              )}
            </div>
            <div>
              <Label className="text-white">CNPJ</Label>
              <Input
                type="text"
                placeholder="Insira o CNPJ"
                {...registerInvite("cnpj")}
              />
              {errorsInvite.cnpj && (
                <span className="text-red-600">
                  {errorsInvite.cnpj.message}
                </span>
              )}
            </div>
            <div className="flex justify-end">
              {isLoading ? (
                <Button disabled>Carregando...</Button>
              ) : (
                <Button className="bg-realizaBlue" type="submit">
                  Enviar
                </Button>
              )}
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {/* Segundo Modal: Cadastro do Contrato */}
      <Dialog open={nextModal} onOpenChange={setNextModal}>
        <DialogContent
          className="max-w-[45vw] border-none"
          style={{ backgroundImage: `url(${bgModalRealiza})` }}
        >
          <DialogHeader>
            <DialogTitle className="text-white">Faça o contrato</DialogTitle>
          </DialogHeader>
          <ScrollArea className="h-[60vh] w-full px-5">
            <form
              onSubmit={handleSubmitContract(onSubmitContract)}
              className="flex flex-col gap-4 p-4"
            >
              {/* CNPJ (apenas leitura, vindo do convite) */}
              <div>
                <Label className="text-white">CNPJ do subcontratado</Label>
                <Input
                  type="text"
                  readOnly
                  {...registerContract("cnpj")}
                  value={inviteData?.cnpj || "Erro ao puxar CNPJ"}
                />
                {errorsContract.cnpj && (
                  <span className="text-red-600">
                    {errorsContract.cnpj.message}
                  </span>
                )}
              </div>

              {/* Seleção do gestor */}
              <div className="flex flex-col gap-2">
                <Label className="text-white">Gestor do serviço</Label>
                <select
                  className="rounded-md border p-2"
                  defaultValue=""
                  {...registerContract("responsible")}
                >
                  <option value="" disabled>
                    Selecione um gestor
                  </option>
                  {Array.isArray(managers) &&
                    managers.map((manager: any) => (
                      <option key={manager.idUser} value={manager.idUser}>
                        {manager.firstName} {manager.surname}
                      </option>
                    ))}
                </select>
                {errorsContract.responsible && (
                  <span className="text-red-600">
                    {errorsContract.responsible.message}
                  </span>
                )}
              </div>

              {/* Referência do serviço */}
              <div>
                <Label className="text-white">Referência de serviço</Label>
                <Input {...registerContract("contractReference")} />
                {errorsContract.contractReference && (
                  <span className="text-red-600">
                    {errorsContract.contractReference.message}
                  </span>
                )}
              </div>

              {/* Tipo do Serviço (condicional) */}
              {shouldShowServiceType && (
                <div>
                  <Label className="text-white">Tipo do Serviço</Label>
                  <Input {...registerContract("serviceType")} />
                  {errorsContract.serviceType && (
                    <span className="text-red-600">
                      {errorsContract.serviceType.message}
                    </span>
                  )}
                </div>
              )}

              {/* Duração do Serviço */}
              <div>
                <Label className="text-white">Duração do Serviço</Label>
                <Input {...registerContract("serviceDuration")} />
                {errorsContract.serviceDuration && (
                  <span className="text-red-600">
                    {errorsContract.serviceDuration.message}
                  </span>
                )}
              </div>

              {/* Tipo de despesa */}
              <div className="flex flex-col gap-1">
                <Label className="text-white">Tipo de despesa</Label>
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
                  <span className="text-red-600">
                    {errorsContract.expenseType.message}
                  </span>
                )}
              </div>

              {/* Nome do Serviço */}
              <div>
                <Label className="text-white">Nome do Serviço</Label>
                <Input {...registerContract("serviceName")} />
                {errorsContract.serviceName && (
                  <span className="text-red-600">
                    {errorsContract.serviceName.message}
                  </span>
                )}
              </div>

              {/* Escopo/Descrição do Serviço */}
              <div className="flex flex-col gap-1">
                <Label className="text-white">Escopo do serviço</Label>
                <textarea
                  {...registerContract("description")}
                  className="rounded-md border p-2"
                />
                {errorsContract.description && (
                  <span className="text-red-600">
                    {errorsContract.description.message}
                  </span>
                )}
              </div>

              {/* Número máximo de empregados alocados */}
              <div>
                <Label className="text-white">
                  Número máximo de empregados alocados
                </Label>
                <Input {...registerContract("allocatedLimit")} />
                {errorsContract.allocatedLimit && (
                  <span className="text-red-600">
                    {errorsContract.allocatedLimit.message}
                  </span>
                )}
              </div>

              {/* Datas de Início e Término */}
              <div>
                <Label className="text-white">Data de início</Label>
                <Input type="date" {...registerContract("startDate")} />
                {errorsContract.startDate && (
                  <span className="text-red-600">
                    {errorsContract.startDate.message}
                  </span>
                )}
              </div>
              <div>
                <Label className="text-white">Data de término</Label>
                <Input type="date" {...registerContract("endDate")} />
                {errorsContract.endDate && (
                  <span className="text-red-600">
                    {errorsContract.endDate.message}
                  </span>
                )}
              </div>

              {/* Contrato do Fornecedor */}
              <div>
                <Label className="text-white">Contrato do Fornecedor</Label>
                <select
                  {...registerContract("supplierContractId")}
                  className="rounded-md border p-2"
                  defaultValue=""
                >
                  <option value="" disabled>
                    Selecione um contrato
                  </option>
                  {supplierContracts.map((contract: any) => (
                    <option
                      key={contract.idContract}
                      value={contract.idContract}
                    >
                      {contract.contractReference} - {contract.serviceName}
                    </option>
                  ))}
                </select>
                {errorsContract.supplierContractId && (
                  <span className="text-red-600">
                    {errorsContract.supplierContractId.message}
                  </span>
                )}
              </div>

              {/* Atividades - múltipla seleção */}
              <div className="flex flex-col gap-1">
                <Label className="text-white">Atividades</Label>
                <Controller
                  name="activities"
                  control={control}
                  render={({ field }) => (
                    <select
                      multiple
                      className="rounded-md border p-2"
                      value={field.value}
                      onChange={(e) => {
                        const selected = Array.from(
                          e.target.selectedOptions,
                          (option) => option.value,
                        );
                        field.onChange(selected);
                      }}
                    >
                      {activities.map((activity: any) => (
                        <option
                          key={activity.idActivity}
                          value={activity.idActivity}
                        >
                          {activity.title}
                        </option>
                      ))}
                    </select>
                  )}
                />
                {errorsContract.activities && (
                  <span className="text-red-600">
                    {errorsContract.activities.message}
                  </span>
                )}
              </div>

              {/* Requisitos - múltipla seleção */}
              <div className="flex flex-col gap-1">
                <Label className="text-white">Requisitos</Label>
                <Controller
                  name="requirements"
                  control={control}
                  render={({ field }) => (
                    <select
                      multiple
                      className="rounded-md border p-2"
                      value={field.value}
                      onChange={(e) => {
                        const selected = Array.from(
                          e.target.selectedOptions,
                          (option) => option.value,
                        );
                        field.onChange(selected);
                      }}
                    >
                      {requirements.map((requirement: any) => (
                        <option
                          key={requirement.idRequeriment}
                          value={requirement.idRequeriment}
                        >
                          {requirement.title}
                        </option>
                      ))}
                    </select>
                  )}
                />
                {errorsContract.requirements && (
                  <span className="text-red-600">
                    {errorsContract.requirements.message}
                  </span>
                )}
              </div>

              <div className="flex justify-end">
                {isLoading ? (
                  <Button disabled>Carregando...</Button>
                ) : (
                  <Button className="bg-realizaBlue" type="submit">
                    Criar contrato
                  </Button>
                )}
              </div>
            </form>
          </ScrollArea>
        </DialogContent>
      </Dialog>
    </>
  );
}
