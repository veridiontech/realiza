import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useBranch } from "@/context/Branch-provider";
import { useUser } from "@/context/user-provider";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { Oval } from "react-loader-spinner";
import { toast } from "sonner";
import { z } from "zod";

const createClientManageFormSchema = z.object({
  firstName: z.string().nonempty("Nome é obrigatório"),
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
  const { selectedBranch } = useBranch();
  const [isLoading, setIsLoading] = useState(false);
  const { user } = useUser();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateClientManagerFormSchema>({
    resolver: zodResolver(createClientManageFormSchema),
  });

  console.log(selectedBranch);

  const createClientManager = async (data: CreateClientManagerFormSchema) => {
    setIsLoading(true);
    const payload = {
      ...data,
      branch: user?.branch,
      idUser: user?.idUser,
    };
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.post(`${ip}/user/client`, payload, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      }
      );
      toast.success("Sucesso ao solicitar novo usuário")
    } catch (err) {
      console.log("erro ao criar gerente:", err);
      toast.error("Erro ao criar nova solicitação")
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen justify-center bg-gray-200 p-16">
      <div className="bg-primary-foreground flex flex-col gap-8 rounded-md p-10">
        <div className="flex items-center gap-1 text-[20px]">
          <h1>Crie um novo gerente para</h1>
          {selectedBranch ? (
            <strong>{selectedBranch?.name}</strong>
          ) : (
            <span className="text-red-600">Selecione uma filial</span>
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
                <Input type="text" {...register("firstName")} className="w-[15vw]" />
                {errors.firstName && <span>{errors.firstName.message}</span>}
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
            {isLoading ? (
              <Button type="submit" className="bg-realizaBlue">
                <Oval
                  visible={true}
                  height="80"
                  width="80"
                  color="#4fa94d"
                  ariaLabel="oval-loading"
                  wrapperStyle={{}}
                  wrapperClass=""
                />
              </Button>
            ) : (
              <Button type="submit" className="bg-realizaBlue">
                Solicitar criação do gerente
              </Button>
            )}
          </form>
        </div>
      </div>
    </div>
  );
}
