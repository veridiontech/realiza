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
import { propsClient } from "@/types/interfaces";
import { toast } from "sonner";
import { ScrollArea } from "./ui/scroll-area";

const modalSendEmailFormSchema = z.object({
  email: z.string().email("Insira um email valido"),
  company: z.string().default("SUPPLIER"),
  idCompany: z.string().nonempty("Selecione um cliente"),
});

const contractFormSchema = z.object({
  serviceName: z.string().nonempty("O nome do serviço é obrigatório"),
  contractReference: z.string().nonempty("A referência do contrato é obrigatória"),
  allocatedLimit: z.string().regex(/^\d+$/, "O limite de alocados deve ser um número válido"),
  startDate: z
    .string()
    .nonempty("A data de início é obrigatória"),
  endDate: z
    .string()
    .nonempty("A data de término é obrigatória"),
    
  serviceType: z.string().nonempty("O tipo de serviço é obrigatório"),
  activities: z.string().nonempty("As atividades são obrigatórias"),
  requirements: z.string().nonempty("As exigências são obrigatórias"),
  units: z.string().nonempty("As unidades são obrigatórias"),
  manager: z.string().nonempty("O gestor é obrigatório"),
  providerSupplier: z.string().nonempty("O fornecedor responsável é obrigatório"),
  description: z
    .string()
    .min(10, "A descrição deve ter no mínimo 10 caracteres")
    .nonempty("A descrição é obrigatória"),
});

type ModalSendEmailFormSchema = z.infer<typeof modalSendEmailFormSchema>;
type ContractFormSchema = z.infer<typeof contractFormSchema>;
export function ModalTesteSendSupplier() {
  const [clients, setClients] = useState<propsClient[]>([]);
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

  useEffect(() => {
    getClients();
  }, []);

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="bg-sky-700">Cadastrar novo prestador</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Cadastrar novo prestador</DialogTitle>
        </DialogHeader>
        <div>
          <form
            action=""
            onSubmit={handleSubmit(createClient)}
            className="flex flex-col gap-4"
          >
            <div>
              <Label>Email</Label>
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
              <Label>Selecione um cliente</Label>
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
            <DialogContent className="max-w-[50vw]">
              <DialogHeader>
                <DialogTitle>Faça o contrato</DialogTitle>
              </DialogHeader>
              <ScrollArea className="h-[60vh] w-full px-5">
                <div className="p-4">
                  <form
                    action=""
                    className="flex flex-col gap-2"
                    onSubmit={handleSubmitContract(createContract)}
                  >
                    <div>
                      <Label>Nome do Serviço</Label>
                      <Input {...registerContract("serviceName")} />
                      {errorsContract.serviceName && (
                        <span className="text-red-500">
                          {errorsContract.serviceName.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label>Ref. do contrato</Label>
                      <Input {...registerContract("contractReference")} />
                      {errorsContract.contractReference && (
                        <span className="text-red-500">
                          {errorsContract.contractReference.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label>Limite de alocados</Label>
                      <Input {...registerContract("allocatedLimit")} />
                      {errorsContract.allocatedLimit && (
                        <span className="text-red-500">
                          {errorsContract.allocatedLimit.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label>Data de início</Label>
                      <Input type="date" {...registerContract("startDate")} />
                      {errorsContract.startDate && (
                        <span className="text-red-500">
                          {errorsContract.startDate.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label>Data de término</Label>
                      <Input type="date" {...registerContract("endDate")} />
                      {errorsContract.endDate && (
                        <span className="text-red-500">
                          {errorsContract.endDate.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label>Tipo do Serviço</Label>
                      <Input {...registerContract("serviceType")} />
                      {errorsContract.serviceType && (
                        <span className="text-red-500">
                          {errorsContract.serviceType.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label>Atividades</Label>
                      <Input {...registerContract("activities")} />
                      {errorsContract.activities && (
                        <span className="text-red-500">
                          {errorsContract.activities.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label>Outras Exigências</Label>
                      <Input {...registerContract("requirements")} />
                      {errorsContract.requirements && (
                        <span className="text-red-500">
                          {errorsContract.requirements.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label>Unidades</Label>
                      <Input {...registerContract("units")} />
                      {errorsContract.units && (
                        <span className="text-red-500">
                          {errorsContract.units.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label>Gestor</Label>
                      <Input {...registerContract("manager")} />
                      {errorsContract.manager && (
                        <span className="text-red-500">
                          {errorsContract.manager.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label>Fornecedor Responsável</Label>
                      <Input {...registerContract("providerSupplier")} />
                      {errorsContract.providerSupplier && (
                        <span className="text-red-500">
                          {errorsContract.providerSupplier.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label>Descrição da solicitação</Label>
                      <Input {...registerContract("description")} />
                      {errorsContract.description && (
                        <span className="text-red-500">
                          {errorsContract.description.message}
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
