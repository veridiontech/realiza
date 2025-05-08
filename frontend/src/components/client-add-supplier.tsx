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
import { Oval, Radio } from "react-loader-spinner";
import { toast } from "sonner";
import { ScrollArea } from "./ui/scroll-area";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { useBranch } from "@/context/Branch-provider";
import { Search } from "lucide-react";
import { fetchCompanyByCNPJ } from "@/hooks/gets/realiza/useCnpjApi";
import { useUser } from "@/context/user-provider";
import { useDataSendEmailContext } from "@/context/dataSendEmail-Provider";
import { useSupplier } from "@/context/Supplier-context";

export const modalSendEmailFormSchema = z.object({
  email: z.string().email("Insira um email válido"),
  phone: z.string(),
  cnpj: z.string().nonempty("Insira o cnpj"),
  corporateName: z.string().nonempty("Insira o nome da empresa"),
});

export const modalSendEmailFormSchemaSubContractor = z.object({
  email: z.string().email("Insira um email válido"),
  phone: z.string(),
  cnpj: z.string().nonempty("Insira o cnpj"),
  corporateName: z.string().nonempty("Insira o nome da empresa"),
  providerSubcontractor: z
    .string()
    .nonempty("Selecionar um fornecedor é obrigatório"),
});

export const contractFormSchema = z.object({
  cnpj: z.string(),
  serviceName: z.string().nonempty("Nome do serviço é obrigatório"),
  idServiceType: z.string().nonempty("Tipo de despesa é obrigatório"),
  description: z.string().optional(),
  expenseType: z.string().nonempty("Tipo do serviço"),
  labor: z.boolean(),
  hse: z.boolean(),
  dateStart: z.string().nonempty("Início efetivo é obrigatório"),
  idResponsible: z.string().nonempty("Selecione um gestor"),
  contractReference: z
    .string()
    .nonempty("Referência do contrato é obrigatório"),
});

type ModalSendEmailFormSchema = z.infer<typeof modalSendEmailFormSchema>;

type ModalSendEmailFormSchemaSubContractor = z.infer<
  typeof modalSendEmailFormSchemaSubContractor
>;
type ContractFormSchema = z.infer<typeof contractFormSchema>;

export function ModalTesteSendSupplier() {
  const [managers, setManagers] = useState<any>([]);
  const [activities, setActivities] = useState<any>([]);
  const [pushCnpj, setPushCnpj] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [nextModal, setNextModal] = useState(false);
  const [providerDatas, setProviderDatas] = useState({});
  const { user } = useUser();
  const { selectedBranch } = useBranch();
  const [isSubcontractor, setIsSubContractor] = useState<string | null>(null);
  const [suppliers, setSuppliers] = useState<any>([]);
  const [getIdManager, setGetIdManager] = useState<string | null>(null);
  const { datasSender, setDatasSender } = useDataSendEmailContext();
  const { supplier } = useSupplier();
  const [contracts, setContracts] = useState([]);
  const [isSsma, setIsSsma] = useState(false);
  const [selectedActivities, setSelectedActivities] = useState<string[]>([]);
  const [servicesType, setServicesType] = useState([]);

  const handleCheckboxChange = (activityId: string, isChecked: boolean) => {
    if (isChecked) {
      setSelectedActivities((prev) => [...prev, activityId]);
    } else {
      setSelectedActivities((prev) => prev.filter((id) => id !== activityId));
    }
  };

  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    getValues,
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
    setValue: setValueSub,
    getValues: getValuesSub,
  } = useForm<ModalSendEmailFormSchemaSubContractor>({
    resolver: zodResolver(modalSendEmailFormSchemaSubContractor),
  });

  const handleCNPJSearch = async () => {
    const cnpjValue = getValues("cnpj");
    console.log("CNPJ no form de contratado:", cnpjValue);
    setIsLoading(true);
    try {
      const companyData = await fetchCompanyByCNPJ(cnpjValue);
      setValue("corporateName", companyData.razaoSocial || "");
    } catch (error) {
      console.log("erro ao buscar cnpj", error);

      toast.error("Erro ao buscar dados do CNPJ");
    } finally {
      setIsLoading(false);
    }
  };

  const getContracts = async () => {
    try {
      const res = await axios.get(
        `${ip}/contract/supplier/filtered-supplier?idSearch=${supplier?.idProvider}`,
      );
      console.log("contratos:", res.data.content);

      setContracts(res.data.content);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    if (supplier?.idProvider) {
      getContracts();
    }
  }, [supplier?.idProvider]);

  const handleCNPJSearchSub = async () => {
    const cnpjValue = getValuesSub("cnpj");
    console.log("CNPJ no form de subcontratado:", cnpjValue);
    setIsLoading(true);
    try {
      const companyData = await fetchCompanyByCNPJ(cnpjValue);
      setValueSub("corporateName", companyData.razaoSocial || "");
    } catch (error) {
      toast.error("Erro ao buscar dados do CNPJ");
    } finally {
      setIsLoading(false);
    }
  };

  const getSupplier = async () => {
    if (!selectedBranch?.idBranch) return;
    try {
      const res = await axios.get(
        `${ip}/supplier/filtered-client?idSearch=${selectedBranch.idBranch}`,
      );
      setSuppliers(res.data.content);
    } catch (err) {
      console.log("Erro ao buscar prestadores de serviço", err);
    }
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getSupplier();
      setSuppliers([]);
    }
  }, [selectedBranch]);

  const getActivities = async () => {
    try {
      const activitieData = await axios.get(`${ip}/contract/activity`, {
        params: {
          size: 1000
        }
      });
      setActivities(activitieData.data.content);
    } catch (err) {
      console.log(err);
    }
  };

  const createClient = async (data: ModalSendEmailFormSchema) => {
    setIsLoading(true);
    try {
      let payload;
      if (isSubcontractor === "contratado") {
        payload = {
          ...data,
        };
        console.log(
          "Dados enviados de contratado para modal de contrato:",
          payload,
        );
      } else {
        payload = {
          ...data,
        };
        console.log(
          "Enviando dados de subcontratado para o modal de contrato:",
          payload,
        );
      }
      setProviderDatas(payload);
      setPushCnpj(data.cnpj);
      toast.success("Prestador preenchido com sucesso");
      setNextModal(true);
    } catch (err) {
      console.log("Erro ao criar prestador", err);
      toast.error("Erro ao criar prestador. Tente novamente");
    } finally {
      setIsLoading(false);
    }
  };

  const getManager = async () => {
    try {
      const res = await axios.get(
        `${ip}/user/client/filtered-client?idSearch=${selectedBranch?.idBranch}`,
      );
      console.log(selectedBranch);
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
  }, [selectedBranch?.idBranch]);

  useEffect(() => {
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
        idRequester: user?.idUser,
        providerDatas,
        idBranch: selectedBranch?.idBranch,
        idActivities: selectedActivities,
      };
      console.log("enviando dados do contrato", payload);
      setDatasSender(payload);
      console.log("dados recebidos:", datasSender);

      await axios.post(`${ip}/contract/supplier`, payload);
      toast.success("Contrato criado com sucesso!");
    } catch (err: any) {
      if (err.response) {
        console.error("Erro no servidor:", err.response.data);
        toast.error(`Erro ao criar contrato: Erro desconhecido.`);
      } else if (err.request) {
        console.error("Erro na requisição:", err.request);
        toast.error("Erro na requisição ao servidor.");
      } else {
        console.error("Erro ao configurar requisição:", err.message);
        toast.error("Erro ao criar contrato. Tente novamente.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  const getServicesType = async () => {
    try {
      const res = await axios.get(`${ip}/contract/service-type`);
      setServicesType(res.data);
    } catch (err) {
      console.log("Erro ao buscar serviços", err);
    }
  };

  useEffect(() => {
    getActivities();
    getServicesType();
  }, []);

  if (
    user?.role === "ROLE_SUPPLIER_RESPONSIBLE" &&
    "ROLE_SUPPLIER_RESPONSIBLE"
  ) {
    return (
      <Dialog>
        <DialogTrigger asChild>
          <Button className="bg-realizaBlue hidden md:block">
            Cadastrar novo prestador
          </Button>
        </DialogTrigger>
        <DialogContent
          style={{
            backgroundImage: `url(${bgModalRealiza})`,
          }}
          className="max-w-[45vw]"
        >
          <DialogHeader>
            <DialogTitle className="text-white">
              Cadastrar Subcontratado
            </DialogTitle>
          </DialogHeader>
          <div>
            {supplier ? (
              <span className="text-white">
                <strong>Filial:</strong> {supplier.corporateName}
              </span>
            ) : (
              <span className="text-white">Nenhuma filial encontrada</span>
            )}
          </div>
          <div>
            <form
              onSubmit={handleSubmitSubContract(createClient)}
              className="flex flex-col gap-4"
            >
              <div className="relative">
                <Label className="text-white">CNPJ</Label>
                <div className="flex items-center gap-2">
                  <Input
                    type="text"
                    placeholder="Insira o cnpj do prestador..."
                    {...registerSubContract("cnpj")}
                    defaultValue={getValues("cnpj") || ""}
                    className="pr-10"
                  />
                  {isLoading ? (
                    <div
                      onClick={handleCNPJSearchSub}
                      className="bg-realizaBlue cursor-pointer rounded-lg p-2 text-white transition-all hover:bg-neutral-500"
                    >
                      <Oval
                        visible={true}
                        height="30"
                        width="40"
                        color="#34495E"
                        ariaLabel="oval-loading"
                        wrapperStyle={{}}
                        wrapperClass=""
                      />
                    </div>
                  ) : (
                    <div
                      onClick={handleCNPJSearchSub}
                      className="bg-realizaBlue cursor-pointer rounded-lg p-2 text-white transition-all hover:bg-neutral-500"
                    >
                      <Search />
                    </div>
                  )}
                </div>
                {errors.cnpj && (
                  <span className="text-red-600">{errors.cnpj.message}</span>
                )}
              </div>
              <div className="mb-1">
                <Label className="text-white">Razão Social</Label>
                <Input
                  type="corporateName"
                  placeholder="Digite a razão social do novo prestador"
                  {...registerSubContract("corporateName")}
                  className="w-full"
                />
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
                  <span className="text-red-600">
                    {errorsSubContract.email.message}
                  </span>
                )}
              </div>
              <div>
                <Label className="text-white">Telefone</Label>
                <Input
                  placeholder="Digite o telefone"
                  {...registerSubContract("phone")}
                />
                {errorsSubContract.phone && (
                  <span className="text-red-600">
                    {errorsSubContract.phone.message}
                  </span>
                )}
              </div>
              <div className="flex flex-col gap-1">
                <Label className="text-white">Selecione um fornecedor</Label>
                <select
                  defaultValue={""}
                  className="rounded-lg p-2"
                  {...registerSubContract("providerSubcontractor")}
                >
                  <option value="" disabled>
                    Selecione uma opção
                  </option>
                  {contracts.map((supplier: any) => (
                    <option
                      value={supplier.idContract}
                      key={supplier.idContract}
                    >
                      {supplier.serviceName}
                    </option>
                  ))}
                </select>
                {errorsSubContract.providerSubcontractor && (
                  <span className="text-red-600">
                    {errorsSubContract.providerSubcontractor.message}
                  </span>
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
            <Dialog open={nextModal} onOpenChange={setNextModal}>
              <DialogContent
                className="max-w-[95vw] border-none md:max-w-[45vw]"
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
                          key={getIdManager}
                          className="rounded-md border p-2"
                          {...registerContract("idResponsible")} // Associando ao idResponsible
                        >
                          <option value="" disabled>
                            Selecione um gestor
                          </option>
                          {Array.isArray(managers) &&
                            managers.map((manager: any) => (
                              <option
                                value={manager.idUser}
                                onClick={() => setGetIdManager(manager.idUser)}
                                key={manager.idUser}
                              >
                                {manager.firstName} {manager.surname}{" "}
                              </option>
                            ))}
                        </select>
                        {errorsContract.idResponsible && (
                          <span className="text-red-600">
                            {errorsContract.idResponsible.message}
                          </span>
                        )}
                      </div>
                      <div>
                        <Label className="text-white">
                          Data de início efetivo
                        </Label>
                        <Input type="date" {...registerContract("dateStart")} />
                        {errorsContract.dateStart && (
                          <span className="text-red-600">
                            {errorsContract.dateStart.message}
                          </span>
                        )}
                      </div>
                      <div>
                        <Label className="text-white">
                          Referência do contrato
                        </Label>
                        <Input {...registerContract("contractReference")} />
                        {errorsContract.contractReference && (
                          <span className="text-red-500">
                            {errorsContract.contractReference.message}
                          </span>
                        )}
                      </div>
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
                        {errorsContract.idServiceType && (
                          <span className="text-red-500">
                            {errorsContract.idServiceType.message}
                          </span>
                        )}
                      </div>

                      <div className="flex flex-col gap-1">
                        <Label className="text-white">Tipo de Gestão</Label>
                        <div>
                          <div>
                            <Label>SSMA</Label>
                            <input type="checkbox" placeholder="SSMA" />
                          </div>
                          <div>
                            <Label>TRABALHISTA</Label>
                            <input type="checkbox" />
                          </div>
                        </div>
                        <select
                          {...registerContract("idServiceType")}
                          className="rounded-md p-1"
                        >
                          <option value="" disabled>
                            Selecione uma opção
                          </option>
                          <option value="AMBAS">AMBAS</option>
                          <option value="">TRABALHISTA</option>
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
                        {/* <select
                          {...registerContract("idActivity")}
                          className="rounded-md border p-2"
                          defaultValue=""
                        > */}
                        {/* <option value="" disabled>
                            Selecione uma atividade
                          </option> */}
                        {activities.map((activitie: any) => (
                          <div
                            // value={activitie.idActivity}
                            key={activitie.idActivity}
                          >
                            {activitie.title}{" "}
                          </div>
                        ))}
                        {/* </select> */}
                        {/* {errorsContract.idActivity && (
                          <span className="text-red-500">
                            {errorsContract.idActivity.message}
                          </span>
                        )} */}
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
                      {isLoading ? (
                        <Button className="bg-realizaBlue" type="submit">
                          <Oval
                            visible={true}
                            height="80"
                            width="80"
                            color="#4fa94d"
                            ariaLabel="oval-loading"
                            wrapperStyle={{}}
                            wrapperClass=""
                          />
                        </Button>
                      ) : (
                        <Button className="bg-realizaBlue" type="submit">
                          Enviar contrato
                        </Button>
                      )}
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

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="hidden bg-sky-700 md:block">
          Cadastrar novo prestador
        </Button>
      </DialogTrigger>
      <DialogTrigger asChild>
        <Button className="h-[8vw] w-[8vw] bg-sky-700 md:hidden">+</Button>
      </DialogTrigger>
      <DialogContent
        style={{
          backgroundImage: `url(${bgModalRealiza})`,
        }}
        className="max-w-[90vw] md:max-w-[45vw]"
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
            <Label className="text-white">Selecione uma das opções</Label>
            <div className="flex flex-col gap-2 md:flex-row">
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
              <div className="relative">
                <Label className="text-white">CNPJ</Label>
                <div className="flex items-center gap-3">
                  <Input
                    type="text"
                    placeholder="Insira o cnpj do prestador..."
                    {...register("cnpj")}
                    className="w-full"
                  />
                  {isLoading ? (
                    <div
                      onClick={handleCNPJSearch}
                      className="bg-realizaBlue cursor-pointer rounded-lg p-2 text-white transition-all hover:bg-neutral-500"
                    >
                      <Oval
                        visible={true}
                        height="30"
                        width="40"
                        color="#34495E"
                        ariaLabel="oval-loading"
                        wrapperStyle={{}}
                        wrapperClass=""
                      />
                    </div>
                  ) : (
                    <div
                      onClick={handleCNPJSearch}
                      className="bg-realizaBlue cursor-pointer rounded-lg p-2 text-white transition-all hover:bg-neutral-500"
                    >
                      <Search />
                    </div>
                  )}
                </div>
                {errors.cnpj && (
                  <span className="text-red-600">{errors.cnpj.message}</span>
                )}
              </div>
              <div className="mb-1">
                <Label className="text-white">Razão Social</Label>
                <Input
                  type="corporateName"
                  placeholder="Digite a razão social do novo prestador"
                  {...register("corporateName")}
                  className="w-full"
                />
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
              <div className="relative">
                <Label className="text-white">CNPJ</Label>
                <div className="flex items-center gap-3">
                  <Input
                    type="text"
                    placeholder="Insira o cnpj do prestador..."
                    {...registerSubContract("cnpj")}
                    defaultValue={getValues("cnpj") || ""}
                    className="w-full"
                  />
                  {isLoading ? (
                    <div
                      onClick={handleCNPJSearchSub}
                      className="bg-realizaBlue cursor-pointer rounded-lg p-2 text-white transition-all hover:bg-neutral-500"
                    >
                      <Oval
                        visible={true}
                        height="30"
                        width="40"
                        color="#34495E"
                        ariaLabel="oval-loading"
                        wrapperStyle={{}}
                        wrapperClass=""
                      />
                    </div>
                  ) : (
                    <div
                      onClick={handleCNPJSearchSub}
                      className="bg-realizaBlue cursor-pointer rounded-lg p-2 text-white transition-all hover:bg-neutral-500"
                    >
                      <Search />
                    </div>
                  )}
                </div>
                {errors.cnpj && (
                  <span className="text-red-600">{errors.cnpj.message}</span>
                )}
              </div>
              <div className="mb-1">
                <Label className="text-white">Razão Social</Label>
                <Input
                  type="corporateName"
                  placeholder="Digite a razão social do novo prestador"
                  {...registerSubContract("corporateName")}
                  className="w-full"
                />
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
                  <span className="text-red-600">
                    {errorsSubContract.email.message}
                  </span>
                )}
              </div>
              <div>
                <Label className="text-white">Telefone</Label>
                <Input
                  placeholder="Digite o telefone"
                  {...registerSubContract("phone")}
                />
                {errorsSubContract.phone && (
                  <span className="text-red-600">
                    {errorsSubContract.phone.message}
                  </span>
                )}
              </div>
              <div className="flex flex-col gap-1">
                <Label className="text-white">Selecione um fornecedor</Label>
                <select
                  defaultValue={""}
                  className="rounded-lg p-2"
                  {...registerSubContract("providerSubcontractor")}
                >
                  <option value="" disabled>
                    Selecione uma opção
                  </option>
                  {suppliers.map((supplier: any) => (
                    <option
                      value={supplier.idProvider}
                      key={supplier.idProvider}
                    >
                      {supplier.tradeName} {supplier.cnpj}
                    </option>
                  ))}
                </select>
                {errorsSubContract.providerSubcontractor && (
                  <span className="text-red-600">
                    {errorsSubContract.providerSubcontractor.message}
                  </span>
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
              className="max-w-[95vw] border-none md:max-w-[45vw]"
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
                  <span className="text-white">Nenhuma filial selecionada</span>
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
                        key={getIdManager}
                        className="rounded-md border p-2"
                        {...registerContract("idResponsible")} // Associando ao idResponsible
                      >
                        <option value="" disabled>
                          Selecione um gestor
                        </option>
                        {Array.isArray(managers) &&
                          managers.map((manager: any) => (
                            <option
                              value={manager.idUser}
                              onClick={() => setGetIdManager(manager.idUser)}
                              key={manager.idUser}
                            >
                              {manager.firstName} {manager.surname}{" "}
                            </option>
                          ))}
                      </select>
                      {errorsContract.idResponsible && (
                        <span className="text-red-600">
                          {errorsContract.idResponsible.message}
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
                    <div>
                      <Label className="text-white">
                        Data de início efetivo
                      </Label>
                      <Input type="date" {...registerContract("dateStart")} />
                      {errorsContract.dateStart && (
                        <span className="text-red-600">
                          {errorsContract.dateStart.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">
                        Referência do contrato
                      </Label>
                      <Input {...registerContract("contractReference")} />
                      {errorsContract.contractReference && (
                        <span className="text-red-500">
                          {errorsContract.contractReference.message}
                        </span>
                      )}
                    </div>
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
                        <span className="text-red-500">
                          {errorsContract.expenseType.message}
                        </span>
                      )}
                    </div>

                    <div className="flex flex-col gap-3">
                      <Label className="text-white">Tipo de Gestão</Label>
                      <div className="flex flex-col items-start gap-3">
                        <div className="flex flex-row-reverse gap-2">
                          <Label className="text-[14px] text-white">SSMA</Label>
                          <input
                            type="checkbox"
                            checked={isSsma}
                            {...registerContract("hse", {
                              onChange: (e) => {
                                setIsSsma(e.target.checked);
                              },
                            })}
                          />
                        </div>
                        <div className="flex flex-row-reverse gap-2">
                          <Label className="text-[14px] text-white">
                            TRABALHISTA
                          </Label>
                          <input
                            type="checkbox"
                            {...registerContract("labor")}
                          />
                        </div>
                      </div>

                      {errorsContract.expenseType && (
                        <span className="text-red-500">
                          {errorsContract.expenseType.message}
                        </span>
                      )}
                    </div>
                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Tipo do Serviço</Label>
                      <select
                        defaultValue=""
                        {...registerContract("idServiceType")}
                        className="rounded-md border p-2"
                      >
                        <option value="" disabled>
                          Selecione uma opção
                        </option>
                        {servicesType.map((idServiceType: any) => (
                          <option
                            value={idServiceType.idServiceType}
                            key={idServiceType.idServiceType}
                          >
                            {idServiceType.title}
                          </option>
                        ))}
                      </select>
                      {errorsContract.idServiceType && (
                        <span className="text-red-500">
                          {errorsContract.idServiceType.message}
                        </span>
                      )}
                    </div>

                    {isSsma === true && (
                      <div className="flex flex-col gap-2">
                        <Label className="text-white">Tipo de atividade</Label>
                        <ScrollArea className="h-[15vh]">
                          <div className="flex flex-col gap-1">
                            {activities.map((activity: any) => (
                              <div
                                key={activity.idActivity}
                                className="flex items-center gap-2"
                              >
                                <input
                                  type="checkbox"
                                  onChange={(e) =>
                                    handleCheckboxChange(
                                      activity.idActivity,
                                      e.target.checked,
                                    )
                                  }
                                  checked={selectedActivities.includes(
                                    activity.idActivity,
                                  )}
                                />
                                <p className="text-white">{activity.title}</p>
                              </div>
                            ))}

                            {/* {errorsContract.idActivity && (
        <span className="text-red-500">
          {errorsContract.idActivity.message}
        </span>
      )} */}
                          </div>
                        </ScrollArea>
                      </div>
                    )}

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
                    {isLoading ? (
                      <Button className="bg-realizaBlue" type="submit">
                        <Oval
                          visible={true}
                          height="80"
                          width="80"
                          color="#4fa94d"
                          ariaLabel="oval-loading"
                          wrapperStyle={{}}
                          wrapperClass=""
                        />
                      </Button>
                    ) : (
                      <Button className="bg-realizaBlue" type="submit">
                        Enviar contrato
                      </Button>
                    )}
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
