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
// import { propsBranch, propsClient } from "@/types/interfaces";
import { toast } from "sonner";
import { ScrollArea } from "./ui/scroll-area";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { fetchCompanyByCNPJ } from "@/hooks/gets/realiza/useCnpjApi";
import { Search } from "lucide-react";
import { useClient } from "@/context/Client-Provider";
import { useBranch } from "@/context/Branch-provider";

const modalSendEmailFormSchema = z.object({
  email: z.string().email("Insira um email válido"),
  company: z.string().default("SUPPLIER"),
  cnpj: z.string().nonempty("Insira o CNPJ"),
  tradeName: z.string(),
  corporateName: z.string(),
  cep: z.string(),
  state: z.string(),
  city: z.string(),
  address: z.string(),
  number: z.string(),
});

const contractFormSchema = z.object({
  contractReference: z
    .string()
    .nonempty("A referência do contrato é obrigatória"),
  serviceName: z.string(),
  description: z.string().nonempty("A descrição detalhada é obrigatória"),
  expenseType: z.string().nonempty("O tipo de despesa é obrigatório"),
  startDate: z
    .string()
    .refine(
      (val) => !isNaN(Date.parse(val)),
      "A data de início deve ser válida",
    ),
  serviceType: z.string().optional(),
  // risk: z.string(),
  activities: z
    .array(z.string())
    .min(1, "Pelo menos uma atividade é obrigatória"),
  serviceTypeExpense: z.string(),
});

type ModalSendEmailFormSchema = z.infer<typeof modalSendEmailFormSchema>;
type ContractFormSchema = z.infer<typeof contractFormSchema>;

export function ModalTesteSendSupplier() {
  const [managers, setManagers] = useState<any>([]);
  const [activities, setActivities] = useState<any>([]);
  // const [requirements, setRequirements] = useState<any>([]);
  // const [selectedRadio, setSelectedRadio] = useState<string | null>(null);
  const [manager, setManager] = useState<any>(null);
  const [supplierInfo, setSupplierInfo] =
    useState<ModalSendEmailFormSchema | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [nextModal, setNextModal] = useState(false);
  const { client } = useClient();
  const { selectedBranch } = useBranch();
  const [allSuppliers, setAllSuppliers] = useState<ModalSendEmailFormSchema[]>([]);


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

  const getActivities = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const activitieData = await axios.get(`${ip}/contract/activity`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      }
      );
      // const requirementData = await axios.get(`${ip}/contract/requirement`);
      setActivities(activitieData.data.content);
      console.log("atividades log teste:", activitieData.data.content);

      // setRequirements(requirementData.data.content);
    } catch (err) {
      console.log(err);
    }
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

  const emailJaExiste = allSuppliers.some(
    (supplier) => supplier.email.toLowerCase() === data.email.toLowerCase()
  );

  if (emailJaExiste) {
    toast.error("E-mail já cadastrado na sessão atual");
    setIsLoading(false);
    return;
  }

  try {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    await axios.post(`${ip}/invite`, {
      email: data.email,
      company: data.company,
      cnpj: data.cnpj,
    }, {
      headers: { Authorization: `Bearer ${tokenFromStorage}` }
    });

    const supplierData = {
      ...data,
      branch: selectedBranch,
      manager: manager,
    };

    setSupplierInfo(supplierData);
    setAllSuppliers((prev) => [...prev, data]);
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
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.post(`${ip}/contract/supplier/new-supplier`, payload, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      }
      );
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

  const getManager = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/user/client/filtered-client?idSearch=${selectedBranch?.idBranch}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      }
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

  console.log("manager teste:", manager);

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getManager();
    }
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
            <h1>Cadastrar novo prestador</h1>
          </DialogTitle>
        </DialogHeader>
        <div className="flex flex-col gap-5">
          <div>
            {client ? (
              <h1 className="text-white">{client?.tradeName}</h1>
            ) : (
              <h1>Cliente não selecionado</h1>
            )}
            <div>
              {selectedBranch ? (
                <h1 className="text-white">Filial: {selectedBranch?.name}</h1>
              ) : (
                <h1>Cliente não selecionado</h1>
              )}
            </div>
          </div>

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
                    className="bg-realizaBlue ml-2"
                  >
                    <Search />
                  </Button>
                </div>
                {errors.cnpj && (
                  <span className="text-red-600">{errors.cnpj.message}</span>
                )}
              </div>
              <div className="flex flex-col gap-4">
                <div>
                  <select
                    defaultValue=""
                    onChange={(event) => {
                      const selectedId = event.target.value;
                      const managerObj = managers.find(
                        (m: any) => m.idUser === selectedId,
                      );
                      setManager(managerObj);
                    }}
                  >
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
                <DialogTitle className="flex flex-col gap-5 text-white">
                  <h1>Faça o contrato</h1>
                  <div className="flex flex-col gap-2">
                    {client ? (
                      <h1 className="text-white">{client?.tradeName}</h1>
                    ) : (
                      <h1>Cliente não selecionado</h1>
                    )}
                    <div>
                      {selectedBranch ? (
                        <h1 className="text-white">
                          Filial: {selectedBranch?.name}
                        </h1>
                      ) : (
                        <h1>Cliente não selecionado</h1>
                      )}
                    </div>
                  </div>
                </DialogTitle>
              </DialogHeader>
              <ScrollArea className="h-[60vh] p-5">
                <div>
                  <h2 className="text-white">
                    Gestor responsável: {manager?.firstName}
                  </h2>
                </div>
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
                      <Label className="text-white">Início do serviço</Label>
                      <Input type="date" {...registerContract("startDate")} />
                      {errorsContract.startDate && (
                        <span className="text-red-500">
                          {errorsContract.startDate.message}
                        </span>
                      )}
                    </div>
                    <div>
                      <Label className="text-white">
                        Referencia do contrato
                      </Label>
                      <Input {...registerContract("contractReference")} />
                      {errorsContract.contractReference && (
                        <span className="text-red-500">
                          {errorsContract.contractReference.message}
                        </span>
                      )}
                    </div>
                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Tipo do serviço</Label>
                      <select
                        {...registerContract("serviceTypeExpense")}
                        className="rounded-md p-1"
                        defaultValue=""
                      >
                        <option value="" disabled>
                          Selecione um tipo de serviço
                        </option>
                        <option value="CAPEX">CAPEX</option>
                        <option value="OPEX">OPEX</option>
                      </select>
                      {errorsContract.serviceType && (
                        <span className="text-red-500">
                          {errorsContract.serviceType.message}
                        </span>
                      )}
                    </div>
                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Tipo de gestão</Label>
                      <select
                        className="rounded-md p-1"
                        defaultValue=""
                        {...registerContract("expenseType")}
                      >
                        <option disabled>Selecione uma opção</option>
                        <option value="TRABALHISTA">Trabalhista</option>
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
                    <div className="flex flex-col gap-1">
                      <Label className="text-white">Tipos de atividades</Label>
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
                    <div>
                      <Label className="text-white">Nome do serviço</Label>
                      <Input {...registerContract("serviceName")} />
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
                    {/* <div>
                      <Label className="text-white">Risco do contrato</Label>
                      <select
                        {...registerContract("risk")}
                        className="w-full rounded-md border p-2"
                        defaultValue=""
                      >
                        <option value="" disabled>
                          Selecione o risco do contrato
                        </option>
                        <option value="LOW_LESS_THAN_8H">
                          LOW_LESS_THAN_8H
                        </option>
                        <option value="LOW_LESS_THAN_1M">
                          LOW_LESS_THAN_1M
                        </option>
                        <option value="LOW_LESS_THAN_6M">
                          LOW_LESS_THAN_6M
                        </option>
                        <option value="LOW_MORE_THAN_6M">
                          LOW_MORE_THAN_6M
                        </option>
                        <option value="MEDIUM_LESS_THAN_1M">
                          MEDIUM_LESS_THAN_1M
                        </option>
                        <option value="MEDIUM_LESS_THAN_6M">
                          MEDIUM_LESS_THAN_6M
                        </option>
                        <option value="MEDIUM_MORE_THAN_6M">
                          MEDIUM_MORE_THAN_6M
                        </option>
                        <option value="HIGH_LESS_THAN_1M">
                          HIGH_LESS_THAN_1M
                        </option>
                        <option value="HIGH_LESS_THAN_6M">
                          HIGH_LESS_THAN_6M
                        </option>
                        <option value="HIGH_MORE_THAN_6M">
                          HIGH_MORE_THAN_6M
                        </option>
                      </select>
                      {errorsContract.risk && (
                        <span className="text-red-500">
                          {errorsContract.risk.message}
                        </span>
                      )}
                    </div> */}
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
