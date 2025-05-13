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

const createUserClientSchema = z.object({
  firstName: z.string().nonempty("Insira um nome"),
  surname: z.string().nonempty("Insira um sobrenome"),
  email: z.string().email("Insira um email válido"),
  cpf: z.string().nonempty("Insira um CPF"),
  telephone: z.string().nonempty("Insira um telefone"),
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

  const {
    register,
    handleSubmit,
    watch,
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
      await axios.post(`${ip}/user/manager/new-user`, data, {});
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
    }
  };

  const getAllClients = async () => {
    const token = localStorage.getItem("tokenClient");
    try {
      const res = await axios.get(`${ip}/client`, {
        headers: {
          Authorization: `Bearer ${token}`,
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
            <Input type="text" {...register("cpf")} className="dark:bg-white" />
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
            <div>
              <Label>Celular</Label>
              <Input
                type="text"
                {...register("telephone")}
                className="dark:bg-white"
              />
              {errors.telephone && (
                <p className="text-red-500">{errors.telephone.message}</p>
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
