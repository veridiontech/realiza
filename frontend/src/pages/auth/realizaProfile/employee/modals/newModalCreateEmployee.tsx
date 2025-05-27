import bgModalRealiza from "@/assets/modalBG.jpeg";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useSupplier } from "@/context/Supplier-context";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Search } from "lucide-react";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { Oval } from "react-loader-spinner";
import { toast } from "sonner";
import { z } from "zod";
import { useEffect } from "react";

function validarCPF(cpf: string): boolean {
  const cleaned = cpf.replace(/\D/g, "");
  if (!cleaned || cleaned.length !== 11 || /^(\d)\1+$/.test(cleaned)) return false;

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

const createNewEmployeeFormSchema = z.object({
  contractType: z.string(),
  name: z.string(),
  surname: z.string(),
  cpf: z.string()
    .nonempty("CPF é obrigatório")
    .regex(cpfRegex, "Formato inválido. Use 000.000.000-00")
    .refine(validarNumerosRepetidos, { message: "CPF com todos os dígitos repetidos" })
    .refine(validarCPF, { message: "CPF inválido" }),
  salary: z.string().regex(/^\d{1,3}(\.\d{3})*,\d{2}$/, "Salário inválido"),
  gender: z.string(),
  maritalStatus: z.string(),
  cep: z.string()
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
  state: z.string(),
  city: z.string(),
  address: z.string(),
  number: z.string(),
  complement: z.string(),
  phone: z.string()
    .nonempty("Celular é obrigatório")
    .regex(phoneRegex, "Telefone inválido, use o formato (XX) XXXXX-XXXX")
    .refine(validarNumerosRepetidos, { message: "Telefone inválido: não pode ter números repetidos" }),
  mobile: z.string()
    .nonempty("Celular obrigatório")
    .refine(validarNumerosRepetidos, { message: "Celular com dígitos repetidos" })
    .refine((val) => phoneRegex.test(val), { message: "Celular inválido" }),
  position: z.string(),
  education: z.string(),
  cboId: z.string().optional(),
  admissionDate: z.string().nonempty("Data de admissão é obrigatória"),
  birthDate: z.string(),
});


type CreateNewEmpoloyeeFormSchema = z.infer<typeof createNewEmployeeFormSchema>;
export function NewModalCreateEmployee() {
  const [isLoading, setIsLoading] = useState(false);
  const { supplier } = useSupplier();
  const [cbos, setCbos] = useState<{ id: string; title: string; code: string }[]>([]);
  const [searchCbo, setSearchCbo] = useState("");
  const [isOpen, setIsOpen] = useState(false);
  const [cepValue, setCepValue] = useState("");
  const [phoneValue, setPhoneValue] = useState("");
  const [mobileValue, setMobileValue] = useState("");
  const [cpfValue, setCpfValue] = useState("");

  console.log("Id Supplier: ", supplier);


  const {
    register,
    handleSubmit,
    getValues,
    setValue,
    reset,
    formState: { errors },
  } = useForm<CreateNewEmpoloyeeFormSchema>({
    resolver: zodResolver(createNewEmployeeFormSchema),
    mode: "onSubmit",
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
      handleCep(); // busca automática
    }
  }, [cepValue]);


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
      return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
    } else {
      return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7, 11)}`;
    }
  };

  const formatCEP = (value: string) => {
    return value.replace(/\D/g, "").replace(/(\d{5})(\d)/, "$1-$2").slice(0, 9);
  };


  const formatSalary = (value: string) => {
    const number = Number(value.replace(/\D/g, "")) / 100;
    return number.toLocaleString("pt-BR", {
      style: "currency",
      currency: "BRL",
      minimumFractionDigits: 2,
    }).replace("R$", "").trim();
  };

  const normalizeSalary = (value: string) => {
    return parseFloat(value.replace(/\./g, '').replace(',', '.'));
  };

  const onSubmit = async (data: CreateNewEmpoloyeeFormSchema) => {
    setIsLoading(true);

    const payload = {
      ...data,
      supplier: supplier?.idProvider,
      salary: normalizeSalary(data.salary)
    };

    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");

      await axios.post(`${ip}/employee/brazilian`, payload, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      });

      toast.success("Sucesso ao cadastrar novo colaborador!");

      // Reset do form e valores controlados
      reset();
      setCpfValue("");
      setCepValue("");
      setPhoneValue("");
      setMobileValue("");

      setIsOpen(false);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        toast.error(`Erro ${err.response?.status}: ${err.response?.data?.message || "Erro ao cadastrar colaborador"}`);
        console.error("Erro Axios:", err.response?.data);
      } else {
        toast.error("Erro inesperado. Tente novamente.");
        console.error("Erro desconhecido:", err);
      }
    } finally {
      setIsLoading(false);
    }
  };


  const handleCep = async () => {
    try {
      const cleanedCep = cepValue.replace(/\D/g, "");
      const res = await axios.get(`https://viacep.com.br/ws/${cleanedCep}/json/`);
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
      console.error(err);
    }
  };



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

    fetchCbos();
  }, []);



  const setValuesCep = (data: propsCep) => {
    setValue("city", data.city),
      setValue("state", data.state),
      setValue("address", data.address);
  };

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        <Button className="hidden md:block bg-realizaBlue border border-white rounded-md">Cadastrar novo colaborador +</Button>
      </DialogTrigger>
      <DialogTrigger asChild>
        <Button className="md:hidden bg-realizaBlue">+</Button>
      </DialogTrigger>
      <DialogContent
        style={{ backgroundImage: `url(${bgModalRealiza})` }}
        className="max-w-[90vw] sm:max-w-[45vw] md:max-w-[45vw]"
      >
        <DialogHeader>
          <DialogTitle className="text-white">Cadastrar colaborador</DialogTitle>
          <ScrollArea className="h-[75vh]">
            <div>
              <form
                action=""
                className="flex flex-col gap-5"
                onSubmit={handleSubmit(onSubmit, (errors) => {
                  console.log("Erros detectados:", errors);
                })}
              >
                <div>
                  <Label className="text-white">Nome</Label>
                  <Input type="text"
                    placeholder="Digite seu nome"
                    {...register("name")} />
                </div>
                <div>
                  <Label className="text-white">Sobrenome</Label>
                  <Input type="text"
                    placeholder="Digite seu sobrenome"
                    {...register("surname")} />
                </div>
                <div>
                  <Label className="text-white">Data de nascimento</Label>
                  <Input type="date"
                    placeholder="Digite a data de nascimento"
                    {...register("birthDate")} />
                </div>
                <div>
                  <Label className="text-white">Estado civil</Label>
                  <select
                    {...register("maritalStatus")}
                    className="flex flex-col rounded-md border p-2 w-full"
                  >
                    <option value="">Selecione</option>
                    <option value="CASADO">Casado</option>
                    <option value="SOLTEIRO">Solteiro</option>
                    <option value="DIVORCIADO">Divorciado</option>
                    <option value="VIUVO">Viúvo </option>
                    <option value="SEPARADO_JUDICIALMENTE">Separado judicialmente </option>
                    <option value="UNIAO_ESTAVEL">União estável</option>
                  </select>
                </div>
                <div>
                  <Label className="text-white">Tipo de contrato</Label>
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
                  <Label className="text-white">CPF</Label>
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
                    <span className="text-sm text-red-600">{errors.cpf.message}</span>
                  )}
                </div>
                <div>
                  <Label className="text-white">Data de admissão</Label>
                  <Input type="date" {...register("admissionDate")} />
                </div>
                <div>
                  <Label className="text-white">Salário:</Label>
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
                  <Label className="text-white">Sexo</Label>
                  <select
                    {...register("gender")}
                    className="flex flex-col rounded-md border p-2 w-full"
                  >
                    <option value="">Selecione</option>
                    <option value="Masculino">Masculino</option>
                    <option value="Feminino">Feminino</option>
                  </select>
                  {errors.gender && (
                    <span className="text-red-600">{errors.gender.message}</span>
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
                        setValue("cep", formattedCEP, { shouldValidate: true });
                      }}
                      placeholder="00000-000"
                      maxLength={9}
                    />
                    <div
                      onClick={handleCep}
                      className="bg-realizaBlue cursor-pointer rounded-md p-2 text-white hover:bg-gray-600 flex items-center justify-center"
                    >

                    </div>
                  </div>
                  {errors.cep && (
                    <span className="text-sm text-red-600">{errors.cep.message}</span>
                  )}
                </div>
                <div>
                  <Label className="text-white">Estado</Label>
                  <Input
                    placeholder="Digite seu estado" {...register("state")} />
                </div>
                <div>
                  <Label className="text-white">Cidade</Label>
                  <Input
                    placeholder="Digite sua cidade"
                    {...register("city")} />
                </div>
                <div>
                  <Label className="text-white">Endereco</Label>
                  <Input
                    placeholder="Digite seu endereço"
                    {...register("address")} />
                </div>
                <div>
                  <Label className="text-white">Número</Label>
                  <Input
                    placeholder="Digite o número"
                    {...register("number")} />
                </div>
                <div>
                  <Label className="text-white">Complemento</Label>
                  <Input
                    placeholder="Digite o complemento"
                    {...register("complement")} />
                </div>
                <div className="flex flex-col gap-2">
                  <Label className="text-white">Telefone</Label>
                  <Input
                    type="text"
                    value={phoneValue}
                    onChange={(e) => {
                      const formattedPhone = formatPhone(e.target.value);
                      setPhoneValue(formattedPhone);
                      setValue("phone", formattedPhone, { shouldValidate: true });
                    }}
                    placeholder="(00) 00000-0000"
                    maxLength={15}
                  />
                  {errors.phone && (
                    <span className="text-sm text-red-600">{errors.phone.message}</span>
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
                      setValue("mobile", formattedPhone, { shouldValidate: true });
                    }}
                    placeholder="(00) 00000-0000"
                    maxLength={15}
                  />
                  {errors.mobile && (
                    <span className="text-sm text-red-600">{errors.mobile.message}</span>
                  )}
                </div>

                <div>
                  <Label className="text-white">Cargo</Label>
                  <Input
                    placeholder="Digite o cargo"
                    {...register("position")} />
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
                    <option value="Pós-graduação">
                      Pós-graduação
                    </option>
                    <option value="Mestrado">
                      Mestrado
                    </option>
                    <option value="Doutorado">
                      Doutorado
                    </option>
                    <option value="Ph.D">
                      Ph.D
                    </option>
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
  );
}  
