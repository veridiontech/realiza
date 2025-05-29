import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { useEffect, useState } from "react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useClient } from "@/context/Client-Provider";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Search } from "lucide-react";
import { useForm } from "react-hook-form";
import { Oval } from "react-loader-spinner";
import { toast } from "sonner";
import { z } from "zod";
import bgModalRealiza from "@/assets/modalBG.jpeg";

const cnpjRegex = /^\d{2}\.\d{3}\.\d{3}\/\d{4}\-\d{2}$/;
const cepRegex = /^\d{5}-?\d{3}$/;
const phoneRegex = /^\(?\d{2}\)?\s?\d{4,5}-?\d{4}$/;

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

// Função para validar se o valor não tem dígitos repetidos
function validarNumerosRepetidos(valor: string) {
  const digits = valor.replace(/\D/g, "");
  return !/^(\d)\1+$/.test(digits);
}

const newBranchFormSchema = z.object({
  cnpj: z.string()
    .nonempty("CNPJ é obrigatório")
    .regex(cnpjRegex, "CNPJ inválido, use o formato XX.XXX.XXX/XXXX-XX")
    .refine(validarNumerosRepetidos, { message: "CNPJ inválido: não pode ter números repetidos" }),
  name: z.string().min(1, "O nome da filial é obrigatório"),
  // email: z.string().email("Insira um email válido"),
  cep: z.string()
    .nonempty("CEP é obrigatório")
    .regex(cepRegex, "CEP inválido, use o formato 12345-678")
    .refine(async (cep) => await validarCEPExiste(cep), {
      message: "CEP não encontrado",
    }),
  country: z.string().min(1, "O país é obrigatório."),
  state: z.string().min(1, "O estado é obrigatório."),
  city: z.string().min(1, "A cidade é obrigatória."),
  address: z.string().min(1, "O endereço é obrigatório."),
  number: z.string().nonempty("Número é obrigatório"),
  telephone: z.string()
    .nonempty("Celular é obrigatório")
    .regex(phoneRegex, "Telefone inválido, use o formato (XX) XXXXX-XXXX")
    .refine(validarNumerosRepetidos, { message: "Telefone inválido: não pode ter números repetidos" }),
});

type NewBranchFormSchema = z.infer<typeof newBranchFormSchema>;

export function AddNewBranch() {
  const { client } = useClient();
  const [loading, setLoading] = useState(false);
  const [cnpjValue, setCnpjValue] = useState("");
  const [cepValue, setCepValue] = useState("");
  const [phoneValue, setPhoneValue] = useState("");
  const [razaoSocial, setRazaoSocial] = useState<string | null>(null);
  const [isOpen, setIsOpen] = useState(false);

  const {
    register,
    handleSubmit,
    getValues,
    setValue,
    reset,
    formState: { errors },
  } = useForm<NewBranchFormSchema>({
    resolver: zodResolver(newBranchFormSchema),
    mode: "onSubmit",
  });

  useEffect(() => {
    const rawCNPJ = getValues("cnpj") || "";
    const formatted = formatCNPJ(rawCNPJ);
    setCnpjValue(formatted);
  }, [getValues]);

  useEffect(() => {
    const rawCEP = getValues("cep") || "";
    const rawPhone = getValues("telephone") || "";

    setCepValue(formatCEP(rawCEP));
    setPhoneValue(formatPhone(rawPhone));
  }, [getValues]);

  const handleCnpj = async () => {
    const cnpj = getValues("cnpj").replace(/\D/g, "");
    if (cnpj.length !== 14) {
      toast.error("CNPJ inválido");
      return;
    }
    try {
      const res = await axios.get(`https://open.cnpja.com/office/${cnpj}`);
      const razao = res.data.company.name || "Razão social não encontrada";
      const cep = res.data.address.zip;
      const city = res.data.address.city;
      const address = res.data.address.street;
      const country = res.data.address.country.name;
      const state = res.data.address.state;
      const number = res.data.address.number;

      setValue("number", number);
      setValue("state", state);
      setValue("country", country);
      setValue("address", address);
      setValue("cep", cep);
      setValue("city", city);
      setValue("name", razao);
      setRazaoSocial(razao);
      toast.success("Sucesso ao buscar CNPJ");
    } catch (err) {
      setRazaoSocial(null);
      toast.error("Erro ao buscar CNPJ");
      console.error("Erro ao buscar CNPJ:", err);
    }
  };

  const onSubmit = async (data: NewBranchFormSchema) => {
    const payload = {
      ...data,
      client: client?.idClient,
    };
    setLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.post(`${ip}/branch`, payload, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      toast.success("Sucesso ao criar filial");
      setIsOpen(false);
      reset();
      setCnpjValue("");
      setCepValue("");
      setPhoneValue("");
      setRazaoSocial(null);

    } catch (err: any) {
      if (err.response && err.response.data) {
        const mensagemBackend =
          err.response.data.message ||
          err.response.data.error ||
          "Erro inesperado no servidor";
        toast.error(mensagemBackend);
      } else if (err.request) {
        toast.error("Não foi possível se conectar ao servidor.");
      } else {
        toast.error("Erro desconhecido ao processar requisição.");
      }
      console.error("Erro ao criar filial:", err);
    } finally {
      setLoading(false);
    }
  };

  const formatCNPJ = (value: string) => {
    const digits = value.replace(/\D/g, "");
    let formatted = digits;
    if (digits.length > 2) formatted = digits.replace(/^(\d{2})(\d+)/, "$1.$2");
    if (digits.length > 5) formatted = formatted.replace(/^(\d{2})\.(\d{3})(\d+)/, "$1.$2.$3");
    if (digits.length > 8) formatted = formatted.replace(/^(\d{2})\.(\d{3})\.(\d{3})(\d+)/, "$1.$2.$3/$4");
    if (digits.length > 12)
      formatted = formatted.replace(/^(\d{2})\.(\d{3})\.(\d{3})\/(\d{4})(\d{0,2})/, "$1.$2.$3/$4-$5");
    return formatted;
  };

  const formatCEP = (value: string) => {
    return value.replace(/\D/g, "").replace(/(\d{5})(\d)/, "$1-$2").slice(0, 9);
  };

  const formatPhone = (value: string) => {
    const digits = value.replace(/\D/g, "");
    if (digits.length <= 2) return digits;
    else if (digits.length <= 6) return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
    else if (digits.length <= 10)
      return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
    else
      return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7, 11)}`;
  };

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        <Button className="bg-realizaBlue hidden md:block">+</Button>
      </DialogTrigger>
      <DialogTrigger asChild>
        <Button className="bg-realizaBlue md:hidden">+</Button>
      </DialogTrigger>
      <DialogContent style={{ backgroundImage: `url(${bgModalRealiza})` }}>
        <DialogHeader>
          <DialogTitle className="text-white">Cadastro de filial</DialogTitle>
        </DialogHeader>
        <ScrollArea className="h-[40vh] p-3">
          <form onSubmit={handleSubmit(onSubmit)} className="m-2 flex flex-col gap-5">
            <div className="flex flex-col gap-2">
              <Label className="text-white">CNPJ</Label>
              <div className="flex items-center gap-2">
                <Input
                  type="text"
                  value={cnpjValue}
                  onChange={(e) => {
                    const formatted = formatCNPJ(e.target.value);
                    setCnpjValue(formatted);
                    setValue("cnpj", formatted, { shouldValidate: true });
                  }}
                  placeholder="00.000.000/0000-00"
                  maxLength={18}
                />
                <div
                  onClick={handleCnpj}
                  className="bg-realizaBlue cursor-pointer rounded-lg p-2 hover:bg-gray-500"
                >
                  <Search className="text-white" />
                </div>
              </div>
              {errors.cnpj && (
                <span className="text-sm text-red-600">{errors.cnpj.message}</span>
              )}
              {razaoSocial && (
                <p className="mt-1 text-sm text-white">
                  Razão social: <strong>{razaoSocial}</strong>
                </p>
              )}
            </div>

            {/* <div>
              <Label className="text-white">Email</Label>
              <Input type="email" placeholder="Digite seu e-mail" {...register("email")} />
              {errors.email && (
                <span className="text-sm text-red-600">{errors.email.message}</span>
              )}
            </div> */}

            <div>
              <Label className="text-white">Nome da filial</Label>
              <Input type="text" placeholder="Digite o nome da filial" {...register("name")} />
              {errors.name && (
                <span className="text-sm text-red-600">{errors.name.message}</span>
              )}
            </div>

            <div>
              <Label className="text-white">Razão social</Label>
              <Input type="text" placeholder="Digite a razão social" {...register("name")} />
              {errors.name && (
                <span className="text-sm text-red-600">{errors.name.message}</span>
              )}
            </div>

            <div>
              <Label className="text-white">CEP</Label>
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
              {errors.cep && <span className="text-sm text-red-600">{errors.cep.message}</span>}
            </div>

            <div>
              <Label className="text-white">Cidade</Label>
              <Input type="text" placeholder="Digite sua cidade" {...register("city")} />
              {errors.city && (
                <span className="text-sm text-red-600">{errors.city.message}</span>
              )}
            </div>

            <div>
              <Label className="text-white">Endereço</Label>
              <Input type="text" placeholder="Digite seu endereço" {...register("address")} />
              {errors.address && (
                <span className="text-sm text-red-600">{errors.address.message}</span>
              )}
            </div>

            <div>
              <Label className="text-white">Número</Label>
              <Input type="text" placeholder="Digite o número" {...register("number")} />
              {errors.number && (
                <span className="text-sm text-red-600">{errors.number.message}</span>
              )}
            </div>

            <div>
              <Label className="text-white">País</Label>
              <Input type="text" placeholder="Digite seu país" {...register("country")} />
              {errors.country && (
                <span className="text-sm text-red-600">{errors.country.message}</span>
              )}
            </div>

            <div>
              <Label className="text-white">Estado</Label>
              <Input type="text" placeholder="Digite seu estado" {...register("state")} />
              {errors.state && (
                <span className="text-sm text-red-600">{errors.state.message}</span>
              )}
            </div>

            <div className="flex flex-col gap-2">
              <Label className="text-white">Telefone</Label>
              <Input
                type="text"
                value={phoneValue}
                onChange={(e) => {
                  const formattedPhone = formatPhone(e.target.value);
                  setPhoneValue(formattedPhone);
                  setValue("telephone", formattedPhone, { shouldValidate: true });
                }}
                placeholder="(00) 00000-0000"
                maxLength={15}
              />
              {errors.telephone && (
                <span className="text-sm text-red-600">{errors.telephone.message}</span>
              )}
            </div>

            <div className="flex justify-end">
              {loading ? (
                <Button type="submit" className="bg-realizaBlue">
                  <Oval
                    visible={true}
                    height="30"
                    width="30"
                    color="#34495D"
                    ariaLabel="puff-loading"
                  />
                </Button>
              ) : (
                <Button type="submit" className="bg-realizaBlue">
                  Cadastrar
                </Button>
              )}
            </div>
          </form>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
