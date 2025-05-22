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

interface propsCep {
  address: string;
  city: string;
  state: string;
}

const createNewEmployeeFormSchema = z.object({
  contractType: z.string(),
  name: z.string(),
  surname: z.string(),
  // email: z.string(),
  cpf: z.string(),
  salary: z.string().regex(/^\d{1,3}(\.\d{3})*,\d{2}$/),
  gender: z.string(),
  maritalStatus: z.string(),
  cep: z.string(),
  state: z.string(),
  city: z.string(),
  address: z.string(),
  number: z.string(),
  complement: z.string(),
  phone: z.string().optional(),
  mobile: z.string(),
  position: z.string(),
  // situation: z.string(),
  education: z.string(),
  cboId: z.string().optional(),
  // platformAccess: z.string(),
  // rg: z.string(),
  admissionDate: z.string().nonempty("Data de admissão é obrigatória"),
  birthDate: z.string(),
});

type CreateNewEmpoloyeeFormSchema = z.infer<typeof createNewEmployeeFormSchema>;
export function NewModalCreateEmployee() {
  const [cep, setCep] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { supplier } = useSupplier();
  const [cbos, setCbos] = useState<{ id: string; title: string; code: string }[]>([]);
  const [searchCbo, setSearchCbo] = useState("");
  const [isOpen, setIsOpen] = useState(false);

  console.log("Id Supplier: ", supplier);


  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<CreateNewEmpoloyeeFormSchema>({
    resolver: zodResolver(createNewEmployeeFormSchema),
  });

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
      .replace(/(\d{3})(\d{2})$/, "$1-$2");
  };

  // const formatRG = (value: string) => {
  //   return value
  //     .replace(/\D/g, "")
  //     .replace(/(\d{2})(\d)/, "$1.$2")
  //     .replace(/(\d{3})(\d)/, "$1.$2")
  //     .replace(/(\d{3})(\d{1})$/, "$1-$2");
  // };

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
    console.log("Enviando dados:", payload);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.post(`${ip}/employee/brazilian`, payload,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
      );
      console.log("Sucesso na requisição!", response.data);
      toast.success("Sucesso ao cadastrar novo usuário");
      setIsOpen(false);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        console.error("Erro Axios:", err.response?.status, err.response?.data);
        toast.error(
          `Erro ${err.response?.status}: ${err.response?.data?.message || "Erro ao cadastrar usuário"}`,
        );
      } else {
        console.error("Erro desconhecido:", err);
        toast.error("Erro inesperado, tente novamente");
        setIsOpen(false);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleCep = async () => {
    try {
      const res = await axios.get(`https://viacep.com.br/ws/${cep}/json/`);
      if (res.data) {
        setValuesCep({
          address: res.data.logradouro,
          city: res.data.localidade,
          state: res.data.uf,
        });
      }
      console.log(res.data);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    const fetchCbos = async () => {
      try {
        const tokenFromStorage = localStorage.getItem("tokenClient");
        const response = await axios.get(`${ip}/cbo`, {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        });

        setCbos(response.data); // Certifique-se que a API retorna uma lista aqui
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
                  <Input type="text" {...register("name")} />
                </div>
                <div>
                  <Label className="text-white">Sobrenome</Label>
                  <Input type="text" {...register("surname")} />
                </div>
                <div>
                  <Label className="text-white">Data de nascimento</Label>
                  <Input type="date" {...register("birthDate")} />
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
                  <Label className="text-white">CPF:</Label>
                  <Input
                    type="text"
                    {...register("cpf")}
                    onChange={(e) => {
                      const formattedCPF = formatCPF(e.target.value);
                      setValue("cpf", formattedCPF);
                    }}
                    placeholder="000.000.000-00"
                    maxLength={14}
                  />
                  {errors.cpf && <span>{errors.cpf.message}</span>}
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

                <div className="flex flex-col gap-1">
                  <div>
                    <Label className="text-white">CEP</Label>
                    <div className="flex w-full gap-2">
                      <Input
                        {...register("cep")}
                        onChange={(e) => setCep(e.target.value)}
                      />
                      <div
                        className="bg-realizaBlue cursor-pointer rounded-md p-2 text-white hover:bg-gray-600 flex items-center justify-center"
                        onClick={handleCep}
                      >
                        <Search />
                      </div>
                    </div>
                  </div>
                </div>
                <div>
                  <Label className="text-white">Estado</Label>
                  <Input {...register("state")} />
                </div>
                <div>
                  <Label className="text-white">Cidade</Label>
                  <Input {...register("city")} />
                </div>
                <div>
                  <Label className="text-white">Endereco</Label>
                  <Input {...register("address")} />
                </div>
                <div>
                  <Label className="text-white">Número</Label>
                  <Input {...register("number")} />
                </div>
                <div>
                  <Label className="text-white">Complemento</Label>
                  <Input {...register("complement")} />
                </div>
                <div>
                  <Label className="text-white">Telefone</Label>
                  <Input {...register("phone")} />
                </div>
                <div>
                  <Label className="text-white">Celular</Label>
                  <Input {...register("mobile")} />
                </div>
                <div>
                  <Label className="text-white">Cargo</Label>
                  <Input {...register("position")} />
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
                {/* <div>
                  <Label className="text-white">Situação</Label>
                  <select
                    {...register("situation")}
                    className="flex flex-col rounded-md border p-2 w-full"
                  >
                    <option value="">Selecione uma situação</option>
                    <option value="ALOCADO">Alocado</option>
                    <option value="DESALOCADO">Desalocado</option>
                    <option value="DEMITIDO">Demitido</option>
                    <option value="AFASTADO">Afastado</option>
                    <option value="LICENCA_MATERNIDADE">Licença Maternidade</option>
                    <option value="LICENCA_MEDICA">Licença Médica</option>
                    <option value="LICENCA_MILITAR">Licença Militar</option>
                    <option value="FERIAS">Férias</option>
                    <option value="ALISTAMENTO_MILITAR">Alistamento Militar</option>
                    <option value="APOSENTADORIA_POR_INVALIDEZ">
                      Aposentadoria por Invalidez
                    </option>
                  </select>
                </div> */}

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
