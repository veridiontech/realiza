import axios from "axios";
// import { fetchCompanyByCNPJ } from "@/hooks/gets/realiza/useCnpjApi"; // Removido devido ao erro
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
import { useForm } from "react-hook-form";
import { ip } from "@/utils/ip";
import { useState, useEffect } from "react";
import { toast } from "sonner";
import { ScrollArea } from "./ui/scroll-area";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { useUser } from "@/context/user-provider";

// ───────────────────────────────────────────────
// Schema do convite (primeiro modal)
const subcontractorInviteSchema = z.object({
  email: z.string().email("Insira um email válido"),
  cnpj: z.string().nonempty("Insira o CNPJ"),
});

// ───────────────────────────────────────────────
// Schema do contrato (segundo modal)
// Conforme o Swagger, o payload precisa conter:
// serviceType, serviceDuration, serviceName, contractReference, description,
// allocatedLimit, responsible, expenseType, startDate, endDate, supplierContractId,
// activities, requirements, branch, além de cnpj, providerSupplier, providerSubcontractor e subcontractPermission.
const contractFormSchema = z.object({
  cnpj: z.string().nonempty("CNPJ obrigatório"),
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
  responsible: z.string().nonempty("O responsável é obrigatório"),
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
  branch: z.string().nonempty("O branch é obrigatório"),
  activities: z
    .array(z.string())
    .min(1, "Pelo menos uma atividade é obrigatória"),
  requirements: z
    .array(z.string())
    .min(1, "Pelo menos um requisito é obrigatória"),
  // Este campo é opcional: se informado, o backend tentará buscá-lo; caso contrário, criará um novo subcontratado
  providerSubcontractor: z.string().optional(),
});

type SubcontractorInviteSchema = z.infer<typeof subcontractorInviteSchema>;
type ContractFormSchema = z.infer<typeof contractFormSchema>;

// ───────────────────────────────────────────────
// Componente principal
export default function SupplierAddQuartered() {
  // O provider fornece o ID do fornecedor (supplier)
  const { user } = useUser();
  const [isLoading, setIsLoading] = useState(false);
  const [nextModal, setNextModal] = useState(false);
  // Guarda os dados do convite para uso no contrato
  const [inviteData, setInviteData] =
    useState<SubcontractorInviteSchema | null>(null);
  // Estados para gestores, atividades e requisitos (usados no contrato)
  const [managers, setManagers] = useState<any[]>([]);
  const [activities, setActivities] = useState<any[]>([]);
  const [requirements, setRequirements] = useState<any[]>([]);
  const [selectedRadio, setSelectedRadio] = useState<string | null>(null);

  // ── Formulário do convite (primeiro modal)
  const {
    register: registerInvite,
    handleSubmit: handleSubmitInvite,
    formState: { errors: errorsInvite },
  } = useForm<SubcontractorInviteSchema>({
    resolver: zodResolver(subcontractorInviteSchema),
  });

  // ── Formulário do contrato (segundo modal)
  const {
    register: registerContract,
    handleSubmit: handleSubmitContract,
    formState: { errors: errorsContract },
  } = useForm<ContractFormSchema>({
    resolver: zodResolver(contractFormSchema),
  });

  // Função para buscar atividades e requisitos
  const getActivitiesAndRequirements = async () => {
    try {
      const activitieData = await axios.get(`${ip}/contract/activity`);
      const requirementData = await axios.get(`${ip}/contract/requirement`);
      setActivities(activitieData.data.content);
      setRequirements(requirementData.data.content);
    } catch (err) {
      console.log("Erro ao buscar atividades e requisitos", err);
    }
  };

  // Função para buscar gestores (no exemplo, filtramos pelo ID do fornecedor)
  const getManagers = async () => {
    try {
      const res = await axios.get(
        `${ip}/user/client/filtered-client?idSearch=${user?.supplier}`,
      );
      setManagers(res.data.content);
    } catch (err) {
      console.log("Erro ao buscar gestores", err);
    }
  };

  useEffect(() => {
    getActivitiesAndRequirements();
  }, []);

  // ── Envio do convite (primeiro modal)
  const onSubmitInvite = async (data: SubcontractorInviteSchema) => {
    setIsLoading(true);
    try {
      // Envia o convite por email
      await axios.post(`${ip}/invite`, {
        email: data.email,
        idCompany: user?.supplier, // ID do fornecedor
        company: "SUBCONTRACTOR", // identifica o tipo de convite
        cnpj: data.cnpj,
      });
      toast.success("Email de cadastro enviado para novo subcontratado");
      setInviteData(data);
      // Busca gestores para preencher o select no contrato (se necessário)
      await getManagers();
      setNextModal(true);
    } catch (error) {
      console.error("Erro ao enviar email para subcontratado:", error);
      toast.error("Erro ao enviar email. Tente novamente");
    } finally {
      setIsLoading(false);
    }
  };

  // ── Envio do contrato (segundo modal)
  const onSubmitContract = async (data: ContractFormSchema) => {
    if (!inviteData) {
      toast.error("Dados do convite não encontrados");
      return;
    }
    setIsLoading(true);
    try {
      // Monta o payload incluindo o CNPJ do convite, o fornecedor (provider) e define a flag de subcontratação
      const payload = {
        ...data,
        cnpj: inviteData.cnpj,
        supplier: user?.supplier, // ID do fornecedor
        subcontractPermission: true,
      };
      // Chama o endpoint de criação do contrato para subcontratados
      await axios.post(`${ip}/contract/subcontractor`, payload);
      toast.success("Contrato criado com sucesso");
    } catch (error) {
      console.error("Erro ao criar contrato:", error);
      toast.error("Erro ao criar contrato. Tente novamente");
    } finally {
      setIsLoading(false);
    }
  };

  // ── Lógica para os radio buttons (se necessário)
  const handleRadioClick = (value: string) => {
    setSelectedRadio(value);
  };

  // Se “Não” ou nenhum for selecionado, mostra o campo “Tipo do Serviço”
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
              {/* Campo de CNPJ (puxado do convite) */}
              <div>
                <Label className="text-white">CNPJ do subcontratado</Label>
                <Input
                  type="text"
                  value={inviteData?.cnpj || "Erro ao puxar CNPJ"}
                  readOnly
                />
                {errorsContract.cnpj && (
                  <span className="text-red-600">
                    {errorsContract.cnpj.message}
                  </span>
                )}
              </div>

              {/* Radio para definir se é subcontratação (opcional) */}
              <div className="flex flex-col gap-2">
                <Label className="text-white">É uma subcontratação?</Label>
                <div className="flex items-center gap-1">
                  <Label className="text-white" htmlFor="subcontratacao-sim">
                    Sim
                  </Label>
                  <input
                    type="radio"
                    id="subcontratacao-sim"
                    name="subcontratacao"
                    value="sim"
                    onClick={() => handleRadioClick("sim")}
                  />
                </div>
                <div className="flex items-center gap-1">
                  <Label className="text-white" htmlFor="subcontratacao-nao">
                    Não
                  </Label>
                  <input
                    type="radio"
                    id="subcontratacao-nao"
                    name="subcontratacao"
                    value="nao"
                    onClick={() => handleRadioClick("nao")}
                  />
                </div>
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
                      <option
                        key={manager.idUser}
                        value={`${manager.firstName} ${manager.surname}`}
                      >
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

              {/* Campo “Tipo do Serviço” – exibido conforme a seleção do radio */}
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
                  <option value="Nenhuma">Nenhuma</option>
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

              {/* Escopo/descrição do serviço */}
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

              {/* Datas de início e término */}
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

              {/* ID do Contrato do Fornecedor */}
              <div>
                <Label className="text-white">
                  ID do Contrato do Fornecedor
                </Label>
                <Input
                  {...registerContract("supplierContractId")}
                  placeholder="Insira o ID do contrato do fornecedor"
                />
                {errorsContract.supplierContractId && (
                  <span className="text-red-600">
                    {errorsContract.supplierContractId.message}
                  </span>
                )}
              </div>

              {/* Branch */}
              <div>
                <Label className="text-white">Branch</Label>
                <Input
                  {...registerContract("branch")}
                  placeholder="Insira o branch"
                />
                {errorsContract.branch && (
                  <span className="text-red-600">
                    {errorsContract.branch.message}
                  </span>
                )}
              </div>

              {/* Atividades */}
              <div className="flex flex-col gap-1">
                <Label className="text-white">Atividades</Label>
                <select
                  {...registerContract("activities")}
                  className="rounded-md border p-2"
                  defaultValue=""
                >
                  <option value="" disabled>
                    Selecione uma atividade
                  </option>
                  {activities.map((activity: any) => (
                    <option key={activity.idActivity} value={activity.title}>
                      {activity.title}
                    </option>
                  ))}
                </select>
                {errorsContract.activities && (
                  <span className="text-red-600">
                    {errorsContract.activities.message}
                  </span>
                )}
              </div>

              {/* Requisitos */}
              <div className="flex flex-col gap-1">
                <Label className="text-white">Requisitos</Label>
                <select
                  {...registerContract("requirements")}
                  className="rounded-md border p-2"
                  defaultValue=""
                >
                  <option value="" disabled>
                    Selecione um requisito
                  </option>
                  {requirements.map((requirement: any) => (
                    <option
                      key={requirement.idRequeriment}
                      value={requirement.title}
                    >
                      {requirement.title}
                    </option>
                  ))}
                </select>
                {errorsContract.requirements && (
                  <span className="text-red-600">
                    {errorsContract.requirements.message}
                  </span>
                )}
              </div>

              {/* Botão de envio */}
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
