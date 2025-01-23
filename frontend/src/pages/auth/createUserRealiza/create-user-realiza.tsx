import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { User } from "lucide-react";
import { useForm } from "react-hook-form";
import { z } from "zod";

const createUserRealizaSchema = z.object({
  name: z.string(),
  surname: z.string(),
  email: z.string().email("Insira um email valido"),
  cpf: z.string(),
  phone: z.string(),
  // profilePicture: z.string().optional(),
});

type CreateUserRealizaSchema = z.infer<typeof createUserRealizaSchema>;
export function CreateUserRealiza() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateUserRealizaSchema>({
    resolver: zodResolver(createUserRealizaSchema),
  });

  const createUser = async (data: CreateUserRealizaSchema) => {
    console.log("Função createUser chamada com os dados:", data);
    try {
      await axios.post(`${ip}/user/manager`, data);
  
      console.log("Usuário criado com sucesso");
    } catch (err) {
      console.error("Erro ao criar novo usuário", err);
    }
  };

  return (
    <div className="dark:bg-primary m-16 flex flex-col gap-8 rounded-md bg-white p-10 shadow-md">
      <div className="flex justify-start">
        <h1 className="text-[24px]">Crie um novo usuário para realiza</h1>
      </div>
      <div className="flex justify-center">
        <form action="" onSubmit={handleSubmit(createUser)}>
          <div className="flex h-[38vh] w-[65vw] items-start justify-between rounded-md border p-10 shadow-md">
            <div className="flex flex-col gap-3">
              <h2>Informações pessoais</h2>
              <div>
                <div className="flex items-center gap-6">
                  <div className="w-[20vw]">
                    <Label>Nome</Label>
                    <Input type="text" {...register("name")} />
                  </div>
                  <div className="w-[20vw]">
                    <Label>Sobrenome</Label>
                    <Input type="text" {...register("surname")} />
                  </div>
                </div>
                <div className="w-[41.2vw]">
                  <Label>Cpf</Label>
                  <Input type="text" {...register("cpf")} />
                </div>
                <div className="flex items-center gap-6">
                  <div className="w-[20vw]">
                    <Label>Email</Label>
                    <Input type="email" {...register("email")} />
                  </div>
                  <div className="w-[20vw]">
                    <Label>Celular</Label>
                    <Input type="text" {...register("phone")} />
                  </div>
                </div>
              </div>
            </div>
            {/* <div className="flex h-[30vh] w-[15vw] flex-col justify-center gap-2 rounded-md border p-14 shadow-md">
              <div className="flex items-center justify-center rounded-md bg-gray-300 px-5 py-2">
                <User size={150} />
              </div>
              <label
                htmlFor="profilePicture"
                className="bg-realizaBlue flex cursor-pointer items-center justify-center rounded-md px-4 py-2 text-white transition hover:bg-blue-700"
              >
                Selecione uma foto
                <input
                  id="profilePicture"
                  type="file"
                  className="hidden"
                  {...register("profilePicture")}
                />
              </label>
            </div> */}
            
          </div>
          <div className="flex justify-end">
              <Button className="bg-realizaBlue" type="submit">
                Criar Usuário
              </Button>
            </div>
        </form>
      </div>
    </div>
  );
}
