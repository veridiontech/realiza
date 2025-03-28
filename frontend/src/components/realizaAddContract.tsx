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
import { toast } from "sonner";
import { ScrollArea } from "./ui/scroll-area";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { useBranch } from "@/context/Branch-provider";
import { Oval } from "react-loader-spinner";

const contractFormSchema = z.object({
  serviceName: z.string().nonempty("Nome do serviço é obrigatório"),
  serviceType: z.string().nonempty("Tipo de despesa é obrigatório"),
  description: z.string().optional(),
  expenseType: z.string().nonempty("Tipo do serviço"),
  dateStart: z.string().nonempty("Início efetivo é obrigatório"),
  idResponsible: z.string().nonempty("Selecione um gestor"), //*
  contractReference: z
    .string()
    .nonempty("Referência do contrato é obrigatório"),
  idActivity: z.string().nonempty("Selecione uma atividade"), //*
});

type ContractFormSchema = z.infer<typeof contractFormSchema>;

export function ModalAddContract() {
  const [isLoading, setIsLoading] = useState(false)
  const { selectedBranch } = useBranch();
  const [managers, setManagers] = useState<any[]>([]);
  const [activities, setActivities] = useState([])
  // const [selectManager, setSelectManager] = useState<string | null>(null);
  // const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    // setValue,
    formState: { errors },
  } = useForm<ContractFormSchema>({
    resolver: zodResolver(contractFormSchema),
  });

  const getActivities = async () => {
    try {
      const activitieData = await axios.get(`${ip}/contract/activity`);
      // const requirementData = await axios.get(`${ip}/contract/requirement`);
      setActivities(activitieData.data.content);
      // setRequirements(requirementData.data.content);
    } catch (err) {
      console.error("Erro ao buscar atividades e requisitos", err);
    }
  };

  const createContract = async (data: ContractFormSchema) => {
    setIsLoading(true);
    // console.log("manager selecionado:", selectManager);

    const payload = {
      ...data,
      branch: selectedBranch?.idBranch,
    };
    try {
      console.log("Criando contrato:", payload);
      await axios.post(`${ip}/contract/supplier`, payload);
      toast.success("Contrato criado com sucesso!");
    } catch (err) {
      console.error("Erro ao criar contrato", err);
      toast.error("Erro ao criar contrato. Tente novamente.");
    } finally {
      setIsLoading(false);
    }
  };

  // const getSupplier = async() => {
  //   try{
  //     const res = await axios.get(`${ip}/`)
  //   }catch(err) {
  //     console.log(err);
      
  //   }
  //  }

  const getManager = async () => {
    try {
      const res = await axios.get(
        `${ip}/user/client/filtered-client?idSearch=${selectedBranch?.idBranch}`,
      );
      console.log("gestores:", res.data.content);
      setManagers(res.data.content);
    } catch (err) {
      console.log(
        `erro ao buscar gestores da filia: ${selectedBranch?.name}`,
        err,
      );
    }
  };

  useEffect(() => {
    getActivities();
    if (selectedBranch?.idBranch) {
      getManager();
    }
  }, [selectedBranch?.idBranch]);

  useEffect(() => {
    console.log("Erros no formulário:", errors);
  }, [errors]);

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
            onSubmit={handleSubmit((data) => {
              console.log("handleSubmit foi chamado com os dados:", data);
              createContract(data);
            })}
          >
            <div className="flex flex-col gap-2">
              <Label className="text-white">Filial selecionada</Label>
              <span className="text-white">
                {selectedBranch ? (
                  <li>{selectedBranch.name}</li>
                ) : (
                  <li>Nenhuma filial selecionada</li>
                )}
              </span>
            </div>
            <div>
              <Label className="text-white">Gestor do serviço</Label>
              <select
                className="w-full rounded-md border p-2"
                {...register("idResponsible")}
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
            </div>

            <div>
              <Label className="text-white">Data de Início</Label>
              <Input type="date" {...register("dateStart")} />
              {errors.dateStart && (
                <span className="text-red-500">{errors.dateStart.message}</span>
              )}
            </div>

            <div>
              <Label className="text-white">Referência do contrato</Label>
              <Input {...register("contractReference")} />
              {errors.contractReference && (
                <span className="text-red-500">
                  {errors.contractReference.message}
                </span>
              )}
            </div>

            <div>
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
                  <option>CAPEX</option>
                  <option>OPEX</option>
                </select>
                {errors.expenseType && (
                  <span className="text-red-500">
                    {errors.expenseType.message}
                  </span>
                )}
              </div>
            </div>

            <div>
              <div>
                <Label className="text-white">Tipo de Gestão</Label>
                <select
                  {...register("serviceType")}
                  className="w-full rounded-md border p-2"
                  defaultValue=""
                >
                  <option value="" disabled>
                    Selecione uma opção
                  </option>
                  <option value="Trabalhista">Trabalhista</option>
                  <option value="SSMA">SSMA</option>
                  <option value="Todas">Todas</option>
                </select>
                {errors.serviceType && (
                  <span className="text-red-500">
                    {errors.serviceType.message}
                  </span>
                )}
              </div>
            </div>
            {/* <div>
              <Label className="text-white">Tipo de atividade</Label>
              <Input {...register("activities")} />
              {errors.activities && (
                <span className="text-red-500">
                  {errors.activities.message}
                </span>
              )}
            </div> */}
            <div>
              <Label className="text-white">Nome do Serviço</Label>
              <Input {...register("serviceName")} />
              {errors.serviceName && (
                <span className="text-red-500">
                  {errors.serviceName.message}
                </span>
              )}
            </div>
            {/* 
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
            </div> */}

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

            <div className="flex flex-col gap-1">
              <Label className="text-white">Atividades</Label>
              <select
                {...register("idActivity")}
                className="w-full rounded-md border p-2"
                
              >
                {activities.map((activity: any) => (
                  <option key={activity.idActivity} value={activity.idActivity}>
                    {activity.title}
                  </option>
                ))}
              </select>
              {errors.idActivity && (
                <span className="text-red-500">
                  {errors.idActivity.message}
                </span>
              )}
            </div>

            {/* <div className="flex flex-col gap-1">
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
            </div>  */}

            <div className="flex justify-end">
              {isLoading ? ( <Button className="bg-green-600" type="submit">
                <Oval
  visible={true}
  height="80"
  width="80"
  color="#4fa94d"
  ariaLabel="oval-loading"
  wrapperStyle={{}}
  wrapperClass=""
  />
              </Button>):( <Button className="bg-green-600" type="submit">
                Criar Contrato
              </Button>)}
             
            </div>
          </form>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
