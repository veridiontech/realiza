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
import axios from "axios";
import { ip } from "@/utils/ip";
import { useEffect, useState } from "react";
import { Radio } from "react-loader-spinner";
import { toast } from "sonner";
import { ScrollArea } from "./ui/scroll-area";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { useUser } from "@/context/user-provider";

// Atualizamos o schema removendo o campo "idCompany"
const modalSendEmailFormSchema = z.object({
  email: z.string().email("Insira um email válido"),
  company: z.string().default("SUPPLIER"),
  cnpj: z.string().nonempty("Insira o cnpj"),
});

const contractFormSchema = z.object({
  cnpj: z.string().nonempty("Cnpj obrigatório"),
  serviceName: z.string().nonempty("O nome do serviço é obrigatório"),
  serviceReference: z
    .string()
    .nonempty("A referência do contrato é obrigatória"),
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
  serviceType: z.string().nonempty("O tipo de serviço é obrigatório"),
  activities: z
    .array(z.string())
    .min(1, "Pelo menos uma atividade é obrigatória"),
  requirements: z
    .array(z.string())
    .min(1, "Pelo menos um requisito é obrigatório"),
  description: z.string().nonempty("A descrição detalhada é obrigatória"),
  serviceTypeExpense: z.string().nonempty("O tipo de despesa é obrigatório"),
  allocatedLimit: z
    .string()
    .regex(/^\d+$/, "O limite de alocados deve ser um número válido"),
  client: z.string().nonempty("O cliente é obrigatório"),
  responsible: z.string().nonempty("O responsável é obrigatório"),
});

type ModalSendEmailFormSchema = z.infer<typeof modalSendEmailFormSchema>;
type ContractFormSchema = z.infer<typeof contractFormSchema>;

export function ModalTesteSendSupplier() {
  // Como não utilizaremos a lista de clientes, removemos o estado "clients"
  const [managers, setManagers] = useState<any>([]);
  const [activities, setActivities] = useState<any>([]);
  const [requirements, setRequirements] = useState<any>([]);
  const [selectedRadio, setSelectedRadio] = useState<string | null>(null);
  const [pushCnpj, setPushCnpj] = useState<string | null>(null);
  const branch = useUser();

  const [isLoading, setIsLoading] = useState(false);
  const [nextModal, setNextModal] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ModalSendEmailFormSchema>({
    resolver: zodResolver(modalSendEmailFormSchema),
  });

  const {
    register: registerContract,
    handleSubmit: handleSubmitContract,
    formState: { errors: errorsContract },
  } = useForm<ContractFormSchema>({
    resolver: zodResolver(contractFormSchema),
  });

  const getActivities = async () => {
    try {
      const activitieData = await axios.get(`${ip}/contract/activity`);
      const requirementData = await axios.get(`${ip}/contract/requirement`);
      setActivities(activitieData.data.content);
      setRequirements(requirementData.data.content);
    } catch (err) {
      console.log(err);
    }
  };

  const createClient = async (data: ModalSendEmailFormSchema) => {
    console.log("enviando dados:", data);
    setIsLoading(true);
    try {
      await axios.post(`${ip}/invite`, {
        email: data.email,
        idCompany: branch,
        company: data.company,
        cnpj: data.cnpj,
      });
      setPushCnpj(data.cnpj);
      toast.success("Email de cadastro enviado para novo prestador");
      setNextModal(true);
      try {
        console.log("teste");

        const res = await axios.get(
          `${ip}/user/client/filtered-client?idSearch=${data.idCompany}`,
        );
        setManagers(res.data.content);
      } catch (err) {
        console.log("erro ao buscar gestores", err);
      }
    } catch (err) {
      console.log("erro ao enviar email para usuário", err);
      toast.error("Erro ao enviar email. Tente novamente");
    } finally {
      setIsLoading(false);
    }
  };

  const createContract = async (data: ContractFormSchema) => {
    const payload = {
      ...data,
      cnpj: "", // conforme sua lógica original
    };
    try {
      console.log("Criando contrato:", data);
      await axios.post(`${ip}/contract/supplier`, payload);
      console.log("sucesso");
    } catch (err) {
      console.log("erro ao criar contrato", err);
    }
  };

  const handleRadioClick = (value: string) => {
    setSelectedRadio(value);
  };

  const shouldShowServiceType =
    selectedRadio === null || selectedRadio === "nao";

  useEffect(() => {
    getActivities();
  }, []);

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="bg-sky-700">Cadastrar novo prestador</Button>
      </DialogTrigger>
      <DialogContent
        style={{
          backgroundImage: `url(${bgModalRealiza})`,
        }}
        className="max-w-[45vw]"
      >
        <DialogHeader>
          <DialogTitle className="text-white">
            Cadastrar novo prestador
          </DialogTitle>
        </DialogHeader>
        <div>
          <form
            onSubmit={handleSubmit(createClient)}
            className="flex flex-col gap-4"
          >
            <div>
              <Label className="text-white">Email</Label>
              <Input
                type="email"
                placeholder="Digite o email do novo cliente"
                {...register("email")}
                className="w-full"
              />
              {errors.email && (
                <span className="text-red-600">{errors.email.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">Cnpj</Label>
              <Input
                type="text"
                placeholder="Insira o cnpj do cliente..."
                {...register("cnpj")}
              />
              {errors.cnpj && (
                <span className="text-red-600">{errors.cnpj.message}</span>
              )}
            </div>
            {/* Removido o select de cliente */}
            <div className="flex justify-end">
              {isLoading ? (
                <Button>
                  <Radio
                    visible={true}
                    height="80"
                    width="80"
                    ariaLabel="radio-loading"
                    wrapperStyle={{}}
                    wrapperClass=""
                  />
                </Button>
              ) : (
                <Button className="bg-realizaBlue" type="submit">
                  Enviar
                </Button>
              )}
            </div>
          </form>
          <Dialog open={nextModal} onOpenChange={setNextModal}>
            <DialogContent
              className="max-w-[45vw] border-none"
              style={{
                backgroundImage: `url(${bgModalRealiza})`,
              }}
            >
              <DialogHeader>
                <DialogTitle className="text-white">
                  Faça o contrato
                </DialogTitle>
              </DialogHeader>
              <ScrollArea className="h-[60vh] w-full px-5">
                <div className="p-4">
                  <form
                    className="flex flex-col gap-2"
                    onSubmit={handleSubmitContract(createContract)}
                  >
                    <div>
                      <Label className="text-white">
                        CNPJ do novo prestador
                      </Label>
                      <div>
                        <Input
                          type="text"
                          value={pushCnpj || "erro ao puxar cnpj"}
                        />
                        {errorsContract.cnpj && (
                          <span>{errorsContract.cnpj?.message}</span>
                        )}
                      </div>
                    </div>
                    <div className="flex flex-col gap-2">
                      <Label className="text-white">
                        É uma subcontratação?
                      </Label>
                      <div className="flex items-center gap-1">
                        <Label
                          className="text-white"
                          htmlFor="subcontratacao-sim"
                        >
                          Sim
                        </Label>
                        <input
                          type="radio"
                          id="subcontratacao-sim"
                          name="subcontratacao"
                          value="sim"
                          onClick={() => handleRadioClick("sim")}
                          className="text-white"
                        />
                      </div>
                      <div className="flex items-center gap-1">
                        <Label
                          className="text-white"
                          htmlFor="subcontratacao-nao"
                        >
                          Não
                        </Label>
                        <input
                          type="radio"
                          id="subcontratacao-nao"
                          name="subcontratacao"
                          value="nao"
                          onClick={() => handleRadioClick("nao")}
                          className="text-white"
                        />
                      </div>
                    </div>
                    <div className="flex flex-col gap-2">
                      <Label className="text-white">Gestor do serviço</Label>
                      <select
                        key={managers.idUser}
                        className="rounded-md border p-2"
                        defaultValue=""
                      >
                        <option value="" disabled>
                          Selecione um gestor
                        </option>
                        {Array.isArray(managers) &&
                          managers.map((manager) => (
                            <option key={manager.idUser}>
                              {manager.firstName} {manager.surname}
                            </option>
                          ))}
                      </select>
                    </div>
                    <div>
                      <Label className="text-white">
                        Referência de serviço
                      </Label>
                      <Input {...registerContract("serviceReference")} />
                      {errorsContract.serviceReference && (
                        <span className="text-red-500">
                          {errorsContract.serviceReference.message}
                        </span>
                      )}
                    </div>
                    {shouldShowServiceType && (
                      <div>
                        <Label className="text-white">Tipo do Serviço</Label>
                        <Input {...registerContract("serviceType")} />
                        {errorsContract.serviceType && (
                          <span className="text-red-500">
                            {errorsContract.serviceType.message}
                          </span>
                        )}
                      </div>
                    )}
                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Tipo de despesa</Label>
                      <select
                        {...registerContract("serviceTypeExpense")}
                        className="rounded-md border p-2"
                        defaultValue=""
                      >
                        <option value="" disabled>
                          Selecione uma opção
                        </option>
                        <option>CAPEX</option>
                        <option>OPEX</option>
                        <option value="">Nenhuma</option>
                      </select>
                      {errorsContract.serviceTypeExpense && (
                        <span className="text-red-500">
                          {errorsContract.serviceTypeExpense.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">Nome do Serviço</Label>
                      <Input {...registerContract("serviceName")} />
                      {errorsContract.serviceName && (
                        <span className="text-red-500">
                          {errorsContract.serviceName.message}
                        </span>
                      )}
                    </div>
                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Escopo do serviço</Label>
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
                    <div>
                      <Label className="text-white">
                        Número máximo de empregados alocados
                      </Label>
                      <Input {...registerContract("allocatedLimit")} />
                      {errorsContract.allocatedLimit && (
                        <span className="text-red-500">
                          {errorsContract.allocatedLimit.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">Data de início</Label>
                      <Input type="date" {...registerContract("startDate")} />
                      {errorsContract.startDate && (
                        <span className="text-red-500">
                          {errorsContract.startDate.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">Data de término</Label>
                      <Input type="date" {...registerContract("endDate")} />
                      {errorsContract.endDate && (
                        <span className="text-red-500">
                          {errorsContract.endDate.message}
                        </span>
                      )}
                    </div>
                    <div className="flex flex-col gap-1">
                      <Label className="text-white">
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
                    {shouldShowServiceType && (
                      <div className="flex flex-col gap-1">
                        <Label className="text-white">Atividades</Label>
                        <select
                          {...registerContract("activities")}
                          key={activities.idActivity}
                          className="rounded-md border p-2"
                          defaultValue=""
                        >
                          <option value="" disabled>
                            Selecione aqui
                          </option>
                          {activities.map((activity: any) => (
                            <option
                              key={activity.idActivity}
                              value={activity.title}
                            >
                              {activity.title}
                            </option>
                          ))}
                        </select>
                        {errorsContract.activities && (
                          <span className="text-red-500">
                            {errorsContract.activities.message}
                          </span>
                        )}
                      </div>
                    )}
                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Requisitos</Label>
                      <select
                        {...registerContract("requirements")}
                        key={requirements.idRequeriment}
                        defaultValue=""
                        className="rounded-md border p-2"
                      >
                        <option value="" disabled>
                          Selecione aqui
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
                        <span className="text-red-500">
                          {errorsContract.requirements.message}
                        </span>
                      )}
                    </div>
                    <Button className="bg-realizaBlue" type="submit">
                      Criar contrato
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
}
