import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { User } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

const createUserRealizaSchema = z.object({
  firstName: z.string().nonempty("Insira um nome"),
  surname: z.string().nonempty("Insira um sobrenome"),
  email: z.string().email("Insira um email válido"),
  cpf: z.string().nonempty("Insira um CPF"),
  telephone: z.string().nonempty("Insira um telefone"),
  password: z.string().nonempty("Insira uma senha"),
});

type CreateUserRealizaSchema = z.infer<typeof createUserRealizaSchema>;

export function CreateUserRealiza() {
  const [showPassword, setShowPassword] = useState(false);
  const [userPreview, setUserPreview] = useState({
    firstName: "",
    surname: "",
    email: "",
  });

  const {
    register,
    handleSubmit,
    watch, 
    formState: { errors },
  } = useForm<CreateUserRealizaSchema>({
    resolver: zodResolver(createUserRealizaSchema),
  });

  const firstName = watch("firstName");
  const surname = watch("surname");
  const email = watch("email");

  useEffect(() => {
    setUserPreview({ firstName, surname, email });
  }, [firstName, surname, email]);

  const createUser = async (data: CreateUserRealizaSchema) => {
    const payload = {
      ...data,
      role: "ROLE_REALIZA_BASIC",
    };
    console.log("Função createUser chamada com os dados:", payload);
    try {
      await axios.post(`${ip}/user/manager`, payload);
      console.log("Usuário criado com sucesso");
      toast.success("Sucesso ao criar novo usuário Realiza");
    } catch (err) {
      console.error("Erro ao criar novo usuário", err);
      toast.error("Erro ao criar um novo usuário, tente novamente");
    }
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
                  className="dark:bg-white"
                />
                {errors.firstName && <p className="text-red-500">{errors.firstName.message}</p>}
              </div>
              <div>
                <Label>Sobrenome</Label>
                <Input
                  type="text"
                  {...register("surname")}
                  className="dark:bg-white"
                />
                {errors.surname && <p className="text-red-500">{errors.surname.message}</p>}
              </div>
            </div>

            <div>
              <Label>CPF</Label>
              <Input
                type="text"
                {...register("cpf")}
                className="dark:bg-white"
              />
              {errors.cpf && <p className="text-red-500">{errors.cpf.message}</p>}
            </div>

            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div>
                <Label>Email</Label>
                <Input
                  type="email"
                  {...register("email")}
                  className="dark:bg-white"
                />
                {errors.email && <p className="text-red-500">{errors.email.message}</p>}
              </div>
              <div>
                <Label>Celular</Label>
                <Input
                  type="text"
                  {...register("telephone")}
                  className="dark:bg-white"
                />
                {errors.telephone && <p className="text-red-500">{errors.telephone.message}</p>}
              </div>
            </div>
            <div>
              <div>
                <Label>Senha</Label>
                <div className="relative">
                  <Input
                    type={showPassword ? "text" : "password"}
                    {...register("password")}
                    className="pr-10 dark:bg-white"
                  />
                  <button
                    type="button"
                    className="absolute inset-y-0 right-2 flex items-center text-gray-500 hover:text-gray-700"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? "🙈" : "👁️"}
                  </button>
                </div>
                {errors.password && <p className="text-red-500">{errors.password.message}</p>}
              </div>
            </div>
            <div>
              <Label>Senha: </Label>
              <Input type="password" 
              {...register("password")}
               className="dark:bg-white"
              />
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
