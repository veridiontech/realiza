import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { User } from "lucide-react";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";

const createUserRealizaSchema = z.object({
  firstName: z.string(),
  surname: z.string(),
  email: z.string().email("Insira um email valido"),
  cpf: z.string(),
  telephone: z.string(),
  password: z.string().nonempty("Insira uma senha"),
  // profilePicture: z.string().optional(),
});

type CreateUserRealizaSchema = z.infer<typeof createUserRealizaSchema>;
export function CreateUserRealiza() {
  const [userPreview, setUserPreview] = useState({
    firstName: "",
    surname: "",
    email: "",
  });

  const { register, handleSubmit } = useForm<CreateUserRealizaSchema>({
    resolver: zodResolver(createUserRealizaSchema),
  });

  const createUser = async (data: CreateUserRealizaSchema) => {
    const payload = {
      ...data,
      role: "ROLE_MANAGER",
    };
    console.log("Função createUser chamada com os dados:", payload);
    try {
      await axios.post(`${ip}/user/manager`, payload);
      console.log("Usuário criado com sucesso");
    } catch (err) {
      console.error("Erro ao criar novo usuário", err);
    }
  };

  const handlePreview = (field: keyof typeof userPreview, value: string) => {
    setUserPreview((prev) => ({ ...prev, [field]: value }));
  };

  return (
    <div className="dark:bg-primary m-20 flex flex-col gap-8 rounded-md bg-white p-6 shadow-md lg:p-10">
      <h1 className="text-xl font-bold lg:text-2xl">
        Crie um novo usuário para Realiza
      </h1>
      <form onSubmit={handleSubmit(createUser)} className="flex flex-col gap-6">
        <div className="dark:bg-primary flex items-center justify-between gap-6 rounded-md border p-6 shadow-md">
          <div className="flex w-full flex-col gap-4 lg:w-[65%]">
            <h2 className="text-lg font-semibold">Informações Pessoais</h2>
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div>
                <Label>Nome</Label>
                <Input
                  type="text"
                  {...register("firstName")}
                  onChange={(e) => handlePreview("firstName", e.target.value)}
                  className="dark:bg-white"
                />
              </div>
              <div>
                <Label>Sobrenome</Label>
                <Input
                  type="text"
                  {...register("surname")}
                  onChange={(e) => handlePreview("surname", e.target.value)}
                  className="dark:bg-white"
                />
              </div>
            </div>

            <div>
              <Label>CPF</Label>
              <Input
                type="text"
                {...register("cpf")}
                className="dark:bg-white"
              />
            </div>

            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div>
                <Label>Email</Label>
                <Input
                  type="email"
                  {...register("email")}
                  onChange={(e) => handlePreview("email", e.target.value)}
                  className="dark:bg-white"
                />
              </div>
              <div>
                <Label>Celular</Label>
                <Input
                  type="text"
                  {...register("telephone")}
                  className="dark:bg-white"
                />
              </div>
            </div>
          </div>

          <div className="flex w-full flex-col gap-4 lg:w-[30%]">
            <div className="flex h-[25vh] w-[20vw] items-center justify-center rounded-md bg-gray-300">
              <User size={94} />
            </div>
            <div className="flex flex-col items-start">
              <div className="flex items-center gap-1">
                <p className="font-semibold">Nome:</p>
                <p>
                  {userPreview.firstName} {userPreview.surname}
                </p>
              </div>
              <div className="flex items-center gap-1">
                <p className="font-semibold">Email:</p>
                <p>{userPreview.email}</p>
              </div>
            </div>
          </div>
        </div>

        <div className="flex justify-end">
          <Button type="submit" className="bg-realizaBlue">
            Criar Usuário
          </Button>
        </div>
      </form>
    </div>
  );
}
