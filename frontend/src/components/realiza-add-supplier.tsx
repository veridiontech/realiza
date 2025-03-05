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
import { propsBranch, propsClient } from "@/types/interfaces";
import { toast } from "sonner";
import { ScrollArea } from "./ui/scroll-area";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { fetchCompanyByCNPJ } from "@/hooks/gets/realiza/useCnpjApi";

const modalSendEmailFormSchema = z.object({
  email: z
    .string()
    .email("Insira um email válido")
    .default("vendas@comercialbrasil.com"),
  company: z.string().default("SUPPLIER"),
  cnpj: z.string().nonempty("Insira o CNPJ").default("98.765.432/0001-12"),
  idCompany: z.string().nonempty("Selecione um cliente"),
  branch: z.string().default("78016e8e-b197-4158-a2df-06a4841a5685"),
  tradeName: z.string().default("Comercial Brasil EIRELI"),
  corporateName: z.string().default("Comercial Brasil EIRELI Matriz"),
  cep: z.string().default("20031-000"),
  state: z.string().default("RJ"),
  city: z.string().default("Rio de Janeiro"),
  address: z.string().default("Rua das Flores, 50"),
  number: z.string().default("50"),
});

const contractFormSchema = z.object({
  contractReference: z
    .string()
    .nonempty("A referência do contrato é obrigatória"),
  serviceDuration: z.string().nonempty("A duração do serviço é obrigatória"),
  serviceName: z.string().nonempty("O nome do serviço é obrigatório"),
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
  serviceType: z.string().nonempty("O tipo de serviço é obrigatório"),
  subcontractPermission: z.string(),
  risk: z.string(),
  activities: z
    .array(z.string())
    .min(1, "Pelo menos uma atividade é obrigatória"),
  requirements: z
    .array(z.string())
    .min(1, "Pelo menos um requisito é obrigatória"),
});

type ModalSendEmailFormSchema = z.infer<typeof modalSendEmailFormSchema>;
type ContractFormSchema = z.infer<typeof contractFormSchema>;

export function ModalTesteSendSupplier() {
  const [clients, setClients] = useState<propsClient[]>([]);
  const [managers, setManagers] = useState<any>([]);
  const [activities, setActivities] = useState<any>([]);
  const [requirements, setRequirements] = useState<any>([]);
  // const [selectedRadio, setSelectedRadio] = useState<string | null>(null);
  const [supplierInfo, setSupplierInfo] =
    useState<ModalSendEmailFormSchema | null>(null);
  const [branches, setBranches] = useState<propsBranch[]>([]);
  const [selectedBranchId, setSelectedBranchId] = useState<string>("");
  const [isLoading, setIsLoading] = useState(false);
  const [nextModal, setNextModal] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    getValues,
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
      const firstRes = await axios.get(`${ip}/client`, {
        params: { page: 0, size: 100 },
      });
      const totalPages = firstRes.data.totalPages;
      const requests = Array.from({ length: totalPages - 1 }, (_, i) =>
        axios.get(`${ip}/client`, { params: { page: i + 1, size: 100 } }),
      );

      const responses = await Promise.all(requests);
      const allClients = [
        firstRes.data.content,
        ...responses.map((res) => res.data.content),
      ].flat();

      setClients(allClients);
      try {
        const res = await axios.get(
          `${ip}/user/client/filtered-client?idSearch=${selectedBranchId}`,
        );
        console.log("gestores:", res.data.content);
        
        setManagers(res.data.content);
      } catch (err) {
        console.log("Erro ao buscar gestores:", err);
      }
    } catch (err) {
      console.log("Erro ao buscar clientes:", err);
    }
  };

  const getBranches = async (clientId: string) => {
    try {
      const res = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${clientId}`,
      );
      setBranches(res.data.content);
    } catch (err) {
      console.log("Erro ao buscar filiais:", err);
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

  const onSelectClient = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const id = e.target.value;
    getBranches(id);
  };

  const onSelectBranch = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const id = e.target.value;
    setSelectedBranchId(id);
  };

  const handleCNPJSearch = async () => {
    const cnpjValue = getValues("cnpj");
    try {
      const companyData = await fetchCompanyByCNPJ(cnpjValue);
      setValue("tradeName", companyData.nomeFantasia || "");
      setValue("corporateName", companyData.razaoSocial || "");
      setValue("cep", companyData.cep || "");
      setValue("state", companyData.state || "");
      setValue("city", companyData.city || "");
      setValue("address", companyData.address || "");
      setValue("number", companyData.number || "");
    } catch (error) {
      toast.error("Erro ao buscar dados do CNPJ");
    }
  };

  const createClient = async (data: ModalSendEmailFormSchema) => {
    setIsLoading(true);
    try {
      await axios.post(`${ip}/invite`, {
        email: data.email,
        idCompany: selectedBranchId,
        company: data.company,
        cnpj: data.cnpj,
      });
      const supplierData = { ...data, branch: selectedBranchId };
      setSupplierInfo(supplierData);
      toast.success("Email de cadastro enviado para novo prestador");
      setNextModal(true);
    } catch (err) {
      console.log("Erro ao enviar email para usuário:", err);
      toast.error("Erro ao enviar email. Tente novamente");
    } finally {
      setIsLoading(false);
    }
  };

  const createContract = async (data: ContractFormSchema) => {
    if (!supplierInfo) {
      toast.error("Informações do prestador não encontradas");
      return;
    }
    const payload = { ...supplierInfo, ...data };
    console.log("Payload do contrato:", payload);
    try {
      await axios.post(`${ip}/contract/supplier/new-supplier`, payload);
      toast.success("Contrato criado com sucesso");
    } catch (err) {
      console.log("Erro ao criar contrato:", err);
      toast.error("Erro ao criar contrato");
    }
  };

  // const handleRadioClick = (value: string) => {
  //   setSelectedRadio(value);
  // };

  // const shouldShowServiceType =
  //   selectedRadio === null || selectedRadio === "nao";

  useEffect(() => {
    getClients();
    getActivities();
  }, []);

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="bg-realizaBlue">Cadastrar novo prestador</Button>
      </DialogTrigger>
      <DialogContent
        style={{ backgroundImage: `url(${bgModalRealiza})` }}
        className="max-w-[45vw]"
      >
        <DialogHeader>
          <DialogTitle className="text-white">
            Cadastrar novo prestador
          </DialogTitle>
        </DialogHeader>
        <div>
          <ScrollArea className="h-[60vh]">
            <form
              onSubmit={handleSubmit(createClient)}
              className="flex flex-col gap-4"
            >
              <div>
                <Label className="text-white">Email do Prestador</Label>
                <Input
                  type="email"
                  placeholder="Digite o email do prestador"
                  {...register("email")}
                  className="w-full"
                />
                {errors.email && (
                  <span className="text-red-600">{errors.email.message}</span>
                )}
              </div>
              <div>
                <Label className="text-white">CNPJ</Label>
                <div className="flex items-center">
                  <Input
                    type="text"
                    placeholder="Insira o CNPJ do prestador"
                    {...register("cnpj")}
                  />
                  <Button
                    type="button"
                    onClick={handleCNPJSearch}
                    className="ml-2"
                  >
                    <i className="icon-search" />
                  </Button>
                </div>
                {errors.cnpj && (
                  <span className="text-red-600">{errors.cnpj.message}</span>
                )}
              </div>
              <div className="flex flex-col gap-4">
                <div className="flex flex-col gap-1">
                  <Label className="text-white">Selecione um cliente</Label>
                  <select
                    className="rounded-md border p-2"
                    defaultValue=""
                    {...register("idCompany")}
                    onChange={onSelectClient}
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
                  {errors.idCompany && (
                    <span className="text-red-600">
                      {errors.idCompany.message}
                    </span>
                  )}
                </div>
                <div className="flex flex-col gap-1">
                  <Label className="text-white">Filiais do cliente</Label>
                  <select
                    className="rounded-md border p-2"
                    defaultValue=""
                    onChange={onSelectBranch}
                  >
                    <option value="" disabled>
                      Selecione uma filial
                    </option>
                    {branches.map((branch) => (
                      <option key={branch.idBranch} value={branch.idBranch}>
                        {branch.name}
                      </option>
                    ))}
                  </select>
                  {!selectedBranchId && (
                    <span className="text-red-600">Selecione uma filial</span>
                  )}
                </div>
                <div>
                  <select defaultValue="">
                    <option value="" disabled>
                      Selecione um gestor
                    </option>
                    {managers.map((manager: any) => (
                      <option key={manager.idUser} value={manager.idUser}>
                        {manager.firstName}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="flex flex-col gap-4 border-t pt-4">
                <div>
                  <Label className="text-white">Trade Name</Label>
                  <Input type="text" {...register("tradeName")} />
                  {errors.tradeName && (
                    <span className="text-red-600">
                      {errors.tradeName.message}
                    </span>
                  )}
                </div>
                <div>
                  <Label className="text-white">Corporate Name</Label>
                  <Input type="text" {...register("corporateName")} />
                  {errors.corporateName && (
                    <span className="text-red-600">
                      {errors.corporateName.message}
                    </span>
                  )}
                </div>
                <div>
                  <Label className="text-white">CEP</Label>
                  <Input type="text" {...register("cep")} />
                  {errors.cep && (
                    <span className="text-red-600">{errors.cep.message}</span>
                  )}
                </div>
                <div>
                  <Label className="text-white">State</Label>
                  <Input type="text" {...register("state")} />
                  {errors.state && (
                    <span className="text-red-600">{errors.state.message}</span>
                  )}
                </div>
                <div>
                  <Label className="text-white">City</Label>
                  <Input type="text" {...register("city")} />
                  {errors.city && (
                    <span className="text-red-600">{errors.city.message}</span>
                  )}
                </div>
                <div>
                  <Label className="text-white">Address</Label>
                  <Input type="text" {...register("address")} />
                  {errors.address && (
                    <span className="text-red-600">
                      {errors.address.message}
                    </span>
                  )}
                </div>
                <div>
                  <Label className="text-white">Number</Label>
                  <Input type="text" {...register("number")} />
                  {errors.number && (
                    <span className="text-red-600">
                      {errors.number.message}
                    </span>
                  )}
                </div>
              </div>
              <div className="flex justify-end">
                {isLoading ? (
                  <Button>
                    <Radio
                      visible={true}
                      height="80"
                      width="80"
                      ariaLabel="radio-loading"
                    />
                  </Button>
                ) : (
                  <Button className="bg-realizaBlue" type="submit">
                    Enviar
                  </Button>
                )}
              </div>
            </form>
          </ScrollArea>
          <Dialog open={nextModal} onOpenChange={setNextModal}>
            <DialogContent
              className="max-w-[45vw] border-none"
              style={{ backgroundImage: `url(${bgModalRealiza})` }}
            >
              <DialogHeader>
                <DialogTitle className="text-white">
                  Faça o contrato
                </DialogTitle>
              </DialogHeader>
              <ScrollArea className="h-[60vh] w-full px-5">
                <div className="p-4">
                  {supplierInfo && (
                    <div className="mb-4 rounded-md border bg-white p-2 text-black">
                      <p>
                        <strong>CNPJ:</strong> {supplierInfo.cnpj}
                      </p>
                      <p>
                        <strong>Trade Name:</strong> {supplierInfo.tradeName}
                      </p>
                      <p>
                        <strong>Corporate Name:</strong>{" "}
                        {supplierInfo.corporateName}
                      </p>
                      <p>
                        <strong>Email:</strong> {supplierInfo.email}
                      </p>
                      <p>
                        <strong>CEP:</strong> {supplierInfo.cep}
                      </p>
                      <p>
                        <strong>State:</strong> {supplierInfo.state}
                      </p>
                      <p>
                        <strong>City:</strong> {supplierInfo.city}
                      </p>
                      <p>
                        <strong>Address:</strong> {supplierInfo.address}
                      </p>
                      <p>
                        <strong>Number:</strong> {supplierInfo.number}
                      </p>
                    </div>
                  )}
                  <form
                    className="flex flex-col gap-2"
                    onSubmit={handleSubmitContract(createContract)}
                  >
                    <div>
                      <Label className="text-white">Contract Reference</Label>
                      <Input {...registerContract("contractReference")} />
                      {errorsContract.contractReference && (
                        <span className="text-red-500">
                          {errorsContract.contractReference.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">Service Duration</Label>
                      <Input {...registerContract("serviceDuration")} />
                      {errorsContract.serviceDuration && (
                        <span className="text-red-500">
                          {errorsContract.serviceDuration.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">Service Name</Label>
                      <Input {...registerContract("serviceName")} />
                      {errorsContract.serviceName && (
                        <span className="text-red-500">
                          {errorsContract.serviceName.message}
                        </span>
                      )}
                    </div>
                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Description</Label>
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
                      <Label className="text-white">Allocated Limit</Label>
                      <Input {...registerContract("allocatedLimit")} />
                      {errorsContract.allocatedLimit && (
                        <span className="text-red-500">
                          {errorsContract.allocatedLimit.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">Responsible</Label>
                      <Input {...registerContract("responsible")} />
                      {errorsContract.responsible && (
                        <span className="text-red-500">
                          {errorsContract.responsible.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">Expense Type</Label>
                      <Input {...registerContract("expenseType")} />
                      {errorsContract.expenseType && (
                        <span className="text-red-500">
                          {errorsContract.expenseType.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">Start Date</Label>
                      <Input type="date" {...registerContract("startDate")} />
                      {errorsContract.startDate && (
                        <span className="text-red-500">
                          {errorsContract.startDate.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">End Date</Label>
                      <Input type="date" {...registerContract("endDate")} />
                      {errorsContract.endDate && (
                        <span className="text-red-500">
                          {errorsContract.endDate.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">Service Type</Label>
                      <Input {...registerContract("serviceType")} />
                      {errorsContract.serviceType && (
                        <span className="text-red-500">
                          {errorsContract.serviceType.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">
                        Subcontract Permission
                      </Label>
                      <select
                        {...registerContract("subcontractPermission")}
                        className="rounded-md border p-2"
                        defaultValue=""
                      >
                        <option value="" disabled>
                          Selecione uma opção
                        </option>
                        <option value="true">True</option>
                        <option value="false">False</option>
                      </select>
                      {errorsContract.subcontractPermission && (
                        <span className="text-red-500">
                          {errorsContract.subcontractPermission.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">Risk</Label>
                      <Input {...registerContract("risk")} />
                      {errorsContract.risk && (
                        <span className="text-red-500">
                          {errorsContract.risk.message}
                        </span>
                      )}
                    </div>
                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Activities</Label>
                      <select
                        multiple
                        {...registerContract("activities")}
                        className="rounded-md border p-2"
                        defaultValue={[]}
                      >
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
                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Requirements</Label>
                      <select
                        multiple
                        {...registerContract("requirements")}
                        className="rounded-md border p-2"
                        defaultValue={[]}
                      >
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
