import bgModalRealiza from "@/assets/modalBG.jpeg";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogFooter,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useSupplier } from "@/context/Supplier-context";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Search, IdCard } from "lucide-react";
import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { Oval } from "react-loader-spinner";
import { toast } from "sonner";
import { z } from "zod";

function validarCPF(cpf: string): boolean {
  const cleaned = cpf.replace(/\D/g, "");
  if (!cleaned || cleaned.length !== 11 || /^(\d)\1+$/.test(cleaned))
    return false;

  let soma = 0;
  for (let i = 0; i < 9; i++) soma += parseInt(cleaned[i]) * (10 - i);
  let resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cleaned[9])) return false;

  soma = 0;
  for (let i = 0; i < 10; i++) soma += parseInt(cleaned[i]) * (11 - i);
  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;

  return resto === parseInt(cleaned[10]);
}

function validarNumerosRepetidos(valor: string): boolean {
  const digits = valor.replace(/\D/g, "");
  return !/^(\d)\1+$/.test(digits);
}

async function validarCEPExiste(cep: string): Promise<boolean> {
  try {
    const cepLimpo = cep.replace(/\D/g, "");
    const res = await fetch(`https://viacep.com.br/ws/${cepLimpo}/json/`);
    if (!res.ok) return false;
    const data = await res.json();
    return !data.erro;
  } catch {
    return false;
  }
}

const phoneRegex = /^\(?\d{2}\)?\s?(?:\d{4,5})-?\d{4}$/;
const cepARegex = /^\d{5}-\d{3}$/;
const cpfRegex = /^\d{3}\.\d{3}\.\d{3}-\d{2}$|^\d{11}$/;

interface propsCep {
  address: string;
  city: string;
  state: string;
}

interface Position {
  id: string;
  title: string;
}

const createNewEmployeeFormSchema = z.object({
  contractType: z.string().nonempty("Tipo de contrato é obrigatório"),
  name: z.string().nonempty("Nome é obrigatório"),
  surname: z.string().nonempty("Sobrenome é obrigatório"),
  cpf: z
    .string()
    .nonempty("CPF é obrigatório")
    .regex(cpfRegex, "Formato inválido. Use 000.000.000-00")
    .refine(validarNumerosRepetidos, {
      message: "CPF com todos os dígitos repetidos",
    })
    .refine(validarCPF, { message: "CPF inválido" }),
  salary: z.string().regex(/^\d{1,3}(\.\d{3})*,\d{2}$/, "Salário inválido"),
  gender: z.string().nonempty("Gênero é obrigatório"),
  maritalStatus: z.string().nonempty("Estado civil é obrigatório"),
  cep: z
    .string()
    .nonempty("CEP é obrigatório")
    .regex(cepARegex, "Formato inválido. Use o formato 12345-678 ")
    .superRefine(async (cep, ctx) => {
      const cleaned = cep.replace(/\D/g, "");
      const exists = await validarCEPExiste(cleaned);
      if (!exists) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: "CEP não encontrado",
        });
      }
    }),
  state: z.string().nonempty("Estado é obrigatório"),
  city: z.string().nonempty("Cidade é obrigatória"),
  address: z.string().nonempty("Endereço é obrigatório"),
  number: z.string().nonempty("Número é obrigatório"),
  complement: z.string().optional(),
  phone: z
    .string()
    .optional()
    .refine((val) => !val || phoneRegex.test(val), {
      message: "Telefone inválido, use o formato (XX) XXXXX-XXXX",
    })
    .refine((val) => !val || validarNumerosRepetidos(val), {
      message: "Telefone inválido: não pode ter números repetidos",
    }),

  mobile: z
    .string()
    .optional()
    .refine((val) => !val || validarNumerosRepetidos(val), {
      message: "Celular com dígitos repetidos",
    })
    .refine((val) => !val || phoneRegex.test(val), {
      message: "Celular inválido",
    }),
  positionId: z.string().nonempty("Cargo é obrigatório"),
  education: z.string().nonempty("Escolaridade é obrigatória"),
  cboId: z.string().optional(),
  admissionDate: z.string().optional(),
  birthDate: z.string().nonempty("Data de nascimento é obrigatória"),
  rneRnmFederalPoliceProtocol: z.string().optional(),
  brazilEntryDate: z.string().optional(),
  passport: z.string().optional(),
});

const schemaBrazilian = createNewEmployeeFormSchema;
const schemaForeigner = createNewEmployeeFormSchema.omit({ cpf: true });
type CreateNewEmpoloyeeFormSchema = z.infer<typeof createNewEmployeeFormSchema>;

interface NewModalCreateEmployeeProps {
  onEmployeeCreated: () => void;
}

export function NewModalCreateEmployee({
  onEmployeeCreated,
}: NewModalCreateEmployeeProps) {
  const [isLoading, setIsLoading] = useState(false);
  const { supplier } = useSupplier();
  const [cbos, setCbos] = useState<
    { id: string; title: string; code: string }[]
  >([]);
  const [positions, setPositions] = useState<Position[]>([]);
  const [searchCbo, setSearchCbo] = useState("");
  const [isSelectTypeModalOpen, setIsSelectTypeModalOpen] = useState(false);
  const [isBrazilianEmployeeModalOpen, setIsBrazilianEmployeeModalOpen] =
    useState(false);
  const [isForeignerEmployeeModalOpen, setIsForeignerEmployeeModalOpen] =
    useState(false);
  const [selectedEmployeeType, setSelectedEmployeeType] = useState<
    "brasileiro" | "estrangeiro" | null
  >(null);
  const [cepValue, setCepValue] = useState("");
  const [phoneValue, setPhoneValue] = useState("");
  const [mobileValue, setMobileValue] = useState("");
  const [cpfValue, setCpfValue] = useState("");

  const {
    register,
    handleSubmit,
    getValues,
    setValue,
    reset,
    formState: { errors },
  } = useForm<CreateNewEmpoloyeeFormSchema>({
    resolver: zodResolver(
      selectedEmployeeType === "estrangeiro" ? schemaForeigner : schemaBrazilian
    ),
  });

  useEffect(() => {
    const rawCEP = getValues("cep") || "";
    const rawPhone = getValues("phone") || "";

    setCepValue(formatCEP(rawCEP));
    setPhoneValue(formatPhone(rawPhone));
  }, [getValues]);

  useEffect(() => {
    const isValidCep = /^\d{5}-\d{3}$/.test(cepValue);
    if (isValidCep) {
      handleCep();
    }
  }, [cepValue]);

  useEffect(() => {
    const fetchCbos = async () => {
      try {
        const tokenFromStorage = localStorage.getItem("tokenClient");
        const response = await axios.get(`${ip}/cbo`, {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        });
        setCbos(response.data);
      } catch (error) {
        toast.error("Erro ao buscar CBOs");
      }
    };

    const fetchPositions = async () => {
      try {
        const tokenFromStorage = localStorage.getItem("tokenClient");
        const response = await axios.get(`${ip}/position`, {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        });
        setPositions(response.data);
      } catch (error) {
        toast.error("Erro ao buscar cargos");
      }
    };

    fetchCbos();
    fetchPositions();
  }, []);

  const filteredCbos = cbos.filter(
    (cbo) =>
      cbo.title.toLowerCase().includes(searchCbo.toLowerCase()) ||
      cbo.code.toLowerCase().includes(searchCbo.toLowerCase())
  );

  const formatCPF = (value: string) => {
    return value
      .replace(/\D/g, "")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d{1,2})$/, "$1-$2")
      .slice(0, 14);
  };

  const formatPhone = (value: string) => {
    const digits = value.replace(/\D/g, "");

    if (digits.length <= 2) {
      return digits;
    } else if (digits.length <= 6) {
      return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
    } else if (digits.length <= 10) {
      return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(
        6
      )}`;
    } else {
      return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(
        7,
        11
      )}`;
    }
  };

  const formatCEP = (value: string) => {
    return value
      .replace(/\D/g, "")
      .replace(/(\d{5})(\d)/, "$1-$2")
      .slice(0, 9);
  };

  const formatSalary = (value: string) => {
    const number = Number(value.replace(/\D/g, "")) / 100;
    return number
      .toLocaleString("pt-BR", {
        style: "currency",
        currency: "BRL",
        minimumFractionDigits: 2,
      })
      .replace("R$", "")
      .trim();
  };

  const normalizeSalary = (value: string) => {
    return parseFloat(value.replace(/\./g, "").replace(",", "."));
  };

  const sendEmployeeData = async (
    data: CreateNewEmpoloyeeFormSchema,
    endpoint: string,
    isForeigner: boolean
  ) => {
    setIsLoading(true);

    const payload: any = {
      ...data,
      supplier: supplier?.idProvider,
      salary: normalizeSalary(data.salary),
    };

    if (isForeigner) {
      payload.rneRnmFederalPoliceProtocol = data.rneRnmFederalPoliceProtocol;
      payload.brazilEntryDate = data.brazilEntryDate;
      payload.passport = data.passport;
      delete payload.cpf;
    } else {
      if (!payload.cpf) {
        toast.error("CPF é obrigatório para colaboradores brasileiros.");
        setIsLoading(false);
        return;
      }
    }

    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      console.log("teste", payload);
      await axios.post(endpoint, payload, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });

      toast.success("Sucesso ao cadastrar novo colaborador!");

      setEmployeeName(data.name + " " + data.surname);
      setSupplierName(supplier?.corporateName || "Fornecedor");
      setShowSuccessModal(true);

      onEmployeeCreated();
      reset();
      setCpfValue("");
      setCepValue("");
      setPhoneValue("");
      setCpfValue("");

      setIsBrazilianEmployeeModalOpen(false);
      setIsForeignerEmployeeModalOpen(false);
      setIsSelectTypeModalOpen(false);
      setSelectedEmployeeType(null);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const errorMsg = err.response?.data?.message || "";

        if (
          errorMsg.toLowerCase().includes("duplicate entry") &&
          errorMsg.toLowerCase().includes("cpf")
        ) {
          toast.error("Já existe um colaborador com esse CPF cadastrado.");
        } else {
          toast.error(
            `Erro ${err.response?.status}: ${
              errorMsg || "Erro ao cadastrar colaborador"
            }`
          );
        }
      } else {
        toast.error("Erro inesperado. Tente novamente.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmitBrazilianEmployee = async (
    data: CreateNewEmpoloyeeFormSchema
  ) => {
    await sendEmployeeData(data, `${ip}/employee/brazilian`, false);
  };

  const handleSubmitForeignerEmployee = async (
    data: CreateNewEmpoloyeeFormSchema
  ) => {
    await sendEmployeeData(data, `${ip}/employee/foreigner`, true);
  };

  const handleCep = async () => {
    try {
      const cleanedCep = cepValue.replace(/\D/g, "");
      const res = await axios.get(
        `https://viacep.com.br/ws/${cleanedCep}/json/`
      );
      if (res.data && !res.data.erro) {
        setValuesCep({
          address: res.data.logradouro,
          city: res.data.localidade,
          state: res.data.uf,
        });
        toast.success("Endereço encontrado com sucesso!");
      } else {
        toast.error("CEP não encontrado!");
      }
    } catch (err) {
      toast.error("Erro ao buscar o CEP");
    }
  };

  const setValuesCep = (data: propsCep) => {
    setValue("city", data.city);
    setValue("state", data.state);
    setValue("address", data.address);
  };

  const handleOpenSelectTypeModal = () => {
    setIsSelectTypeModalOpen(true);
  };

  const handleProceedWithEmployeeType = () => {
    if (selectedEmployeeType === "brasileiro") {
      setIsSelectTypeModalOpen(false);
      setIsBrazilianEmployeeModalOpen(true);
    } else if (selectedEmployeeType === "estrangeiro") {
      setIsSelectTypeModalOpen(false);
      setIsForeignerEmployeeModalOpen(true);
    } else {
      toast.error(
        "Por favor, selecione um tipo de colaborador (Brasileiro ou Estrangeiro)."
      );
    }
  };

  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [employeeName, setEmployeeName] = useState("");
  const [supplierName, setSupplierName] = useState("");

  return (
    <>
      <Dialog
        open={isSelectTypeModalOpen}
        onOpenChange={setIsSelectTypeModalOpen}
      >
        <DialogTrigger asChild>
          <Button
            className="hidden md:block bg-realizaBlue border border-white rounded-md"
            onClick={handleOpenSelectTypeModal}
          >
            Cadastrar novo colaborador +
          </Button>
        </DialogTrigger>
        <DialogTrigger asChild>
          <Button
            className="md:hidden bg-realizaBlue"
            onClick={handleOpenSelectTypeModal}
          >
            +
          </Button>
        </DialogTrigger>
        <DialogContent
          style={{ backgroundImage: `url(${bgModalRealiza})` }}
          className="max-w-[90vw] sm:max-w-[45vw] md:max-w-[45vw]"
        >
          <DialogHeader>
            <DialogTitle className="text-white">
              Cadastrar novo colaborador
            </DialogTitle>
          </DialogHeader>
          <div className="flex flex-col gap-5 py-4 text-white">
            <div>
              <Label className="text-white">
                Selecione qual tipo de colaborador deseja criar:
              </Label>
              <div className="flex items-center space-x-4 mt-2">
                <div className="flex items-center">
                  <input
                    type="radio"
                    id="brasileiro"
                    name="employeeType"
                    value="brasileiro"
                    checked={selectedEmployeeType === "brasileiro"}
                    onChange={() => setSelectedEmployeeType("brasileiro")}
                    className="form-radio h-4 w-4 text-realizaBlue"
                  />
                  <Label
                    htmlFor="brasileiro"
                    className="ml-2 text-white cursor-pointer"
                  >
                    Brasileiro
                  </Label>
                </div>
                <div className="flex items-center">
                  <input
                    type="radio"
                    id="estrangeiro"
                    name="employeeType"
                    value="estrangeiro"
                    checked={selectedEmployeeType === "estrangeiro"}
                    onChange={() => setSelectedEmployeeType("estrangeiro")}
                    className="form-radio h-4 w-4 text-realizaBlue"
                  />
                  <Label
                    htmlFor="estrangeiro"
                    className="ml-2 text-white cursor-pointer"
                  >
                    Estrangeiro
                  </Label>
                </div>
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button
              onClick={handleProceedWithEmployeeType}
              className="bg-realizaBlue hover:bg-blue-700"
              disabled={!selectedEmployeeType}
            >
              Prosseguir
            </Button>
            <Button
              onClick={() => {
                setIsSelectTypeModalOpen(false);
                setSelectedEmployeeType(null);
              }}
              className="bg-gray-500 hover:bg-gray-600"
            >
              Cancelar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog
        open={isBrazilianEmployeeModalOpen}
        onOpenChange={setIsBrazilianEmployeeModalOpen}
      >
        <DialogContent
          className="max-w-[90vw] sm:max-w-[45vw] md:max-w-[45vw] p-5"
        >
          <DialogHeader>
            <DialogTitle
              style={{ backgroundColor: "#27394D" }}
              className="text-white p-5 flex items-center gap-2"
            >
              <IdCard color="#C0B15B" />
              Cadastrar colaborador Brasileiro
            </DialogTitle>
            <ScrollArea className="h-[75vh]">
              <div>
                <form
                  action=""
                  className="flex flex-col gap-5 bg-slate-50 p-5"
                  onSubmit={handleSubmit(
                    handleSubmitBrazilianEmployee,
                    (errors) => {
                      console.log("Erros detectados:", errors);
                    }
                  )}
                >
                  <div>
                    <Label>Nome</Label>
                    <Input
                      type="text"
                      placeholder="Digite seu nome"
                      {...register("name")}
                    />
                  </div>
                  <div>
                    <Label>Sobrenome</Label>
                    <Input
                      type="text"
                      placeholder="Digite seu sobrenome"
                      {...register("surname")}
                    />
                  </div>
                  <div>
                    <Label>Data de nascimento</Label>
                    <Input
                      type="date"
                      placeholder="Digite a data de nascimento"
                      {...register("birthDate")}
                    />
                  </div>
                  <div>
                    <Label>Estado civil</Label>
                    <select
                      {...register("maritalStatus")}
                      className="flex flex-col rounded-md border p-2 w-full"
                    >
                      <option value="">Selecione</option>
                      <option value="CASADO">Casado</option>
                      <option value="SOLTEIRO">Solteiro</option>
                      <option value="DIVORCIADO">Divorciado</option>
                      <option value="VIUVO">Viúvo </option>
                      <option value="SEPARADO_JUDICIALMENTE">
                        Separado judicialmente{" "}
                      </option>
                      <option value="UNIAO_ESTAVEL">União estável</option>
                    </select>
                  </div>
                  <div>
                    <Label>Tipo de contrato</Label>
                    <select
                      {...register("contractType")}
                      className="flex flex-col rounded-md border p-2 w-full"
                    >
                      <option value="">Selecione um tipo de contrato</option>
                      <option value="AUTONOMO">Autônomo</option>
                      <option value="AVULSO_SINDICATO">
                        Avulso (Sindicato)
                      </option>
                      <option value="CLT_HORISTA">CLT - Horista</option>
                      <option value="CLT_TEMPO_DETERMINADO">
                        CLT - Tempo Determinado
                      </option>
                      <option value="CLT_TEMPO_INDETERMINADO">
                        CLT - Tempo Indeterminado
                      </option>
                      <option value="COOPERADO">Cooperado</option>
                      <option value="ESTAGIO_BOLSA">Estágio / Bolsa</option>
                      <option value="ESTRANGEIRO_IMIGRANTE">
                        Estrangeiro - Imigrante
                      </option>
                      <option value="ESTRANGEIRO_TEMPORARIO">
                        Estrangeiro - Temporário
                      </option>
                      <option value="INTERMITENTE">Intermitente</option>
                      <option value="JOVEM_APRENDIZ">Jovem Aprendiz</option>
                      <option value="SOCIO">Sócio</option>
                      <option value="TEMPORARIO">Temporário</option>
                    </select>
                  </div>

                  <div>
                    <Label>CPF</Label>
                    <Input
                      type="text"
                      value={cpfValue}
                      onChange={(e) => {
                        const formattedCpf = formatCPF(e.target.value);
                        setCpfValue(formattedCpf);
                        setValue("cpf", formattedCpf, { shouldValidate: true });
                      }}
                      placeholder="000.000.000-00"
                      maxLength={14}
                    />
                    {errors.cpf && (
                      <span className="text-sm text-red-600">
                        {errors.cpf.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label>Data de admissão</Label>
                    <Input type="date" {...register("admissionDate")} />
                  </div>
                  <div>
                    <Label>Salário:</Label>
                    <Input
                      type="text"
                      {...register("salary")}
                      onChange={(e) => {
                        const formattedSalary = formatSalary(e.target.value);
                        setValue("salary", formattedSalary);
                      }}
                      placeholder="000.000,00"
                    />
                    {errors.salary && <span>{errors.salary.message}</span>}
                  </div>
                  <div>
                    <Label>Sexo</Label>
                    <select
                      {...register("gender")}
                      className="flex flex-col rounded-md border p-2 w-full"
                    >
                      <option value="">Selecione</option>
                      <option value="Masculino">Masculino</option>
                      <option value="Feminino">Feminino</option>
                    </select>
                    {errors.gender && (
                      <span className="text-red-600">
                        {errors.gender.message}
                      </span>
                    )}
                  </div>

                  <div>
                    <Label>CEP</Label>
                    <div className="flex w-full gap-2">
                      <Input
                        type="text"
                        value={cepValue}
                        onChange={(e) => {
                          const formattedCEP = formatCEP(e.target.value);
                          setCepValue(formattedCEP);
                          setValue("cep", formattedCEP, {
                            shouldValidate: true,
                          });
                        }}
                        placeholder="00000-000"
                        maxLength={9}
                      />
                      <div
                        onClick={handleCep}
                        className="bg-realizaBlue cursor-pointer rounded-md p-2 text-white hover:bg-gray-600 flex items-center justify-center"
                      >
                        <Search className="w-5 h-5" />
                      </div>
                    </div>
                  </div>
                  <div>
                    <Label>Estado</Label>
                    <Input
                      placeholder="Digite seu estado"
                      {...register("state")}
                    />
                  </div>
                  <div>
                    <Label>Cidade</Label>
                    <Input
                      placeholder="Digite sua cidade"
                      {...register("city")}
                    />
                  </div>
                  <div>
                    <Label>Endereco</Label>
                    <Input
                      placeholder="Digite seu endereço"
                      {...register("address")}
                    />
                  </div>
                  <div>
                    <Label>Número</Label>
                    <Input
                      placeholder="Digite o número"
                      {...register("number")}
                    />
                  </div>
                  <div>
                    <Label>Complemento</Label>
                    <Input
                      placeholder="Digite o complemento"
                      {...register("complement")}
                    />
                  </div>
                  <div className="flex flex-col gap-2">
                    <Label>Telefone</Label>
                    <Input
                      type="text"
                      value={phoneValue}
                      onChange={(e) => {
                        const formattedPhone = formatPhone(e.target.value);
                        setPhoneValue(formattedPhone);
                        setValue("phone", formattedPhone, {
                          shouldValidate: true,
                        });
                      }}
                      placeholder="(00) 00000-0000"
                      maxLength={15}
                    />
                    {errors.phone && (
                      <span className="text-sm text-red-600">
                        {errors.phone.message}
                      </span>
                    )}
                  </div>
                  <div className="flex flex-col gap-2">
                    <Label>Celular</Label>
                    <Input
                      type="text"
                      value={mobileValue}
                      onChange={(e) => {
                        const formattedPhone = formatPhone(e.target.value);
                        setMobileValue(formattedPhone);
                        setValue("mobile", formattedPhone, {
                          shouldValidate: true,
                        });
                      }}
                      placeholder="(00) 00000-0000"
                      maxLength={15}
                    />
                    {errors.mobile && (
                      <span className="text-sm text-red-600">
                        {errors.mobile.message}
                      </span>
                    )}
                  </div>

                  <div>
                    <Label>Cargo</Label>
                    <select
                      {...register("positionId")}
                      className="flex flex-col rounded-md border p-2 w-full"
                    >
                      <option value="">Selecione o Cargo</option>
                      {positions.map((position) => (
                        <option key={position.id} value={position.id}>
                          {position.title}
                        </option>
                      ))}
                    </select>
                    {errors.positionId && (
                      <span className="text-red-600">
                        {errors.positionId.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label>CBO</Label>
                    <div className="border border-neutral-400 flex items-center gap-2 rounded-md px-2 py-1 bg-white shadow-sm">
                      <Search className="text-neutral-500 w-5 h-5" />
                      <input
                        type="text"
                        placeholder="Pesquisar CBO..."
                        value={searchCbo}
                        onChange={(e) => setSearchCbo(e.target.value)}
                        className="border-none w-full outline-none text-sm placeholder:text-neutral-400"
                      />
                    </div>
                    <select
                      {...register("cboId")}
                      className="flex flex-col rounded-md border p-2 w-full"
                    >
                      <option value="">Selecione o CBO</option>
                      {filteredCbos.map((cbo) => (
                        <option key={cbo.id} value={cbo.id}>
                          {cbo.title} - {cbo.code}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="flex flex-col gap-2">
                    <Label>Graduação</Label>
                    <select
                      {...register("education")}
                      className="flex flex-col rounded-md border p-2"
                    >
                      <option value="">Selecione</option>
                      <option value="Ensino Fundamental Incompleto">
                        Ensino Fundamental Incompleto
                      </option>
                      <option value="Ensino Fundamental Completo">
                        Ensino Fundamental Completo
                      </option>
                      <option value="Fundamental I incompleto">
                        Ensino Fundamental I incompleto
                      </option>
                      <option value="Fundamental I completo">
                        Ensino Fundamental I completo
                      </option>
                      <option value="Fundamental II incompleto">
                        Ensino Fundamental II incompleto
                      </option>
                      <option value="Fundamental II completo">
                        Ensino Fundamental II completo
                      </option>
                      <option value="Ensino Médio Incompleto">
                        Ensino Médio Incompleto
                      </option>
                      <option value="Ensino Médio Completo">
                        Ensino Médio Completo
                      </option>
                      <option value="Ensino Superior Incompleto">
                        Ensino Superior Incompleto
                      </option>
                      <option value="Ensino Superior Completo">
                        Ensino Superior Completo
                      </option>
                      <option value="Pós-graduação">Pós-graduação</option>
                      <option value="Mestrado">Mestrado</option>
                      <option value="Doutorado">Doutorado</option>
                      <option value="Ph.D">Ph.D</option>
                    </select>
                  </div>

                  <Button
                    type="submit"
                    className="bg-realizaBlue"
                    disabled={isLoading}
                  >
                    {isLoading ? (
                      <Oval
                        visible={true}
                        height={20}
                        width={20}
                        color="#fff"
                        ariaLabel="oval-loading"
                      />
                    ) : (
                      "Cadastrar"
                    )}
                  </Button>
                </form>
              </div>
            </ScrollArea>
          </DialogHeader>
        </DialogContent>
      </Dialog>

      <Dialog
        open={isForeignerEmployeeModalOpen}
        onOpenChange={setIsForeignerEmployeeModalOpen}
      >
        <DialogContent
          style={{ backgroundImage: `url(${bgModalRealiza})` }}
          className="max-w-[90vw] sm:max-w-[45vw] md:max-w-[45vw]"
        >
          <DialogHeader>
            <DialogTitle className="text-white">
              Cadastrar colaborador Estrangeiro
            </DialogTitle>
            <ScrollArea className="h-[75vh]">
              <div>
                <form
                  action=""
                  className="flex flex-col gap-5"
                  onSubmit={handleSubmit(
                    handleSubmitForeignerEmployee,
                    (errors) => {
                      console.log("Erros detectados:", errors);
                    }
                  )}
                >
                  <div>
                    <Label className="text-white">Nome</Label>
                    <Input
                      type="text"
                      placeholder="Digite seu nome"
                      {...register("name", { required: "Nome é obrigatório" })}
                    />
                    {errors.name && (
                      <span className="text-sm text-red-600">
                        {errors.name.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">Sobrenome</Label>
                    <Input
                      type="text"
                      placeholder="Digite seu sobrenome"
                      {...register("surname", {
                        required: "Sobrenome é obrigatório",
                      })}
                    />
                    {errors.surname && (
                      <span className="text-sm text-red-600">
                        {errors.surname.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">Data de nascimento</Label>
                    <Input
                      type="date"
                      placeholder="Digite a data de nascimento"
                      {...register("birthDate", {
                        required: "Data de nascimento é obrigatória",
                      })}
                    />
                    {errors.birthDate && (
                      <span className="text-sm text-red-600">
                        {errors.birthDate.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">Estado civil</Label>
                    <select
                      {...register("maritalStatus", {
                        required: "Estado civil é obrigatório",
                      })}
                      className="flex flex-col rounded-md border p-2 w-full"
                    >
                      <option value="">Selecione</option>
                      <option value="CASADO">Casado</option>
                      <option value="SOLTEIRO">Solteiro</option>
                      <option value="DIVORCIADO">Divorciado</option>
                      <option value="VIUVO">Viúvo </option>
                      <option value="SEPARADO_JUDICIALMENTE">
                        Separado judicialmente{" "}
                      </option>
                      <option value="UNIAO_ESTAVEL">União estável</option>
                    </select>
                    {errors.maritalStatus && (
                      <span className="text-sm text-red-600">
                        {errors.maritalStatus.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">Tipo de contrato</Label>
                    <select
                      {...register("contractType", {
                        required: "Tipo de contrato é obrigatório",
                      })}
                      className="flex flex-col rounded-md border p-2 w-full"
                    >
                      <option value="">Selecione um tipo de contrato</option>
                      <option value="AUTONOMO">Autônomo</option>
                      <option value="AVULSO_SINDICATO">
                        Avulso (Sindicato)
                      </option>
                      <option value="CLT_HORISTA">CLT - Horista</option>
                      <option value="CLT_TEMPO_DETERMINADO">
                        CLT - Tempo Determinado
                      </option>
                      <option value="CLT_TEMPO_INDETERMINADO">
                        CLT - Tempo Indeterminado
                      </option>
                      <option value="COOPERADO">Cooperado</option>
                      <option value="ESTAGIO_BOLSA">Estágio / Bolsa</option>
                      <option value="ESTRANGEIRO_IMIGRANTE">
                        Estrangeiro - Imigrante
                      </option>
                      <option value="ESTRANGEIRO_TEMPORARIO">
                        Estrangeiro - Temporário
                      </option>
                      <option value="INTERMITENTE">Intermitente</option>
                      <option value="JOVEM_APRENDIZ">Jovem Aprendiz</option>
                      <option value="SOCIO">Sócio</option>
                      <option value="TEMPORARIO">Temporário</option>
                    </select>
                    {errors.contractType && (
                      <span className="text-sm text-red-600">
                        {errors.contractType.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">
                      Protocolo RNE/RNM Polícia Federal
                    </Label>
                    <Input
                      type="text"
                      placeholder="Digite o protocolo RNE/RNM"
                      {...register("rneRnmFederalPoliceProtocol")}
                    />
                    {errors.rneRnmFederalPoliceProtocol && (
                      <span className="text-sm text-red-600">
                        {errors.rneRnmFederalPoliceProtocol.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">
                      Data de Entrada no Brasil
                    </Label>
                    <Input type="date" {...register("brazilEntryDate")} />
                    {errors.brazilEntryDate && (
                      <span className="text-sm text-red-600">
                        {errors.brazilEntryDate.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">Passaporte</Label>
                    <Input
                      type="text"
                      placeholder="Digite o número do passaporte"
                      {...register("passport")}
                    />
                    {errors.passport && (
                      <span className="text-sm text-red-600">
                        {errors.passport.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">Salário:</Label>
                    <Input
                      type="text"
                      {...register("salary", {
                        required: "Salário é obrigatório",
                      })}
                      onChange={(e) => {
                        const formattedSalary = formatSalary(e.target.value);
                        setValue("salary", formattedSalary);
                      }}
                      placeholder="000.000,00"
                    />
                    {errors.salary && <span>{errors.salary.message}</span>}
                  </div>
                  <div>
                    <Label className="text-white">Sexo</Label>
                    <select
                      {...register("gender", {
                        required: "Gênero é obrigatório",
                      })}
                      className="flex flex-col rounded-md border p-2 w-full"
                    >
                      <option value="">Selecione</option>
                      <option value="Masculino">Masculino</option>
                      <option value="Feminino">Feminino</option>
                    </select>
                    {errors.gender && (
                      <span className="text-sm text-red-600">
                        {errors.gender.message}
                      </span>
                    )}
                  </div>

                  <div>
                    <Label className="text-white">CEP</Label>
                    <div className="flex w-full gap-2">
                      <Input
                        type="text"
                        value={cepValue}
                        onChange={(e) => {
                          const formattedCEP = formatCEP(e.target.value);
                          setCepValue(formattedCEP);
                          setValue("cep", formattedCEP, {
                            shouldValidate: true,
                          });
                        }}
                        placeholder="00000-000"
                        maxLength={9}
                      />
                      <div
                        onClick={handleCep}
                        className="bg-realizaBlue cursor-pointer rounded-md p-2 text-white hover:bg-gray-600 flex items-center justify-center"
                      >
                        <Search className="w-5 h-5" />
                      </div>
                    </div>
                    {errors.cep && (
                      <span className="text-sm text-red-600">
                        {errors.cep.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">Estado</Label>
                    <Input
                      placeholder="Digite seu estado"
                      {...register("state", {
                        required: "Estado é obrigatório",
                      })}
                    />
                    {errors.state && (
                      <span className="text-sm text-red-600">
                        {errors.state.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">Cidade</Label>
                    <Input
                      placeholder="Digite sua cidade"
                      {...register("city", {
                        required: "Cidade é obrigatória",
                      })}
                    />
                    {errors.city && (
                      <span className="text-sm text-red-600">
                        {errors.city.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">Endereco</Label>
                    <Input
                      placeholder="Digite seu endereço"
                      {...register("address", {
                        required: "Endereço é obrigatório",
                      })}
                    />
                    {errors.address && (
                      <span className="text-sm text-red-600">
                        {errors.address.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">Número</Label>
                    <Input
                      placeholder="Digite o número"
                      {...register("number", {
                        required: "Número é obrigatório",
                      })}
                    />
                    {errors.number && (
                      <span className="text-sm text-red-600">
                        {errors.number.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">Complemento</Label>
                    <Input
                      placeholder="Digite o complemento"
                      {...register("complement")}
                    />
                  </div>
                  <div className="flex flex-col gap-2">
                    <Label className="text-white">Telefone</Label>
                    <Input
                      type="text"
                      value={phoneValue}
                      onChange={(e) => {
                        const formattedPhone = formatPhone(e.target.value);
                        setPhoneValue(formattedPhone);
                        setValue("phone", formattedPhone, {
                          shouldValidate: true,
                        });
                      }}
                      placeholder="(00) 00000-0000"
                      maxLength={15}
                    />
                    {errors.phone && (
                      <span className="text-sm text-red-600">
                        {errors.phone.message}
                      </span>
                    )}
                  </div>
                  <div className="flex flex-col gap-2">
                    <Label className="text-white">Celular</Label>
                    <Input
                      type="text"
                      value={mobileValue}
                      onChange={(e) => {
                        const formattedPhone = formatPhone(e.target.value);
                        setMobileValue(formattedPhone);
                        setValue("mobile", formattedPhone, {
                          shouldValidate: true,
                        });
                      }}
                      placeholder="(00) 00000-0000"
                      maxLength={15}
                    />
                    {errors.mobile && (
                      <span className="text-sm text-red-600">
                        {errors.mobile.message}
                      </span>
                    )}
                  </div>

                  <div>
                    <Label className="text-white">Cargo</Label>
                    <select
                      {...register("positionId", {
                        required: "Cargo é obrigatório",
                      })}
                      className="flex flex-col rounded-md border p-2 w-full"
                    >
                      <option value="">Selecione o Cargo</option>
                      {positions.map((position) => (
                        <option key={position.id} value={position.id}>
                          {position.title}
                        </option>
                      ))}
                    </select>
                    {errors.positionId && (
                      <span className="text-sm text-red-600">
                        {errors.positionId.message}
                      </span>
                    )}
                  </div>
                  <div>
                    <Label className="text-white">CBO</Label>
                    <div className="border border-neutral-400 flex items-center gap-2 rounded-md px-2 py-1 bg-white shadow-sm">
                      <Search className="text-neutral-500 w-5 h-5" />
                      <input
                        type="text"
                        placeholder="Pesquisar CBO..."
                        value={searchCbo}
                        onChange={(e) => setSearchCbo(e.target.value)}
                        className="border-none w-full outline-none text-sm placeholder:text-neutral-400"
                      />
                    </div>
                    <select
                      {...register("cboId")}
                      className="flex flex-col rounded-md border p-2 w-full"
                    >
                      <option value="">Selecione o CBO</option>
                      {filteredCbos.map((cbo) => (
                        <option key={cbo.id} value={cbo.id}>
                          {cbo.title} - {cbo.code}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="flex flex-col gap-2">
                    <Label className="text-white">Graduação</Label>
                    <select
                      {...register("education", {
                        required: "Escolaridade é obrigatória",
                      })}
                      className="flex flex-col rounded-md border p-2"
                    >
                      <option value="">Selecione</option>
                      <option value="Ensino Fundamental Incompleto">
                        Ensino Fundamental Incompleto
                      </option>
                      <option value="Ensino Fundamental Completo">
                        Ensino Fundamental Completo
                      </option>
                      <option value="Fundamental I incompleto">
                        Ensino Fundamental I incompleto
                      </option>
                      <option value="Fundamental I completo">
                        Ensino Fundamental I completo
                      </option>
                      <option value="Fundamental II incompleto">
                        Ensino Fundamental II incompleto
                      </option>
                      <option value="Fundamental II completo">
                        Ensino Fundamental II completo
                      </option>
                      <option value="Ensino Médio Incompleto">
                        Ensino Médio Incompleto
                      </option>
                      <option value="Ensino Médio Completo">
                        Ensino Médio Completo
                      </option>
                      <option value="Ensino Superior Incompleto">
                        Ensino Superior Incompleto
                      </option>
                      <option value="Ensino Superior Completo">
                        Ensino Superior Completo
                      </option>
                      <option value="Pós-graduação">Pós-graduação</option>
                      <option value="Mestrado">Mestrado</option>
                      <option value="Doutorado">Doutorado</option>
                      <option value="Ph.D">Ph.D</option>
                    </select>
                    {errors.education && (
                      <span className="text-sm text-red-600">
                        {errors.education.message}
                      </span>
                    )}
                  </div>

                  <Button
                    type="submit"
                    className="bg-realizaBlue"
                    disabled={isLoading}
                  >
                    {isLoading ? (
                      <Oval
                        visible={true}
                        height={20}
                        width={20}
                        color="#fff"
                        ariaLabel="oval-loading"
                      />
                    ) : (
                      "Cadastrar"
                    )}
                  </Button>
                </form>
              </div>
            </ScrollArea>
          </DialogHeader>
        </DialogContent>
      </Dialog>
      <Dialog open={showSuccessModal} onOpenChange={setShowSuccessModal}>
        <DialogContent className="max-w-[700px] p-6 text-center">
          <DialogHeader>
            <DialogTitle className="flex items-center justify-start gap-2 text-lg text-realizaBlue">
              <IdCard className="w-5 h-5" />
              Cadastro de colaborador concluído
            </DialogTitle>
          </DialogHeader>
          <div className="flex ">
            <div className="flex flex-col items-center justify-center gap-4 py-4 w-[100%]">
              <p className="text-md text-gray-700 text-start">
                Colaborador <strong>"{employeeName}"</strong> adicionado com
                sucesso à aba de colaboradores. Se deseja visualizar este
                colaborador, acesse a aba de colaboradores vinculados ao
                fornecedor <strong>"{supplierName}"</strong>.
              </p>
            </div>
          </div>
          <DialogFooter className="flex justify-center">
            <Button
              onClick={() => setShowSuccessModal(false)}
              className="bg-realizaBlue"
            >
              Fechar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}