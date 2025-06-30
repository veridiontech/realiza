import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { PersonStanding } from "lucide-react";
import { useUser } from "@/context/user-provider";
import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

const cpfRegex = /^\d{3}\.\d{3}\.\d{3}-\d{2}$/;
const phoneRegex = /^\(?\d{2}\)?\s?\d{4,5}-?\d{4}$/;

function validarCPF(cpf: string): boolean {
  cpf = cpf.replace(/[^\d]+/g, "");
  if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return false;

  let soma = 0;
  for (let i = 1; i <= 9; i++) soma += parseInt(cpf[i - 1]) * (11 - i);
  let resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf[9])) return false;

  soma = 0;
  for (let i = 1; i <= 10; i++) soma += parseInt(cpf[i - 1]) * (12 - i);
  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  return resto === parseInt(cpf[10]);
}

function validarTelefoneRepetido(telefone: string) {
  const digits = telefone.replace(/\D/g, "");
  return !/^(\d)\1+$/.test(digits);
}

const profileSchema = z.object({
  firstName: z.string(),
  surname: z.string(),
  email: z.string(),
  telephone: z.string()
    .nonempty("Celular é obrigatório")
    .regex(phoneRegex, "Telefone inválido, use o formato (XX) XXXXX-XXXX")
    .refine(validarTelefoneRepetido, {
      message: "Telefone inválido: não pode ter números repetidos"
    }),
  cpf: z.string()
    .nonempty("CPF é obrigatório")
    .regex(cpfRegex, "CPF inválido, use o formato 000.000.000-00")
    .refine(validarCPF, { message: "CPF inválido" }),
  description: z.string(),
});

type ProfileFormData = z.infer<typeof profileSchema>;

export function MiddleSection() {
  const { user } = useUser();

  const [cpfValue, setCpfValue] = useState("");
  const [phoneValue, setPhoneValue] = useState("");

  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
  } = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
  });

  useEffect(() => {
    if (user) {
      setValue("firstName", user.firstName || "");
      setValue("surname", user.surname || "");
      setValue("cpf", user.cpf || "");
      setValue("email", user.email || "");
      setValue("telephone", user.telephone || "");
      setValue("description", user.description || "");

      setCpfValue(user.cpf || "");
      setPhoneValue(user.telephone || "");
    }
  }, [user, setValue]);

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
    if (digits.length <= 2) return digits;
    if (digits.length <= 6) return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
    if (digits.length <= 10) return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
    return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7, 11)}`;
  };

  const onSubmit = async (data: ProfileFormData) => {
    try {
      const token = localStorage.getItem("tokenClient");
      const payload = { ...data, password: "40028922" };
      await axios.put(`${ip}/user/manager/${user?.idUser}`, payload, {
        headers: { Authorization: `Bearer ${token}` },
      });
      window.location.reload();
    } catch (err) {
      console.error("Erro ao atualizar usuário", err);
    }
  };

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="flex flex-col gap-6 rounded-lg p-6 shadow"
    >
      <div className="flex items-center gap-2">
        <PersonStanding className="text-realizaBlue h-6 w-6" />
        <h2 className="text-realizaBlue text-base font-medium">Informações Pessoais</h2>
      </div>

  
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <Label>Nome:</Label>
          <Input {...register("firstName")} placeholder="Digite seu nome" />
          {errors.firstName && <p className="text-sm text-red-500">{errors.firstName.message}</p>}
      </div>

        <div>
          <Label>Sobrenome:</Label>
          <Input {...register("surname")} placeholder="Digite seu sobrenome" />
          {errors.surname && <p className="text-sm text-red-500">{errors.surname.message}</p>}
        </div>

        <div>
          <Label>E-mail:</Label>
          <Input {...register("email")} placeholder="Digite seu e-mail" />
          {errors.email && <p className="text-sm text-red-500">{errors.email.message}</p>}
        </div>

        <div>
          <Label>Telefone:</Label>
          <Input
            type="text"
            value={phoneValue}
            onChange={(e) => {
              const formatted = formatPhone(e.target.value);
              setPhoneValue(formatted);
              setValue("telephone", formatted, { shouldValidate: true });
            }}
            placeholder="(00) 00000-0000"
            maxLength={15}
          />
          {errors.telephone && <p className="text-sm text-red-500">{errors.telephone.message}</p>}
        </div>

      <div className="md:col-span-2">
        <Label>CPF:</Label>
        <Input
          type="text"
          value={cpfValue}
          onChange={(e) => {
            const formatted = formatCPF(e.target.value);
            setCpfValue(formatted);
            setValue("cpf", formatted, { shouldValidate: true });
          }}
          placeholder="000.000.000-00"
          maxLength={14}
        />
        {errors.cpf && <p className="text-sm text-red-500">{errors.cpf.message}</p>}
      </div>

      <div className="md:col-span-2">
        <Label>Descrição:</Label>
          <textarea
            {...register("description")}
            className={`w-full rounded border p-3 text-sm ${errors.description ? "border-red-500" : "border-gray-300"}`}
            placeholder="Digite uma descrição sobre você"
            rows={4}
          />
          {errors.description && <p className="text-sm text-red-500">{errors.description.message}</p>}
        </div>
      </div>

      <div className="flex justify-end mt-4">
          <button
            type="submit"
            className="bg-[#1f2f54] hover:bg-[#152446] text-white rounded px-6 py-2 text-sm font-medium shadow-sm transition"
          >
          Salvar alterações
        </button>
      </div>
    </form>

  );
}
