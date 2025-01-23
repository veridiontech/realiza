import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { PersonStanding } from 'lucide-react';
import { useUser } from "@/context/user-provider";
import { useEffect } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";

const profileSchema = z.object({
    firstName: z.string(),
    surname: z.string(),
    email: z.string(),
    telephone: z
      .string(),
    cpf: z.string().regex(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/, "CPF inválido"),
    description: z.string(),
  });
  
  type ProfileFormData = z.infer<typeof profileSchema>;

export function MiddleSection (){
    const { user } = useUser()
    
    const {
        register,
        handleSubmit,
        formState: { errors },
        setValue,
      } = useForm<ProfileFormData>({
        resolver: zodResolver(profileSchema),
      });
    
      const onSubmit = async(data: ProfileFormData) => {
        const payload = {
          ...data,
          password: "40028922"
        }
        try{
          console.log(payload);
          await axios.put(`${ip}/user/manager/${user?.idUser}`, payload)
          console.log("sucesso");
          
        }catch(err){
          console.log("erro ao atualizar usuário",err);
        }
      };

      useEffect(() => {
        setValue("firstName", user?.firstName || "")
        setValue("surname", user?.surname ||"")
        setValue("cpf", user?.cpf ||"")
        setValue("email", user?.email ||"")
        setValue("telephone", user?.telephone ||"")
        setValue("description", user?.description ||"") 
      }, [])

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
            Nome
          </label>
          <input
            type="text"
            {...register("firstName")}
            className={`w-full p-3 border rounded ${
              errors.firstName ? "border-red-500" : "border-gray-300"
            }`}
            placeholder="Digite seu nome completo"
          />
          {errors.firstName && (
            <p className="text-sm text-red-500">{errors.firstName.message}</p>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            Sobrenome
          </label>
          <input
            type="text"
            {...register("surname")}
            className={`w-full p-3 border rounded ${
              errors.surname ? "border-red-500" : "border-gray-300"
            }`}
            placeholder="Digite seu nome completo"
          />
          {errors.surname && (
            <p className="text-sm text-red-500">{errors.surname.message}</p>
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
            {...register("telephone")}
            className={`w-full p-3 border rounded ${
              errors.telephone ? "border-red-500" : "border-gray-300"
            }`}
            placeholder="+55 11 91234-5678"
          />
          {errors.telephone && (
            <p className="text-sm text-red-500">{errors.telephone.message}</p>
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