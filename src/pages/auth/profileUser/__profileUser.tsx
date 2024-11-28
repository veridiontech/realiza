import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useState } from "react";
import { Avatar, AvatarImage } from "@/components/ui/avatar";
import { PersonStanding, LockKeyhole } from 'lucide-react';

const profileSchema = z.object({
  fullName: z.string().min(1, "O nome completo é obrigatório"),
  email: z.string().email("Insira um e-mail válido"),
  phone: z
    .string()
    .regex(/^\+\d{2}\s\d{2}\s\d{4,5}-\d{4}$/, "Telefone inválido"),
  cpf: z.string().regex(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/, "CPF inválido"),
  description: z.string().min(1, "A descrição é obrigatória"),
  password: z.string().min(6, "A senha deve ter pelo menos 6 caracteres"),
});

type ProfileFormData = z.infer<typeof profileSchema>;

export function ProfileUser() {
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
  });

  const onSubmit = (data: ProfileFormData) => {
    console.log("Dados do formulário:", data);
    alert("Dados salvos com sucesso!");
  };

  return (
    <div className="flex flex-col w-full h-full p-6 bg-white">
      <h1 className="text-2xl font-semibold mb-6 text-blue-600">Meu Perfil</h1>
      <div className="flex flex-col md:flex-row items-center justify-between bg-white shadow rounded-lg p-6 mb-6">
        <div className="flex items-center">
          <Avatar className="w-24 h-24">
            <AvatarImage src="https://github.com/shadcn.png" />
          </Avatar>
          <div className="ml-6">
            <h2 className="text-lg font-bold">Jean de Castro</h2>
            <span>email@email.com</span>
            <p className="text-blue-600">Usuário Comum</p>
          </div>
        </div>
        <button className="mt-4 md:mt-0 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
          Editar
        </button>
      </div>
      <form
        onSubmit={handleSubmit(onSubmit)}
        className="flex flex-col gap-6 bg-white shadow rounded-lg p-6"
      >
        <div className="flex flex-row gap-2">
          <PersonStanding className="w-7 h-7 text-blue-600"/>
          <h2 className="mb-4 text-lg text-blue-600" >Informações Pessoais</h2>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Nome Completo
          </label>
          <input
            type="text"
            {...register("fullName")}
            className={`w-full p-3 border rounded ${
              errors.fullName ? "border-red-500" : "border-gray-300"
            }`}
            placeholder="Digite seu nome completo"
          />
          {errors.fullName && (
            <p className="text-sm text-red-500">{errors.fullName.message}</p>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            E-mail
          </label>
          <input
            type="email"
            {...register("email")}
            className={`w-full p-3 border rounded ${
              errors.email ? "border-red-500" : "border-gray-300"
            }`}
            placeholder="Digite seu e-mail"
          />
          {errors.email && (
            <p className="text-sm text-red-500">{errors.email.message}</p>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Telefone
          </label>
          <input
            type="text"
            {...register("phone")}
            className={`w-full p-3 border rounded ${
              errors.phone ? "border-red-500" : "border-gray-300"
            }`}
            placeholder="+55 11 91234-5678"
          />
          {errors.phone && (
            <p className="text-sm text-red-500">{errors.phone.message}</p>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">CPF</label>
          <input
            type="text"
            {...register("cpf")}
            className={`w-full p-3 border rounded ${
              errors.cpf ? "border-red-500" : "border-gray-300"
            }`}
            placeholder="123.456.789-00"
          />
          {errors.cpf && (
            <p className="text-sm text-red-500">{errors.cpf.message}</p>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Descrição
          </label>
          <textarea
            {...register("description")}
            className={`w-full p-3 border rounded ${
              errors.description ? "border-red-500" : "border-gray-300"
            }`}
            placeholder="Digite uma descrição sobre você"
            rows={4}
          ></textarea>
          {errors.description && (
            <p className="text-sm text-red-500">
              {errors.description.message}
            </p>
          )}
        </div>

        <div className="flex justify-end">
          <button
            type="submit"
            className="w-[20vw] p-3 text-white bg-blue-500 rounded hover:bg-blue-600"
          >
            Salvar
          </button>
        </div>
      </form>
      <div className="flex flex-col bg-white shadow rounded-lg p-6 mt-6">
        <div className="flex flex-row gap-2">
          <LockKeyhole className="w-7 h-7 text-blue-600" />
          <h2 className="mb-8 text-lg text-blue-600">Segurança</h2>
        </div>
        <form
          onSubmit={handleSubmit(onSubmit)}
          className="flex flex-col gap-4 w-full"
        >
          <div className="relative mb-4">
            <label
              htmlFor="password"
              className="block text-sm font-medium text-gray-700"
            >
              Nova Senha
            </label>
            <input
              type={showPassword ? "text" : "password"}
              id="password"
              {...register("password")}
              className={`block w-full rounded-md border border-gray-300 px-3 py-2 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                errors.password ? "border-red-500 focus:ring-red-500" : ""
              }`}
              placeholder="Digite sua nova senha"
            />
            <button
              type="button"
              className="absolute mt-4 inset-y-0 right-3 flex items-center text-gray-500 hover:text-gray-700"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? "🙈" : "👁️"}
            </button>
          </div>
          {errors.password && (
            <p className="text-sm text-red-500 mt-1">{errors.password.message}</p>
          )}
          <div className="flex justify-end">
            <button
              type="submit"
              className="w-[20vw] p-3 text-white bg-blue-500 rounded hover:bg-blue-600"
            >
              Alterar Senha
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
