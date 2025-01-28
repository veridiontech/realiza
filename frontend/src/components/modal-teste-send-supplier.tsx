import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "./ui/button";
import { Label  } from "./ui/label";
import { Input } from "./ui/input";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useEffect, useState } from "react";
import { Radio } from "react-loader-spinner";
import { propsClient } from "@/types/interfaces";
import { toast } from "sonner";
import { ScrollArea } from "./ui/scroll-area";
import bgModalRealiza from "@/assets/modalBG.jpeg";

const modalSendEmailFormSchema = z.object({
  email: z.string().email("Insira um email valido"),
  company: z.string().default("SUPPLIER"),
  idCompany: z.string().nonempty("Selecione um cliente"),
});

const contractFormSchema = z.object({
  serviceName: z.string().nonempty("O nome do serviço é obrigatório"),
  serviceReference: z
    .string()
    .nonempty("A referência do contrato é obrigatória"),
  // serviceDuration: z.string().nonempty("A duração do serviço é obrigatória"),
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
  const [clients, setClients] = useState<propsClient[]>([]);
  const [managers, setManagers] = useState<any>([]);
  const [activities, setActivities] = useState<any>([]);
  const [requirements, setRequirements] = useState<any>([]);
  const [selectedRadio, setSelectedRadio] = useState<string | null>(null);

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

  const getClients = async () => {
    try {
      const res = await axios.get(`${ip}/client`);
      setClients(res.data.content);
    } catch (err) {
      console.log("erro ao puxar clientes", err);
    }
  };

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
        idCompany: data.idCompany,
        company: data.company,
      });
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
      console.log("erro ao enviar email para usuario", err);
      toast.error("Erro ao enviar email. Tente novamente");
    } finally {
      setIsLoading(false);
    }
  };

  const createContract = async (data: ContractFormSchema) => {
    try {
      console.log("Criando contrato:", data);
      await axios.post(`${ip}/contract/supplier`);
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
    getClients();
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
            action=""
            onSubmit={handleSubmit(createClient)}
            className="flex flex-col gap-4"
          >
            <div>
              <Label  className="text-white">Email</Label >
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
            <div className="flex flex-col gap-1">
              <Label  className="text-white">Selecione um cliente</Label >
              <select
                className="rounded-md border p-2"
                defaultValue=""
                {...register("idCompany")}
              >
                <option value="" disabled>
                  Selecione um cliente
                </option>
                {clients.map((client) => (
                  <option key={client.idClient} value={client.idClient}>
                    {client.companyName}
                  </option>
                ))}
              </select>
              {errors.idCompany && (
                <span className="text-red-600">{errors.idCompany.message}</span>
              )}
            </div>
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
                <DialogTitle className="text-white">Faça o contrato</DialogTitle>
              </DialogHeader>
              <ScrollArea className="h-[60vh] w-full px-5">
                <div className="p-4">
                  <form
                    action=""
                    className="flex flex-col gap-2"
                    onSubmit={handleSubmitContract(createContract)}
                  >
                    <div className="flex flex-col gap-2">
                      <Label  className="text-white">É uma subcontratação?</Label >
                      <div className="flex items-center gap-1">
                        <Label  className="text-white" htmlFor="subcontratacao-sim">Sim</Label >
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
                        <Label  className="text-white" htmlFor="subcontratacao-nao">Não</Label >
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
                      <Label  className="text-white">Gestor do serviço</Label >
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
                            <option>
                              {manager.firstName} {manager.surname}
                            </option>
                          ))}
                      </select>
                    </div>
                    <div>
                      <Label  className="text-white">Referência de serviço</Label >
                      <Input {...registerContract("serviceReference")} />
                      {errorsContract.serviceReference && (
                        <span className="text-red-500">
                          {errorsContract.serviceReference.message}
                        </span>
                      )}
                    </div>

                    {shouldShowServiceType && (
                      <div>
                        <Label  className="text-white">Tipo do Serviço</Label >
                        <Input {...registerContract("serviceType")} />
                        {errorsContract.serviceType && (
                          <span className="text-red-500">
                            {errorsContract.serviceType.message}
                          </span>
                        )}
                      </div>
                    )}
                    <div className="flex flex-col gap-1">
                      <Label  className="text-white">Tipo de despesa</Label >
                      <select
                        {...registerContract("serviceTypeExpense")}
                        className="rounded-md border p-2"
                        defaultValue=""
                      >
                        <option value="" disabled>
                          Selecione uma opção
                        </option>
                        <option>Capex</option>
                        <option>Opex</option>
                        <option value="">Nenhuma</option>
                      </select>
                      {errorsContract.serviceTypeExpense && (
                        <span className="text-red-500">
                          {errorsContract.serviceTypeExpense.message}
                        </span>
                      )}
                    </div>
                    {/* <div>
                      <Label >Duração do serviço</Label >
                      <Input {...registerContract("serviceDuration")} />
                      {errorsContract.serviceDuration && (
                        <span className="text-red-500">
                          {errorsContract.serviceDuration.message}
                        </span>
                      )}
                    </div> */}
                    <div>
                      <Label className="text-white">Nome do Serviço</Label >
                      <Input {...registerContract("serviceName")} />
                      {errorsContract.serviceName && (
                        <span className="text-red-500">
                          {errorsContract.serviceName.message}
                        </span>
                      )}
                    </div>
                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Escopo do serviço</Label >
                      <textarea {...registerContract("description")} className="border rounded-md p-2 "/>
                      {errorsContract.description && (
                        <span className="text-red-500">
                          {errorsContract.description.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">Número máximo de empregados alocados</Label >
                      <Input {...registerContract("allocatedLimit")} />
                      {errorsContract.allocatedLimit && (
                        <span className="text-red-500">
                          {errorsContract.allocatedLimit.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">Data de início</Label >
                      <Input type="date" {...registerContract("startDate")} />
                      {errorsContract.startDate && (
                        <span className="text-red-500">
                          {errorsContract.startDate.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">Data de término</Label >
                      <Input type="date" {...registerContract("endDate")} />
                      {errorsContract.endDate && (
                        <span className="text-red-500">
                          {errorsContract.endDate.message}
                        </span>
                      )}
                    </div>

                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Descrição detalhada do serviço</Label >
                      <textarea {...registerContract("description")}  className="border rounded-md p-2"/>
                      {errorsContract.description && (
                        <span className="text-red-500">
                          {errorsContract.description.message}
                        </span>
                      )}
                    </div>
                    {shouldShowServiceType && (
                      <div className="flex flex-col gap-1">
                        <Label className="text-white">Atividades</Label >
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
                            <option value="">{activity.title}</option>
                          ))}
                          <option value=""></option>
                        </select>
                        {errorsContract.activities && (
                          <span className="text-red-500">
                            {errorsContract.activities.message}
                          </span>
                        )}
                      </div>
                    )}

                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Requisitos</Label >
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
                          <option value="">{requirement.title}</option>
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
