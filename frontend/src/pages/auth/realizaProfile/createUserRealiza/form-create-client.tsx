import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { propsClient } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { User } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { TailSpin } from "react-loader-spinner";
import { toast } from "sonner";
import { z } from "zod";

// Regex para telefone (formato válido)
const phoneRegex = /^\(?\d{2}\)?[\s-]?\d{4,5}[-]?\d{4}$/;

// Função para validar CPF
function validarCPF(cpf: string): boolean {
  cpf = cpf.replace(/[^\d]+/g, "");

  if (cpf.length !== 11) return false;

  // Elimina CPFs com todos os dígitos iguais
  if (/^(\d)\1{10}$/.test(cpf)) return false;

  let soma = 0;
  let resto;

  for (let i = 1; i <= 9; i++) {
    soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
  }

  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf.substring(9, 10))) return false;

  soma = 0;
  for (let i = 1; i <= 10; i++) {
    soma += parseInt(cpf.substring(i - 1, i)) * (12 - i);
  }
  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf.substring(10, 11))) return false;

  return true;
}

// Função para validar se telefone tem dígitos todos iguais (ex: 11111111111)
function validarTelefoneRepetido(telefone: string): boolean {
  const digits = telefone.replace(/\D/g, "");
  return !/^(\d)\1+$/.test(digits);
}

const createUserClientSchema = z.object({
  firstName: z.string().nonempty("Insira um nome"),
  surname: z.string().nonempty("Insira um sobrenome"),
  email: z.string().email("Insira um email válido"),
  cpf: z
    .string()
    .nonempty("Insira um CPF")
    .refine((cpf) => validarCPF(cpf), { message: "CPF inválido" }),
  telephone: z
    .string()
    .nonempty("Telefone é obrigatório")
    .regex(phoneRegex, "Telefone inválido, use o formato (XX) XXXXX-XXXX")
    .refine((tel) => validarTelefoneRepetido(tel), {
      message: "Telefone inválido: não pode ter números repetidos",
    }),
  position: z.string().nonempty("Insira um cargo"),
  role: z.string().default("ROLE_CLIENT_RESPONSIBLE"),
  enterprise: z.string().default("CLIENT"),
  idEnterprise: z.string(),
});

type CreateUserClientSchema = z.infer<typeof createUserClientSchema>;

export function FormCreateUserClient() {
  const [userPreview, setUserPreview] = useState({
    firstName: "",
    surname: "",
    email: "",
  });
  const [isLoading, setIsLoading] = useState(false);
  const [clients, setClients] = useState([]);
  const [phoneValue, setPhoneValue] = useState("");
  const [cpfValue, setCpfValue] = useState("");

  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<CreateUserClientSchema>({
    resolver: zodResolver(createUserClientSchema),
  });

  const firstName = watch("firstName");
  const surname = watch("surname");
  const email = watch("email");

  useEffect(() => {
    setUserPreview({ firstName, surname, email });
  }, [firstName, surname, email]);

  const createUser = async (data: CreateUserClientSchema) => {
    setIsLoading(true);
    console.log("enviando dados:", data);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.post(`${ip}/user/manager/new-user`, data, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      toast.success("Sucesso ao criar novo usuário Realiza");
    } catch (err: any) {
      if (err.response?.status === 500) {
        console.log("Erro 500 - Erro interno do servidor:", err.response.data);
      } else {
        console.error("Erro ao criar novo usuário", err);
      }
      toast.error("Erro ao criar um novo usuário, tente novamente");
    } finally {
      setIsLoading(false);
      setPhoneValue("");
      setCpfValue("");
      setValue("telephone", "");
      setValue("cpf", "");
    }
  };

  const getAllClients = async () => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      const res = await axios.get(`${ip}/client`, {
        headers: {
          Authorization: `Bearer ${tokenFromStorage}`,
        },
        params: {
          size: 1000,
        },
      });
      setClients(res.data.content);
    } catch (err) {
      console.log("erro ao puxar todos os cliente:", err);
    }
  };

  useEffect(() => {
    getAllClients();
  }, []);

  const formatPhone = (value: string) => {
    const digits = value.replace(/\D/g, "");
    if (digits.length <= 2) return digits;
    if (digits.length <= 6) return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
    if (digits.length <= 10)
      return `(${digits.slice(0, 2)}) ${digits.slice(
        2,
        digits.length - 4
      )}-${digits.slice(digits.length - 4)}`;
    return value;
  };

  return (
    <form onSubmit={handleSubmit(createUser)} className="flex flex-col gap-6">
      <div className="dark:bg-primary flex items-center justify-between gap-6 rounded-md border p-6 shadow-md">
        <div className="flex w-full flex-col gap-4 lg:w-[65%]">
          <div className="flex w-full flex-col gap-4 md:hidden lg:w-[30%]">
            <div className="flex h-[25vh] w-full items-center justify-center rounded-md bg-gray-300 md:w-[20vw]">
              <User size={94} />
            </div>
            <div className="flex flex-col items-start">
              <p className="font-semibold">
                Nome: {userPreview.firstName} {userPreview.surname}
              </p>
              <p className="font-semibold">Email: {userPreview.email}</p>
            </div>
          </div>

          <h2 className="text-lg font-semibold">Informações Pessoais</h2>
          <div>
            <Label>Selecione um cliente</Label>
            <select
              className="w-full rounded-md border border-neutral-200 p-2"
              {...register("idEnterprise")}
            >
              <option value="" disabled>
                Selecione um cliente
              </option>
              {Array.isArray(clients) &&
                clients.map((client: propsClient) => (
                  <option value={client.idClient} key={client.idClient}>
                    {client.tradeName}
                  </option>
                ))}
            </select>
          </div>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div>
              <Label>Nome</Label>
              <Input
                type="text"
                {...register("firstName")}
                className="dark:bg-white"
              />
              {errors.firstName && (
                <p className="text-red-500">{errors.firstName.message}</p>
              )}
            </div>
            <div>
              <Label>Sobrenome</Label>
              <Input
                type="text"
                {...register("surname")}
                className="dark:bg-white"
              />
              {errors.surname && (
                <p className="text-red-500">{errors.surname.message}</p>
              )}
            </div>
          </div>

          <div>
            <Label>CPF</Label>
            <Input
              type="text"
              {...register("cpf")}
              className="dark:bg-white"
              value={cpfValue}
              onChange={(e) => setCpfValue(e.target.value)}
            />
            {errors.cpf && <p className="text-red-500">{errors.cpf.message}</p>}
          </div>

          <div>
            <Label>Cargo</Label>
            <Input
              type="text"
              {...register("position")}
              className="dark:bg-white"
            />
            {errors.position && (
              <p className="text-red-500">{errors.position.message}</p>
            )}
          </div>

          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div>
              <Label>Email</Label>
              <Input
                type="email"
                {...register("email")}
                className="dark:bg-white"
              />
              {errors.email && (
                <p className="text-red-500">{errors.email.message}</p>
              )}
            </div>
            <div className="flex flex-col gap-2">
              <Label className="text-white">Telefone</Label>
              <Input
                type="text"
                value={phoneValue}
                {...register("telephone")}
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
          </div>
        </div>

        <div className="hidden w-full flex-col gap-4 md:block lg:w-[30%]">
          <div className="flex h-[25vh] w-[20vw] items-center justify-center rounded-md bg-gray-300">
            <User size={94} />
          </div>
          <div className="flex flex-col items-start">
            <p className="font-semibold">
              Nome: {userPreview.firstName} {userPreview.surname}
            </p>
            <p className="font-semibold">Email: {userPreview.email}</p>
          </div>
        </div>
      </div>

      <div className="flex justify-end">
        {isLoading ? (
          <Button type="submit" className="bg-realizaBlue">
            <TailSpin
              visible={true}
              height="80"
              width="80"
              color="#4fa94d"
              ariaLabel="tail-spin-loading"
              radius="1"
              wrapperStyle={{}}
              wrapperClass=""
            />
          </Button>
        ) : (
          <Button type="submit" className="bg-realizaBlue">
            Criar Usuário
          </Button>
        )}
      </div>
    </form>
  );
}
