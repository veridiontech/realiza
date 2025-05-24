import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "./ui/button";
import { Plus, Search } from "lucide-react";
import { Label } from "./ui/label";
import { Input } from "./ui/input";
import { toast } from "sonner";
import { useEffect, useState } from "react";
import axios from "axios";
import { propsCompanyData } from "@/types/interfaces";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { ip } from "@/utils/ip";
import { Oval } from "react-loader-spinner";

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

function validarNumerosRepetidos(valor: string) {
  const digits = valor.replace(/\D/g, "");
  return !/^(\d)\1+$/.test(digits);
}

const cepRegex = /^\d{5}-\d{3}$/;
// Aceita CNPJ formatado ou apenas números puros com 14 dígitos
const cnpjRegex = /^(\d{14}|\d{2}\.\d{3}\.\d{3}\/\d{4}\-\d{2})$/;
const phoneRegex = /^\(\d{2}\)\s?\d{4,5}-\d{4}$/;

const createClienteFormSchema = z.object({
  cnpj: z.string()
    .nonempty("CNPJ é obrigatório")
    .regex(cnpjRegex, "CNPJ inválido, use o formato XX.XXX.XXX/XXXX-XX ou 14 dígitos")
    .refine(validarNumerosRepetidos, { message: "CNPJ inválido: não pode ter números repetidos" }),
  tradeName: z.string().nonempty("Nome fantasia é obrigatório"),
  corporateName: z.string().nonempty("Razão social é obrigatória"),
  email: z.string().email("Email inválido"),
  telephone: z
    .string()
    .nonempty("Celular é obrigatório")
    .regex(phoneRegex, "Telefone inválido, use o formato (XX) XXXXX-XXXX")
    .refine(validarNumerosRepetidos, {
      message: "Telefone inválido: não pode ter números repetidos",
    }),
  cep: z
    .string()
    .nonempty("CEP é obrigatório")
    .regex(cepRegex, "CEP inválido, use o formato 12345-678")
    .refine(async (cep) => await validarCEPExiste(cep), {
      message: "CEP não encontrado",
    }),
  state: z.string().nonempty("Estado é obrigatório"),
  city: z.string().nonempty("Cidade é obrigatória"),
  address: z.string().nonempty("Endereço é obrigatório"),
  number: z.string().nonempty("Número é obrigatório"),
});

type CreateClientFormSchema = z.infer<typeof createClienteFormSchema>;

export function ModalCreateCliente() {
  const [showFirstModal, setShowFirstModal] = useState(false);
  const [showSecondModal, setShowSecondModal] = useState(false);
  const [cnpjData, setCnpjData] = useState<propsCompanyData | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [cepValue, setCepValue] = useState("");
  const [phoneValue, setPhoneValue] = useState("");
  const [cnpjValue, setCnpjValue] = useState("");

  const {
    register,
    handleSubmit,
    getValues,
    setValue,
    reset,
    formState: { errors },
  } = useForm<CreateClientFormSchema>({
    resolver: zodResolver(createClienteFormSchema),
    mode: "onSubmit",
  });

  // Formatar CEP e telefone sempre que o formulário mudar
  useEffect(() => {
    const rawCEP = getValues("cep") || "";
    const rawPhone = getValues("telephone") || "";

    setCepValue(formatCEP(rawCEP));
    setPhoneValue(formatPhone(rawPhone));
  }, [getValues]);

  // Formatar e sincronizar o valor do CNPJ no input e form
  useEffect(() => {
    const rawCNPJ = getValues("cnpj") || "";
    const formatted = formatCNPJ(rawCNPJ);
    setCnpjValue(formatted);
    setValue("cnpj", formatted, { shouldValidate: true });
  }, [getValues, setValue]);

  useEffect(() => {
    if (cnpjData) {
      setValue("corporateName", cnpjData.company.name || "");
      setValue("tradeName", cnpjData.alias || "");
      setValue("email", cnpjData.emails?.[0]?.address || "");
      setValue("telephone", cnpjData.phones?.[0]?.number || "");
      setValue("cep", cnpjData.address.zip || "");
      setValue("state", cnpjData.address.state || "");
      setValue("city", cnpjData.address.city || "");
      setValue("address", cnpjData.address.street || "");
      setValue("number", cnpjData.address.number || "");
      setShowSecondModal(true);
    }
  }, [cnpjData, setValue]);

  // Sanitiza o CNPJ removendo caracteres não numéricos
  const sanitizedCnpj = cnpjValue.replace(/\D/g, "");

  // Busca dados do CNPJ na API
  const handleCnpj = async () => {
    if (sanitizedCnpj.length !== 14) {
      toast.error("CNPJ inválido, deve conter 14 dígitos.");
      return;
    }
    try {
      console.log("CNPJ enviado para a API:", cnpjValue);
      const res = await axios.get(`https://open.cnpja.com/office/${sanitizedCnpj}`);
      setCnpjData(res.data);
      toast.success("CNPJ carregado com sucesso!");
    } catch (err) {
      toast.error("Erro ao buscar CNPJ");
      console.error(err);
    }
  };

  // Envio do formulário para criar cliente
  const createCliente = async (data: CreateClientFormSchema) => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    const payload = {
      ...data,
      cnpj: cnpjValue,
    };
    setIsLoading(true);
    try {
      await axios.post(`${ip}/client`, payload, {
        headers: {
          Authorization: `Bearer ${tokenFromStorage}`,
        },
      });
      toast.success("Sucesso ao criar cliente");

      // Resetar e fechar ambos os modais
      reset();
      setCnpjData(null);
      setShowSecondModal(false);
      setShowFirstModal(false);
      toast.success("Cliente criado com sucesso!");
      reset();
      setCepValue("");
      setPhoneValue("");
      setCnpjValue("");
      setShowSecondModal(false);
    } catch (err: any) {
      if (err.response?.status === 422) {
        toast.warning("CNPJ já cadastrado");
      } else {
        toast.error("Erro ao criar cliente");
      }
    } finally {
      setIsLoading(false);
    }
  };

  // Funções de formatação
  const formatCEP = (value: string) =>
    value.replace(/\D/g, "").replace(/(\d{5})(\d)/, "$1-$2").slice(0, 9);

  const formatPhone = (value: string) => {
    const digits = value.replace(/\D/g, "");
    if (digits.length <= 2) return digits;
    if (digits.length <= 6) return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
    if (digits.length <= 10)
      return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
    return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7, 11)}`;
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

  return (
    <div>
      <Dialog  open={showFirstModal} onOpenChange={setShowFirstModal}>
        <DialogTrigger asChild>
          <Button className="bg-realizaBlue w-[2.1vw] rounded-full">
            <Plus size={24} className="dark:text-white" />
          </Button>
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Cadastrar novo cliente</DialogTitle>
          </DialogHeader>
          <form onSubmit={(e) => e.preventDefault()}>
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
              {errors.cnpj && <span className="text-sm text-red-600">{errors.cnpj.message}</span>}
            </div>
          </form>
        </DialogContent>
      </Dialog>
      <Dialog open={showSecondModal} onOpenChange={setShowSecondModal}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{cnpjData?.company.name || "Detalhes do cliente"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(createCliente)} className="flex flex-col gap-2">
            <div>
              <Label>Nome fantasia</Label>
              <Input {...register("tradeName")} placeholder="Insira o nome fantasia" />
              {errors.tradeName && <span className="text-sm text-red-600">{errors.tradeName.message}</span>}
            </div>
            <div>
              <Label>Razão social</Label>
              <Input {...register("corporateName")} placeholder="Insira a razão social" />
              {errors.corporateName && <span className="text-sm text-red-600">{errors.corporateName.message}</span>}
            </div>
            <div>
              <Label>Email</Label>
              <Input {...register("email")} placeholder="Insira o email" />
              {errors.email && <span className="text-sm text-red-600">{errors.email.message}</span>}
            </div>
            <div>
              <Label>Telefone</Label>
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
              {errors.telephone && <span className="text-sm text-red-600">{errors.telephone.message}</span>}
            </div>
            <div>
              <Label>CEP</Label>
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
              <Label>Estado</Label>
              <Input {...register("state")} placeholder="Insira o estado" />
              {errors.state && <span className="text-sm text-red-600">{errors.state.message}</span>}
            </div>
            <div>
              <Label>Cidade</Label>
              <Input {...register("city")} placeholder="Insira a cidade" />
              {errors.city && <span className="text-sm text-red-600">{errors.city.message}</span>}
            </div>
            <div>
              <Label>Endereço</Label>
              <Input {...register("address")} placeholder="Insira o endereço" />
              {errors.address && <span className="text-sm text-red-600">{errors.address.message}</span>}
            </div>
            <div>
              <Label>Número</Label>
              <Input {...register("number")} placeholder="Insira o número" />
              {errors.number && <span className="text-sm text-red-600">{errors.number.message}</span>}
            </div>
            <div>
              {isLoading ? (
                <Button className="bg-realizaBlue w-full">
                  <Oval
                    visible={true}
                    height="80"
                    width="80"
                    color="#4fa94d"
                    ariaLabel="oval-loading"
                  />
                </Button>
              ) : (
                <Button className="bg-realizaBlue w-full" type="submit">
                  Cadastrar cliente
                </Button>
              )}
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}