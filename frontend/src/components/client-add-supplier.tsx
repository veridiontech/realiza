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
// import { useUser } from "@/context/user-provider";
// import { useClient } from "@/context/Client-Provider";
import { useBranch } from "@/context/Branch-provider";
// import { fetchCompanyByCNPJ } from "@/hooks/gets/realiza/useCnpjApi";

// Torna o campo idClient opcional, pois vamos atribuí-lo via código
const modalSendEmailFormSchema = z.object({
  email: z.string().email("Insira um email válido"),
  phone: z.string(),
  cnpj: z.string().nonempty("Insira o cnpj"),
});

const modalSendEmailFormSchemaSubContractor = z.object({
  email: z.string().email("Insira um email válido"),
  phone: z.string(),
  cnpj: z.string().nonempty("Insira o cnpj"),
  providerSubcontractor: z.string().nonempty("Selecionar um fornecedor é obrigatório")
});

const contractFormSchema = z.object({
  cnpj: z.string().nonempty("Cnpj obrigatório"),
  serviceName: z.string().nonempty("O nome do serviço é obrigatório"),
  serviceReference: z
    .string()
    .nonempty("A referência do contrato é obrigatória"),
  serviceType: z.string().optional(),
  activities: z.string().min(1, "Pelo menos uma atividade é obrigatória"),
  description: z.string().optional(),
  expenseType: z.string().nonempty("O tipo de serviço é obrigatório"),
});

type ModalSendEmailFormSchema = z.infer<typeof modalSendEmailFormSchema>;

type ModalSendEmailFormSchemaSubContractor = z.infer<
  typeof modalSendEmailFormSchemaSubContractor
>;
type ContractFormSchema = z.infer<typeof contractFormSchema>;

export function ModalTesteSendSupplier() {
  const [managers, setManagers] = useState<any>([]);
  const [activities, setActivities] = useState<any>([]);
  // const [setRequirements] = useState<any>([]);
  // const [selectedRadio, setSelectedRadio] = useState<string | null>(null);
  const [pushCnpj, setPushCnpj] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [nextModal, setNextModal] = useState(false);
  const [providerDatas, setProviderDatas] = useState({});
  // const { client } = useClient();
  const { selectedBranch } = useBranch();
  const [isSubcontractor, setIsSubContractor] = useState<string | null>(null);
  const [suppliers, setSuppliers] = useState<any>([]);
  // const [subContractDatas, setSubContractDatas] = useState({});

  const {
    register,
    handleSubmit,
    formState: { errors },
    // setValue,
    // getValues,
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

  const {
    register: registerSubContract,
    handleSubmit: handleSubmitSubContract,
    formState: { errors: errorsSubContract },
  } = useForm<ModalSendEmailFormSchemaSubContractor>({
    resolver: zodResolver(modalSendEmailFormSchemaSubContractor),
  });


  
  const getSupplier = async () => {
    if (!selectedBranch?.idBranch) return;
    try {
      const res = await axios.get(
        `${ip}/supplier/filtered-client?idSearch=${selectedBranch.idBranch}`,
      );
      console.log("Dados do supplier:", res.data.content);
      setSuppliers(res.data.content);
    } catch (err) {
      console.log("Erro ao buscar prestadores de serviço", err);
    }
  };

  useEffect(() => {
    if(selectedBranch?.idBranch) {
      getSupplier()
      setSuppliers([])
    }
  }, [selectedBranch]);

  const getActivities = async () => {
    try {
      const activitieData = await axios.get(`${ip}/contract/activity`);
      // const requirementData = await axios.get(`${ip}/contract/requirement`);
      setActivities(activitieData.data.content);
      console.log("atividades log teste:", activitieData.data.content);

      // setRequirements(requirementData.data.content);
    } catch (err) {
      console.log(err);
    }
  };

  const createClient = async (data: ModalSendEmailFormSchema) => {
    setIsLoading(true);
    try {
      const payload = {
        ...data,

      }
      console.log("Dados enviados para modal de contrato:", payload);
      setProviderDatas(payload);
      setPushCnpj(data.cnpj);
      toast.success("Email de cadastro enviado para novo prestador");
      setNextModal(true);
    } catch (err) {
      console.log("Erro ao enviar email para usuário:", err);
      toast.error("Erro ao enviar email. Tente novamente");
    } finally {
      setIsLoading(false);
    }
  };

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
    if (selectedBranch?.idBranch) {
      getManager();
    }
    getActivities();
  }, []);

  const createContract = async (data: ContractFormSchema) => {
    if (!providerDatas) {
      toast.error("Dados do prestador não encontrados. Reinicie o processo.");
      return;
    }

    setIsLoading(true);
    try {
      const payload = {
        ...data,
        providerDatas,
        activity: ["teste", "teste23"],
      };
      console.log("enviando dados do contrato", payload);
      await axios.post(`${ip}/contract/supplier`, payload);

      toast.success("Contrato criado com sucesso!");
    } catch (err) {
      console.error("Erro ao criar contrato:", err);
      toast.error(
        "Erro ao criar contrato. Verifique os dados e tente novamente.",
      );
    } finally {
      setIsLoading(false);
    }
  };

  // const handleRadioClick = (value: string) => {
  //   setSelectedRadio(value);
  // };

  // const shouldShowServiceType =
  //   selectedRadio === null || selectedRadio === "nao";

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
          {selectedBranch ? (
            <span className="text-white">
              <strong>Filial:</strong> {selectedBranch?.name}
            </span>
          ) : (
            <span className="text-white">Nenhuma filial selecionada</span>
          )}
        </div>
        <div>
          <div className="flex flex-col gap-2">
            <Label className="text-white">É uma subcontratação?</Label>
            <div className="flex items-center gap-2">
              <label className="flex items-center gap-1 text-white">
                <input
                  type="radio"
                  value="sim"
                  checked={isSubcontractor === "contratado"}
                  onChange={() => setIsSubContractor("contratado")}
                />
                Contratado direto
              </label>
              <label className="flex items-center gap-1 text-white">
                <input
                  type="radio"
                  value="nao"
                  checked={isSubcontractor === "subcontratado"}
                  onChange={() => setIsSubContractor("subcontratado")}
                />
                Subcontratado
              </label>
            </div>
          </div>
          {isSubcontractor === "contratado" && (
            <form
              onSubmit={handleSubmit(createClient)}
              className="flex flex-col gap-4"
            >
              <div>
                <Label className="text-white">CNPJ</Label>
                <Input
                  type="text"
                  placeholder="Insira o cnpj do prestador..."
                  {...register("cnpj")}
                />
                {errors.cnpj && (
                  <span className="text-red-600">{errors.cnpj.message}</span>
                )}
              </div>
              <div className="mb-1">
                <Label className="text-white">Email</Label>
                <Input
                  type="email"
                  placeholder="Digite o email do novo prestador"
                  {...register("email")}
                  className="w-full"
                />
                {errors.email && (
                  <span className="text-red-600">{errors.email.message}</span>
                )}
              </div>
              <div>
                <Label className="text-white">Telefone</Label>
                <Input placeholder="Digite o telefone" {...register("phone")} />
                {errors.phone && (
                  <span className="text-red-600">{errors.phone.message}</span>
                )}
              </div>
              <div className="flex justify-end">
                <Button className="bg-realizaBlue">Próximo</Button>
              </div>
            </form>
          )}
          {isSubcontractor === "subcontratado" && (
            <form
              onSubmit={handleSubmitSubContract(createClient)}
              className="flex flex-col gap-4"
            >
              <div>
                <Label className="text-white">CNPJ</Label>
                <Input
                  type="text"
                  placeholder="Insira o cnpj do prestador..."
                  {...registerSubContract("cnpj")}
                />
                {errorsSubContract.cnpj && (
                  <span className="text-red-600">{errorsSubContract.cnpj.message}</span>
                )}
              </div>
              <div className="mb-1">
                <Label className="text-white">Email</Label>
                <Input
                  type="email"
                  placeholder="Digite o email do novo prestador"
                  {...registerSubContract("email")}
                  className="w-full"
                />
                {errorsSubContract.email && (
                  <span className="text-red-600">{errorsSubContract.email.message}</span>
                )}
              </div>
              <div>
                <Label className="text-white">Telefone</Label>
                <Input placeholder="Digite o telefone" {...registerSubContract("phone")} />
                {errorsSubContract.phone && (
                  <span className="text-red-600">{errorsSubContract.phone.message}</span>
                )}
              </div>
              <div className="flex flex-col gap-1">
                <Label className="text-white">Selecione um fornecedor</Label>
                <select defaultValue={""} className="rounded-lg p-2" {...registerSubContract("providerSubcontractor")}>
                  <option value="" disabled>Selecione uma opção</option>
                  {suppliers.map((supplier: any) => (
                    <option value={supplier.idProvider} key={supplier.idProvider}>{supplier.tradeName}  {supplier.cnpj}</option>
                  ))}
                </select>
                {errorsSubContract.providerSubcontractor && (
                  <span className="text-red-600">{errorsSubContract.providerSubcontractor.message}</span>
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
                    Próximo
                  </Button>
                )}
              </div>
            </form>
          )}
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
              <div>
                    {selectedBranch ? (
                      <span className="text-white">
                        <strong>Filial:</strong> {selectedBranch?.name}
                      </span>
                    ) : (
                      <span className="text-white">
                        Nenhuma filial selecionada
                      </span>
                    )}
                  </div>
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
                          {...registerContract("cnpj")}
                          value={pushCnpj || "erro ao puxar cnpj"}
                        />
                        {errorsContract.cnpj && (
                          <span className="text-red-600">
                            {errorsContract.cnpj.message}
                          </span>
                        )}
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
                          managers.map((manager: any) => (
                            <option value={manager.iduser} key={manager.idUser}>
                              {manager.firstName} {manager.surname}
                            </option>
                          ))}
                      </select>
                    </div>
                    <div>
                      <Label className="text-white">
                        Referência do contrato
                      </Label>
                      <Input {...registerContract("serviceReference")} />
                      {errorsContract.serviceReference && (
                        <span className="text-red-500">
                          {errorsContract.serviceReference.message}
                        </span>
                      )}
                    </div>
                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Tipo de despesa</Label>
                      <select
                        {...registerContract("serviceType")}
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
                      {errorsContract.serviceType && (
                        <span className="text-red-500">
                          {errorsContract.serviceType.message}
                        </span>
                      )}
                    </div>

                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Tipo do Serviço</Label>
                      <select
                        {...registerContract("expenseType")}
                        className="rounded-md p-1"
                      >
                        <option value="TRABALHISTA">TRABALHISTA</option>
                        <option value="AMBAS">AMBAS</option>
                        <option value="SSMA">SSMA</option>
                        <option value="TODAS">TODAS</option>
                      </select>
                      {errorsContract.expenseType && (
                        <span className="text-red-500">
                          {errorsContract.expenseType.message}
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
                      <Label className="text-white">Tipo de atividade</Label>
                      <select
                        {...registerContract("activities")}
                        className="rounded-md border p-2"
                        defaultValue=""
                      >
                        <option value="" disabled>
                          Selecione uma atividade
                        </option>
                        {activities.map((activitie: any) => (
                          <option
                            value={activitie.idActivity}
                            key={activitie.idActivity}
                          >
                            {activitie.title}{" "}
                          </option>
                        ))}
                      </select>
                      {errorsContract.description && (
                        <span className="text-red-500">
                          {errorsContract.description.message}
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
