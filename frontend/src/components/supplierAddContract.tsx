import React, { useEffect, useState } from "react";
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
import { ScrollArea } from "./ui/scroll-area";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import axios from "axios";
import { ip } from "@/utils/ip";
import { Radio } from "react-loader-spinner";
import { toast } from "sonner";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { useUser } from "@/context/user-provider";

// Esquema do formulário com os campos exigidos pela API (conforme Swagger)
const contractFormSchema = z.object({
  contractReference: z
    .string()
    .nonempty("A referência do contrato é obrigatória"),
  serviceType: z.string().nonempty("O tipo de serviço é obrigatório"),
  serviceDuration: z.string().nonempty("A duração do serviço é obrigatória"),
  serviceName: z.string().nonempty("O nome do serviço é obrigatório"),
  description: z.string().nonempty("A descrição detalhada é obrigatória"),
  allocatedLimit: z
    .string()
    .regex(/^\d+$/, "O limite alocado deve ser um número válido"),
  responsible: z.string().nonempty("O responsável é obrigatório"),
  expenseType: z.string().nonempty("O tipo de despesa é obrigatório"),
  startDate: z
    .string()
    .refine((val) => !isNaN(Date.parse(val)), "Data de início inválida"),
  endDate: z
    .string()
    .refine((val) => !isNaN(Date.parse(val)), "Data de término inválida"),
  supplierContractId: z
    .string()
    .nonempty("O id do contrato do fornecedor é obrigatório"),
  activities: z
    .array(z.string())
    .min(1, "Pelo menos uma atividade é obrigatória"),
  requirements: z
    .array(z.string())
    .min(1, "Pelo menos um requisito é obrigatório"),
  cnpj: z.string().nonempty("O CNPJ é obrigatório"),
  // Esses campos serão preenchidos a partir do contexto do usuário ou do formulário
  providerSubcontractor: z.string().optional(),
  branch: z.string().nonempty("A filial é obrigatória"),
});

type ContractFormSchema = z.infer<typeof contractFormSchema>;

export function SupplierAddContract() {
  const { user } = useUser();
  const [clients, setClients] = useState<any[]>([]);
  const [activities, setActivities] = useState<any[]>([]);
  const [requirements, setRequirements] = useState<any[]>([]);
  const [managers, setManagers] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
  } = useForm<ContractFormSchema>({
    resolver: zodResolver(contractFormSchema),
  });

  // Exemplo de carregamento de clientes (se necessário)
  const getClients = async () => {
    try {
      const res = await axios.get(`${ip}/client`);
      setClients(res.data.content);
    } catch (err) {
      console.error("Erro ao buscar clientes", err);
    }
  };

  // Carrega atividades e requisitos
  const getActivitiesAndRequirements = async () => {
    try {
      const [activitieData, requirementData] = await Promise.all([
        axios.get(`${ip}/contract/activity`),
        axios.get(`${ip}/contract/requirement`),
      ]);
      setActivities(activitieData.data.content);
      setRequirements(requirementData.data.content);
    } catch (err) {
      console.error("Erro ao buscar atividades e requisitos", err);
    }
  };

  // Exemplo para carregar gestores a partir do cliente selecionado
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
      // Mescla os dados do formulário com os dados do contexto do usuário
      // providerSupplier e branch são obtidos do usuário supplier atual
      const payload = {
        ...data,
        subcontractPermission: true, // sempre true para contrato de subcontratado
        providerSupplier: user?.idUser, // obtido do contexto
      };

      console.log("Criando contrato de subcontratado:", payload);
      // Envia para o endpoint de subcontratados
      await axios.post(`${ip}/contract/subcontractor`, payload);
      toast.success("Contrato criado com sucesso!");
    } catch (err) {
      console.error("Erro ao criar contrato", err);
      toast.error("Erro ao criar contrato. Tente novamente.");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    getClients();
    getActivitiesAndRequirements();
    // Se necessário, carregue gestores a partir de um cliente selecionado
    // getManagers("algumIdDeCliente");
  }, []);

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="bg-green-600">Novo Contrato</Button>
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
            {/* Campo: Referência do contrato */}
            <div>
              <Label className="text-white">Referência do Contrato</Label>
              <Input {...register("contractReference")} />
              {errors.contractReference && (
                <span className="text-red-500">
                  {errors.contractReference.message}
                </span>
              )}
            </div>

            {/* Campo: Tipo do serviço */}
            <div>
              <Label className="text-white">Tipo do Serviço</Label>
              <Input {...register("serviceType")} />
              {errors.serviceType && (
                <span className="text-red-500">
                  {errors.serviceType.message}
                </span>
              )}
            </div>

            {/* Campo: Duração do serviço */}
            <div>
              <Label className="text-white">Duração do Serviço</Label>
              <Input {...register("serviceDuration")} />
              {errors.serviceDuration && (
                <span className="text-red-500">
                  {errors.serviceDuration.message}
                </span>
              )}
            </div>

            {/* Campo: Nome do serviço */}
            <div>
              <Label className="text-white">Nome do Serviço</Label>
              <Input {...register("serviceName")} />
              {errors.serviceName && (
                <span className="text-red-500">
                  {errors.serviceName.message}
                </span>
              )}
            </div>

            {/* Campo: Descrição do serviço */}
            <div>
              <Label className="text-white">Descrição Detalhada</Label>
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

            {/* Campo: Limite alocado */}
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

            {/* Campo: Responsável (gestor) */}
            <div>
              <Label className="text-white">Gestor do Serviço</Label>
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
                <span className="text-red-500">
                  {errors.responsible.message}
                </span>
              )}
            </div>

            {/* Campo: Tipo de despesa */}
            <div>
              <Label className="text-white">Tipo de Despesa</Label>
              <select
                {...register("expenseType")}
                className="w-full rounded-md border p-2"
                defaultValue=""
              >
                <option value="" disabled>
                  Selecione uma opção
                </option>
                <option value="CAPEX">Capex</option>
                <option value="OPEX">Opex</option>
              </select>
              {errors.expenseType && (
                <span className="text-red-500">
                  {errors.expenseType.message}
                </span>
              )}
            </div>

            {/* Campo: Data de início */}
            <div>
              <Label className="text-white">Data de Início</Label>
              <Input type="date" {...register("startDate")} />
              {errors.startDate && (
                <span className="text-red-500">{errors.startDate.message}</span>
              )}
            </div>

            {/* Campo: Data de término */}
            <div>
              <Label className="text-white">Data de Término</Label>
              <Input type="date" {...register("endDate")} />
              {errors.endDate && (
                <span className="text-red-500">{errors.endDate.message}</span>
              )}
            </div>

            {/* Campo: Atividades */}
            <div className="flex flex-col gap-1">
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
            </div>

            {/* Campo: Requisitos */}
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
            </div>

            {/* Campo: CNPJ */}
            <div>
              <Label className="text-white">CNPJ</Label>
              <Input {...register("cnpj")} />
              {errors.cnpj && (
                <span className="text-red-500">{errors.cnpj.message}</span>
              )}
            </div>

            {/* Campo: Filial */}
            <div>
              <Label className="text-white">Filial</Label>
              <Input {...register("branch")} />
              {errors.branch && (
                <span className="text-red-500">{errors.branch.message}</span>
              )}
            </div>

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
