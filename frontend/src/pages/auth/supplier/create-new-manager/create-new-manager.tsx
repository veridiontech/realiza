import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Skeleton } from "@/components/ui/skeleton";
import { useUser } from "@/context/user-provider";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Oval } from "react-loader-spinner";
import { toast } from "sonner";
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
  role: z.string().default("ROLE_SUPPLIER_MANAGER"),
});

type CreateClientManagerFormSchema = z.infer<
  typeof createClientManageFormSchema
>;
export function CreateNewManagerSupplier() {
  const [uniqueSupplier, setUniqueSupplier] = useState<any>(
    null,
  );
  const [isLoading, setIsLoading] = useState(false);
  const { user } = useUser();

  console.log("objeto do usuario:", user);

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
      supplier: user?.supplier,
    };
    console.log("DADOS SENDO ENVIADOS: ", payload);

    try {
      await axios.post(`${ip}/user/supplier`, payload);
      toast.success("Sua solicitação foi enviada para realiza");
    } catch (err) {
      console.log("erro ao criar gerente:", err);
      toast.error("Erro ao solicitar criação de usuário, tente novamente");
    } finally {
      setIsLoading(false);
    }
  };

  const getUniqueBranch = async () => {
    setIsLoading(true);
    try {
      const res = await axios.get(`${ip}/supplier/${user?.supplier}`);
      setUniqueSupplier(res.data);
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
            <strong>{uniqueSupplier?.tradeName}</strong>
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
            {isLoading ? (
              <Button type="submit" className="bg-realizaBlue">
                Solicitar criação do gerente
              </Button>
            ) : (
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
            )}
          </form>
        </div>
      </div>
    </div>
  );
}
