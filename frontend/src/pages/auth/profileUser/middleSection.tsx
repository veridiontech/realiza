import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { PersonStanding } from 'lucide-react';

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

export function MiddleSection (){

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
    )
}