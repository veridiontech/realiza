import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Skeleton } from "@/components/ui/skeleton";
import { useUser } from "@/context/user-provider";
import { propsBranch } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";

const createClientManageFormSchema = z.object({
  name: z.string().nonempty("Nome é obrigatório"),
  surname: z.string().nonempty("Sobrenome é obrigatório"),
  cellPhone: z.string().nonempty("Celular é obrigatório"),
  cpf: z.string().nonempty("Cpf é obrigatório"),
  email: z
    .string()
    .email("Formato de email inválido")
    .nonempty("Email é obrigatório"),
  position: z.string().nonempty("Seu cargo é obrigatório"),
  password: z.string().min(6, "A senha deve ter pelo menos 6 caracteres"),
  role: z.string().default("ROLE_CLIENT_MANAGER"),
});

type CreateClientManagerFormSchema = z.infer<
  typeof createClientManageFormSchema
>;
export function CreateNewManagerClient() {
  const [uniqueBranch, setUniqueBranch] = useState<propsBranch | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const { user } = useUser();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateClientManagerFormSchema>({
    resolver: zodResolver(createClientManageFormSchema),
  });

  const createClientManager = async (data: CreateClientManagerFormSchema) => {
    setIsLoading(true);
    const payload = {
      ...data,
      branch: user?.branch,
    };
    console.log(payload);

    let responseClient: any;

    let branchName: string;

    let branchResponse: any;

    try {
      responseClient = await axios.post(`${ip}/user/client`, payload);
    } catch (err) {
      console.log("erro ao criar gerente:", err);
    } finally {
      setIsLoading(false);
    }
    try {
      branchResponse = await axios.get(`${ip}/branch/${user?.branch}`);
    } catch (err) {
      console.log("erro ao buscar nome da branch:", err);
    } finally {
      setIsLoading(false);
    }

    branchName = branchResponse.data.name;

    const newSolicitation = {
      title: "Novo usuário Cliente Gerente",
      details: `Solicitação para criar um novo gerente em ${branchName}`,
      idRequester: user?.idUser,
      idNewUser: responseClient.data.idUser,
    };

    try {
      await axios.post(`${ip}/item-management/new`, newSolicitation);
    } catch (err) {
      console.log("erro ao criar solicitação:", err);
    }
  };

  const getUniqueBranch = async () => {
    setIsLoading(true);
    try {
      const res = await axios.get(`${ip}/branch/${user?.branch}`);
      setUniqueBranch(res.data);
      console.log(res.data);
    } catch (err) {
      console.log("erro ao buscar filial:", err);
    }
    setIsLoading(false);
  };

  useEffect(() => {
    getUniqueBranch();
  }, []);

  return (
    <div className="flex min-h-screen justify-center bg-gray-200 p-16">
      <div className="bg-primary-foreground flex flex-col gap-8 rounded-md p-10">
        <div className="flex items-center gap-1 text-[20px]">
          <h1>Crie um novo gerente para</h1>
          {isLoading ? (
            <strong>{uniqueBranch?.name}</strong>
          ) : (
            <Skeleton className="h-[10px] w-[150px] rounded-full bg-gray-300" />
          )}{" "}
        </div>
        <div className="rounded-md bg-gray-200 p-10 shadow-md">
          <form
            action=""
            onSubmit={handleSubmit(createClientManager)}
            className="flex flex-col gap-5"
          >
            <div className="flex gap-7">
              <div>
                <Label>Nome</Label>
                <Input type="text" {...register("name")} className="w-[15vw]" />
                {errors.name && <span>{errors.name.message}</span>}
              </div>
              <div>
                <Label>Sobrenome</Label>
                <Input
                  type="text"
                  {...register("surname")}
                  className="w-[15vw]"
                />
                {errors.surname && <span>{errors.surname.message}</span>}
              </div>
            </div>
            <div>
              <Label>Email</Label>
              <Input type="text" {...register("email")} className="w-full" />
              {errors.email && <span>{errors.email.message}</span>}
            </div>
            <div>
              <Label>Telefone</Label>
              <Input
                type="text"
                {...register("cellPhone")}
                className="w-full"
              />
              {errors.cellPhone && <span>{errors.cellPhone.message}</span>}
            </div>
            <div>
              <Label>CPF</Label>
              <Input type="text" {...register("cpf")} className="w-full" />
              {errors.cpf && <span>{errors.cpf.message}</span>}
            </div>
            <div className="flex gap-7">
              <div>
                <Label>Senha</Label>
                <Input
                  type="password"
                  {...register("password")}
                  className="w-[15vw]"
                />
                {errors.password && <span>{errors.password.message}</span>}
              </div>
              <div>
                <Label>Cargo</Label>
                <Input
                  type="text"
                  {...register("position")}
                  className="w-[15vw]"
                />
              </div>
            </div>
            <Button type="submit" className="bg-realizaBlue">
              Solicitar criação do gerente
            </Button>
          </form>
        </div>
      </div>
    </div>
  );
}
