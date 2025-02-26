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
import { propsBranch, propsClient } from "@/types/interfaces";

const contractFormSchema = z.object({
  serviceName: z.string().nonempty("O nome do serviço é obrigatório"),
  clientSelect: z.string().nonempty("Selecione um cliente"),
  id_branch: z.string().nonempty("Selecione uma filial"),
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

type ContractFormSchema = z.infer<typeof contractFormSchema>;

export function ModalAddContract() {
  const [clients, setClients] = useState<propsClient[]>([]);
  const [branches, setBranches] = useState<propsBranch[]>([]);
  const [activities, setActivities] = useState<any[]>([]);
  const [requirements, setRequirements] = useState<any[]>([]);
  const [managers, setManagers] = useState<any[]>([]);
  const [selectedRadio, setSelectedRadio] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
  } = useForm<ContractFormSchema>({
    resolver: zodResolver(contractFormSchema),
  });

  const getClients = async () => {
    try {
      const res = await axios.get(`${ip}/client`);
      setClients(res.data.content);
    } catch (err) {
      console.error("Erro ao buscar clientes", err);
    }
  };

  const getBranches = async (clientId: string) => {
    try {
      const res = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${clientId}`,
      );
      setBranches(res.data.content);
      console.log("Filiais do cliente:", res.data.content);
    } catch (err) {
      console.log(err);
    }
  };

  const handleClientChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    getBranches(e.target.value);
  };

  const getActivities = async () => {
    try {
      const activitieData = await axios.get(`${ip}/contract/activity`);
      const requirementData = await axios.get(`${ip}/contract/requirement`);
      setActivities(activitieData.data.content);
      setRequirements(requirementData.data.content);
    } catch (err) {
      console.error("Erro ao buscar atividades e requisitos", err);
    }
  };

  const getManagers = async (clientId: string) => {
    try {
      const res = await axios.get(
        `${ip}/user/client/filtered-client?idSearch=${clientId}`,
      );
      setManagers(res.data.content);
    } catch (err) {
      console.error("Erro ao buscar gestores", err);
    }
  };

  const createContract = async (data: ContractFormSchema) => {
    setIsLoading(true);
    try {
      console.log("Criando contrato:", data);
      await axios.post(`${ip}/contract/supplier`, data);
      toast.success("Contrato criado com sucesso!");
    } catch (err) {
      console.error("Erro ao criar contrato", err);
      toast.error("Erro ao criar contrato. Tente novamente.");
    } finally {
      setIsLoading(false);
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
        <Button className="bg-realizaBlue">Novo Contrato</Button>
      </DialogTrigger>
      <DialogContent
        style={{
          backgroundImage: `url(${bgModalRealiza})`,
        }}
        className="max-w-[45vw]"
      >
        <DialogHeader>
          <DialogTitle className="text-white">Criar Novo Contrato</DialogTitle>
        </DialogHeader>
        <ScrollArea className="h-[60vh] w-full px-5">
          <form
            className="flex flex-col gap-4"
            onSubmit={handleSubmit(createContract)}
          >
            <div>
              <Label className="text-white">Selecione um cliente</Label>
              <select
                className="w-full rounded-md border p-2"
                {...register("client")}
                defaultValue=""
                onChange={(e) => {
                  register("clientSelect").onChange(e);
                  handleClientChange(e);
                  getManagers(e.target.value);
                }}
              >
                <option value="" disabled>
                  Selecione um cliente
                </option>
                {clients.map((client) => (
                  <option key={client.idClient} value={client.idClient}>
                    {client.tradeName}
                  </option>
                ))}
              </select>
              {errors.client && (
                <span className="text-red-600">{errors.client.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">Filiais do cliente</Label>
              <select
                {...register("id_branch")}
                className="flex flex-col rounded-md border p-2"
              >
                <option value="">Selecione uma filial</option>
                {branches.map((branch) => (
                  <option key={branch.id_branch} value={branch.id_branch}>
                    {branch.name}
                  </option>
                ))}
              </select>
              {errors.id_branch && (
                <span className="text-red-600">{errors.id_branch.message}</span>
              )}
            </div>

            <div className="flex flex-col gap-2">
              <Label className="text-white">Permitido subcontratação?</Label>
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
                  className="text-white"
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
                  className="text-white"
                />
              </div>
            </div>

            <div>
              <Label className="text-white">Gestor do serviço</Label>
              <select
                className="w-full rounded-md border p-2"
                defaultValue=""
                {...register("responsible")}
              >
                <option value="" disabled>
                  Selecione um gestor
                </option>
                {managers.map((manager) => (
                  <option key={manager.idUser} value={manager.idUser}>
                    {manager.firstName} {manager.surname}
                  </option>
                ))}
              </select>
              {errors.responsible && (
                <span className="text-red-600">
                  {errors.responsible.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">Referência de serviço</Label>
              <Input {...register("serviceReference")} />
              {errors.serviceReference && (
                <span className="text-red-500">
                  {errors.serviceReference.message}
                </span>
              )}
            </div>

            <div>
              {shouldShowServiceType && (
                <div>
                  <Label className="text-white">Tipo do Serviço</Label>
                  <Input {...register("serviceType")} />
                  {errors.serviceType && (
                    <span className="text-red-500">
                      {errors.serviceType.message}
                    </span>
                  )}
                </div>
              )}
            </div>

            <div>
              <div>
                <Label className="text-white">Tipo de Despesa</Label>
                <select
                  {...register("serviceTypeExpense")}
                  className="w-full rounded-md border p-2"
                  defaultValue=""
                >
                  <option value="" disabled>
                    Selecione uma opção
                  </option>
                  <option>Capex</option>
                  <option>Opex</option>
                </select>
                {errors.serviceTypeExpense && (
                  <span className="text-red-500">
                    {errors.serviceTypeExpense.message}
                  </span>
                )}
              </div>
            </div>

            <div>
              <Label className="text-white">Nome do Serviço</Label>
              <Input {...register("serviceName")} />
              {errors.serviceName && (
                <span className="text-red-500">
                  {errors.serviceName.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">Escopo do Serviço</Label>
              <textarea
                {...register("description")}
                className="w-full rounded-md border p-2"
                rows={4}
              />
              {errors.description && (
                <span className="text-red-500">
                  {errors.description.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">
                Número Máximo de Empregados Alocados
              </Label>
              <Input {...register("allocatedLimit")} />
              {errors.allocatedLimit && (
                <span className="text-red-500">
                  {errors.allocatedLimit.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">Data de Início</Label>
              <Input type="date" {...register("startDate")} />
              {errors.startDate && (
                <span className="text-red-500">{errors.startDate.message}</span>
              )}
            </div>

            <div>
              <Label className="text-white">Data de Término</Label>
              <Input type="date" {...register("endDate")} />
              {errors.endDate && (
                <span className="text-red-500">{errors.endDate.message}</span>
              )}
            </div>

            <div>
              <Label className="text-white">
                Descrição detalhada do serviço
              </Label>
              <textarea
                {...register("description")}
                className="w-full rounded-md border p-2"
                rows={4}
              />
              {errors.description && (
                <span className="text-red-500">
                  {errors.description.message}
                </span>
              )}
            </div>

            {/* <div className="flex flex-col gap-1">
              <Label className="text-white">Atividades</Label>
              <select
                className="w-full rounded-md border p-2"
                multiple
                onChange={(e) => {
                  const selectedValues = Array.from(
                    e.target.selectedOptions,
                    (option) => option.value,
                  );
                  setValue("activities", selectedValues);
                }}
              >
                {activities.map((activity) => (
                  <option key={activity.idActivity} value={activity.title}>
                    {activity.title}
                  </option>
                ))}
              </select>
              {errors.activities && (
                <span className="text-red-500">
                  {errors.activities.message}
                </span>
              )}
            </div> */}
{/* 
            <div className="flex flex-col gap-1">
              <Label className="text-white">Requisitos</Label>
              <select
                className="w-full rounded-md border p-2"
                multiple
                onChange={(e) => {
                  const selectedValues = Array.from(
                    e.target.selectedOptions,
                    (option) => option.value,
                  );
                  setValue("requirements", selectedValues);
                }}
              >
                {requirements.map((requirement) => (
                  <option
                    key={requirement.idRequeriment}
                    value={requirement.title}
                  >
                    {requirement.title}
                  </option>
                ))}
              </select>
              {errors.requirements && (
                <span className="text-red-500">
                  {errors.requirements.message}
                </span>
              )}
            </div> */}

            <div className="flex justify-end">
              {isLoading ? (
                <Radio visible={true} height="80" width="80" />
              ) : (
                <Button className="bg-green-600" type="submit">
                  Criar Contrato
                </Button>
              )}
            </div>
          </form>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
